package com.liangwj.jeeves.wechat.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.liangwj.jeeves.wechat.domain.request.component.BaseRequest;
import com.liangwj.jeeves.wechat.domain.response.BatchGetContactResponse;
import com.liangwj.jeeves.wechat.domain.response.GetContactResponse;
import com.liangwj.jeeves.wechat.domain.response.InitResponse;
import com.liangwj.jeeves.wechat.domain.response.LoginResult;
import com.liangwj.jeeves.wechat.domain.response.StatusNotifyResponse;
import com.liangwj.jeeves.wechat.domain.shared.ChatRoomDescription;
import com.liangwj.jeeves.wechat.domain.shared.Token;
import com.liangwj.jeeves.wechat.enums.BotStatus;
import com.liangwj.jeeves.wechat.enums.LoginCode;
import com.liangwj.jeeves.wechat.enums.StatusNotifyCode;
import com.liangwj.jeeves.wechat.exception.WechatException;
import com.liangwj.jeeves.wechat.exception.WechatQRExpiredException;
import com.liangwj.jeeves.wechat.utils.WechatUtils;

@Component
public class LoginService {
	private static final Logger log = LoggerFactory.getLogger(LoginService.class);

	@Autowired
	private CacheService cacheService;
	@Autowired
	private SyncServie syncServie;
	@Autowired
	private WechatHttpServiceInternal wechatHttpServiceInternal;
	@Autowired
	private MessageHandler messageHandler;

	@Value("${jeeves.auto-relogin-when-qrcode-expired}")
	private boolean AUTO_RELOGIN_WHEN_QRCODE_EXPIRED;

	@Value("${jeeves.max-qr-refresh-times}")
	private int MAX_QR_REFRESH_TIMES;

	private final int qrRefreshTimes = 0;

	private boolean quit = false;
	private BotStatus botStatus = BotStatus.Disconnect;

	public BotStatus getBotStatus() {
		return botStatus;
	}

	private byte[] qrCodeBytes;

	private void onBotStatusChange(BotStatus status) {
		this.messageHandler.onBotStatusChange(status);
		this.botStatus = status;
	}

	protected void onShutdown() {
		this.quit = true;
	}

	/**
	 * 登录
	 */
	private LoginResult loginWithUuid(String uuid) {
		LoginResult loginResponse = null;
		while (!quit) {
			loginResponse = wechatHttpServiceInternal.login(uuid);
			if (LoginCode.SUCCESS.getCode().equals(loginResponse.getCode())) {
				if (loginResponse.getHostUrl() == null) {
					throw new WechatException("hostUrl can't be found");
				}
				if (loginResponse.getRedirectUrl() == null) {
					throw new WechatException("redirectUrl can't be found");
				}
				cacheService.setHostUrl(loginResponse.getHostUrl());
				if (loginResponse.getHostUrl().equals("https://wechat.com")) {
					cacheService.setSyncUrl("https://webpush.web.wechat.com");
					cacheService.setFileUrl("https://file.web.wechat.com");
				} else {
					cacheService.setSyncUrl(loginResponse.getHostUrl().replaceFirst("^https://", "https://webpush."));
					cacheService.setFileUrl(loginResponse.getHostUrl().replaceFirst("^https://", "https://file."));
				}
				break;
			} else if (LoginCode.AWAIT_CONFIRMATION.getCode().equals(loginResponse.getCode())) {
				log.info("[*] login status = AWAIT_CONFIRMATION");
			} else if (LoginCode.AWAIT_SCANNING.getCode().equals(loginResponse.getCode())) {
				log.info("[*] login status = AWAIT_SCANNING");
			} else if (LoginCode.EXPIRED.getCode().equals(loginResponse.getCode())) {
				log.info("[*] login status = EXPIRED");
				throw new WechatQRExpiredException();
			} else {
				log.info("[*] login status = " + loginResponse.getCode());
			}
		}
		log.info("[4] login completed");
		this.onBotStatusChange(BotStatus.Success);

		return loginResponse;
	}

	public void connect() {
		log.debug("开始连接微信");
		if (this.quit) {
			return;
		}

		// 状态：连接中
		this.onBotStatusChange(BotStatus.Connecting);

		try {
			// 0 entry
			wechatHttpServiceInternal.open(qrRefreshTimes);
			log.info("[0] entry completed");

			// 1 uuid
			String uuid = wechatHttpServiceInternal.getUUID();
			cacheService.setUuid(uuid);
			log.info("[1] 成功获取UUID: {}", uuid);

			// 2 qr
			this.qrCodeBytes = wechatHttpServiceInternal.getQR(uuid);
			this.messageHandler.onQrCode(this.qrCodeBytes); // 顺便通知一下消息处理器，二维码图片已经生成了
			this.onBotStatusChange(BotStatus.AwaitQrCode); // 状态：等待扫描二维码
			log.info("[2] 成功获取二维码");

			// 3 statreport
			wechatHttpServiceInternal.statReport();
			log.info("[3] 完成statReport");

			// 4 login
			LoginResult loginResponse = this.loginWithUuid(uuid);
			if (this.quit) {
				return;
			}

			// 处理重定向，并初始化各项api参数
			InitResponse initResponse = this.afterLogin(loginResponse);

			// 9 get contact
			this.getContact();

			// 10 batch get contact
			this.batchGetContact(initResponse);

			cacheService.setAlive(true);
			log.info("[*] login process completed");

			log.info("[*] 开始监听");
			while (!this.quit) {
				syncServie.listen();
			}

		} catch (IOException | URISyntaxException ex) {
			throw new WechatException(ex);
		} catch (WechatQRExpiredException ex) {
			if (AUTO_RELOGIN_WHEN_QRCODE_EXPIRED && qrRefreshTimes <= MAX_QR_REFRESH_TIMES) {
				connect();
			} else {
				throw new WechatException(ex);
			}
		}
	}

