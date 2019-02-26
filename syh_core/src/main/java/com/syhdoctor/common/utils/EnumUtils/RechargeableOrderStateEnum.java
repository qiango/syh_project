package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 充值订单状态
 */
public enum RechargeableOrderStateEnum implements CodeEnum {


    UnPaid(1, "未支付"),

    Paid(2, "已支付");

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

    RechargeableOrderStateEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static RechargeableOrderStateEnum getValue(int code) {
        for (RechargeableOrderStateEnum c : RechargeableOrderStateEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return UnPaid;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RechargeableOrderStateEnum c : RechargeableOrderStateEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
