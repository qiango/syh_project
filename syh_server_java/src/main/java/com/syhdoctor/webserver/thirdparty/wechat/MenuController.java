package com.syhdoctor.webserver.thirdparty.wechat;


import com.syhdoctor.common.utils.EnumUtils.OpenTypeEnum;
import com.syhdoctor.common.utils.JsonUtil;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.XmlUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.user.UserService;
import com.syhdoctor.webserver.service.wechat.AccessTokenService;
import com.syhdoctor.webserver.service.wechat.WeChatService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/Menu")
public class MenuController extends BaseController {


    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private UserService userService;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private AccessTokenService accessTokenService;

    /**
     * 接收微信通知验证
     */
    @GetMapping(value = "/getWxQrCodeParam")
    @ResponseBody
    public String getWxQrCodeParam(@RequestParam Map<String, Object> params) {
        log.info("Menu>getWxQrCodeParam   " + params);
        return ModelUtil.getStr(params, "echostr");
    }

    /**
     * 接收微信通知验证
     */
    @GetMapping(value = "/getToken")
    @ResponseBody
    public Map<String, Object> getToken(@RequestParam Map<String, Object> params) {
        log.info("Menu>getToken   " + params);
        try {
            wxMpService.getAccessToken();
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return accessTokenService.getAccessToken();
    }


    /**
     * 接收微信通知
     *
     * @param value
     */
    @PostMapping(value = "/getWxQrCodeParam", produces = {"application/xml"})
    @ResponseBody
    public String getWxQrCodeParam(@RequestBody String value) {
        log.info("Menu>getWxQrCodeParam 参数   " + value);
        Map<String, Object> result = XmlUtil.xmlToMap(value);
        return weChatService.getWxQrCodeParam(result);
    }

    /**
     * 授权链接
     *
     * @param target 自定义参数
     */
    @GetMapping("/GetCode")
    public String GetCode(@RequestParam String target) {
        String url = ConfigModel.APILINKURL + "Menu/OAuthResponse";
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_BASE, target);
        redirectUrl = redirectUrl.replace("#wechat_redirect", "&connect_redirect=1#wechat_redirect");
        log.info("育儿授权链接" + redirectUrl);
        return "redirect:" + redirectUrl;
    }

    /**
     * @param params 微信回调参数
     */
    @RequestMapping("/OAuthResponse")
    public String OAuthResponse(@RequestParam Map<String, Object> params) {
        log.info("Menu>oAuthResponse   " + params);
        String code = ModelUtil.getStr(params, "code");
        String state = ModelUtil.getStr(params, "state");
        if (!StrUtil.isEmpty(code)) {
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
            try {
                wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
                log.info("微信网页授权获取Token   " + JsonUtil.getInstance().toJson(wxMpOAuth2AccessToken.getOpenId()));
            } catch (Exception e) {
                log.error("【微信网页授权】  ", e);
            }
            return "redirect:" + userService.getRedirectUrl(state, wxMpOAuth2AccessToken.getOpenId(), ConfigModel.USER_CHANNEL.WECHAT_WEB, OpenTypeEnum.Wechat.getCode());
        } else {
            return null;
        }
    }

    /**
     * 配置
     *
     * @param url 参数
     */
    @RequestMapping("/getWXJSConfig")
    @ResponseBody
    public Map<String, Object> getWXJSConfig(@RequestParam String url) {
        log.info("Menu>getWXJSConfig   " + url);
        Map<String, Object> result = new HashMap<>();
        try {
            WxJsapiSignature wxJsapiSignature = wxMpService.createJsapiSignature(url);
            result.put("data", wxJsapiSignature);
            setOkResult(result, "成功");
        } catch (Exception e) {
            setErrorResult(result, "系统异常");
            e.printStackTrace();
        }
        return result;
    }
}
