package com.syhdoctor.webserver.thirdparty.ali;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.StringUtils;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.syhdoctor.common.utils.EnumUtils.OpenTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.ali.AliService;
import com.syhdoctor.webserver.service.user.UserService;
import com.syhdoctor.webserver.thirdparty.ali.constants.AlipayServiceEnvConstants;
import com.syhdoctor.webserver.thirdparty.ali.dispatcher.Dispatcher;
import com.syhdoctor.webserver.thirdparty.ali.executor.ActionExecutor;
import com.syhdoctor.webserver.thirdparty.ali.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 支付宝生活号授权
 */
@Controller
@RequestMapping("/Ali/Authorization")
public class AliAuthorizationController extends BaseController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AliService aliService;

    @Autowired
    private UserService userService;

    /**
     * 授权链接
     *
     * @param target 自定义参数
     */
    @GetMapping("/getCode")
    public String GetCode(@RequestParam String target) {
        log.info("/Ali/Authorization>GetCode   " + target);
        String url = ConfigModel.APILINKURL + "/Ali/Authorization/OAuthResponse";
        String redirectUrl = String.format(com.syhdoctor.common.config.ConfigModel.AliMap.AUTHORIZE_URL, com.syhdoctor.common.config.ConfigModel.AliMap.APPID, com.syhdoctor.common.config.ConfigModel.AliMap.AUTH_BASE, StrUtil.decode(url), target);
        log.info("育儿授权链接" + redirectUrl);
        return "redirect:" + redirectUrl;
    }


    /**
     * @param params 支付宝回调参数
     */
    @RequestMapping("/OAuthResponse")
    public String OAuthResponse(@RequestParam Map<String, Object> params) {
        log.info("/Ali/Authorization>oAuthResponse   " + params);
        String code = ModelUtil.getStr(params, "auth_code");
        String state = ModelUtil.getStr(params, "state");
        log.info("auth_code:" + code + ",state:" + state);
        try {
            //3. 利用authCode获得authToken
            AlipaySystemOauthTokenRequest oauthTokenRequest = new AlipaySystemOauthTokenRequest();
            oauthTokenRequest.setCode(code);
            oauthTokenRequest.setGrantType(com.syhdoctor.common.config.ConfigModel.AliMap.GRANT_TYPE);
            AlipayClient alipayClient = new DefaultAlipayClient(com.syhdoctor.common.config.ConfigModel.AliMap.URL, com.syhdoctor.common.config.ConfigModel.AliMap.APPID, com.syhdoctor.common.config.ConfigModel.AliMap.RSA_PRIVATE_KEY, com.syhdoctor.common.config.ConfigModel.AliMap.FORMAT, com.syhdoctor.common.config.ConfigModel.AliMap.CHARSET, com.syhdoctor.common.config.ConfigModel.AliMap.ALIPAY_PUBLIC_KEY, com.syhdoctor.common.config.ConfigModel.AliMap.SIGNTYPE);
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient
                    .execute(oauthTokenRequest);
            System.out.println(oauthTokenResponse.getAlipayUserId());
            log.info("ali getAlipayUserId>>>" + oauthTokenResponse.getAlipayUserId());
            log.info("ali token>>>" + oauthTokenResponse.getAccessToken());
            log.info("用户id>>>" + oauthTokenResponse.getUserId());

            long id = userService.addUpdateUserOpen(oauthTokenResponse.getUserId(), ConfigModel.USER_CHANNEL.ALI, OpenTypeEnum.Ali.getCode());
            long userid = userService.getUserAccount(oauthTokenResponse.getUserId(), OpenTypeEnum.Ali.getCode());
            //成功获得authToken  授权方式为AUTH_USER 使用
            /*if (oauthTokenResponse.isSuccess()) {

                //4. 利用authToken获取用户信息
                AlipayUserInfoShareRequest userinfoShareRequest = new AlipayUserInfoShareRequest();
                AlipayUserInfoShareResponse userinfoShareResponse = alipayClient
                        .execute(userinfoShareRequest, oauthTokenResponse.getAccessToken());
                //成功获得用户信息
                if (null != userinfoShareResponse && userinfoShareResponse.isSuccess()) {
                    //这里仅是简单打印， 请开发者按实际情况自行进行处理
                    log.info("获取用户信息成功：" + userinfoShareResponse.getBody());

                } else {
                    //这里仅是简单打印， 请开发者按实际情况自行进行处理
                    log.info("获取用户信息失败");

                }
            } else {
                //这里仅是简单打印， 请开发者按实际情况自行进行处理
                log.info("authCode换取authToken失败");
            }*/
            return "redirect:" + userService.getRedirectUrl(state, oauthTokenResponse.getUserId(), ConfigModel.USER_CHANNEL.ALI_WEB, OpenTypeEnum.Ali.getCode());
        } catch (AlipayApiException alipayApiException) {
            //自行处理异常
            alipayApiException.printStackTrace();
        }
        return null;
    }

    /**
     * 网关
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/gateway")
    public void gateway(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //支付宝响应消息
        String responseMsg = "";

        //1. 解析请求参数
        Map<String, String> params = RequestUtil.getRequestParams(request);

        //打印本次请求日志，开发者自行决定是否需要
        logger.info("支付宝请求串>" + params.toString());

        try {
            //2. 验证签名
            this.verifySign(params);

            //3. 获取业务执行器   根据请求中的 service, msgType, eventType, actionParam 确定执行器
            ActionExecutor executor = Dispatcher.getExecutor(params);

            //4. 执行业务逻辑
            responseMsg = executor.execute();

        } catch (AlipayApiException alipayApiException) {
            //开发者可以根据异常自行进行处理
            alipayApiException.printStackTrace();

        } catch (Exception exception) {
            //开发者可以根据异常自行进行处理
            exception.printStackTrace();

        } finally {
            //5. 响应结果加签及返回
            try {
                //对响应内容加签
                responseMsg = encryptAndSign(responseMsg,
                        AlipayServiceEnvConstants.ALIPAY_PUBLIC_KEY,
                        AlipayServiceEnvConstants.PRIVATE_KEY, AlipayServiceEnvConstants.CHARSET,
                        false, true, AlipayServiceEnvConstants.SIGN_TYPE);

                //http 内容应答
                response.reset();
                response.setContentType("text/xml;charset=GBK");
                PrintWriter printWriter = response.getWriter();
                printWriter.print(responseMsg);
                response.flushBuffer();

                //开发者自行决定是否要记录，视自己需求
                logger.info("开发者响应串>" + responseMsg);

            } catch (AlipayApiException alipayApiException) {
                //开发者可以根据异常自行进行处理
                alipayApiException.printStackTrace();
            }
        }
    }

    /**
     * 验签
     *
     * @param params‘
     * @return
     */
    private void verifySign(Map<String, String> params) throws AlipayApiException {
        if (!AlipaySignature.rsaCheckV2(params, AlipayServiceEnvConstants.ALIPAY_PUBLIC_KEY,
                AlipayServiceEnvConstants.SIGN_CHARSET, AlipayServiceEnvConstants.SIGN_TYPE)) {
            throw new AlipayApiException("verify sign fail.");
        }
    }


    public static String encryptAndSign(String bizContent, String alipayPublicKey, String cusPrivateKey, String charset,
                                        boolean isEncrypt, boolean isSign, String signType) throws AlipayApiException {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isEmpty(charset)) {
            charset = AlipayConstants.CHARSET_GBK;
        }
        sb.append("<?xml version=\"1.0\" encoding=\"" + charset + "\"?>");
        if (isEncrypt) {// 加密
            sb.append("<alipay>");
            String encrypted = AlipaySignature.rsaEncrypt(bizContent, alipayPublicKey, charset);
            sb.append("<response>" + encrypted + "</response>");
            sb.append("<encryption_type>AES</encryption_type>");
            if (isSign) {
                String sign = AlipaySignature.rsaSign(encrypted, cusPrivateKey, charset, signType);
                sb.append("<sign>" + sign + "</sign>");
                sb.append("<sign_type>");
                sb.append(signType);
                sb.append("</sign_type>");
            }
            sb.append("</alipay>");
        } else if (isSign) {// 不加密，但需要签名
            sb.append("<alipay>");
            sb.append("<response>" + bizContent + "</response>");
            String sign = AlipaySignature.rsaSign(bizContent, cusPrivateKey, charset, signType);
            sb.append("<sign>" + sign + "</sign>");
            sb.append("<sign_type>");
            sb.append(signType);
            sb.append("</sign_type>");
            sb.append("</alipay>");
        } else {// 不加密，不加签
            sb.append(bizContent);
        }
        return sb.toString();
    }
}
