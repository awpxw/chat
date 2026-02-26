package com.aw.login;

import com.aw.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LoginRequiredAspect {

    @Around("@annotation(loginRequired)")
    public Object around(ProceedingJoinPoint joinPoint, LoginRequired loginRequired) throws Throwable {

        LoginUserInfo loginUser = UserContext.get();
        if (loginUser == null || loginUser.getUserId() == null) {
            if (loginRequired.value()) {
                throw new BizException("请先登录");
            }
        }
        return joinPoint.proceed();

    }
}