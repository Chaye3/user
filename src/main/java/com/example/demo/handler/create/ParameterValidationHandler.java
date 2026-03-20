package com.example.demo.handler.create;

import com.example.demo.context.UserCreateContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 参数校验处理器 - 验证用户创建的参数
 */
@Component
public class ParameterValidationHandler implements UserCreateHandler {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Override
    public void handle(UserCreateContext context) {
        validateUsername(context.getUsername());
        validateEmail(context.getEmail());
        validateAge(context.getAge());
    }

    /**
     * 验证用户名
     */
    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (username.length() < 2) {
            throw new IllegalArgumentException("用户名长度不能少于2个字符");
        }
        if (username.length() > 50) {
            throw new IllegalArgumentException("用户名长度不能超过50个字符");
        }
        if (!username.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("用户名只能包含中文、字母、数字和下划线");
        }
    }

    /**
     * 验证邮箱
     */
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("邮箱格式不正确：" + email);
        }
        if (email.length() > 100) {
            throw new IllegalArgumentException("邮箱长度不能超过100个字符");
        }
    }

    /**
     * 验证年龄
     */
    private void validateAge(Integer age) {
        if (age == null) {
            throw new IllegalArgumentException("年龄不能为空");
        }
        if (age < 0) {
            throw new IllegalArgumentException("年龄不能为负数");
        }
        if (age > 150) {
            throw new IllegalArgumentException("年龄不能超过150岁");
        }
    }
}
