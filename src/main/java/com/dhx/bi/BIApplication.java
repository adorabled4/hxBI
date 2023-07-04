package com.dhx.bi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.dhx.bi.mapper")
@EnableScheduling
public class BIApplication {

    public static void main(String[] args) {
        SpringApplication.run(BIApplication.class, args);
    }
}
