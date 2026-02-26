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

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey("blacklist:access:" + token)) ||
               Boolean.TRUE.equals(stringRedisTemplate.hasKey("blacklist:refresh:" + token));
    }

}