package com.aw.filter;

import com.aw.jwt.JwtUtil;
import com.aw.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;


@Component
public class AuthGlobalFilter implements WebFilter, Ordered {

    @Resource
    private JwtUtil jwtUtil;   // 直接注入你 common 里的工具类

    @Resource
    private TokenBlacklistService tokenBlacklistService;

    // 白名单路径（不用登录）
    private static final Set<String> WHITE_LIST = Set.of(
            "/user/login", "/user/register", "/user/captcha",
            "/auth/refresh", "/v3/api-docs", "/swagger", "/webjars"
    );

    private String getToken(ServerHttpRequest request) {
        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        // 支持 cookie 方式（如果前端放 cookie）
        return request.getCookies()
                .getOrDefault("access_token", Collections.emptyList())
                .stream()
                .findFirst()
                .map(HttpCookie::getValue)
                .orElse(null);
    }

    private Mono<Void> unAuth(ServerWebExchange exchange, String msg) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set("Content-Type", "application/json;charset=UTF-8");
        String json = "{\"code\":401,\"message\":\"" + msg + "\"}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(json.getBytes())));
    }

    @Override
    public int getOrder() {
        return -100; // 比跨域过滤器先执行
    }

    @NotNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value(); // 注意：getPath().value() 才是字符串

        // 1. 白名单直接放行
        if (WHITE_LIST.stream().anyMatch(path::startsWith)) { // 改成 startsWith 更准！
            return chain.filter(exchange);
        }

        // 2. 获取 token
        String token = getToken(request);
        if (Objects.isNull(token) || Objects.equals(token, "")) {
            return unAuth(exchange, "缺失 AccessToken");
        }

        // 3. 解析 + 校验 token（只解析一次！避免重复解析）
        try {
            jwtUtil.validateToken(token); // 推荐你工具类加个 parseToken 返回 Claims
        } catch (JwtException e) {
            return unAuth(exchange, "无效的 Token");
        }

        // 4. 黑名单校验（关键！）
        if (tokenBlacklistService.isBlacklisted(token)) {
            return unAuth(exchange, "Token 已失效，请重新登录");
        }

        // 5. 校验通过 → 把用户信息塞到 header
        Claims claims = jwtUtil.parseToken(token);
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        String role = claims.get("role", String.class);

        ServerHttpRequest newRequest = exchange.getRequest().mutate()
                .header("x-user-id", String.valueOf(userId))
                .header("x-username", StringUtils.hasText(username) ? username : "")
                .header("x-role", StringUtils.hasText(role) ? role : "")
                .build();

        return chain.filter(exchange.mutate().request(newRequest).build());
    }
}