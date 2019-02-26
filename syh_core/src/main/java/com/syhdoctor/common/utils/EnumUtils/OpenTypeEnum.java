package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * openId类型
 */
public enum OpenTypeEnum implements CodeEnum {


    Wechat(1, "微信公众号"),

    Ali(2, "支付宝公众号"),
    ;


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

    OpenTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static OpenTypeEnum getValue(int code) {
        for (OpenTypeEnum c : OpenTypeEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return Wechat;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OpenTypeEnum c : OpenTypeEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
