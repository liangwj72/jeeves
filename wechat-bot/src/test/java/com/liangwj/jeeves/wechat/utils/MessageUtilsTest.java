package com.liangwj.jeeves.wechat.utils;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

public class MessageUtilsTest {
    @Test
    public void getChatRoomMessageContent() throws Exception {
        String sample = "@90715b46bed6d190d4a217e402d67f9ffb6769c1ba4af7b3bebf53f37fb939b9:<br/>11";
		Assert.isTrue(MessageUtils.getChatRoomTextMessageContent(sample).equals("\r\n11"), "");
    }


	@Test
	public void test() {
		String content = " 文字很长的问题 [嘴唇1] ";
		System.out.println("替换结果:" + content.replaceAll("(\\[\\S{2}\\])", ""));
	}
}