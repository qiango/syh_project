package com.syhdoctor.common.pay;

import java.math.BigDecimal;
import java.util.Map;


public interface IPayService {

    class PayBean {
        private boolean state; //支付状态
        private String appId;
        private String timeStamp;//时间戳
        private String nonceStr;//随机字符串

        private long orderid;//订单id

        /**
         * 由于package为java保留关键字，因此改为packageValue.
         */
        private String packageValue;//微信预支付交易会话标识
        private String signType;//交易类型
        private String paysign;//签名
        private String form; //web支付宝form表单
        private String orderinfo;//web支付宝返回信息
        private String returnMsg;//返回消息

        private String sign;
        private String prepayId;
        private String partnerId;

        public long getOrderid() {
            return orderid;
        }

        public void setOrderid(long orderid) {
            this.orderid = orderid;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getPrepayId() {
            return prepayId;
        }

        public void setPrepayId(String prepayId) {
            this.prepayId = prepayId;
        }

        public String getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(String partnerId) {
            this.partnerId = partnerId;
        }

        public String getOrderinfo() {
            return orderinfo;
        }

        public void setOrderinfo(String orderinfo) {
            this.orderinfo = orderinfo;
        }

        public String getReturnMsg() {
            return returnMsg;
        }

        public void setReturnMsg(String returnMsg) {
            this.returnMsg = returnMsg;
        }

        public String getForm() {
            return form;
        }

        public void setForm(String form) {
            this.form = form;
        }

        public boolean isState() {
            return state;
        }

        public void setState(boolean state) {
            this.state = state;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }

        public String getNonceStr() {
            return nonceStr;
        }

        public void setNonceStr(String nonceStr) {
            this.nonceStr = nonceStr;
        }

        public String getPackageValue() {
            return packageValue;
        }

        public void setPackageValue(String packageValue) {
            this.packageValue = packageValue;
        }

        public String getSignType() {
            return signType;
        }

        public void setSignType(String signType) {
            this.signType = signType;
        }

        public String getPaysign() {
            return paysign;
        }

        public void setPaysign(String paySign) {
            this.paysign = paySign;
        }
    }

    class CallBackBean {
        private boolean verify;//成功或者失败
        private String outTradeNo;//本地订单编号
        private String tradeNo;//第三方编号

        public boolean isVerify() {
            return verify;
        }

        public void setVerify(boolean verify) {
            this.verify = verify;
        }

        public String getOutTradeNo() {
            return outTradeNo;
        }

        public void setOutTradeNo(String outTradeNo) {
            this.outTradeNo = outTradeNo;
        }

        public String getTradeNo() {
            return tradeNo;
        }

        public void setTradeNo(String tradeNo) {
            this.tradeNo = tradeNo;
        }

        @Override
        public String toString() {
            return "CallBackBean{" +
                    "verify=" + verify +
                    ", outTradeNo='" + outTradeNo + '\'' +
                    ", tradeNo='" + tradeNo + '\'' +
                    '}';
        }
    }

    class ReturnBean {
        private boolean verify;//成功或者失败

        public boolean isVerify() {
            return verify;
        }

        public void setVerify(boolean verify) {
            this.verify = verify;
        }

        @Override
        public String toString() {
            return "ReturnBean{" +
                    "verify=" + verify +
                    '}';
        }
    }


    /**
     * 支付接口
     *
     * @param orderNo    订单号
     * @param totalFee
     * @param user
     * @param des
     * @param ip
     * @param notify_url 异步通知接口
     * @param return_url 同步通知接口
     * @return 返回支付的参数
     */
    PayBean pay(String orderNo, BigDecimal totalFee, String user, String des, String ip, String notify_url, String return_url);

    CallBackBean callback(Map<?, ?> params);

    ReturnBean refund(String orderNo, BigDecimal totalFee, String remark);
}
