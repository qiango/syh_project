package com.syhdoctor.webserver.thirdparty.mongodb.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "share_collection")//文件名
public class Share implements Serializable {

    @Id
    private long id;

    private long pid; //上级分享id

    private int shareType; //分享类型

    private long shareTypeId; //分享类型对应的id

    private long shareUserId; //分享人id

    private int usertype; //1:分享人分享  2：被分享人点击 3：分享人转换为用户

    private long openId; //openid表的id

    public long getOpenId() {
        return openId;
    }

    public void setOpenId(long openId) {
        this.openId = openId;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getShareType() {
        return shareType;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }

    public long getShareTypeId() {
        return shareTypeId;
    }

    public void setShareTypeId(long shareTypeId) {
        this.shareTypeId = shareTypeId;
    }

    public long getShareUserId() {
        return shareUserId;
    }

    public void setShareUserId(long shareUserId) {
        this.shareUserId = shareUserId;
    }
}
