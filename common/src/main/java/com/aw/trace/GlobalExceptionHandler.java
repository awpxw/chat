package com.aw.trace;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.net.BindException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. 业务异常（最常用）
    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e) {
        log.warn("业务异常: {} traceId={}", e.getMessage(), TraceIdUtil.getTraceId());
        return Result.fail(e.getCode(), e.getMessage());
    }

    // 2. 参数校验异常（@Valid）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return Result.fail(BizCodeEnum.PARAM_ERROR.getCode(), msg);
    }

    // 3. BindException（@Validated 在 Controller 上）
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String msg = e.getMessage();
        return Result.fail(BizCodeEnum.PARAM_ERROR.getCode(), msg);
    }

    // 4. 登录失效（JWT 过期、无效）
    @ExceptionHandler(AuthenticationException.class)
    public Result<?> handleAuthException(AuthenticationException e) {
        return Result.fail(BizCodeEnum.USER_NOT_LOGIN);
    }


    // 5. 兜底异常
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常 traceId={}", TraceIdUtil.getTraceId(), e);
        return Result.fail(BizCodeEnum.UNKNOWN_ERROR);
    }
}