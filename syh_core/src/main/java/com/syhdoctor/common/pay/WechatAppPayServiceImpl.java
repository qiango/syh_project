package com.syhdoctor.common.pay;

import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;

public class WechatAppPayServiceImpl implements IPayService {


    Logger log = LoggerFactory.getLogger(this.getClass());


    private WxPayService wxPayService;

    public WechatAppPayServiceImpl(WxPayService wxPayService) {
        this.wxPayService = wxPayService;
    }

    @Override
    public IPayService.PayBean pay(String orderNo, BigDecimal totalFee, String user, String des, String ip, String notify_url, String return_url) {
        log.info("WechatAppPayServiceImpl->pay orderno:" + orderNo + "totalFee:" + totalFee + "user:" + user + "des:" + des + "ip:" + ip + "notify_url:" + notify_url + "return_url:" + return_url);
        IPayService.PayBean payBean = new IPayService.PayBean();
        WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = new WxPayUnifiedOrderRequest();
        try {
            if (StrUtil.isEmpty(des, orderNo, ip) || totalFee.compareTo(BigDecimal.ZERO) == 0) {
                payBean.setReturnMsg("参数错误");
                payBean.setState(false);
            } else {
                wxPayUnifiedOrderRequest.setBody(des);
                wxPayUnifiedOrderRequest.setOutTradeNo(orderNo);
                //wxPayUnifiedOrderRequest.setDeviceInfo("WEB");
                wxPayUnifiedOrderRequest.setTotalFee(totalFee.multiply(new BigDecimal(100)).intValue());
                wxPayUnifiedOrderRequest.setSpbillCreateIp(ip);
                wxPayUnifiedOrderRequest.setNotifyUrl(notify_url);
                wxPayUnifiedOrderRequest.setTradeType("APP");
                WxPayAppOrderResult wxPayAppOrderResult = wxPayService.createOrder(wxPayUnifiedOrderRequest);
                log.info("sign>" + wxPayAppOrderResult.getSign());
                log.info("prepayId>" + wxPayAppOrderResult.getPrepayId());
                log.info("partnerId>" + wxPayAppOrderResult.getPartnerId());
                log.info("appId>" + wxPayAppOrderResult.getAppId());
                log.info("packageValue>" + wxPayAppOrderResult.getPackageValue());
                log.info("timeStamp>" + wxPayAppOrderResult.getTimeStamp());
                log.info("nonceStr>" + wxPayAppOrderResult.getNonceStr());
                log.info("getMchKey>" + wxPayService.getConfig().getMchKey());
                payBean.setPaysign(wxPayAppOrderResult.getSign());
                payBean.setPartnerId(wxPayAppOrderResult.getPartnerId());
                payBean.setPrepayId(wxPayAppOrderResult.getPrepayId());
                payBean.setPackageValue(wxPayAppOrderResult.getPackageValue());
                payBean.setTimeStamp(wxPayAppOrderResult.getTimeStamp());
                payBean.setNonceStr(wxPayAppOrderResult.getNonceStr());
                payBean.setAppId(wxPayAppOrderResult.getAppId());
                payBean.setState(true);
            }
        } catch (WxPayException e) {
            e.printStackTrace();
            payBean.setReturnMsg(e.getReturnMsg());
            payBean.setState(false);
        }
        return payBean;
    }

    @Override
    public IPayService.CallBackBean callback(Map<?, ?> params) {
        log.info("WechatAppPayServiceImpl ->callback  params:" + params);
        IPayService.CallBackBean callBackBean = new IPayService.CallBackBean();
        if ("SUCCESS".equals(ModelUtil.getStr(params, "return_code"))) {
            callBackBean.setVerify(true);
            callBackBean.setOutTradeNo(ModelUtil.getStr(params, "out_trade_no"));
            callBackBean.setTradeNo(ModelUtil.getStr(params, "transaction_id"));
        } else {
            callBackBean.setVerify(false);
        }
        return callBackBean;
    }

    @Override
    public ReturnBean refund(String orderNo, BigDecimal totalFee, String remark) {
        log.info("WechatAppPayServiceImpl->refund orderno:" + orderNo + "totalFee:" + totalFee);
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
        ReturnBean returnBean = new ReturnBean();
        try {
            wxPayRefundRequest.setOutTradeNo(orderNo);
            wxPayRefundRequest.setOutRefundNo(orderNo);
            wxPayRefundRequest.setTotalFee(totalFee.multiply(new BigDecimal(100)).intValue());
            wxPayRefundRequest.setRefundFee(totalFee.multiply(new BigDecimal(100)).intValue());
            //wxPayRefundRequest.setNotifyUrl(notify_url);
            wxPayRefundRequest.setRefundDesc(remark);
            WxPayRefundResult refund = wxPayService.refund(wxPayRefundRequest);
            if ("SUCCESS".equals(refund.getResultCode())) {
                returnBean.setVerify(true);
            } else {
                returnBean.setVerify(false);
            }
        } catch (WxPayException e) {
            e.printStackTrace();
            returnBean.setVerify(false);
        }
        return returnBean;
    }
}
