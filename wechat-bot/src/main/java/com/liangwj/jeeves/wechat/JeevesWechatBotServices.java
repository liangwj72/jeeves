package com.liangwj.jeeves.wechat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liangwj.jeeves.wechat.service.LoginService;

@Service
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
