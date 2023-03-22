package com.liangwj.jeeves.wechat.enums;

public enum LoginCode {

	/** 成功 */
	SUCCESS("200", "成功"),

	/** 在手机上点击确认 */
	AWAIT_CONFIRMATION("201", "在手机上点击确认"),

	/** 二维码超时 */
	EXPIRED("400", "二维码超时"),

	/** 等待扫描二维码 */
	AWAIT_SCANNING("408", "等待扫描二维码");

	private final String code;
	private final String msg;

	LoginCode(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
}
