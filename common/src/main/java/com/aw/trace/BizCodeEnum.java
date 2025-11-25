package com.aw.trace;

import lombok.Getter;

// BizCodeEnum.java
@Getter
public enum BizCodeEnum {
    SUCCESS(200, "成功"),

    // 通用错误 10000~
    UNKNOWN_ERROR(10000, "系统异常，请稍后重试"),
    PARAM_ERROR(10001, "参数错误"),
    TOO_MANY_REQUESTS(10002, "请求太频繁，请稍后重试"),

    // 用户相关 11000~
    USER_NOT_LOGIN(11001, "请先登录"),
    USER_LOGIN_EXPIRED(11002, "登录已过期"),
    USER_TOKEN_INVALID(11003, "无效的令牌"),
    USER_REFRESH_TOKEN_EXPIRED(11004, "登录已过期，请重新登录"),
    USER_FORBIDDEN(11005, "无权限访问"),
    USER_PASSWORD_ERROR(11006, "用户名或密码错误"),

    //redis相关
    REDIS_OPERATION_FAIL(12001,"redis操作是失败");

    private final int code;
    private final String message;

    BizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}