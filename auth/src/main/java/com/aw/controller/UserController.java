package com.aw.controller;

import com.aw.dto.UserDTO;
import com.aw.dto.groups.*;
import com.aw.entity.User;
import com.aw.exception.Result;
import com.aw.login.LoginRequired;
import com.aw.service.UserService;
import com.aw.validate.ValidatorUtil;
import com.aw.vo.DeptVO;
import com.aw.vo.MenuTreeResultVO;
import com.aw.vo.UserDetailVO;
import com.aw.vo.UserPageVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


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

    @PostMapping("/detail")
    @LoginRequired
    public Result<UserDetailVO> detail() {
        UserDetailVO detail = userService.detail();
        return Result.success(detail);
    }

    @PostMapping("/update")
    @LoginRequired
    public Result<DeptVO> userUpdate(@RequestBody UserDTO userDTO) {
        ValidatorUtil.validate(userDTO, UserUpdateGroup.class);
        userService.update(userDTO);
        return Result.success();
    }

    @PostMapping("/profile/update")
    @LoginRequired
    public Result<DeptVO> profileUpdate(@RequestBody UserDTO userDTO) {
        ValidatorUtil.validate(userDTO, ProfileUpdate.class);
        userService.profileUpdate(userDTO);
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
    public Result<IPage<UserPageVO>> page(@RequestBody UserDTO userDTO) {
        IPage<UserPageVO> result = userService.page(userDTO);
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

    @PostMapping("/menu/list")
    @LoginRequired
    public Result<List<Long>> menuList(@RequestBody UserDTO userDTO) {
        ValidatorUtil.validate(userDTO, UserMenuTreeGroup.class);
        List<Long> menuIds = userService.menuList(userDTO);
        return Result.success(menuIds);
    }


    @PostMapping("/changePwd")
    @LoginRequired
    public Result<String> updatePass(@RequestBody UserDTO userDTO) {
        ValidatorUtil.validate(userDTO, PasswordChange.class);
        userService.updatePass(userDTO);
        return Result.success();
    }

}
