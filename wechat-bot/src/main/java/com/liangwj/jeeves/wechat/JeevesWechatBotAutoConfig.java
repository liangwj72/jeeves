package com.liangwj.jeeves.wechat;

import java.io.IOException;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import com.liangwj.jeeves.wechat.service.ConfigServices;
import com.liangwj.jeeves.wechat.service.DefaultMessageHandler;
import com.liangwj.jeeves.wechat.service.MessageHandler;

import springframework.StatefullRestTemplate;

@Configuration
@ComponentScan(basePackageClasses = JeevesWechatBotAutoConfig.class)
public class JeevesWechatBotAutoConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JeevesWechatBotAutoConfig.class);

	public JeevesWechatBotAutoConfig() throws IOException {
		System.setProperty("jsse.enableSNIExtension", "false");
	}

	@Bean
	@ConditionalOnMissingBean(value = MessageHandler.class)
	public MessageHandler messageHandler() {
		log.info("没有找到 MessageHandler 的实现类，用默认实例");
		return new DefaultMessageHandler();
	}

	@Bean
	public RestTemplate restTemplate(ConfigServices configServices) throws IOException {
		log.debug("创建 带状态的 RestTemplate, 可保存cookie到文件");

		HttpContext httpContext = new BasicHttpContext();
		// 使用 cookieStore
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, new BasicCookieStore());
		httpContext.setAttribute(HttpClientContext.REQUEST_CONFIG, RequestConfig.custom().setRedirectsEnabled(false).build());// 不重定向
		StatefullRestTemplate rest = new StatefullRestTemplate(httpContext);

		rest.getInterceptors().add(new ClientHttpRequestInterceptor() {
			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
					throws IOException {

				HttpHeaders headers = request.getHeaders();
				headers.set(HttpHeaders.ACCEPT_LANGUAGE, BotConstants.HEADER_ACCEPT_LANGUAGE);
				headers.set(HttpHeaders.ACCEPT_ENCODING, BotConstants.HEADER_ACCEPT_ENCODING);
				headers.set(HttpHeaders.USER_AGENT, BotConstants.HEADER_USER_AGENT);
				headers.set(HttpHeaders.REFERER, BotConstants.HEADER_UOS_REFERER);

				log.debug("请求:{} {}", request.getMethod(), request.getURI());

				return execution.execute(request, body);
			}
		});

		return rest;
	}
}
