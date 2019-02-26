package com.syhdoctor.webserver.controller.webapp.appapi.special;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.specialistcounseling.SpecialistCounselingService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2018/11/16
 */
@Api(description = "/App/Special App首页特色专科")
@RestController
@RequestMapping("/App/Special")
public class AppSpecialController extends BaseController {

    @Autowired
    private SpecialistCounselingService specialistCounselingService;

    @ApiOperation(value = "用户端首页专病资讯列表")
    @ApiImplicitParams({
    })
    @PostMapping("/getSpecialCounList")
    public Map<String, Object> getSpecialCounList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", specialistCounselingService.getSpecialCounList());
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "用户端首页专病资讯详情")
    @ApiImplicitParams({
    })
    @PostMapping("/getSpecialCountDetail")
    public Map<String, Object> getSpecialCountDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id= ModelUtil.getLong(params,"id");
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
        long id= ModelUtil.getLong(params,"id");
        result.put("data", specialistCounselingService.getSpecialDetail(id));
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "用户端首页特色专科列表")
    @ApiImplicitParams({
    })
    @PostMapping("/getSpecialList")
    public Map<String, Object> getSpecialList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", specialistCounselingService.getSpecialList());
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "用户端首页特色专科分页列表")
    @ApiImplicitParams({
    })
    @PostMapping("/getSpecialListPage")
    public Map<String, Object> getSpecialListPage(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", specialistCounselingService.getSpecialList(pageSize,pageIndex));
        result.put("total",specialistCounselingService.getSpecialListConut());
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "用户端首页专病资讯分页列表")
    @ApiImplicitParams({
    })
    @PostMapping("/getSpecialCounListPage")
    public Map<String, Object> getSpecialCounListPage(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", specialistCounselingService.getSpecialCounListPage(pageSize,pageIndex));
        result.put("total",specialistCounselingService.getSpecialCountListConut());
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "开屏图")
    @ApiImplicitParams({
    })
    @PostMapping("/getFlickerscreen")
    public Map<String, Object> getFlickerscreen(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", specialistCounselingService.findfocus());
        setOkResult(result, "查询成功!");
        return result;
    }

    /**
     *  todo 版本兼容
     * @param params
     * @return
     */
    @ApiOperation(value = "专科 带层级常见病症状列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "专病咨询id", dataType = "String"),
    })
    @PostMapping("/getSpecialCounListSymptomsTypeList")
    public Map<String, Object> getAppSymptomsTypeList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", specialistCounselingService.getSymptomsTypeList(id));
        setOkResult(result, "查询成功");
        return result;
    }
}
