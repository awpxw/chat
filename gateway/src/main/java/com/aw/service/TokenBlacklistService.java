package com.aw.service;

import com.aw.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Resource;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private JwtUtil jwtUtil;

    // 登出时调用
    public void blacklist(String accessToken, String refreshToken) {
        long accessTtl = getRemainingSeconds(accessToken);   // 解析出剩余时间
        long refreshTtl = getRemainingSeconds(refreshToken);
        // access_token 加入黑名单（短时间）
        stringRedisTemplate.opsForValue().set("blacklist:access:" + accessToken, "1", accessTtl, TimeUnit.SECONDS);
        // refresh_token 加入黑名单（长时间）
        stringRedisTemplate.opsForValue().set("blacklist:refresh:" + refreshToken, "1", refreshTtl, TimeUnit.SECONDS);
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey("blacklist:access:" + token)) ||
               Boolean.TRUE.equals(stringRedisTemplate.hasKey("blacklist:refresh:" + token));
    }

    private long getRemainingSeconds(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtUtil.key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;
        } catch (Exception e) {
            return 3600; // 解析失败给个默认值
        }
    }
}