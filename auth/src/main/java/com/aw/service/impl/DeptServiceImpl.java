package com.aw.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.aw.dto.DeptDTO;
import com.aw.entity.Dept;
import com.aw.exception.BizException;
import com.aw.mapper.DeptMapper;
import com.aw.redis.RedisUtils;
import com.aw.service.DeptService;
import com.aw.vo.DeptVO;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class DeptServiceImpl implements DeptService {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private DeptMapper deptMapper;

    @Override
    public DeptVO loadDeptTreeFromDB() {
        List<Dept> allDept = selectAllActiveDept();
        boolean isEmptyTable = checkIfEmptyTable(allDept);
        if (isEmptyTable) {
            return defaultRoot();
        }
        List<Dept> activeDept = ignoreOrphanNode(allDept);
        List<Dept> effectDept = checkIfCircularDependency(activeDept);
        return assembledDeptTree(effectDept);
    }

    @Override
    public DeptVO tree() {

        String key = redisUtils.key("dept_tree");

        return redisUtils.get(key, () -> {
            DeptVO deptVO = loadDeptTreeFromDB();
            return BeanUtil.toBean(deptVO, DeptVO.class);
        }, DeptVO.class);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DeptDTO deptDTO) {

        checkIfCircularDependencyAfterUpdate(deptDTO);

        saveOrUpdateDept(deptDTO);

        deleteDeptCache();

    }

    private void deleteDeptCache() {
        redisUtils.delete("dept_tree");
        redisUtils.delayDoubleDelete("dept_tree");
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateDept(DeptDTO deptDTO) {
        if (Objects.nonNull(deptDTO.getId())) {
            updateDeptById(deptDTO);
        } else {
            insertDept(deptDTO);
        }
    }

    private void insertDept(DeptDTO deptDTO) {
        Dept dept = new Dept();
        BeanUtils.copyProperties(deptDTO, dept);
        int result = deptMapper.insert(dept);
        if (result <= 0) {
            log.error(">>>保存部门信息失败,部门id:{}", deptDTO.getId());
            throw new BizException("保存/更新部门失败");
        }
    }

    private void updateDeptById(DeptDTO deptDTO) {
        boolean success = ChainWrappers.lambdaUpdateChain(Dept.class)
                .eq(Dept::getId, deptDTO.getId())
                .set(Dept::getParentId, deptDTO.getParentId())
                .set(Dept::getName, deptDTO.getName())
                .set(Dept::getSort, deptDTO.getSort())
                .set(Dept::getStatus, deptDTO.getStatus())
                .update();
        if (!success) {
            log.error(">>>更新部门信息失败,部门id:{}", deptDTO.getId());
            throw new BizException("保存/更新部门失败");
        }
    }

    private List<Dept> deptTreeToList(DeptVO deptVO) {
        return Stream.of(deptVO).flatMap(node -> Stream.iterate(node, Objects::nonNull, n -> n.getChildren() != null && !n.getChildren().isEmpty() ? n.getChildren().get(0) : null).takeWhile(Objects::nonNull)).map(vo -> BeanUtil.toBean(vo, Dept.class)).collect(Collectors.toList());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DeptDTO deptDTO) {

        removeWithChildren(deptDTO);

        deleteDeptCache();

    }

    @Transactional(rollbackFor = Exception.class)
    public void removeWithChildren(DeptDTO deptDTO) {
        Long deptId = deptDTO.getId();
        int isSuccess = deptMapper.logicDeleteWithChildren(deptId);
        if (isSuccess <= 0) {
            throw new BizException("删除部门失败,部门id为" + deptId);
        }
    }

    private DeptVO defaultRoot() {
        DeptVO root = new DeptVO();
        root.setId(0L);
        root.setParentId(null);
        root.setName("集团总部");
        root.setSort(0);
        root.setChildren(new ArrayList<>());
        return root;
    }

    private List<Dept> selectAllActiveDept() {
        return ChainWrappers.lambdaQueryChain(Dept.class).select(Dept::getId, Dept::getParentId, Dept::getName, Dept::getSort, Dept::getStatus).eq(Dept::getStatus, 1).orderByAsc(Dept::getSort).list();
    }

    private boolean checkIfEmptyTable(List<Dept> deptList) {
        return CollectionUtils.isEmpty(deptList);
    }


    private List<Dept> ignoreOrphanNode(List<Dept> allDept) {
        return allDept.stream().filter(dept -> Objects.nonNull(dept.getParentId())).collect(Collectors.toList());
    }

    private void checkIfCircularDependencyAfterUpdate(DeptDTO deptDTO) {
        DeptVO deptVO = loadDeptTreeFromDB();
        List<Dept> deptList = deptTreeToList(deptVO);
        Dept dept = new Dept();
        BeanUtils.copyProperties(deptDTO, dept);
        deptList.add(dept);
        checkIfCircularDependency(deptList);
    }

    private List<Dept> checkIfCircularDependency(List<Dept> activeDept) {
        Map<Long, Long> parentMap = activeDept.stream().collect(Collectors.toMap(Dept::getId, Dept::getParentId, (a, b) -> a));
        Set<Long> visiting = new HashSet<>();
        Set<Long> visited = new HashSet<>();
        for (Dept dept : activeDept) {
            if (hasCycle(dept.getId(), parentMap, visiting, visited)) {
                throw new BizException("部门树存在循环依赖，请检查 parent_id 设置");
            }
        }
        return activeDept;
    }

    /**
     * DFS 递归检测是否有环
     */
    private boolean hasCycle(Long currentId, Map<Long, Long> parentMap, Set<Long> visiting, Set<Long> visited) {
        if (visiting.contains(currentId)) {
            return true;
        }
        if (visited.contains(currentId)) {
            return false;
        }
        visiting.add(currentId);
        Long parentId = parentMap.get(currentId);
        if (parentId != null && parentId != 0) {
            if (hasCycle(parentId, parentMap, visiting, visited)) {
                return true;
            }
        }
        visiting.remove(currentId);
        visited.add(currentId);
        return false;
    }

    private DeptVO assembledDeptTree(List<Dept> activeDept) {
        Map<Long, DeptVO> map = new HashMap<>();
        List<DeptVO> rootList = new ArrayList<>();

        // 第一遍：所有部门转 VO 并放 map
        for (Dept dept : activeDept) {
            DeptVO vo = new DeptVO();
            BeanUtils.copyProperties(dept, vo);
            vo.setChildren(new ArrayList<>());  // 初始化 children
            map.put(vo.getId(), vo);
        }

        // 第二遍：挂 children + 找根
        for (Dept dept : activeDept) {
            DeptVO vo = map.get(dept.getId());
            if (dept.getParentId() == null || dept.getParentId() == 0) {
                rootList.add(vo);
            } else {
                DeptVO parent = map.get(dept.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                }
            }
        }

        // 可选：排序
        rootList.sort(Comparator.comparing(DeptVO::getSort, Comparator.nullsLast(Integer::compareTo)));
        rootList.forEach(vo -> vo.getChildren().sort(Comparator.comparing(DeptVO::getSort, Comparator.nullsLast(Integer::compareTo))));

        return rootList.isEmpty() ? null : rootList.get(0);
    }

}
