package com.liangwj.jeeves.example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.liangwj.jeeves.wechat.domain.shared.ChatRoomMember;
import com.liangwj.jeeves.wechat.domain.shared.Contact;
import com.liangwj.jeeves.wechat.domain.shared.FriendInvitationContent;
import com.liangwj.jeeves.wechat.domain.shared.Message;
import com.liangwj.jeeves.wechat.domain.shared.RecommendInfo;
import com.liangwj.jeeves.wechat.enums.BotStatus;
import com.liangwj.jeeves.wechat.service.CacheService;
import com.liangwj.jeeves.wechat.service.MessageHandler;
import com.liangwj.jeeves.wechat.service.WechatHttpService;
import com.liangwj.jeeves.wechat.utils.MessageUtils;
import com.liangwj.tools2k.utils.other.LogUtil;

@Component
public class MessageHandlerImpl implements MessageHandler {

	private static final Logger log = LoggerFactory.getLogger(MessageHandlerImpl.class);
	@Autowired
	private WechatHttpService wechatHttpService;

	@Autowired
	private CacheService cacheService;

	private final int questMinLen = 5;

	@Override
	public void onReceivingChatRoomTextMessage(Message message, Contact room, ChatRoomMember sender, String content) {
		String owner = "@" + this.cacheService.getOwner().getNickName();
		if (content.startsWith(owner)) {
			// 被人@了

			String txt = MessageUtils.removeEm(content.substring(owner.length()).trim());
			// 如果有文字
			String reply = String.format("@%s 宝宝 [嘴唇]", sender.getNickName());

			if (StringUtils.hasText(txt)) {

				if (txt.length() >= this.questMinLen) {
					reply = String.format("@%s 收到", sender.getNickName(), txt);
				} else {
					reply = String.format("@%s 你的提问太短了,请至少超过%d个字", sender.getNickName(), this.questMinLen);
				}

				// 回复
			}
			this.sendText(message.getFromUserName(), reply);
		}
	}

	private void sendText(String username, String text) {
		try {
			this.wechatHttpService.sendText(username, text);
		} catch (IOException e) {
			LogUtil.traceError(log, e, "发送消息失败");
		}
	}

	@Override
	public void onReceivingChatRoomImageMessage(Message message, String thumbImageUrl, String fullImageUrl) {
		log.debug("onReceivingChatRoomImageMessage");
		log.debug("thumbImageUrl:" + thumbImageUrl);
		log.debug("fullImageUrl:" + fullImageUrl);
	}

	@Override
	public void onReceivingPrivateTextMessage(Message message) throws IOException {
		log.debug("onReceivingPrivateTextMessage");
		log.debug("from: " + message.getFromUserName());
		log.debug("to: " + message.getToUserName());
		log.debug("content:" + message.getContent());
		// 将原文回复给对方
		replyMessage(message);
	}

	@Override
	public void onReceivingPrivateImageMessage(Message message, String thumbImageUrl, String fullImageUrl) throws IOException {
		log.debug("onReceivingPrivateImageMessage");
		log.debug("thumbImageUrl:" + thumbImageUrl);
		log.debug("fullImageUrl:" + fullImageUrl);
		// 将图片保存在本地
		byte[] data = wechatHttpService.downloadImage(thumbImageUrl);
		FileOutputStream fos = new FileOutputStream("thumb.jpg");
		fos.write(data);
		fos.close();
	}

	@Override
	public boolean onReceivingFriendInvitation(RecommendInfo info) {
		log.debug("onReceivingFriendInvitation");
		log.debug("recommendinfo content:" + info.getContent());
		// 默认接收所有的邀请
		return true;
	}

	@Override
	public void postAcceptFriendInvitation(Message message) throws IOException {
		log.debug("postAcceptFriendInvitation");
		// 将该用户的微信号设置成他的昵称
		String content = StringEscapeUtils.unescapeXml(message.getContent());
		ObjectMapper xmlMapper = new XmlMapper();
		FriendInvitationContent friendInvitationContent = xmlMapper.readValue(content, FriendInvitationContent.class);
		wechatHttpService.setAlias(message.getRecommendInfo().getUserName(), friendInvitationContent.getFromusername());
	}

	@Override
	public void onChatRoomMembersChanged(Contact chatRoom, Set<ChatRoomMember> membersJoined, Set<ChatRoomMember> membersLeft) {
		log.debug("onChatRoomMembersChanged");
		log.debug("chatRoom:" + chatRoom.getUserName());
		if (membersJoined != null && membersJoined.size() > 0) {
			log.debug("membersJoined:"
					+ String.join(",", membersJoined.stream().map(ChatRoomMember::getNickName).collect(Collectors.toList())));
		}
		if (membersLeft != null && membersLeft.size() > 0) {
			log.debug("membersLeft:"
					+ String.join(",", membersLeft.stream().map(ChatRoomMember::getNickName).collect(Collectors.toList())));
		}
	}

	@Override
	public void onNewChatRoomsFound(Set<Contact> chatRooms) {
		log.debug("onNewChatRoomsFound");
		chatRooms.forEach(x -> log.debug(x.getUserName()));
	}

	@Override
	public void onChatRoomsDeleted(Set<Contact> chatRooms) {
		log.debug("onChatRoomsDeleted");
		chatRooms.forEach(x -> log.debug(x.getUserName()));
	}

	@Override
	public void onNewFriendsFound(Set<Contact> contacts) {
		log.debug("onNewFriendsFound");
		contacts.forEach(x -> {
			log.debug(x.getUserName());
			log.debug(x.getNickName());
		});
	}

	@Override
	public void onFriendsDeleted(Set<Contact> contacts) {
		log.debug("onFriendsDeleted");
		contacts.forEach(x -> {
			log.debug(x.getUserName());
			log.debug(x.getNickName());
		});
	}

	@Override
	public void onNewMediaPlatformsFound(Set<Contact> mps) {
		log.debug("onNewMediaPlatformsFound");
	}

	@Override
	public void onMediaPlatformsDeleted(Set<Contact> mps) {
		log.debug("onMediaPlatformsDeleted");
	}

	@Override
	public void onRedPacketReceived(Contact contact) {
		log.debug("onRedPacketReceived");
		if (contact != null) {
			log.debug("the red packet is from " + contact.getNickName());
		}
	}

	private void replyMessage(Message message) throws IOException {
		wechatHttpService.sendText(message.getFromUserName(), message.getContent());
	}

	@Override
	public void onQrCode(byte[] bytes) {
		// 将二维码保存在内存

		// DebugUtils.saveQrCodeToFile(bytes);
	}

	@Override
	public void onBotStatusChange(BotStatus status) {
		log.debug("机器人状态改变为:{}", status.getMsg());
	}

}
