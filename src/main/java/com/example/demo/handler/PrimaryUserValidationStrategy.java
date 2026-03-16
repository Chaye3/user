package com.example.demo.handler;

import com.example.demo.dao.JsonFileUserDao;
import org.springframework.stereotype.Component;

/**
 * 主用户业务校验策略
 * 主用户拥有完整权限，校验规则较宽松
 */
@Component
public class PrimaryUserValidationStrategy implements BusinessValidationStrategy {

    private final JsonFileUserDao userDao;

    public PrimaryUserValidationStrategy(JsonFileUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void validate(UserCreateContext context) {
        validateEmailUnique(context.getEmail());
        validateUserCountLimit();
    }

    @Override
    public UserType getBusinessKey() {
        return UserType.PRIMARY;
    }

    /**
     * 验证邮箱唯一性
     */
    private void validateEmailUnique(String email) {
        userDao.reload();
        if (userDao.findByEmail(email) != null) {
            throw new IllegalStateException("邮箱已被注册：" + email);
        }
    }

    /**
     * 验证用户数量限制
     */
    private void validateUserCountLimit() {
        userDao.reload();
        long userCount = userDao.count();
        if (userCount >= 1000) {
            throw new IllegalStateException("用户数量已达上限，暂时无法注册新用户");
        }
    }
}
