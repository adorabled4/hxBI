package com.dhx.bi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dhx.bi.mapper")
public class BIApplication {

    public static void main(String[] args) {
        SpringApplication.run(BIApplication.class, args);
    }
}
