package com.syhdoctor.webserver.controller.webapp.appapi.doctor;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.doctor.DoctorPhoneService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/App/doctorPhone 医生急诊")
@RestController
@RequestMapping("/App/doctorPhone")
public class AppDoctorPhoneController extends BaseController {


    @Autowired
    private DoctorPhoneService doctorPhoneService;

    //todo 版本兼容
    @ApiOperation(value = "订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/getPhoneOrderById")
    public Map<String, Object> getPhoneOrderById(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorPhoneService.getPhoneOrderById(orderid));
        }
        setOkResult(result, "查询成功");
        return result;
    }

    //TODO 版本兼容
    @ApiOperation(value = "保存诊疗结果")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diagnosis", value = "诊疗结果", required = true, dataType = "String"),
    })
    @PostMapping("/updatePhoneOrder")
    public Map<String, Object> updatePhoneOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        String diagnosis = ModelUtil.getStr(params, "diagnosis");
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorPhoneService.updatePhoneOrder(orderid, diagnosis));
        }
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/getDoctorPhoneDetail")
    public Map<String, Object> getUserPhoneDetailed(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorPhoneService.getUserPhoneDetailed(orderid));
        }
        setOkResult(result, "查询成功");
        return result;
    }


}
