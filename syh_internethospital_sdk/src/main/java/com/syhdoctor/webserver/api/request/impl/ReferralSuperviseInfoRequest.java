package com.syhdoctor.webserver.api.request.impl;

import com.alibaba.fastjson.annotation.JSONField;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.api.BaseException;
import com.syhdoctor.webserver.api.request.IRequest;
import com.syhdoctor.webserver.api.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * 转诊监管信息
 */
public class ReferralSuperviseInfoRequest extends BaseRequest implements IRequest<BaseResponse> {

    private static Logger log = LoggerFactory.getLogger(ReferralSuperviseInfoRequest.class);

    @Override
    public String getApiName() {
        return "";
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
     * 转诊时间
     */
    @JSONField(name = "referral_time")
    private String referralTime;


    /**
     * 结束时间
     */
    @JSONField(name = "referral_finish_time")
    private String referralFinishTime;


    /**
     * 转诊医疗机构编码
     */
    @JSONField(name = "referral_org_code")
    private String referralOrgCode;

    /**
     * 转诊医疗机构名称
     */
    @JSONField(name = "referral_org_name")
    private String referralOrgName;


    /**
     * 申请转诊医师编码
     */
    @JSONField(name = "apply_referral_doc_code")
    private String applyReferralDocCode;


    /**
     * 申请转诊医师姓名
     */
    @JSONField(name = "apply_referral_doc_name")
    private String applyReferralDocName;

    /**
     * 转诊原因
     */
    @JSONField(name = "referral_reason")
    private String referralReason;

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
     * 患者所在地区
     */
    @JSONField(name = "pt_district")
    private String ptDistrict;


}
