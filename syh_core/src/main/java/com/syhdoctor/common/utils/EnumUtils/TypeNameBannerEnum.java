package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//只针对广告图跳转（不针对其他typeName）
public enum TypeNameBannerEnum implements CodeEnum {

    doNot(-1, "不做任何操作"),
    articleList(1, "头条列表"),
    outpatientOrder(2, "门诊下单页面"),
    emergencyOrder(3, "急诊下单页面"),
    microclassDetail(4, "课堂内页(课程详情页)"),
    webLink(5, "外部网页链接"),
    greenChannel(6, "绿通"),
    expert(7, "专家"),
    vip(8, "会员"),
    ;

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

    @Override
    public Integer getCode() {
        return this.code;
    }

    TypeNameBannerEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public static TypeNameBannerEnum getValue(int code) {
        for (TypeNameBannerEnum c : TypeNameBannerEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return doNot;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TypeNameBannerEnum c : TypeNameBannerEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
