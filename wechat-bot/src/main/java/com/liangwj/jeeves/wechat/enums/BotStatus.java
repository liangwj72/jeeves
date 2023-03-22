package com.liangwj.jeeves.wechat.enums;

/**
 * 机器人状态
 *
 */
public enum BotStatus {

	Connecting("连接中"),
	AwaitQrCode("等待扫描二维码"),
	Success("登录成功");

	private final String msg;

	BotStatus(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}
}
