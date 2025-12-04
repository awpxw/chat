package com.aw.redis;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 终极 Redis 工具类（2025工业级版）
 * 功能：生成 key + 自定义过期 + 防穿透 + 防击穿 + 防雪崩 + 本地缓存(Caffeine) + 延迟双删
 * 直接注入使用，永不翻车！
 */
@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final StringRedisTemplate redisTemplate;

    /** 默认过期时间 10 分钟 */
    private static final long DEFAULT_EXPIRE = 10 * 60L;

    /** 防雪崩：随机额外时间 0~5 分钟 */
    private static final long RANDOM_EXPIRE_RANGE = 5 * 60L;

    /** 空值占位符，防止穿透 */
    private static final String NULL_VALUE = "__NULL__";

    /** 本地缓存：Caffeine（热点数据毫秒级返回） */
    private final Cache<String, Object> localCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)    // 本地缓存比 Redis 稍短
            .maximumSize(20_000)
            .recordStats()
            .build();

    /**
     * 通用生成 key（全项目统一格式）
     */
    public String key(String pattern, Object... args) {
        if (args == null || args.length == 0) return pattern;
        return pattern + ":" + Arrays.stream(args)
                .map(String::valueOf)
                .collect(Collectors.joining(":"));
    }

    /**
     * 终极 get（推荐所有业务都用这个！本地缓存 + Redis 双保险）
     */
    public <T> T get(String key, Supplier<T> loader, Class<T> clazz) {
        return get(key, loader, DEFAULT_EXPIRE, TimeUnit.SECONDS, clazz);
    }

    public <T> T get(String key, Supplier<T> loader, long expire, TimeUnit unit, Class<T> clazz) {
        // Step 1: 先查本地缓存（最快！）
        Object local = localCache.getIfPresent(key);
        if (local != null) {
            return local == NULL_VALUE ? null : (T) local;
        }

        // Step 2: 本地没命中 → 查 Redis + 防击穿锁
        String json = redisTemplate.opsForValue().get(key);

        if (json != null) {
            T result = NULL_VALUE.equals(json) ? null : JSONUtil.toBean(json, clazz);
            // 回种本地缓存
            localCache.put(key, result == null ? NULL_VALUE : result);
            return result;
        }

        // Step 3: Redis 也没 → 防击穿本地锁
        synchronized (key.intern()) {
            json = redisTemplate.opsForValue().get(key);
            if (json != null) {
                T result = NULL_VALUE.equals(json) ? null : JSONUtil.toBean(json, clazz);
                localCache.put(key, result == null ? NULL_VALUE : result);
                return result;
            }

            try {
                T data = loader.get();  // 真正查数据库
                long realExpire = expire + RandomUtil.randomLong(0, RANDOM_EXPIRE_RANGE);

                String valueToSet = (data == null) ? NULL_VALUE : JSONUtil.toJsonStr(data);
                redisTemplate.opsForValue().set(key, valueToSet, realExpire, unit);

                // 同步写本地缓存
                localCache.put(key, data == null ? NULL_VALUE : data);

                return data;
            } catch (Exception e) {
                // 异常时放一个短时占位，防止持续击穿
                redisTemplate.opsForValue().set(key, NULL_VALUE, 30, TimeUnit.SECONDS);
                localCache.put(key, NULL_VALUE);
                throw e;
            }
        }
    }

    /** 删除缓存（同时删 Redis 和本地） */
    public void delete(String key) {
        redisTemplate.delete(key);
        localCache.invalidate(key);
    }

    /** 延迟双删（写完库后调用） */
    @Async("ioExecutor")
    public void delayDoubleDelete(String key) {
        try {
            Thread.sleep(1000);
            redisTemplate.delete(key);
            localCache.invalidate(key);
        } catch (InterruptedException ignored) {}
    }

    /** 手动设置缓存（预热专用） */
    public <T> void set(String key, T data) {
        set(key, data, DEFAULT_EXPIRE, TimeUnit.SECONDS);
    }

    public <T> void set(String key, T data, long expire, TimeUnit unit) {
        String value = (data == null) ? NULL_VALUE : JSONUtil.toJsonStr(data);
        long realExpire = expire + RandomUtil.randomLong(0, RANDOM_EXPIRE_RANGE);
        redisTemplate.opsForValue().set(key, value, realExpire, unit);
        localCache.put(key, data == null ? NULL_VALUE : data);
    }
}