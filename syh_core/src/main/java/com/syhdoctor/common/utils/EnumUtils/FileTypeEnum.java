package com.syhdoctor.common.utils.EnumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件类型
 */
public enum FileTypeEnum implements CodeEnum {


    Voice(1, "语音"), Text(2, "文本"), Picture(3, "图片"), Video(3, "视频"),;


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

    FileTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static FileTypeEnum getValue(int code) {
        for (FileTypeEnum c : FileTypeEnum.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return Voice;
    }

    public static List<Map<String, Object>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (FileTypeEnum c : FileTypeEnum.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getCode());
            map.put("value", c.getMessage());
            list.add(map);
        }
        return list;
    }
}
