package com.liangwj.jeeves.wechat.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liangwj.jeeves.wechat.domain.response.component.WechatHttpResponseBase;
import com.liangwj.jeeves.wechat.domain.shared.Contact;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetContactResponse extends WechatHttpResponseBase {
    @JsonProperty
    private int MemberCount;
    @JsonProperty
    private Set<Contact> MemberList;
    @JsonProperty
    private long Seq;

    public int getMemberCount() {
        return MemberCount;
    }

    public void setMemberCount(int memberCount) {
        MemberCount = memberCount;
    }

    public Set<Contact> getMemberList() {
        return MemberList;
    }

    public void setMemberList(Set<Contact> memberList) {
        MemberList = memberList;
    }

    public long getSeq() {
        return Seq;
    }

    public void setSeq(long seq) {
        Seq = seq;
    }
}