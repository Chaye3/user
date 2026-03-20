package com.example.demo.handler.create;

import com.example.demo.context.UserCreateContext;
import com.example.demo.handler.auth.AuthHandler;

/**
 * 用户创建处理器接口
 * 责任链模式的接口定义 (已迁移到统一的 AuthHandler)
 */
public interface UserCreateHandler extends AuthHandler<UserCreateContext> {

    /**
     * 处理用户创建请求
     *
     * @param context 用户创建上下文
     * @throws IllegalArgumentException 参数校验失败
     * @throws IllegalStateException    业务逻辑校验失败
     */
    @Override
    void handle(UserCreateContext context);

}
