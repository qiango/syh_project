package com.syhdoctor.webserver.api.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class DocMultiSitedLicRecordList {
    public String getDocMultiSitedLicRecord() {
        return docMultiSitedLicRecord;
    }

    public void setDocMultiSitedLicRecord(String docMultiSitedLicRecord) {
        this.docMultiSitedLicRecord = docMultiSitedLicRecord;
    }

    @JSONField(name = "doc_multi_sited_lic_record")
    private String docMultiSitedLicRecord;

}
