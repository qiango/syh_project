package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 电话订单状态
 */
public enum PhoneOrderStateEnum implements CodeEnum {


    UnPaid(1, "未支付"),

    Paid(2, "待接诊"),

    InCall(3, "通话中"),

//    WaitDiagnosis(9, "待诊断"),//上线将6的改为4

    OrderSuccess(4, "交易完成"),

    OrderFail(5, "交易失败"),

    WaitRefund(8, "退款中"),
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

    PhoneOrderStateEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static PhoneOrderStateEnum getValue(int code) {
        for (PhoneOrderStateEnum c : PhoneOrderStateEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return UnPaid;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PhoneOrderStateEnum c : PhoneOrderStateEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
