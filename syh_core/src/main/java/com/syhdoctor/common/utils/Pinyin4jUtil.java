package com.syhdoctor.common.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class Pinyin4jUtil {


    private static volatile Pinyin4jUtil instance = null;
    //pinyin4j格式类
    private HanyuPinyinOutputFormat format = null;
    //拼音字符串数组
    private String[] pinyin;

    //通过构造方法进行初始化
    public Pinyin4jUtil() {
        format = new HanyuPinyinOutputFormat();
        /*
         * 设置需要转换的拼音格式
         * 以天为例
         * HanyuPinyinToneType.WITHOUT_TONE 转换为tian
         * HanyuPinyinToneType.WITH_TONE_MARK 转换为tian1
         * HanyuPinyinVCharType.WITH_U_UNICODE 转换为tiān
         *
         */
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        pinyin = null;
    }

    public static Pinyin4jUtil getInstance() {
        synchronized (Pinyin4jUtil.class) {
            if (instance == null) {
                instance = new Pinyin4jUtil();
            }
        }
        return instance;
    }

    /**
     * 对单个字进行转换
     *
     * @param pinYinStr 需转换的汉字字符串
     * @return 拼音字符串数组
     */
    public String getCharPinYin(char pinYinStr) {

        try {
            //执行转换
            pinyin = PinyinHelper.toHanyuPinyinStringArray(pinYinStr, format);

        } catch (BadHanyuPinyinOutputFormatCombination e) {
            //TODO Auto-generated catch block
            e.printStackTrace();
        }

        //pinyin4j规则，当转换的符串不是汉字，就返回null
        if (pinyin == null || pinyin.length == 0) {
            return null;
        }
        //多音字会返回一个多音字拼音的数组，pinyiin4j并不能有效判断该字的读音

        return pinyin[0];
    }

    /**
     * 对单个字进行转换
     *
     * @param pinYinStr
     * @return
     */
    public String getStringPinYin(String pinYinStr) {
        StringBuffer sb = new StringBuffer();
        String tempStr;
        if (!StrUtil.isEmpty(pinYinStr)) {
            //循环字符串
            for (int i = 0; i < pinYinStr.length(); i++) {
                try {
                    tempStr = this.getCharPinYin(pinYinStr.charAt(i));
                    if (tempStr == null) {
                        //非汉字直接拼接
                        sb.append(pinYinStr.charAt(i));
                    } else {
                        sb.append(tempStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * @Title: 获取中文串拼音首字母，英文字符不变
     * @methodName: getFirstSpell
     */
    public String getFirstSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++)
            if (arr[i] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (temp != null && temp.length > 0) {
                        pybf.append(temp[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }
        return pybf.toString().replaceAll("\\W", "").trim();
    }

}
