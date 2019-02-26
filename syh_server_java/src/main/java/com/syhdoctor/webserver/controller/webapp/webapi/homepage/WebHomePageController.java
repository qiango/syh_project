package com.syhdoctor.webserver.controller.webapp.webapi.homepage;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.homepage.HomePageService;
import com.syhdoctor.webserver.service.specialistcounseling.SpecialistCounselingService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/Web/HomePage Web首页")
@RestController
@RequestMapping("/Web/HomePage")
public class WebHomePageController extends BaseController {

    @Autowired
    private HomePageService homePageService;

    @Autowired
    private SpecialistCounselingService specialistCounselingService;

    @ApiOperation(value = "首页")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
    })
    @PostMapping("/homepage")
    public Map<String, Object> honePage(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        result.put("data", homePageService.homePage(userid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "大学")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "课程类型id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", dataType = "String"),
    })
    @PostMapping("/courseList")
    public Map<String, Object> courseList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", homePageService.courseAndTypeList(id, pageIndex, pageSize));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "用户端首页特色专科列表")
    @ApiImplicitParams({
    })
    @PostMapping("/getSpecialList")
    public Map<String, Object> getSpecialList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", specialistCounselingService.getWebSpecialList());
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "我的")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
    })
    @PostMapping("/getUser")
    public Map<String, Object> getDepartmentList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        result.put("data", homePageService.getUser(userid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "个人消息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String"),
    })
    @PostMapping("/getUserMessageList")
    public Map<String, Object> getUserMessageList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        int pageIndex = ModelUtil.getInt(params, "pageindex");
        int pageSize = ModelUtil.getInt(params, "pagesize");
        result.put("data", homePageService.getUserMessageList(userId, pageIndex, pageSize));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "个人消息详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "消息id", dataType = "String"),
    })
    @PostMapping("/getUserMessageDetailed")
    public Map<String, Object> getUserMessageDetailed(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", homePageService.getUserMessageDetailed(id));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "更新消息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/updateMessageReadStatus")
    public Map<String, Object> updateMessageReadStatus(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", homePageService.updateMessageReadStatus(id));
            setOkResult(result, "已阅读");
        }
        return result;
    }

    @ApiOperation(value = "用户端首页专病资讯详情")
    @ApiImplicitParams({
    })
    @PostMapping("/getSpecialCountDetail")
    public Map<String, Object> getSpecialCountDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", specialistCounselingService.getSpecialCountDetail(id));
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "用户端首页特色专科详情")
    @ApiImplicitParams({
    })
    @PostMapping("/getSpecialDetail")
    public Map<String, Object> getSpecialDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", specialistCounselingService.getSpecialDetail(id));
        setOkResult(result, "查询成功!");
        return result;
    }
}
