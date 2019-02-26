package com.syhdoctor.webserver.api.request.impl;

import com.alibaba.fastjson.annotation.JSONField;
import com.syhdoctor.webserver.api.bean.ConsultOrgList;
import com.syhdoctor.webserver.api.request.IRequest;
import com.syhdoctor.webserver.api.response.BaseResponse;

import java.util.List;

/**
 * 会诊监管数据上传接口
 */
public class ConsultationSupersiveInfoRequest extends BaseRequest implements IRequest<BaseResponse> {

    @Override
    public String getApiName() {
        return "up_consult_supervise_info";
    }

    @Override
    public Class<BaseResponse> getResponseClass() {
        return BaseResponse.class;
    }

    @Override
    public void Validate() {

    }

    /**
     * 会诊类型编码
     */
    @JSONField(name = "consult_type_code")
    private String consultTypeCode;

    /**
     * 会诊类型名称
     */
    @JSONField(name = "consult_type_name")
    private String consultTypeName;

    /**
     * 会诊原因
     */
    @JSONField(name = "consult_reason")
    private String consultReason;

    /**
     * 会诊时间
     */
    @JSONField(name = "consult_time")
    private String consultTime;

    /**
     * 会诊医疗机构列表
     */
    @JSONField(name = "consult_org_list")
    private List<ConsultOrgList> consultOrgList;

    /**
     * 患者ID
     */
    @JSONField(name = "pt_id")
    private String ptId;

    /**
     * 就诊号
     */
    @JSONField(name = "med_rd_no")
    private String medRdNo;

    /**
     * 就诊类别编码
     */
    @JSONField(name = "med_class_code")
    private String medClassCode;

    /**
     * 就诊类别名称
     */
    @JSONField(name = "med_class_name")
    private String medClassName;

    /**
     * 患者姓名
     */
    @JSONField(name = "pt_no")
    private String ptNo;

    /**
     * 性别编码
     */
    @JSONField(name = "ge_code")
    private String geCode;

    /**
     * 性别名称
     */
    @JSONField(name = "ge_name")
    private String geName;

    /**
     * 患者年龄
     */
    @JSONField(name = "pt_age")
    private String ptAge;

    /**
     * 患者出生日期
     */
    @JSONField(name = "pt_birthdate")
    private String ptBirthdate;

    /**
     * 身份证号
     */
    @JSONField(name = "id_no")
    private String idNo;

    /**
     * 患者手机号
     */
    @JSONField(name = "pt_tel")
    private String ptTel;

    /**
     * 患者所在地区
     */
    @JSONField(name = "pt_district")
    private String ptDistrict;

    /**
     * 会诊结论
     */
    @JSONField(name = "consult_comment")
    private String consultComment;


    public String getConsultTypeCode() {
        return consultTypeCode;
    }

    public void setConsultTypeCode(String consultTypeCode) {
        this.consultTypeCode = consultTypeCode;
    }

    public String getConsultTypeName() {
        return consultTypeName;
    }

    public void setConsultTypeName(String consultTypeName) {
        this.consultTypeName = consultTypeName;
    }

    public String getConsultReason() {
        return consultReason;
    }

    public void setConsultReason(String consultReason) {
        this.consultReason = consultReason;
    }

    public String getConsultTime() {
        return consultTime;
    }

    public void setConsultTime(String consultTime) {
        this.consultTime = consultTime;
    }

    public String getPtId() {
        return ptId;
    }

    public void setPtId(String ptId) {
        this.ptId = ptId;
    }

    public String getMedRdNo() {
        return medRdNo;
    }

    public void setMedRdNo(String medRdNo) {
        this.medRdNo = medRdNo;
    }

    public String getMedClassCode() {
        return medClassCode;
    }

    public void setMedClassCode(String medClassCode) {
        this.medClassCode = medClassCode;
    }

    public String getMedClassName() {
        return medClassName;
    }

    public void setMedClassName(String medClassName) {
        this.medClassName = medClassName;
    }

    public String getPtNo() {
        return ptNo;
    }

    public void setPtNo(String ptNo) {
        this.ptNo = ptNo;
    }

    public String getGeCode() {
        return geCode;
    }

    public void setGeCode(String geCode) {
        this.geCode = geCode;
    }

    public String getGeName() {
        return geName;
    }

    public void setGeName(String geName) {
        this.geName = geName;
    }

    public String getPtAge() {
        return ptAge;
    }

    public void setPtAge(String ptAge) {
        this.ptAge = ptAge;
    }

    public String getPtBirthdate() {
        return ptBirthdate;
    }

    public void setPtBirthdate(String ptBirthdate) {
        this.ptBirthdate = ptBirthdate;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getPtTel() {
        return ptTel;
    }

    public void setPtTel(String ptTel) {
        this.ptTel = ptTel;
    }

    public String getPtDistrict() {
        return ptDistrict;
    }

    public void setPtDistrict(String ptDistrict) {
        this.ptDistrict = ptDistrict;
    }

    public String getConsultComment() {
        return consultComment;
    }

    public void setConsultComment(String consultComment) {
        this.consultComment = consultComment;
    }
}
