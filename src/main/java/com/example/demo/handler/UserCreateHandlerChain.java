package com.example.demo.handler;

import com.example.demo.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户创建处理器链 - 责任链模式的编排者
 * 使用 List 管理处理器，按顺序执行
 */
@Component
public class UserCreateHandlerChain {

    private final List<UserCreateHandler> handlers;

    public UserCreateHandlerChain(
            ParameterValidationHandler parameterValidationHandler,
            BusinessValidationHandler businessValidationHandler,
            UserPersistenceHandler userPersistenceHandler) {
        this.handlers = new ArrayList<>();
        this.handlers.add(parameterValidationHandler);
        this.handlers.add(businessValidationHandler);
        this.handlers.add(userPersistenceHandler);
    }

    /**
     * 执行用户创建流程
     *
     * @param username 用户名
     * @param email    邮箱
     * @param age      年龄
     * @return 创建成功的用户
     */
    public User execute(String username, String email, Integer age) {
        UserCreateContext context = new UserCreateContext(username, email, age);

        // 按顺序执行所有处理器
        for (UserCreateHandler handler : handlers) {
            handler.handle(context);
        }

        return context.getResultUser();
    }

    /**
     * 获取处理器列表（用于调试和日志）
     */
    public List<UserCreateHandler> getHandlers() {
        return new ArrayList<>(handlers);
    }
}
