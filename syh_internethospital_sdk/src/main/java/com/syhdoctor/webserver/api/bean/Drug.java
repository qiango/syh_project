package com.syhdoctor.webserver.api.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class Drug {
    /**
     * 药品通用名称
     */
    @JSONField(name ="appr_drug_name")
    private String apprDrugName;


    /**
     * 药品商品编码
     */
    @JSONField(name ="drug_code")
    private String drugCode;

    /**
     * 药品商品名称
     */
    @JSONField(name ="drug_name")
    private String drugName;

    /**
     * 药品剂型
     */
    @JSONField(name ="drug_form")
    private String drugForm;

    /**
     * 用药剂量-单次
     */
    @JSONField(name ="dosage")
    private String dosAge;

    /**
     * 用药剂量单位-单次
     */
    @JSONField(name ="dosage_unit")
    private String dosageUnit;

    /**
     * 用药剂量-总量
     */
    @JSONField(name ="total_dosage")
    private String totalDosage;


    /**
     * 用药剂量单位-总量
     */
    @JSONField(name ="total_dosage_unit")
    private String totalDosageUnit;

    /**
     * 用药频率编码
     */
    @JSONField(name ="medicine_freq")
    private String medicineFreq;

    /**
     * 用药频率
     */
    @JSONField(name ="medicine_freq_name")
    private String medicineFreqName;

    /**
     * 规格
     */
    @JSONField(name ="standard_desc")
    private String standardDesc;

    /**
     * 单价
     */
    @JSONField(name ="single_price")
    private String singlePrice;

    /**
     * 金额
     */
    @JSONField(name ="drug_total_price")
    private String drugTotalPrice;

    /**
     * 嘱托
     */
    @JSONField(name ="comments")
    private String comments;

    /**
     * 抗菌药说明
     */
    @JSONField(name ="anti_comments")
    private String antiComments;

    /**
     * 中药煎煮法名称
     */
    @JSONField(name ="dec_meth_name")
    private String decMethName;

    /**
     * 药量(单位为天)
     */
    @JSONField(name ="total_charge")
    private String totalCharge;

    public String getApprDrugName() {
        return apprDrugName;
    }

    public void setApprDrugName(String apprDrugName) {
        this.apprDrugName = apprDrugName;
    }

    public String getDrugCode() {
        return drugCode;
    }

    public void setDrugCode(String drugCode) {
        this.drugCode = drugCode;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDrugForm() {
        return drugForm;
    }

    public void setDrugForm(String drugForm) {
        this.drugForm = drugForm;
    }

    public String getDosAge() {
        return dosAge;
    }

    public void setDosAge(String dosAge) {
        this.dosAge = dosAge;
    }

    public String getDosageUnit() {
        return dosageUnit;
    }

    public void setDosageUnit(String dosageUnit) {
        this.dosageUnit = dosageUnit;
    }

    public String getTotalDosage() {
        return totalDosage;
    }

    public void setTotalDosage(String totalDosage) {
        this.totalDosage = totalDosage;
    }

    public String getTotalDosageUnit() {
        return totalDosageUnit;
    }

    public void setTotalDosageUnit(String totalDosageUnit) {
        this.totalDosageUnit = totalDosageUnit;
    }

    public String getMedicineFreq() {
        return medicineFreq;
    }

    public void setMedicineFreq(String medicineFreq) {
        this.medicineFreq = medicineFreq;
    }

    public String getMedicineFreqName() {
        return medicineFreqName;
    }

    public void setMedicineFreqName(String medicineFreqName) {
        this.medicineFreqName = medicineFreqName;
    }

    public String getStandardDesc() {
        return standardDesc;
    }

    public void setStandardDesc(String standardDesc) {
        this.standardDesc = standardDesc;
    }

    public String getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(String singlePrice) {
        this.singlePrice = singlePrice;
    }

    public String getDrugTotalPrice() {
        return drugTotalPrice;
    }

    public void setDrugTotalPrice(String drugTotalPrice) {
        this.drugTotalPrice = drugTotalPrice;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAntiComments() {
        return antiComments;
    }

    public void setAntiComments(String antiComments) {
        this.antiComments = antiComments;
    }

    public String getDecMethName() {
        return decMethName;
    }

    public void setDecMethName(String decMethName) {
        this.decMethName = decMethName;
    }

    public String getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(String totalCharge) {
        this.totalCharge = totalCharge;
    }
}
