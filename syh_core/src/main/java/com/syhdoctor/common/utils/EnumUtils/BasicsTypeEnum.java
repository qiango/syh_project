package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * basics表type对应的说明
 */
public enum BasicsTypeEnum implements CodeEnum {


    drugPackage(1, "药品包装单位"),
    drugUsage(2, "药品用法"),
    drugCycle(3, "药品周期"),
    graphicOrderStatus(4, "医生咨询(图文)订单状态"),
    phoneOrderStatus(5, "急诊(电话)订单状态"),
    phoneAnswerStatus(6, "急诊(电话)接听状态"),
    messageType(7, "消息中心消息类型"),
    HomePage(17, "首页-图文资讯-支付详情页面"),
    HomePhone(18, "首页-电话咨询-支付详情页面"),
    ExpertPage(19, "专家详情-图文咨询-支付详情页面"),
    ExpertPhone(20, "专家详情-电话咨询-支付详情页面"),
    ExpertVideo(21, "专家详情-视x频咨询-支付详情页面"),
    ExtractOne(22, "提现文案（前）"),
    ExtractTwo(23, "提现文案（后）"),//废除
    ExtractMoney(24, "医生提现最低金额800"),
    SickTime(25, "患病时长"),
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

    BasicsTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static BasicsTypeEnum getValue(int code) {
        for (BasicsTypeEnum c : BasicsTypeEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return drugPackage;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BasicsTypeEnum c : BasicsTypeEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
