package com.example.demo.entity;

import lombok.Data;

@Data
public class UserRspDTO {
    private Long id;
    private String username;
    private String email;
    private Integer age;
}
