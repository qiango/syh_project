package com.syhdoctor.common.utils.EnumUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易记录支出或者收入
 */
public enum MoneyTypeEnum implements CodeEnum {

    All(0, "全部"),
    Income(1, "收入"),
    Expenditure(2, "支出");

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

    MoneyTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getName(int code) {
        for (MoneyTypeEnum c : MoneyTypeEnum.values()) {
            if (c.code == code) {
                return c.getMessage();
            }
        }
        return null;
    }

    public static MoneyTypeEnum getValue(int code) {
        for (MoneyTypeEnum c : MoneyTypeEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return MoneyTypeEnum.All;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MoneyTypeEnum c : MoneyTypeEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
