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
 * 医疗安全监管数据上传接口
 */
public class MedicalSafetyInfoRequest extends BaseRequest implements IRequest<BaseResponse> {
    private static Logger log = LoggerFactory.getLogger(PrescriptionRequest.class);

    @Override
    public String getApiName() {
        return "up_medical_safety_info";
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
    private String orgCode;
    /**
     * 医疗机构名称
     */
    @JSONField(name = "org_name")
    private String orgName;
    /**
     * 医师编码
     */
    @JSONField(name = "in_doc_code")
    private String inDocCode;
    /**
     * 医师姓名
     */
    @JSONField(name = "doc_name")
    public String docName;
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
     * 患者联系方式
     */
    @JSONField(name = "pt_tel")
    public String ptTel;
    /**
     * 诊疗科室编码
     */
    @JSONField(name = "dept_code")
    public String deptCode;
    /**
     * 诊疗科室名称
     */
    @JSONField(name = "dept_name")
    public String deptName;
    /**
     * 疾病编码
     */
    @JSONField(name = "diseases_code")
    public String diseasesCode;
    /**
     * 疾病名称
     */
    @JSONField(name = "diseases_name")
    public String diseasesName;
    /**
     * 床号
     */
    @JSONField(name = "bed_no")
    public String bedNo;
    /**
     * 住院号
     */
    @JSONField(name = "in_hospital_no")
    public String inHospitalNo;
    /**
     * 诊疗时间
     */
    @JSONField(name = "visit_time")
    public String visitTime;
    /**
     * 责任人联系方式
     */
    @JSONField(name = "tel")
    public String tel;
    /**
     * 事件编号
     */
    @JSONField(name = "event_id")
    public String eventId;
    /**
     * 事件发生日期
     */
    @JSONField(name = "date")
    public String date;
    /**
     * 事件分类
     */
    @JSONField(name = "trouble")
    public String trouble;
    /**
     * 具体事件类型
     */
    @JSONField(name = "eventType")
    public String eventType;
    /**
     * 具体事件名称
     */
    @JSONField(name = "eventName")
    public String eventName;
    /**
     * 事件发生场所
     */
    @JSONField(name = "eventPlace")
    public String eventPlace;
    /**
     * 不良后果
     */
    @JSONField(name = "adverseConsequences")
    public String adverseConsequences;
    /**
     * 事件描述
     */
    @JSONField(name = "describe")
    public String describe;
    /**
     * 不良事件类别
     */
    @JSONField(name = "adverseCategory")
    public String adverseCategory;
    /**
     * 不良事件处理情况
     */
    @JSONField(name = "treatment")
    public String treatment;
    /**
     * 不良事件等级
     */
    @JSONField(name = "eventLevel")
    public String eventLevel;
    /**
     * 不良事件原因
     */
    @JSONField(name = "eventReason")
    public String eventReason;
    /**
     * 主管部门意见陈述
     */
    @JSONField(name = "competentDEPTopinion")
    public String competentDEPTopinion;
    /**
     * 持续改进措施
     */
    @JSONField(name = "improvingMeasures")
    public String improvingMeasures;

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

    public String getPtTel() {
        return ptTel;
    }

    public void setPtTel(String ptTel) {
        this.ptTel = ptTel;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDiseasesCode() {
        return diseasesCode;
    }

    public void setDiseasesCode(String diseasesCode) {
        this.diseasesCode = diseasesCode;
    }

    public String getDiseasesName() {
        return diseasesName;
    }

    public void setDiseasesName(String diseasesName) {
        this.diseasesName = diseasesName;
    }

    public String getBedNo() {
        return bedNo;
    }

    public void setBedNo(String bedNo) {
        this.bedNo = bedNo;
    }

    public String getInHospitalNo() {
        return inHospitalNo;
    }

    public void setInHospitalNo(String inHospitalNo) {
        this.inHospitalNo = inHospitalNo;
    }

    public String getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(String visitTime) {
        this.visitTime = visitTime;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTrouble() {
        return trouble;
    }

    public void setTrouble(String trouble) {
        this.trouble = trouble;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventPlace() {
        return eventPlace;
    }

    public void setEventPlace(String eventPlace) {
        this.eventPlace = eventPlace;
    }

    public String getAdverseConsequences() {
        return adverseConsequences;
    }

    public void setAdverseConsequences(String adverseConsequences) {
        this.adverseConsequences = adverseConsequences;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getAdverseCategory() {
        return adverseCategory;
    }

    public void setAdverseCategory(String adverseCategory) {
        this.adverseCategory = adverseCategory;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getEventLevel() {
        return eventLevel;
    }

    public void setEventLevel(String eventLevel) {
        this.eventLevel = eventLevel;
    }

    public String getEventReason() {
        return eventReason;
    }

    public void setEventReason(String eventReason) {
        this.eventReason = eventReason;
    }

    public String getCompetentDEPTopinion() {
        return competentDEPTopinion;
    }

    public void setCompetentDEPTopinion(String competentDEPTopinion) {
        this.competentDEPTopinion = competentDEPTopinion;
    }

    public String getImprovingMeasures() {
        return improvingMeasures;
    }

    public void setImprovingMeasures(String improvingMeasures) {
        this.improvingMeasures = improvingMeasures;
    }
}
