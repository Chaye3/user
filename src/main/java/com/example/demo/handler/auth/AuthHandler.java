package com.example.demo.handler.auth;

import com.example.demo.handler.auth.context.BaseContext;

import java.util.function.Consumer;

/**
 * 泛型化认证处理器接口，继承 Consumer 支持方法引用
 */
public interface AuthHandler<T extends BaseContext> extends Consumer<T> {

    @Override
    default void accept(T context) {
        handle(context);
    }

    /**
     * 处理上下文
     */
    void handle(T context);
}
