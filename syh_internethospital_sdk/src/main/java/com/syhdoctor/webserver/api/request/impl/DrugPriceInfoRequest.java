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
 * 药品价格监管数据上传接口
 */
public class DrugPriceInfoRequest extends BaseRequest implements IRequest<BaseResponse> {
    private static Logger log = LoggerFactory.getLogger(PrescriptionRequest.class);

    @Override
    public String getApiName() {
        return "up_drug_price_info";
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
     * 药品通用名称
     */
    @JSONField(name = "appr_drug_name")
    private String apprDrugName;
    /**
     * 药品编码分类
     */
    @JSONField(name = "drug_code_type")
    private String drugCodeType;
    /**
     * 药品编码
     */
    @JSONField(name = "drug_code")
    private String drugCode;
    /**
     * 药品商品名称
     */
    @JSONField(name = "drug_name")
    private String drugName;
    /**
     * 药品商品编号
     */
    @JSONField(name = "drug_id")
    private String drugId;
    /**
     * 药品剂型
     */
    @JSONField(name = "drug_form")
    private String drugForm;
    /**
     * 规格
     */
    @JSONField(name = "standard_desc")
    private String standardDesc;
    /**
     * 医疗机构编码
     */
    @JSONField(name = "inst_id")
    private String instId;
    /**
     * 医疗机构名称
     */
    @JSONField(name = "inst_name")
    private String instName;
    /**
     * 药品电商编码
     */
    @JSONField(name = "drug_orgId")
    private String drugOrgId;
    /**
     * 药品电商名称
     */
    @JSONField(name = "drug_orgName")
    private String drugOrgName;
    /**
     * 原价格
     */
    @JSONField(name = "pre_price")
    private String prePrice;
    /**
     * 变更后价格
     */
    @JSONField(name = "after_price")
    private String afterPrice;
    /**
     * 变更日期
     */
    @JSONField(name = "changeDate")
    private String changeDate;
    /**
     * 变更原因
     */
    @JSONField(name = "reason")
    private String reason;

    public String getApprDrugName() {
        return apprDrugName;
    }

    public void setApprDrugName(String apprDrugName) {
        this.apprDrugName = apprDrugName;
    }

    public String getDrugCodeType() {
        return drugCodeType;
    }

    public void setDrugCodeType(String drugCodeType) {
        this.drugCodeType = drugCodeType;
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

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public String getDrugForm() {
        return drugForm;
    }

    public void setDrugForm(String drugForm) {
        this.drugForm = drugForm;
    }

    public String getStandardDesc() {
        return standardDesc;
    }

    public void setStandardDesc(String standardDesc) {
        this.standardDesc = standardDesc;
    }

    public String getInstId() {
        return instId;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }

    public String getInstName() {
        return instName;
    }

    public void setInstName(String instName) {
        this.instName = instName;
    }

    public String getDrugOrgId() {
        return drugOrgId;
    }

    public void setDrugOrgId(String drugOrgId) {
        this.drugOrgId = drugOrgId;
    }

    public String getDrugOrgName() {
        return drugOrgName;
    }

    public void setDrugOrgName(String drugOrgName) {
        this.drugOrgName = drugOrgName;
    }

    public String getPrePrice() {
        return prePrice;
    }

    public void setPrePrice(String prePrice) {
        this.prePrice = prePrice;
    }

    public String getAfterPrice() {
        return afterPrice;
    }

    public void setAfterPrice(String afterPrice) {
        this.afterPrice = afterPrice;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    /*
    药品通用名称	appr_drug_name
药品编码分类	drug_code_type
药品编码	drug_code
药品商品名称	drug_name
药品商品编号	drug_id
药品剂型	drug_form
规格	standard_desc
医疗机构编码	inst_id
医疗机构名称	inst_name
药品电商编码	drug_orgId
药品电商名称	drug_orgName
原价格	pre_price
变更后价格	after_price
变更日期	changeDate
变更原因	reason
     */

}
