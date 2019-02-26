package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 财务订单状态
 */
public enum FinanceOrderStateEnum implements CodeEnum {


    Submit(1, "打款成功"),

    Examine(2, "打款失败"),

    //Confirm(3, "打款成功"),
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

    FinanceOrderStateEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static FinanceOrderStateEnum getValue(int code) {
        for (FinanceOrderStateEnum c : FinanceOrderStateEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return Submit;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (FinanceOrderStateEnum c : FinanceOrderStateEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
