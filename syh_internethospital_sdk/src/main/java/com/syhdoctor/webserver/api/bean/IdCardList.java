package com.syhdoctor.webserver.api.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class IdCardList {
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    @JSONField(name = "id_card")
    private String idCard;
}
