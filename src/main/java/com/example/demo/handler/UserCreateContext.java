package com.example.demo.handler;

import com.example.demo.entity.User;

/**
 * 用户创建上下文 - 用于在处理器链中传递数据
 */
public class UserCreateContext {

    private String username;
    private String email;
    private Integer age;
    private UserType userType;

    private User resultUser;

    public UserCreateContext(String username, String email, Integer age) {
        this(username, email, age, UserType.PRIMARY);
    }

    public UserCreateContext(String username, String email, Integer age, UserType userType) {
        this.username = username;
        this.email = email;
        this.age = age;
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public User getResultUser() {
        return resultUser;
    }

    public void setResultUser(User resultUser) {
        this.resultUser = resultUser;
    }
}
