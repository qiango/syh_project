package com.syhdoctor.webserver.utils;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.syhdoctor.common.utils.StrUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qian.wang
 * @description
 * @date 2018/11/28
 */
public class JiebaUtils {

    public static String getName(String name) {
        //分词器
        if (name == null) {
            return "";
        }
        if (name.length() == 2) {
            name = name.substring(0, 1) + "%" + name.substring(1, name.length());
        } else {
            JiebaSegmenter segmenter = new JiebaSegmenter();
            if (!StrUtil.isEmpty(name)) {
                name = String.join("%", segmenter.sentenceProcess(name));
            }
        }
        return name;
    }



}
