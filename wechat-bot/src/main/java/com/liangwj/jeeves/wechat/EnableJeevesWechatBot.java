package com.liangwj.jeeves.wechat;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.liangwj.jeeves.wechat.service.LoginService;

/**
 * 自动配置微信机器人
 *
 * @author rock
 *
 * @see LoginService#connect() 启动方法
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = {
		JeevesWechatBotAutoConfig.class
})
@EnableScheduling
public @interface EnableJeevesWechatBot {

}
