package com.liangwj.jeeves.wechat.service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

public class UrlServicesTest {
	private UrlServices obj = new UrlServices();
	
	@Test
	public void test1() throws MalformedURLException, URISyntaxException {
		System.out.println(obj.getUuIdUrl().toString());
	}
}
