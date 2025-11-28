package com.aw.service.impl;

import com.aw.dto.LoginDTO;
import com.aw.exception.BizException;
import com.aw.jwt.JwtUtil;
import com.aw.service.AuthService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)  // 加这行！
class AuthControllerIntegrationTest {

    @Autowired
    TestRestTemplate rest;

    @Resource
    private AuthService authService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void login_success() {
        redisTemplate.opsForValue().set("captcha:admin", "1111");
        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("admin");
        dto.setCaptcha("admin");
        dto.setCaptchaId("1111");
        ResponseEntity<String> r = rest.postForEntity("/api/auth/login", dto, String.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void login_captcha_empty() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("123456");
        BizException e = assertThrows(BizException.class, () -> authService.login(loginDTO));
        assertEquals("验证码不能为空", e.getMessage());
    }

    @Test
    void login_name_or_password_wrong() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setCaptcha("123456");
        loginDTO.setCaptchaId("123456");
        BizException e = assertThrows(BizException.class, () -> authService.login(loginDTO));
        assertEquals("账号或密码错误", e.getMessage());

        LoginDTO loginDTO1 = new LoginDTO();
        loginDTO1.setPassword("111");
        loginDTO1.setCaptcha("123456");
        loginDTO1.setCaptchaId("123456");
        BizException e1 = assertThrows(BizException.class, () -> authService.login(loginDTO1));
        assertEquals("账号或密码错误", e1.getMessage());

        LoginDTO loginDTO2 = new LoginDTO();
        loginDTO2.setUsername("admin");
        loginDTO2.setCaptcha("123456");
        loginDTO2.setCaptchaId("123456");
        BizException e2 = assertThrows(BizException.class, () -> authService.login(loginDTO2));
        assertEquals("账号或密码错误", e2.getMessage());
    }

    @Test
    void captcha_expired_or_wrong() {
        LoginDTO loginDTO2 = new LoginDTO();
        loginDTO2.setUsername("admin");
        loginDTO2.setPassword("admin");
        loginDTO2.setCaptcha("123456");
        loginDTO2.setCaptchaId("123456");
        BizException e = assertThrows(BizException.class, () -> authService.login(loginDTO2));
        assertEquals("验证码过期或错误", e.getMessage());
    }

    @Test
    void account_banned() {
        redisTemplate.opsForValue().set("captcha:captcha:123456", "1111");
        LoginDTO loginDTO2 = new LoginDTO();
        loginDTO2.setUsername("test");
        loginDTO2.setPassword("test");
        loginDTO2.setCaptcha("captcha:123456");
        loginDTO2.setCaptchaId("1111");
        BizException e = assertThrows(BizException.class, () -> authService.login(loginDTO2));
        assertEquals("账号已被禁用", e.getMessage());
    }

    @Test
    void testRegister() {
        redisTemplate.opsForValue().set("captcha:123456", "1111");
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("user1");
        loginDTO.setPassword("123456");
        loginDTO.setCaptcha("123456");
        loginDTO.setCaptchaId("1111");
        loginDTO.setMobile("19514700697");
        ResponseEntity<String> response = rest.postForEntity("/api/auth/register", loginDTO, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void name_repeat() {
        redisTemplate.opsForValue().set("captcha:123456", "1111");
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("admin");
        BizException e = assertThrows(BizException.class, () -> authService.register(loginDTO));
        assertEquals("用户名重复", e.getMessage());
    }

    @Test
    void phone_repeat() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin1");
        loginDTO.setPassword("1");
        loginDTO.setMobile("19514700698");
        BizException e = assertThrows(BizException.class, () -> authService.register(loginDTO));
        assertEquals("手机号重复", e.getMessage());
    }

    @Test
    void captcha_wrong() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("admin1");
        loginDTO.setPassword("1");
        loginDTO.setMobile("19514700697");
        loginDTO.setCaptcha("123456");
        loginDTO.setCaptchaId("123456");
        BizException e = assertThrows(BizException.class, () -> authService.register(loginDTO));
        assertEquals("验证码过期或错误", e.getMessage());
    }

    @Test
    void refresh_token_success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setRefreshToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzY0MzExMzE3LCJleHAiOjE3NjQzMTg1MTcsInVzZXJuYW1lIjoiYWRtaW4ifQ.nqytGq2aPzr8xovPLvvgAUv9gW5JK-Um17JrKyhlm2Y");
        ResponseEntity<String> response = rest.postForEntity("/api/auth/refresh", loginDTO, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void refresh_token_expired() {
        LoginDTO loginDTO = new LoginDTO();
        String accessTokenWithExpired = jwtUtil.generateAccessTokenWithExpired(1L, "admin", null,-1L);
        loginDTO.setRefreshToken(accessTokenWithExpired);
        BizException e = assertThrows(BizException.class, () -> authService.refresh(loginDTO));
        assertEquals("验证码过期或错误", e.getMessage());
    }

    @Test
    void refresh_token_wrong() {
        LoginDTO loginDTO = new LoginDTO();
        String accessTokenWithExpired = jwtUtil.generateAccessTokenWithExpired(1L, "admin", null,-1L);
        loginDTO.setRefreshToken(accessTokenWithExpired+"///");
        BizException e = assertThrows(BizException.class, () -> authService.refresh(loginDTO));
        assertEquals("验证码过期或错误", e.getMessage());
    }

}
