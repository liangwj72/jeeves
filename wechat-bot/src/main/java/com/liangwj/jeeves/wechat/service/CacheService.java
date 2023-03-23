package com.liangwj.jeeves.wechat.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.liangwj.jeeves.wechat.domain.request.component.BaseRequest;
import com.liangwj.jeeves.wechat.domain.shared.Contact;
import com.liangwj.jeeves.wechat.domain.shared.Owner;
import com.liangwj.jeeves.wechat.domain.shared.SyncCheckKey;
import com.liangwj.jeeves.wechat.domain.shared.SyncKey;
import com.liangwj.jeeves.wechat.utils.DeviceIdGenerator;

@Component
public class CacheService {

	public void reset() {
		this.hostUrl = null;
		this.syncUrl = null;
		this.fileUrl = null;
		this.passTicket = null;
		this.baseRequest = null;
		this.owner = null;
		this.syncCheckKey = null;
		this.syncKey = null;
		this.sKey = null;
		this.uuid = null;
		this.uin = null;
		this.sid = null;
		this.individuals.clear();
		this.mediaPlatforms.clear();
		this.chatRooms.clear();
	}

	private boolean alive = false;
	private String uuid;
	private String hostUrl;
	private String syncUrl;
	private String fileUrl;
	private String passTicket;
	private BaseRequest baseRequest;
	private Owner owner;
	private SyncKey syncKey;
	private SyncCheckKey syncCheckKey;
	private String sKey;
	private String uin;
	private String sid;

	private final Set<Contact> individuals = new HashSet<>();
	private final Set<Contact> mediaPlatforms = new HashSet<>();
	private final Set<Contact> chatRooms = new HashSet<>();

	private final Set<String> contactNamesWithUnreadMessage = new HashSet<>();

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public String getHostUrl() {
		return hostUrl;
	}

	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}

	public String getPassTicket() {
		return passTicket;
	}

	public void setPassTicket(String passTicket) {
		this.passTicket = passTicket;
	}

	public BaseRequest getBaseRequest() {
		baseRequest.setDeviceID(DeviceIdGenerator.generate());
		return baseRequest;
	}

	public void setBaseRequest(BaseRequest baseRequest) {
		this.baseRequest = baseRequest;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public SyncKey getSyncKey() {
		return syncKey;
	}

	public void setSyncKey(SyncKey syncKey) {
		this.syncKey = syncKey;
	}

	public SyncCheckKey getSyncCheckKey() {
		return syncCheckKey;
	}

	public void setSyncCheckKey(SyncCheckKey syncCheckKey) {
		this.syncCheckKey = syncCheckKey;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getsKey() {
		return sKey;
	}

	public void setsKey(String sKey) {
		this.sKey = sKey;
	}

	public String getUin() {
		return uin;
	}

	public void setUin(String uin) {
		this.uin = uin;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public Set<Contact> getIndividuals() {
		return individuals;
	}

	public Set<Contact> getMediaPlatforms() {
		return mediaPlatforms;
	}

	public Set<Contact> getChatRooms() {
		return chatRooms;
	}

	public String getSyncUrl() {
		return syncUrl;
	}

	public void setSyncUrl(String syncUrl) {
		this.syncUrl = syncUrl;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public Set<String> getContactNamesWithUnreadMessage() {
		return contactNamesWithUnreadMessage;
	}

	/**
	 * 根据群id，查找群
	 * 
	 * @param username
	 *            群id
	 * @return
	 */
	public Contact getChatRoomById(String username) {
		if (StringUtils.hasText(username)) {
			for (Contact contact : chatRooms) {
				if (username.equals(contact.getUserName())) {
					return contact;
				}
			}
		}
		return null;
	}
}
