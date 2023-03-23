package com.liangwj.jeeves.wechat.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {
	public static String getChatRoomTextMessageContent(String content) {
		if (content == null) {
			throw new IllegalArgumentException("content");
		}
		return content.replaceAll("^(@([0-9]|[a-z])+):", "")
				.replaceAll("<br/>", "\n").trim();
	}

	/**
	 * 删除微信消息中的表情
	 * 
	 * @param content
	 * @return
	 */
	public static String removeEm(String content) {
		if (content != null) {
			return content.replaceAll("(\\[\\S{2}\\])", "").trim();
		} else {
			return null;
		}
	}

	public static String getSenderOfChatRoomTextMessage(String content) {
		if (content == null) {
			throw new IllegalArgumentException("content");
		}
		Pattern pattern = Pattern.compile("^(@([0-9]|[a-z])+):");
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
}
