package com.syhdoctor.webserver.controller.webapp.appapi.user.microclass;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.microclass.MicroClassService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/App/MicroClass 健康大学")
@RestController
@RequestMapping("/App/MicroClass")
public class AppMicroClassController extends BaseController {

    @Autowired
    private MicroClassService microClassService;

    @ApiOperation(value = "课程详情")
    @ApiImplicitParam(paramType = "query", name = "id", value = "课程id", required = true, dataType = "String")
    @PostMapping("/getMicroClassCourse")
    public Map<String, Object> getCourse(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");

        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", microClassService.getAppCourse(id));
            microClassService.updateCourseBrowsenum(id);//添加浏览量
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "邀请卡")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "courseid", value = "课程id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
    })
    @PostMapping("/getInvitingCard")
    public Map<String, Object> getInvitingCard(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long courseid = ModelUtil.getLong(params, "courseid");
        long userid = ModelUtil.getLong(params, "userid");
        if (courseid == 0 || userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", microClassService.getInvitingCard(courseid, userid));
            setOkResult(result, "查询成功");
        }
        return result;
    }
}
