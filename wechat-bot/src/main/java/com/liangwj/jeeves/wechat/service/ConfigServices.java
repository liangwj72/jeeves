package com.liangwj.jeeves.wechat.service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;

import com.liangwj.jeeves.wechat.enums.UUIDParaEnum;

@Service
public class ConfigServices {

	public static final String LOGIN_URL = "https://login.weixin.qq.com"; // 基本的URL
	public static final String UUID_URL = LOGIN_URL + "/jslogin"; // 获取uuid
	// public static final String
	// public static final String
	// public static final String

	/**
	 * 构造获取uuid的url
	 * 
	 * @return
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 */
	public URI getUuIdUrl() throws URISyntaxException, MalformedURLException {
		URIBuilder builder = new URIBuilder(UUID_URL);
		builder.addParameter(UUIDParaEnum.APP_ID.para(), UUIDParaEnum.APP_ID.value());
		builder.addParameter(UUIDParaEnum.REDIRECT_URL.para(), UUIDParaEnum.REDIRECT_URL.value());
		builder.addParameter(UUIDParaEnum.FUN.para(), UUIDParaEnum.FUN.value());
		builder.addParameter(UUIDParaEnum.U_.para(), String.valueOf(System.currentTimeMillis()));
		builder.addParameter(UUIDParaEnum.LANG.para(), UUIDParaEnum.LANG.value());
		return builder.build().toURL().toURI();
	}

}
