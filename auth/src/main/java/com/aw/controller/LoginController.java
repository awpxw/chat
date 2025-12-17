package com.aw.controller;

import com.aw.dto.CaptchaDTO;
import com.aw.dto.LoginDTO;
import com.aw.dto.groups.*;
import com.aw.exception.Result;
import com.aw.limit.AccessLimit;
import com.aw.limit.LimitType;
import com.aw.login.LoginRequired;
import com.aw.service.LoginService;
import com.aw.validate.ValidatorUtil;
import com.aw.vo.CaptchaVO;
import com.aw.vo.LoginVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Resource
    private LoginService loginService;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        ValidatorUtil.validate(loginDTO, LoginGroup.class);
        LoginVO login = loginService.login(loginDTO);
        return Result.success(login);
    }

    @PostMapping("/register")
    public Result<LoginVO> register(@RequestBody LoginDTO loginDTO) {
        ValidatorUtil.validate(loginDTO, RegisterGroup.class);
        loginService.register(loginDTO);
        return Result.success();
    }

    @PostMapping("/refresh")
    public Result<LoginVO> refresh(@RequestBody LoginDTO loginDTO) {
        ValidatorUtil.validate(loginDTO, RefreshGroup.class);
        LoginVO loginVO = loginService.refresh(loginDTO);
        return Result.success(loginVO);
    }

    @PostMapping("/logout")
    public Result<LoginVO> logout(@RequestBody LoginDTO loginDTO) {
        ValidatorUtil.validate(loginDTO, RefreshGroup.class);
        loginService.logout(loginDTO);
        return Result.success();
    }

    @PostMapping("/captcha")
    @AccessLimit(seconds = 60, limitType = LimitType.IP)
    public Result<CaptchaVO> captcha(@RequestBody CaptchaDTO captchaDTO) throws IOException {
        ValidatorUtil.validate(captchaDTO, CaptchaGroup.class);
        CaptchaVO captchaVO = loginService.captcha(captchaDTO);
        return Result.success(captchaVO);
    }

    @PostMapping("/captcha/verify")
    public Result<CaptchaVO> captchaVerify(@RequestBody CaptchaDTO captchaDTO) {
        ValidatorUtil.validate(captchaDTO, CaptchaVerifyGroup.class);
        loginService.captchaVerify(captchaDTO);
        return Result.success();
    }

    @PostMapping("/password/change")
    @LoginRequired
    public Result<CaptchaVO> passwordChange(@RequestBody LoginDTO loginDTO) {
        ValidatorUtil.validate(loginDTO, ChangePasswordGroup.class);
        loginService.passwordChange(loginDTO);
        return Result.success();
    }

}
