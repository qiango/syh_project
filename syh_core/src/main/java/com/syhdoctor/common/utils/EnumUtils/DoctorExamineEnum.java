package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 医生审核状态
 */
public enum DoctorExamineEnum implements CodeEnum {


    notCertified(0, "全部"),

    successfulCertified(1, "信息审核中"),

    auditSuccess(2, "信息审核成功"),

    failCertified(3, "信息审核失败"),

    inCertified(4, "信息未完善"),

    Certification(5, "处方认证中"),

    authenticationFailed(6, "处方认证失败"),

    certificationSuccess(7, "处方认证成功"),
    ;

    private Integer code;

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    DoctorExamineEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static DoctorExamineEnum getValue(int code) {
        for (DoctorExamineEnum c : DoctorExamineEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return notCertified;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DoctorExamineEnum c : DoctorExamineEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
