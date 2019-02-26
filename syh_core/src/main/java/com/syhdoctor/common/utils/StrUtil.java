package com.syhdoctor.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtil {
    /**
     * 删除html富文本里面的样式和js脚本
     *
     * @param htmlStr 源数据
     * @return java.lang.String
     */
    public static String delHTMLTag(String htmlStr) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); //过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); //过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }


    /**
     * 验证字符串是否为空
     *
     * @param values 需要验证的字符串数组
     * @return boolean 是否存在任意一个为空
     */
    public static boolean isEmpty(String... values) {
        boolean result = false;
        for (String value : values) {
            result = StringUtils.isBlank(value);
            if (result) {
                break;
            }
        }
        return result;
    }

    /**
     * 检查指定的字符串列表是否不为空。
     */
    public static boolean isNotEmpty(String... values) {
        boolean result = true;
        if (values == null || values.length == 0) {
            result = false;
        } else {
            for (String value : values) {
                result &= !isEmpty(value);
            }
        }
        return result;
    }

    /**
     * 字符串乱码转换
     *
     * @param value 需要转换的字符
     * @return String java.lang.String
     */
    public static String formatIso(String value) {
        String result = "";
        try {
            if (null != value && !"".equals(value)) {
                result = new String(value.getBytes(), "iso-8859-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 中文decode编码
     *
     * @param value 需要编码的字符串
     * @return 编码过的字符串
     */
    public static String encode(String value) {
        String result = "";
        try {
            result = URLEncoder.encode(value, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 中文decode解码
     *
     * @param value 需要解码的字符串
     * @return 解码过的字符串
     */
    public static String decode(String value) {
        String result = "";
        try {
            result = URLDecoder.decode(value, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断是不是网页链接
     *
     * @param url 网页地址
     * @return true 是http链接 false 不是http链接
     */
    public static boolean isHttpUrl(String url) {
        return url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://");
    }

    public static String getUrl(String url, Map<?, ?> params) {
        String query = buildQuery(params);
        if (isEmpty(query)) {
            return url;
        } else {
            return url + "?" + query;
        }
    }

    public static String buildQuery(Map<?, ?> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder query = new StringBuilder();
        Set<?> entries = params.entrySet();
        boolean hasParam = false;

        for (Object obj : entries) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) obj;
            String name = (String) entry.getKey();
            String value = (String) entry.getValue();
            // 忽略参数名或参数值为空的参数
            if (StrUtil.isNotEmpty(name, value)) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }
                query.append(name).append("=").append(StrUtil.encode(value));
            }
        }
        return query.toString();
    }


    /**
     * 取整
     *
     * @param value 源数据
     * @return
     */
    public static BigDecimal getIntegerBigDecimal(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        } else {
            return value.setScale(0, BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    }


    public static String getDiscount(double v) {
        try {
            double tempD = v * 10;
            BigDecimal big = new BigDecimal(tempD);
            double temp = big.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            if (temp >= 10) {
                return "无折扣";
            } else {
                String str = temp + "";
                if (temp < 1) {
                    return temp + "折";
                } else {
                    if (str.endsWith(".0")) {
                        str = str.replaceAll(".0", "");
                    } else {
                        str = str.replaceAll("\\.", "");
                    }
                }
                return str + "折";
            }
        } catch (Exception e) {
            return "无折扣";
        }
    }
}
