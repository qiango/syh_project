package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * banner图位置
 */
public enum DisplaypositionEnum implements CodeEnum {


    userTop(1, "用户端顶部banner图"),

    UserOpen(2, "用户端开屏banner图"),

    DoctorOpen(3, "医生端开屏banner图"),

    UserLightning(4, "用户端闪屏banner图"),

    DoctorTop(5, "医生首页banner图"),

    DoctorLightning(6, "医生端闪屏banner图"),
    ;

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

    DisplaypositionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static DisplaypositionEnum getValue(int code) {
        for (DisplaypositionEnum c : DisplaypositionEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return userTop;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DisplaypositionEnum c : DisplaypositionEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
