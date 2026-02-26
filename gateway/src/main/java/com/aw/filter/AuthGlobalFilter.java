package com.aw.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aw.exception.Result;
import com.aw.jwt.JwtUtil;
import com.aw.service.TokenBlacklistService;
import jakarta.annotation.Resource;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Component
public class AuthGlobalFilter implements WebFilter, Ordered {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private TokenBlacklistService tokenBlacklistService;

    private static final Set<String> WHITE_LIST = Set.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/auth/captcha",
            "/api/auth/captcha/verify",
            "/v3/api-docs");

    private String getToken(ServerHttpRequest request) {
        List<String> headers = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (headers != null && !headers.isEmpty()) {
            String auth = headers.get(0);
            if (StrUtil.isNotBlank(auth) && auth.startsWith("Bearer ")) {
                return auth.substring(7);
            }
        }
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
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Result<String> result = Result.fail(401, msg);
        DataBuffer buffer = response.bufferFactory()
                .wrap(JSONUtil.toJsonStr(result).getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer)).then(Mono.empty());
    }

    @Override
    public int getOrder() {
        return -1000000000;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (request.getMethod() == org.springframework.http.HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }
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
        boolean isActive = jwtUtil.validateToken(token);
        if (!isActive) {
            return unAuth(exchange, "无效的 Token");
        }

        // 4. 黑名单校验（关键！）
        if (tokenBlacklistService.isBlacklisted(token)) {
            return unAuth(exchange, "Token 已失效，请重新登录");
        }

        // 5. 校验通过 → 把用户信息塞到 header
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);

        ServerHttpRequest newRequest = request.mutate()
                .header("x-user-id", String.valueOf(userId))
                .header("x-username", StringUtils.hasText(username) ? username : "")
                .build();

        // 关键！改成这行，100% 转发成功！
        return chain.filter(exchange.mutate().request(newRequest).build());
    }

}