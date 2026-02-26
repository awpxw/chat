
package com.aw.config;

import com.aw.properties.MinioProps;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class MinioConfig {

    @Resource
    private MinioProps props;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(props.getEndpoint())
            .credentials(props.getAccessKey(), props.getSecretKey())
            .build();
    }

}