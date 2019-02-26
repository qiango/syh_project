package com.syhdoctor.webserver.api.request.impl;

import com.alibaba.fastjson.annotation.JSONField;
import com.syhdoctor.webserver.api.bean.Drug;
import com.syhdoctor.webserver.api.bean.PresPhotosList;
import com.syhdoctor.webserver.api.request.IRequest;
import com.syhdoctor.webserver.api.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 电子处方
 */
public class PrescriptionRequest extends BaseRequest implements IRequest<BaseResponse> {

    private static Logger log = LoggerFactory.getLogger(PrescriptionRequest.class);

    @Override
    public String getApiName() {
        return "up_pres";
    }

    @Override
    public Class<BaseResponse> getResponseClass() {
        return BaseResponse.class;
    }

    @Override
    public void Validate() {
       /* Field[] fields = this.getClass().getDeclaredFields();
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
                    *//*if (temp.size() == 0) {
                        log.info("list:" + field.getName() + "");
                        throw new BaseException("" + field.getName() + ":null");
                    }*//*
                } else if (object == null) {
                    log.info("object:" + field.getName() + "");
                    throw new BaseException("" + field.getName() + ":null");
                }
            }
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }*/

    }


    /**
     * 处方号
     */
    @JSONField(name = "pres_no")
    private String presNo;

    /**
     * 处方类别编码
     */
    @JSONField(name = "pres_class_code")
    private String presClassCode;

    /**
     * 处方类别名称
     */
    @JSONField(name = "pres_class_name")
    private String presClassName;

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
    private String birthday;

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
     * 保险类别编码
     */
    @JSONField(name = "ins_class_code")
    private String insClassCode;

    /**
     * 保险类别名称
     */
    @JSONField(name = "ins_class_name")
    private String insClassName;


    /**
     * 就诊科室编码
     */
    @JSONField(name = "visit_dept_code")
    private String visitDeptCode;

    /**
     * 就诊科室名称
     */
    @JSONField(name = "visit_dept_name")
    private String visitDeptName;

    /**
     * 开方科室编码
     */
    @JSONField(name = "pres_dept_code")
    private String presDeptCode;

    /**
     * 开方科室名称
     */
    @JSONField(name = "pres_dept_name")
    private String presDeptName;

    /**
     * 开方时间
     */
    @JSONField(name = "pres_time")
    private String presTime;

    /**
     * 开方医生编码
     */
    @JSONField(name = "pres_doc_code")
    private String presDocCode;

    /**
     * 开方医生姓名
     */
    @JSONField(name = "pres_doc_name")
    private String presDocName;

    /**
     * 开方医师照片数据
     */
    @JSONField(name = "pres_doc_phote_data")
    private String presDocPhoteData;

    /**
     * 审核时间
     */
    @JSONField(name = "review_time")
    private String reviewTime;

    /**
     * 审核医生编码
     */
    @JSONField(name = "review_doc_code")
    private String reviewDocCode;

    /**
     * 审核医生姓名
     */
    @JSONField(name = "review_doc_name")
    private String reviewDocName;

    /**
     * 审方时间
     */
    @JSONField(name = "trial_time")
    private String trialTime;

    /**
     * 审方医生编码
     */
    @JSONField(name = "trial_doc_code")
    private String trialDocCode;

    /**
     * 审方医生姓名
     */
    @JSONField(name = "trial_doc_name")
    private String trialDocName;

    /**
     * 诊断编码类型
     */
    @JSONField(name = "diag_code_type")
    private String diagCodeType;

    /**
     * 疾病编码
     */
    @JSONField(name = "diseases_code")
    private String diseasesCode;

    /**
     * 疾病名称
     */
    @JSONField(name = "diseases_name")
    private String diseasesName;

    /**
     * 药品列表
     */
    @JSONField(name = "drug_list")
    private List<Drug> drugList;

    /**
     * 疾病分类
     */
    @JSONField(name = "diseases_type")
    private String diseasesType;

    /**
     * 行动不便标志
     */
    @JSONField(name = "mobility_flag")
    private String mobilityFlag;

    /**
     * 病情稳定需长期服药标志
     */
    @JSONField(name = "long_medical_flag")
    private String longMedicalFlag;

    /**
     * 处方有效期（单位天）
     */
    @JSONField(name = "pres_effec_days")
    private String presEffecDays;

    /**
     * 总金额
     */
    @JSONField(name = "total_price")
    private String totalPrice;

