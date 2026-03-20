package com.example.demo.handler.create;

import com.example.demo.context.UserCreateContext;
import com.example.demo.enums.UserType;
import com.example.demo.handler.strategy.BusinessValidationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 业务逻辑校验处理器 - 使用策略模式验证用户创建的业务规则
 */
@Component
public class BusinessValidationHandler implements UserCreateHandler {

    private final Map<UserType, BusinessValidationStrategy> strategyMap;

    @Autowired
    public BusinessValidationHandler(List<BusinessValidationStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(BusinessValidationStrategy::getBusinessKey, strategy -> strategy));
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
