package com.liangwj.jeeves.wechat.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.liangwj.jeeves.wechat.domain.response.component.WechatHttpResponseBase;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteChatRoomMemberResponse extends WechatHttpResponseBase {
}
