package com.example.demo.biz;

import com.example.demo.constant.UserAuthConstant;
import com.example.demo.dao.UserDao;
import com.example.demo.dos.UserDO;
import com.example.demo.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
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
    private final ConcurrentHashMap<String, ReentrantLock> emailLockStore = new ConcurrentHashMap<>();

    /**
     * 按邮箱获取业务锁，用于注册相关流程的串行化和幂等控制，带超时机制
     */
    public ReentrantLock acquireEmailLock(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        ReentrantLock lock = emailLockStore.computeIfAbsent(email, key -> new ReentrantLock());
        try {
            // 尝试获取锁，最多等待6秒，避免死锁
            boolean locked = lock.tryLock(6, TimeUnit.SECONDS);
            if (!locked) {
                throw new IllegalStateException("系统繁忙，请稍后再试");
            }
            return lock;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("获取锁被中断，请稍后再试", e);
        }
    }

    /**
     * 释放业务锁
     */
    public void releaseEmailLock(ReentrantLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

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
     * 模拟发送邮件
     */
    public void mockSendRegisterMail(String email, String code) {
        if (email == null || code == null) {
            throw new IllegalArgumentException("发送邮件参数不能为空");
        }
    }

    /**
     * 持久化验证码到内存缓存
     */
    public void saveRegisterCode(String email, String code, long expireAt) {
        registerCodeStore.put(email, new RegisterCodeInfo(code, expireAt));
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
