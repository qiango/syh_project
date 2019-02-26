package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum MongodbEnum implements CodeEnum{

    Banner(1, "广告图"),

    Share(2, "分享");

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

    MongodbEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static MongodbEnum getValue(int code) {
        for (MongodbEnum c : MongodbEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return Banner;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MongodbEnum c : MongodbEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }



}
