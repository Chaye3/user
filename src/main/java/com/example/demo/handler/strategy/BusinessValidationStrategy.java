package com.example.demo.handler.strategy;

import com.example.demo.context.UserCreateContext;
import com.example.demo.enums.UserType;

/**
 * 业务校验策略接口
 * 不同用户类别有不同的业务校验规则
 */
public interface BusinessValidationStrategy {


    void validate(UserCreateContext context);


    /**
     * 获取业务标识
     *
     * @return 业务标识
     */
    UserType getBusinessKey();
}
