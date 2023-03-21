package com.cherry.jeeves.utils.rest;

import java.io.IOException;

import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

/**
 * 配置 HTTP 请求
 * 
 * @author liangwj
 *
 */
@Configuration
public class HttpRestConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HttpRestConfig.class);

	private String BROWSER_DEFAULT_ACCEPT_LANGUAGE = "en,zh-CN;q=0.8,zh;q=0.6,ja;q=0.4,zh-TW;q=0.2";
	private String BROWSER_DEFAULT_ACCEPT_ENCODING = "gzip, deflate, br";

	@Value("${wechat.ua}")
	private String BROWSER_DEFAULT_USER_AGENT;

	public static final String UOS_PATCH_CLIENT_VERSION = "2.0.0";
	public static final String UOS_PATCH_EXTSPAM = "Go8FCIkFEokFCggwMDAwMDAwMRAGGvAESySibk50w5Wb3uTl2c2h64jVVrV7gNs06GFlWplHQbY/5FfiO++1yH4ykCyNPWKXmco+wfQzK5R98D3so7rJ5LmGFvBLjGceleySrc3SOf2Pc1gVehzJgODeS0lDL3/I/0S2SSE98YgKleq6Uqx6ndTy9yaL9qFxJL7eiA/R3SEfTaW1SBoSITIu+EEkXff+Pv8NHOk7N57rcGk1w0ZzRrQDkXTOXFN2iHYIzAAZPIOY45Lsh+A4slpgnDiaOvRtlQYCt97nmPLuTipOJ8Qc5pM7ZsOsAPPrCQL7nK0I7aPrFDF0q4ziUUKettzW8MrAaiVfmbD1/VkmLNVqqZVvBCtRblXb5FHmtS8FxnqCzYP4WFvz3T0TcrOqwLX1M/DQvcHaGGw0B0y4bZMs7lVScGBFxMj3vbFi2SRKbKhaitxHfYHAOAa0X7/MSS0RNAjdwoyGHeOepXOKY+h3iHeqCvgOH6LOifdHf/1aaZNwSkGotYnYScW8Yx63LnSwba7+hESrtPa/huRmB9KWvMCKbDThL/nne14hnL277EDCSocPu3rOSYjuB9gKSOdVmWsj9Dxb/iZIe+S6AiG29Esm+/eUacSba0k8wn5HhHg9d4tIcixrxveflc8vi2/wNQGVFNsGO6tB5WF0xf/plngOvQ1/ivGV/C1Qpdhzznh0ExAVJ6dwzNg7qIEBaw+BzTJTUuRcPk92Sn6QDn2Pu3mpONaEumacjW4w6ipPnPw+g2TfywJjeEcpSZaP4Q3YV5HG8D6UjWA4GSkBKculWpdCMadx0usMomsSS/74QgpYqcPkmamB4nVv1JxczYITIqItIKjD35IGKAUwAA==";

	@Bean
	public RestTemplate restTemplate() {
		// TODO 需要持久化 cookie
		CookieStore cookieStore = new BasicCookieStore();

		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore); // 使用
																				// cookieStore
																				// 记录cookie
		httpContext.setAttribute(HttpClientContext.REQUEST_CONFIG, RequestConfig.custom().setRedirectsEnabled(false).build());// 不重定向
		StatefullRestTemplate rest = new StatefullRestTemplate(httpContext);

		rest.getInterceptors().add(new ClientHttpRequestInterceptor() {
			@Override
			public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
					throws IOException {

				HttpHeaders headers = request.getHeaders();
				headers.set(HttpHeaders.ACCEPT_LANGUAGE, BROWSER_DEFAULT_ACCEPT_LANGUAGE);
				headers.set(HttpHeaders.ACCEPT_ENCODING, BROWSER_DEFAULT_ACCEPT_ENCODING);
				headers.set(HttpHeaders.USER_AGENT, BROWSER_DEFAULT_USER_AGENT);
				headers.set(HttpHeaders.REFERER, "https://wx.qq.com/?target=t");

				log.info("请求:{} {}", request.getMethod(), request.getURI());

				return execution.execute(request, body);
			}
		});

		return rest;
	}
}
