package com.aw.controller;

import cn.hutool.db.Page;
import com.aw.dto.RoleDTO;
import com.aw.dto.groups.RoleAllotGroup;
import com.aw.dto.groups.RoleCancelGroup;
import com.aw.entity.Role;
import com.aw.exception.Result;
import com.aw.login.LoginRequired;
import com.aw.service.RoleService;
import com.aw.validate.ValidatorUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @PostMapping("/update")
    @LoginRequired
    public Result<String> update(@RequestBody RoleDTO roleDTO) {
        roleService.roleUpdate(roleDTO);
        return Result.success();
    }

    @PostMapping("/delete")
    @LoginRequired
    public Result<String> delete(@RequestBody RoleDTO roleDTO) {
        roleService.roleDelete(roleDTO);
        return Result.success();
    }

    @PostMapping("/page")
    @LoginRequired
    public Result<IPage<Role>> page(@RequestBody RoleDTO roleDTO) {
        IPage<Role> page = roleService.page(roleDTO);
        return Result.success(page);
    }


    @PostMapping("/allot")
    @LoginRequired
    public Result<String> allot(@RequestBody RoleDTO roleDTO) {
        ValidatorUtil.validate(roleDTO, RoleAllotGroup.class);
        roleService.allot(roleDTO);
        return Result.success();
    }


    @PostMapping("/cancel")
    @LoginRequired
    public Result<String> cancel(@RequestBody RoleDTO roleDTO) {
        ValidatorUtil.validate(roleDTO, RoleCancelGroup.class);
        roleService.cancel(roleDTO);
        return Result.success();
    }

}


