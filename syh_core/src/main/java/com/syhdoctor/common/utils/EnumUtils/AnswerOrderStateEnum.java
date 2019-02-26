package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图文订单状态
 */
public enum AnswerOrderStateEnum implements CodeEnum {


    UnPaid(1, "未支付"),

    Paid(2, "待接诊"),

    WaitReply(6, "处理中"),

    OrderSuccess(4, "交易完成"),

    OrderFail(5, "交易失败"),//交易失败,退款完成

    WaitRefund(8, "退款中"),//交易失败,退款中

//    WaitDiagnosis(9, "待诊断"),//只做展示,不做业务和存储
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

    AnswerOrderStateEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static AnswerOrderStateEnum getValue(int code) {
        for (AnswerOrderStateEnum c : AnswerOrderStateEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return UnPaid;
    }

    public List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AnswerOrderStateEnum c : AnswerOrderStateEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
