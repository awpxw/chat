package com.aw.interceptor;

import com.aw.utils.RequestIdUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// RequestIdInterceptor.java
@Component
public class RequestIdInterceptor implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                // 1. 优先从请求头拿（网关传过来的）
                String requestId = request.getHeader("X-Request-Id");
                if (StringUtils.isBlank(requestId)) {
                    requestId = RequestIdUtil.generate();  // 没有就自己生成
                } else {
                    RequestIdUtil.set(requestId);  // 有就用网关的
                }

                // 2. 响应头也带上（前端、Postman、Swagger 都能看到）
                response.setHeader("X-Request-Id", requestId);
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
                RequestIdUtil.clear();  // 清理，防止内存泄漏
            }
        }).order(Ordered.HIGHEST_PRECEDENCE);  // 最高优先级，最先执行
    }
}