package com.syhdoctor.webserver.controller.webadmin.user;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.user.UserManagementService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@Api(description = "/Admin/UserManagement 用户详细信息管理")
@RestController
@RequestMapping("/Admin/UserManagement")
public class AdminUserManagementController extends BaseController {

    @Autowired
    private UserManagementService userManagementService;


    @ApiOperation(value = "用户基础信息")

    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
    })
    @PostMapping("/getUserId")
    public Map<String, Object> getUserId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "userid");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userManagementService.getUserId(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    /*
    账户信息
     */
    @ApiOperation(value = "基础信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
    })
    @PostMapping("/getUserAccountId")
    public Map<String, Object> getUserAccountId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "userid");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userManagementService.getUserAccountId(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "钱包的交易记录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/transactionRecordList")
    public Map<String, Object> transactionRecordList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userManagementService.transactionRecordList(userid, pageIndex, pageSize));
            result.put("total", userManagementService.transactionRecordListCount(userid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "积分记录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/userIntegralList")

    public Map<String, Object> userIntegralList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userManagementService.userIntegralList(userid, pageIndex, pageSize));
            result.put("total", userManagementService.userIntegralListCount(userid));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    /*
    会员信息
     */
    @ApiOperation(value = "会员信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
    })
    @PostMapping("/getUserMember")
    public Map<String, Object> getUserMember(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userManagementService.getUserMember(userid));
            setOkResult(result, "成功");
        }
        return result;
    }


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
            result.put("data", userManagementService.getBasic(userid));
            setOkResult(result, "成功");
        }
        return result;
    }


    @ApiOperation(value = "基本信息修改加载")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
    })
    @PostMapping("/getBasicList")
    public Map<String, Object> getBasicList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
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

    /*
        就诊信息
     */
    @ApiOperation(value = "就诊信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getMedicalInformationList")
    public Map<String, Object> getMedicalInformationList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userManagementService.getMedicalInformationList(userid, pageIndex, pageSize).get("list"));
            result.put("total", userManagementService.getMedicalInformationList(userid, pageIndex, pageSize).get("count"));
            setOkResult(result, "成功");
        }
        return result;
    }

    @ApiOperation(value = "就诊信息详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "问诊类型", dataType = "String"),
    })
    @PostMapping("/getOrderListId")
    public Map<String, Object> getOrderListId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        if (orderid == 0 || ordertype == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userManagementService.getOrderListId(orderid, ordertype));
            setOkResult(result, "成功");
        }
        return result;
    }

    @ApiOperation(value = "患病时长")
    @PostMapping("/getSickTimeList")
    public Map<String, Object> getSickTimeList() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", userManagementService.getSickTimeList());
        setOkResult(result, "成功");
        return result;
    }


    @ApiOperation(value = "修改就诊信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "问诊类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "complaints", value = "主诉", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diagnosis", value = "诊后指导", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "remark", value = "备注", dataType = "String"),
    })
    @PostMapping("/updateOrder")
    public Map<String, Object> updateOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        String complaints = ModelUtil.getStr(params, "complaints");
        String diagnosis = ModelUtil.getStr(params, "diagnosis");
        String remark = ModelUtil.getStr(params, "remark");
        if (orderid == 0 || ordertype == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userManagementService.updateOrder(ordertype, orderid, complaints, diagnosis, remark));
            setOkResult(result, "修改成功");
        }
        return result;
    }

    @ApiOperation(value = "修改疾病信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "问诊类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diseasetime", value = "患病时长", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "gohospital", value = "是否去过医院（0否，1是）", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "issuredis", value = "疾病是否确诊（0否，1是）", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "disdescribe", value = "病情描述", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "picturelist", value = "图片", dataType = "String"),
    })
    @PostMapping("/updateSickOrder")
    public Map<String, Object> updateSickOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        String diseasetime = ModelUtil.getStr(params, "diseasetime");
        int gohospital = ModelUtil.getInt(params, "gohospital");
        int issuredis = ModelUtil.getInt(params, "issuredis");
        String disdescribe = ModelUtil.getStr(params, "disdescribe");
        List<?> picturelist = ModelUtil.getList(params, "picturelist", new ArrayList<>());
        if (orderid == 0 || ordertype == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userManagementService.updateSickOrder(ordertype, orderid, diseasetime, gohospital, issuredis, disdescribe, picturelist));
            setOkResult(result, "修改成功");
        }
        return result;
    }


    @ApiOperation(value = "基本信息修改")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "weight", value = "体重", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "height", value = "高度", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ismarry", value = "是否结婚", dataType = "String"),
    })
    @PostMapping("/updateAccount")
    public Map<String, Object> updateAccount(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        int weight = ModelUtil.getInt(params, "weight");
        int height = ModelUtil.getInt(params, "height");
        int ismarry = ModelUtil.getInt(params, "ismarry");
        if (userid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", userManagementService.updateAccount(userid, weight, height, ismarry));
            setOkResult(result, "成功");
        }
        return result;
    }


    @ApiOperation(value = "基本病例信息修改")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isallergy", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ischronicillness", value = "体重", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "issurgery", value = "高度", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "issurgeryother", value = "是否结婚", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "familyother", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ischronicillnessother", value = "体重", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isallergyother", value = "高度", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isfamilyhistory", value = "是否结婚", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "issmoking", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isdrinking", value = "体重", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isfertility", value = "高度", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pregnancy", value = "是否结婚", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "childbirth", value = "高度", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "abortion", value = "是否结婚", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "mencharage", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "finalmenarche", value = "体重", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ismenopause", value = "高度", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "values", value = "是否结婚", dataType = "String"),
    })
    @PostMapping("/updateUserCase")
    public Map<String, Object> updateUserCase(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
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
            result.put("data", userManagementService.updateUserCase(userid, isallergy, ischronicillness, ischronOther, issurgery, issurgeryOther, isallergyOther, isfamilyhistory, familyOther, issmoking, isdrinking, isfertility, pregnancy, childbirth, abortion, menarche_age, final_menarche, ismenopause, values));
            setOkResult(result, "成功");
        }
        return result;
    }

}



