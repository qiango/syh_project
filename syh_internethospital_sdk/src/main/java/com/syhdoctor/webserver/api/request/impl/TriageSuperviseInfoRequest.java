package com.syhdoctor.webserver.api.request.impl;

import com.alibaba.fastjson.annotation.JSONField;
import com.syhdoctor.webserver.api.bean.IllDes;
import com.syhdoctor.webserver.api.request.IRequest;
import com.syhdoctor.webserver.api.response.BaseResponse;

import java.util.List;

/**
 * 分诊监管信息
 */
public class TriageSuperviseInfoRequest extends BaseRequest implements IRequest<BaseResponse> {

    @Override
    public String getApiName() {
        return "up_triage_supervise_info";
    }

    @Override
    public Class<BaseResponse> getResponseClass() {
        return BaseResponse.class;
    }

    @Override
    public void Validate() {

    }

    /**
     * 分诊时间
     */
    @JSONField(name = "triage_time")
    private String triageTime;

    /**
     * 分诊原因
     */

    @JSONField(name = "triage_reason")
    private String triageReason;

    /**
     * 接诊科室编码
     */
    @JSONField(name = "visit_dept_code")
    private String visitDeptCode;

    /**
     * 接诊科室名称
     */
    @JSONField(name = "visit_dept_name")
    private String visitDeptName;

    /**
     * 接诊医师编码
     */
    @JSONField(name = "visit_doc_code")
    private String visitDocCode;

    /**
     * 接诊医师姓名
     */
    @JSONField(name = "visit_doc_name")
    private String visitDocName;

    /**
     * 分诊目标科室编码
     */
    @JSONField(name = "des_dept_code")
    private String desDeptCode;

    /**
     * 分诊目标科室名称
     */
    @JSONField(name = "des_dept_name")
    private String desDeptName;

    /**
     * 分诊目标医师编码
     */
    @JSONField(name = "des_doc_code")
    private String desDocCode;

    /**
     * 分诊目标医师姓名
     */
    @JSONField(name = "des_doc_name")
    private String desDocName;

    /**
     * 申请分诊医师编码
     */
    @JSONField(name = "apply_doc_code")
    private String applyDocCode;

    /**
     * 申请分诊医师姓名
     */
    @JSONField(name = "apply_doc_name")
    private String applyDocName;

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
     * 出生日期
     */
    @JSONField(name = "birthday")
    private String birthDay;

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
     * 患者在线病例信息
     */
    @JSONField(name = "ill_des")
    private List<IllDes> illDes;

    /**
     * 患者所在地区
     */
    @JSONField(name = "pt_district")
    private String ptDistrict;

    public String getTriageReason() {
        return triageReason;
    }

    public void setTriageReason(String triageReason) {
        this.triageReason = triageReason;
    }

    public String getVisitDeptCode() {
        return visitDeptCode;
    }

    public void setVisitDeptCode(String visitDeptCode) {
        this.visitDeptCode = visitDeptCode;
    }

    public String getVisitDeptName() {
        return visitDeptName;
    }

    public void setVisitDeptName(String visitDeptName) {
        this.visitDeptName = visitDeptName;
    }

    public String getVisitDocCode() {
        return visitDocCode;
    }

    public void setVisitDocCode(String visitDocCode) {
        this.visitDocCode = visitDocCode;
    }

    public String getVisitDocName() {
        return visitDocName;
    }

    public void setVisitDocName(String visitDocName) {
        this.visitDocName = visitDocName;
    }

    public String getDesDeptCode() {
        return desDeptCode;
    }

    public void setDesDeptCode(String desDeptCode) {
        this.desDeptCode = desDeptCode;
    }

    public String getDesDeptName() {
        return desDeptName;
    }

    public void setDesDeptName(String desDeptName) {
        this.desDeptName = desDeptName;
    }

    public String getDesDocCode() {
        return desDocCode;
    }

    public void setDesDocCode(String desDocCode) {
        this.desDocCode = desDocCode;
    }

    public String getDesDocName() {
        return desDocName;
    }

    public void setDesDocName(String desDocName) {
        this.desDocName = desDocName;
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

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
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

    public List<IllDes> getIllDes() {
        return illDes;
    }

    public void setIllDes(List<IllDes> illDes) {
        this.illDes = illDes;
    }

    public String getPtDistrict() {
        return ptDistrict;
    }

    public void setPtDistrict(String ptDistrict) {
        this.ptDistrict = ptDistrict;
    }


}
