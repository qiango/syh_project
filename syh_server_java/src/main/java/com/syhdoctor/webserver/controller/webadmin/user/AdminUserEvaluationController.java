package com.syhdoctor.webserver.controller.webadmin.user;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.user.UserEvaluateService;
import io.swagger.annotations.*;
import org.omg.CORBA.ObjectHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/Admin/evaluate 用户评价")
@RestController
@RequestMapping("/Admin/evaluate")
public class AdminUserEvaluationController extends BaseController {

    @Autowired
    private UserEvaluateService userEvaluateService;


    @ApiOperation(value = "用户评价列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "username", value = "用户姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorname", value = "医生姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageIndex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getUserEvaluateList")
    public Map<String, Object> getUserEvaluateList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String username = ModelUtil.getStr(params, "username");
        String doctorname = ModelUtil.getStr(params, "doctorname");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageIndex = ModelUtil.getInt(params, "pageIndex", 1);
        int pageSize = ModelUtil.getInt(params, "pageSize", 20);
        result.put("data", userEvaluateService.getUserEvaluateList(id, username, doctorname, begintime, endtime, pageIndex, pageSize));
        result.put("total", userEvaluateService.getUserEvaluateListCount(id, username, doctorname, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "删除，修改删除原因")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "delreason", value = "删除原因", dataType = "String"),
    })
    @PostMapping("/delUpdateReason")
    public Map<String, Object> delUpdateReason(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String delreason = ModelUtil.getStr(params, "delreason");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userEvaluateService.delUpdateReason(id, delreason));
            setOkResult(result, "删除成功");
        }
        return result;
    }


}
