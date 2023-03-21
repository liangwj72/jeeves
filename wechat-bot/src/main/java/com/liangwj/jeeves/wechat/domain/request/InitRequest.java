package com.liangwj.jeeves.wechat.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liangwj.jeeves.wechat.domain.request.component.BaseRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InitRequest {
    @JsonProperty
    private BaseRequest BaseRequest;

    public BaseRequest getBaseRequest() {
        return BaseRequest;
    }

    public void setBaseRequest(BaseRequest baseRequest) {
        BaseRequest = baseRequest;
    }
}