package com.dhx.bi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@MapperScan("com.dhx.bi.mapper")
@EnableScheduling
@EnableWebMvc
public class BIApplication {

    public static void main(String[] args) {
        SpringApplication.run(BIApplication.class, args);
    }
}
