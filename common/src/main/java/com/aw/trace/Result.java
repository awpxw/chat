package com.aw.trace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Result.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;        // 状态码：200 成功，400+ 失败
    private String msg;      // 提示信息
    private T data;          // 数据
    private String traceId;  // 请求链路追踪ID（出问题好查日志）

    // 成功
    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data, TraceIdUtil.getTraceId());
    }

    // 失败（业务异常）
    public static <T> Result<T> fail(String msg) {
        return fail(400, msg);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg, null, TraceIdUtil.getTraceId());
    }

    // 失败（带业务码）
    public static <T> Result<T> fail(BizCodeEnum bizCode) {
        return new Result<>(bizCode.getCode(), bizCode.getMessage(), null, TraceIdUtil.getTraceId());
    }

}