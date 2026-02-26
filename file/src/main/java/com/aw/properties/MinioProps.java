// common/src/main/java/com/aw/common/config/MinioProperties.java
package com.aw.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProps {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;

    private String domain; // 可选：对外访问域名，如 https://img.xxx.com，不填默认用 endpoint

}