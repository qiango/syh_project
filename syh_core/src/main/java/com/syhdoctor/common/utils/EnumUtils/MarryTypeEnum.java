package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单类型
 */
public enum MarryTypeEnum implements CodeEnum {


    Unmarried(1, "未婚"),

    Phone(2, "已婚"),

    Divorce(3, "离异"),

    Widowedspouse(4, "丧偶"),

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

    MarryTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static MarryTypeEnum getValue(int code) {
        for (MarryTypeEnum c : MarryTypeEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return Unmarried;
    }

    public List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MarryTypeEnum c : MarryTypeEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
