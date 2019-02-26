package com.syhdoctor.common.utils.http;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.FormBody;

import java.util.HashMap;
import java.util.Map;

public class HttpParamModel {
    private FormBody.Builder form;
    private Map<String, Object> obj;

    public HttpParamModel() {
        form = new FormBody.Builder();
        obj = new HashMap<>();
    }

    public FormBody.Builder getForm() {
        return form;
    }

    public void add(String name, Object value) {
        if (name == null) {
            name = "";
        }
        if (value == null) {
            value = "";
        }
        if (value instanceof JSONArray) {
            form.add(name, value.toString());
            obj.put(name, value.toString());
        } else if (value instanceof JSONObject) {
            form.add(name, value.toString());
            obj.put(name, value.toString());
        } else {
            form.add(name, String.valueOf(value));
            obj.put(name, String.valueOf(value));
        }
    }

    @Override
    public String toString() {
        return "HttpParamModel{" +
                "params=" + obj.toString() +
                '}';
    }
}
