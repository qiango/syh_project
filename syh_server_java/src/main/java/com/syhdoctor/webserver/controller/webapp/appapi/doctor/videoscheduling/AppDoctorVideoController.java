package com.syhdoctor.webserver.controller.webapp.appapi.doctor.videoscheduling;

import com.syhdoctor.common.utils.EnumUtils.OrderTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.prescription.PrescriptionService;
import com.syhdoctor.webserver.service.video.DoctorVideoService;
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
 * @date 2018/11/22
 */
@Api(description = "/App/DoctorVideo 医生预约视频排班")
@RestController
@RequestMapping("/App/DoctorVideo")
public class AppDoctorVideoController extends BaseController {

    @Autowired
    private DoctorVideoService doctorVideoService;

    @Autowired
    private PrescriptionService prescriptionService;


    @ApiOperation(value = "医生订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", dataType = "String"),})
    @PostMapping("/findOrderDetail")
    public Map<String, Object> findOrderDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        result.put("data", doctorVideoService.findOrderDetail(orderid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "进入视频通话")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderId", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctordevicecode", value = "医生设备码,只能当前设备码能视频通话", required = true, dataType = "String"),
    })
    @PostMapping("/doctorIntoVideo")
    public Map<String, Object> doctorIntoVideo(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        String doctordevicecode = ModelUtil.getStr(params, "doctordevicecode");
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorVideoService.doctorIntoVideo(orderId, doctordevicecode));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "医生排班时间列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
    })
    @PostMapping("/findSchedulingList")
    public Map<String, Object> findSchedulingList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        result.put("data", doctorVideoService.findSchedulingList(doctorid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "医生取消排班")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "visitingstarttime", value = "取消时间", dataType = "String"),
    })
    @PostMapping("/cancelScheduling")
    public Map<String, Object> cancelScheduling(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
//        long visiting_start_time = ModelUtil.getLong(params, "visitingstarttime");
        List<?> visiting_start_time = ModelUtil.getList(params, "visitingstarttime", new ArrayList<>());
        result.put("data", doctorVideoService.cancelScheduling(doctorid, visiting_start_time));
        setOkResult(result, "取消成功");
        return result;
    }

    @ApiOperation(value = "查看诊后小结")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "订单类型", required = true, dataType = "String"),
    })
    @PostMapping("/getOrderGuidance")
    public Map<String, Object> getOrderGuidance(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        if (orderid == 0 || ordertype == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorVideoService.getOrderGuidanceDoctor(orderid, ordertype));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "添加诊后小结")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "ordertype", value = "订单类型", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diagnosis", value = "诊断建议", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diagnosisfordec", value = "处方诊断建议", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diagnosticresults", value = "诊疗结果", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "presno", value = "处方编号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "oftenprescriptionid", value = "常用处方id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "druglist", value = "药品列表", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
    })
    @PostMapping("/addOrderGuidance")
    public Map<String, Object> findGuidance(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int ordertype = ModelUtil.getInt(params, "ordertype");
        String diagnosis = ModelUtil.getStr(params, "diagnosis");
        String diagnosisfordec = ModelUtil.getStr(params, "diagnosisfordec");
        String diagnosticresults = ModelUtil.getStr(params, "diagnosticresults");
        String presNo = ModelUtil.getStr(params, "presno");
        long oftenPrescriptionId = ModelUtil.getLong(params, "oftenprescriptionid");
        List<?> druglist = ModelUtil.getList(params, "druglist", new ArrayList<>());
        Map<String, Object> maps = new HashMap<>();
        if (ordertype == OrderTypeEnum.Answer.getCode()) {
            maps = prescriptionService.getAnswerOrderDoctorId(orderid);
        } else if (ordertype == OrderTypeEnum.Phone.getCode()) {
            maps = prescriptionService.getPhoneOrderDoctorId(orderid);
        } else if (ordertype == OrderTypeEnum.Video.getCode()) {
            maps = prescriptionService.getVideoOrderDoctorId(orderid);
        }
        long doctorid = ModelUtil.getLong(maps, "doctorid");
        if (orderid == 0 || ordertype == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorVideoService.addOrderGuidance(orderid, ordertype, diagnosis, diagnosisfordec, diagnosticresults, presNo, oftenPrescriptionId, druglist, doctorid));
            setOkResult(result, "添加成功");
        }
        return result;
    }


    @ApiOperation(value = "首页查询当天预约")

    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
    })
    @PostMapping("/getSubscribeList")
    public Map<String, Object> getSubscribeList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {

        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        result.put("data", doctorVideoService.getSubscribeList(doctorid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "新增修改家庭成员")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "age", value = "年龄", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "gender", value = "性别", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "isdefault", value = "是否默认", dataType = "String"),

    })
    @PostMapping("/insertFamily")
    public Map<String, Object> insertFamily(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        long id = ModelUtil.getLong(params, "id");
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "phone");
        int age = ModelUtil.getInt(params, "age");
        int gender = ModelUtil.getInt(params, "gender");
        int isdefault = ModelUtil.getInt(params, "isdefault");
        if (StrUtil.isEmpty(name) || age == 0) {
            setErrorResult(result, "请检查参数!");
        }
        result.put("data", doctorVideoService.insertFamily(id, userid, name, age, gender, phone, isdefault, 0));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "删除我的家庭成员")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/deleteFamily")
    public Map<String, Object> deleteFamily(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", doctorVideoService.deleteFamily(id));
        setOkResult(result, "删除成功");
        return result;
    }

    @ApiOperation(value = "查询我的家庭成员")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
    })
    @PostMapping("/findFamilyList")
    public Map<String, Object> findFamilyList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userid = ModelUtil.getLong(params, "userid");
        result.put("data", doctorVideoService.findFamilyList(userid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "视频时间列表")
    @ApiImplicitParams({
    })
    @PostMapping("/videoScheudTime")
    public Map<String, Object> videoScheudTime(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        result.put("data", doctorVideoService.findScheudList(doctorid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "关闭订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", dataType = "String"),
    })
    @PostMapping("/closeOrder")
    public Map<String, Object> closeOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
            return result;
        }
        result.put("data", doctorVideoService.closeOrder(orderid));
        setOkResult(result, "查询成功");
        return result;
    }

}
