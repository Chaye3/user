package com.example.demo.biz;

import com.example.demo.constant.UserAuthConstant;
import com.example.demo.dao.UserDao;
import com.example.demo.dos.UserDO;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.handler.auth.context.RegisterContext;
import com.example.demo.handler.auth.context.SendCodeContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * 用户认证 Biz 层，负责认证相关的业务计算和业务规则校验
 */
@Component
public class UserAuthBiz {

    @Autowired
    private UserDao userDao;

    private final Pattern emailPattern = Pattern.compile(UserAuthConstant.EMAIL_PATTERN);
    private final ConcurrentHashMap<String, RegisterCodeInfo> registerCodeStore = new ConcurrentHashMap<>();

    /**
     * 校验邮箱格式
     */
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        if (!emailPattern.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
    }

    /**
     * 校验邮箱格式（基于上下文）
     */
    public void validateEmail(SendCodeContext context) {
        validateEmail(context.getEmail());
    }

    /**
     * 校验注册参数
     */
    public void validateRegisterParam(String username, String email, String password, String verificationCode) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        validateEmail(email);
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            throw new IllegalArgumentException("验证码不能为空");
        }
    }

    /**
     * 校验注册参数（基于上下文）
     */
    public void validateRegisterParam(RegisterContext context) {
        validateRegisterParam(context.getUsername(), context.getEmail(), context.getPassword(), context.getVerificationCode());
    }

    /**
     * 处理注册业务逻辑：验证码校验、邮箱唯一性、构建用户对象
     */
    public void processRegisterBiz(RegisterContext context) {
        validateRegisterCode(context.getEmail(), context.getVerificationCode(), System.currentTimeMillis());
        validateEmailNotRegistered(context.getEmail());
        UserDO pendingUser = buildRegisterUser(context.getUsername(), context.getEmail(), context.getPassword());
        context.setPendingUser(pendingUser);
    }

    /**
     * 校验登录参数
     */
    public void validateLoginParam(String email, String password) {
        validateEmail(email);
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
    }

    /**
     * 生成6位数字验证码
     */
    public String generateVerificationCode() {
        int value = ThreadLocalRandom.current().nextInt(UserAuthConstant.VERIFICATION_CODE_BOUND);
        return String.format(UserAuthConstant.VERIFICATION_CODE_FORMAT, value);
    }

    /**
     * 生成验证码并设置到上下文（包含过期时间）
     */
    public void generateAndSetCode(SendCodeContext context) {
        String code = generateVerificationCode();
        context.setMockCode(code);
        context.setCodeExpireAt(System.currentTimeMillis() + UserAuthConstant.REGISTER_CODE_TTL_MILLIS);
    }

    /**
     * 模拟发送邮件
     */
    public void mockSendRegisterMail(String email, String code) {
        if (email == null || code == null) {
            throw new IllegalArgumentException("发送邮件参数不能为空");
        }
    }

    /**
     * 模拟发送邮件（基于上下文）
     */
    public void mockSendRegisterMail(SendCodeContext context) {
        mockSendRegisterMail(context.getEmail(), context.getMockCode());
    }

    /**
     * 持久化验证码到内存缓存
     */
    public void saveRegisterCode(String email, String code, long expireAt) {
        registerCodeStore.put(email, new RegisterCodeInfo(code, expireAt));
    }

    /**
     * 持久化验证码到内存缓存（基于上下文）
     */
    public void saveRegisterCode(SendCodeContext context) {
        saveRegisterCode(context.getEmail(), context.getMockCode(), context.getCodeExpireAt());
    }

    /**
     * 校验验证码合法性
     */
    public void validateRegisterCode(String email, String inputCode, long currentTimeMillis) {
        RegisterCodeInfo registerCodeInfo = registerCodeStore.get(email);
        if (registerCodeInfo == null || registerCodeInfo.expireAt < currentTimeMillis) {
            throw new IllegalArgumentException("验证码已失效，请重新获取");
        }
        if (!Objects.equals(registerCodeInfo.code, inputCode)) {
            throw new IllegalArgumentException("验证码错误");
        }
    }

    /**
     * 校验邮箱是否已注册
     */
    public void validateEmailNotRegistered(String email) {
        UserDO existUser = userDao.findByEmail(email);
        if (existUser != null) {
            throw new UserAlreadyExistsException("该邮箱已注册");
        }
    }

    /**
     * 构建待注册用户
     */
    public UserDO buildRegisterUser(String username, String email, String password) {
        UserDO user = new UserDO();
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setPassword(password);
        return user;
    }

    /**
     * 获取已注册用户
     */
    public UserDO findUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    /**
     * 校验密码是否匹配
     */
    public void validatePassword(UserDO user, String password) {
        if (user == null || !Objects.equals(user.getPassword(), password)) {
            throw new IllegalArgumentException("邮箱或密码错误");
        }
    }

    /**
     * 消费验证码，防止重复使用
     */
    public void consumeRegisterCode(String email) {
        registerCodeStore.remove(email);
    }

    /**
     * 注册验证码缓存对象
     */
    private static class RegisterCodeInfo {
        private final String code;
        private final long expireAt;

        private RegisterCodeInfo(String code, long expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }
    }
}
