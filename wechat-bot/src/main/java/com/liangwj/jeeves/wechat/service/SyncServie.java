package com.liangwj.jeeves.wechat.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.liangwj.jeeves.wechat.domain.response.SyncCheckResponse;
import com.liangwj.jeeves.wechat.domain.response.SyncResponse;
import com.liangwj.jeeves.wechat.domain.response.VerifyUserResponse;
import com.liangwj.jeeves.wechat.domain.shared.ChatRoomMember;
import com.liangwj.jeeves.wechat.domain.shared.Contact;
import com.liangwj.jeeves.wechat.domain.shared.Message;
import com.liangwj.jeeves.wechat.domain.shared.RecommendInfo;
import com.liangwj.jeeves.wechat.domain.shared.VerifyUser;
import com.liangwj.jeeves.wechat.enums.MessageType;
import com.liangwj.jeeves.wechat.enums.RetCode;
import com.liangwj.jeeves.wechat.enums.Selector;
import com.liangwj.jeeves.wechat.exception.WechatException;
import com.liangwj.jeeves.wechat.utils.MessageUtils;
import com.liangwj.jeeves.wechat.utils.WechatUtils;

@Component
public class SyncServie {
	private static final Logger log = LoggerFactory.getLogger(SyncServie.class);
	@Autowired
	private CacheService cacheService;
	@Autowired
	private WechatHttpServiceInternal wechatHttpService;
	@Autowired
	private MessageHandler messageHandler;

	@Value("${wechat.url.get_msg_img}")
	private String WECHAT_URL_GET_MSG_IMG;

	private final static String RED_PACKET_CONTENT = "收到红包，请在手机上查看";

	public void listen() throws IOException, URISyntaxException {
		SyncCheckResponse syncCheckResponse = wechatHttpService.syncCheck(
				cacheService.getSyncUrl(),
				cacheService.getBaseRequest().getUin(),
				cacheService.getBaseRequest().getSid(),
				cacheService.getBaseRequest().getSkey(),
				cacheService.getSyncKey());
		int retCode = syncCheckResponse.getRetcode();
		int selector = syncCheckResponse.getSelector();
		log.info(String.format("[SYNCCHECK] retcode = %s, selector = %s", retCode, selector));
		if (retCode == RetCode.NORMAL.getCode()) {
			// 有新消息
			if (selector == Selector.NEW_MESSAGE.getCode()) {
				onNewMessage();
			} else if (selector == Selector.ENTER_LEAVE_CHAT.getCode()) {
				sync();
			} else if (selector == Selector.CONTACT_UPDATED.getCode()) {
				sync();
			} else if (selector == Selector.UNKNOWN1.getCode()) {
				sync();
			} else if (selector == Selector.UNKNOWN6.getCode()) {
				sync();
			} else if (selector != Selector.NORMAL.getCode()) {
				throw new WechatException("syncCheckResponse ret = " + retCode);
			}
		} else {
			throw new WechatException("syncCheckResponse selector = " + selector);
		}
	}

	private SyncResponse sync() throws IOException {
		SyncResponse syncResponse = wechatHttpService.sync(cacheService.getHostUrl(), cacheService.getSyncKey(),
				cacheService.getBaseRequest());
		WechatUtils.checkBaseResponse(syncResponse);
		cacheService.setSyncKey(syncResponse.getSyncKey());
		cacheService.setSyncCheckKey(syncResponse.getSyncCheckKey());
		// mod包含新增和修改
		if (syncResponse.getModContactCount() > 0) {
			onContactsModified(syncResponse.getModContactList());
		}
		// del->联系人移除
		if (syncResponse.getDelContactCount() > 0) {
			onContactsDeleted(syncResponse.getDelContactList());
		}
		return syncResponse;
	}

	private void acceptFriendInvitation(RecommendInfo info) throws IOException, URISyntaxException {
		VerifyUser user = new VerifyUser();
		user.setValue(info.getUserName());
		user.setVerifyUserTicket(info.getTicket());
		VerifyUserResponse verifyUserResponse = wechatHttpService.acceptFriend(
				cacheService.getHostUrl(),
				cacheService.getBaseRequest(),
				cacheService.getPassTicket(),
				new VerifyUser[] {
						user
				});
		WechatUtils.checkBaseResponse(verifyUserResponse);
	}

