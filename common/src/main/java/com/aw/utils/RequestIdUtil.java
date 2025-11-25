package com.aw.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.UUID;

// RequestIdUtil.java
@Slf4j
public class RequestIdUtil {
    private static final String REQUEST_ID_KEY = "requestId";
    private static final ThreadLocal<String> REQUEST_ID_HOLDER = new ThreadLocal<>();

    /**
     * 生成并设置 requestId
     */
    public static String generate() {
        String requestId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        set(requestId);
        return requestId;
    }

    public static void set(String requestId) {
        REQUEST_ID_HOLDER.set(requestId);
        MDC.put(REQUEST_ID_KEY, requestId);  // 放进 MDC，日志自动带
    }

    public static String get() {
        String requestId = REQUEST_ID_HOLDER.get();
        return requestId == null ? "unknown" : requestId;
    }

    public static void clear() {
        MDC.remove(REQUEST_ID_KEY);
        REQUEST_ID_HOLDER.remove();
    }
}