package com.example.demo.handler;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务逻辑校验处理器 - 使用策略模式验证用户创建的业务规则
 */
@Component
public class BusinessValidationHandler implements UserCreateHandler {

    private final Map<UserType, BusinessValidationStrategy> strategyMap;

    public BusinessValidationHandler(List<BusinessValidationStrategy> strategies) {
        this.strategyMap = new HashMap<>();
        for (BusinessValidationStrategy strategy : strategies) {
            strategyMap.put(strategy.getBusinessKey(), strategy);
        }
    }

    @Override
    public void handle(UserCreateContext context) {
        UserType userType = context.getUserType();
        BusinessValidationStrategy strategy = strategyMap.get(userType);
        if (strategy == null) {
            throw new IllegalStateException("不支持的业务策略：" + userType.getBusinessKey());
        }
        strategy.validate(context);
    }
}
