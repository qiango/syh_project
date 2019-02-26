package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易记录类型
 */
public enum TransactionTypeStateEnum implements CodeEnum {


    Rechargeable(1, "充值卡充值"),

    Wechat(2, "微信充值"),

    Ali(3, "支付宝充值"),

    /*    Department(4, "急诊服务"),*/

    Phone(5, "电话问诊"),

    /*Outpatient(6, "门诊服务"),*/

    Graphic(7, "图文咨询"),

    Video(8, "视频问诊"),

    VIP(9, "VIP服务"),

    Extract(10, "提现到银行卡"),

    Test(11, "测试"),

    Refund(12, "发生退款"),

    Platformreward(13, "平台奖励"),

    OpenVip(14, "开通尊享会员"),

    RenewVip(15, "续费尊享会员"),

    Other(16, "其他"),

    ExtractRefund(17, "提现退款"),

    Green(18, "绿通充值"),

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

    TransactionTypeStateEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static TransactionTypeStateEnum getValue(int code) {
        for (TransactionTypeStateEnum c : TransactionTypeStateEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return Rechargeable;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TransactionTypeStateEnum c : TransactionTypeStateEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
