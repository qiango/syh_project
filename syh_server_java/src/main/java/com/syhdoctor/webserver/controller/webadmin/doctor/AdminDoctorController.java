package com.syhdoctor.webserver.controller.webadmin.doctor;

import com.syhdoctor.common.utils.EnumUtils.DoctorExamineEnum;
import com.syhdoctor.common.utils.EnumUtils.VisitCategoryEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.doctor.DoctorService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "/Admin/Doctor 医生后台管理")
@RestController
@RequestMapping("/Admin/Doctor")
public class AdminDoctorController extends BaseController {

    @Autowired
    private DoctorService doctorService;

    @ApiOperation(value = "坐班医生列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "医生名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = "医生电话", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String"),
    })
    @PostMapping("/getSittingDoctorList")
    public Map<String, Object> getSittingDoctorList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "phone");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", doctorService.getSittingDoctorList(name, phone, pageIndex, pageSize));
        result.put("total", doctorService.getSittingDoctorCount(name, phone));
        setOkResult(result, "设置成功!");
        return result;
    }

    @ApiOperation(value = "设置坐班医生")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorids", value = "医生IDs", required = true, dataType = "List"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登陆人id", dataType = "String"),
    })
    @PostMapping("/setSittingDoctor")
    public Map<String, Object> setSittingDoctor(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        List<?> doctorids = ModelUtil.getList(params, "doctorids", new ArrayList<>());
        long agentid = ModelUtil.getLong(params, "agentid");
        long startTime = ModelUtil.getLong(params, "startTime");
        long endTime = ModelUtil.getLong(params, "endTime");
//        List<?> timeList = ModelUtil.getList(params, "timelist", new ArrayList<>());
        result.put("data", doctorService.setSittingDoctor(doctorids, agentid, startTime, endTime));
        setOkResult(result, "设置成功!");
        return result;
    }

    @ApiOperation(value = "查询坐班医生排班信息")
    @PostMapping("/getSittingDoctorInfoCalendar")
    public Map<String, Object> getSittingDoctorInfoCalendar(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", doctorService.getSittingDoctorInfoCalendar());
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "查询时间坐班医生排班列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "time", value = "时间戳(年月日)", required = true, dataType = "String")
    })
    @PostMapping("/getSittingDoctorListScheduling")
    public Map<String, Object> getDoctorListSchedulings(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long time = ModelUtil.getLong(params, "time");
        result.put("data", doctorService.getDoctorSchedulingInfos(time));
        setOkResult(result, "查询成功!");
        return result;
    }


    @ApiOperation(value = "删除坐班医生")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "医生ID", required = true, dataType = "List"),
    })
    @PostMapping("/delSittingDoctor")
    public Map<String, Object> delSittingDoctor(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.delSittingDoctor(id));
            setOkResult(result, "设置成功!");
        }
        return result;
    }

    @ApiOperation(value = "等待坐班医生列表")
    @PostMapping("/getWaitSittingDoctorList")
    public Map<String, Object> getWaitSittingDoctorList() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", doctorService.getWaitSittingDoctorList());
        setOkResult(result, "查询成功!");
        return result;
    }


    @ApiOperation(value = "设置医生排班")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "timelist", value = "时间", required = true, dataType = "String")
    })
    @PostMapping("/setUpScheduling")
    public Map<String, Object> setUpScheduling(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        List<?> timeList = ModelUtil.getList(params, "timelist", new ArrayList<>());
        result.put("data", doctorService.setUpScheduling(doctorid, timeList));
        setOkResult(result, "设置成功!");
        return result;
    }

    @ApiOperation(value = "等待排班医生列表")
    @PostMapping("/getWaitSchedulingDoctorList")
    public Map<String, Object> getWaitSchedulingDoctorList() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", doctorService.getWaitSchedulingDoctorList());
        setOkResult(result, "查询成功!");
        return result;
    }


    @ApiOperation(value = "查询时间排班列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "time", value = "时间戳(年月日)", required = true, dataType = "String")
    })
    @PostMapping("/getDoctorListScheduling")
    public Map<String, Object> getDoctorListScheduling(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long time = ModelUtil.getLong(params, "time");
        result.put("data", doctorService.getDoctorSchedulingInfo(time));
        setOkResult(result, "审核成功!");
        return result;
    }

    @ApiOperation(value = "审核医生排班")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "examinestate", value = "医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "id", value = "医生ID", required = true, dataType = "String")
    })
    @PostMapping("/reviewUpScheduling")
    public Map<String, Object> reviewUpScheduling(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int examineState = ModelUtil.getInt(params, "examinestate");
        int id = ModelUtil.getInt(params, "id");
        result.put("data", doctorService.examineState(examineState, id));
        setOkResult(result, "审核成功!");
        return result;
    }


    @ApiOperation(value = "查询医生排班信息")
    @PostMapping("/getDoctorInfoCalendar")
    public Map<String, Object> getDoctorInfoCalendar(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", doctorService.getDoctorInfoCalendar());
        setOkResult(result, "查询成功!");
        return result;
    }


    @ApiOperation(value = "查询医生价格信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", required = true, dataType = "String")
    })
    @PostMapping("/getDocMedPriceList")
    public Map<String, Object> getDocMedPriceList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        result.put("data", doctorService.getDocMedPriceList(doctorid));
        result.put("total", 10);
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "查询医生价格详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String")
    })
    @PostMapping("/getDocMedPriceListById")
    public Map<String, Object> getDocMedPriceListById(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int id = ModelUtil.getInt(params, "id");
        result.put("data", doctorService.getDocMedPriceListById(id));
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "新增修改医生价格")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "whetheropen", value = "是否开启图文 1开启 0 未开启", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "price", value = "价格", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "medclassid", value = "咨询类型", required = true, dataType = "String")
    })
    @PostMapping("/addUpdateDocMedPrice")
    public Map<String, Object> addUpdateDocMedPrice(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        int id = ModelUtil.getInt(params, "id");
        int whetheropen = ModelUtil.getInt(params, "whetheropen");
        BigDecimal price = ModelUtil.getDec(params, "price", BigDecimal.ZERO);
        int medclassid = ModelUtil.getInt(params, "medclassid");
        String medclassname = VisitCategoryEnum.getName(medclassid);
        if (price == BigDecimal.ZERO || doctorId == 0) {
            setErrorResult(result, "请检查参数!");
        }
        doctorService.addUpdateDocMedPrice(doctorId, id, whetheropen, price, medclassid, medclassname);
        setOkResult(result, "保存成功!");
        return result;
    }


    /**
     * @param
     * @return
     */
    @ApiOperation(value = "查询医生订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "docname", value = "医生姓名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "username", value = "用户姓名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "dootel", value = "医生手机号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "states", value = "订单状态", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "paystatus", value = "支付状态", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "orderno", value = "订单号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", required = true, dataType = "String"),
    })
    @PostMapping("/getDoctorProblemOrder")
    public Map<String, Object> getDoctorProblemOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String docName = ModelUtil.getStr(params, "docname");
        String userName = ModelUtil.getStr(params, "username");
        String dooTel = ModelUtil.getStr(params, "dootel");
        int states = ModelUtil.getInt(params, "states");
        int paystatus = ModelUtil.getInt(params, "paystatus");
        String orderNo = ModelUtil.getStr(params, "orderno");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", doctorService.getDoctorProblemOrder(docName, userName, dooTel, states, paystatus, orderNo, pageIndex, pageSize));
        result.put("total", doctorService.getDoctorProblemOrderTotal(docName, userName, dooTel, states, paystatus, orderNo));
        setOkResult(result, "查询成功!");
        return result;
    }


    /**
     * @param params
     * @return
     */
    @ApiOperation(value = "审核医生")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生Id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "examine", value = "审核状态0:未认证 1:认证中 2:认证成功 3:认证失败", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登录人ID", required = true, dataType = "String"),
    })
    @PostMapping("/examineDoctor")
    public Map<String, Object> examineDoctor(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        int examine = ModelUtil.getInt(params, "examine");
        long agentId = ModelUtil.getLong(params, "agentid");
        String reason = ModelUtil.getStr(params, "reason");
        int doctype = ModelUtil.getInt(params, "doctype");
        if (doctorId == 0) {
            setErrorResult(result, "请检查参数!");
        } else {
            boolean flag = doctorService.examineDoctor(doctorId, examine, agentId, reason, doctype);
            if (flag) {
                result.put("data", true);
                setOkResult(result, "成功!");
            } else {
                result.put("data", false);
                setErrorResult(result, "失败!");
            }
        }
        return result;
    }

    /**
     * 删除医生
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "删除医生")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生Id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登录人ID", required = true, dataType = "String"),
    })
    @PostMapping("/deleteDoctor")
    public Map<String, Object> deleteDoctor(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        long agentId = ModelUtil.getLong(params, "agentid");
        if (doctorId == 0) {
            setErrorResult(result, "请检查参数!");
        } else {
            result.put("data", doctorService.deleteDoctor(doctorId, agentId));
            setOkResult(result, "删除成功!");
        }
        return result;
    }

    /**
     * 删除审方医生
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "删除审方医生")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生Id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登录人ID", required = true, dataType = "String"),
    })
    @PostMapping("/deleteExamineDoctor")
    public Map<String, Object> deleteExamineDoctor(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        long agentId = ModelUtil.getLong(params, "agentid");
        int doctype = ModelUtil.getInt(params, "doctype");
        int examine = ModelUtil.getInt(params, "examine");
        if (doctorId == 0) {
            setErrorResult(result, "请检查参数!");
        } else if (doctorService.trialPartyOk() == 1 && doctype == 1 && examine == 7) {
            setErrorResult(result, "最后一位审方医师，不可删除");
        } else if (doctorService.examineOk() == 1 && doctype == 5 && examine == 7) {
            setErrorResult(result, "最后一位审核医师，不可删除");
        } else {
            result.put("data", doctorService.deleteExamineDoctor(doctorId, agentId));
            setOkResult(result, "删除成功!");
        }
        return result;
    }


    /**
     * 查询科室
     *
     * @return
     */
    @ApiOperation(value = "查询科室")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "value", value = "科室名称", dataType = "String")
    })
    @PostMapping("/getDepartment")
    public Map<String, Object> getDepartment(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String value = ModelUtil.getStr(params, "value");
        result.put("data", doctorService.getLastDepartment(value));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "查询梯形科室")
    @ApiImplicitParams({
    })
    @PostMapping("/getDeparent")
    public Map<String, Object> getDeparent(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long partId = ModelUtil.getLong(params, "id");
        result.put("data", doctorService.getDeparents(partId));
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * 查询职称
     *
     * @return
     */
    @ApiOperation(value = "查询职称")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "value", value = "职称名称", dataType = "String")
    })
    @PostMapping("/getDoctorTitle")
    public Map<String, Object> getDoctorTitle(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String value = ModelUtil.getStr(params, "value");
        result.put("data", doctorService.getDoctorTitle(value));
        setOkResult(result, "查询成功");
        return result;
    }


    /**
     * 查询医生详情
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "加载医生信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生Id", required = true, dataType = "String"),
    })
    @PostMapping("/getDoctorById")
    public Map<String, Object> getDoctorById(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        if (doctorId == 0) {
            setErrorResult(result, "请检查参数!");
        } else {
            result.put("data", doctorService.getDoctorById(doctorId));
            setOkResult(result, "查询成功!");
        }
//        result.put("picdomain", ConfigModel.DOCTORPICDOMAIN);
        return result;
    }

    /**
     * 查询专家医生认证详情
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "加载医生认证信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生Id", required = true, dataType = "String"),
    })
    @PostMapping("/getDoctorexpertId")
    public Map<String, Object> getDoctorexpertId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        if (doctorId == 0) {
            setErrorResult(result, "请检查参数!");
        } else {
            result.put("data", doctorService.getDoctorexpertId(doctorId));
            setOkResult(result, "查询成功!");
        }
        return result;
    }

    /**
     * 查询医生详情
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "加载医生信息Code")
    @ApiImplicitParams({
    })
    @PostMapping("/getDoctorCode")
    public Map<String, Object> getDoctorCode(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", doctorService.getDoctorCode());
        setOkResult(result, "查询成功!");
        return result;
    }

    /**
     * 查询医生列表
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "查询医生列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorname", value = "医生姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "dootel", value = "医生手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "workinstname", value = "医院名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleid", value = "职称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorstart", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorend", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "examine", value = "审核状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "graphicstatus", value = "图文状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String"),
    })
    @PostMapping("/getDoctorList")
    public Map<String, Object> getDoctorList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String docName = ModelUtil.getStr(params, "doctorname");
        String dooTel = ModelUtil.getStr(params, "dootel");
        String workInstName = ModelUtil.getStr(params, "workinstname");
        String titleId = ModelUtil.getStr(params, "titleid");
        String departmentId = ModelUtil.getStr(params, "departmentid");
        long doctorStart = ModelUtil.getLong(params, "doctorstart");
        long doctorEnd = ModelUtil.getLong(params, "doctorend");
        int examine = ModelUtil.getInt(params, "auditstate", -1);
        int graphicStatus = ModelUtil.getInt(params, "graphicstatus", -1);
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", doctorService.getDoctorList(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus, pageIndex, pageSize));
        result.put("total", doctorService.getdoctorTotal(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus));
        setOkResult(result, "查询成功!");
        return result;
    }

    /**
     * 医生认证详情添加和修改
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "医生添加和修改")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "docname", value = "医生名字", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "indoccode", value = "医生CODE", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "tempDoctorId", value = "新增的医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docphotourl", value = "医生头像", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctype", value = "医师类型", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "dootel", value = "医生手机号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleid", value = "职称名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "workinstname", value = "医院名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "idcard", value = "身份证号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pracno", value = "医师执业证号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pracrecdate", value = "执业证取得时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "certno", value = "医师资格证号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "certrecdate", value = "资格证取得时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleno", value = "医师职称证号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titlerecdate", value = "职称证取得时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "practype", value = "医师执业类别", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "qualifyornot", value = "是|否合格", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "professional", value = "医师擅长专业", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "signtime", value = "签约时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "signlife", value = "签约年限", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "employfileurl", value = "互联网医院聘任合同", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "creditlevel", value = "信用评级", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "occulevel", value = "职业评级", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "digitalsignurl", value = "医师数字签名留样", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "docpenaltypoints", value = "医师评分", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "ycrecordflag", value = "银川是否已备案 1 备案 否 0", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "hosconfirmflag", value = "医院是否已确认", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "ycpresrecordflag", value = "银川处方开具权是否备案", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pracscope", value = "医师执业范围", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pracscopeapproval", value = "审批局规定的医师执业范围", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "agreeterms", value = "是否同意以上条款", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docmultisiteddatestart", value = "医师多点执业起始时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docmultisiteddateend", value = "医师多点执业终止时间", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "hosopinion", value = "申请拟执业医疗机构意见", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hosdigitalsign", value = "申请拟执业医疗机构-电子章", required = true, dataType = "String"),//
//            @ApiImplicitParam(paramType = "query", name = "hosopiniondate", value = "申请拟执业医疗机构意见时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docmultisiteddatepromise", value = "医师申请多点执业承诺时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登陆人", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "introduction", value = "简介", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "gender", value = "性别", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "cardlist", value = "身份证文件列表数组", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "certlist", value = "资格证文件数组", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "certpraclist", value = "执业证文件数组", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titlecertlist", value = "职称证文件数组", required = true, dataType = "String")
    })
    @PostMapping("/addUpdateDoctorInfo")
    public Map<String, Object> addUpdateDoctorInfo(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        String docName = ModelUtil.getStr(params, "docname");
        String docPhotoUrl = ModelUtil.getStr(params, "docphotourl");
        int docType = ModelUtil.getInt(params, "doctype");
        int titleId = ModelUtil.getInt(params, "titleid");
        long hospitalid = ModelUtil.getLong(params, "hospitalid");
        String dooTel = ModelUtil.getStr(params, "dootel");
        String idCard = ModelUtil.getStr(params, "idcard");
        String pracNo = ModelUtil.getStr(params, "pracno");
        long pracRecDate = ModelUtil.getLong(params, "pracrecdate");
        String certNo = ModelUtil.getStr(params, "certno");
        long certRecDate = ModelUtil.getLong(params, "certrecdate");
        String titleNo = ModelUtil.getStr(params, "titleno");
        long titleRecDate = ModelUtil.getLong(params, "titlerecdate");
        String pracType = ModelUtil.getStr(params, "practype");
        String professional = ModelUtil.getStr(params, "professional");
        long signTime = ModelUtil.getLong(params, "signtime");
        String signLife = ModelUtil.getStr(params, "signlife");
        String employFileUrl = ModelUtil.getStr(params, "employfileurl");
        String digitalSignUrl = ModelUtil.getStr(params, "digitalsignurl");
        String pracScope = ModelUtil.getStr(params, "pracscope");
        String pracScopeApproval = ModelUtil.getStr(params, "pracscopeapproval");
        long docMultiSitedDateStart = ModelUtil.getLong(params, "docmultisiteddatestart");
        long docMultiSitedDateEnd = ModelUtil.getLong(params, "docmultisiteddateend");
//        String hosDigitalSignUrl = ModelUtil.getStr(params, "hosdigitalsign");
        long docMultiSitedDatePromise = ModelUtil.getLong(params, "docmultisiteddatepromise");
        long agentid = ModelUtil.getLong(params, "agentid");
        String introduction = ModelUtil.getStr(params, "introduction");
        int gender = ModelUtil.getInt(params, "gender");
        int departmentId = ModelUtil.getInt(params, "departmentid");
        long doctorId = ModelUtil.getLong(params, "doctorid");
        int examine = ModelUtil.getInt(params, "examine");

        List<?> cardList = ModelUtil.getList(params, "cardlist", new ArrayList<>());//身份证
        List<?> certList = ModelUtil.getList(params, "certlist", new ArrayList<>());//资格证文件
        List<?> certPracList = ModelUtil.getList(params, "certpraclist", new ArrayList<>());//执业证文件
        List<?> titleCertList = ModelUtil.getList(params, "titlecertlist", new ArrayList<>());//职称证文件

        List<?> multiSitedLicRecordList = ModelUtil.getList(params, "multisitedlicrecordlist", new ArrayList<>());//多点执业文件

        String inDocCode = ModelUtil.getStr(params, "indoccode", "");
        long tempDoctorId = ModelUtil.getLong(params, "tempDoctorId", 0);


        if (StrUtil.isEmpty(certNo, titleNo, pracType
                , employFileUrl, digitalSignUrl
                , pracScope, pracScopeApproval//, inDocCode
        ) || cardList.size() == 0 || certList.size() == 0 || certPracList.size() == 0 || titleCertList.size() == 0) {
            setErrorResult(result, "请检查参数!");
        } else if (tempDoctorId == 0 && doctorId == 0 && hospitalid == 0 && docType == 0) {
            setErrorResult(result, "请检查参数!");
        } else if (cardList.size() < 2) {
            setErrorResult(result, "身份证请上传正反面");
        } else if (certList.size() < 2) {
            setErrorResult(result, "资格证文件不足");
        } else {
            doctorService.addUpdateDoctorInfo(examine, docName, docPhotoUrl, docType, titleId, hospitalid, dooTel, idCard, pracNo, pracRecDate, certNo, certRecDate, titleNo,
                    titleRecDate, pracType, professional,
                    signTime, signLife, employFileUrl,
                    digitalSignUrl,
                    pracScope, pracScopeApproval,
                    docMultiSitedDateStart, docMultiSitedDateEnd,
                    docMultiSitedDatePromise, agentid,
                    introduction, gender, departmentId, doctorId, inDocCode, tempDoctorId, cardList, certList, certPracList, titleCertList, multiSitedLicRecordList);
            setOkResult(result, "保存成功!");
        }
        return result;
    }


    @ApiOperation(value = "坐班价格列表")
    @ApiImplicitParams({
    })
    @PostMapping("/getEmergencyClinicPriceList")
    public Map<String, Object> getEmergencyClinicPriceList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", doctorService.getEmergencyClinicPriceList());
        setOkResult(result, "设置成功!");
        return result;
    }

    @ApiOperation(value = "坐班价格修改")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "医生IDs", required = true, dataType = "List"),
            @ApiImplicitParam(paramType = "query", name = "emergencyprice", value = "急诊价格", required = true, dataType = "List"),
            @ApiImplicitParam(paramType = "query", name = "outpatientprice", value = "门诊价格", required = true, dataType = "List"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登陆人id", dataType = "String"),
    })
    @PostMapping("/updateEmergencyClinicPrice")
    public Map<String, Object> updateEmergencyClinicPrice(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long agentid = ModelUtil.getLong(params, "agentid");
        long id = ModelUtil.getLong(params, "id");
        BigDecimal emergencyprice = ModelUtil.getDec(params, "emergencyprice", BigDecimal.ZERO);
        BigDecimal outpatientprice = ModelUtil.getDec(params, "outpatientprice", BigDecimal.ZERO);
        result.put("data", doctorService.updateEmergencyClinicPrice(id, emergencyprice, outpatientprice, agentid));
        setOkResult(result, "设置成功!");
        return result;
    }

    @ApiOperation(value = "坐班价格查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "序号", required = true, dataType = "String"),
    })
    @PostMapping("/findPriceByid")
    public Map<String, Object> findPriceByid(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", doctorService.findById());
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "原价")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "序号", required = true, dataType = "String"),
    })
    @PostMapping("/discountPrice")
    public Map<String, Object> discountPrice(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", doctorService.discountPrice());
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "修改金额并且刷新")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "price", value = "金额", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "type", value = "类型：图文 电话", required = true, dataType = "String"),
    })
    @PostMapping("/updatePrice")
    public Map<String, Object> updatePrice(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        BigDecimal price = ModelUtil.getDec(params, "price", BigDecimal.ZERO);
        int type = ModelUtil.getInt(params, "type");
        if (type == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.updatePrice(price, type));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    @ApiOperation(value = "诊疗医师列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorname", value = "医生姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "dootel", value = "医生手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "workinstname", value = "医院名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleid", value = "职称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorstart", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorend", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "examine", value = "审核状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "graphicstatus", value = "图文状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String"),
    })
    @PostMapping("/getClinicsList")
    public Map<String, Object> getClinicsList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String docName = ModelUtil.getStr(params, "doctorname");
        String dooTel = ModelUtil.getStr(params, "dootel");
        String workInstName = ModelUtil.getStr(params, "workinstname");
        String titleId = ModelUtil.getStr(params, "academictitle");
        String departmentId = ModelUtil.getStr(params, "department");
        long doctorStart = ModelUtil.getLong(params, "doctorstart");
        long doctorEnd = ModelUtil.getLong(params, "doctorend");
        int examine = ModelUtil.getInt(params, "auditstate");
        int graphicStatus = ModelUtil.getInt(params, "imgstate", -1);
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", doctorService.getClinicsList(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus, pageIndex, pageSize));
        result.put("total", doctorService.getClinicsListCount(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus));
        setOkResult(result, "查询成功!");
//        result.put("picdomain", ConfigModel.DOCTORPICDOMAIN);
        return result;
    }

    @ApiOperation(value = "诊疗医师详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
    })
    @PostMapping("/getClinicsById")
    public Map<String, Object> getClinicsById(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.getClinicsById(doctorid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "修改审核状态,失败则添加失败原因")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "examine", value = "审核状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "reason", value = "失败原因", dataType = "String"),
    })
    @PostMapping("/updataExamine")
    public Map<String, Object> updataExamine(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        int examine = ModelUtil.getInt(params, "examine");
        String reason = ModelUtil.getStr(params, "reason");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            if (examine == 3 && StrUtil.isEmpty(reason)) {
                setErrorResult(result, "失败原因不能为空");
            } else if (examine != 3 && examine != 2) {
                setErrorResult(result, "请选择类型");
            } else {
                result.put("data", doctorService.updataExamine(doctorid, examine, reason));
                setOkResult(result, "操作成功");
            }
        }
        return result;
    }

    @ApiOperation(value = "修改顾问认证状态,失败则添加失败原因")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "examine", value = "认证状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "reason", value = "失败原因", dataType = "String"),
    })
    @PostMapping("/updataAdviserExamine")
    public Map<String, Object> updataAdviserExamine(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        int examine = ModelUtil.getInt(params, "examine");
        String reason = ModelUtil.getStr(params, "reason");
        if (doctorid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            if (examine == DoctorExamineEnum.failCertified.getCode() && StrUtil.isEmpty(reason)) {
                setErrorResult(result, "失败原因不能为空");
            } else if (examine != DoctorExamineEnum.failCertified.getCode() && examine != DoctorExamineEnum.auditSuccess.getCode()) {
                setErrorResult(result, "请选择类型");
            } else {
                result.put("data", doctorService.updataExamine(doctorid, examine, reason));
                setOkResult(result, "操作成功");
            }
        }
        return result;
    }


    @ApiOperation(value = "专家医师列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorname", value = "医生姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "dootel", value = "医生手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "workinstname", value = "医院名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleid", value = "职称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorstart", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorend", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "examine", value = "审核状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "graphicstatus", value = "图文状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String"),
    })
    @PostMapping("/getExpertPhysicianList")
    public Map<String, Object> getExpertPhysicianList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String docName = ModelUtil.getStr(params, "doctorname");
        String dooTel = ModelUtil.getStr(params, "dootel");
        String workInstName = ModelUtil.getStr(params, "workinstname");
        String titleId = ModelUtil.getStr(params, "academictitle");
        String departmentId = ModelUtil.getStr(params, "department");
        long doctorStart = ModelUtil.getLong(params, "doctorstart");
        long doctorEnd = ModelUtil.getLong(params, "doctorend");
        int examine = ModelUtil.getInt(params, "auditstate");
        int graphicStatus = ModelUtil.getInt(params, "imgstate", -1);
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", doctorService.getExpertPhysicianList(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus, pageIndex, pageSize));
        result.put("total", doctorService.getExpertPhysicianListCount(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus));
        setOkResult(result, "查询成功!");
//        result.put("picdomain", ConfigModel.DOCTORPICDOMAIN);
        return result;
    }


    @ApiOperation(value = "新增或修改诊疗医师")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "docname", value = "医生名字", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "tempDoctorId", value = "新增的医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docphotourl", value = "医生头像", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "dootel", value = "医生手机号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleid", value = "职称名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "idcard", value = "身份证号", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "pracno", value = "医师执业证号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "professional", value = "医师擅长专业", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "introduction", value = "简介", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "gender", value = "性别", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitalid", value = "医院ID", required = true, dataType = "String"),
    })
    @PostMapping("/addUpdateClinics")
    public Map<String, Object> addUpdateClinics(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String docName = ModelUtil.getStr(params, "docname");
        String docPhotoUrl = ModelUtil.getStr(params, "docphotourl");
        int titleId = ModelUtil.getInt(params, "titleid");
        long hospitalid = ModelUtil.getLong(params, "hospitalid");
        String dooTel = ModelUtil.getStr(params, "dootel");
        String idCard = ModelUtil.getStr(params, "idcard");
//        String pracNo = ModelUtil.getStr(params, "pracno");
        String professional = ModelUtil.getStr(params, "professional");
        String introduction = ModelUtil.getStr(params, "introduction");
        int gender = ModelUtil.getInt(params, "gender");
        int departmentId = ModelUtil.getInt(params, "departmentid");
        long doctorId = ModelUtil.getLong(params, "doctorid");
        //long tempDoctorId = ModelUtil.getLong(params, "tempDoctorId", 0);
        String invitationcode = ModelUtil.getStr(params, "invitationcode");

        if (StrUtil.isEmpty(docName) || StrUtil.isEmpty(idCard) || gender == 0) {
            setErrorResult(result, "参数错误");
        } else {
            doctorService.addUpdateClinics(invitationcode, docName, docPhotoUrl, titleId, hospitalid, dooTel, idCard, professional, introduction, gender, departmentId, doctorId);
            setOkResult(result, "操作成功");
        }
        return result;
    }

    @ApiOperation(value = "邀请人下拉框")
    @PostMapping("/salespersonAll")
    public Map<String, Object> salespersonAll() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", doctorService.salespersonAll());
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "新增或修改专家医师基本信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "docname", value = "医生名字", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "tempDoctorId", value = "新增的医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docphotourl", value = "医生头像", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "dootel", value = "医生手机号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleid", value = "职称名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "idcard", value = "身份证号", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "pracno", value = "医师执业证号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "professional", value = "医师擅长专业", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "introduction", value = "简介", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "gender", value = "性别", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitalid", value = "医院ID", required = true, dataType = "String"),
    })
    @PostMapping("/addUpdateExpertPhysician")
    public Map<String, Object> addUpdateExpertPhysician(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String docName = ModelUtil.getStr(params, "docname");
        String docPhotoUrl = ModelUtil.getStr(params, "docphotourl");
        int titleId = ModelUtil.getInt(params, "titleid");
        long hospitalid = ModelUtil.getLong(params, "hospitalid");
        String dooTel = ModelUtil.getStr(params, "dootel");
        String idCard = ModelUtil.getStr(params, "idcard");
//        String pracNo = ModelUtil.getStr(params, "pracno");
        String professional = ModelUtil.getStr(params, "professional");
        String introduction = ModelUtil.getStr(params, "introduction");
        int gender = ModelUtil.getInt(params, "gender");
        int departmentId = ModelUtil.getInt(params, "departmentid");
        long doctorId = ModelUtil.getLong(params, "doctorid");
        //long tempDoctorId = ModelUtil.getLong(params, "tempDoctorId", 0);
        String invitationcode = ModelUtil.getStr(params, "invitationcode");

        if (StrUtil.isEmpty(docName) || StrUtil.isEmpty(idCard) || gender == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.addUpdateExpertPhysician(invitationcode, docName, docPhotoUrl, titleId, hospitalid, dooTel, idCard, professional, introduction, gender, departmentId, doctorId));
            setOkResult(result, "操作成功");
        }
        return result;
    }

    @ApiOperation(value = "新增或修改审方医师基本信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "docname", value = "医生名字", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docphotourl", value = "医生头像", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "dootel", value = "医生手机号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleid", value = "职称名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "idcard", value = "身份证号", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "pracno", value = "医师执业证号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "professional", value = "医师擅长专业", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "introduction", value = "简介", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "gender", value = "性别", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitalid", value = "医院ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctype", value = "医师类型", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "examine", value = "医师状态", required = true, dataType = "String"),
    })
    @PostMapping("/updateAddTrialParty")
    public Map<String, Object> updateAddTrialParty(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String docName = ModelUtil.getStr(params, "docname");
        String docPhotoUrl = ModelUtil.getStr(params, "docphotourl");
        int titleId = ModelUtil.getInt(params, "titleid");
        long hospitalid = ModelUtil.getLong(params, "hospitalid");
        String dooTel = ModelUtil.getStr(params, "dootel");
        String idCard = ModelUtil.getStr(params, "idcard");
//        String pracNo = ModelUtil.getStr(params, "pracno");
        String professional = ModelUtil.getStr(params, "professional");
        String introduction = ModelUtil.getStr(params, "introduction");
        int gender = ModelUtil.getInt(params, "gender");
        int departmentId = ModelUtil.getInt(params, "departmentid");
        long doctorId = ModelUtil.getLong(params, "doctorid");
        int doctype = ModelUtil.getInt(params, "doctype");
        int examine = ModelUtil.getInt(params, "examine");
        //long tempDoctorId = ModelUtil.getLong(params, "tempDoctorId", 0);
        String invitationcode = ModelUtil.getStr(params, "invitationcode");

        if (StrUtil.isEmpty(docName) || StrUtil.isEmpty(idCard) || gender == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorService.updateAddTrialParty(invitationcode, examine, doctype, docName, docPhotoUrl, titleId, hospitalid, dooTel, idCard, professional, introduction, gender, departmentId, doctorId));
            setOkResult(result, "操作成功");
        }
        return result;
    }

    @ApiOperation(value = "顾问列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorname", value = "医生姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "dootel", value = "医生手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "workinstname", value = "医院名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleid", value = "职称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorstart", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorend", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "examine", value = "审核状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "graphicstatus", value = "图文状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String"),
    })
    @PostMapping("/getAdviserList")
    public Map<String, Object> getAdviserList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String docName = ModelUtil.getStr(params, "doctorname");
        String dooTel = ModelUtil.getStr(params, "dootel");
        String workInstName = ModelUtil.getStr(params, "workinstname");
        String titleId = ModelUtil.getStr(params, "titleId");
        String departmentId = ModelUtil.getStr(params, "department");
        long doctorStart = ModelUtil.getLong(params, "doctorstart");
        long doctorEnd = ModelUtil.getLong(params, "doctorend");
        int examine = ModelUtil.getInt(params, "auditstate");
        int graphicStatus = ModelUtil.getInt(params, "imgstate", -1);
        int pageIndex = ModelUtil.getInt(params, "pageIndex", 1);
        int pageSize = ModelUtil.getInt(params, "pageSize", 20);
        result.put("data", doctorService.getAdviserList(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus, pageIndex, pageSize));
        result.put("total", doctorService.getAdviserListCount(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "新增或修改顾问")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "docname", value = "医生名字", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "tempDoctorId", value = "新增的医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docphotourl", value = "医生头像", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "dootel", value = "医生手机号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleid", value = "职称名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "idcard", value = "身份证号", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "pracno", value = "医师执业证号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "professional", value = "医师擅长专业", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "introduction", value = "简介", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "gender", value = "性别", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitalid", value = "医院ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "expertid", value = "顾问绑定的专家ID", required = true, dataType = "String"),
    })
    @PostMapping("/addUpdateAdviser")
    public Map<String, Object> addUpdateAdviser(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String docName = ModelUtil.getStr(params, "docname");
        String docPhotoUrl = ModelUtil.getStr(params, "docphotourl");
        int titleId = ModelUtil.getInt(params, "titleid");
        long hospitalid = ModelUtil.getLong(params, "hospitalid");
        String dooTel = ModelUtil.getStr(params, "dootel");
        String idCard = ModelUtil.getStr(params, "idcard");
//        String pracNo = ModelUtil.getStr(params, "pracno");
        String professional = ModelUtil.getStr(params, "professional");
        String introduction = ModelUtil.getStr(params, "introduction");
        int gender = ModelUtil.getInt(params, "gender");
        int departmentId = ModelUtil.getInt(params, "departmentid");
        long doctorId = ModelUtil.getLong(params, "doctorid");
        long expertid = ModelUtil.getLong(params, "expertid");
        int examine = ModelUtil.getInt(params, "examine");
        String invitationcode = ModelUtil.getStr(params, "invitationcode");

        if (StrUtil.isEmpty(docName) || StrUtil.isEmpty(idCard) || gender == 0) {
            setErrorResult(result, "参数错误");
        } else if (expertid == 0) {
            setErrorResult(result, "请绑定专家");
        } else {
            doctorService.addUpdateAdviser(invitationcode, examine, docName, docPhotoUrl, titleId, hospitalid, dooTel, idCard, professional, introduction, gender, departmentId, doctorId, expertid);
            setOkResult(result, "操作成功");
        }
        return result;
    }


    @ApiOperation(value = "审方医师列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorname", value = "医生姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "dootel", value = "医生手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "workinstname", value = "医院名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleid", value = "职称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorstart", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorend", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "examine", value = "审核状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "graphicstatus", value = "图文状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String"),
    })
    @PostMapping("/getTrialPartyList")
    public Map<String, Object> getTrialPartyList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String docName = ModelUtil.getStr(params, "doctorname");
        String dooTel = ModelUtil.getStr(params, "dootel");
        String workInstName = ModelUtil.getStr(params, "workinstname");
        String titleId = ModelUtil.getStr(params, "academictitle");
        String departmentId = ModelUtil.getStr(params, "department");
        long doctorStart = ModelUtil.getLong(params, "doctorstart");
        long doctorEnd = ModelUtil.getLong(params, "doctorend");
        int examine = ModelUtil.getInt(params, "auditstate");
        int graphicStatus = ModelUtil.getInt(params, "imgstate", -1);
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", doctorService.getTrialPartyList(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus, pageIndex, pageSize));
        result.put("total", doctorService.getTrialPartyListCount(docName, dooTel, workInstName, titleId, departmentId, doctorStart, doctorEnd, examine, graphicStatus));
        setOkResult(result, "查询成功!");
//        result.put("picdomain", ConfigModel.DOCTORPICDOMAIN);
        return result;
    }


    /**
     * 审方医生添加和修改
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "审方医生添加和修改")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "docname", value = "医生名字", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "indoccode", value = "医生CODE", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "tempDoctorId", value = "新增的医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docphotourl", value = "医生头像", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctype", value = "医师类型", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "dootel", value = "医生手机号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleid", value = "职称名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "workinstname", value = "医院名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "idcard", value = "身份证号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pracno", value = "医师执业证号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pracrecdate", value = "执业证取得时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "certno", value = "医师资格证号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "certrecdate", value = "资格证取得时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titleno", value = "医师职称证号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titlerecdate", value = "职称证取得时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "practype", value = "医师执业类别", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "qualifyornot", value = "是|否合格", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "professional", value = "医师擅长专业", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "signtime", value = "签约时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "signlife", value = "签约年限", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "employfileurl", value = "互联网医院聘任合同", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "creditlevel", value = "信用评级", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "occulevel", value = "职业评级", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "digitalsignurl", value = "医师数字签名留样", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "docpenaltypoints", value = "医师评分", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "ycrecordflag", value = "银川是否已备案 1 备案 否 0", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "hosconfirmflag", value = "医院是否已确认", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "ycpresrecordflag", value = "银川处方开具权是否备案", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pracscope", value = "医师执业范围", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pracscopeapproval", value = "审批局规定的医师执业范围", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "agreeterms", value = "是否同意以上条款", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docmultisiteddatestart", value = "医师多点执业起始时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docmultisiteddateend", value = "医师多点执业终止时间", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "hosopinion", value = "申请拟执业医疗机构意见", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hosdigitalsign", value = "申请拟执业医疗机构-电子章", required = true, dataType = "String"),//
//            @ApiImplicitParam(paramType = "query", name = "hosopiniondate", value = "申请拟执业医疗机构意见时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docmultisiteddatepromise", value = "医师申请多点执业承诺时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登陆人", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "introduction", value = "简介", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "gender", value = "性别", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentid", value = "科室ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "cardlist", value = "身份证文件列表数组", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "certlist", value = "资格证文件数组", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "certpraclist", value = "执业证文件数组", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titlecertlist", value = "职称证文件数组", required = true, dataType = "String")
    })
    @PostMapping("/addUpdateTrialParty")
    public Map<String, Object> addUpdateTrialParty(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        String docName = ModelUtil.getStr(params, "docname");
        String docPhotoUrl = ModelUtil.getStr(params, "docphotourl");
        int docType = ModelUtil.getInt(params, "doctype");
        int titleId = ModelUtil.getInt(params, "titleid");
        long hospitalid = ModelUtil.getLong(params, "hospitalid");
        String dooTel = ModelUtil.getStr(params, "dootel");
        String idCard = ModelUtil.getStr(params, "idcard");
        String pracNo = ModelUtil.getStr(params, "pracno");
        long pracRecDate = ModelUtil.getLong(params, "pracrecdate");
        String certNo = ModelUtil.getStr(params, "certno");
        long certRecDate = ModelUtil.getLong(params, "certrecdate");
        String titleNo = ModelUtil.getStr(params, "titleno");
        long titleRecDate = ModelUtil.getLong(params, "titlerecdate");
        String pracType = ModelUtil.getStr(params, "practype");
//        String qualifyOrNot = ModelUtil.getStr(params, "qualifyornot");
        String professional = ModelUtil.getStr(params, "professional");
        long signTime = ModelUtil.getLong(params, "signtime");
        String signLife = ModelUtil.getStr(params, "signlife");
        String employFileUrl = ModelUtil.getStr(params, "employfileurl");
//        String creditLevel = ModelUtil.getStr(params, "creditlevel");
//        String occuLevel = ModelUtil.getStr(params, "occulevel");
        String digitalSignUrl = ModelUtil.getStr(params, "digitalsignurl");
//        String docPenaltyPoints = ModelUtil.getStr(params, "docpenaltypoints");
//        int ycRecordFlag = ModelUtil.getInt(params, "ycrecordflag");
//        String hosConfirmFlag = ModelUtil.getStr(params, "hosconfirmflag");
//        int ycPresRecordFlag = ModelUtil.getInt(params, "ycpresrecordflag");
        String pracScope = ModelUtil.getStr(params, "pracscope");
        String pracScopeApproval = ModelUtil.getStr(params, "pracscopeapproval");
//        int agreeTerms = ModelUtil.getInt(params, "agreeterms");
        long docMultiSitedDateStart = ModelUtil.getLong(params, "docmultisiteddatestart");
        long docMultiSitedDateEnd = ModelUtil.getLong(params, "docmultisiteddateend");
//        String hosOpinion = ModelUtil.getStr(params, "hosopinion");
//        String hosDigitalSignUrl = ModelUtil.getStr(params, "hosdigitalsign");
//        long hosOpinionDate = ModelUtil.getLong(params, "hosopiniondate");
        long docMultiSitedDatePromise = ModelUtil.getLong(params, "docmultisiteddatepromise");
        long agentid = ModelUtil.getLong(params, "agentid");
        String introduction = ModelUtil.getStr(params, "introduction");
        int gender = ModelUtil.getInt(params, "gender");
        int examine = ModelUtil.getInt(params, "examine");
        int departmentId = ModelUtil.getInt(params, "departmentid");
        long doctorId = ModelUtil.getLong(params, "doctorid");
        List<?> cardList = ModelUtil.getList(params, "cardlist", new ArrayList<>());//身份证
        List<?> certList = ModelUtil.getList(params, "certlist", new ArrayList<>());//资格证文件
        List<?> certPracList = ModelUtil.getList(params, "certpraclist", new ArrayList<>());//执业证文件
        List<?> titleCertList = ModelUtil.getList(params, "titlecertlist", new ArrayList<>());//职称证文件

        List<?> multiSitedLicRecordList = ModelUtil.getList(params, "multisitedlicrecordlist", new ArrayList<>());//多点执业文件

        String inDocCode = ModelUtil.getStr(params, "indoccode", "");
        long tempDoctorId = ModelUtil.getLong(params, "tempDoctorId", 0);

        if (StrUtil.isEmpty(docName, docPhotoUrl,
                dooTel, idCard, pracNo, certNo, titleNo, pracType
                , professional, employFileUrl, digitalSignUrl
                , pracScope, pracScopeApproval, introduction, inDocCode
        ) || cardList.size() == 0 || certList.size() == 0 || certPracList.size() == 0 || titleCertList.size() == 0) {
            setErrorResult(result, "请检查参数!");
        } else if (tempDoctorId == 0 && doctorId == 0 && hospitalid == 0 && docType == 0) {
            setErrorResult(result, "请检查参数!");
        } else {
            doctorService.addUpdateTrialParty(examine, docName, docPhotoUrl, docType, titleId, hospitalid, dooTel, idCard, pracNo, pracRecDate, certNo, certRecDate, titleNo,
                    titleRecDate, pracType, professional,
                    signTime, signLife, employFileUrl,
                    digitalSignUrl, pracScope, pracScopeApproval,
                    docMultiSitedDateStart, docMultiSitedDateEnd,
                    docMultiSitedDatePromise, agentid,
                    introduction, gender, departmentId, doctorId, inDocCode, tempDoctorId, cardList, certList, certPracList, titleCertList, multiSitedLicRecordList);
            setOkResult(result, "保存成功!");
        }
        return result;
    }


    @ApiOperation(value = "顾问详情的医生下拉框（专家认证成功）")
    @PostMapping("/doctorSelect")
    public Map<String, Object> doctorSelect(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String phoneName = ModelUtil.getStr(params, "phoneName");
        result.put("data", doctorService.doctorSelect(phoneName));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "医生推荐价格列表")
    @PostMapping("/doctorRecommendPriceList")
    public Map<String, Object> doctorRecommendPriceList() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", doctorService.doctorRecommendPriceList());
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "新增推荐医生价格")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "answer", value = "图文", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = "电话", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "video", value = "视频", required = true, dataType = "String"),
    })
    @PostMapping("/insertDoctorRecommendPrice")
    public Map<String, Object> insertDoctorRecommendPrice(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        List<?> answerlist = ModelUtil.getList(params, "answer", new ArrayList<>());
        List<?> phonelist = ModelUtil.getList(params, "phone", new ArrayList<>());
        List<?> videolist = ModelUtil.getList(params, "video", new ArrayList<>());
        result.put("data", doctorService.insertDoctorRecommendPrice(answerlist, phonelist, videolist));
        setOkResult(result, "添加成功");
        return result;
    }

    @ApiOperation(value = "自动审核修改")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "onecheck", value = "第一次审核", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "twocheck", value = "第二次审核", required = true, dataType = "String"),
    })
    @PostMapping("/updatecheck")
    public Map<String, Object> updatecheck(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int id=ModelUtil.getInt(params,"id");
        int onecheck=ModelUtil.getInt(params,"onecheck");
        int twocheck=ModelUtil.getInt(params,"twocheck");
        result.put("data", doctorService.updatecheck(id,onecheck,twocheck));
        setOkResult(result, "修改成功");
        return result;
    }

    @ApiOperation(value = "自动审核查看")
    @ApiImplicitParams({
    })
    @PostMapping("/getCheck")
    public Map<String, Object> getCheck(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", doctorService.getCheck());
        setOkResult(result, "查看成功");
        return result;
    }

    @ApiOperation(value = "自动审核详情")
    @ApiImplicitParams({
    })
    @PostMapping("/getCheckDetail")
    public Map<String, Object> getCheckDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id=ModelUtil.getLong(params,"id");
        result.put("data", doctorService.getCheckDetail(id));
        setOkResult(result, "查看成功");
        return result;
    }


}
