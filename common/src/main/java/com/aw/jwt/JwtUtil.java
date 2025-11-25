// src/main/java/com/bzt/common/utils/JwtUtil.java
package com.aw.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类（纯工具，无任何配置硬编码）
 * 所有项目引用 common 模块后，只需在 application.yml 配置 jwt.xxx 即可
 */
@Component
public class JwtUtil {

    private final JwtProperties properties;

    private SecretKey key;

    public JwtUtil(JwtProperties properties) {
        this.properties = properties;
    }


    public void init() {
        // 自动生成安全的 HMAC 密钥（要求 secret >= 256 bit）
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes());
    }

    /**
     * 生成 AccessToken
     */
    public String generateAccessToken(Long userId, String username, Map<String, Object> extraClaims) {
        return generateToken(userId, username, extraClaims, properties.getAccessExpire());
    }

    /**
     * 生成 RefreshToken（只包含 userId）
     */
    public String generateRefreshToken(Long userId) {
        return generateToken(userId, null, null, properties.getRefreshExpire());
    }

    /**
     * 解析 token 获取所有 claims
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取用户ID
     */
    public Long getUserId(String token) {
        return Long.valueOf(parseToken(token).getSubject());
    }

    /**
     * 获取用户名（可能为 null）
     */
    public String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    /**
     * 判断 token 是否过期
     */
    public boolean isExpired(String token) {
        try {
            return parseToken(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 校验 token 是否有效（签名正确 + 未过期）
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return !isExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 内部通用生成方法
     */
    private String generateToken(Long userId, String username,
                                 Map<String, Object> extraClaims, long expireSeconds) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expireSeconds * 1000);

        JwtBuilder builder = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key);

        if (username != null) {
            builder.claim("username", username);
        }
        if (extraClaims != null && !extraClaims.isEmpty()) {
            extraClaims.forEach(builder::claim);
        }

        return builder.compact();
    }
}