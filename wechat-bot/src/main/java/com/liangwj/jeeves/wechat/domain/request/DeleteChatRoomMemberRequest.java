package com.liangwj.jeeves.wechat.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liangwj.jeeves.wechat.domain.request.component.BaseRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteChatRoomMemberRequest {

    @JsonProperty
    private BaseRequest BaseRequest;
    @JsonProperty
    private String ChatRoomName;
    @JsonProperty
    private String DelMemberList;

    public BaseRequest getBaseRequest() {
        return BaseRequest;
    }

    public void setBaseRequest(BaseRequest baseRequest) {
        BaseRequest = baseRequest;
    }

    public String getChatRoomName() {
        return ChatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        ChatRoomName = chatRoomName;
    }

    public String getDelMemberList() {
        return DelMemberList;
    }

    public void setDelMemberList(String delMemberList) {
        DelMemberList = delMemberList;
    }
}
