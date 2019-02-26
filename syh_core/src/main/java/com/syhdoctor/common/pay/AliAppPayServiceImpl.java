package com.syhdoctor.common.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.syhdoctor.common.config.ConfigModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AliAppPayServiceImpl implements IPayService {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public PayBean pay(String orderNo, BigDecimal totalFee, String user, String des, String ip, String notify_url, String return_url) {
        log.info("AliAppPayServiceImpl->pay orderno:" + orderNo + "totalFee:" + totalFee + "user:" + user + "des:" + des + "ip:" + ip + "notify_url:" + notify_url + "return_url:" + return_url);
        PayBean result = new PayBean();
        String out_trade_no = orderNo;
        String total_amount = String.valueOf(totalFee.doubleValue());
        // 超时时间 可空
        String timeout_express = ConfigModel.AliPay.TIMEOUT_EXPRESS;
        // 销售产品码 必填
        String product_code = ConfigModel.AliPay.PRODUCT_CODE;
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        //调用RSA签名方式
        //实例化客户端
        AlipayClient client = new DefaultAlipayClient(ConfigModel.AliPay.URL, ConfigModel.AliPay.APPID, ConfigModel.AliPay.RSA_PRIVATE_KEY, ConfigModel.AliPay.FORMAT, ConfigModel.AliPay.CHARSET, ConfigModel.AliPay.ALIPAY_PUBLIC_KEY, ConfigModel.AliPay.SIGNTYPE);
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest alipay_request = new AlipayTradeAppPayRequest();

        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setOutTradeNo(out_trade_no);
        model.setSubject(des);
        model.setTotalAmount(total_amount);
        model.setBody(des);
        model.setTimeoutExpress(timeout_express);
        model.setProductCode(product_code);
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(notify_url);

        // form表单生产
        try {
            // 调用SDK生成表单
            result.setState(true);
            String body = client.sdkExecute(alipay_request).getBody();
            log.info(body);
            result.setOrderinfo(body);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            result.setReturnMsg(e.getErrMsg());
            result.setState(false);
        }
        return result;
    }

    @Override
    public CallBackBean callback(Map<?, ?> value) {
        log.info("AliAppPayServiceImpl->CallBackBean value:" + value);
        CallBackBean result = new CallBackBean();

        try {
            Map<String, String> params = new HashMap<String, String>();
            for (Iterator iter = value.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) value.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。
                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }

            //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
            //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
            boolean flag = AlipaySignature.rsaCheckV1(params, ConfigModel.AliPay.ALIPAY_PUBLIC_KEY, ConfigModel.AliPay.CHARSET, "RSA2");
            result.setVerify(flag);

        } catch (AlipayApiException e) {
            e.printStackTrace();
            result.setVerify(false);
        }
        return result;
    }

    public static void main(String[] args) {

        new AliAppPayServiceImpl().refund("201811191811287568192", new BigDecimal(10), "");
    }

    @Override
    public ReturnBean refund(String orderNo, BigDecimal totalFee, String remark) {
        //商户订单号和支付宝交易号不能同时为空。 trade_no、  out_trade_no如果同时存在优先取trade_no
        //商户订单号，和支付宝交易号二选一
        String out_trade_no = orderNo;
        //支付宝交易号，和商户订单号二选一
        //String trade_no = "";
        //退款金额，不能大于订单总金额
        String refund_amount = String.valueOf(totalFee.doubleValue());
        //退款的原因说明
        String refund_reason = remark;
        //标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
        //String out_request_no = "";
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        AlipayClient client = new DefaultAlipayClient(ConfigModel.AliPay.URL, ConfigModel.AliPay.APPID, ConfigModel.AliPay.RSA_PRIVATE_KEY, ConfigModel.AliPay.FORMAT, ConfigModel.AliPay.CHARSET, ConfigModel.AliPay.ALIPAY_PUBLIC_KEY, ConfigModel.AliPay.SIGNTYPE);
        AlipayTradeRefundRequest alipay_request = new AlipayTradeRefundRequest();

        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(out_trade_no);
        //model.setTradeNo(trade_no);
        model.setRefundAmount(refund_amount);
        model.setRefundReason(refund_reason);
        //model.setOutRequestNo(out_request_no);
        alipay_request.setBizModel(model);

        AlipayTradeRefundResponse alipay_response = null;
        ReturnBean returnBean = new ReturnBean();
        try {
            alipay_response = client.execute(alipay_request);
            if ("10000".equals(alipay_response.getCode())) {
                returnBean.setVerify(true);
            } else {
                returnBean.setVerify(false);
            }
        } catch (AlipayApiException e) {
            returnBean.setVerify(false);
            e.printStackTrace();
        }
        return returnBean;
    }
}
