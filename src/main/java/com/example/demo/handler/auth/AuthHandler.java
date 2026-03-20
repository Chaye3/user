package com.example.demo.handler.auth;

import com.example.demo.handler.auth.context.BaseContext;

/**
 * 泛型化认证处理器接口
 */
public interface AuthHandler<T extends BaseContext> {
    void handle(T context);
}
