package com.syhdoctor.webserver.controller.webadmin.user;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.user.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/Admin/User 用户管理")
@RestController
@RequestMapping("/Admin/User")
public class AdminUserController extends BaseController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "用户名字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = "电话", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getUserList")
    public Map<String, Object> getUserList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "phone");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", userService.getUserList(name, phone, pageIndex, pageSize));
        result.put("total", userService.getUserCount(name, phone));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "用户详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
    })
    @PostMapping("/getUser")
    public Map<String, Object> getUser(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        if (userId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userService.getUserByAdmin(userId));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "用户处方列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户ｉｄ", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getPrescriptionList")
    public Map<String, Object> getPrescriptionList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        if (userId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userService.getPrescriptionList(userId, pageIndex, pageSize));
            result.put("total", userService.getPrescriptionCount(userId));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "用户订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户ｉｄ", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getOrderList")
    public Map<String, Object> getOrderList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        if (userId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userService.AdminUserOrderList(userId, pageIndex, pageSize));
            result.put("total", userService.getOrderCount(userId));
            setOkResult(result, "查询成功");
        }
        return result;
    }


}
