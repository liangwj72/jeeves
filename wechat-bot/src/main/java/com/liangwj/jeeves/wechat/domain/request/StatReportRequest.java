package com.liangwj.jeeves.wechat.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.liangwj.jeeves.wechat.domain.request.component.BaseRequest;
import com.liangwj.jeeves.wechat.domain.shared.StatReport;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatReportRequest {
    private BaseRequest BaseRequest;
    private int Count;
    private StatReport[] List;

    public BaseRequest getBaseRequest() {
        return BaseRequest;
    }

    public void setBaseRequest(BaseRequest baseRequest) {
        BaseRequest = baseRequest;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public StatReport[] getList() {
        return List;
    }

    public void setList(StatReport[] list) {
        List = list;
    }
}
