package com.syhdoctor.webserver.api.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class ConsultOrgList {


    /**
     * 会诊医疗机构编码
     */
    @JSONField(name = "consult_org_code")
    private String consultOrgCode;

    /**
     * 会诊医疗机构名称
     */
    @JSONField(name = "consult_org_name")
    private String consultOrgName;


    /**
     * 参会医生列表
     */
    @JSONField(name = "consult_doc_list")
    private List<ConsultDocList> consultDocList;
}
