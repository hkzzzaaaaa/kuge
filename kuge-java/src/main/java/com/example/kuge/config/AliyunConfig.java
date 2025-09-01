package com.example.kuge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyunoss")
public class AliyunConfig {
    private String endpoint;
    private String accesskey;
    private String accessid;
    private String bucketName;

}
