package com.syhdoctor.webserver.api.request.impl;

import com.alibaba.fastjson.annotation.JSONField;
import com.syhdoctor.webserver.api.request.IRequest;
import com.syhdoctor.webserver.api.response.BaseResponse;

/**
 * 7.9药品配送监管数据上传接口
 */
public class Drug_DeliverySupersiveRequest extends BaseRequest implements IRequest<BaseResponse> {
    @Override
    public String getApiName() {
        return "up_drug_delivery_supervise_info";
    }

    @Override
    public Class<BaseResponse> getResponseClass() {
        return BaseResponse.class;
    }

    @Override
    public void Validate() {

    }

    /**
     * 药品电商编码
     */
    @JSONField(name = "drug_e_commere_code")
    private String drugeCommereCode;

    /**
     * 药品电商名称
     */
    @JSONField(name = "drug_e_commere_name")
    private String drugeCommereName;

    /**
     * 配送时间 形式如“YYYY-MM-DD”+空格+“ hh:mm:ss”
     */
    @JSONField(name = "delivery_time")
    private String deliveryTime;

    /**
     * 是否及时
     */
    @JSONField(name = "on_time_flag")
    private String onTimeFlag;

    /**
     * 是否出错
     */
    @JSONField(name = "error_flag")
    private String errorFlag;

    /**
     * 处方号
     */
    @JSONField(name = "pres_no")
    private String presNo;


    public String getDrugeCommereCode() {
        return drugeCommereCode;
    }

    public void setDrugeCommereCode(String drugeCommereCode) {
        this.drugeCommereCode = drugeCommereCode;
    }

    public String getDrugeCommereName() {
        return drugeCommereName;
    }

    public void setDrugeCommereName(String drugeCommereName) {
        this.drugeCommereName = drugeCommereName;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getOnTimeFlag() {
        return onTimeFlag;
    }

    public void setOnTimeFlag(String onTimeFlag) {
        this.onTimeFlag = onTimeFlag;
    }

    public String getErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(String errorFlag) {
        this.errorFlag = errorFlag;
    }

    public String getPresNo() {
        return presNo;
    }

    public void setPresNo(String presNo) {
        this.presNo = presNo;
    }
}
