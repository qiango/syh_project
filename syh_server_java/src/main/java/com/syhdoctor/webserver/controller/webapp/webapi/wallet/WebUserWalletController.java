package com.syhdoctor.webserver.controller.webapp.webapi.wallet;

import com.syhdoctor.common.pay.IPayService;
import com.syhdoctor.common.utils.EnumUtils.MoneyTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.OpenTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.TextFixed;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.system.SystemService;
import com.syhdoctor.webserver.service.wallet.UserWalletBaseService;
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

@Api(description = "/Web/UserWallet 用户钱包管理")
@RestController
@RequestMapping("/Web/UserWallet")
public class WebUserWalletController extends BaseController {

    @Autowired
    private UserWalletService userWalletService;


    @Autowired
    private SystemService systemService;

    @ApiOperation(value = "我的钱包")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
    })
    @PostMapping("/getUserWallet")
    public Map<String, Object> getUserWallet(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        if (userId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userWalletService.getUserWallet(userId));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "充值卡充值")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "redeemcode", value = "兑换码", dataType = "String"),
    })
    @PostMapping("/addRechargeableOrder")
    public Map<String, Object> addRechargeableOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        String redeemcode = ModelUtil.getStr(params, "redeemcode");
        if (userId == 0 || StrUtil.isEmpty(redeemcode)) {
            setErrorResult(result, "参数错误");
        } else {
            UserWalletBaseService.WalletBean walletBean = userWalletService.addRechargeableOrder(userId, redeemcode);
            if (walletBean.getCardstatus() == 1) {
                setErrorResult(result, "充值卡不存在");
            } else if (walletBean.getCardstatus() == 2) {
                setErrorResult(result, "充值卡已被使用");
            } else {
                result.put("data", walletBean);
                setOkResult(result, "充值成功");
            }
        }
        return result;
    }


    @ApiOperation(value = "支付宝web充值")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "amountmoney", value = "金额", dataType = "String"),
    })
    @PostMapping("/addAliWebOrder")
    public Map<String, Object> addAliWebOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        BigDecimal amountmoney = ModelUtil.getDec(params, "amountmoney", BigDecimal.ZERO);
        if (userId == 0 || amountmoney.compareTo(BigDecimal.ZERO) == 0) {
            setErrorResult(result, "参数错误");
        } else {
            if (amountmoney.compareTo(TextFixed.user_min_walletbalance) < 0) {
                setErrorResult(result, "充值金额不能小于10元");
            } else {
                UserWalletBaseService.WalletBean walletBean = userWalletService.addOrder(userId, amountmoney, OpenTypeEnum.Ali.getCode());
                result.put("data", userWalletService.aliWebPay(walletBean.getOrderno(), walletBean.getActualmoney()));
                setOkResult(result, "充值成功");
            }
        }
        return result;
    }


    @ApiOperation(value = "微信web充值")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "amountmoney", value = "金额", dataType = "String"),
    })
    @PostMapping("/addWechatWebOrder")
    public Map<String, Object> addWechatWebOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        BigDecimal amountmoney = ModelUtil.getDec(params, "amountmoney", BigDecimal.ZERO);
        String ip = request.getRemoteAddr();
        if (userId == 0 || amountmoney.compareTo(BigDecimal.ZERO) == 0) {
            setErrorResult(result, "参数错误");
        } else {
            if (amountmoney.compareTo(TextFixed.user_min_walletbalance) < 0) {
                setErrorResult(result, "充值金额不能小于10元");
            } else {
                UserWalletBaseService.WalletBean walletBean = userWalletService.addOrder(userId, amountmoney, OpenTypeEnum.Wechat.getCode());
                IPayService.PayBean payBean = userWalletService.weChatWebPay(walletBean.getOrderno(), walletBean.getActualmoney(), ip, walletBean.getOpenid());
                Map<String, Object> result1 = getResult(payBean);
                result1.put("orderid", walletBean.getOrderid());
                result.put("data", result1);
                setOkResult(result, "充值成功");
            }
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

    @ApiOperation(value = "交易记录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "moneytype", value = "金额", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数据量", dataType = "String"),
    })
    @PostMapping("/transactionRecordList")
    public Map<String, Object> transactionRecordList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        int moneytype = ModelUtil.getInt(params, "moneytype");
        MoneyTypeEnum value = MoneyTypeEnum.getValue(moneytype);
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        if (userId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userWalletService.transactionRecordList(value, userId, pageindex, pagesize));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "交易详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/getTransactionRecord")
    public Map<String, Object> getTransactionRecord(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userWalletService.getTransactionRecord(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "充值详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "id", dataType = "String"),
    })
    @PostMapping("/getRechargeableOrder")
    public Map<String, Object> orderid(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userWalletService.getRechargeableOrder(orderid));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    @ApiOperation(value = "支付成功页面")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "type", value = "文案类型", dataType = "String"),
    })
    @PostMapping("/getHomePage")
    public Map<String, Object> getHomePage(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int type = ModelUtil.getInt(params, "type");
        if(type == 0){
            setErrorResult(result,"参数错误");
        }else{
            result.put("data",systemService.getHomePage(type));
            setOkResult(result,"成功");
        }
        return result;
    }





}
