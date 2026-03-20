package com.example.demo.constant;

/**
 * 用户认证模块常量定义
 */
public final class UserAuthConstant {
    /**
     * 注册验证码有效时长（毫秒）
     */
    public static final long REGISTER_CODE_TTL_MILLIS = 5 * 60 * 1000L;

    /**
     * 验证码最大随机值（不含上界）
     */
    public static final int VERIFICATION_CODE_BOUND = 1_000_000;

    /**
     * 六位数字验证码格式
     */
    public static final String VERIFICATION_CODE_FORMAT = "%06d";

    /**
     * 邮箱格式正则
     */
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private UserAuthConstant() {
    }
}
