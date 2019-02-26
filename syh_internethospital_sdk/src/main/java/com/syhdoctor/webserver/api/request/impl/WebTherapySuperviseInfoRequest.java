package com.syhdoctor.webserver.api.request.impl;

import com.alibaba.fastjson.annotation.JSONField;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.api.BaseException;
import com.syhdoctor.webserver.api.bean.ProcIndexList;
import com.syhdoctor.webserver.api.request.IRequest;
import com.syhdoctor.webserver.api.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 在线诊疗监管信息
 */
public class WebTherapySuperviseInfoRequest extends BaseRequest implements IRequest<BaseResponse> {

    private static Logger log = LoggerFactory.getLogger(WebTherapySuperviseInfoRequest.class);

    @Override
    public String getApiName() {
        return "up_web_therapy_supervise_info";
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
     * 接诊时间
     */
    @JSONField(name = "visit_time")
    private String visitTime;

    /**
     * 结束时间
     */
    @JSONField(name = "visit_finish_time")
    private String visitFinishTime;


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
     * 诊疗费用
     */
    @JSONField(name = "price")
    private String price;

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
     * 主诉
     */
    @JSONField(name = "complaint_content")
    private String complaintContent;


    /**
     * 现病史
     */
    @JSONField(name = "present_illness")
    private String presentIllness;


    /**
     * 既往史
     */
    @JSONField(name = "past_history")
    private String pastHistory;


    /**
     * 患者所在地区
     */
    @JSONField(name = "pt_district")
    private String ptDistrict;


    /**
     * 咨询或就诊(如果本次诊疗属于咨询型，则填“0”；如果本次诊疗属于就诊型，则填“1”)
     */
    @JSONField(name = "ask_or_med")
    private String askOrMed;

    /**
     * 诊疗过程文件索引列表
     */
    @JSONField(name = "proc_index_list")
    private List<ProcIndexList> procIndexList;


    public String getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(String visitTime) {
        this.visitTime = visitTime;
    }

    public String getVisitFinishTime() {
        return visitFinishTime;
    }

    public void setVisitFinishTime(String visitFinishTime) {
        this.visitFinishTime = visitFinishTime;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getComplaintContent() {
        return complaintContent;
    }

    public void setComplaintContent(String complaintContent) {
        this.complaintContent = complaintContent;
    }

    public String getPresentIllness() {
        return presentIllness;
    }

    public void setPresentIllness(String presentIllness) {
        this.presentIllness = presentIllness;
    }

    public String getPastHistory() {
        return pastHistory;
    }

    public void setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
    }

    public String getPtDistrict() {
        return ptDistrict;
    }

    public void setPtDistrict(String ptDistrict) {
        this.ptDistrict = ptDistrict;
    }

    public String getAskOrMed() {
        return askOrMed;
    }

    public void setAskOrMed(String askOrMed) {
        this.askOrMed = askOrMed;
    }

    public List<ProcIndexList> getProcIndexList() {
        return procIndexList;
    }

    public void setProcIndexList(List<ProcIndexList> procIndexList) {
        this.procIndexList = procIndexList;
    }
}
