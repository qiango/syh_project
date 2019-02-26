package com.syhdoctor.webserver.api.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class RecordRules {
    /**
     * 互联网医院备案-医疗机构规章制度
     */
    @JSONField(name = "record_rules")
    private String recordRules;

    public String getRecordRules() {
        return recordRules;
    }

    public void setRecordRules(String recordRules) {
        this.recordRules = recordRules;
    }
}
