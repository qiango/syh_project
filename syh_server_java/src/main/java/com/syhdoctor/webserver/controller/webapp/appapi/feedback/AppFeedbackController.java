package com.syhdoctor.webserver.controller.webapp.appapi.feedback;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.feedback.FeedbackService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Api(description = "/App/Feedback 意见反馈接口")
@RequestMapping("/App/Feedback")
public class AppFeedbackController extends BaseController {

    @Autowired
    private FeedbackService feedbackService;

    @ApiOperation(value = "意见反馈")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "医生或者用户id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生或者用户id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phonetype", value = "1:Android,2:ios", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "system", value = "1:用户端，2：医生端", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "content", value = "反馈信息", required = true, dataType = "String"),
    })
    @PostMapping("/addFeedback")
    public Map<String, Object> addFeedback(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        long doctorid = ModelUtil.getLong(params, "doctorid");
        int type = ModelUtil.getInt(params, "phonetype");
        int system = ModelUtil.getInt(params, "system");
        String content = ModelUtil.getStr(params, "content");
        if (StrUtil.isEmpty(content)) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", feedbackService.addFeedback(userid, doctorid, type, system, content));
            setOkResult(result, "添加成功");
        }
        return result;
    }

}