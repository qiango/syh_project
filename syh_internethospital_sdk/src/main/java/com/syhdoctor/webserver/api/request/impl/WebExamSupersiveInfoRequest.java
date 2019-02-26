package com.syhdoctor.webserver.api.request.impl;

import com.alibaba.fastjson.annotation.JSONField;
import com.syhdoctor.webserver.api.request.IRequest;
import com.syhdoctor.webserver.api.response.BaseResponse;

/**
 * 在线检查监管信息
 */
public class WebExamSupersiveInfoRequest extends BaseRequest implements IRequest<BaseResponse> {
    @Override
    public String getApiName() {
        return "up_web_exam_supervise_info";
    }

    @Override
    public Class<BaseResponse> getResponseClass() {
        return BaseResponse.class;
    }

    @Override
    public void Validate() {

    }


    /**
     * 检查申请时间
     */
    @JSONField(name = "exam_apply_time")
    private String examApplyTime;

    /**
     * 检查类型编码
     */
    @JSONField(name = "exam_type_code")
    private String examTypeCode;

    /**
     * 检查类型名称
     */
    @JSONField(name = "exam_type_name")
    private String examTypeName;

    /**
     * 检查部位
     */
    @JSONField(name = "exam_part")
    private String examPart;

    /**
     * 检查医疗机构编码
     */
    @JSONField(name = "exam_org_code")
    private String examOrgCode;


    /**
     * 检查医疗机构名称
     */
    @JSONField(name = "exam_org_name")
    private String examOrgName;

    /**
     * 申请医师编码
     */
    @JSONField(name = "apply_doc_code")
    private String applyDocCode;

    /**
     * 申请医师姓名
     */
    @JSONField(name = "apply_doc_name")
    private String applyDocName;

    /**
     * 费用
     */
    @JSONField(name = "price")
    private String price;

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


    public String getExamApplyTime() {
        return examApplyTime;
    }

    public void setExamApplyTime(String examApplyTime) {
        this.examApplyTime = examApplyTime;
    }

    public String getExamTypeCode() {
        return examTypeCode;
    }

    public void setExamTypeCode(String examTypeCode) {
        this.examTypeCode = examTypeCode;
    }

    public String getExamTypeName() {
        return examTypeName;
    }

    public void setExamTypeName(String examTypeName) {
        this.examTypeName = examTypeName;
    }

    public String getExamPart() {
        return examPart;
    }

    public void setExamPart(String examPart) {
        this.examPart = examPart;
    }

    public String getExamOrgCode() {
        return examOrgCode;
    }

    public void setExamOrgCode(String examOrgCode) {
        this.examOrgCode = examOrgCode;
    }

    public String getExamOrgName() {
        return examOrgName;
    }

    public void setExamOrgName(String examOrgName) {
        this.examOrgName = examOrgName;
    }

    public String getApplyDocCode() {
        return applyDocCode;
    }

    public void setApplyDocCode(String applyDocCode) {
        this.applyDocCode = applyDocCode;
    }

    public String getApplyDocName() {
        return applyDocName;
    }

    public void setApplyDocName(String applyDocName) {
        this.applyDocName = applyDocName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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
}
