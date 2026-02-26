package com.aw.limit;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimit {
    // 限制次数
    int count() default 100;

    // 时间窗口（秒）
    int seconds() default 1;

    // 限流类型
    LimitType limitType() default LimitType.IP;

    // 自定义 key（支持 SpEL）
    String key() default "";

    // 提示消息
    String message() default "请求太频繁，请稍后重试";
}