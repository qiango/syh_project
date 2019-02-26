package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单类型
 */
public enum OrderTypeEnum implements CodeEnum {


    Answer(1, "图文"),

    Phone(2, "电话"),

    Video(3, "视频"),

    Vip(4, "会员卡"),

    Rechargeable(5, "充值卡"),

    Green(6, "绿通"),
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

    OrderTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static OrderTypeEnum getValue(int code) {
        for (OrderTypeEnum c : OrderTypeEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return Answer;
    }

    public List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderTypeEnum c : OrderTypeEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
