package com.aw.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.aw.dto.CaptchaDTO;
import com.aw.dto.LoginDTO;
import com.aw.entity.BannedUser;
import com.aw.entity.Dept;
import com.aw.entity.User;
import com.aw.exception.BizException;
import com.aw.jwt.JwtUtil;
import com.aw.login.LoginUserInfo;
import com.aw.login.UserContext;
import com.aw.mapper.BannedUserMapper;
import com.aw.mapper.UserMapper;
import com.aw.redis.RedisUtils;
import com.aw.service.AuthService;
import com.aw.utils.CaptchaUtils;
import com.aw.vo.CaptchaVO;
import com.aw.vo.DeptVO;
import com.aw.vo.LoginVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private CaptchaUtils captchaUtils;

    @Resource
    private BannedUserMapper bannedUserMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public LoginVO login(LoginDTO loginDTO) {

        checkCaptchaNull(loginDTO);

        checkCaptchaExpireOrWrong(loginDTO);

        checkNameAndPassword(loginDTO);

        checkAccountBanned(loginDTO);

        String accessToken = generateAccessToken(loginDTO);

        String refreshToken = generateRefreshToken(loginDTO);

        LoginVO loginVO = new LoginVO();

        loginVO.setAccessToken(accessToken);

        loginVO.setRefreshToken(refreshToken);

        return loginVO;
    }

    @Override
    public void register(LoginDTO loginDTO) {

        checkNameRepeat(loginDTO);

        checkPhoneRepeat(loginDTO);

        checkCaptchaExpireOrWrong(loginDTO);

        addNewUser(loginDTO);

    }

    @Override
    public LoginVO refresh(LoginDTO loginDTO) {

        checkInvalid(loginDTO);

        Claims claims = jwtUtil.parseToken(loginDTO.getRefreshToken());

        User user = findUser(claims.getSubject());

        String accessToken = generateAccessTokenByUserInfo(user);

        LoginVO loginVO = new LoginVO();

        loginVO.setAccessToken(accessToken);

        return loginVO;

    }

    @Override
    public LoginVO logout(LoginDTO loginDTO) {
        return null;
    }

    @Override
    public CaptchaVO captcha(CaptchaDTO captchaDTO) {

        Integer expireIns = captchaDTO.getExpireIns();

        Map<String, String> map = generateCaptcha(expireIns);

        CaptchaVO captchaVO = new CaptchaVO();

        captchaVO.setCaptchaId(map.get("captchaId"));

        captchaVO.setImage(map.get("image"));

        return captchaVO;

    }

    @Override
    public void captchaVerify(CaptchaDTO captchaDTO) {
        String uuid = captchaDTO.getUuid();
        String code = captchaDTO.getCode();
        if (!captchaUtils.validate(uuid, code)) {
            throw new BizException("验证码失效");
        }
    }

    @Override
    public void passwordChange(LoginDTO loginDTO) {
        LoginUserInfo loginUser = UserContext.get();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::getName, loginUser.getUsername()).eq(User::getId, loginUser.getUserId()).eq(User::getPassword, loginDTO.getOldPassword());
        User user = userMapper.selectOne(wrapper);
        user.setPassword(loginDTO.getPassword());
        userMapper.updateById(user);
    }

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

    public DeptVO deptTree() {

        String key = redisUtils.key("dept_tree");

        return redisUtils.get(key, () -> {
            DeptVO deptVO = loadDeptTreeFromDB();
            return BeanUtil.toBean(deptVO, DeptVO.class);
        }, DeptVO.class);

    }

    private List<Dept> checkIfCircularDependency(List<Dept> activeDept) {
        Map<Long, Long> parentMap = activeDept
                .stream()
                .collect(Collectors.toMap(Dept::getId, Dept::getParentId, (a, b) -> a));
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
    private boolean hasCycle(Long currentId,
                             Map<Long, Long> parentMap,
                             Set<Long> visiting,
                             Set<Long> visited) {
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
        for (Dept dept : activeDept) {
            DeptVO vo = new DeptVO();
            BeanUtils.copyProperties(dept, vo);
            map.put(vo.getId(), vo);
            if (dept.getParentId() == 0) {
                rootList.add(vo);
            } else {
                DeptVO parent = map.get(dept.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                }
            }
        }
        return rootList.isEmpty() ? null : rootList.get(0);
    }

    private List<Dept> ignoreOrphanNode(List<Dept> allDept) {
        return allDept
                .stream()
                .filter(dept -> Objects.nonNull(dept.getParentId()))
                .collect(Collectors.toList());
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
        return ChainWrappers.lambdaQueryChain(Dept.class)
                .select(Dept::getId, Dept::getParentId, Dept::getName, Dept::getSort, Dept::getStatus)
                .eq(Dept::getStatus, 1)
                .orderByAsc(Dept::getSort)
                .list();
    }

    private boolean checkIfEmptyTable(List<Dept> deptList) {
        return CollectionUtils.isEmpty(deptList);
    }

    private Map<String, String> generateCaptcha(int expireIns) {
        return captchaUtils.generateCaptcha(expireIns);
    }

    private String generateAccessTokenByUserInfo(User user) {
        return jwtUtil.generateAccessToken(user.getId(), user.getName(), null);
    }

    private User findUser(String id) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId, Long.parseLong(id)));
    }

    private void checkInvalid(LoginDTO loginDTO) {
        String accessToken = loginDTO.getRefreshToken();
        boolean isValid = jwtUtil.validateToken(accessToken);
        if (!isValid) {
            throw new BizException("验证码过期或错误");
        }
    }

    private void addNewUser(LoginDTO loginDTO) {
        User user = new User();
        BeanUtils.copyProperties(loginDTO, user);
        user.setName(loginDTO.getUsername());
        userMapper.insert(user);
    }

    private void checkPhoneRepeat(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String mobile = loginDTO.getMobile();
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>().eq(User::getName, username).eq(User::getMobile, mobile);
        List<User> users = userMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(users)) {
            throw new BizException("手机号重复");
        }
    }

    private void checkNameRepeat(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().eq(User::getName, username));
        if (CollectionUtils.isNotEmpty(users)) {
            throw new BizException("用户名重复");
        }
    }

    private String generateAccessToken(LoginDTO loginDTO) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>().eq(User::getName, loginDTO.getUsername());
        User user = userMapper.selectOne(wrapper);
        Long userId = user.getId();
        String userName = user.getName();
        return jwtUtil.generateAccessToken(userId, userName, null);
    }

    private String generateRefreshToken(LoginDTO loginDTO) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>().eq(User::getName, loginDTO.getUsername());
        User user = userMapper.selectOne(wrapper);
        Long userId = user.getId();
        return jwtUtil.generateRefreshToken(userId);
    }

    private void checkAccountBanned(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getName, username).eq(User::getPassword, password));
        Long id = user.getId();
        List<BannedUser> bannedUsers = bannedUserMapper.selectList(new LambdaUpdateWrapper<BannedUser>().eq(BannedUser::getUserId, id));
        if (Objects.nonNull(bannedUsers) && !bannedUsers.isEmpty()) {
            throw new BizException("账号已被禁用");
        }
    }

    private void checkCaptchaExpireOrWrong(LoginDTO loginDTO) {
        String captcha = loginDTO.getCaptcha();
        String captchaId = loginDTO.getCaptchaId();
        try {
            boolean validate = captchaUtils.validate(captcha, captchaId);
            if (!validate) {
                throw new BizException("验证码过期或错误");
            }
        } catch (Exception e) {
            throw new BizException("验证码过期或错误");
        }
    }

    private void checkCaptchaNull(LoginDTO loginDTO) {
        if (Objects.isNull(loginDTO) || Objects.isNull(loginDTO.getCaptcha()) || Objects.isNull(loginDTO.getCaptchaId())) {
            throw new BizException("验证码不能为空");
        }
    }

    private void checkNameAndPassword(LoginDTO loginDTO) {
        try {
            if (Objects.nonNull(loginDTO) && Objects.nonNull(loginDTO.getUsername()) && Objects.nonNull(loginDTO.getPassword())) {
                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::getName, loginDTO.getUsername()).eq(User::getPassword, loginDTO.getPassword());
                User user = userMapper.selectOne(wrapper);
                if (Objects.nonNull(user)) {
                    log.info("success");
                } else {
                    throw new BizException("账号或密码错误");
                }
            } else {
                throw new BizException("账号或密码错误");
            }
        } catch (Exception e) {
            throw new BizException("账号或密码错误");
        }
    }

}
