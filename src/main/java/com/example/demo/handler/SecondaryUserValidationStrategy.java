package com.example.demo.handler;

import com.example.demo.dao.UserDao;
import org.springframework.stereotype.Component;

/**
 * 从用户业务校验策略
 * 从用户权限受限，校验规则更严格
 */
@Component
public class SecondaryUserValidationStrategy implements BusinessValidationStrategy {

    private final UserDao userDao;

    public SecondaryUserValidationStrategy(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void validate(UserCreateContext context) {
        validateEmailUnique(context.getEmail());
        validateUserCountLimit();
        validateEmailDomain(context.getEmail());
        validateAgeRequirement(context.getAge());
    }

    @Override
    public UserType getBusinessKey() {
        return UserType.SECONDARY;
    }

    /**
     * 验证邮箱唯一性
     */
    private void validateEmailUnique(String email) {
        if (userDao.findByEmail(email) != null) {
            throw new IllegalStateException("邮箱已被注册：" + email);
        }
    }

    /**
     * 验证用户数量限制 - 从用户限制更严
     */
    private void validateUserCountLimit() {
        long userCount = userDao.count();
        if (userCount >= 500) {
            throw new IllegalStateException("从用户数量已达上限，暂时无法注册");
        }
    }

    /**
     * 验证邮箱域名 - 从用户只能使用特定域名
     */
    private void validateEmailDomain(String email) {
        String domain = email.substring(email.lastIndexOf("@"));
        if (!domain.endsWith("@company.com")) {
            throw new IllegalStateException("从用户只能使用公司邮箱注册");
        }
    }

    /**
     * 验证年龄要求 - 从用户必须年满18岁
     */
    private void validateAgeRequirement(Integer age) {
        if (age < 18) {
            throw new IllegalStateException("从用户必须年满18岁");
        }
    }
}
