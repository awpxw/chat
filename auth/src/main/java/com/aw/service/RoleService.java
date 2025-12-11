package com.aw.service;

import com.aw.dto.RoleDTO;
import com.aw.entity.Role;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface RoleService {

    void roleUpdate(RoleDTO roleDTO);

    void roleDelete(RoleDTO roleDTO);

    IPage<Role> page(RoleDTO roleDTO);

    void allot(RoleDTO roleDTO);

    void cancel(RoleDTO roleDTO);

}
