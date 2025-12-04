package com.aw.controller;

import com.aw.dto.CaptchaDTO;
import com.aw.dto.DeptDTO;
import com.aw.dto.LoginDTO;
import com.aw.dto.groups.*;
import com.aw.exception.Result;
import com.aw.limit.AccessLimit;
import com.aw.limit.LimitType;
import com.aw.login.LoginRequired;
import com.aw.service.AuthService;
import com.aw.validate.ValidatorUtil;
import com.aw.vo.CaptchaVO;
import com.aw.vo.DeptVO;
import com.aw.vo.LoginVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
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

    @PostMapping("/captcha")
    @AccessLimit(seconds = 60, limitType = LimitType.IP)
    public Result<CaptchaVO> captcha(@RequestBody CaptchaDTO captchaDTO) {
        ValidatorUtil.validate(captchaDTO, CaptchaGroup.class);
        CaptchaVO captchaVO = authService.captcha(captchaDTO);
        return Result.success(captchaVO);
    }

    @PostMapping("/captcha/verify")
    public Result<CaptchaVO> captchaVerify(@RequestBody CaptchaDTO captchaDTO) {
        ValidatorUtil.validate(captchaDTO, CaptchaVerifyGroup.class);
        authService.captchaVerify(captchaDTO);
        return Result.success();
    }

    @PostMapping("/password/change")
    @LoginRequired
    public Result<CaptchaVO> passwordChange(@RequestBody LoginDTO loginDTO) {
        ValidatorUtil.validate(loginDTO, ChangePasswordGroup.class);
        authService.passwordChange(loginDTO);
        return Result.success();
    }

    @PostMapping("/dept/tree")
    @LoginRequired
    public Result<DeptVO> deptTree() {
        DeptVO deptVO = authService.deptTree();
        return Result.success(deptVO);
    }

    @PostMapping("/dept/update")
    @LoginRequired
    public Result<DeptVO> updateDept(@RequestBody DeptDTO deptDTO) {
        if (Objects.nonNull(deptDTO.getId())) {
            ValidatorUtil.validate(deptDTO, DeptUpdateGroup.class);
        }else {
            ValidatorUtil.validate(deptDTO, DeptAddGroup.class);
        }
        authService.updateDept(deptDTO);
        return Result.success();
    }

    @PostMapping("/dept/delete")
    @LoginRequired
    public Result<DeptVO> deleteDept(@RequestBody DeptDTO deptDTO) {
        ValidatorUtil.validate(deptDTO, DeptDeleteGroup.class);
        authService.deleteDept(deptDTO);
        return Result.success();
    }

}
