package com.dhx.bi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author adorabled4
 * @className XfXhConfig
 * @date : 2023/11/17/ 14:21
 **/
@Configuration
@ConfigurationProperties(prefix = "spark")
@Data
public class SparkConfig {

    private String appId;

    private String apiKey;

    private String apiSecret;

    private String host;

    private String path;

    private String domain;


}
