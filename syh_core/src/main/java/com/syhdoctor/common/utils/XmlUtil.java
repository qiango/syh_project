package com.syhdoctor.common.utils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlUtil {
    private static Logger log = LoggerFactory.getLogger(XmlUtil.class);

    /**
     * 将XML转化成Map对象
     *
     * @param value xml字符串
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    public static Map<String, Object> xmlToMap(String value) {
        // 将解析结果存储在HashMap中
        Map<String, Object> map = new HashMap<>();
        try {
            // 从request中取得输入流
            // 读取输入流
            SAXReader reader = new SAXReader();
            Document document = reader.read(new ByteArrayInputStream(value.getBytes()));
            // document.selectSingleNode("//")
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<?> elementList = root.elements();
            // 遍历所有子节点
            for (Object obj : elementList) {
                if (obj instanceof Element) {
                    Element e = (Element) obj;
                    map.put(e.getName(), e.getText());
                }
            }
            log.info("xml" + map.toString());
        } catch (Exception ex) {
            log.error("xmlToMap error", ex);
        }
        return map;
    }
}
