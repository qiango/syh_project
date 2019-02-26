package com.syhdoctor.common.utils.EnumUtils;

public enum TemplateEnum implements StingCodeEnum {


    UserCloseOrder("_jWEYfqH3DtWpezpQbxQbKTyZ9Sj1UzG23AhRgpKDCM", "用户关闭订单，推送医生"),


    DoctorNewOrdesr("atZ98K1UKFUsmT8rpqvocSA2U5VkerhmHa45gyOtIzc", "医生新订单,推送医生"),


    YERemindUserNews("KfvmAWiK4-ylAIbcrNdCz2MV1Rco9Mr6FZ-VyvrpZLk", "给育儿公众号，用户推送医生回复消息"),
    ;



    private String code;

    private String message;

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    TemplateEnum(String code, String message) {
        this.code = code;

        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }
}
