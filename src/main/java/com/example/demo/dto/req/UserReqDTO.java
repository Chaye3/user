package com.example.demo.dto.req;

import lombok.Data;

@Data
public class UserReqDTO {
    private Long id;
    private String username;
    private String email;
    private Integer age;
}