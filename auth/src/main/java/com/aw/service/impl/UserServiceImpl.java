package com.aw.service.impl;

import com.aw.dto.UserDTO;
import com.aw.entity.User;
import com.aw.entity.UserRole;
import com.aw.exception.BizException;
import com.aw.login.UserContext;
import com.aw.mapper.UserMapper;
import com.aw.mapper.UserRoleMapper;
import com.aw.service.UserService;
import com.aw.snowflake.IdWorker;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private IdWorker idWorker;

    @Override
    public void add(UserDTO userDTO) {

        Long id = userDTO.getId();

        User user = selectUserInfoByUserId(id);

        bindUserToDept(user, userDTO);

    }

    private void bindUserToDept(User user, UserDTO userDTO) {
        Long deptId = userDTO.getDeptId();
        boolean result = ChainWrappers.lambdaUpdateChain(User.class).eq(User::getId, user.getId()).set(User::getDeptId, deptId).update();
        if (!result) {
            log.error("绑定用户失败,id：{}", userDTO.getId());
            throw new BizException("绑定用户失败");
        }
    }

    private User selectUserInfoByUserId(Long id) {
        return ChainWrappers.lambdaQueryChain(User.class).select(User::getId, User::getWorkNo, User::getName, User::getNickname, User::getMobile, User::getEmail, User::getAvatar, User::getDeptId, User::getPosition, User::getStatus, User::getIsAdmin, User::getPassword).eq(User::getId, id).eq(User::getStatus, 1).last("LIMIT 1").one();
    }

    @Override
    public void update(UserDTO userDTO) {

        updateUserInfoById(userDTO);

    }

    private void updateUserInfoById(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        boolean result = ChainWrappers.lambdaUpdateChain(User.class).eq(User::getId, user.getId()).eq(User::getStatus, userDTO.getStatus()).update(user);
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
    public IPage<User> page(UserDTO userDTO) {
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
