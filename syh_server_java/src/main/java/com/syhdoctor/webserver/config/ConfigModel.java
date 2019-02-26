package com.syhdoctor.webserver.config;

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

    public static String APKPICDOMAIN;

    @Value("${base.apkpicurl}")
    public void setAPKPICDOMAIN(String value) {
        APKPICDOMAIN = value;
    }

    public static String APILINKURL;

    @Value("${base.apilinkurl}")
    public void setAPILINKURL(String value) {
        APILINKURL = value;
    }


    public static String WEBSOCKETLINKURL;

    @Value("${base.websocketlinkurl}")
    public void setWEBSOCKETLINKURL(String value) {
        WEBSOCKETLINKURL = value;
    }

    public static String WEBLINKURL;

    @Value("${base.weblinkurl}")
    public void setWEBLINKURL(String value) {
        WEBLINKURL = value;
    }

    public static String JIANGUAN;

    @Value("${base.jianguan}")
    public void setJIANGUAN(String value) {
        JIANGUAN = value;
    }

    public static String BEIAN;

    @Value("${base.beian}")
    public void setBEIAN(String value) {
        BEIAN = value;
    }

    public static String QINIULINK;

    @Value("${base.picdomain}")
    public void setQINIULINK(String value) {
        QINIULINK = value;
    }


    public static String LOCALIMGLINK;

    @Value("${base.localimg}")
    public void setLOCALIMGLINK(String value) {
        LOCALIMGLINK = value;
    }

    //七牛相关参数
    public static class QINIU {

        public static final String BUCKET = "syhdoctor";
    }

    public static String UNIONPAYAPPID;

    @Value("${unionpay.appid}")
    public void setUNIONPAYAPPID(String value) {
        UNIONPAYAPPID = value;
    }

    public static String UNIONPAYSECRET;

    @Value("${unionpay.secret}")
    public void setUNIONPAYSECRET(String value) {
        UNIONPAYSECRET = value;
    }

    public static String UNIONPAYSIGNATURE;

    @Value("${unionpay.signature}")
    public void setUNIONPAYSIGNATURE(String value) {
        UNIONPAYSIGNATURE = value;
    }

    /**
     * 第三方用户注册来源渠道
     */
    public static class USER_CHANNEL {
        public static final int WECHAT = 1;//微信公众号进入
        public static final int ALI = 2;//支付宝生活号进入
        public static final int WECHAT_WEB = 3;//微信网页
        public static final int ALI_WEB = 4;//支付宝网页进入
        public static final int WECHAT_QRCODE = 5;//扫码进入
        public static final int ALI_QRCODE = 6;//扫码进入
    }
}
