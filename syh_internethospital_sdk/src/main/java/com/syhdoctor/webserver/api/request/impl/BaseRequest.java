package com.syhdoctor.webserver.api.request.impl;

import com.alibaba.fastjson.annotation.JSONField;

public class BaseRequest {

    private static final String ORGCODE = "WEBH019";
    private static final String ORGNAME = "银川山屿海互联网医院";

    public BaseRequest() {
        this.setOrgName(ORGNAME);
        this.setOrgCode(ORGCODE);
    }

    /**
     * 医疗机构编码
     */
    @JSONField(name = "org_code")
    private String orgCode;


    /**
     * 医疗机构名称
     */
    @JSONField(name = "org_name")
    private String orgName;


    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

}