	private boolean isMessageFromIndividual(Message message) {
		return message.getFromUserName() != null
				&& message.getFromUserName().startsWith("@")
				&& !message.getFromUserName().startsWith("@@");
	}

	private boolean isMessageFromChatRoom(Message message) {
		return message.getFromUserName() != null && message.getFromUserName().startsWith("@@");
	}

	/**
	 * 发送 收到群聊事件
	 * 
	 * @param message
	 */
	private void dispatchReceivingChatRoomTextMessage(Message message) {
		String roomId = message.getFromUserName();
		String sendUserName = MessageUtils.getSenderOfChatRoomTextMessage(message.getContent());
		String content = MessageUtils.getChatRoomTextMessageContent(message.getContent());

		Contact room = this.cacheService.getChatRoomById(roomId);
		if (room == null) {
			log.warn(" 但找不到聊天室 {} 的信息", roomId);
		}
		ChatRoomMember sender = room.getUserInfoByUsername(sendUserName);
		if (sender == null) {
			log.warn(" 但找不到发送人 {} 的信息", sendUserName);
		}
		if (room != null && sender != null && StringUtils.hasText(content)) {
			log.debug("收到群聊消息 \n\t群聊: {} ({}) \n\t来自: {} ({}) \n\t内容: {}",
					room.getNickName(), room.getUserName(), sender.getNickName(), sender.getUserName(), content);

			messageHandler.onReceivingChatRoomTextMessage(message, room, sender, content);
		}
	}

	private void onNewMessage() throws IOException, URISyntaxException {
		SyncResponse syncResponse = sync();
		if (messageHandler == null) {
			return;
		}
		for (Message message : syncResponse.getAddMsgList()) {
			// 文本消息
			if (message.getMsgType() == MessageType.TEXT.getCode()) {
				cacheService.getContactNamesWithUnreadMessage().add(message.getFromUserName());
				// 个人
				if (isMessageFromIndividual(message)) {
					messageHandler.onReceivingPrivateTextMessage(message);
				}
				// 群
				else if (isMessageFromChatRoom(message)) {
					this.dispatchReceivingChatRoomTextMessage(message);
				}
				// 图片
			} else if (message.getMsgType() == MessageType.IMAGE.getCode()) {
				cacheService.getContactNamesWithUnreadMessage().add(message.getFromUserName());
				String fullImageUrl = String.format(WECHAT_URL_GET_MSG_IMG, cacheService.getHostUrl(), message.getMsgId(),
						cacheService.getsKey());
				String thumbImageUrl = fullImageUrl + "&type=slave";
				// 个人
				if (isMessageFromIndividual(message)) {
					messageHandler.onReceivingPrivateImageMessage(message, thumbImageUrl, fullImageUrl);
				}
				// 群
				else if (isMessageFromChatRoom(message)) {
					messageHandler.onReceivingChatRoomImageMessage(message, thumbImageUrl, fullImageUrl);
				}
			}
			// 系统消息
			else if (message.getMsgType() == MessageType.SYS.getCode()) {
				// 红包
				if (RED_PACKET_CONTENT.equals(message.getContent())) {
					log.info("[*] you've received a red packet");
					String from = message.getFromUserName();
					Set<Contact> contacts = null;
					// 个人
					if (isMessageFromIndividual(message)) {
						contacts = cacheService.getIndividuals();
					}
					// 群
					else if (isMessageFromChatRoom(message)) {
						contacts = cacheService.getChatRooms();
					}
					if (contacts != null) {
						Contact contact = contacts.stream().filter(x -> Objects.equals(x.getUserName(), from)).findAny()
								.orElse(null);
						messageHandler.onRedPacketReceived(contact);
					}
				}
			}
			// 好友邀请
			else if (message.getMsgType() == MessageType.VERIFYMSG.getCode()
					&& cacheService.getOwner().getUserName().equals(message.getToUserName())) {
				if (messageHandler.onReceivingFriendInvitation(message.getRecommendInfo())) {
					acceptFriendInvitation(message.getRecommendInfo());
					log.info("[*] you've accepted the invitation");
					messageHandler.postAcceptFriendInvitation(message);
				} else {
					log.info("[*] you've declined the invitation");
					// TODO decline invitation
				}
			}

		}
	}

