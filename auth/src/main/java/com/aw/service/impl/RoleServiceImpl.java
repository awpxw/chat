package com.aw.service.impl;

import cn.hutool.json.JSONUtil;
import com.aw.dto.RoleDTO;
import com.aw.dto.groups.RoleAddGroup;
import com.aw.dto.groups.RoleDeleteGroup;
import com.aw.dto.groups.RoleUpdateGroup;
import com.aw.entity.Role;
import com.aw.entity.RoleMenu;
import com.aw.exception.BizException;
import com.aw.login.UserContext;
import com.aw.mapper.RoleMapper;
import com.aw.mapper.RoleMenuMapper;
import com.aw.service.RoleService;
import com.aw.snowflake.IdWorker;
import com.aw.validate.ValidatorUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IdWorker idWorker;

    @Override
    @Transactional
    public void roleUpdate(RoleDTO roleDTO) {

        saveOrUpdate(roleDTO);

    }

    private void saveOrUpdate(RoleDTO roleDTO) {
        if (roleDTO.getId() != null) {
            updateRole(roleDTO);
        } else {
            insertRole(roleDTO);
        }
    }

    private void updateRole(RoleDTO roleDTO) {
        ValidatorUtil.validate(roleDTO, RoleUpdateGroup.class);
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        boolean success = ChainWrappers.lambdaUpdateChain(Role.class)
                .eq(Role::getId, role.getId())
                .update(role);
        if (!success) {
            log.error("修改角色失败，id：{}", roleDTO.getId());
            throw new BizException("修改角色失败");
        }
    }

    private void insertRole(RoleDTO roleDTO) {
        ValidatorUtil.validate(roleDTO, RoleAddGroup.class);
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        int success = roleMapper.insert(role);
        if (success <= 0) {
            log.error("新增角色失败，id：{}", roleDTO.getId());
            throw new BizException("新增角色失败");
        }
    }

    @Override
    @Transactional
    public void roleDelete(RoleDTO roleDTO) {
        ValidatorUtil.validate(roleDTO, RoleDeleteGroup.class);
        Long id = roleDTO.getId();
        ChainWrappers.lambdaUpdateChain(Role.class)
                .eq(Role::getId, id)
                .eq(Role::getStatus, 1)
                .set(Role::getDeleted, true)
                .update();
    }

    @Override
    public IPage<Role> page(RoleDTO roleDTO) {
        return ChainWrappers.lambdaQueryChain(Role.class)
                .eq(Role::getStatus, 1)
                .page(Page.of(roleDTO.getPageNum(), roleDTO.getPageSize()));
    }

    @Override
    @Transactional
    public void allot(RoleDTO roleDTO) {

        List<RoleMenu> roleMenus = dto2Entity(roleDTO);

        insertBatch(roleDTO, roleMenus);

    }


    public void insertBatch(RoleDTO roleDTO, List<RoleMenu> roleMenus) {
        Long id = roleDTO.getId();
        Long userId = UserContext.get().getUserId();
        Integer success = roleMenuMapper.insertBatch(roleMenus, idWorker.nextId(), userId);
        if (success <= 0) {
            log.error("新增权限失败，角色id：{}", id);
            throw new BizException("新增权限失败");
        }
    }

    private List<RoleMenu> dto2Entity(RoleDTO roleDTO) {
        Long id = roleDTO.getId();
        List<Long> menuIds = roleDTO.getMenuIds();
        ArrayList<RoleMenu> roleMenus = new ArrayList<>();
        for (Long menuId : menuIds) {
            RoleMenu roleMenu = RoleMenu
                    .builder()
                    .roleId(id)
                    .menuId(menuId)
                    .build();
            roleMenus.add(roleMenu);
        }
        return roleMenus;
    }

    @Override
    @Transactional
    public void cancel(RoleDTO roleDTO) {

        removeByIds(roleDTO);

    }


    public void removeByIds(RoleDTO roleDTO) {
        Long id = roleDTO.getId();
        List<Long> menuIds = roleDTO.getMenuIds();
        boolean success = ChainWrappers.lambdaUpdateChain(RoleMenu.class)
                .eq(RoleMenu::getRoleId, id)
                .in(RoleMenu::getMenuId, menuIds)
                .set(RoleMenu::getDeleted, 1)
                .update();
        if (!success) {
            log.error("删除权限失败，角色id：{}，菜单ids：{}", id, JSONUtil.toJsonStr(menuIds));
            throw new BizException("删除权限失败");
        }
    }

}
