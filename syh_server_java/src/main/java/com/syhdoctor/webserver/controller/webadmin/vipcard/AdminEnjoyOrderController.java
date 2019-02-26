package com.syhdoctor.webserver.controller.webadmin.vipcard;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.vipcard.EnjoyOrderService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/Admin/EnjoyOrder 尊享订单")
@RestController
@RequestMapping("/Admin/EnjoyOrder")
public class AdminEnjoyOrderController extends BaseController {

    @Autowired
    private EnjoyOrderService enjoyOrderService;


    @ApiOperation(value = "尊享订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageIndex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getEnjoyOrder")
    public Map<String, Object> getEnjoyOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "phone");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageIndex = ModelUtil.getInt(params, "pageIndex", 1);
        int pageSize = ModelUtil.getInt(params, "pageSize", 20);
        result.put("data", enjoyOrderService.getEnjoyOrder(name, phone, begintime, endtime, pageIndex, pageSize));
        result.put("total", enjoyOrderService.getEnjoyOrderCount(name, phone, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "尊享订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/getEnjoyOrderId")
    public Map<String, Object> getEnjoyOrderId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", enjoyOrderService.getEnjoyOrderId(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }











    @ApiOperation(value = "尊享列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "level", value = "等级", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageIndex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getEnjoyList")
    public Map<String, Object> getEnjoyList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "phone");
        int level = ModelUtil.getInt(params, "level");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", enjoyOrderService.getEnjoyList(name, phone, level, begintime, endtime, pageIndex, pagesize));
        result.put("total", enjoyOrderService.getEnjoyListCount(name, phone, level, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "尊享列表详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/getEnjoyListId")
    public Map<String, Object> getEnjoyListId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", enjoyOrderService.getEnjoyListId(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    @ApiOperation(value = "修改尊享列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ceefax", value = "电话咨询", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "video", value = "视频咨询", dataType = "String"),
    })
    @PostMapping("/updateEnjoyList")
    public Map<String, Object> updateEnjoyList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int ceefax = ModelUtil.getInt(params, "ceefax");
        int video = ModelUtil.getInt(params, "video");
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", enjoyOrderService.updateEnjoyList(ceefax, video, id));
            setOkResult(result, "查询成功");
        }
        return result;
    }


}
