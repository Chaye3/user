package com.example.demo.context;

import com.example.demo.dos.UserDO;
import com.example.demo.enums.UserType;
import com.example.demo.handler.auth.context.BaseContext;

/**
 * 用户创建上下文 - 用于在处理器链中传递数据
 */
public class UserCreateContext extends BaseContext {

    private String username;
    private Integer age;
    private UserType userType;

    private UserDO resultUser;
    private UserDO pendingUser;

    public UserCreateContext(String username, String email, Integer age) {
        this(username, email, age, UserType.PRIMARY);
    }

    public UserCreateContext(String username, String email, Integer age, UserType userType) {
        this.username = username;
        this.setEmail(email);
        this.age = age;
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public UserDO getResultUser() {
        return resultUser;
    }

    public void setResultUser(UserDO resultUser) {
        this.resultUser = resultUser;
    }

    public UserDO getPendingUser() {
        return pendingUser;
    }

    public void setPendingUser(UserDO pendingUser) {
        this.pendingUser = pendingUser;
    }
}
