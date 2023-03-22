package com.liangwj.jeeves.wechat;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.liangwj.jeeves.wechat.service.DefaultMessageHandler;
import com.liangwj.jeeves.wechat.service.MessageHandler;

@Configuration
@ComponentScan(basePackageClasses = JeevesWechatBotAutoConfig.class)
public class JeevesWechatBotAutoConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JeevesWechatBotAutoConfig.class);

	@Bean
	@ConditionalOnMissingBean(value = MessageHandler.class)
	public MessageHandler messageHandler() {
		log.info("没有找到 MessageHandler 的实现类，用默认实例");
		return new DefaultMessageHandler();
	}
}
