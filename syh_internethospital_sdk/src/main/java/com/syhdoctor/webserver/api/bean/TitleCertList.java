package com.syhdoctor.webserver.api.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class TitleCertList {


    public String getTitleCert() {
        return titleCert;
    }

    public void setTitleCert(String titleCert) {
        this.titleCert = titleCert;
    }

    @JSONField(name = "title_cert")
    private String titleCert;
}
