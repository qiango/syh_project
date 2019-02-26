package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户注册渠道
 */
public enum RegisterChannelEnum implements CodeEnum {


    Android(1, "app安卓"),
    Ios(2, "app苹果"),
    Pc(3, "pc"),
    Admin(4, "后台"),
    Kangyang(5, "乐养云"),
    WechatAndroid(6, "微信安卓"),
    WechatIos(7, "微信苹果");


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

    RegisterChannelEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static RegisterChannelEnum getValue(int code) {
        for (RegisterChannelEnum c : RegisterChannelEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return RegisterChannelEnum.Android;
    }


    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RegisterChannelEnum c : RegisterChannelEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
