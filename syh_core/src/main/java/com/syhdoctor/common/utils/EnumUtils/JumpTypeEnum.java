package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 授权跳转类型
 */
public enum JumpTypeEnum implements CodeEnum {

    CourseList(1, "大学列表"),

    CourseDetail(2, "大学详情"),

    ArticleList(3, "头条列表"),

    ArticleDetail(4, "头条详情"),

    DoctorList(5, "医生列表"),

    DoctorDetail(6, "医生详情"),

    AnswerOrder(7, "图文咨询"),

    PhoneOrder(8, "电话咨询"),

    PersonalCenter(9, "个人中心"),

    SpecialtiesDetail(10, "特色专科详情"),

    CounselingDetail(11, "专病咨询详情"),

    MDTSpecialtiesDetail(12, "MDT特色专科详情"),

    GreenChannel(13, "绿色通道"),

    SpecialtiesDetailList(14, "特色专科列表");

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

    JumpTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static JumpTypeEnum getValue(int code) {
        for (JumpTypeEnum c : JumpTypeEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return CourseList;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (JumpTypeEnum c : JumpTypeEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }


}
