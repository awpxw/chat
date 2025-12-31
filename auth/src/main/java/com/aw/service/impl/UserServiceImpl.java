package com.aw.service.impl;

import com.aw.dto.UserDTO;
import com.aw.entity.Dept;
import com.aw.entity.Menu;
import com.aw.entity.User;
import com.aw.entity.UserRole;
import com.aw.exception.BizException;
import com.aw.jwt.JwtUtil;
import com.aw.login.UserContext;
import com.aw.mapper.UserMapper;
import com.aw.mapper.UserRoleMapper;
import com.aw.service.UserService;
import com.aw.snowflake.IdWorker;
import com.aw.utils.tree.TreeUtil;
import com.aw.vo.MenuTreeResultVO;
import com.aw.vo.MenuTreeVO;
import com.aw.vo.UserDetailVO;
import com.aw.vo.UserPageVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private IdWorker idWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public void add(UserDTO userDTO) {

        Long userId = saveUser(userDTO);

        bindUserToDept(userId, userDTO);

    }

    private Long saveUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        int success = userMapper.insert(user);
        if (success <= 0) {
            throw new BizException("创建用户失败");
        }
        return user.getId();
    }

    private void bindUserToDept(Long userId, UserDTO userDTO) {
        Long deptId = userDTO.getDeptId();
        boolean result = ChainWrappers.lambdaUpdateChain(User.class).eq(User::getId, userId).set(User::getDeptId, deptId).update();
        if (!result) {
            log.error("绑定用户失败,id：{}", userDTO.getId());
            throw new BizException("绑定用户失败");
        }
    }

    @Override
    public void update(UserDTO userDTO) {

        updateUserInfoById(userDTO);

    }

    private void updateUserInfoById(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        boolean result = ChainWrappers.lambdaUpdateChain(User.class)
                .eq(User::getId, userDTO.getId())
                .set(User::getName, user.getName())
                .set(User::getWorkNo, user.getWorkNo())
                .set(User::getMobile, userDTO.getMobile())
                .set(User::getDeptId, userDTO.getDeptId())
                .set(User::getPosition, userDTO.getPosition())
                .set(User::getPassword, userDTO.getPassword())
                .set(User::getStatus, userDTO.getStatus())
                .update(user);
        if (!result) {
            log.error("更新用户失败,id:{}", userDTO.getId());
            throw new BizException("更新用户失败");
        }
    }

    @Override
    public void delete(UserDTO userDTO) {

        deleteUserInfoById(userDTO);

    }

    @Override
    public IPage<UserPageVO> page(UserDTO userDTO) {
        Integer pageNum = userDTO.getPageNum();
        Integer pageSize = userDTO.getPageSize();
        return userMapper.selectUserPage(Page.of(pageNum, pageSize), userDTO);
    }

    @Override
    public void ban(UserDTO userDTO) {
        Long id = userDTO.getId();
        Integer status = userDTO.getStatus();
        Integer originStatus = userDTO.getOriginStatus();
        boolean success = ChainWrappers.lambdaUpdateChain(User.class)
                .eq(User::getId, id)
                .eq(User::getStatus, originStatus)
                .set(User::getStatus, status)
                .update();
        if (!success) {
            log.error("【禁用/启用】员工失败,id:{}", id);
            throw new BizException("【禁用/启用】员工失败");
        }
    }

    @Override
    public void allotRole(UserDTO userDTO) {

        List<UserRole> userRoles = dto2Entity(userDTO);

        insertBatch(userRoles);

    }

    @Override
    public MenuTreeResultVO menuTree(UserDTO userDTO) {

        List<Menu> menus = selectMenuByRoleId(userDTO);

        List<MenuTreeVO> treeVO = menu2TreeVO(menus);

        return buildMenuTree(treeVO);

    }

    @Override
    public UserDetailVO detail() {

        UserDetailVO userDetailVO = findUserById();

        String deptName = findDeptNameById(userDetailVO);

        userDetailVO.setDeptName(deptName);

        return userDetailVO;

    }

    private String findDeptNameById(UserDetailVO userDetailVO) {
        Dept dept = ChainWrappers.lambdaQueryChain(Dept.class)
                .eq(Dept::getId, userDetailVO.getDeptId())
                .one();
        return dept == null ? null : dept.getName();
    }


    private UserDetailVO findUserById() {
        Long userId = UserContext.get().getUserId();
        User user = ChainWrappers.lambdaQueryChain(User.class)
                .eq(User::getId, userId)
                .eq(User::getStatus, 1)
                .one();
        UserDetailVO userDetailVO = new UserDetailVO();
        BeanUtils.copyProperties(user, userDetailVO);
        return userDetailVO;
    }

    @Override
    public void profileUpdate(UserDTO userDTO) {
        boolean success = ChainWrappers.lambdaUpdateChain(User.class)
                .eq(User::getId, userDTO.getId())
                .set(User::getName, userDTO.getName())
                .set(User::getNickname, userDTO.getNickname())
                .set(User::getMobile, userDTO.getMobile())
                .set(User::getEmail, userDTO.getEmail())
                .update();
        if (!success) {
            log.error("更新用户个人信息失败，id：{}", userDTO.getId());
            throw new BizException("更新用户个人信息失败");
        }
    }

    @Override
    public void updatePass(UserDTO userDTO) {

        checkIfInValid(userDTO);

        updatePassword(userDTO);

        kickOut();

    }

    @Override
    public List<Long> menuList(UserDTO userDTO) {

        List<Menu> menus = selectMenuByRoleId(userDTO);

        return menus.stream().filter(Objects::nonNull).map(Menu::getId).toList();

    }

    private void kickOut() {
        String token = UserContext.get().getAccessToken();
        if (token == null) {
            return;
        }
        try {
            String tokenStr = token.substring(7);
            Long ttl = remainSeconds(tokenStr);
            stringRedisTemplate.opsForValue().set("blacklist:access:" + tokenStr, "__NULL__", ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            stringRedisTemplate.opsForValue().set("blacklist:access:" + token, "__NULL__", 3600, TimeUnit.SECONDS);
            log.error("token解析失败");
        }
    }

    private Long remainSeconds(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtUtil.key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;
        } catch (Exception e) {
            return 3600L; // 解析失败给个默认值
        }
    }

    private void updatePassword(UserDTO userDTO) {
        String newPassword = userDTO.getNewPassword();
        String oldPassword = userDTO.getOldPassword();
        boolean success = ChainWrappers.lambdaUpdateChain(User.class)
                .eq(User::getId, userDTO.getId())
                .eq(User::getPassword, oldPassword)
                .set(User::getPassword, newPassword)
                .update();
        if (!success) {
            throw new BizException("更新密码失败");
        }
    }

    private void checkIfInValid(UserDTO userDTO) {
        String oldPassword = userDTO.getOldPassword();
        String newPassword = userDTO.getNewPassword();
        if (Objects.equals(oldPassword, newPassword)) {
            throw new BizException("新旧密码一致，无法修改");
        }
    }

    private List<Menu> selectMenuByRoleId(UserDTO userDTO) {
        return userMapper.selectMenuByRoleId(userDTO.getRoleId());
    }

    private List<MenuTreeVO> menu2TreeVO(List<Menu> menus) {
        return menus.stream().map(menu -> {
            MenuTreeVO menuTreeVO = new MenuTreeVO();
            BeanUtils.copyProperties(menu, menuTreeVO);
            return menuTreeVO;
        }).toList();
    }


    private MenuTreeResultVO buildMenuTree(List<MenuTreeVO> treeVO) {
        List<MenuTreeVO> menuTreeVOS = TreeUtil.buildTree(treeVO, 0L);
        MenuTreeResultVO menuTreeResultVO = new MenuTreeResultVO();
        menuTreeResultVO.setMenuTree(menuTreeVOS);
        return menuTreeResultVO;
    }

    private void insertBatch(List<UserRole> userRoles) {
        Long loginUserId = UserContext.get().getUserId();
        Integer success = userRoleMapper.insertBatch(userRoles, loginUserId);
        if (success <= 0) {
            log.error("用户分配角色失败，id：{}", loginUserId);
            throw new BizException("用户分配角色失败");
        }
    }

    private List<UserRole> dto2Entity(UserDTO userDTO) {
        Long userId = userDTO.getId();
        List<Long> roleIds = userDTO.getRoleIds();
        List<UserRole> userRoles = new ArrayList<>();
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setId(idWorker.nextId());
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoles.add(userRole);
        }
        return userRoles;
    }


    private void deleteUserInfoById(UserDTO userDTO) {
        Long id = userDTO.getId();
        boolean result = ChainWrappers.lambdaUpdateChain(User.class)
                .eq(User::getId, id)
                .set(User::getDeleted, 1)
                .update();
        if (!result) {
            log.error("用户移除失败,id:{}", userDTO.getId());
            throw new BizException("用户移除失败");
        }
    }

}
