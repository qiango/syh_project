package com.syhdoctor.webserver.thirdparty.wechat;

import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.common.utils.http.HttpUtil;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.wechat.AccessTokenService;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.impl.WxMpServiceOkHttpImpl;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Lock;


public class WxMpCustomServiceImpl extends WxMpServiceOkHttpImpl {
    private AccessTokenService accessTokenService;

    public WxMpCustomServiceImpl(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @Override
    public String getAccessToken(boolean forceRefresh) throws WxErrorException {
        this.log.info("WxMpCustomServiceImpl is running");
        Lock lock = this.getWxMpConfigStorage().getAccessTokenLock();
        try {
            lock.lock();
            Map<String, Object> token = accessTokenService.getAccessToken();
            long createtime = ModelUtil.getLong(token, "createtime", 0);
            this.log.info("WxMpCustomServiceImpl createtime = " + createtime + "|time=" + UnixUtil.getNowTimeStamp() + "|istoken=" + this.getWxMpConfigStorage().isAccessTokenExpired());
            log.info("isonline>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + ConfigModel.ISONLINE);
            if (ConfigModel.ISONLINE.equals("1")) {
                if ((createtime == 0 || UnixUtil.getNowTimeStamp() - createtime > 2 * 3600 * 1000)) {
                    this.log.info("WxMpCustomServiceImpl params " + "appid=" + this.getWxMpConfigStorage().getAppId() + "|secret=" + this.getWxMpConfigStorage().getSecret());
                    String url = String.format(GET_ACCESS_TOKEN_URL,
                            this.getWxMpConfigStorage().getAppId(), this.getWxMpConfigStorage().getSecret());

                    Request request = new Request.Builder().url(url).get().build();
                    Response response = getRequestHttpClient().newCall(request).execute();
                    String resultContent = response.body().string();
                    WxError error = WxError.fromJson(resultContent);
                    if (error.getErrorCode() != 0) {
                        throw new WxErrorException(error);
                    }
                    WxAccessToken accessToken = WxAccessToken.fromJson(resultContent);
                    this.getWxMpConfigStorage().updateAccessToken(accessToken.getAccessToken(),
                            accessToken.getExpiresIn());

                    accessTokenService.setAccessToken(accessToken.getAccessToken());
                } else {
                    this.getWxMpConfigStorage().updateAccessToken(ModelUtil.getStr(token, "access_token"), 7200 - (int) (System.currentTimeMillis() - createtime));
                }
            } else {
                Map<String, Object> map = HttpUtil.getInstance().get("https://www.syhdoctor.com/api/Menu/getToken");
                this.getWxMpConfigStorage().updateAccessToken(ModelUtil.getStr(map, "access_token"),
                        7200 - (int) (System.currentTimeMillis() - ModelUtil.getInt(map, "expires_in")));
            }
        } catch (IOException e) {
            this.log.error("WxMpCustomServiceImpl > getAccessToken > error ", e);
        } finally {
            lock.unlock();
        }
        return this.getWxMpConfigStorage().getAccessToken();
    }

    public static void main(String[] args) {
        String s = FileUtil.txt2String(new File("/home/qwq/桌面/wxtoken.txt"));
        System.out.println(s);
    }
}
