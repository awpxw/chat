package com.aw.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {

        String userId = request.getHeader("x-user-id");
        String username = request.getHeader("x-username");
        if (StringUtils.isNotBlank(userId)) {
            LoginUserInfo userInfo = new LoginUserInfo();
            userInfo.setUserId(Long.valueOf(userId));
            userInfo.setUsername(username);
            UserContext.set(userInfo);
        }
        return true;

    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request,
                                @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) {

        UserContext.remove();

    }
}