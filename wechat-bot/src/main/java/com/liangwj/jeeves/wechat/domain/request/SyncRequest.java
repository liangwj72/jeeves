package com.liangwj.jeeves.wechat.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liangwj.jeeves.wechat.domain.request.component.BaseRequest;
import com.liangwj.jeeves.wechat.domain.shared.SyncKey;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncRequest {
    @JsonProperty
    private BaseRequest BaseRequest;
    @JsonProperty
    private long rr;
    @JsonProperty
    private SyncKey SyncKey;

    public BaseRequest getBaseRequest() {
        return BaseRequest;
    }

    public void setBaseRequest(BaseRequest baseRequest) {
        BaseRequest = baseRequest;
    }

    public long getRr() {
        return rr;
    }

    public void setRr(long rr) {
        this.rr = rr;
    }

    public SyncKey getSyncKey() {
        return SyncKey;
    }

    public void setSyncKey(SyncKey syncKey) {
        SyncKey = syncKey;
    }
}
