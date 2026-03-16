package com.example.demo.handler;

import java.util.Arrays;

/**
 * 用户类别枚举
 */
public enum UserType {
    /**
     * 主用户 - 拥有完整权限
     */
    PRIMARY("primary_user_validation"),

    /**
     * 从用户 - 权限受限
     */
    SECONDARY("secondary_user_validation");

    private final String businessKey;

    UserType(String businessKey) {
        this.businessKey = businessKey;
    }

    /**
     * 获取业务标识
     *
     * @return 业务标识
     */
    public String getBusinessKey() {
        return businessKey;
    }

    /**
     * 根据业务标识查找用户类型
     *
     * @param businessKey 业务标识
     * @return 用户类型
     * @throws IllegalArgumentException 如果找不到对应的用户类型
     */
    public static UserType fromBusinessKey(String businessKey) {
        return Arrays.stream(values())
                .filter(type -> type.businessKey.equals(businessKey))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知的业务标识：" + businessKey));
    }
}