	private void onContactsModified(Set<Contact> contacts) {
		Set<Contact> individuals = new HashSet<>();
		Set<Contact> chatRooms = new HashSet<>();
		Set<Contact> mediaPlatforms = new HashSet<>();

		for (Contact contact : contacts) {
			if (WechatUtils.isIndividual(contact)) {
				individuals.add(contact);
			} else if (WechatUtils.isMediaPlatform(contact)) {
				mediaPlatforms.add(contact);
			} else if (WechatUtils.isChatRoom(contact)) {
				chatRooms.add(contact);
			}
		}

		// individual
		if (individuals.size() > 0) {
			Set<Contact> existingIndividuals = cacheService.getIndividuals();
			Set<Contact> newIndividuals = individuals.stream().filter(x -> !existingIndividuals.contains(x))
					.collect(Collectors.toSet());
			individuals.forEach(x -> {
				existingIndividuals.remove(x);
				existingIndividuals.add(x);
			});
			if (messageHandler != null && newIndividuals.size() > 0) {
				messageHandler.onNewFriendsFound(newIndividuals);
			}
		}
		// chatroom
		if (chatRooms.size() > 0) {
			Set<Contact> existingChatRooms = cacheService.getChatRooms();
			Set<Contact> newChatRooms = new HashSet<>();
			Set<Contact> modifiedChatRooms = new HashSet<>();
			for (Contact chatRoom : chatRooms) {
				if (existingChatRooms.contains(chatRoom)) {
					modifiedChatRooms.add(chatRoom);
				} else {
					newChatRooms.add(chatRoom);
				}
			}
			existingChatRooms.addAll(newChatRooms);
			if (messageHandler != null && newChatRooms.size() > 0) {
				messageHandler.onNewChatRoomsFound(newChatRooms);
			}
			for (Contact chatRoom : modifiedChatRooms) {
				Contact existingChatRoom = existingChatRooms.stream().filter(x -> x.getUserName().equals(chatRoom.getUserName()))
						.findFirst().orElse(null);
				if (existingChatRoom == null) {
					continue;
				}
				existingChatRooms.remove(existingChatRoom);
				existingChatRooms.add(chatRoom);
				if (messageHandler != null) {
					Set<ChatRoomMember> oldMembers = existingChatRoom.getMemberList();
					Set<ChatRoomMember> newMembers = chatRoom.getMemberList();
					Set<ChatRoomMember> joined = newMembers.stream().filter(x -> !oldMembers.contains(x))
							.collect(Collectors.toSet());
					Set<ChatRoomMember> left = oldMembers.stream().filter(x -> !newMembers.contains(x))
							.collect(Collectors.toSet());
					if (joined.size() > 0 || left.size() > 0) {
						messageHandler.onChatRoomMembersChanged(chatRoom, joined, left);
					}
				}
			}
		}
		if (mediaPlatforms.size() > 0) {
			// media platform
			Set<Contact> existingPlatforms = cacheService.getMediaPlatforms();
			Set<Contact> newMediaPlatforms = existingPlatforms.stream().filter(x -> !existingPlatforms.contains(x))
					.collect(Collectors.toSet());
			mediaPlatforms.forEach(x -> {
				existingPlatforms.remove(x);
				existingPlatforms.add(x);
			});
			if (messageHandler != null && newMediaPlatforms.size() > 0) {
				messageHandler.onNewMediaPlatformsFound(newMediaPlatforms);
			}
		}
	}

	private void onContactsDeleted(Set<Contact> contacts) {
		Set<Contact> individuals = new HashSet<>();
		Set<Contact> chatRooms = new HashSet<>();
		Set<Contact> mediaPlatforms = new HashSet<>();
		for (Contact contact : contacts) {
			if (WechatUtils.isIndividual(contact)) {
				individuals.add(contact);
				cacheService.getIndividuals().remove(contact);
			} else if (WechatUtils.isChatRoom(contact)) {
				chatRooms.add(contact);
				cacheService.getChatRooms().remove(contact);
			} else if (WechatUtils.isMediaPlatform(contact)) {
				mediaPlatforms.add(contact);
				cacheService.getMediaPlatforms().remove(contact);
			}
		}
		if (messageHandler != null) {
			if (individuals.size() > 0) {
				messageHandler.onFriendsDeleted(individuals);
			}
			if (chatRooms.size() > 0) {
				messageHandler.onChatRoomsDeleted(chatRooms);
			}
			if (mediaPlatforms.size() > 0) {
				messageHandler.onMediaPlatformsDeleted(mediaPlatforms);
			}
		}
	}
}
