package com.example.demo.dto.rsp;

import lombok.Data;

@Data
public class UserRspDTO {
    private Long id;
    private String username;
    private String email;
    private Integer age;
}
