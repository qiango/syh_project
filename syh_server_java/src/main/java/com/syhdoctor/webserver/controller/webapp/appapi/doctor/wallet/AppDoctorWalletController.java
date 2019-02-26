
package com.syhdoctor.webserver.controller.webapp.appapi.doctor.wallet;

import com.syhdoctor.common.utils.EnumUtils.MoneyTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.system.SystemService;
import com.syhdoctor.webserver.service.wallet.DoctorWalletService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Api(description = "/App/DoctorWallet 医生钱包管理")
@RestController
@RequestMapping("/App/DoctorWallet")
public class AppDoctorWalletController extends BaseController {

    @Autowired
    private DoctorWalletService doctorWalletService;

    @Autowired
    private SystemService systemService;

    @ApiOperation(value = "积分首页")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
    })
    @PostMapping("/getDoctorWalletHomepage")
    public Map<String, Object> getDoctorWalletHomepage(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        if (doctorId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorWalletService.getDoctorWalletHomepage(doctorId));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "交易记录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数据量", dataType = "String"),
    })
    @PostMapping("/transactionRecordList")
    public Map<String, Object> transactionRecordList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorWalletService.transactionRecordList(doctorid, pageindex, pagesize));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "交易记录搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "moneytype", value = "金额类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数据量", dataType = "String"),
    })
    @PostMapping("/findTransactionRecordList")
    public Map<String, Object> findTransactionRecordList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        int moneytype = ModelUtil.getInt(params, "moneytype");
        MoneyTypeEnum value = MoneyTypeEnum.getValue(moneytype);
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorWalletService.findTransactionRecordList(doctorid, value, begintime, endtime, pageindex, pagesize));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "申请提现")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
    })
    @PostMapping("/applyExtract")
    public Map<String, Object> applyExtract(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorWalletService.applyExtract(doctorid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "确认提现")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "cardid", value = "银行卡id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "amountmoney", value = "提现积分", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "code", value = "验证码", dataType = "String"),
    })
    @PostMapping("/confirmExtract")
    public Map<String, Object> confirmExtract(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        long cardid = ModelUtil.getLong(params, "cardid");
        BigDecimal amountmoney = ModelUtil.getDec(params, "amountmoney", BigDecimal.ZERO);
        String code = ModelUtil.getStr(params, "code");
        if (doctorid == 0 || cardid == 0 || StrUtil.isEmpty(code)) {
            setErrorResult(result, "参数错误");
        } else {
            Map<String, Object> map = doctorWalletService.confirmExtract(doctorid, cardid, amountmoney, code);
            int status = ModelUtil.getInt(map, "status");
            if (status == 2) {
                setResult(result, "未获取验证码", 100);
            } else if (status == 3) {
                setResult(result, "验证码错误", 100);
            } else if (status == 4) {
                setResult(result, "积分不能低于最低值", 100);
            } else {
                result.put("data", map);
                setOkResult(result, "查询成功");
            }
        }
        return result;
    }

    @ApiOperation(value = "体现详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "id", value = "交易记录id", dataType = "String"),
    })
    @PostMapping("/getExtractOrder")
    public Map<String, Object> getExtractOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        long id = ModelUtil.getLong(params, "id");
        if (orderid == 0 && id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorWalletService.getExtractOrder(orderid, id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "交易详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "交易id", dataType = "String"),
    })
    @PostMapping("/getTransactionRecord")
    public Map<String, Object> getTransactionRecord(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorWalletService.getTransactionRecord(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
    })
    @PostMapping("/sendExtractCode")
    public Map<String, Object> sendExtractCode(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorWalletService.sendExtractCode(doctorid));
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
        if (type == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", systemService.getHomePage(type));
            setOkResult(result, "成功");
        }
        return result;
    }

//    @ApiOperation(value = "提现文案")
//    @ApiImplicitParams({
//    })
//    @PostMapping("/getExtractPage")
//    public Map<String, Object> getExtractPage(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
//        Map<String, Object> result = new HashMap<>();
//        result.put("data",systemService.getExtractPage());
//        setOkResult(result,"成功");
//        return result;
//    }


}
