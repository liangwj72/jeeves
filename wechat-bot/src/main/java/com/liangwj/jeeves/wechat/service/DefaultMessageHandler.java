package com.liangwj.jeeves.wechat.service;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liangwj.jeeves.wechat.domain.shared.ChatRoomMember;
import com.liangwj.jeeves.wechat.domain.shared.Contact;
import com.liangwj.jeeves.wechat.domain.shared.Message;
import com.liangwj.jeeves.wechat.domain.shared.RecommendInfo;
import com.liangwj.jeeves.wechat.enums.BotStatus;
import com.liangwj.jeeves.wechat.utils.DebugUtils;

public class DefaultMessageHandler implements MessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(DefaultMessageHandler.class);

	@Override
	public void onReceivingChatRoomTextMessage(Message message) {
		logger.info("onReceivingChatRoomTextMessage");
	}

	@Override
	public void onReceivingChatRoomImageMessage(Message message, String thumbImageUrl, String fullImageUrl) {
		logger.info("onReceivingChatRoomImageMessage");
	}

	@Override
	public void onReceivingPrivateTextMessage(Message message) throws IOException {
		logger.info("onReceivingPrivateTextMessage");
	}

	@Override
	public void onReceivingPrivateImageMessage(Message message, String thumbImageUrl, String fullImageUrl) throws IOException {
		logger.info("onReceivingPrivateImageMessage");
	}

	@Override
	public boolean onReceivingFriendInvitation(RecommendInfo info) {
		logger.info("onReceivingFriendInvitation");
		return false;
	}

	@Override
	public void postAcceptFriendInvitation(Message message) throws IOException {
		logger.info("postAcceptFriendInvitation");
	}

	@Override
	public void onChatRoomMembersChanged(Contact chatRoom, Set<ChatRoomMember> membersJoined, Set<ChatRoomMember> membersLeft) {
		logger.info("onChatRoomMembersChanged");
	}

	@Override
	public void onNewChatRoomsFound(Set<Contact> chatRooms) {
		logger.info("onNewChatRoomsFound");
	}

	@Override
	public void onChatRoomsDeleted(Set<Contact> chatRooms) {
		logger.info("onChatRoomsDeleted");
	}

	@Override
	public void onNewFriendsFound(Set<Contact> contacts) {
		logger.info("onNewFriendsFound");
	}

	@Override
	public void onFriendsDeleted(Set<Contact> contacts) {
		logger.info("onFriendsDeleted");
	}

	@Override
	public void onNewMediaPlatformsFound(Set<Contact> mps) {
		logger.info("onNewMediaPlatformsFound");
	}

	@Override
	public void onMediaPlatformsDeleted(Set<Contact> mps) {
		logger.info("onMediaPlatformsDeleted");
	}

	@Override
	public void onRedPacketReceived(Contact contact) {
		logger.info("onRedPacketReceived");
	}

	/**
	 * 将二维码保存为一个临时文件
	 */
	@Override
	public void onQrCode(byte[] bytes) {
		DebugUtils.saveQrCodeToFile(bytes); // 打开登陆二维码图片
	}

	@Override
	public void onBotStatusChange(BotStatus status) {
		logger.info("机器人状态改变为:{}", status.getMsg());
	}
}
