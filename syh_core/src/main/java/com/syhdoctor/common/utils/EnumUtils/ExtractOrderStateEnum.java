package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 医生体现订单状态
 */
public enum ExtractOrderStateEnum implements CodeEnum {


    Submit(1, "审核中"),

    Auditfailure(2, "审核失败"),

    Auditsuccess(3, "审核成功"),

    Remittancesuccess(4, "打款失败"),

    Remittancefailure(5, "打款成功"),
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

    ExtractOrderStateEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ExtractOrderStateEnum getValue(int code) {
        for (ExtractOrderStateEnum c : ExtractOrderStateEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return Submit;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ExtractOrderStateEnum c : ExtractOrderStateEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
