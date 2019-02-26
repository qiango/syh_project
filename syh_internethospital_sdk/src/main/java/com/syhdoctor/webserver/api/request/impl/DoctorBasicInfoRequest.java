package com.syhdoctor.webserver.api.request.impl;

import com.alibaba.fastjson.annotation.JSONField;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.api.BaseException;
import com.syhdoctor.webserver.api.bean.*;
import com.syhdoctor.webserver.api.request.IRequest;
import com.syhdoctor.webserver.api.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 *
 */
public class DoctorBasicInfoRequest extends BaseRequest implements IRequest<BaseResponse> {


    private static Logger log = LoggerFactory.getLogger(DoctorBasicInfoRequest.class);

    @Override
    public String getApiName() {
        return "up_doc_basic_info";
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
     * 医生名称
     */
    @JSONField(name = "doc_name")
    private String docName;

    /**
     * //     * 医师类型 -1 即可
     * //
     */
    @JSONField(name = "doc_type")
    private String docType;

    @JSONField(name = "title_code")
    private String titleCode;

    @JSONField(name = "title_name")
    private String titleName;


    /**
     * 机构内医师编码
     */
    @JSONField(name = "in_doc_code")
    private String inDocCode;

//    /**
//     * 签约时间
//     */
//    @Deprecated
//    @JSONField(name = "sign_time")
//    private String signTime;
//
//    @Deprecated
//    @JSONField(name = "sign_life")
//    private String signLife;

    /**
     * 信用评级
     */
    @JSONField(name = "credit_level")
    private String creditLevel;

    /**
     * 职业评级
     */
    @JSONField(name = "occu_level")
    private String occuLevel;

    /**
     * 医师线下现任职机构编码
     */
    @JSONField(name = "work_inst_code")
    private String workInstCode;

    /**
     * 医师线下现任职机构名称
     */
    @JSONField(name = "work_inst_name")
    private String workInstName;

    /**
     * 医师联系手机号
     */
    @JSONField(name = "doo_tel")
    private String dooTel;

    /**
     * 医师身份证号
     */
    @JSONField(name = "id_card")
    private String idCard;

    /**
     * 医师执业证号
     */
    @JSONField(name = "prac_no")
    private String pracNo;

    /**
     * 执业证取得时间
     */
    @JSONField(name = "prac_rec_date")
    private String pracRecDate;

    /**
     * 医师资格证号
     */
    @JSONField(name = "cert_no")
    private String certNo;

    /**
     * 资格证取得时间
     */
    @JSONField(name = "cert_rec_date")
    private String certRecDate;

    /**
     * 医师职称证号
     */
    @JSONField(name = "title_no")
    private String titleNo;

    /**
     * 职称证取得时间
     */
    @JSONField(name = "title_rec_date")
    private String titleRecDate;

    /**
     * 医师执业类别
     */
    @JSONField(name = "prac_type")
    private String pracType;

    /**
     * 最近连续两个周期的医师定期考核合格是否合格
     */
    @JSONField(name = "qualify_or_not")
    private String qualifyOrNot;

    /**
     * 医师擅长专业
     */
    @JSONField(name = "professional")
    private String professional;

    /**
     * 医师诊疗活动价格列表
     */
    @JSONField(name = "med_price_list")
    private List<MedPriceList> medPriceList;

    /**
     * 医师数字签名留样
     */
    @JSONField(name = "digital_sign")
    private String digitalSign;

    /**
     * 医师评分
     */
    @JSONField(name = "doc_penalty_points")
    private String docPenaltyPoints;

//    /**
//     * 银川是否已备案
//     */
//    @Deprecated
//    @JSONField(name = "yc_record_flag")
//    private String ycRecordFlag;
//
//    /**
//     * 医院是否已确认
//     */
//    @Deprecated
//    @JSONField(name = "hos_confirm_flag")
//    private String hosConfirmFlag;
//
//    /**
//     * 银川处方开具权是否备案
//     */
//    @Deprecated
//    @JSONField(name = "yc_pres_record_flag")
//    private String ycPresRecordFlag;

    /**
     * 医师多点执业备案表文件列表
     */
    @JSONField(name = "doc_multi_sited_lic_record_list")
    private List<DocMultiSitedLicRecordList> docMultiSitedLicRecordList;

    /**
     * 医师身份证文件列表 正反两面
     */
    @JSONField(name = "id_card_list")
    private List<IdCardList> idCardList;

    /**
     * 医师执业证文件列表
     */
    @JSONField(name = "cert_doc_prac_list")
    private List<CertDocPracList> certDocPracList;

    /**
     * 医师职称证文件列表
     */
    @JSONField(name = "title_cert_list")
    private List<TitleCertList> titleCertList;

    /**
     * 医师资格证文件列表
     */
    @JSONField(name = "doc_cert_list")
    private List<DocCertList> docCertList;

    /**
     * 医师认证照片文件
     */
    @JSONField(name = "doc_photo")
    private String docPhoto;

    /**
     * 互联网医院聘任合同
     */
    @JSONField(name = "employ_file")
    private String employFile;


    /**
     * 是否同意以上条款
     */
    @JSONField(name = "agree_terms")
    private String agreeTerms;

    /**
     * 医师执业范围
     */
    @JSONField(name = "prac_scope")
    private String pracScope;

    /**
     * 审批局规定的医师执业范围
     */
    @JSONField(name = "prac_scope_approval")
    private String pracScopeApproval;

    /**
     * 医师多点执业起始时间
     */
    @JSONField(name = "doc_multi_sited_date_start")
    private String docMultiSitedDateStart;

    /**
     * 医师多点执业终止时间
     */
    @JSONField(name = "doc_multi_sited_date_end")
    private String docMultiSitedDateEnd;

    /**
     * 申请拟执业医疗机构意见
     */
    @JSONField(name = "hos_opinion")
    private String hosOpinion;

    /**
     * 申请拟执业医疗机构意见时间
     */
    @JSONField(name = "hos_opinion_date")
    private String hosOpinionDate;

    /**
     * 签名
     */
    @JSONField(name = "hos_digital_sign")
    private String hosDigitalSign;

    /**
     * 医师申请多点执业承诺时间
     */
    @JSONField(name = "doc_multi_sited_date_promise")
    private String docMultiSitedDatePromise;


    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getTitleCode() {
        return titleCode;
    }

    public void setTitleCode(String titleCode) {
        this.titleCode = titleCode;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getInDocCode() {
        return inDocCode;
    }

    public void setInDocCode(String inDocCode) {
        this.inDocCode = inDocCode;
    }

//    public String getSignTime() {
//        return signTime;
//    }
//
//    public void setSignTime(String signTime) {
//        this.signTime = signTime;
//    }
//
//    public String getSignLife() {
//        return signLife;
//    }
//
//    public void setSignLife(String signLife) {
//        this.signLife = signLife;
//    }

    public String getCreditLevel() {
        return creditLevel;
    }

    public void setCreditLevel(String creditLevel) {
        this.creditLevel = creditLevel;
    }

    public String getOccuLevel() {
        return occuLevel;
    }

    public void setOccuLevel(String occuLevel) {
        this.occuLevel = occuLevel;
    }

    public String getWorkInstCode() {
        return workInstCode;
    }

    public void setWorkInstCode(String workInstCode) {
        this.workInstCode = workInstCode;
    }

    public String getWorkInstName() {
        return workInstName;
    }

    public void setWorkInstName(String workInstName) {
        this.workInstName = workInstName;
    }

    public String getDooTel() {
        return dooTel;
    }

    public void setDooTel(String dooTel) {
        this.dooTel = dooTel;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPracNo() {
        return pracNo;
    }

    public void setPracNo(String pracNo) {
        this.pracNo = pracNo;
    }

    public String getPracRecDate() {
        return pracRecDate;
    }

    public void setPracRecDate(String pracRecDate) {
        this.pracRecDate = pracRecDate;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getCertRecDate() {
        return certRecDate;
    }

    public void setCertRecDate(String certRecDate) {
        this.certRecDate = certRecDate;
    }

    public String getTitleNo() {
        return titleNo;
    }

    public void setTitleNo(String titleNo) {
        this.titleNo = titleNo;
    }

    public String getTitleRecDate() {
        return titleRecDate;
    }

    public void setTitleRecDate(String titleRecDate) {
        this.titleRecDate = titleRecDate;
    }

    public String getPracType() {
        return pracType;
    }

    public void setPracType(String pracType) {
        this.pracType = pracType;
    }

    public String getQualifyOrNot() {
        return qualifyOrNot;
    }

    public void setQualifyOrNot(String qualifyOrNot) {
        this.qualifyOrNot = qualifyOrNot;
    }

    public String getProfessional() {
        return professional;
    }

    public void setProfessional(String professional) {
        this.professional = professional;
    }

    public List<MedPriceList> getMedPriceList() {
        return medPriceList;
    }

    public void setMedPriceList(List<MedPriceList> medPriceList) {
        this.medPriceList = medPriceList;
    }

    public String getDigitalSign() {
        return digitalSign;
    }

    public void setDigitalSign(String digitalSign) {
        this.digitalSign = digitalSign;
    }

    public String getDocPenaltyPoints() {
        return docPenaltyPoints;
    }

    public void setDocPenaltyPoints(String docPenaltyPoints) {
        this.docPenaltyPoints = docPenaltyPoints;
    }

//    public String getYcRecordFlag() {
//        return ycRecordFlag;
//    }
//
//    public void setYcRecordFlag(String ycRecordFlag) {
//        this.ycRecordFlag = ycRecordFlag;
//    }
//
//    public String getHosConfirmFlag() {
//        return hosConfirmFlag;
//    }
//
//    public void setHosConfirmFlag(String hosConfirmFlag) {
//        this.hosConfirmFlag = hosConfirmFlag;
//    }
//
//    public String getYcPresRecordFlag() {
//        return ycPresRecordFlag;
//    }
//
//    public void setYcPresRecordFlag(String ycPresRecordFlag) {
//        this.ycPresRecordFlag = ycPresRecordFlag;
//    }
//
    public List<DocMultiSitedLicRecordList> getDocMultiSitedLicRecordList() {
        return docMultiSitedLicRecordList;
    }

    public void setDocMultiSitedLicRecordList(List<DocMultiSitedLicRecordList> docMultiSitedLicRecordList) {
        this.docMultiSitedLicRecordList = docMultiSitedLicRecordList;
    }

    public List<IdCardList> getIdCardList() {
        return idCardList;
    }

    public void setIdCardList(List<IdCardList> idCardList) {
        this.idCardList = idCardList;
    }

    public List<CertDocPracList> getCertDocPracList() {
        return certDocPracList;
    }

    public void setCertDocPracList(List<CertDocPracList> certDocPracList) {
        this.certDocPracList = certDocPracList;
    }

    public List<TitleCertList> getTitleCertList() {
        return titleCertList;
    }

    public void setTitleCertList(List<TitleCertList> titleCertList) {
        this.titleCertList = titleCertList;
    }

    public List<DocCertList> getDocCertList() {
        return docCertList;
    }

    public void setDocCertList(List<DocCertList> docCertList) {
        this.docCertList = docCertList;
    }

    public String getDocPhoto() {
        return docPhoto;
    }

    public void setDocPhoto(String docPhoto) {
        this.docPhoto = docPhoto;
    }

    public String getEmployFile() {
        return employFile;
    }

    public void setEmployFile(String employFile) {
        this.employFile = employFile;
    }


    public String getAgreeTerms() {
        return agreeTerms;
    }

    public void setAgreeTerms(String agreeTerms) {
        this.agreeTerms = agreeTerms;
    }


    public String getPracScope() {
        return pracScope;
    }

    public void setPracScope(String pracScope) {
        this.pracScope = pracScope;
    }


    public String getPracScopeApproval() {
        return pracScopeApproval;
    }

    public void setPracScopeApproval(String pracScopeApproval) {
        this.pracScopeApproval = pracScopeApproval;
    }


    public String getDocMultiSitedDateStart() {
        return docMultiSitedDateStart;
    }

    public void setDocMultiSitedDateStart(String docMultiSitedDateStart) {
        this.docMultiSitedDateStart = docMultiSitedDateStart;
    }


    public String getDocMultiSitedDateEnd() {
        return docMultiSitedDateEnd;
    }

    public void setDocMultiSitedDateEnd(String docMultiSitedDateEnd) {
        this.docMultiSitedDateEnd = docMultiSitedDateEnd;
    }


    public String getHosOpinion() {
        return hosOpinion;
    }

    public void setHosOpinion(String hosOpinion) {
        this.hosOpinion = hosOpinion;
    }


    public String getHosOpinionDate() {
        return hosOpinionDate;
    }

    public void setHosOpinionDate(String hosOpinionDate) {
        this.hosOpinionDate = hosOpinionDate;
    }


    public String getDocMultiSitedDatePromise() {
        return docMultiSitedDatePromise;
    }

    public void setDocMultiSitedDatePromise(String docMultiSitedDatePromise) {
        this.docMultiSitedDatePromise = docMultiSitedDatePromise;
    }

    public String getHosDigitalSign() {
        return hosDigitalSign;
    }

    public void setHosDigitalSign(String hosDigitalSign) {
        this.hosDigitalSign = hosDigitalSign;
    }
}


