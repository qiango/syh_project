package com.syhdoctor.webserver.api.request.impl;

import com.alibaba.fastjson.annotation.JSONField;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.api.BaseException;
import com.syhdoctor.webserver.api.request.IRequest;
import com.syhdoctor.webserver.api.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 特殊病例监管数据上传接口
 */
public class SpecialCaseReportedInfoRequest extends BaseRequest implements IRequest<BaseResponse> {
    private static Logger log = LoggerFactory.getLogger(PrescriptionRequest.class);

    @Override
    public String getApiName() {
        return "up_special_case_reported_info";
    }

    @Override
    public Class<BaseResponse> getResponseClass() {
        return BaseResponse.class;
    }

    @Override
    public void Validate() {
        Field[] fields = this.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Object object = field.get(this);
                if (object instanceof String) {
                    if (StrUtil.isEmpty((String) object)) {
                        log.info("string" + field.getName() + "");
                        throw new BaseException("" + field.getName() + ":null");
                    }
                } else if (object instanceof List) {
                    List temp = (List) object;
                    if (temp.size() == 0) {
                        log.info("list:" + field.getName() + "");
                        throw new BaseException("" + field.getName() + ":null");
                    }
                } else if (object == null) {
                    log.info("object:" + field.getName() + "");
                    throw new BaseException("" + field.getName() + ":null");
                }
            }
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 医疗机构编码
     */
    @JSONField(name = "org_code")
    public String orgCode;
    /**
     * 医疗机构名称
     */
    @JSONField(name = "org_name")
    public String orgName;
    /**
     * 特殊病例编号
     */
    @JSONField(name = "special_case_code")
    public String specialCaseCode;
    /**
     * 特殊病例类型
     */
    @JSONField(name = "special_case_type")
    public String specialCaseType;
    /**
     * 特殊病例具体分类
     */
    @JSONField(name = "special_case_detail")
    public String specialCaseDetail;
    /**
     * 特殊病例处理结果
     */
    @JSONField(name = "special_case_process")
    public String specialCaseProcess;
    /**
     * 特殊病例名称
     */
    @JSONField(name = "special_case_name")
    public String specialCaseName;
    /**
     * 医师编码
     */
    @JSONField(name = "in_doc_code")
    public String inDocCode;
    /**
     * 医师姓名
     */
    @JSONField(name = "doc_name")
    public String docName;
    /**
     * 医师联系方式
     */
    @JSONField(name = "tel")
    public String tel;
    /**
     * 上报时间
     */
    @JSONField(name = "date")
    public String date;
    /**
     * 患者ID
     */
    @JSONField(name = "pt_id")
    public String ptId;
    /**
     * 患者姓名
     */
    @JSONField(name = "pt_no")
    public String ptNo;
    /**
     * 患儿家长姓名
     */
    @JSONField(name = "pt_parent_no")
    public String ptParentNo;
    /**
     * 患者有效证件号
     */
    @JSONField(name = "id_no")
    public String idNo;
    /**
     * 患者性别编码
     */
    @JSONField(name = "ge_code")
    public String geCode;
    /**
     * 患者性别名称
     */
    @JSONField(name = "ge_name")
    public String geName;
    /**
     * 患者出生日期
     */
    @JSONField(name = "pt_birthdate")
    public String ptBirthdate;
    /**
     * 患者年龄
     */
    @JSONField(name = "pt_age")
    public String ptAge;
    /**
     * 患者所在地区
     */
    @JSONField(name = "pt_district")
    public String ptDistrict;
    /**
     * 患者工作单位（学校）
     */
    @JSONField(name = "pt_co_school")
    public String ptCoSchool;
    /**
     * 患者联系方式
     */
    @JSONField(name = "pt_tel")
    public String ptTel;
    /**
     * 患者所属
     */
    @JSONField(name = "pt_hukou")
    public String ptHukou;
    /**
     * 患者住址
     */
    @JSONField(name = "pt_address")
    public String ptAddress;
    /**
     * 患者职业
     */
    @JSONField(name = "pt_profession")
    public String ptProfession;
    /**
     * 患者发病日期
     */
    @JSONField(name = "pt_onset_date")
    public String ptOnsetDate;
    /**
     * 诊断日期
     */
    @JSONField(name = "pt_outcome_time")
    public String ptOutcomeTime;

