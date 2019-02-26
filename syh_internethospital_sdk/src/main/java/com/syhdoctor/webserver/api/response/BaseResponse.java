package com.syhdoctor.webserver.api.response;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    /**
     * 消息确认ID
     */
    @SerializedName("msgid")
    private String msgId;


    /**
     * 消息接收状态，其中1表示接收成功，0表示接收失败。
     */
    private String status;

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    private String errorType;


    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
