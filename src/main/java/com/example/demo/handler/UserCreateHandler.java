package com.example.demo.handler;

/**
 * 用户创建处理器接口
 * 责任链模式的接口定义
 */
public interface UserCreateHandler {

    /**
     * 处理用户创建请求
     *
     * @param context 用户创建上下文
     * @throws IllegalArgumentException 参数校验失败
     * @throws IllegalStateException    业务逻辑校验失败
     */
    void handle(UserCreateContext context);

}
