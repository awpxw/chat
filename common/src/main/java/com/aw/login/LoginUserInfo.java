package com.aw.login;

import lombok.Data;

@Data
public class LoginUserInfo {

    private Long userId;

    private String username;

    private String accessToken;

    private String refreshToken;

}