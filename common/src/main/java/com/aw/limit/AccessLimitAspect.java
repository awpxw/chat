// common/src/main/java/com/aw/common/aspect/AccessLimitAspect.java
package com.aw.limit;

import com.aw.trace.BizException;
import com.aw.utils.RequestIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Objects;

@Slf4j
@Aspect
@Component
@Order(1) // 确保在事务前执行
@RequiredArgsConstructor
public class AccessLimitAspect {

    private final StringRedisTemplate stringRedisTemplate;

    // Lua 脚本（原子性保证）
    private static final RedisScript<Long> LIMIT_SCRIPT = RedisScript.of(
        "local key = KEYS[1]\n" +
        "local limit = tonumber(ARGV[1])\n" +
        "local expire = tonumber(ARGV[2])\n" +
        "local current = redis.call('incr', key)\n" +
        "if current == 1 then\n" +
        "    redis.call('expire', key, expire)\n" +
        "end\n" +
        "if current > limit then\n" +
        "    return 0\n" +
        "else\n" +
        "    return 1\n" +
        "end", Long.class);

    @Before("@annotation(accessLimit)")
    public void accessLimit(AccessLimit accessLimit) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;

        HttpServletRequest request = attributes.getRequest();
        String key = getLimitKey(request, accessLimit);

        Long result = stringRedisTemplate.execute(
            LIMIT_SCRIPT,
            Collections.singletonList(key),
            String.valueOf(accessLimit.count()),
            String.valueOf(accessLimit.seconds())
        );

        if (result == null || result == 0) {
            log.warn("接口限流触发 => key={}, method={}, ip={}, requestId={}",
                key, request.getMethod(), getIp(request), RequestIdUtil.get());
            throw new BizException(10002, accessLimit.message());
        }
    }

    private String getLimitKey(HttpServletRequest request, AccessLimit accessLimit) {
        String prefix = "limit:" + request.getRequestURI();
        String key;

        switch (accessLimit.limitType()) {
            case IP:
                key = prefix + ":" + getIp(request);
                break;

            case USER:
                String userId = (String) request.getAttribute("userId");
                key = prefix + ":user:" + (userId != null ? userId : "anonymous");
                break;

            case CUSTOM:
                if (Objects.equals(accessLimit.key(), "")) {
                    throw new IllegalArgumentException("自定义限流 key 不能为空");
                }
                key = prefix + ":" + accessLimit.key();
                break;
            // 可选：加上 default，防止以后新增枚举报错
            default:
                throw new IllegalArgumentException("不支持的限流类型: " + accessLimit.limitType());
        }
        return key;
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip == null ? "unknown" : ip.split(",")[0].trim();
    }
}