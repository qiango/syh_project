package com.syhdoctor.webserver.api.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class CertDocPracList {

    public String getCertDocPrac() {
        return certDocPrac;
    }

    public void setCertDocPrac(String certDocPrac) {
        this.certDocPrac = certDocPrac;
    }

    @JSONField(name = "cert_doc_prac")
    private String certDocPrac;
}
