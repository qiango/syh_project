package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频订单websocket提示类型
 */
public enum VideoTipTypeEnum implements CodeEnum {

    twoMinute(1, "医生用户提示俩分钟订单结束"),

    UserClose(2, "患者已关闭问诊,感谢您的解答,谢谢."),//用户关闭订单 医生提示弹窗结束

    AutoClose(3, "医生订单结束自动结束"),

    userAgree(4, "本次问诊已经结束,感谢您的解答,谢谢."),//医生订单结束用户同意结束

    userRefuse(5, "用户不同意结束问诊,请继续解答."),//用户不同意结束

    FiveSeconds(6, "用户医生5秒提醒"),//交易失败,退款中

    DoctorClose(7, "医生请求结束此次问诊,是否确认结束本次问诊?"),//医生询问关闭订单用户提示
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

    VideoTipTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static VideoTipTypeEnum getValue(int code) {
        for (VideoTipTypeEnum c : VideoTipTypeEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return twoMinute;
    }

    public List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (VideoTipTypeEnum c : VideoTipTypeEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
