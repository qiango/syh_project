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
 * 签约药品电商基本数据上传接口
 */
public class DrugEOrgBasicInfoRequest extends BaseRequest implements IRequest<BaseResponse>{
    private static Logger log = LoggerFactory.getLogger(PrescriptionRequest.class);

    @Override
    public String getApiName() {
        return "up_durg_e_org_basic_info";
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
     * 互联网医疗机构编码
     */
    @JSONField(name = "web_org_code")
    private String webOrgCode;
    /**
     * 互联网医疗机构名称
     */
    @JSONField(name = "web_org_name")
    private String webOrgName;
    /**
     * 药品电商编码
     */
    @JSONField(name = "org_code")
    private String orgCode;
    /**
     * 药品电商名称
     */
    @JSONField(name = "org_name")
    private String orgName;
    /**
     * 医疗机构联系电话
     */
    @JSONField(name = "org_tel")
    private String orgTel;
    /**
     * 药品电商简介
     */
    @JSONField(name = "org_comment")
    private String orgComment;
    /**
     * 签约时间
     */
    @JSONField(name = "sign_time")
    private String signTime;
    /**
     * 签约年限
     */
    @JSONField(name = "sign_life")
    private String signLife;

    public String getWebOrgCode() {
        return webOrgCode;
    }

    public void setWebOrgCode(String webOrgCode) {
        this.webOrgCode = webOrgCode;
    }

    public String getWebOrgName() {
        return webOrgName;
    }

    public void setWebOrgName(String webOrgName) {
        this.webOrgName = webOrgName;
    }

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

    public String getOrgTel() {
        return orgTel;
    }

    public void setOrgTel(String orgTel) {
        this.orgTel = orgTel;
    }

    public String getOrgComment() {
        return orgComment;
    }

    public void setOrgComment(String orgComment) {
        this.orgComment = orgComment;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public String getSignLife() {
        return signLife;
    }

    public void setSignLife(String signLife) {
        this.signLife = signLife;
    }

    /*
    互联网医疗机构编码	web_org_code
互联网医疗机构名称	web_org_name
药品电商编码	org_code
药品电商名称	org_name
医疗机构联系电话	org_tel
药品电商简介	org_comment

签约时间	sign_time
签约年限	sign_life
     */
}
