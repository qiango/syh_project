package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 尊享类别
 */
public enum EnjoyTypeEnum implements CodeEnum {


    Untreated(1, "年费"),

    Handle(2, "续费"),


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

    EnjoyTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static EnjoyTypeEnum getValue(int code) {
        for (EnjoyTypeEnum c : EnjoyTypeEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return Untreated;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (EnjoyTypeEnum c : EnjoyTypeEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
