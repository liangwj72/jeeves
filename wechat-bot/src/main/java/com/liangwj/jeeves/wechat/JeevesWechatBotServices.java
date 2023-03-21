package com.liangwj.jeeves.wechat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.liangwj.jeeves.wechat.service.LoginService;

@Configuration
@ComponentScan(basePackageClasses = JeevesWechatBotServices.class)
public class JeevesWechatBotServices {
    @Autowired
    private LoginService loginService;

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    public void start() {
        logger.info("Jeeves starts");
        System.setProperty("jsse.enableSNIExtension", "false");
        loginService.login();
    }
}