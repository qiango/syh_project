package com.syhdoctor.webtask.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigModel {
    public static String BASEFILEPATH;

    @Value("${base.filepath}")
    public void setBASEFILEPATH(String value) {
        BASEFILEPATH = value;
    }

    public static String APILINKURL;
    @Value("${base.apilinkurl}")
    public void setAPILINKURL(String value) {
        APILINKURL = value;
    }

    public static String ISONLINE;

    @Value("${base.isonline}")
    public void setISONLINE(String value) {
        ISONLINE = value;
    }

    public static String WEBSOCKETLINKURL;

    @Value("${base.websocketlinkurl}")
    public void setWEBSOCKETLINKURL(String value) {
        WEBSOCKETLINKURL = value;
    }


    //七陌相关参数
    public static class QIMO {
        public static final String ACCOUNTID = "N00000032760";
        public static final String APISECRET = "0a8b3cb0-b0b0-11e8-ac83-613efed083c8";
        public static final String SERVICENO = "01025270191";
    }

    //七牛相关参数
    public static class QINIU {

        public static final String BUCKET = "syhdoctor";
    }
}
