
package com.syhdoctor.webserver.thirdparty.ali;

import com.syhdoctor.common.pay.IPayService;
import com.syhdoctor.common.utils.EnumUtils.PayTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.TransactionTypeStateEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.answer.AnswerService;
import com.syhdoctor.webserver.service.video.UserVideoService;
import com.syhdoctor.webserver.service.vipcard.VipCardService;
import com.syhdoctor.webserver.service.wallet.UserWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/aliCallback")
public class AliCallbackController extends BaseController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private IPayService aliAppPayImpl;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private VipCardService vipCardService;

    @Autowired
    private UserVideoService userVideoService;

    /**
     * 问诊app同步回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/answerAliAppReturnUrl")
    public void answerAliAppReturnUrl(HttpServletRequest request) {
        //获取支付宝GET过来反馈信息
        log.info("ali========================>/answerAliAppReturnUrl");
        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        log.info(result.toString());
        if (result.isVerify()) {//验证成功
            //该页面可做页面美工编辑
            log.info("return success");
        } else {
            log.info("return fail");
        }
    }

    /**
     * 问诊Web同步回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/answerAliWebReturnUrl")
    public String answerAliWebReturnUrl(HttpServletRequest request) {
        //获取支付宝GET过来反馈信息
        log.info("ali========================>/answerAliWebReturnUrl");
        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        String out_trade_no = null;
        try {
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("商户订单号:" + out_trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, Object> problem = answerService.getProblemOrder(out_trade_no);
        log.info(result.toString());
        if (result.isVerify()) {//验证成功
            //该页面可做页面美工编辑
            log.info("return success");
            return "redirect:" + ConfigModel.WEBLINKURL + "web/syhdoctor/#/paysuccess?ordertype=1&orderid=" + ModelUtil.getLong(problem, "id");
        } else {
            log.info("return fail");
            return "redirect:" + ConfigModel.WEBLINKURL + "web/syhdoctor/#/paysuccess?ordertype=1&orderid=" + ModelUtil.getLong(problem, "id");
        }
    }


    /**
     * 急诊同步回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/phoneAliAppReturnUrl")
    public void phoneAliAppReturnUrl(HttpServletRequest request) {
        //获取支付宝GET过来反馈信息
        log.info("ali========================>/phoneAliAppReturnUrl");
        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        log.info(result.toString());
        if (result.isVerify()) {//验证成功
            log.info("return success");
        } else {
            log.info("return fail");
        }
    }

    /**
     * 急诊同步回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/videoAliAppReturnUrl")
    public void videoAliAppReturnUrl(HttpServletRequest request) {
        //获取支付宝GET过来反馈信息
        log.info("ali========================>/videoAliAppReturnUrl");
        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        log.info(result.toString());
        if (result.isVerify()) {//验证成功
            log.info("return success");
        } else {
            log.info("return fail");
        }
    }

    /**
     * 急诊同步回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/phoneAliWebReturnUrl")
    public String phoneAliWebReturnUrl(HttpServletRequest request) {
        //获取支付宝GET过来反馈信息
        log.info("ali========================>/phoneAliWebReturnUrl");
        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        String out_trade_no = null;
        try {
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("商户订单号:" + out_trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        log.info(result.toString());
        Map<String, Object> phoneOrder = answerService.getPhoneOrder(out_trade_no);
        long userid = ModelUtil.getLong(phoneOrder, "userid");
        if (result.isVerify()) {//验证成功
            //该页面可做页面美工编辑
            log.info("return success");
            log.info("redirect>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + ConfigModel.WEBLINKURL + "web/syhdoctor/#/paysuccess?ordertype=2&orderid=" + ModelUtil.getLong(phoneOrder, "id"));
            return "redirect:" + ConfigModel.WEBLINKURL + "web/syhdoctor/#/paysuccess?ordertype=2&orderid=" + ModelUtil.getLong(phoneOrder, "id");
        } else {
            log.info("return fail");
            log.info("redirect>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + ConfigModel.WEBLINKURL + "web/syhdoctor/#/paysuccess?ordertype=2&orderid=" + ModelUtil.getLong(phoneOrder, "id"));
            return "redirect:" + ConfigModel.WEBLINKURL + "web/syhdoctor/#/paysuccess?ordertype=2&orderid=" + ModelUtil.getLong(phoneOrder, "id");
        }
    }

    /**
     * 问诊Web异步回调
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/answerAliWebNotifyUrl")
    public void answerAliWebNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("ali========================>/answerAliWebNotifyUrl");
        //交易状态
        String trade_status = null;
        String total_fee = "0";
        String out_trade_no = null;
        String trade_no = null;
        String out_biz_no = null;
        try {
            trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易状态:" + trade_status);
            if (!StrUtil.isEmpty(request.getParameter("out_biz_no"))) {
                out_biz_no = new String(request.getParameter("out_biz_no").getBytes("ISO-8859-1"), "UTF-8");
            }
            log.info("退款编号:" + out_biz_no);
            total_fee = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易金额:" + total_fee);
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("商户订单号:" + out_trade_no);
            trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("支付宝交易号:" + trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        log.info(result.toString());
        if (result.isVerify()) {//验证成功
            //请在这里加上商户的业务逻辑程序代码
            Map<String, Object> problem = answerService.getProblemOrder(out_trade_no);
            if (StrUtil.isEmpty(out_biz_no) && ModelUtil.getInt(problem, "states") == 1 && "TRADE_SUCCESS".equals(trade_status)) {
                //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                String orderNo = ModelUtil.getStr(problem, "orderno");
                BigDecimal actualmoney = ModelUtil.getDec(problem, "actualmoney", BigDecimal.ZERO);
                long userid = ModelUtil.getLong(problem, "userid");
                long doctorid = ModelUtil.getLong(problem, "doctorid");
                log.info("actualmoney:" + actualmoney);
                log.info("orderNo:" + orderNo);
                if (actualmoney.compareTo(new BigDecimal(total_fee).multiply(new BigDecimal(100))) != 0 &&
                        orderNo.equals(out_trade_no)) {
                    //更改订单状态
                    answerService.updateAnswerStatusSuccess(out_trade_no, trade_no, PayTypeEnum.AliWeb.getCode(), userid, doctorid);
                    answerService.addAnswerOrderPushData(orderNo);
                } else {
                    answerService.updateAnswerStatusFail(out_trade_no, trade_no, "请求时的total_fee、seller_id与通知时获取的total_fee、seller_id不一致", PayTypeEnum.AliWeb.getCode());
                }
            }

            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
            try {
                response.getWriter().println("success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {//验证失败
            try {
                response.getWriter().println("fail");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 问诊App异步回调
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/answerAliAppNotifyUrl")
    public void answerAliAppNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("ali========================>/answerAliAppNotifyUrl");
        //交易状态
        String trade_status = null;
        String total_fee = "0";
        String out_trade_no = null;
        String trade_no = null;
        String out_biz_no = null;
        try {
            trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易状态:" + trade_status);
            if (!StrUtil.isEmpty(request.getParameter("out_biz_no"))) {
                out_biz_no = new String(request.getParameter("out_biz_no").getBytes("ISO-8859-1"), "UTF-8");
            }
            log.info("退款编号:" + out_biz_no);
            total_fee = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易金额:" + total_fee);
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("商户订单号:" + out_trade_no);
            trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("支付宝交易号:" + trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        log.info(result.toString());
        if (result.isVerify()) {//验证成功
            Map<String, Object> problem = answerService.getProblemOrder(out_trade_no);
            if (StrUtil.isEmpty(out_biz_no) && ModelUtil.getInt(problem, "states") == 1 && "TRADE_SUCCESS".equals(trade_status)) {
                String orderNo = ModelUtil.getStr(problem, "orderno");
                BigDecimal actualmoney = ModelUtil.getDec(problem, "actualmoney", BigDecimal.ZERO);
                long userid = ModelUtil.getLong(problem, "userid");
                long doctorid = ModelUtil.getLong(problem, "doctorid");
                log.info("actualmoney:" + actualmoney);
                log.info("orderNo:" + orderNo);
                if (actualmoney.compareTo(new BigDecimal(total_fee).multiply(new BigDecimal(100))) != 0 &&
                        orderNo.equals(out_trade_no)) {
                    //更改订单状态
                    answerService.updateAnswerStatusSuccess(out_trade_no, trade_no, PayTypeEnum.AliApp.getCode(), userid, doctorid);
                    answerService.addAnswerOrderPushData(orderNo);

                } else {
                    answerService.updateAnswerStatusFail(out_trade_no, trade_no, "请求时的total_fee、seller_id与通知时获取的total_fee、seller_id不一致", PayTypeEnum.AliApp.getCode());
                }
            }

            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
            try {
                response.getWriter().println("success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {//验证失败
            try {
                response.getWriter().println("fail");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 急诊异步回调
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/phoneAliAppNotifyUrl")
    public void phoneAliAppNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("ali========================>/phoneAliAppNotifyUrl");
        //交易状态
        String trade_status = null;
        String total_fee = "0";
        String out_trade_no = null;
        String trade_no = null;
        String out_biz_no = null;
        try {
            trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易状态:" + trade_status);
            if (!StrUtil.isEmpty(request.getParameter("out_biz_no"))) {
                out_biz_no = new String(request.getParameter("out_biz_no").getBytes("ISO-8859-1"), "UTF-8");
            }
            log.info("退款编号:" + out_biz_no);
            total_fee = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易金额:" + total_fee);
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("商户订单号:" + out_trade_no);
            trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("支付宝交易号:" + trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        log.info(result.toString());

        if (result.isVerify()) {//验证成功
            Map<String, Object> problem = answerService.getPhoneOrder(out_trade_no);

            if (StrUtil.isEmpty(out_biz_no) && ModelUtil.getInt(problem, "status") == 1 && "TRADE_SUCCESS".equals(trade_status)) {
                //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
                String orderNo = ModelUtil.getStr(problem, "orderno");
                BigDecimal actualmoney = ModelUtil.getDec(problem, "actualmoney", BigDecimal.ZERO);
                long userid = ModelUtil.getLong(problem, "userid");
                long doctorid = ModelUtil.getLong(problem, "doctorid");
                log.info("actualmoney:" + actualmoney);
                log.info("orderNo:" + orderNo);
                if (actualmoney.compareTo(new BigDecimal(total_fee).multiply(new BigDecimal(100))) == 0 &&
                        orderNo.equals(out_trade_no)) {
                    //更改订单状态
                    log.info("更改订单状态");
                    answerService.updatePhoneStatusSuccess(out_trade_no, trade_no, PayTypeEnum.AliApp.getCode(), userid, doctorid);
                    answerService.addPhoneOrderPushData(orderNo);

                } else {
                    log.info("支付失败");
                    answerService.updatePhoneStatusFail(out_trade_no, trade_no, "请求时的total_fee、seller_id与通知时获取的total_fee、seller_id不一致", PayTypeEnum.AliApp.getCode());
                }
            }
            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
            try {
                response.getWriter().println("success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {//验证失败
            try {
                response.getWriter().println("fail");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 急诊异步回调
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/phoneAliWebNotifyUrl")
    public void phoneAliWebNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("ali========================>/phoneAliWebNotifyUrl");
        //交易状态
        String trade_status = null;
        String total_fee = "0";
        String out_trade_no = null;
        String trade_no = null;
        String out_biz_no = null;
        try {
            trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易状态:" + trade_status);
            if (!StrUtil.isEmpty(request.getParameter("out_biz_no"))) {
                out_biz_no = new String(request.getParameter("out_biz_no").getBytes("ISO-8859-1"), "UTF-8");
            }
            log.info("退款编号:" + out_biz_no);
            total_fee = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易金额:" + total_fee);
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("商户订单号:" + out_trade_no);
            trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("支付宝交易号:" + trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        log.info(result.toString());

        if (result.isVerify()) {//验证成功
            Map<String, Object> problem = answerService.getPhoneOrder(out_trade_no);

            if (StrUtil.isEmpty(out_biz_no) && ModelUtil.getInt(problem, "status") == 1 && "TRADE_SUCCESS".equals(trade_status)) {
                String orderNo = ModelUtil.getStr(problem, "orderno");
                BigDecimal actualmoney = ModelUtil.getDec(problem, "actualmoney", BigDecimal.ZERO);
                long userid = ModelUtil.getLong(problem, "userid");
                long doctorid = ModelUtil.getLong(problem, "doctorid");
                log.info("actualmoney:" + actualmoney);
                log.info("orderNo:" + orderNo);
                if (actualmoney.compareTo(new BigDecimal(total_fee).multiply(new BigDecimal(100))) == 0 &&
                        orderNo.equals(out_trade_no)) {
                    //更改订单状态
                    log.info("更改订单状态");
                    answerService.updatePhoneStatusSuccess(out_trade_no, trade_no, PayTypeEnum.AliWeb.getCode(), userid, doctorid);
                    answerService.addPhoneOrderPushData(orderNo);
                } else {
                    log.info("支付失败");
                    answerService.updatePhoneStatusFail(out_trade_no, trade_no, "请求时的total_fee、seller_id与通知时获取的total_fee、seller_id不一致", PayTypeEnum.AliWeb.getCode());
                }
            }
            try {
                response.getWriter().println("success");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {//验证失败
            try {
                response.getWriter().println("fail");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 问诊App异步回调
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/videoAliAppNotifyUrl")
    public void videoAliAppNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("ali========================>/videoAliAppNotifyUrl");
        //交易状态
        String trade_status = null;
        String total_fee = "0";
        String out_trade_no = null;
        String trade_no = null;
        String out_biz_no = null;
        try {
            trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易状态:" + trade_status);
            if (!StrUtil.isEmpty(request.getParameter("out_biz_no"))) {
                out_biz_no = new String(request.getParameter("out_biz_no").getBytes("ISO-8859-1"), "UTF-8");
            }
            log.info("退款编号:" + out_biz_no);
            total_fee = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易金额:" + total_fee);
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("商户订单号:" + out_trade_no);
            trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("支付宝交易号:" + trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        log.info(result.toString());
        if (result.isVerify()) {//验证成功
            Map<String, Object> order = userVideoService.getVideoOrderByOrderNo(out_trade_no);
            if (StrUtil.isEmpty(out_biz_no) && ModelUtil.getInt(order, "status") == 1 && "TRADE_SUCCESS".equals(trade_status)) {
                String orderNo = ModelUtil.getStr(order, "orderno");
                BigDecimal actualmoney = ModelUtil.getDec(order, "actualmoney", BigDecimal.ZERO);
                long userid = ModelUtil.getLong(order, "userid");
                long doctorid = ModelUtil.getLong(order, "doctorid");
                long schedulingid = ModelUtil.getLong(order, ",schedulingid");
                log.info("actualmoney:" + actualmoney);
                log.info("orderNo:" + orderNo);
                if (actualmoney.compareTo(new BigDecimal(total_fee).multiply(new BigDecimal(100))) != 0 &&
                        orderNo.equals(out_trade_no)) {
                    //更改订单状态
                    userVideoService.updateVideoStatusSuccess(out_trade_no, trade_no, PayTypeEnum.AliApp.getCode(), userid, doctorid, schedulingid);
                    userVideoService.addVideoOrderPushData(orderNo);

                } else {
                    answerService.updateAnswerStatusFail(out_trade_no, trade_no, "请求时的total_fee、seller_id与通知时获取的total_fee、seller_id不一致", PayTypeEnum.AliApp.getCode());
                }
            }

            //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
            try {
                response.getWriter().println("success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {//验证失败
            try {
                response.getWriter().println("fail");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 充值app同步回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/rechargeableAliAppReturnUrl")
    public void rechargeableAliAppReturnUrl(HttpServletRequest request) {
        //获取支付宝GET过来反馈信息
        log.info("ali========================>/rechargeableAliAppReturnUrl");
        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        log.info(result.toString());
        if (result.isVerify()) {//验证成功
            //该页面可做页面美工编辑
            log.info("return success");
        } else {
            log.info("return fail");
        }
    }

    /**
     * 充值web同步回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/rechargeableAliWebReturnUrl")
    public String rechargeableAliWebReturnUrl(HttpServletRequest request) {
        //获取支付宝GET过来反馈信息
        log.info("ali========================>/rechargeableAliWebReturnUrl");
        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        String out_trade_no = null;
        try {
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("商户订单号:" + out_trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, Object> order = userWalletService.getRechargeableOrder(out_trade_no);
        log.info(result.toString());
        if (result.isVerify()) {//验证成功
            //该页面可做页面美工编辑
            log.info("return success");
            return "redirect:" + ConfigModel.WEBLINKURL + "web/syhdoctor/#/rechargeway/rechargesuccess?orderid=" + ModelUtil.getLong(order, "orderid");

        } else {
            log.info("return fail");
            return "redirect:" + ConfigModel.WEBLINKURL + "web/syhdoctor/#/rechargeway/rechargesuccess?orderid=" + ModelUtil.getLong(order, "orderid");
        }
    }

    /**
     * vip充值web同步回调
     *
     * @param request
     * @return
     */
    @RequestMapping("/vipWebAliWebReturnUrl")
    public String vipWebAliWebReturnUrl(HttpServletRequest request) {
        //获取支付宝GET过来反馈信息
        log.info("ali========================>/vipWebAliWebReturnUrl");
        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        String out_trade_no = null;
        try {
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("商户订单号:" + out_trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, Object> order = vipCardService.findByOrder(out_trade_no);
        log.info(result.toString());
        if (result.isVerify()) {//验证成功
            //该页面可做页面美工编辑
            log.info("return success");
            return "redirect:" + ConfigModel.WEBLINKURL + "web/syhdoctor/#/member?uid=" + ModelUtil.getLong(order, "userid");

        } else {
            log.info("return fail");
            return "redirect:" + ConfigModel.WEBLINKURL + "web/syhdoctor/#/member?uid=" + ModelUtil.getLong(order, "userid");
        }
    }

    /**
     * 问诊Web异步回调
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/rechargeableAliAppNotifyUrl")
    public void rechargeableAliAppNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("ali========================>/rechargeableAliAppNotifyUrl");
        //交易状态
        String trade_status = null;
        String total_fee = "0";
        String out_trade_no = null;
        String trade_no = null;
        String out_biz_no = null;
        try {
            trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易状态:" + trade_status);
            if (!StrUtil.isEmpty(request.getParameter("out_biz_no"))) {
                out_biz_no = new String(request.getParameter("out_biz_no").getBytes("ISO-8859-1"), "UTF-8");
            }
            log.info("退款编号:" + out_biz_no);
            total_fee = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易金额:" + total_fee);
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("商户订单号:" + out_trade_no);
            trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("支付宝交易号:" + trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        log.info(result.toString());
        if (result.isVerify()) {//验证成功
            Map<String, Object> order = userWalletService.getRechargeableOrder(out_trade_no);
            if (StrUtil.isEmpty(out_biz_no) && ModelUtil.getInt(order, "status") == 1 && "TRADE_SUCCESS".equals(trade_status)) {
                String orderNo = ModelUtil.getStr(order, "orderno");
                long userid = ModelUtil.getLong(order, "userid");
                BigDecimal amountmoney = ModelUtil.getDec(order, "amountmoney", BigDecimal.ZERO);
                log.info("actualmoney:" + amountmoney);
                log.info("orderNo:" + orderNo);
                if (amountmoney.compareTo(new BigDecimal(total_fee)) == 0 &&
                        orderNo.equals(out_trade_no)) {
                    //更改订单状态
                    userWalletService.updateOrderStatusSuccess(PayTypeEnum.AliApp, out_trade_no);
                    userWalletService.addUserWallet(orderNo, TransactionTypeStateEnum.Ali, userid, amountmoney);
                }
            }
            try {
                response.getWriter().println("success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {//验证失败
            try {
                response.getWriter().println("fail");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 问诊App异步回调
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/rechargeableAliWebNotifyUrl")
    public void rechargeableAliWebNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("ali========================>/rechargeableAliWebNotifyUrl");
        //交易状态
        String trade_status = null;
        String total_fee = "0";
        String out_trade_no = null;
        String trade_no = null;
        String out_biz_no = null;
        try {
            trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易状态:" + trade_status);
            if (!StrUtil.isEmpty(request.getParameter("out_biz_no"))) {
                out_biz_no = new String(request.getParameter("out_biz_no").getBytes("ISO-8859-1"), "UTF-8");
            }
            log.info("退款编号:" + out_biz_no);
            total_fee = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易金额:" + total_fee);
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("商户订单号:" + out_trade_no);
            trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("支付宝交易号:" + trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        log.info(result.toString());
        if (result.isVerify()) {//验证成功
            Map<String, Object> order = userWalletService.getRechargeableOrder(out_trade_no);
            if (StrUtil.isEmpty(out_biz_no) && ModelUtil.getInt(order, "status") == 1 && "TRADE_SUCCESS".equals(trade_status)) {
                String orderNo = ModelUtil.getStr(order, "orderno");
                long userid = ModelUtil.getLong(order, "userid");
                BigDecimal amountmoney = ModelUtil.getDec(order, "amountmoney", BigDecimal.ZERO);
                log.info("amountmoney:" + amountmoney);
                log.info("orderNo:" + orderNo);
                if (amountmoney.compareTo(new BigDecimal(total_fee)) == 0 &&
                        orderNo.equals(out_trade_no)) {
                    //更改订单状态
                    userWalletService.updateOrderStatusSuccess(PayTypeEnum.AliWeb, out_trade_no);

                    userWalletService.addUserWallet(orderNo, TransactionTypeStateEnum.Ali, userid, amountmoney);

                }
            }
            try {
                response.getWriter().println("success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {//验证失败
            try {
                response.getWriter().println("fail");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * vip卡首次充值App异步回调
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/vipCardAliAppNotifyUrl")
    public void vipCardAliAppNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("ali========================>/vipCardAliAppNotifyUrl");
        //交易状态
        String trade_status = null;
        String total_fee = "0";
        String out_trade_no = null;
        String trade_no = null;
        String out_biz_no = null;
        try {
            trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易状态:" + trade_status);
            if (!StrUtil.isEmpty(request.getParameter("out_biz_no"))) {
                out_biz_no = new String(request.getParameter("out_biz_no").getBytes("ISO-8859-1"), "UTF-8");
            }
            log.info("退款编号:" + out_biz_no);
            total_fee = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易金额:" + total_fee);
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("商户订单号:" + out_trade_no);
            trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("支付宝交易号:" + trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        log.info(result.toString());
        if (result.isVerify()) {//验证成功
            Map<String, Object> order = vipCardService.findByOrder(out_trade_no);
            if (StrUtil.isEmpty(out_biz_no) && ModelUtil.getInt(order, "status") == 1 && "TRADE_SUCCESS".equals(trade_status)) {
                String orderNo = ModelUtil.getStr(order, "orderno");
                long userid = ModelUtil.getLong(order, "userid");
                long orderid = ModelUtil.getLong(order, "id");
                BigDecimal actualmoney = ModelUtil.getDec(order, "price", BigDecimal.ZERO);
                log.info("actualmoney:" + actualmoney);
                log.info("orderNo:" + orderNo);
                if (actualmoney.compareTo(new BigDecimal(total_fee).multiply(new BigDecimal(100))) != 0 &&
                        orderNo.equals(out_trade_no)) {
                    //更改订单状态
                    vipCardService.updateStatus(orderid, userid, PayTypeEnum.AliApp.getCode());
                }
            }
            try {
                response.getWriter().println("success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {//验证失败
            try {
                response.getWriter().println("fail");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * vip卡续费充值App异步回调
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/vipReneCardAliAppNotifyUrl")
    public void vipReneCardAliAppNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("ali========================>/vipReneCardAliAppNotifyUrl");
        //交易状态
        String trade_status = null;
        String total_fee = "0";
        String out_trade_no = null;
        String trade_no = null;
        String out_biz_no = null;
        try {
            trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易状态:" + trade_status);
            if (!StrUtil.isEmpty(request.getParameter("out_biz_no"))) {
                out_biz_no = new String(request.getParameter("out_biz_no").getBytes("ISO-8859-1"), "UTF-8");
            }
            log.info("退款编号:" + out_biz_no);
            total_fee = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易金额:" + total_fee);
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("商户订单号:" + out_trade_no);
            trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("支付宝交易号:" + trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        log.info(result.toString());
        if (result.isVerify()) {//验证成功
            Map<String, Object> order = vipCardService.findByOrder(out_trade_no);
            if (StrUtil.isEmpty(out_biz_no) && ModelUtil.getInt(order, "status") == 1 && "TRADE_SUCCESS".equals(trade_status)) {
                String orderNo = ModelUtil.getStr(order, "orderno");
                long userid = ModelUtil.getLong(order, "userid");
                long orderid = ModelUtil.getLong(order, "id");
                long vip_cardid = ModelUtil.getLong(order, "vip_cardid");
                BigDecimal actualmoney = ModelUtil.getDec(order, "price", BigDecimal.ZERO);
                log.info("actualmoney:" + actualmoney);
                log.info("orderNo:" + orderNo);
                if (actualmoney.compareTo(new BigDecimal(total_fee).multiply(new BigDecimal(100))) != 0 &&
                        orderNo.equals(out_trade_no)) {
                    //更改订单状态long orderid,long userid,int paytype,String orderNo
                    vipCardService.updateStatusRene(orderid, userid, PayTypeEnum.AliApp.getCode(), orderNo, vip_cardid);
                }
            }
            try {
                response.getWriter().println("success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {//验证失败
            try {
                response.getWriter().println("fail");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //vip会员卡支付宝web充值
    @RequestMapping("/vipCardAliWebNotifyUrl")
    public void vipCardAliWebNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
        log.info("ali========================>/vipCardAliWebNotifyUrl");
        //交易状态
        String trade_status = null;
        String total_fee = "0";
        String out_trade_no = null;
        String trade_no = null;
        String out_biz_no = null;
        try {
            trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易状态:" + trade_status);
            if (!StrUtil.isEmpty(request.getParameter("out_biz_no"))) {
                out_biz_no = new String(request.getParameter("out_biz_no").getBytes("ISO-8859-1"), "UTF-8");
            }
            log.info("退款编号:" + out_biz_no);
            total_fee = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            log.info("交易金额:" + total_fee);
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("商户订单号:" + out_trade_no);
            trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("支付宝交易号:" + trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        IPayService.CallBackBean result = aliAppPayImpl.callback(request.getParameterMap());
        log.info(result.toString());
        if (result.isVerify()) {//验证成功
            Map<String, Object> order = vipCardService.findByOrder(out_trade_no);
            if (StrUtil.isEmpty(out_biz_no) && ModelUtil.getInt(order, "status") == 1 && "TRADE_SUCCESS".equals(trade_status)) {
                String orderNo = ModelUtil.getStr(order, "orderno");
                long userid = ModelUtil.getLong(order, "userid");
                long orderid = ModelUtil.getLong(order, "id");
                BigDecimal actualmoney = ModelUtil.getDec(order, "price", BigDecimal.ZERO);
                log.info("actualmoney:" + actualmoney);
                log.info("orderNo:" + orderNo);
                if (actualmoney.compareTo(new BigDecimal(total_fee).multiply(new BigDecimal(100))) != 0 &&
                        orderNo.equals(out_trade_no)) {
                    //更改订单状态
                    vipCardService.updateStatus(orderid, userid, PayTypeEnum.AliApp.getCode());
                }
            }
            try {
                response.getWriter().println("success");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {//验证失败
            try {
                response.getWriter().println("fail");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
