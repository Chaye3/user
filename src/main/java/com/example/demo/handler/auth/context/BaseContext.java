package com.example.demo.handler.auth.context;

import lombok.Data;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 认证基础上下文
 */
@Data
public abstract class BaseContext {
    private String email;
    private ReentrantLock lock;

}
