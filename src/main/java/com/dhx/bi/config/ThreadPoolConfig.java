package com.dhx.bi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author adorabled4
 * @className ThreadPoolConfig
 * @date : 2023/08/15/ 22:24
 **/
@Configuration
public class ThreadPoolConfig {

    @Bean
    ThreadPoolExecutor threadPoolExecutor() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private int count = 1;

            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread(r, "thread-" + count++);
            }
        };

        return new ThreadPoolExecutor(2, 4, 1000, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(4),
                threadFactory
        );
    }

}
