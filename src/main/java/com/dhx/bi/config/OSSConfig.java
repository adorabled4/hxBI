package com.dhx.bi.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dhx_
 * @className OSSConfig
 * @date : 2023/02/02/ 20:45
 **/
@Configuration
public class OSSConfig {
    @Value("${alibaba.cloud.access-key}")
    String accessKey;

    @Value("${alibaba.cloud.secret-key}")
    String secretKey;

    @Value("${alibaba.cloud.oss.endpoint}")
    String endpoint;

    @Value("${alibaba.cloud.oss.bucket}")
    String bucket;

    @Value("${alibaba.cloud.oss.domain}")
    String domain;


    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(endpoint, accessKey, secretKey);
    }

    public String getBucket() {
        return bucket;
    }


    public String getUrlPrefix() {
        return domain;
    }
}
