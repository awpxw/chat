// common/src/main/java/com/aw/common/config/MinioProperties.java
package com.aw.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String endpoint = "http://127.0.0.1:9000";
    private String accessKey = "minioadmin";
    private String secretKey = "minioadmin";
    private String bucketName = "mall";
    private String domain; // 可选：对外访问域名，如 https://img.xxx.com，不填默认用 endpoint
}