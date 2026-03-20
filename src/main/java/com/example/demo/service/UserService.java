package com.example.demo.service;

import com.example.demo.dao.UserDao;
import com.example.demo.dos.UserDO;
import com.example.demo.handler.create.BusinessValidationHandler;
import com.example.demo.handler.create.ParameterValidationHandler;
import com.example.demo.context.UserCreateContext;
import com.example.demo.handler.create.UserPersistenceHandler;
import com.example.demo.handler.auth.AuthChainExecutor;
import com.example.demo.handler.auth.context.LoginContext;
import com.example.demo.handler.auth.context.RegisterContext;
import com.example.demo.handler.auth.context.SendCodeContext;
import com.example.demo.handler.auth.login.LoginBizHandler;
import com.example.demo.handler.auth.login.LoginValidationHandler;
import com.example.demo.handler.auth.register.RegisterBizHandler;
import com.example.demo.handler.auth.register.RegisterProcessHandler;
import com.example.demo.handler.auth.register.RegisterValidationHandler;
import com.example.demo.handler.auth.sendcode.SendCodeBizHandler;
import com.example.demo.handler.auth.sendcode.SendCodePersistenceHandler;
import com.example.demo.handler.auth.sendcode.SendCodeValidationHandler;
import com.example.demo.handler.auth.shared.EmailLockHandler;
import com.example.demo.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务层
 */
@Service
public class UserService {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private ParameterValidationHandler parameterValidationHandler;

    @Autowired
    private BusinessValidationHandler businessValidationHandler;

    @Autowired
    private UserPersistenceHandler userPersistenceHandler;

    @Autowired
    private AuthChainExecutor authChainExecutor;
    
    @Autowired
    private EmailLockHandler emailLockHandler;

    @Autowired
    private SendCodeValidationHandler sendCodeValidationHandler;
    
    @Autowired
    private SendCodeBizHandler sendCodeBizHandler;
    
    @Autowired
    private SendCodePersistenceHandler sendCodePersistenceHandler;

    @Autowired
    private RegisterValidationHandler registerValidationHandler;
    
    @Autowired
    private RegisterBizHandler registerBizHandler;
    
    @Autowired
    private RegisterProcessHandler registerProcessHandler;

    @Autowired
    private LoginValidationHandler loginValidationHandler;
    
    @Autowired
    private LoginBizHandler loginBizHandler;

    /**
     * 发送邮箱验证码 - Service 仅做流程编排，具体逻辑在责任链与 Biz 层
     */
    public String sendRegisterCode(String email) {
        SendCodeContext context = new SendCodeContext();
        context.setEmail(email);

        authChainExecutor.execute(context, List.of(
                emailLockHandler,
                sendCodeValidationHandler,
                sendCodeBizHandler,
                sendCodePersistenceHandler
        ));

        return context.getMockCode();
    }

    /**
     * 邮箱验证码注册 - Service 仅做流程编排，具体逻辑在责任链与 Biz 层
     */
    public UserDO registerByEmail(String username, String email, String password, String verificationCode) {
        RegisterContext context = new RegisterContext();
        context.setUsername(username);
        context.setEmail(email);
        context.setPassword(password);
        context.setVerificationCode(verificationCode);

        authChainExecutor.execute(context, List.of(
                // [锁] 基于邮箱的并发锁保护（后期可优化为基于Redisson的分布式锁）
                emailLockHandler,
                registerValidationHandler,
                registerBizHandler,
                // [事务] 数据持久化入库
                registerProcessHandler
        ));

        return context.getResultUser();
    }

    /**
     * 邮箱密码登录 - Service 仅做流程编排，具体逻辑在责任链与 Biz 层
     */
    public UserDO loginByEmail(String email, String password) {
        LoginContext context = new LoginContext();
        context.setEmail(email);
        context.setPassword(password);

        authChainExecutor.execute(context, List.of(
                emailLockHandler,
                loginValidationHandler,
                loginBizHandler
        ));

        return context.getResultUser();
    }

    /**
     * 创建用户 - 使用责任链处理器
     */
    public UserDO createUser(String username, String email, Integer age) {
        UserCreateContext context = new UserCreateContext(username, email, age);
        authChainExecutor.execute(context, List.<com.example.demo.handler.auth.AuthHandler<? super UserCreateContext>>of(
                parameterValidationHandler,
                businessValidationHandler,
                userPersistenceHandler
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
