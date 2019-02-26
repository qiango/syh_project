package com.syhdoctor.common.utils.EnumUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 问诊类型
 */
public enum VisitCategoryEnum implements CodeEnum {

    //医生
    graphic(1, "图文问诊"),
    voice(2, "语音问诊"),
    //医生
    phone(3, "电话问诊"),
    video(4, "视频问诊"),
    referral(5, "转诊"),
    reservation(6, "预约"),
    //首页
    department(7, "急诊"),
    //首页
    Outpatient(8, "门诊");

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

    VisitCategoryEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getName(int code) {
        for (VisitCategoryEnum c : VisitCategoryEnum.values()) {
            if (c.code == code) {
                return c.getMessage();
            }
        }
        return null;
    }

    public static VisitCategoryEnum getValue(int code) {
        for (VisitCategoryEnum c : VisitCategoryEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return graphic;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (VisitCategoryEnum c : VisitCategoryEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }

}
