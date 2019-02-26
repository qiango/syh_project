package com.syhdoctor.webserver.api.request.impl;

import com.alibaba.fastjson.annotation.JSONField;
import com.syhdoctor.webserver.api.bean.RecordRules;
import com.syhdoctor.webserver.api.request.IRequest;
import com.syhdoctor.webserver.api.response.BaseResponse;

import java.util.List;

/**
 * 互联网医院基本信息
 */
public class WebHospitalBasicInfoRequest implements IRequest<BaseResponse> {
    @Override
    public String getApiName() {
        return "up_hos_basic_info";
    }

    @Override
    public Class<BaseResponse> getResponseClass() {
        return BaseResponse.class;
    }

    @Override
    public void Validate() {

    }

    /**
     * 医疗机构联系电话
     */
    @JSONField(name = "org_tel")
    private String orgTel;

    /**
     * 医疗机构负责人姓名
     */
    @JSONField(name = "org_principa_name")
    private String orgPrincipaPame;
    /**
     * 医疗机构负责人电话
     */
    @JSONField(name = "org_principa_tel")
    private String orgPrincipaTel;
    /**
     * 机构简介
     */
    @JSONField(name = "org_comment")
    private String orgComment;
    /**
     * 科室简介
     */
    @JSONField(name = "dept_comment")
    private String deptComment;
    /**
     * 备案信息表
     */
    @JSONField(name = "record_prove")
    private String recordProve;
    /**
     * 电子签章
     */
    @JSONField(name = " hos_digital_sign")
    private String hosDigitalSign;
    /**
     * 可行性报告
     */
    @JSONField(name = "record_Feasibility")
    private String recordFeasibility;
    /**
     * 医疗机构规章制度列表
     */
    @JSONField(name = "record_rules_list")
    private List<RecordRules> recordRulesList;
    /**
     * 网络拓扑图
     */
    @JSONField(name = "record_topology")
    private String recordTopology;
    /**
     * 其他资料
     */
    @JSONField(name = "record_Other")
    private String recordOther;
    /**
     * 互联网病案管理制度
     */
    @JSONField(name = "case_management")
    private String caseManagement;
    /**
     * 患者隐私方案
     */
    @JSONField(name = "privacy_solution")
    private String privacySolution;

    public String getOrgTel() {
        return orgTel;
    }

    public void setOrgTel(String orgTel) {
        this.orgTel = orgTel;
    }

    public String getOrgPrincipaPame() {
        return orgPrincipaPame;
    }

    public void setOrgPrincipaPame(String orgPrincipaPame) {
        this.orgPrincipaPame = orgPrincipaPame;
    }

    public String getOrgPrincipaTel() {
        return orgPrincipaTel;
    }

    public void setOrgPrincipaTel(String orgPrincipaTel) {
        this.orgPrincipaTel = orgPrincipaTel;
    }

    public String getOrgComment() {
        return orgComment;
    }

    public void setOrgComment(String orgComment) {
        this.orgComment = orgComment;
    }

    public String getDeptComment() {
        return deptComment;
    }

    public void setDeptComment(String deptComment) {
        this.deptComment = deptComment;
    }

    public String getRecordProve() {
        return recordProve;
    }

    public void setRecordProve(String recordProve) {
        this.recordProve = recordProve;
    }

    public String getHosDigitalSign() {
        return hosDigitalSign;
    }

    public void setHosDigitalSign(String hosDigitalSign) {
        this.hosDigitalSign = hosDigitalSign;
    }

    public String getRecordFeasibility() {
        return recordFeasibility;
    }

    public void setRecordFeasibility(String recordFeasibility) {
        this.recordFeasibility = recordFeasibility;
    }

    public List<RecordRules> getRecordRulesList() {
        return recordRulesList;
    }

    public void setRecordRulesList(List<RecordRules> recordRulesList) {
        this.recordRulesList = recordRulesList;
    }

    public String getRecordTopology() {
        return recordTopology;
    }

    public void setRecordTopology(String recordTopology) {
        this.recordTopology = recordTopology;
    }

    public String getRecordOther() {
        return recordOther;
    }

    public void setRecordOther(String recordOther) {
        this.recordOther = recordOther;
    }

    public String getCaseManagement() {
        return caseManagement;
    }

    public void setCaseManagement(String caseManagement) {
        this.caseManagement = caseManagement;
    }

    public String getPrivacySolution() {
        return privacySolution;
    }

    public void setPrivacySolution(String privacySolution) {
        this.privacySolution = privacySolution;
    }
}
