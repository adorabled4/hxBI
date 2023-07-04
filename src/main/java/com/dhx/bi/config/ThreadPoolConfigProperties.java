package com.dhx.bi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author adorabled4
 * @className ThreadPoolConfigProperties
 * @date : 2023/05/04/ 17:00
 **/
@Data
@Component
@ConfigurationProperties(prefix = "template.thread")
public class ThreadPoolConfigProperties {

    private Integer coreSize;

    private Integer maxSize;

    private Integer keepAliveTime;
}
