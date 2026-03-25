package com.example.demo.handler.auth.sendcode;

import com.example.demo.biz.UserAuthBiz;
import com.example.demo.constant.UserAuthConstant;
import com.example.demo.handler.auth.AuthHandler;
import com.example.demo.handler.auth.context.SendCodeContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 发码步骤：业务逻辑与发信
 */
@Component
@RequiredArgsConstructor
public class SendCodeBizHandler implements AuthHandler<SendCodeContext> {

    private final UserAuthBiz userAuthBiz;

    @Override
    public void handle(SendCodeContext context) {
        String code = userAuthBiz.generateVerificationCode();
        long expireAt = System.currentTimeMillis() + UserAuthConstant.REGISTER_CODE_TTL_MILLIS;
        userAuthBiz.mockSendRegisterMail(context.getEmail(), code);
        context.setMockCode(code);
        context.setCodeExpireAt(expireAt);
    }
}
