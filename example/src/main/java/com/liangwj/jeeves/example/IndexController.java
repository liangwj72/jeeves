package com.liangwj.jeeves.example;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.liangwj.jeeves.wechat.enums.BotStatus;
import com.liangwj.jeeves.wechat.service.LoginService;

@Controller
public class IndexController {
	@Autowired
	private LoginService loginService;

	@GetMapping(value = {
			"/"
	})
	public String index(Model model) {
		model.addAttribute("status", this.loginService.getBotStatus().getMsg());
		model.addAttribute("showQrCode", this.loginService.getBotStatus() == BotStatus.AwaitQrCode);
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
