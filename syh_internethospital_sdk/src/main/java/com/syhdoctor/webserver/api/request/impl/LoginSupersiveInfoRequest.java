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
 * 登陆监管信息
 */
public class LoginSupersiveInfoRequest extends BaseRequest implements IRequest<BaseResponse> {

    private static Logger log = LoggerFactory.getLogger(LoginSupersiveInfoRequest.class);

    @Override
    public String getApiName() {
        return "up_login_supersive_info";
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
     * 登录医师编码
     */
    @JSONField(name = "ogin_doc_code")
    private String oginDocCode;

    /**
     * 登录医师姓名
     */
    @JSONField(name = "login_doc_name")
    private String loginDocName;

    /**
     * 登录时间
     */
    @JSONField(name = "login_time")
    private String loginTime;

    /**
     * 登录医师照片数据
     */
    @JSONField(name = "login_doc_photo")
    private String loginDocPhoto;

    public String getOginDocCode() {
        return oginDocCode;
    }

    public void setOginDocCode(String oginDocCode) {
        this.oginDocCode = oginDocCode;
    }

    public String getLoginDocName() {
        return loginDocName;
    }

    public void setLoginDocName(String loginDocName) {
        this.loginDocName = loginDocName;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginDocPhoto() {
        return loginDocPhoto;
    }

    public void setLoginDocPhoto(String loginDocPhoto) {
        this.loginDocPhoto = loginDocPhoto;
    }
}
