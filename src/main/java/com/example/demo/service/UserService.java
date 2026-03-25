package com.example.demo.service;

import com.example.demo.biz.UserAuthBiz;
import com.example.demo.constant.UserAuthConstant;
import com.example.demo.constant.LockConstant;
import com.example.demo.dao.UserDao;
import com.example.demo.dos.UserDO;
import com.example.demo.enums.UserType;
import com.example.demo.handler.create.BusinessValidationHandler;
import com.example.demo.handler.create.ParameterValidationHandler;
import com.example.demo.context.UserCreateContext;
import com.example.demo.handler.create.UserProcessHandler;
import com.example.demo.handler.auth.AuthChainExecutor;
import com.example.demo.handler.auth.context.LoginContext;
import com.example.demo.handler.auth.context.RegisterContext;
import com.example.demo.handler.auth.context.SendCodeContext;
import com.example.demo.handler.auth.login.LoginBizHandler;
import com.example.demo.handler.auth.login.LoginValidationHandler;
import com.example.demo.handler.auth.register.RegisterBizHandler;
import com.example.demo.handler.auth.register.RegisterProcessHandler;
import com.example.demo.handler.auth.register.RegisterValidationHandler;
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
    private UserProcessHandler userProcessHandler;

    @Autowired
    private AuthChainExecutor authChainExecutor;

    @Autowired
    private UserAuthBiz userAuthBiz;

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
     * 发送邮箱验证码 - 函数式责任链，使用 Lambda 表达式代替 Handler类 去处理不同的业务逻辑
     *
     * Lambda写法 安全警告：
     * 1.lambda中捕获外部变量问题，会导致并发问题。要谨慎的只使用context中的变量。
     * 2.使用context时的多线程安全问题：不要多个不同方法操作同一个context对象，每个方法都应该new一个新的context对象然后自己操作自己的context对象。
     * 3.内存泄漏问题：lambda表达式中不要捕获外部大对象，否则lambda执行结束后，大对象也不会被GC，会导致内存泄漏。
     * 4.状态无依赖：每个步骤都应该只依赖当前锁下面context中的状态，不要出现无锁状态依赖等情况。
     */
    public String sendRegisterCode(String email) {
        SendCodeContext context = new SendCodeContext();
        context.setEmail(email);

        // 使用分布式锁保护责任链执行
        authChainExecutor.executeWithLock(context, LockConstant.SEND_CODE_EMAIL + email, List.of(
            // 参数验证
            ctx -> userAuthBiz.validateEmail(ctx.getEmail()),
            // 生成验证码
            ctx -> {
                String code = userAuthBiz.generateVerificationCode();
                ctx.setMockCode(code);
                ctx.setCodeExpireAt(System.currentTimeMillis() + UserAuthConstant.REGISTER_CODE_TTL_MILLIS);
            },
            // 发送邮件
            ctx -> userAuthBiz.mockSendRegisterMail(ctx.getEmail(), ctx.getMockCode()),
            // 持久化
            ctx -> userAuthBiz.saveRegisterCode(ctx.getEmail(), ctx.getMockCode(), ctx.getCodeExpireAt())
        ));

        return context.getMockCode();
    }

    /**
     * 邮箱验证码注册 - 对象式责任链，创建多个handler对象去处理不同的业务逻辑
     */
    public UserDO registerByEmail(String username, String email, String password, String verificationCode) {
        RegisterContext context = new RegisterContext();
        context.setUsername(username);
        context.setEmail(email);
        context.setPassword(password);
        context.setVerificationCode(verificationCode);

        // 使用分布式锁保护责任链执行
        authChainExecutor.executeWithLock(context, LockConstant.REGISTER_EMAIL + email, List.of(
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

        // 使用分布式锁保护责任链执行
        authChainExecutor.executeWithLock(context, LockConstant.LOGIN_EMAIL + email, List.of(
                loginValidationHandler,
                loginBizHandler
        ));

        return context.getResultUser();
    }

    /**
     * 后台管理手动创建用户 - 使用责任链处理器
     */
    public UserDO createAdminUser(String username, String email, Integer age) {
        UserCreateContext context = new UserCreateContext(username, email, age, UserType.PRIMARY);
        authChainExecutor.execute(context, List.of(
                // 参数校验
                parameterValidationHandler,
                // 业务逻辑校验
                businessValidationHandler,
                // 数据持久化入库
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