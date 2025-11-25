package com.aw.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性（所有项目统一使用这个类）
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret = "aw";
    private long accessExpire = 7200L;     // 秒
    private long refreshExpire = 604800L;  // 秒

    // getter and setter
    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public long getAccessExpire() { return accessExpire; }
    public void setAccessExpire(long accessExpire) { this.accessExpire = accessExpire; }

    public long getRefreshExpire() { return refreshExpire; }
    public void setRefreshExpire(long refreshExpire) { this.refreshExpire = refreshExpire; }
}