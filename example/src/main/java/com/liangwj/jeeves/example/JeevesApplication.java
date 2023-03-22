package com.liangwj.jeeves.example;

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.liangwj.jeeves.wechat.EnableJeevesWechatBot;
import com.liangwj.jeeves.wechat.service.LoginService;
import com.liangwj.tools2k.apiServer.anno.EnableWebPorjectSet;
import com.liangwj.tools2k.utils.other.SpringUtil;

@SpringBootApplication
@EnableJeevesWechatBot
@EnableWebPorjectSet
public class JeevesApplication {

	@Autowired
	private LoginService loginService;

	public static void main(String[] args) {
		SpringUtil.startServer(JeevesApplication.class);
	}

	@PostConstruct
	protected void start() {
		// 延时启动机器人
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// loginService.connect();
			}
		}, 1000L);
	}
}
