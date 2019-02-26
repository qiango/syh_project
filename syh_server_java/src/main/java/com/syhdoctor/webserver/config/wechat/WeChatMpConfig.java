package com.syhdoctor.webserver.config.wechat;

import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.webserver.thirdparty.wechat.WxMpCustomServiceImpl;
import com.syhdoctor.webserver.service.wechat.AccessTokenService;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class WeChatMpConfig {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AccessTokenService accessTokenService;

    public static String BASEFILEPATH;

    @Value("${base.filepath}")
    public void setBASEFILEPATH(String value) {
        BASEFILEPATH = value;
    }

    @Value("${wechat.syhweb.appid}")
    private String appid;

    @Value("${wechat.syhweb.secret}")
    private String secret;

    @Bean(name = "wxMpService")
    public WxMpService wxMpService() {
        return wxMpService(appid, secret);
    }

    private WxMpService wxMpService(String appid, String secret) {
        WxMpService wxMpService = new WxMpCustomServiceImpl(accessTokenService);
        WxMpInMemoryConfigStorage wxMpConfigStorage = new WxMpInMemoryConfigStorage();
        wxMpConfigStorage.setAppId(appid);
        wxMpConfigStorage.setSecret(secret);
        String fileUri = BASEFILEPATH + FileUtil.getTempPath(FileUtil.FILE_TEMP_PATH);
        if (!FileUtil.validateFile(fileUri)) {
            FileUtil.createFile(fileUri);
        }
        wxMpConfigStorage.setTmpDirFile(new File(fileUri));
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
        return wxMpService;
    }
}