    /**
     * 互联网医院处方图片(适合单张处方照片的情形)
     */
    @JSONField(name = "pres_photo")
    private String presPhoto;

    public List<PresPhotosList> getPresPhotosList() {
        return presPhotosList;
    }

    public void setPresPhotosList(List<PresPhotosList> presPhotosList) {
        this.presPhotosList = presPhotosList;
    }

    /**
     * 互联网医院处方图片列表(适合多张处方照片的情形)
     */
    @JSONField(name = "pres_photos_list")
    private List<PresPhotosList> presPhotosList;


    public String getPresNo() {
        return presNo;
    }

    public void setPresNo(String presNo) {
        this.presNo = presNo;
    }

    public String getPresClassCode() {
        return presClassCode;
    }

    public void setPresClassCode(String presClassCode) {
        this.presClassCode = presClassCode;
    }

    public String getPresClassName() {
        return presClassName;
    }

    public void setPresClassName(String presClassName) {
        this.presClassName = presClassName;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
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

    public String getInsClassCode() {
        return insClassCode;
    }

    public void setInsClassCode(String insClassCode) {
        this.insClassCode = insClassCode;
    }

    public String getInsClassName() {
        return insClassName;
    }

    public void setInsClassName(String insClassName) {
        this.insClassName = insClassName;
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

    public String getPresDeptCode() {
        return presDeptCode;
    }

    public void setPresDeptCode(String presDeptCode) {
        this.presDeptCode = presDeptCode;
    }

    public String getPresDeptName() {
        return presDeptName;
    }

    public void setPresDeptName(String presDeptName) {
        this.presDeptName = presDeptName;
    }

    public String getPresTime() {
        return presTime;
    }

    public void setPresTime(String presTime) {
        this.presTime = presTime;
    }

    public String getPresDocCode() {
        return presDocCode;
    }

    public void setPresDocCode(String presDocCode) {
        this.presDocCode = presDocCode;
    }

    public String getPresDocName() {
        return presDocName;
    }

    public void setPresDocName(String presDocName) {
        this.presDocName = presDocName;
    }

    public String getPresDocPhoteData() {
        return presDocPhoteData;
    }

    public void setPresDocPhoteData(String presDocPhoteData) {
        this.presDocPhoteData = presDocPhoteData;
    }

    public String getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getReviewDocCode() {
        return reviewDocCode;
    }

    public void setReviewDocCode(String reviewDocCode) {
        this.reviewDocCode = reviewDocCode;
    }

    public String getReviewDocName() {
        return reviewDocName;
    }

    public void setReviewDocName(String reviewDocName) {
        this.reviewDocName = reviewDocName;
    }

    public String getTrialTime() {
        return trialTime;
    }

    public void setTrialTime(String trialTime) {
        this.trialTime = trialTime;
    }

    public String getTrialDocCode() {
        return trialDocCode;
    }

    public void setTrialDocCode(String trialDocCode) {
        this.trialDocCode = trialDocCode;
    }

    public String getTrialDocName() {
        return trialDocName;
    }

    public void setTrialDocName(String trialDocName) {
        this.trialDocName = trialDocName;
    }

    public String getDiagCodeType() {
        return diagCodeType;
    }

    public void setDiagCodeType(String diagCodeType) {
        this.diagCodeType = diagCodeType;
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

    public List<Drug> getDrugList() {
        return drugList;
    }

    public void setDrugList(List<Drug> drugList) {
        this.drugList = drugList;
    }

    public String getDiseasesType() {
        return diseasesType;
    }

    public void setDiseasesType(String diseasesType) {
        this.diseasesType = diseasesType;
    }

    public String getMobilityFlag() {
        return mobilityFlag;
    }

    public void setMobilityFlag(String mobilityFlag) {
        this.mobilityFlag = mobilityFlag;
    }

    public String getLongMedicalFlag() {
        return longMedicalFlag;
    }

    public void setLongMedicalFlag(String longMedicalFlag) {
        this.longMedicalFlag = longMedicalFlag;
    }

    public String getPresEffecDays() {
        return presEffecDays;
    }

    public void setPresEffecDays(String presEffecDays) {
        this.presEffecDays = presEffecDays;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPresPhoto() {
        return presPhoto;
    }

    public void setPresPhoto(String presPhoto) {
        this.presPhoto = presPhoto;
    }


}
