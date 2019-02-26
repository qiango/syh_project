package com.syhdoctor.webserver.controller.webapp.webapi.user.video;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.video.UserVideoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/Web/UserVideo 用户视频问诊")
@RestController
@RequestMapping("/Web/UserVideo")
public class WebUserVideoController extends BaseController {

    @Autowired
    private UserVideoService userVideoService;


    @ApiOperation(value = "查看评价")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "订单类型", required = true, dataType = "String"),
    })
    @PostMapping("getOrderEvaluate")
    public Map<String, Object> getOrderEvaluate(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        if (orderid == 0 || ordertype == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.getOrderEvaluate(orderid, ordertype));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "添加评价")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "订单类型", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isanonymous", value = "是否匿名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "evaluate", value = "评星", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "content", value = "评价", required = true, dataType = "String"),
    })
    @PostMapping("addOrderEvaluate")
    public Map<String, Object> addOrderEvaluate(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        int isanonymous = ModelUtil.getInt(params, "isanonymous");
        int evaluate = ModelUtil.getInt(params, "evaluate");
        String content = ModelUtil.getStr(params, "content");
        if (orderid == 0 || ordertype == 0 || evaluate == 0 || StrUtil.isEmpty(content)) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.addOrderEvaluate(orderid, ordertype, isanonymous, evaluate, content));
            setOkResult(result, "评价成功");
        }
        return result;
    }

    @ApiOperation(value = "查看诊断")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "订单类型", required = true, dataType = "String"),
    })
    @PostMapping("getOrderGuidance")
    public Map<String, Object> getOrderGuidance(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        if (orderid == 0 || ordertype == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userVideoService.getOrderGuidance(orderid, ordertype));
            setOkResult(result, "查询成功");
        }
        return result;
    }
}
