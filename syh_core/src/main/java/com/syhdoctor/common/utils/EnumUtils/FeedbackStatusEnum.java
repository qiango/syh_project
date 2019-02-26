package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 反馈处理状态
 */
public enum FeedbackStatusEnum implements CodeEnum {


    Untreated(1, "未处理"),

    Handle(2, "已处理"),


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

    FeedbackStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static FeedbackStatusEnum getValue(int code) {
        for (FeedbackStatusEnum c : FeedbackStatusEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return Untreated;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (FeedbackStatusEnum c : FeedbackStatusEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
