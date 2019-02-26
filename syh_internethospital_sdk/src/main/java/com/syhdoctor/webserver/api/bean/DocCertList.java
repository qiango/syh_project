package com.syhdoctor.webserver.api.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class DocCertList {
    public String getDocCert() {
        return docCert;
    }

    public void setDocCert(String docCert) {
        this.docCert = docCert;
    }

    @JSONField(name = "doc_cert")
    private String docCert;
}
