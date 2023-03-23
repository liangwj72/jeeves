package com.liangwj.jeeves.example;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.liangwj.jeeves.wechat.enums.BotStatus;
import com.liangwj.jeeves.wechat.service.CacheService;
import com.liangwj.jeeves.wechat.service.LoginService;

@Controller
public class IndexController {
	@Autowired
	private LoginService loginService;

	@Autowired
	private CacheService cacheService;

	@GetMapping(value = {
			"/"
	})
	public String index(Model model) {
		boolean showQrCode = false;
		boolean logined = false;

		model.addAttribute("status", this.loginService.getBotStatus().getMsg());
		if (this.loginService.getBotStatus() == BotStatus.AwaitQrCode) {
			// 如果等待扫描二维码
			showQrCode = true;
		} else if (this.loginService.getBotStatus() == BotStatus.Success) {
			// 如果已经登录成功
			logined = true;
			model.addAttribute("owner", this.cacheService.getOwner()); // 用户信息
			model.addAttribute("chatrooms", this.cacheService.getChatRooms()); // 所有聊天室
		}
		model.addAttribute("showQrCode", showQrCode);
		model.addAttribute("logined", logined);

		return "index";
	}

	/**
	 * 文件下载
	 * 
	 * @param name
	 * @param response
	 * @throws IOException
	 */
	@GetMapping("/qrcode.jpg")
	public void download(HttpServletResponse response) throws IOException {
		this.loginService.downloadQrCode(response);
	}
}
