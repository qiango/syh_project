package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 医生证件
 */
public enum FolderType implements CodeEnum {

    docPhoto(0, "医师认证照片文件", "docphoto"),

    card(1, "身份证", "card"),

    cert(2, "资格证", "cert"),

    certPrac(3, "执业证文件", "certprac"),

    titleCert(4, "职称证文件", "titlecert"),

    signature(5, "医生签名", "signature"),

    employFile(6, "互联网医院聘任合同", "employfile"),

    hosDigitalSign(7, "互联网医院聘任合同", "employfile"),

    multiSitedLicRecord(8, "多点执业文件", "multisitedlic"),
    ;

    private Integer code;

    private String message;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    FolderType(Integer code, String message, String value) {
        this.code = code;
        this.message = message;
        this.value = value;
    }

    public static FolderType getValue(int code) {
        for (FolderType c : FolderType.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return docPhoto;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (FolderType c : FolderType.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
