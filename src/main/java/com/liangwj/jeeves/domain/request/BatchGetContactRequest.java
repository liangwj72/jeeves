package com.liangwj.jeeves.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liangwj.jeeves.domain.request.component.BaseRequest;
import com.liangwj.jeeves.domain.shared.ChatRoomDescription;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchGetContactRequest {
    @JsonProperty
    private BaseRequest BaseRequest;
    @JsonProperty
    private int Count;
    @JsonProperty
    private ChatRoomDescription[] List;

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

    public ChatRoomDescription[] getList() {
        return List;
    }

    public void setList(ChatRoomDescription[] list) {
        List = list;
    }
}
