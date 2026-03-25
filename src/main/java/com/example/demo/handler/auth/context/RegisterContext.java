package com.example.demo.handler.auth.context;

import com.example.demo.dos.UserDO;
import lombok.Getter;
import lombok.Setter;

/**
 * 注册上下文
 */
@Setter
@Getter
public class RegisterContext extends BaseContext {
    private String username;
    private String password;
    private String verificationCode;
    private UserDO pendingUser;
    private UserDO resultUser;

    public RegisterContext(String username, String email, String password, String verificationCode) {
        this.username = username;
        this.setEmail(email);
        this.password = password;
        this.verificationCode = verificationCode;
    }
}
