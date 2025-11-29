package com.aw.dto;

import com.aw.dto.groups.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class LoginDTO {

    @NotBlank(message = "用户名不为空", groups = {RegisterGroup.class, LoginGroup.class})
    private String username;

    @NotBlank(message = "密码不为空", groups = {RegisterGroup.class, LoginGroup.class,ChangePasswordGroup.class})
    private String password;

    private String captcha;

    private String captchaId;

    @Pattern(regexp = "^1(3\\d|4[01456879]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[012356789])\\d{8}$", message = "手机号格式不正确", groups = {RegisterGroup.class})
    private String mobile;

    @NotBlank(message = "access token 不为空", groups = {RegisterGroup.class, LogoutGroup.class})
    private String accessToken;

    @NotBlank(message = "refresh =token 不为空", groups = {RefreshGroup.class, LogoutGroup.class})
    private String refreshToken;

    @NotBlank(message = "密码不为空", groups = {ChangePasswordGroup.class})
    private String oldPassword;

}
