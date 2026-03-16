package com.example.demo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * User实体类
 */
@Data
public class User {
    private Long id;
    private String username;
    private String email;
    private Integer age;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public User() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    public User(String username, String email, Integer age) {
        this();
        this.username = username;
        this.email = email;
        this.age = age;
    }


    /**
     * 静态建造者类
     */
    public static class Builder {
        private String username;
        private String email;
        private Integer age;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder age(Integer age) {
            this.age = age;
            return this;
        }

        public User build() {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("用户名不能为空");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("邮箱不能为空");
            }
            if (age == null) {
                throw new IllegalArgumentException("年龄不能为空");
            }

            return new User(username, email, age);
        }
    }

    /**
     * 创建建造者
     */
    public static Builder builder() {
        return new Builder();
    }
}
