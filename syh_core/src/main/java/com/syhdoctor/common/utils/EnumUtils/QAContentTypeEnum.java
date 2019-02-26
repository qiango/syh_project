package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图文聊天内容类型
 */
public enum QAContentTypeEnum implements CodeEnum {

    Voice(1, "语音"),
    Text(2, "文本"),
    Picture(3, "图片"),
    Prescription(4, "处方审核成功"),
    DiseaseUser(5, "默认病症问题用户显示"),
    DiseaseDoctor(6, "默认病症问题医生显示"),
    UserInfo(7, "用户信息"),
    Tips(8, "开始结束提示"),
    UserTips(9, "用户回答问题提示"),
    DoctorTips(10, "用户回答问题医生提示(医生提示专用)"),
    Submit(11, "提交"),

    UserAgreenCloseToDoctor(13,"用户同意关闭医生提示"),
    DoctorClose(12,"医生图文结束用户提示"),
    UserAgreenCloseToUser(14,"用户同意关闭用户提示"),
    UserNotAgreenCloseToDoctor(15,"用户不同意关闭医生提示"),
    UserNotAgreenCloseToUser(16,"用户不同意关闭用户提示"),
    InExaminePrescription(17,"处方审核中"),
    PrescriptionFail(18,"处方审核失败");

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

    QAContentTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static QAContentTypeEnum getValue(int code) {
        for (QAContentTypeEnum c : QAContentTypeEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return Picture;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QAContentTypeEnum c : QAContentTypeEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
