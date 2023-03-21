package com.liangwj.jeeves.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.liangwj.jeeves.domain.response.component.WechatHttpResponseBase;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteChatRoomMemberResponse extends WechatHttpResponseBase {
}
