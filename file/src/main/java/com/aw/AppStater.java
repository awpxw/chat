package com.aw;


import com.aw.properties.MinioProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MinioProps.class)
public class AppStater
{
    public static void main(String[] args) {
        SpringApplication.run(AppStater.class, args);
    }
}