	private InitResponse afterLogin(LoginResult loginResponse) throws IOException {
		// 5 redirect login
		Token token = wechatHttpServiceInternal.openNewloginpage(loginResponse.getRedirectUrl());
		if (token.getRet() == 0) {
			cacheService.setPassTicket(token.getPass_ticket());
			cacheService.setsKey(token.getSkey());
			cacheService.setSid(token.getWxsid());
			cacheService.setUin(token.getWxuin());
			BaseRequest baseRequest = new BaseRequest();
			baseRequest.setUin(cacheService.getUin());
			baseRequest.setSid(cacheService.getSid());
			baseRequest.setSkey(cacheService.getsKey());
			cacheService.setBaseRequest(baseRequest);
		} else {
			throw new WechatException("token ret = " + token.getRet());
		}
		log.info("[5] redirect login completed");

		// 6 redirect
		wechatHttpServiceInternal.redirect(cacheService.getHostUrl());
		log.info("[6] redirect completed");

		// 7 init
		InitResponse initResponse = wechatHttpServiceInternal.init(cacheService.getHostUrl(), cacheService.getBaseRequest());
		WechatUtils.checkBaseResponse(initResponse);
		cacheService.setSyncKey(initResponse.getSyncKey());
		cacheService.setOwner(initResponse.getUser());
		log.info("[7] init completed");

		// 8 status notify
		StatusNotifyResponse statusNotifyResponse = wechatHttpServiceInternal.statusNotify(cacheService.getHostUrl(),
				cacheService.getBaseRequest(),
				cacheService.getOwner().getUserName(), StatusNotifyCode.INITED.getCode());
		WechatUtils.checkBaseResponse(statusNotifyResponse);
		log.info("[8] status notify completed");

		return initResponse;
	}

	private void batchGetContact(InitResponse initResponse) throws IOException {
		ChatRoomDescription[] chatRoomDescriptions = initResponse.getContactList().stream()
				.filter(x -> x != null && WechatUtils.isChatRoom(x))
				.map(x -> {
					ChatRoomDescription description = new ChatRoomDescription();
					description.setUserName(x.getUserName());
					return description;
				})
				.toArray(ChatRoomDescription[]::new);
		if (chatRoomDescriptions.length > 0) {
			BatchGetContactResponse batchGetContactResponse = wechatHttpServiceInternal.batchGetContact(
					cacheService.getHostUrl(),
					cacheService.getBaseRequest(),
					chatRoomDescriptions);
			WechatUtils.checkBaseResponse(batchGetContactResponse);
			cacheService.getChatRooms().addAll(batchGetContactResponse.getContactList());
		}
		log.debug("批量获取联系人: {} ", JSON.toJSONString(cacheService.getChatRooms(), true));
		log.info("[10] batch get contact completed");

	}

	/**
	 * 获取联系人
	 * 
	 * @throws IOException
	 */
	private void getContact() throws IOException {
		// 9 get contact
		long seq = 0;
		do {
			GetContactResponse getContactResponse = wechatHttpServiceInternal.getContact(cacheService.getHostUrl(),
					cacheService.getBaseRequest().getSkey(), seq);
			WechatUtils.checkBaseResponse(getContactResponse);
			log.info("[*] getContactResponse seq = " + getContactResponse.getSeq());
			log.info("[*] getContactResponse memberCount = " + getContactResponse.getMemberCount());
			seq = getContactResponse.getSeq();
			cacheService.getIndividuals().addAll(getContactResponse.getMemberList().stream().filter(WechatUtils::isIndividual)
					.collect(Collectors.toSet()));
			cacheService.getMediaPlatforms().addAll(getContactResponse.getMemberList().stream()
					.filter(WechatUtils::isMediaPlatform).collect(Collectors.toSet()));
		} while (seq > 0);

		log.debug("获取联系人 getIndividuals : {} ", JSON.toJSONString(cacheService.getIndividuals(), true));
		log.debug("获取联系人 getMediaPlatforms: {} ", JSON.toJSONString(cacheService.getMediaPlatforms(), true));

		log.info("[9] get contact completed");
	}

	/**
	 * 下载二维码
	 * 
	 * @throws IOException
	 */
	public void downloadQrCode(HttpServletResponse response) throws IOException {
		Assert.notNull(this.qrCodeBytes, "二维码不能为空");
		// 输出流，通过输出流将文件写回浏览器
		ServletOutputStream outputStream = response.getOutputStream();
		response.setContentType("image/jpeg");

		outputStream.write(this.qrCodeBytes, 0, this.qrCodeBytes.length);
		outputStream.flush();
		// 关闭资源
		outputStream.close();
	}

}
