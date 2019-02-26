package com.syhdoctor.webserver.controller.webapp.webapi.homepage;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.user.UserManagementService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2019/1/7
 */
@Api(description = "/Web/UserManagement Web健康档案")
@RestController
@RequestMapping("/Web/UserManagement")
public class WebUserManagementController extends BaseController {

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
            result.put("data", userManagementService.getBasicListWeb(userid));
            setOkResult(result, "成功");
        }
        return result;
    }


    @ApiOperation(value = "病例信息修改")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "weight", value = "体重", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "height", value = "身高", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ismarry", value = "是否结婚", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isallergy", value = "是否过敏", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ischronicillness", value = "有无慢性病史", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "issurgery", value = "有无手术史", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "issurgeryother", value = "手术病史其他内容", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "familyother", value = "家族病史其他内容", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ischronicillnessother", value = "慢性病史其他内容", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isallergyother", value = "过敏史其他内容", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isfamilyhistory", value = "有无家族史", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "issmoking", value = "是否抽烟", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isdrinking", value = "是否饮酒", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isfertility", value = "有无生育史", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pregnancy", value = "妊娠次数", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "childbirth", value = "分娩次数", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "abortion", value = "流产次数", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "mencharage", value = "初潮年龄", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "finalmenarche", value = "末次月经时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ismenopause", value = "是否绝经", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "values", value = "选中的值id", dataType = "String"),
    })
    @PostMapping("/updateUserCase")
    public Map<String, Object> updateUserCase(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        Object weight = ModelUtil.getIntForNull(params, "weight");
        Object height = ModelUtil.getIntForNull(params, "height");
        Object ismarry = ModelUtil.getIntForNull(params, "ismarry");
        Object isallergy = ModelUtil.getIntForNull(params, "isallergy");
        Object ischronicillness = ModelUtil.getIntForNull(params, "ischronicillness");
        Object issurgery = ModelUtil.getIntForNull(params, "issurgery");
        String issurgeryOther = ModelUtil.getStr(params, "issurgeryother");
        String familyOther = ModelUtil.getStr(params, "familyother");
        String ischronOther = ModelUtil.getStr(params, "ischronicillnessother");
        String isallergyOther = ModelUtil.getStr(params, "isallergyother");
        Object isfamilyhistory = ModelUtil.getIntForNull(params, "isfamilyhistory");
        Object issmoking = ModelUtil.getIntForNull(params, "issmoking");
        Object isdrinking = ModelUtil.getIntForNull(params, "isdrinking");
        Object isfertility = ModelUtil.getIntForNull(params, "isfertility");
        Object pregnancy = ModelUtil.getIntForNull(params, "pregnancy");
        Object childbirth = ModelUtil.getIntForNull(params, "childbirth");
        Object abortion = ModelUtil.getIntForNull(params, "abortion");
        Object menarche_age = ModelUtil.getIntForNull(params, "mencharage");
        long final_menarche = ModelUtil.getLong(params, "finalmenarche");
        Object ismenopause = ModelUtil.getIntForNull(params, "ismenopause");
        List<?> values = ModelUtil.getList(params, "values", new ArrayList<>());
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userManagementService.updateUserCaseApp(userid, weight, height, ismarry, isallergy, ischronicillness, ischronOther, issurgery, issurgeryOther, isallergyOther, isfamilyhistory, familyOther, issmoking, isdrinking, isfertility, pregnancy, childbirth, abortion, menarche_age, final_menarche, ismenopause, values));
            setOkResult(result, "成功");
        }
        return result;
    }


}
