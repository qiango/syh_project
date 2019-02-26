package com.syhdoctor.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {


    private static Logger log = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * 单例模式
     */
    private static volatile JsonUtil instance = null;

    /**
     * 单例模式初始化工具类
     *
     * @return JsonUtil
     */
    public static JsonUtil getInstance() {
        synchronized (JsonUtil.class) {
            if (instance == null) {
                instance = new JsonUtil();
            }
        }
        return instance;
    }


    /**
     * 对象转换json字符串
     *
     * @param object 需要转换json的对象
     * @return String  json字符串
     */
    public String toJson(Object object) {
        String result = "";
        try {
            if (object != null) {
                result = JSONObject.toJSONString(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("JsonUtil toJson error ", e);
        }
        return result;
    }

    public String gsonToJson(Object object) {
        String result = "";
        try {
            if (object != null) {
                result = new Gson().toJson(object);
            }
        } catch (Exception e) {
            log.error("JsonUtil gsonToJson error ", e);
        }
        return result;
    }

    /**
     * json字符串转换为对象
     *
     * @param json 源数据
     * @param cls  需要转换的类型
     * @return T  返回对应的数据
     */
    public <T> T fromJson(String json, Class<T> cls) {
        T t = null;
        try {
            t = JSON.parseObject(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("JsonUtil fromJson error ", e);
        }
        return t;
    }


    /**
     * json转成Map对象
     *
     * @param json
     * @return Map对象
     */
    public Map<String, Object> jsonToMap(JSONObject json) {
        Map<String, Object> temp = new HashMap<>();
        for (String key : json.keySet()) {
            Object obj = json.get(key);
            if (obj instanceof JSONObject) {
                temp.put(key, jsonToMap((JSONObject) obj));
            } else if (obj instanceof JSONArray) {
                JSONArray array = (JSONArray) obj;
                List<Object> list = new ArrayList<>();
                for (Object val : array) {
                    if (val instanceof JSONObject) {
                        list.add(jsonToMap((JSONObject) val));
                    } else {
                        list.add(val);
                    }
                }
                temp.put(key, list);
            } else {
                if (null != obj && !"null".equals(obj.toString().toLowerCase())) {
                    temp.put(key, obj);
                }
            }
        }
        return temp;
    }

    /**
     * map转换成Json对象
     *
     * @param value
     * @return Map对象
     */
    public JSONObject mapToJson(Map<?, ?> value) {
        JSONObject temp = new JSONObject();
        for (Object tKey : value.keySet()) {
            String key = (String) tKey;
            Object obj = value.get(key);
            if (obj instanceof Map) {
                temp.put(key, mapToJson((Map<?, ?>) obj));
            } else if (obj instanceof List) {
                List<?> list = (List<?>) obj;
                JSONArray array = new JSONArray();
                for (Object val : list) {
                    if (val instanceof Map) {
                        array.add(mapToJson((Map<?, ?>) val));
                    } else {
                        array.add(val);
                    }
                }
                temp.put(key, array);
            } else {
                temp.put(key, obj);
            }
        }
        return temp;
    }
}
