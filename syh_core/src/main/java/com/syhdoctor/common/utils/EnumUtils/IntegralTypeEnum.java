package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分类型
 */
public enum IntegralTypeEnum implements CodeEnum {

    SignIn(1, "登录签到"),
    Info(2, "完善信息"),
    QA(3, "开通图文服务"),
    Phone(4, "开通电话服务");

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private Integer code;

    private String message;

    IntegralTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static IntegralTypeEnum getValue(int code) {
        for (IntegralTypeEnum c : IntegralTypeEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return SignIn;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (IntegralTypeEnum c : IntegralTypeEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
