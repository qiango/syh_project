package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付状态
 */
public enum PayStateEnum implements CodeEnum {


    UnPaid(0, "未支付"),

    Paid(1, "已支付");


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

    PayStateEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static PayStateEnum getValue(int code) {
        for (PayStateEnum c : PayStateEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return UnPaid;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PayStateEnum c : PayStateEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
