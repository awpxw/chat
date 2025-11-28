package com.aw.controller;

import com.aw.dto.LoginDTO;
import com.aw.dto.groups.LoginGroup;
import com.aw.dto.groups.RefreshGroup;
import com.aw.dto.groups.RegisterGroup;
import com.aw.exception.Result;
import com.aw.service.AuthService;
import com.aw.validate.ValidatorUtil;
import com.aw.vo.LoginVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        ValidatorUtil.validate(loginDTO, LoginGroup.class);
        LoginVO login = authService.login(loginDTO);
        return Result.success(login);
    }

    @PostMapping("/register")
    public Result<LoginVO> register(@RequestBody LoginDTO loginDTO) {
        ValidatorUtil.validate(loginDTO, RegisterGroup.class);
        authService.register(loginDTO);
        return Result.success();
    }

    @PostMapping("/refresh")
    public Result<LoginVO> refresh(@RequestBody LoginDTO loginDTO) {
        ValidatorUtil.validate(loginDTO, RefreshGroup.class);
        LoginVO loginVO = authService.refresh(loginDTO);
        return Result.success(loginVO);
    }

    @PostMapping("/logout")
    public Result<LoginVO> logout(@RequestBody LoginDTO loginDTO) {
        ValidatorUtil.validate(loginDTO, RefreshGroup.class);
        LoginVO loginVO = authService.logout(loginDTO);
        return Result.success(loginVO);
    }


}
