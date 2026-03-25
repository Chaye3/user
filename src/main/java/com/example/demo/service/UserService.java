package com.example.demo.service;

import com.example.demo.biz.UserAuthBiz;
import com.example.demo.constant.LockConstant;
import com.example.demo.context.UserCreateContext;
import com.example.demo.dao.UserDao;
import com.example.demo.dos.UserDO;
import com.example.demo.enums.UserType;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.handler.auth.AuthChainExecutor;
import com.example.demo.handler.auth.context.LoginContext;
import com.example.demo.handler.auth.context.RegisterContext;
import com.example.demo.handler.auth.context.SendCodeContext;
import com.example.demo.handler.auth.login.LoginBizHandler;
import com.example.demo.handler.auth.login.LoginValidationHandler;
import com.example.demo.handler.auth.register.RegisterProcessHandler;
import com.example.demo.handler.create.BusinessValidationHandler;
import com.example.demo.handler.create.ParameterValidationHandler;
import com.example.demo.handler.create.UserProcessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务层
 */
@Service
// @RequiredArgsConstructor 是 Lombok 提供的注解，结合final关键字，用于自动生成构造函数
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final ParameterValidationHandler parameterValidationHandler;
    private final BusinessValidationHandler businessValidationHandler;
    private final UserProcessHandler userProcessHandler;
    private final AuthChainExecutor authChainExecutor;
    private final UserAuthBiz userAuthBiz;
    private final RegisterProcessHandler registerProcessHandler;
    private final LoginValidationHandler loginValidationHandler;
    private final LoginBizHandler loginBizHandler;

    /**
     * 发送邮箱验证码 - 函数式责任链，使用方法引用代替 Handler 类
     */
    public String sendRegisterCode(String email) {
        SendCodeContext context = new SendCodeContext(email);
        authChainExecutor.executeWithLock(context, LockConstant.SEND_CODE_EMAIL + email, List.of(
                userAuthBiz::validateEmail,
                userAuthBiz::generateAndSetCode,
                userAuthBiz::mockSendRegisterMail,
                userAuthBiz::saveRegisterCode
        ));
        return context.getMockCode();
    }

    /**
     * 邮箱验证码注册 - 函数式责任链 + Handler 混合模式
     */
    public UserDO registerByEmail(String username, String email, String password, String verificationCode) {
        RegisterContext context = new RegisterContext(username, email, password, verificationCode);
        authChainExecutor.executeWithLock(context, LockConstant.REGISTER_EMAIL + email, List.of(
                userAuthBiz::validateRegisterParam,
                userAuthBiz::processRegisterBiz,
                registerProcessHandler
        ));
        return context.getResultUser();
    }

    /**
     * 邮箱密码登录 - 函数式责任链
     */
    public UserDO loginByEmail(String email, String password) {
        LoginContext context = new LoginContext(email, password);
        authChainExecutor.executeWithLock(context, LockConstant.LOGIN_EMAIL + email, List.of(
                loginValidationHandler,
                loginBizHandler
        ));
        return context.getResultUser();
    }

    /**
     * 后台管理手动创建用户 - 函数式责任链
     */
    public UserDO createAdminUser(String username, String email, Integer age) {
        UserCreateContext context = new UserCreateContext(username, email, age, UserType.PRIMARY);
        authChainExecutor.execute(context, List.of(
                parameterValidationHandler,
                businessValidationHandler,
                userProcessHandler
        ));
        return context.getResultUser();
    }

    /**
     * 根据ID查询用户
     */
    public UserDO getUserById(Long id) {
        return Optional.ofNullable(userDao.findById(id))
                .orElseThrow(() -> new UserNotFoundException("用户未找到，ID：" + id));
    }

    /**
     * 查询所有用户
     */
    public List<UserDO> getAllUsers() {
        return userDao.findAll();
    }

    /**
     * 更新用户
     */
    public UserDO updateUser(Long id, String username, String email, Integer age) {
        UserDO user = userDao.findById(id);
        if (user == null) {
            throw new UserNotFoundException("用户不存在，ID：" + id);
        }
        user.setUsername(username);
        user.setEmail(email);
        user.setAge(age);
        return userDao.update(user);
    }

    /**
     * 删除用户
     */
    public boolean deleteUser(Long id) {
        int affectedRows = userDao.deleteById(id);
        if (affectedRows == 0) {
            throw new UserNotFoundException("用户不存在，ID：" + id);
        }
        return true;
    }

    /**
     * 根据用户名搜索用户
     */
    public List<UserDO> searchUsers(String username) {
        return userDao.findByUsername(username);
    }

    /**
     * 获取用户总数
     */
    public long getUserCount() {
        return userDao.count();
    }

    /**
     * 批量创建用户
     */
    public List<UserDO> createUsers(List<UserDO> users) {
        return userDao.saveAll(users);
    }

    /**
     * 删除所有用户
     */
    public void deleteAllUsers() {
        userDao.deleteAll();
    }

}