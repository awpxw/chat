package com.aw.service.impl;

import com.aw.dto.RoleDTO;
import com.aw.dto.groups.RoleAddGroup;
import com.aw.dto.groups.RoleDeleteGroup;
import com.aw.dto.groups.RoleUpdateGroup;
import com.aw.entity.Role;
import com.aw.mapper.RoleMapper;
import com.aw.service.RoleService;
import com.aw.validate.ValidatorUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Override
    @Transactional
    public void roleUpdate(RoleDTO roleDTO) {
        Role role = new Role();
        Long id = roleDTO.getId();
        if (Objects.nonNull(id)) {
            ValidatorUtil.validate(roleDTO, RoleUpdateGroup.class);
            BeanUtils.copyProperties(roleDTO, role);
            ChainWrappers.lambdaUpdateChain(Role.class)
                    .eq(Role::getId, role.getId())
                    .update(role);
        } else {
            ValidatorUtil.validate(roleDTO, RoleAddGroup.class);
            BeanUtils.copyProperties(roleDTO, role);
            roleMapper.insert(role);
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

}
