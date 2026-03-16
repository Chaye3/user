package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户中心 Spring Boot 启动类
 */
@SpringBootApplication
public class UserCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserCenterApplication.class, args);
        System.out.println("========================================");
        System.out.println("用户中心服务已启动！");
        System.out.println("服务地址: http://localhost:8080");
        System.out.println("========================================");
    }
}
