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
 *签约实体医院基本数据上传接口
 */
public class SignHospitalBasicInfoRequest extends BaseRequest implements IRequest<BaseResponse> {

    private static Logger log = LoggerFactory.getLogger(PrescriptionRequest.class);

    @Override
    public String getApiName() {
        return "up_sigin_hos_basic_info";
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
     *医疗机构编码
     */
    @JSONField(name = "org_code")
    private String orgCode;

    /**
     * 医疗机构名称
     */
    @JSONField(name = "org_name")
    private String orgName;

    /**
     * 级别
     */
    @JSONField(name = "level")
    private String level;
    /**
     * 性质
     */
    @JSONField(name = "nature")
    private String nature;
    /**
     * 医疗机构联系电话
     */
    @JSONField(name = "org_tel")
    private String orgTel;
    /**
     * 医疗机构负责人姓名
     */
    @JSONField(name = "org_principa_name")
    private String orgPrincipaName;
    /**
     * 医疗机构负责人电话
     */
    @JSONField(name = "org_principa_tel")
    private String orgPrincipaTel;
    /**
     * 地址
     */
    @JSONField(name = "address")
    private String address;
    /**
     * 医院简介
     */
    @JSONField(name = "org_comment")
    private String orgComment;
    /**
     * 科室简介
     */
    @JSONField(name = "dept_comment")
    private String deptComment;
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

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getOrgTel() {
        return orgTel;
    }

    public void setOrgTel(String orgTel) {
        this.orgTel = orgTel;
    }

    public String getOrgPrincipaName() {
        return orgPrincipaName;
    }

    public void setOrgPrincipaName(String orgPrincipaName) {
        this.orgPrincipaName = orgPrincipaName;
    }

    public String getOrgPrincipaTel() {
        return orgPrincipaTel;
    }

    public void setOrgPrincipaTel(String orgPrincipaTel) {
        this.orgPrincipaTel = orgPrincipaTel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrgComment() {
        return orgComment;
    }

    public void setOrgComment(String orgComment) {
        this.orgComment = orgComment;
    }

    public String getDeptComment() {
        return deptComment;
    }

    public void setDeptComment(String deptComment) {
        this.deptComment = deptComment;
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


    //todo 待完善

    /*{
      @JSONField(name ")web_org_code": "WEBH012",
          @JSONField(name ")web_org_name": "银川云海翼互联网医院",
          @JSONField(name ")org_code": "123456",
          @JSONField(name ")org_name": "东京医院",
          @JSONField(name ")level": "三级甲等",
          @JSONField(name ")nature": "综合医院",
          @JSONField(name ")org_tel": "010-0000000",
          @JSONField(name ")org_principa_name": "王五",
          @JSONField(name ")org_principa_tel": "132xxxxxxxx",
          @JSONField(name ")address": " 北京市海淀区复兴路28号",
          @JSONField(name ")org_comment": "医院介绍",
          @JSONField(name ")dept_comment": " 部门科室简介 ",
          @JSONField(name ")sign_time": "2017-01-03 12:00:00",
          @JSONField(name ")sign_life": "1"
    }*/
}
