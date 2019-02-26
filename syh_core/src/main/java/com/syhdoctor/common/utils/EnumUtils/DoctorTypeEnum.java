package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DoctorTypeEnum implements CodeEnum {

    trialDoctor(1, "审方医师"),

    DctorDiagnosis(2, "诊疗医师"),

    DoctorExpert(3, "专家"),

    DoctorAdviser(4, "顾问"),

    ReviewDoctor(5, "审核医生");

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

    DoctorTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static DoctorTypeEnum getValue(int code) {
        for (DoctorTypeEnum c : DoctorTypeEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return DoctorAdviser;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DoctorTypeEnum c : DoctorTypeEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }


}
