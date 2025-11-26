// common/src/main/java/com/aw/common/util/RedisUtil.java
package com.aw.redis;

import com.aw.trace.BizException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== String → String ====================
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public String getString(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        return obj == null ? null : obj.toString();
    }

    // ==================== Object → String ====================
    public <T> void set(String key, T value) {
        redisTemplate.opsForValue().set(key, toJson(value));
    }

    public <T> void set(String key, T value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, toJson(value), timeout, unit);
    }

    // ==================== String → Object ====================
    public <T> T get(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForValue().get(key);
        return obj == null ? null : fromJson(obj.toString(), clazz);
    }

    public <T> T get(String key, TypeReference<T> typeReference) {
        Object obj = redisTemplate.opsForValue().get(key);
        return obj == null ? null : fromJson(obj.toString(), typeReference);
    }

    // ==================== 通用方法 ====================
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    // ==================== JSON 序列化 ====================
    private <T> String toJson(T value) {
        if (value == null) return null;
        if (value instanceof String ) return (String) value;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("Redis 序列化失败: {}", value, e);
            throw new BizException(12001,"Redis 序列化失败");
        }
    }

    private <T> T fromJson(String json, Class<T> clazz) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Redis 反序列化失败: {}", json, e);
            throw new BizException(12001,"Redis 序列化失败");
        }
    }

    private <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("Redis 反序列化失败: {}", json, e);
            throw new BizException(12001,"Redis 序列化失败");
        }
    }
}