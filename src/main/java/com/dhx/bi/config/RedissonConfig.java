package com.dhx.bi.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author adorabled4
 * @className RedissonConfgi
 * @date : 2023/08/14/ 19:10
 **/
@Configuration
public class RedissonConfig {

    @Bean
    RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.159.134:6379")
                .setPassword("adorabled4")
                .setDatabase(1);
        return Redisson.create(config);
    }

}
