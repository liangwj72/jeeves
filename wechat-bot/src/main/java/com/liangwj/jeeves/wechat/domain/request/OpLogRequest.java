package com.liangwj.jeeves.wechat.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liangwj.jeeves.wechat.domain.request.component.BaseRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpLogRequest {
    @JsonProperty
    private BaseRequest BaseRequest;
    @JsonProperty
    private int CmdId;
    @JsonProperty
    private String RemarkName;
    @JsonProperty
    private String UserName;

    public BaseRequest getBaseRequest() {
        return BaseRequest;
    }

    public void setBaseRequest(BaseRequest baseRequest) {
        BaseRequest = baseRequest;
    }

    public int getCmdId() {
        return CmdId;
    }

    public void setCmdId(int cmdId) {
        CmdId = cmdId;
    }

    public String getRemarkName() {
        return RemarkName;
    }

    public void setRemarkName(String remarkName) {
        RemarkName = remarkName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}