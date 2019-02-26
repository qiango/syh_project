package com.syhdoctor.webserver.controller.webapp.appapi.user;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.RegexValidateUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.knowledge.KnowledgeService;
import com.syhdoctor.webserver.service.user.UserService;
import com.syhdoctor.webserver.utils.IdCardVerCheckUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/App/User App用戶接口")
@RestController
@RequestMapping("/App/User")
public class AppUserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private KnowledgeService knowledgeService;

    @ApiOperation(value = "发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "phone", value = "手机号码", dataType = "String"),
    })
    @PostMapping("/sendAuthenticationCode")
    public Map<String, Object> sendAuthenticationCode(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String phone = ModelUtil.getStr(params, "phone");
        if (!RegexValidateUtil.checkCellphone(phone)) {
            setErrorResult(result, "手机号码格式错误");
        } else {
            boolean flag = userService.sendAuthenticationCode(phone);
            if (flag) {
                setOkResult(result, "发送成功");
            } else {
                setErrorResult(result, "发送失败");
            }
        }
        return result;
    }

    @ApiOperation(value = "登录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "phone", value = "手机号码", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "code", value = "验证码", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phonetype", value = "渠道1：安卓，2：苹果", dataType = "String"),
    })
    @PostMapping("/login")
    public Map<String, Object> login(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String phone = ModelUtil.getStr(params, "phone");
        String code = ModelUtil.getStr(params, "code");
        int phonetype = ModelUtil.getInt(params, "phonetype");
        if (StrUtil.isEmpty(phone, code)) {
            setErrorResult(result, "参数错误");
        } else {
            if (!RegexValidateUtil.checkCellphone(phone)) {
                setErrorResult(result, "请填入正确的手机号!");
            } else {
                int num = userService.validCode(phone, code);
                if (num == -1) {
                    setErrorResult(result, "请先获取验证码");
                } else if (num == -2) {
                    setErrorResult(result, "验证码错误");
                } else if (num == 1) {
                    result.put("data", userService.login(phone, phonetype));
                    setOkResult(result, "登录成功");
                }
            }

        }
        return result;
    }

    @ApiOperation(value = "完善信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "headpic", value = "头像", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "名字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "gender", value = "性别(1:男,2:女,9:保密)", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "cardno", value = "省份证", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "areas", value = "地址（省市区id逗号隔开）", dataType = "String"),
    })
    @PostMapping("/addUserInfo")
    public Map<String, Object> addUserInfo(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        String headpic = ModelUtil.getStr(params, "headpic");
        String name = ModelUtil.getStr(params, "name");
        int gender = ModelUtil.getInt(params, "gender");
        String cardno = ModelUtil.getStr(params, "cardno");
        String areas = ModelUtil.getStr(params, "areas");
        if (userid == 0 || StrUtil.isEmpty(name)) {
            setErrorResult(result, "参数错误");
        } else {
            if (StrUtil.isEmpty(headpic)) {
                setErrorResult(result, "请上传头像");
            } else if (areas.length() != 20) {
                setErrorResult(result, "请填写完整地区");
            } else {
                boolean validate = IdCardVerCheckUtil.validate(cardno);
                if (!validate) {
                    setErrorResult(result, "请输入正确的身份证号");
                    return result;
                }
                boolean flag = userService.addUserInfo(userid, headpic, name, gender, cardno, areas);
                if (flag) {
                    setOkResult(result, "保存成功");
                } else {
                    setErrorResult(result, "保存失败");
                }
            }
        }
        return result;
    }

    @ApiOperation(value = "查看个人信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
    })
    @PostMapping("/getUser")
    public Map<String, Object> getUser(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userService.getUser(userid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    /**
     * TODO  版本兼容
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "查看个人健康档案")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
    })
    @PostMapping("/getHealth")
    public Map<String, Object> getHealth(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userService.getHealth(userid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    /**
     * TODO 版本兼容
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "保存个人健康档案")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "height", value = "身高", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "weight", value = "体重", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "history", value = "患病史", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "treatment", value = "治疗方案", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "habitlife", value = "生活习惯", dataType = "String"),
    })
    @PostMapping("/updateHealth")
    public Map<String, Object> updateHealth(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        double height = ModelUtil.getDouble(params, "height", 0);
        double weight = ModelUtil.getDouble(params, "weight", 0);
        String history = ModelUtil.getStr(params, "history");
        String treatment = ModelUtil.getStr(params, "treatment");
        String habitlife = ModelUtil.getStr(params, "habitlife");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userService.updateHealth(userid, height, weight, history, treatment, habitlife));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "查看地区")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "地区id,查询省份id=0", dataType = "String"),
    })
    @PostMapping("/getAreas")
    public Map<String, Object> getAreaByParentId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int code = ModelUtil.getInt(params, "id");
        result.put("data", userService.getAreaByParentId(code));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "用户签到（新版）")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
    })
    @PostMapping("/start")
    public Map<String, Object> start(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            int integral = userService.userSignIn(userid);
            Map<String, Object> vip = userService.isVip(userid);
            Map<String, Object> map = new HashMap<>();
            if (integral > 0) {
                map.put("integral", integral);
            } else {
                map.put("integral", 0);
            }
            map.put("uservip", vip);
            result.put("data", map);
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "积分列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/integralList")
    public Map<String, Object> integralList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userService.userIntegralList(userid, pageIndex, pageSize));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "处方列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getPrescriptionList")
    public Map<String, Object> getPrescriptionList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userService.getPrescriptionList(userid, pageIndex, pageSize));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "处方详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "prescriptionid", value = "处方id", required = true, dataType = "String"),
    })
    @PostMapping("/getPrescription")
    public Map<String, Object> getPrescription(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long prescriptionid = ModelUtil.getLong(params, "prescriptionid");
        if (prescriptionid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userService.getAppPrescription(prescriptionid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "知识库列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "name", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getDiseaseList")
    public Map<String, Object> getDiseaseList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", knowledgeService.getAppDiseaseList(name, pageIndex, pageSize));
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * todo 没用到
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "知识库列表搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "name", dataType = "String"),
    })
    @PostMapping("/searchDiseaseList")
    public Map<String, Object> searchDiseaseList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        result.put("data", knowledgeService.searchDiseaseList(name));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "知识库详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "diseaseid", value = "疾病id", dataType = "String"),
    })
    @PostMapping("/getDisease")
    public Map<String, Object> getDisease(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long diseaseid = ModelUtil.getLong(params, "diseaseid");
        result.put("data", knowledgeService.getAppDisease(diseaseid));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "用户就诊记录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getUserOrderList")
    public Map<String, Object> getUserOrderList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", userService.getUserOrderList(userid, pageIndex, pageSize));
        setOkResult(result, "查询成功");
        return result;
    }
}
