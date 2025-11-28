package com.aw.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性（所有项目统一使用这个类）
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGH";
    private long accessExpire = 7200L;     // 秒
    private long refreshExpire = 604800L;  // 秒

}