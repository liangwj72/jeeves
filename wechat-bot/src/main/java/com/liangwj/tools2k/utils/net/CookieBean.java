package com.liangwj.tools2k.utils.net;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.http.cookie.Cookie;
import org.apache.http.util.Args;

import com.alibaba.fastjson2.annotation.JSONField;

public class CookieBean implements Cookie {

	private String name;
	private String value;
	private String comment;
	private String commentURL;
	private Date expiryDate;
	private boolean persistent;
	private String domain;
	private String path;
	private int[] ports;
	private boolean secure;
	private int version;

	public CookieBean() {
	}

	public CookieBean(Cookie src) {
		this.name = src.getName();
		this.value = src.getValue();
		this.comment = src.getComment();
		this.commentURL = src.getCommentURL();
		this.persistent = src.isPersistent();
		this.domain = src.getDomain();
		this.path = src.getPath();
		this.ports = src.getPorts();
		this.secure = src.isSecure();
		this.version = src.getVersion();

		if (src.getExpiryDate() != null) {
			// 如果时间太大，保存到json时会出问题，所以我们限制一下最大时间
			this.expiryDate = src.getExpiryDate();
			long maxTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365 * 100); // 100年
			if (this.expiryDate.getTime() > maxTime) {
				this.expiryDate = new Date(maxTime);
			}
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String getCommentURL() {
		return commentURL;
	}

	public void setCommentURL(String commentURL) {
		this.commentURL = commentURL;
	}

	@Override
	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Override
	public boolean isPersistent() {
		return persistent;
	}

	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	@Override
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Override
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public int[] getPorts() {
		return ports;
	}

	public void setPorts(int[] ports) {
		this.ports = ports;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	@JSONField(serialize = false)
	public boolean isExpired(Date date) {
		Args.notNull(date, "Date");
		return (this.expiryDate != null
				&& expiryDate.getTime() <= date.getTime());
	}

	@Override
	public boolean isSecure() {
		return this.secure;
	}

	@Override
	public int getVersion() {
		return this.version;
	}

}
