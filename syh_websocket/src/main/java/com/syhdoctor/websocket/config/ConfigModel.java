package com.syhdoctor.websocket.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigModel {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    public static String BASEFILEPATH;

    @Value("${base.filepath}")
    public void setBASEFILEPATH(String value) {
        BASEFILEPATH = value;
    }

    public static String ISONLINE;

    @Value("${base.isonline}")
    public void setISONLINE(String value) {
        ISONLINE = value;
    }

    public static String DOCTORPICDOMAIN;

    @Value("${base.doctorpicurl}")
    public void setDOCTORPICDOMAIN(String value) {
        DOCTORPICDOMAIN = value;
    }


    public static String QINIULINK;

    @Value("${base.picdomain}")
    public void setQINIULINK(String value) {
        QINIULINK = value;
    }

}
