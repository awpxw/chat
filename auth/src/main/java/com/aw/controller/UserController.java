package com.aw.controller;

import com.aw.dto.UserDTO;
import com.aw.dto.groups.UserAddGroup;
import com.aw.dto.groups.UserAllotRoleGroup;
import com.aw.dto.groups.UserDeleteGroup;
import com.aw.dto.groups.UserMenuTreeGroup;
import com.aw.entity.User;
import com.aw.exception.Result;
import com.aw.login.LoginRequired;
import com.aw.service.UserService;
import com.aw.validate.ValidatorUtil;
import com.aw.vo.DeptVO;
import com.aw.vo.MenuTreeResultVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/add")
    @LoginRequired
    public Result<DeptVO> add(@RequestBody UserDTO userDTO) {
        ValidatorUtil.validate(userDTO, UserAddGroup.class);
        userService.add(userDTO);
        return Result.success();
    }

    @PostMapping("/update")
    @LoginRequired
    public Result<DeptVO> userUpdate(@RequestBody UserDTO userDTO) {
        userService.update(userDTO);
        return Result.success();
    }

    @PostMapping("/delete")
    @LoginRequired
    public Result<DeptVO> delete(@RequestBody UserDTO userDTO) {
        ValidatorUtil.validate(userDTO, UserDeleteGroup.class);
        userService.delete(userDTO);
        return Result.success();
    }

    @PostMapping("/page")
    @LoginRequired
    public Result<IPage<User>> page(@RequestBody UserDTO userDTO) {
        IPage<User> result = userService.page(userDTO);
        return Result.success(result);
    }

    @PostMapping("/ban")
    @LoginRequired
    public Result<String> ban(@RequestBody UserDTO userDTO) {
        userService.ban(userDTO);
        return Result.success();
    }

    @PostMapping("/allot/role")
    @LoginRequired
    public Result<String> allotRole(@RequestBody UserDTO userDTO) {
        ValidatorUtil.validate(userDTO, UserAllotRoleGroup.class);
        userService.allotRole(userDTO);
        return Result.success();
    }

    @PostMapping("/menu/tree")
    @LoginRequired
    public Result<MenuTreeResultVO> menuTree(@RequestBody UserDTO userDTO) {
        ValidatorUtil.validate(userDTO, UserMenuTreeGroup.class);
        MenuTreeResultVO tree = userService.menuTree(userDTO);
        return Result.success(tree);
    }


}
