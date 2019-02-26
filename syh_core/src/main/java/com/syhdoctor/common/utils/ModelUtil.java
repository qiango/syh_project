package com.syhdoctor.common.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelUtil {
    /**
     * 类型转换 string转int
     *
     * @param value 原始值
     * @param def   默认值
     * @return int
     */
    public static int strToInt(String value, int def) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 类型转换 string转long
     *
     * @param value 原始值
     * @param def   默认值
     * @return long
     */
    public static long strToLong(String value, long def) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 类型转换 string转long
     *
     * @param value 原始值
     * @param def   默认值
     * @return long
     */
    public static Long strToLong(String value, Long def) {
        try {
            if (StrUtil.isEmpty(value)) {
                return def;
            } else {
                return Long.parseLong(value);
            }
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 类型转换 string转BigDecimal
     *
     * @param value 原始值
     * @param def   默认值
     * @return BigDecimal
     */
    public static BigDecimal strToDec(String value, BigDecimal def) {
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 类型转换 string转BigDecimal 保留两位
     *
     * @param value 原始值
     * @param def   默认值
     * @return BigDecimal
     */
    public static BigDecimal strToDec2(String value, BigDecimal def) {
        try {
            BigDecimal bigDecimal = new BigDecimal(value);
            return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {
            return def.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    }


    /**
     * 类型转换 string转Boolean
     *
     * @param value 原始值
     * @param def   默认值
     * @return Boolean
     */
    public static boolean strToBoolean(String value, boolean def) {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 类型转换 string转Double
     *
     * @param value 原始值
     * @param def   默认值
     * @return Double
     */
    public static Double strToDouble(String value, double def) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 获取Map中的值
     *
     * @param value 原始Map
     * @param key   数据的键
     * @param def   默认值
     * @return Boolean
     */
    public static boolean getBoolean(Map<?, ?> value, String key, boolean def) {
        if (value != null && value.containsKey(key)) {
            return strToBoolean(getStr(value, key), def);
        } else {
            return def;
        }
    }

    /**
     * 获取Map中的值
     *
     * @param value 原始Map
     * @param key   数据的键
     * @param def   默认值
     * @return BigDecimal
     */
    public static BigDecimal getDec(Map<?, ?> value, String key, BigDecimal def) {
        if (value != null && value.containsKey(key)) {
            return strToDec(getStr(value, key), def);
        } else {
            return def;
        }
    }


    /**
     * 获取Map中的值
     *
     * @param value 原始Map
     * @param key   数据的键
     * @param def   默认值
     * @return Double
     */
    public static Double getDouble(Map<?, ?> value, String key, double def) {
        if (value != null && value.containsKey(key)) {
            return strToDouble(getStr(value, key), def);
        } else {
            return def;
        }
    }

    /**
     * 获取Map中的值
     *
     * @param value 原始Map
     * @param key   数据的键
     * @param def   默认值
     * @return int
     */
    public static int getInt(Map<?, ?> value, String key, int def) {
        if (value != null && value.containsKey(key)) {
            return strToInt(getStr(value, key), def);
        } else {
            return def;
        }
    }

    public static Object getIntForNull(Map<?, ?> value, String key) {
        if (value != null && value.containsKey(key)) {
            String str = getStr(value, key);
            if(StringUtils.isEmpty(str)){
                return null;
            }else {
                return Integer.parseInt(str);
            }
        } else {
            return null;
        }
    }

    public static int getInt(Map<?, ?> value, String key) {
        return getInt(value, key, 0);
    }

    /**
     * 获取Map中的值
     *
     * @param value 原始Map
     * @param key   数据的键
     * @return java.lang.String
     */
    public static String getStr(Map<?, ?> value, String key) {
        return getStr(value, key, null);
    }


    /**
     * 获取Map中的值
     *
     * @param value 原始值
     * @return java.lang.String
     */
    public static String setLocalUrl(String value) {
        if (StrUtil.isEmpty(value)) {
            return "";
        } else {
            return value.replaceAll("/", "-");
        }
    }

    /**
     * 获取Map中的值
     *
     * @param value 原始Map
     * @param key   数据的键
     * @param def   默认值
     * @return java.lang.String
     */
    public static String getStr(Map<?, ?> value, String key, String def) {
        if (value != null && value.containsKey(key)) {
            if (value.get(key) == null) {
                return def;
            } else {
                return String.valueOf(value.get(key));
            }
        } else {
            return def;
        }
    }

    /**
     * 获取Map中的值
     *
     * @param value 原始Map
     * @param key   数据的键
     * @return map
     */
    public static Map<?, ?> getMap(Map<?, ?> value, String key) {
        if (value != null && value.containsKey(key)) {
            return (Map<?, ?>) value.get(key);
        } else {
            return new HashMap<>();
        }
    }

    /**
     * 获取Map中的值
     *
     * @param value 原始Map
     * @param key   数据的键
     * @param def   默认值
     * @return long
     */
    public static long getLong(Map<?, ?> value, String key, int def) {
        if (value != null && value.containsKey(key)) {
            return strToLong(getStr(value, key), def);
        } else {
            return def;
        }
    }

    /**
     * 获取Map中的值
     *
     * @param value 原始Map
     * @param key   数据的键
     * @param def   默认值
     * @return long
     */
    public static Long getLong(Map<?, ?> value, String key, Long def) {
        if (value != null && value.containsKey(key)) {
            return strToLong(getStr(value, key), def);
        } else {
            return def;
        }
    }

    /**
     * 获取Map中的值
     *
     * @param value 原始Map
     * @param key   数据的键
     * @return long
     */
    public static long getLong(Map<?, ?> value, String key) {
        return getLong(value, key, 0);
    }

    /**
     * 获取Map中的值
     *
     * @param value 原始Map
     * @param key   数据的键
     * @param def   默认值
     * @return java.com.syhdoctor.webserver.api.util.List<?>
     */
    public static List<?> getList(Map<?, ?> value, String key, List<?> def) {
        if (value != null && value.containsKey(key)) {
            Object obj = value.get(key);
            if (obj instanceof List) {
                return (List<?>) obj;
            } else if (obj instanceof String) {
                JSONArray array = (JSONArray) JSON.parse((String) obj);
                List<Object> tempList = new ArrayList<>();
                if (array != null) {
                    for (int i = 0; i < array.size(); i++) {
                        Object temp = array.get(i);
                        if (temp instanceof JSONObject) {
                            tempList.add(JsonUtil.getInstance().jsonToMap(array.getJSONObject(i)));
                        } else {
                            tempList.add(temp);
                        }
                    }
                }
                return tempList;
            } else {
                return def;
            }
        } else {
            return def;
        }
    }

    /**
     * 获取Map中的值
     *
     * @param value 原始Value* @param def   默认值
     * @return java.com.syhdoctor.webserver.api.util.List<?>
     */
    public static List<?> getList(String value, List<?> def) {
        JSONArray array = (JSONArray) JSON.parse(value);
        List<Object> tempList = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                Object temp = array.get(i);
                if (temp instanceof JSONObject) {
                    tempList.add(JsonUtil.getInstance().jsonToMap(array.getJSONObject(i)));
                } else {
                    tempList.add(temp);
                }
            }
            return tempList;
        } else {
            return def;
        }
    }

    /**
     * 根据逗号切割字符串用于 IN  关键字的查询
     *
     * @param value 逗号连接的字符串
     * @return id的列表数据
     */
    public static List<Long> splitStrToList(String value) {
        List<Long> temp = new ArrayList<>();
        if (!StrUtil.isEmpty(value)) {
            String[] cids = value.split(",");
            for (String cid : cids) {
                Long id = ModelUtil.strToLong(cid, 0);
                if (id > 0) {
                    temp.add(id);
                }
            }
        }
        return temp;
    }

    /**
     * 根据逗号切割字符串
     *
     * @param value 逗号连接的字符串
     * @return id的列表数据
     */
    public static List<String> splitStrToListString(String value) {
        List<String> temp = new ArrayList<>();
        if (!StrUtil.isEmpty(value)) {
            String[] cids = value.split(",");
            for (String cid : cids) {
                temp.add(cid);
            }
        }
        return temp;
    }
}
