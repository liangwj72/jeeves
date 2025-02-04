package com.liangwj.jeeves.wechat.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liangwj.jeeves.wechat.domain.request.component.BaseRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusNotifyRequest {
    @JsonProperty
    private BaseRequest BaseRequest;
    @JsonProperty
    private String ClientMsgId;
    @JsonProperty
    private int Code;
    @JsonProperty
    private String FromUserName;
    @JsonProperty
    private String ToUserName;

    public BaseRequest getBaseRequest() {
        return BaseRequest;
    }

    public void setBaseRequest(BaseRequest baseRequest) {
        BaseRequest = baseRequest;
    }

    public String getClientMsgId() {
        return ClientMsgId;
    }

    public void setClientMsgId(String clientMsgId) {
        ClientMsgId = clientMsgId;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }
}
