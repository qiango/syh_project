package com.syhdoctor.webserver.controller.webapp.webapi.user;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.doctor.DoctorInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/Web/DoctorInfo web用戶端接口")
@RestController
@RequestMapping("/Web/DoctorInfo")
public class WebDoctorInfoController extends BaseController {


    @Autowired
    private DoctorInfoService doctorInfoService;

    @ApiOperation(value = "名医在线")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室ID", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "recommend", value = "是否推荐0 未推荐 1 推荐", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String"),
    })
    @PostMapping("/getDoctorOnDutyList")
    public Map<String, Object> getDoctorOnDutyList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int departmentId = ModelUtil.getInt(params, "departmentid");
        int recommend = ModelUtil.getInt(params, "recommend");
        int pageIndex = ModelUtil.getInt(params, "pageindex");
        int pageSize = ModelUtil.getInt(params, "pagesize");
        result.put("data", doctorInfoService.getDoctorOnDutyList(departmentId, recommend, pageIndex, pageSize));
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "医生评论列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String"),
    })
    @PostMapping("/getDoctorEvaluateList")
    public Map<String, Object> getDoctorEvaluateList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        int pageIndex = ModelUtil.getInt(params, "pageindex");
        int pageSize = ModelUtil.getInt(params, "pagesize");
        result.put("data", doctorInfoService.getDoctorEvaluateList(doctorId, pageIndex, pageSize));
        setOkResult(result, "查询成功!");
        return result;
    }

    /**
     * @param params
     * @return
     */
    @ApiOperation(value = "名医医生主页")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户ID", dataType = "String"),
    })
    @PostMapping("/getDoctorHomePageNew")
    public Map<String, Object> getDoctorHomePageNew(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        long userId = ModelUtil.getLong(params, "userid");
        result.put("data", doctorInfoService.getDoctorHomePageNew(doctorId, userId));
        setOkResult(result, "查询成功!");
        return result;
    }

    /**
     * todo 版本兼容
     * @param params
     * @return
     */
    @ApiOperation(value = "名医医生主页症状列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户ID", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "订单类型 1：图文，2：电话", dataType = "String"),
    })
    @PostMapping("/getDoctorSymptomslist")
    public Map<String, Object> getDoctorSymptomslist(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        long userId = ModelUtil.getLong(params, "userid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        if (doctorId == 0 || userId == 0 || ordertype == 0) {
            setErrorResult(result, "参数错误");
            return result;
        }
        result.put("data", doctorInfoService.getDoctorSymptomslist(userId, doctorId, ordertype));
        setOkResult(result, "查询成功!");
        return result;
    }
}

