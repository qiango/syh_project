package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口返回
 */
public enum ResultEnum implements CodeEnum {

    Success(1, "请求成功"),

    Fial(-1, "请求失败"),

    TokenExpired(-100, "token过期，重新登录"),

    StrongUpdate(-101, "强制更新");

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private Integer code;
    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResultEnum getValue(int code) {
        for (ResultEnum c : ResultEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return Success;
    }

    public List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ResultEnum c : ResultEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
