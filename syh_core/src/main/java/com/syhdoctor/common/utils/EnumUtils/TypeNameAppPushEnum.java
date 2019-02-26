package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//只针对app推送和消息列表推送（不针对其他typeName）
public enum TypeNameAppPushEnum implements CodeEnum {


    doNot(0, "不做任何操作"),
    answerOrderDetail(1, "咨询订单详情"),
    phoneOrderDetail(2, "电话订单详情"),
    doctorUserSignIn(3, "医生、用户签到"),
    article(4, "文章"),
    inquiryOrderEnd(5, "问诊订单结束(服务通知)"),
    inquiryDoctorReplyOrder(6, "问诊医生回复"),
    inquiryUserReplyOrder(7, "问诊用户回复"),
    departmentCallSuccessDoctorOrder(8, "医生急诊电话呼叫成功"),
    departmentCallFailDoctorOrder(9, "医生急诊电话呼叫失败"),
    departmentCallSuccessUserOrder(10, "用户急诊电话呼叫成功"),
    departmentCallFailUserOrder(11, "用户急诊电话呼叫失败"),
    SystemMessage(12, "系统消息"),
    VideoOrderDetail(13, "视频订单详细"),
    PhoneOrderTenTime(14, "电话订单前十分钟推送"),
    VideoOrderTenTime(15, "视频订单前十分钟推送"),
    VideoOrderStartTime(16, "视频订单开始时推送"),
    PhoneDiagnosisGuidance(17, "电话诊后指导给用户推送"),
    AnswerDiagnosisGuidance(18, "图文诊后指导给用户推送"),
    VideoDiagnosisGuidance(19, "视频诊后指导给用户推送"),
    DoctorAfter(20, "医生未回复一段时间后推送"),
    answerSuccessOrder(21, "图文成功"),
    answerFailOrder(22, "图文失败"),
    videoSuccessOrder(23, "视频成功"),
    videoFailOrder(24, "视频失败"),
    DoctorClose(25, "医生请求关闭图文订单"),
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

    TypeNameAppPushEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public static TypeNameAppPushEnum getValue(int code) {
        for (TypeNameAppPushEnum c : TypeNameAppPushEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return doNot;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TypeNameAppPushEnum c : TypeNameAppPushEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