    @Override
    public String getOrgCode() {
        return orgCode;
    }

    @Override
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Override
    public String getOrgName() {
        return orgName;
    }

    @Override
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getSpecialCaseCode() {
        return specialCaseCode;
    }

    public void setSpecialCaseCode(String specialCaseCode) {
        this.specialCaseCode = specialCaseCode;
    }

    public String getSpecialCaseType() {
        return specialCaseType;
    }

    public void setSpecialCaseType(String specialCaseType) {
        this.specialCaseType = specialCaseType;
    }

    public String getSpecialCaseDetail() {
        return specialCaseDetail;
    }

    public void setSpecialCaseDetail(String specialCaseDetail) {
        this.specialCaseDetail = specialCaseDetail;
    }

    public String getSpecialCaseProcess() {
        return specialCaseProcess;
    }

    public void setSpecialCaseProcess(String specialCaseProcess) {
        this.specialCaseProcess = specialCaseProcess;
    }

    public String getSpecialCaseName() {
        return specialCaseName;
    }

    public void setSpecialCaseName(String specialCaseName) {
        this.specialCaseName = specialCaseName;
    }

    public String getInDocCode() {
        return inDocCode;
    }

    public void setInDocCode(String inDocCode) {
        this.inDocCode = inDocCode;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPtId() {
        return ptId;
    }

    public void setPtId(String ptId) {
        this.ptId = ptId;
    }

    public String getPtNo() {
        return ptNo;
    }

    public void setPtNo(String ptNo) {
        this.ptNo = ptNo;
    }

    public String getPtParentNo() {
        return ptParentNo;
    }

    public void setPtParentNo(String ptParentNo) {
        this.ptParentNo = ptParentNo;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
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

    public String getPtBirthdate() {
        return ptBirthdate;
    }

    public void setPtBirthdate(String ptBirthdate) {
        this.ptBirthdate = ptBirthdate;
    }

    public String getPtAge() {
        return ptAge;
    }

    public void setPtAge(String ptAge) {
        this.ptAge = ptAge;
    }

    public String getPtDistrict() {
        return ptDistrict;
    }

    public void setPtDistrict(String ptDistrict) {
        this.ptDistrict = ptDistrict;
    }

    public String getPtCoSchool() {
        return ptCoSchool;
    }

    public void setPtCoSchool(String ptCoSchool) {
        this.ptCoSchool = ptCoSchool;
    }

    public String getPtTel() {
        return ptTel;
    }

    public void setPtTel(String ptTel) {
        this.ptTel = ptTel;
    }

    public String getPtHukou() {
        return ptHukou;
    }

    public void setPtHukou(String ptHukou) {
        this.ptHukou = ptHukou;
    }

    public String getPtAddress() {
        return ptAddress;
    }

    public void setPtAddress(String ptAddress) {
        this.ptAddress = ptAddress;
    }

    public String getPtProfession() {
        return ptProfession;
    }

    public void setPtProfession(String ptProfession) {
        this.ptProfession = ptProfession;
    }

    public String getPtOnsetDate() {
        return ptOnsetDate;
    }

    public void setPtOnsetDate(String ptOnsetDate) {
        this.ptOnsetDate = ptOnsetDate;
    }

    public String getPtOutcomeTime() {
        return ptOutcomeTime;
    }

    public void setPtOutcomeTime(String ptOutcomeTime) {
        this.ptOutcomeTime = ptOutcomeTime;
    }
    /*

患者职业	pt_profession
患者发病日期	pt_onset_date
诊断日期	pt_outcome_time
     */










}
