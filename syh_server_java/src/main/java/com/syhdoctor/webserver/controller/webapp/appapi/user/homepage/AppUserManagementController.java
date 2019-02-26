package com.syhdoctor.webserver.controller.webapp.appapi.user.homepage;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.user.UserManagementService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2019/1/7
 */
@Api(description = "/App/UserManagement app健康档案")
@RestController
@RequestMapping("/App/UserManagement")
public class AppUserManagementController extends BaseController {

    @Autowired
    private UserManagementService userManagementService;

    @ApiOperation(value = "基本信息查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
    })
    @PostMapping("/getBasic")
    public Map<String, Object> getBasic(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userManagementService.getBasicApp(userid));
            setOkResult(result, "成功");
        }
        return result;
    }


    @ApiOperation(value = "基本信息修改加载")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
    })
    @PostMapping("/getBasicLoad")
    public Map<String, Object> getBasicLoad(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userManagementService.getBasicList(userid));
            setOkResult(result, "成功");
        }
        return result;
    }


}
