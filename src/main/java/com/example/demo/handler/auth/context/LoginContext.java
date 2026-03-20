package com.example.demo.handler.auth.context;

import com.example.demo.dos.UserDO;
import lombok.Getter;
import lombok.Setter;

/**
 * 登录上下文
 */
@Setter
@Getter
public class LoginContext extends BaseContext {
    private String password;
    private UserDO resultUser;
}
