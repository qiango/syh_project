package com.syhdoctor.webserver.api.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class ConsultDocList {

    /**
     * 参会医师编码
     */
    @JSONField(name = "consult_doc_code")
    private String consultDocCode;

    /**
     * 参会医师姓名
     */
    @JSONField(name = "consult_doc_name")
    private String consultDocName;

    public String getConsultDocCode() {
        return consultDocCode;
    }

    public void setConsultDocCode(String consultDocCode) {
        this.consultDocCode = consultDocCode;
    }

    public String getConsultDocName() {
        return consultDocName;
    }

    public void setConsultDocName(String consultDocName) {
        this.consultDocName = consultDocName;
    }
}
