// src/test/java/com/aw/jwt/JwtUtilTest.java
package com.aw;

import com.aw.jwt.JwtProperties;
import com.aw.jwt.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String VALID_BASE64_SECRET = "qwdrtfgyhjokplfgtwrsyegfhwrtyePLKUYTRFGHJMNBVCFDRT";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(VALID_BASE64_SECRET);
        properties.setAccessExpire(900L);   // 15分钟
        properties.setRefreshExpire(604800L); // 7天
        // 2. 手动创建 JwtUtil（会自动触发 @PostConstruct）
        jwtUtil = new JwtUtil(properties);
        jwtUtil.init();
    }

    @Test
    @DisplayName("生成 AccessToken 成功")
    void generateAccessToken_success() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "admin");
        claims.put("deptId", 1L);
        String token = jwtUtil.generateAccessToken(1001L, "zhangsan", claims);
        Assertions.assertNotNull(token);
        Assertions.assertEquals(3, token.split("\\.").length);
        System.out.println("AccessToken: " + token);
    }

    @Test
    @DisplayName("解析 token 正确返回 userId、username、自定义 claims")
    void parseToken_success() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "admin");
        String token = jwtUtil.generateAccessToken(1001L, "zhangsan", claims);
        assertEquals(1001L, jwtUtil.getUserId(token));
        assertEquals("zhangsan", jwtUtil.getUsername(token));
        assertEquals("admin", jwtUtil.parseToken(token).get("role"));
    }

    @Test
    @DisplayName("有效 token 验证通过")
    void validateToken_success() {
        String token = jwtUtil.generateAccessToken(1001L, "zhangsan", null);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("过期 token 验证失败")
    void validateToken_expired_fail() throws InterruptedException {
        // 构造一个超短命的 JwtUtil（1秒过期）
        JwtProperties shortLive = new JwtProperties();
        shortLive.setSecret("a1234567890-a1234567890-a1234567890-a1234567890");
        shortLive.setAccessExpire(1L); // 1秒
        JwtUtil tempUtil = new JwtUtil(shortLive);
        tempUtil.init();
        String token = tempUtil.generateAccessToken(1001L, "zhangsan", null);
        Thread.sleep(1100);
        assertTrue(tempUtil.isExpired(token));
        assertFalse(tempUtil.validateToken(token));
    }

    @Test
    @DisplayName("签名错误 token 验证失败")
    void validateToken_invalidSignature_fail() {
        String token = jwtUtil.generateAccessToken(1001L, "zhangsan", null);
        String tampered = token + "xxx";
        assertFalse(jwtUtil.validateToken(tampered));
    }

    @Test
    @DisplayName("RefreshToken 只包含 userId，username 为 null")
    void generateRefreshToken_success() {
        String refreshToken = jwtUtil.generateRefreshToken(1001L);
        Assertions.assertNotNull(refreshToken);
        assertEquals(1001L, jwtUtil.getUserId(refreshToken));
        Assertions.assertNull(jwtUtil.getUsername(refreshToken)); // refresh 不带 username
    }

}