package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 绿通订单状态
 */
public enum GreenOrderStateEnum implements CodeEnum {


    UnPaid(1, "待支付"),

    Paid(2, "待接诊"),

    InCall(3, "进行中"),

    OrderSuccess(4, "交易完成"),

    OrderFail(5, "交易失败"),

    WaitRefund(6, "预约中"),
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

    GreenOrderStateEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static GreenOrderStateEnum getValue(int code) {
        for (GreenOrderStateEnum c : GreenOrderStateEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return UnPaid;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (GreenOrderStateEnum c : GreenOrderStateEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
