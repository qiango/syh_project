package com.syhdoctor.webtask.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.syhdoctor.common.pay.*;
import com.syhdoctor.common.utils.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ThirdPartyPayConfig {

    public static String BASEFILEPATH;

    @Value("${base.filepath}")
    public void setBASEFILEPATH(String value) {
        BASEFILEPATH = value;
    }

    @Value("${wechat.syhapp.appid}")
    private String syhAppid;
    @Value("${wechat.syhapp.mchid}")
    private String syhMchId;
    @Value("${wechat.syhapp.mchkey}")
    private String syhMchKey;
    @Value("${wechat.syhapp.keypath}")
    private String syhKeyPath;

    @Value("${wechat.syhweb.appid}")
    private String syhWebAppid;
    @Value("${wechat.syhweb.mchid}")
    private String syhWebMchId;
    @Value("${wechat.syhweb.mchkey}")
    private String syhWebMchKey;
    @Value("${wechat.syhweb.keypath}")
    private String syhWebKeyPath;

    private WxPayService wxPayService(String appid, String mchId, String mchKey, String keyPath) {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(appid);
        payConfig.setMchId(mchId);
        payConfig.setMchKey(mchKey);
        payConfig.setKeyPath(keyPath);
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }

    @Bean(name = "aliWebPayImpl")
    public IPayService aliWebPayImpl() {
        return new AliWebPayServiceImpl();
    }

    @Bean(name = "aliAppPayImpl")
    public IPayService aliAppPayImpl() {
        return new AliAppPayServiceImpl();
    }

    @Bean(name = "wechatWebPayImpl")
    public IPayService wechatWebPayImpl() {
        return new WechatWebPayServiceImpl(wxPayService(syhWebAppid, syhWebMchId, syhWebMchKey, BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_STATIC_PATH, syhWebKeyPath)));
    }

    @Bean(name = "wechatAppPayImpl")
    public IPayService wechatAppPayImpl() {
        return new WechatAppPayServiceImpl(wxPayService(syhAppid, syhMchId, syhMchKey, BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_STATIC_PATH, syhKeyPath)));
    }
}
