package com.syhdoctor.common.pay;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
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

public class WechatWebPayServiceImpl implements IPayService {

    Logger log = LoggerFactory.getLogger(this.getClass());


    private WxPayService wxPayService;

    public WechatWebPayServiceImpl(WxPayService wxPayService) {
        this.wxPayService = wxPayService;
    }

    @Override
    public IPayService.PayBean pay(String orderNo, BigDecimal totalFee, String user, String des, String ip, String notify_url, String return_url) {
        log.info("WechatWebPayServiceImpl->pay orderno:" + orderNo + "totalFee:" + totalFee + "user:" + user + "des:" + des + "ip:" + ip + "notify_url:" + notify_url + "return_url:" + return_url);
        IPayService.PayBean payBean = new IPayService.PayBean();
        WxPayUnifiedOrderRequest wxPayUnifiedOrderRequest = new WxPayUnifiedOrderRequest();
        try {
            if (StrUtil.isEmpty(des, orderNo, ip, user) || totalFee.compareTo(BigDecimal.ZERO) == 0) {
                payBean.setReturnMsg("参数错误");
                payBean.setState(false);
            } else {
                wxPayUnifiedOrderRequest.setBody(des);
                wxPayUnifiedOrderRequest.setOutTradeNo(orderNo);
                wxPayUnifiedOrderRequest.setDeviceInfo("WEB");
                wxPayUnifiedOrderRequest.setTotalFee(totalFee.multiply(new BigDecimal(100)).intValue());
                wxPayUnifiedOrderRequest.setOpenid(user);
                wxPayUnifiedOrderRequest.setSpbillCreateIp(ip);
                wxPayUnifiedOrderRequest.setNotifyUrl(notify_url);
                wxPayUnifiedOrderRequest.setTradeType("JSAPI");
                WxPayMpOrderResult wxPayMpOrderResult = wxPayService.createOrder(wxPayUnifiedOrderRequest);
                payBean.setNonceStr(wxPayMpOrderResult.getNonceStr());
                payBean.setAppId(wxPayMpOrderResult.getAppId());
                payBean.setPackageValue(wxPayMpOrderResult.getPackageValue());
                payBean.setSignType(wxPayMpOrderResult.getSignType());
                payBean.setPaysign(wxPayMpOrderResult.getPaySign());
                payBean.setTimeStamp(wxPayMpOrderResult.getTimeStamp());
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
        log.info("WechatWebPayServiceImpl ->CallBackBean  params:" + params);
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
        log.info("WechatWebPayServiceImpl->refund orderno:" + orderNo + "totalFee:" + totalFee);
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
