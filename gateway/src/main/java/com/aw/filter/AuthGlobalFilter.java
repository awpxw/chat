package com.aw.filter;

import com.aw.jwt.JwtUtil;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpCookie;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.Set;


@Component
public class AuthGlobalFilter implements WebFilter, Ordered {

    @Resource
    private JwtUtil jwtUtil;   // 直接注入你 common 里的工具类

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

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 白名单直接放行
        if (WHITE_LIST.stream().anyMatch(path::contains)) {
            return chain.filter(exchange);
        }

        // 2. 获取 token
        String token = getToken(request);
        if (token == null || token.isBlank()) {
            return unAuth(exchange, "缺失AccessToken");
        }

        // 3. 校验 token（直接用你 common 里的方法）
        if (!jwtUtil.validateToken(token)) {
            return unAuth(exchange, "无效或已过期Token");
        }

        // 4. 校验通过 → 把用户信息塞到 header 传给下游微服务
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);

        ServerHttpRequest newRequest = request.mutate()
                .header("x-user-id", String.valueOf(userId))
                .header("x-username", username == null ? "" : username)
                .build();

        return chain.filter(exchange.mutate().request(newRequest).build());
    }
}