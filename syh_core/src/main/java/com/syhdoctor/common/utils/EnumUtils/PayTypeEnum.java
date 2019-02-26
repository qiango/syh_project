package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 支付类型
 */
public enum PayTypeEnum implements CodeEnum {


    WxApp(1, "微信APP"),

    AliApp(2, "支付宝APP"),

    UnionPay(3, "银联"),

    ZERO(4, "零元支付"),

    RechargeableCard(5, "充值卡"),

    Wallet(6, "钱包支付"),

    WxWeb(7, "微信Web"),

    AliWeb(8, "支付宝Web"),

    VipFree(9, "VIP永久免费"),

    VipZero(10, "VIP次数免费"),

    VipDiscount(11, "VIP折扣"),

    Kangyang(12, "康养支付"),
    ;


    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private Integer code;

    private String message;

    PayTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static PayTypeEnum getValue(int code) {
        for (PayTypeEnum c : PayTypeEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return WxApp;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PayTypeEnum c : PayTypeEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
