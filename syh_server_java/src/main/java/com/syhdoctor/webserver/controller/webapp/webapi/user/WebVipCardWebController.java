package com.syhdoctor.webserver.controller.webapp.webapi.user;

import com.syhdoctor.common.pay.IPayService;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.user.UserManagementService;
import com.syhdoctor.webserver.service.vipcard.VipCardService;
import com.syhdoctor.webserver.service.wallet.UserWalletService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qian.wang
 * @description：vip充值卡web充值
 * @date 2018/11/6
 */
@Api(description = "/Web/UserVip web用戶会员卡充值")
@RestController
@RequestMapping("/Web/UserVip")
public class WebVipCardWebController extends BaseController {

    @Autowired
    private VipCardService vipCardService;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private UserManagementService userManagementService;

    @ApiOperation(value = "vip卡微信web支付")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单号id", dataType = "String"),
    })
    @PostMapping("/addWechatAppOrder")
    public Map<String, Object> addWechatAppOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long orderid = ModelUtil.getLong(params, "orderid");
        Map<String, Object> map = vipCardService.findOrderByorderId(orderid);
        BigDecimal amountmoney;
        int order_type = ModelUtil.getInt(map, "order_type");//订单类型，判断是首冲还是续费
        String orderno = ModelUtil.getStr(map, "orderno");
        if (0 == order_type) {
            amountmoney = ModelUtil.getDec(map, "price", BigDecimal.ZERO);//首冲价格
        } else {
            amountmoney = ModelUtil.getDec(map, "renewal_fee", BigDecimal.ZERO);//续费价格
        }
        String ip = request.getRemoteAddr();
//        String ip="127.0.0.1";
        if (userId == 0 || amountmoney.compareTo(BigDecimal.ZERO) == 0) {
            setErrorResult(result, "参数错误");
        } else {
            IPayService.PayBean payBean = vipCardService.weChatWebPay(orderno, amountmoney, ip, userId);
            payBean.setOrderid(orderid);
            result.put("data", getResult(payBean));
            setOkResult(result, "充值成功");
        }
        return result;
    }

    @ApiOperation(value = "vip卡支付宝web充值")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单号id", dataType = "String"),
    })
    @PostMapping("/addAliAppOrder")
    public Map<String, Object> addAliAppOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long orderid = ModelUtil.getLong(params, "orderid");
        Map<String, Object> map = vipCardService.findOrderByorderId(orderid);
        BigDecimal amountmoney;
        int order_type = ModelUtil.getInt(map, "order_type");//订单类型，判断是首冲还是续费
        String orderno = ModelUtil.getStr(map, "orderno");
        if (0 == order_type) {
            amountmoney = ModelUtil.getDec(map, "price", BigDecimal.ZERO);//首冲价格
        } else {
            amountmoney = ModelUtil.getDec(map, "renewal_fee", BigDecimal.ZERO);//续费价格
        }
        if (userId == 0 || amountmoney.compareTo(BigDecimal.ZERO) == 0) {
            setErrorResult(result, "参数错误");
        } else {
            IPayService.PayBean payBean = vipCardService.aliWebPay(orderno, amountmoney, userId);
            payBean.setOrderid(orderid);
            result.put("data", payBean);
            setOkResult(result, "充值成功");
        }
        return result;
    }

    /**
     * vip充值订单微信支付状态查询
     */
    @ApiOperation(value = "vip充值订单微信支付状态查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/vipWeChatPayStatus")
    public Map<String, Object> answerWeChatPayStatus(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            Map<String, Integer> data = new HashMap<>();
            data.put("paystatus", vipCardService.answerWeChatPayStatus(orderId));
            result.put("data", data);
            setOkResult(result, "支付成功");
        }
        return result;
    }

    @ApiOperation(value = "会员卡详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "用户id", value = "userid", dataType = "String"),
    })
    @PostMapping("/getVipCard")
    public Map<String, Object> getVipCardDetails(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "userid");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", vipCardService.findVipCard(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "首次充值vip生成订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "vipcardid", value = "vip卡片id", dataType = "String"),
    })
    @PostMapping("/createOrder")
    public Map<String, Object> addorder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long vipcardid = ModelUtil.getLong(params, "vipcardid");
        Map<String, Object> map = vipCardService.createOrderAndSys(userId, vipcardid);
        result.put("data", map);
        setOkResult(result, "成功");
        return result;
    }

    @ApiOperation(value = "续费充值vip生成订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "vipcardid", value = "vip卡片id", dataType = "String"),
    })
    @PostMapping("/renewalOrder")
    public Map<String, Object> renewalOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long vipcardid = ModelUtil.getLong(params, "vipcardid");
        Map<String, Object> map = vipCardService.renewalOrder(userId, vipcardid);
        result.put("data", map);
        setOkResult(result, "成功");
        return result;
    }

    @ApiOperation(value = "vip卡钱包充值")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单号id", dataType = "String"),
    })
    @PostMapping("/addWalletAppOrder")
    public Map<String, Object> addWalletOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> suf = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long orderid = ModelUtil.getLong(params, "orderid");
        Map<String, Object> maps = vipCardService.findOrderByorderId(orderid);
        String orderno = ModelUtil.getStr(maps, "orderno");
        BigDecimal amountmoney;
        int order_type = ModelUtil.getInt(maps, "order_type");//订单类型，判断是首冲还是续费
        if (0 == order_type) {
            amountmoney = ModelUtil.getDec(maps, "price", BigDecimal.ZERO);//首冲价格
        } else {
            amountmoney = ModelUtil.getDec(maps, "renewal_fee", BigDecimal.ZERO);//续费价格
        }
        if (userId == 0 || amountmoney.compareTo(BigDecimal.ZERO) == 0) {
            setErrorResult(result, "参数错误");
        } else {
            Map<String, Object> map = userWalletService.getUserWallet(userId);
            BigDecimal bigDecimal = ModelUtil.getDec(map, "walletbalance", BigDecimal.ZERO);//钱包金额
            if (bigDecimal.compareTo(amountmoney) < 1) {
                suf.put("sufficient", 0);
                setOkResult(result, "钱包余额不足");
            } else {
                vipCardService.wallet(bigDecimal, amountmoney, orderid, userId, order_type, orderno);
                suf.put("sufficient", 1);
                setOkResult(result, "充值成功");
            }
            suf.put("orderid", orderid);
            result.put("data", suf);
            return result;
        }
        return result;
    }

    private Map<String, Object> getResult(IPayService.PayBean payBean) {
        Map<String, Object> result = new HashMap<>();
        result.put("state", payBean.isState());
        result.put("timestamp", payBean.getTimeStamp());
        result.put("noncestr", payBean.getNonceStr());
        result.put("package", payBean.getPackageValue());
        result.put("signtype", payBean.getSignType());
        result.put("paysign", payBean.getPaysign());
        result.put("returnmsg", payBean.getReturnMsg());
        result.put("appid", payBean.getAppId());
        return result;
    }

    @ApiOperation(value = "会员信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
    })
    @PostMapping("/getUserMember")
    public Map<String, Object> getUserMember(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userManagementService.getUserMemberWeb(userid));
            setOkResult(result, "成功");
        }
        return result;
    }


}
