package com.example.demo.constant;

/**
 * 分布式锁常量
 */
public final class LockConstant {

    private LockConstant() {
    }

    /**
     * 锁key前缀
     */
    private static final String PREFIX = "user:lock:";

    /**
     * 邮箱注册锁
     */
    public static final String REGISTER_EMAIL = PREFIX + "register:email:";

    /**
     * 邮箱登录锁
     */
    public static final String LOGIN_EMAIL = PREFIX + "login:email:";

    /**
     * 发送验证码锁
     */
    public static final String SEND_CODE_EMAIL = PREFIX + "sendcode:email:";
}
