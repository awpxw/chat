package com.aw.advince;

import com.aw.exception.BizCodeEnum;
import com.aw.exception.BizException;
import com.aw.exception.Result;
import com.aw.trace.TraceIdUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.net.BindException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. 业务异常（最常用）
    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e, HttpServletResponse response) {
        log.warn("业务异常: {} traceId={}", e.getMessage(), TraceIdUtil.getTraceId());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());  // 500
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理 @Valid + ValidatorUtil 抛出的参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e,HttpServletResponse response) {
        String msg = e.getConstraintViolations().stream()
                .map(violation -> {
                    // 尝试取字段名（如果是方法参数校验，path 会是 "arg0"、"create.arg0" 这种）
                    String field = violation.getPropertyPath().toString();
                    // 去掉方法名前缀，只保留参数名（如 arg0 -> username）
                    int dotIndex = field.lastIndexOf('.');
                    if (dotIndex != -1) {
                        field = field.substring(dotIndex + 1);
                    }
                    return field + violation.getMessage();
                })
                .collect(Collectors.joining("；"));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        log.warn("参数校验失败: {} traceId={}", msg, TraceIdUtil.getTraceId());
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