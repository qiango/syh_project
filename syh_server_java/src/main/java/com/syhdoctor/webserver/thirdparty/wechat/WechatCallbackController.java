package com.syhdoctor.webserver.thirdparty.wechat;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.syhdoctor.common.pay.IPayService;
import com.syhdoctor.common.utils.EnumUtils.PayTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.TransactionTypeStateEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.answer.AnswerService;
import com.syhdoctor.webserver.service.system.SystemService;
import com.syhdoctor.webserver.service.video.UserVideoService;
import com.syhdoctor.webserver.service.vipcard.VipCardService;
import com.syhdoctor.webserver.service.wallet.UserWalletService;
import com.syhdoctor.webserver.thirdparty.wechat.util.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wechatPay")
public class WechatCallbackController extends BaseController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private IPayService wechatAppPayImpl;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private VipCardService vipCardService;

    @Autowired
    private UserVideoService userVideoService;


    //问诊app异步回调
    @RequestMapping("/videoWechatAppNotifyUrl")
    public void videoWechatAppNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("wechatPay===============>videoWechatAppNotifyUrl");
        try {
            InputStream inStream = request.getInputStream();
            int _buffer_size = 1024;
            if (inStream != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] tempBytes = new byte[_buffer_size];
                int count = -1;
                while ((count = inStream.read(tempBytes, 0, _buffer_size)) != -1) {
                    outStream.write(tempBytes, 0, count);
                }
                outStream.flush();
                //将流转换成字符串
                String result = new String(outStream.toByteArray(), "UTF-8");
                log.info("result:" + result);
                Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
                log.info("resultmap:" + resultMap);
                IPayService.CallBackBean callback = wechatAppPayImpl.callback(resultMap);
                String resultXml = getVideoAppResult(callback);
                log.info("resultXml:" + resultXml);
                response.getWriter().println(resultXml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //问诊app异步回调
    @RequestMapping("/answerWechatAppNotifyUrl")
    public void wechatAnswerAppNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("wechatPay===============>answerWechatAppNotifyUrl");
        try {
            InputStream inStream = request.getInputStream();
            int _buffer_size = 1024;
            if (inStream != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] tempBytes = new byte[_buffer_size];
                int count = -1;
                while ((count = inStream.read(tempBytes, 0, _buffer_size)) != -1) {
                    outStream.write(tempBytes, 0, count);
                }
                outStream.flush();
                //将流转换成字符串
                String result = new String(outStream.toByteArray(), "UTF-8");
                log.info("result:" + result);
                Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
                log.info("resultmap:" + resultMap);
                IPayService.CallBackBean callback = wechatAppPayImpl.callback(resultMap);
                String resultXml = getAnswerAppResult(callback);
                log.info("resultXml:" + resultXml);
                response.getWriter().println(resultXml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //问诊web异步回调
    @RequestMapping("/answerWechatWebNotifyUrl")
    public void answerWechatWebNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("wechatPay===============>answerWechatWebNotifyUrl");
        try {
            InputStream inStream = request.getInputStream();
            int _buffer_size = 1024;
            if (inStream != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] tempBytes = new byte[_buffer_size];
                int count = -1;
                while ((count = inStream.read(tempBytes, 0, _buffer_size)) != -1) {
                    outStream.write(tempBytes, 0, count);
                }
                outStream.flush();
                //将流转换成字符串
                String result = new String(outStream.toByteArray(), "UTF-8");
                log.info("result:" + result);
                Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
                log.info("resultmap:" + resultMap);
                IPayService.CallBackBean callback = wechatAppPayImpl.callback(resultMap);
                String resultXml = getAnswerWebResult(callback);
                log.info("resultXml:" + resultXml);
                response.getWriter().println(resultXml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //急诊app异步回调
    @RequestMapping("/phoneWechatAppNotifyUrl")
    public void phoneWechatAppNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("wechatPay===============>phoneWechatAppNotifyUrl");
        try {
            InputStream inStream = request.getInputStream();
            int _buffer_size = 1024;
            if (inStream != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] tempBytes = new byte[_buffer_size];
                int count = -1;
                while ((count = inStream.read(tempBytes, 0, _buffer_size)) != -1) {
                    outStream.write(tempBytes, 0, count);
                }
                outStream.flush();
                //将流转换成字符串
                String result = new String(outStream.toByteArray(), "UTF-8");
                log.info("result:" + result);
                Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
                log.info("resultmap:" + resultMap);

                IPayService.CallBackBean callback = wechatAppPayImpl.callback(resultMap);
                String resultXml = getPhoneAppResult(callback);
                log.info("resultXml:" + resultXml);
                response.getWriter().println(resultXml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //急诊app异步回调
    @RequestMapping("/phoneWechatWebNotifyUrl")
    public void phoneWechatWebNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("wechatPay===============>phoneWechatWebNotifyUrl");
        try {
            InputStream inStream = request.getInputStream();
            int _buffer_size = 1024;
            if (inStream != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] tempBytes = new byte[_buffer_size];
                int count = -1;
                while ((count = inStream.read(tempBytes, 0, _buffer_size)) != -1) {
                    outStream.write(tempBytes, 0, count);
                }
                outStream.flush();
                //将流转换成字符串
                String result = new String(outStream.toByteArray(), "UTF-8");
                log.info("result:" + result);
                Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
                log.info("resultmap:" + resultMap);

                IPayService.CallBackBean callback = wechatAppPayImpl.callback(resultMap);
                String resultXml = getPhoneWebResult(callback);
                log.info("resultXml:" + resultXml);
                response.getWriter().println(resultXml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAnswerWebResult(IPayService.CallBackBean callback) {
        String result = "";
        if (callback.isVerify()) {
            try {
                result = returnSuccessXml("OK");
                log.info("returnCode:" + callback.toString());
                Map<String, Object> problem = answerService.getProblemOrder(callback.getOutTradeNo());
                long userid = ModelUtil.getLong(problem, "userid");
                long doctorid = ModelUtil.getLong(problem, "doctorid");

                answerService.updateAnswerStatusSuccess(callback.getOutTradeNo(), callback.getTradeNo(), PayTypeEnum.WxWeb.getCode(), userid, doctorid);
                answerService.addAnswerOrderPushData(callback.getOutTradeNo());

            } catch (Exception e) {
                try {
                    result = returnFailXml("FAIL");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        } else {
            try {
                result = returnFailXml("FAIL");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("result:" + result);
        return result;
    }

    private String getVideoAppResult(IPayService.CallBackBean callback) {
        String result = "";
        if (callback.isVerify()) {
            try {
                result = returnSuccessXml("OK");
                log.info("returnCode:" + callback.toString());
                Map<String, Object> order = userVideoService.getVideoOrderByOrderNo(callback.getOutTradeNo());
                long userid = ModelUtil.getLong(order, "userid");
                long doctorid = ModelUtil.getLong(order, "doctorid");
                long schedulingid = ModelUtil.getLong(order, "schedulingid");
                userVideoService.updateVideoStatusSuccess(callback.getOutTradeNo(), callback.getTradeNo(), PayTypeEnum.WxApp.getCode(), userid, doctorid, schedulingid);
                userVideoService.addVideoOrderPushData(callback.getOutTradeNo());

            } catch (Exception e) {
                try {
                    result = returnFailXml("FAIL");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        } else {
            try {
                result = returnFailXml("FAIL");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("result:" + result);
        return result;
    }

    private String getAnswerAppResult(IPayService.CallBackBean callback) {
        String result = "";
        if (callback.isVerify()) {
            try {
                result = returnSuccessXml("OK");
                log.info("returnCode:" + callback.toString());
                Map<String, Object> problem = answerService.getProblemOrder(callback.getOutTradeNo());
                long userid = ModelUtil.getLong(problem, "userid");
                long doctorid = ModelUtil.getLong(problem, "doctorid");
                answerService.updateAnswerStatusSuccess(callback.getOutTradeNo(), callback.getTradeNo(), PayTypeEnum.WxApp.getCode(), userid, doctorid);
                answerService.addAnswerOrderPushData(callback.getOutTradeNo());

            } catch (Exception e) {
                try {
                    result = returnFailXml("FAIL");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        } else {
            try {
                result = returnFailXml("FAIL");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("result:" + result);
        return result;
    }

    private String getPhoneWebResult(IPayService.CallBackBean callback) {
        String result = "";
        if (callback.isVerify()) {
            result = WxPayNotifyResponse.success("OK");
            log.info("returnCode:" + callback.toString());
            try {
                Map<String, Object> problem = answerService.getPhoneOrder(callback.getOutTradeNo());
                long userid = ModelUtil.getLong(problem, "userid");
                long doctorid = ModelUtil.getLong(problem, "doctorid");
                answerService.updatePhoneStatusSuccess(callback.getOutTradeNo(), callback.getTradeNo(), PayTypeEnum.WxWeb.getCode(), userid, doctorid);
                //todo 微信推送
            } catch (Exception e) {
                result = WxPayNotifyResponse.fail("FAIL");
                e.printStackTrace();
            }
        } else {
            result = WxPayNotifyResponse.fail("FAIL");
        }
        log.info("result:" + result);
        return result;
    }

    private String getPhoneAppResult(IPayService.CallBackBean callback) {
        String result = "";
        if (callback.isVerify()) {
            result = WxPayNotifyResponse.success("OK");
            log.info("returnCode:" + callback.toString());
            try {
                Map<String, Object> problem = answerService.getPhoneOrder(callback.getOutTradeNo());

                long userid = ModelUtil.getLong(problem, "userid");
                long doctorid = ModelUtil.getLong(problem, "doctorid");
                answerService.updatePhoneStatusSuccess(callback.getOutTradeNo(), callback.getTradeNo(), PayTypeEnum.WxApp.getCode(), userid, doctorid);
                answerService.addPhoneOrderPushData(callback.getOutTradeNo());
            } catch (Exception e) {
                result = WxPayNotifyResponse.fail("FAIL");
                e.printStackTrace();
            }
        } else {
            result = WxPayNotifyResponse.fail("FAIL");
        }
        log.info("result:" + result);
        return result;
    }

    private String returnFailXml(String msg) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("return_code", "FAIL");
        map.put("return_msg", msg);
        return WXPayUtil.mapToXml(map);
    }

    private String returnSuccessXml(String msg) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("return_code", "SUCCESS");
        map.put("return_msg", msg);
        return WXPayUtil.mapToXml(map);
    }

    //app充值
    @RequestMapping("/rechargeableWechatAppNotifyUrl")
    public void rechargeableWechatAppNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("wechatPay===============>rechargeableWechatAppNotifyUrl");
        try {
            InputStream inStream = request.getInputStream();
            int _buffer_size = 1024;
            if (inStream != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] tempBytes = new byte[_buffer_size];
                int count = -1;
                while ((count = inStream.read(tempBytes, 0, _buffer_size)) != -1) {
                    outStream.write(tempBytes, 0, count);
                }
                outStream.flush();
                //将流转换成字符串
                String result = new String(outStream.toByteArray(), "UTF-8");
                log.info("result:" + result);
                Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
                log.info("resultmap:" + resultMap);
                IPayService.CallBackBean callback = wechatAppPayImpl.callback(resultMap);
                String resultXml = getRechargeableAppResult(callback);
                log.info("resultXml:" + resultXml);
                response.getWriter().println(resultXml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //web充值
    @RequestMapping("/rechargeableWechatWebNotifyUrl")
    public void rechargeableWechatWebNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("wechatPay===============>rechargeableWechatWebNotifyUrl");
        try {
            InputStream inStream = request.getInputStream();
            int _buffer_size = 1024;
            if (inStream != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] tempBytes = new byte[_buffer_size];
                int count = -1;
                while ((count = inStream.read(tempBytes, 0, _buffer_size)) != -1) {
                    outStream.write(tempBytes, 0, count);
                }
                outStream.flush();
                //将流转换成字符串
                String result = new String(outStream.toByteArray(), "UTF-8");
                log.info("result:" + result);
                Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
                log.info("resultmap:" + resultMap);
                IPayService.CallBackBean callback = wechatAppPayImpl.callback(resultMap);
                String resultXml = getRechargeableAppResult(callback);
                log.info("resultXml:" + resultXml);
                response.getWriter().println(resultXml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //vip卡微信web充值
    @RequestMapping("/vipWechatWebNotifyUrl")
    public void vipWechatWebNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("wechatPay===============>vipWechatWebNotifyUrl");
        try {
            InputStream inStream = request.getInputStream();
            int _buffer_size = 1024;
            if (inStream != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] tempBytes = new byte[_buffer_size];
                int count = -1;
                while ((count = inStream.read(tempBytes, 0, _buffer_size)) != -1) {
                    outStream.write(tempBytes, 0, count);
                }
                outStream.flush();
                //将流转换成字符串
                String result = new String(outStream.toByteArray(), "UTF-8");
                log.info("result:" + result);
                Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
                log.info("resultmap:" + resultMap);
                IPayService.CallBackBean callback = wechatAppPayImpl.callback(resultMap);
                String resultXml = getVipWebResult(callback);
                log.info("resultXml:" + resultXml);
                response.getWriter().println(resultXml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //vip卡微信首冲app充值
    @RequestMapping("/vipWechatAppNotifyUrl")
    public void vipWechatAppNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("wechatPay===============>vipWechatAppNotifyUrl");
        try {
            InputStream inStream = request.getInputStream();
            int _buffer_size = 1024;
            if (inStream != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] tempBytes = new byte[_buffer_size];
                int count = -1;
                while ((count = inStream.read(tempBytes, 0, _buffer_size)) != -1) {
                    outStream.write(tempBytes, 0, count);
                }
                outStream.flush();
                //将流转换成字符串
                String result = new String(outStream.toByteArray(), "UTF-8");
                log.info("result:" + result);
                Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
                log.info("resultmap:" + resultMap);
                IPayService.CallBackBean callback = wechatAppPayImpl.callback(resultMap);
                String resultXml = getvipAppResult(callback, 0);
                log.info("resultXml:" + resultXml);
                response.getWriter().println(resultXml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //vip卡续费微信app充值
    @RequestMapping("/vipRenewWechatAppNotifyUrl")
    public void vipRenewWechatAppNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("wechatPay===============>vipRenewWechatAppNotifyUrl");
        try {
            InputStream inStream = request.getInputStream();
            int _buffer_size = 1024;
            if (inStream != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] tempBytes = new byte[_buffer_size];
                int count = -1;
                while ((count = inStream.read(tempBytes, 0, _buffer_size)) != -1) {
                    outStream.write(tempBytes, 0, count);
                }
                outStream.flush();
                //将流转换成字符串
                String result = new String(outStream.toByteArray(), "UTF-8");
                log.info("result:" + result);
                Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
                log.info("resultmap:" + resultMap);
                IPayService.CallBackBean callback = wechatAppPayImpl.callback(resultMap);
                String resultXml = getvipAppResult(callback, 1);
                log.info("resultXml:" + resultXml);
                response.getWriter().println(resultXml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRechargeableAppResult(IPayService.CallBackBean callback) {
        String result = "";
        if (callback.isVerify()) {
            try {
                result = returnSuccessXml("OK");
                log.info("returnCode:" + callback.toString());
                Map<String, Object> order = userWalletService.getRechargeableOrder(callback.getOutTradeNo());
                long userid = ModelUtil.getLong(order, "userid");
                BigDecimal amountmoney = ModelUtil.getDec(order, "amountmoney", BigDecimal.ZERO);
                userWalletService.updateOrderStatusSuccess(PayTypeEnum.WxApp, callback.getOutTradeNo());
                userWalletService.addUserWallet(callback.getOutTradeNo(), TransactionTypeStateEnum.Wechat, userid, amountmoney);
            } catch (Exception e) {
                try {
                    result = returnFailXml("FAIL");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        } else {
            try {
                result = returnFailXml("FAIL");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("result:" + result);
        return result;
    }


    private String getvipAppResult(IPayService.CallBackBean callback, int type) {
        String result = "";
        if (callback.isVerify()) {
            try {
                result = returnSuccessXml("OK");
                log.info("returnCode:" + callback.toString());
                String orderNo = callback.getOutTradeNo();
                Map<String, Object> order = vipCardService.findByOrder(orderNo);
                long userid = ModelUtil.getLong(order, "userid");
                long orderid = ModelUtil.getLong(order, "id");
                long vip_cardid = ModelUtil.getLong(order, "vip_cardid");
                if (0 == type) {
                    vipCardService.updateStatus(orderid, userid, PayTypeEnum.WxApp.getCode());
                } else {
                    vipCardService.updateStatusRene(orderid, userid, PayTypeEnum.WxApp.getCode(), orderNo, vip_cardid);
                }

            } catch (Exception e) {
                try {
                    result = returnFailXml("FAIL");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        } else {
            try {
                result = returnFailXml("FAIL");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("result:" + result);
        return result;
    }

    private String getVipWebResult(IPayService.CallBackBean callback) {
        String result = "";
        if (callback.isVerify()) {
            try {
                result = returnSuccessXml("OK");
                log.info("returnCode:" + callback.toString());
                Map<String, Object> order = vipCardService.findByOrder(callback.getOutTradeNo());
                long userid = ModelUtil.getLong(order, "userid");
                long orderid = ModelUtil.getLong(order, "id");
                vipCardService.updateStatus(orderid, userid, PayTypeEnum.WxWeb.getCode());
            } catch (Exception e) {
                try {
                    result = returnFailXml("FAIL");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        } else {
            try {
                result = returnFailXml("FAIL");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("result:" + result);
        return result;
    }

}
