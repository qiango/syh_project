package com.syhdoctor.webserver.controller.webapp.appapi.doctor.prescription;

import com.syhdoctor.common.utils.EnumUtils.QAContentTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.answer.AnswerService;
import com.syhdoctor.webserver.service.prescription.PrescriptionService;
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

@Api(description = "/App/Prescription APP处方相关接口")
@RestController
@RequestMapping("/App/Prescription")
public class AppPrescriptionController extends BaseController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private AnswerService answerService;

    @ApiOperation(value = "添加常用处方")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "oftenprescriptionid", value = "常用处方详情id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diagnosis", value = "临床诊断", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diseasestypeid", value = "疾病分类", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "druglist", value = "药品列表", required = true, dataType = "String"),
    })
    @PostMapping("/addOftenPrescription")
    public Map<String, Object> addOftenPrescription(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long oftenprescriptionid = ModelUtil.getLong(params, "oftenprescriptionid");
        long doctorId = ModelUtil.getLong(params, "doctorid");
        long diseasesTypeId = ModelUtil.getLong(params, "diseasestypeid");
        String diagnosis = ModelUtil.getStr(params, "diagnosis");
        List<?> druglist = ModelUtil.getList(params, "druglist", new ArrayList<>());
        if (StrUtil.isEmpty(diagnosis) || doctorId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", prescriptionService.addOftenPrescription(oftenprescriptionid, doctorId, diagnosis, diagnosis, diseasesTypeId, druglist));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "常用处方列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数据量", defaultValue = "20", dataType = "String")
    })
    @PostMapping("/getOftenPrescriptionList")
    public Map<String, Object> getOftenPrescriptionList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", prescriptionService.getOftenPrescriptionList(doctorId, pageindex, pagesize));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "常用处方详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "prescriptionid", value = "常用处方id", required = true, dataType = "String"),
    })
    @PostMapping("/getOftenPrescription")
    public Map<String, Object> getOftenPrescription(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long prescriptionId = ModelUtil.getLong(params, "prescriptionid");
        result.put("data", prescriptionService.getOftenPrescription(prescriptionId));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "处方发送页面详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userid", value = "患者id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "oftenprescriptionid", value = "常用处方id", required = true, dataType = "String"),
    })
    @PostMapping("/getSendPrescription")
    public Map<String, Object> getSendPrescription(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long userId = ModelUtil.getLong(params, "userid");
        long prescriptionId = ModelUtil.getLong(params, "oftenprescriptionid");
        if (userId == 0 || prescriptionId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", prescriptionService.getSendPrescription(userId, prescriptionId));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "删除常用处方")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "prescriptionid", value = "常用处方详情id", required = true, dataType = "String"),
    })
    @PostMapping("/delOftenPrescription")
    public Map<String, Object> delOftenPrescription(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long prescriptionId = ModelUtil.getLong(params, "prescriptionid");
        if (prescriptionId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", prescriptionService.delOftenPrescription(prescriptionId));
            setOkResult(result, "删除成功");
        }
        return result;
    }


    @ApiOperation(value = "药品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "药品id", required = true, dataType = "String"),
    })
    @PostMapping("/getDrugUseDetail")
    public Map<String, Object> getDrugUseDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", prescriptionService.getDrugUseDetail(id));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "预览处方")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "处方id", dataType = "String"),
    })
    @PostMapping("/addPrescription")
    public Map<String, Object> appDoctorPreview(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderId = ModelUtil.getLong(params, "orderid");
        String diagnosis = ModelUtil.getStr(params, "diagnosis");
        List<?> druglist = ModelUtil.getList(params, "druglist", new ArrayList<>());
        if (orderId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", prescriptionService.appDoctorPreview(orderId, diagnosis, druglist));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    @ApiOperation(value = "发送处方图片回答")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/sendPrescriptionImg")
    public Map<String, Object> sendPrescriptionImg(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        String diagnosis = ModelUtil.getStr(params, "diagnosis");
        String presNo = ModelUtil.getStr(params, "presno");
        long oftenPrescriptionId = ModelUtil.getLong(params, "oftenprescriptionid");
        long prescriptionId = ModelUtil.getLong(params, "prescriptionid");
        long answerid = ModelUtil.getLong(params, "answerid");
        List<?> druglist = ModelUtil.getList(params, "druglist", new ArrayList<>());
        Map<String, Object> maps = prescriptionService.getAnswerOrderDoctorId(orderid);
        long doctorid = ModelUtil.getLong(maps, "doctorid");
        if (oftenPrescriptionId > 0 && prescriptionId > 0) {
            setErrorResult(result, "参数错误");
            return result;
        }
        //常用出方发处方
        if (oftenPrescriptionId != 0) {
            if (orderid == 0 || StrUtil.isEmpty(presNo)) {
                setErrorResult(result, "参数错误");
            } else {
                Map<String, Object> map = answerService.sendPrescription(orderid, presNo, oftenPrescriptionId);
                result.put("data", map);
                setOkResult(result, "添加成功");
            }
            return result;
        //修改处方
        } else if (prescriptionId != 0) {
            answerService.updateAppAnswerPrescriptionExamine(prescriptionId,orderid, doctorid, diagnosis, druglist, QAContentTypeEnum.InExaminePrescription.getCode(), answerid);
            //answerid>0 从聊天页面发送的处方  讲该消息的处方类型改成审核中
            result.put("data", answerid > 0 ? answerService.getDoctorAnswerList(answerid) : null);
            setOkResult(result, "修改成功");
            return result;
        //直接发送处方
        } else {
            if (orderid == 0 || doctorid == 0) {
                setErrorResult(result, "参数错误");
            } else {
                result.put("data", answerService.addAppAnswerPrescriptionExamine(orderid, doctorid, diagnosis, druglist, QAContentTypeEnum.InExaminePrescription.getCode(), 1));
                setOkResult(result, "添加成功");
            }
            return result;
        }
    }

    @ApiOperation(value = "处方详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "prescriptionid", value = "处方id", required = true, dataType = "String"),
    })
    @PostMapping("/getPrescription")
    public Map<String, Object> getPrescription(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long prescriptionId = ModelUtil.getLong(params, "prescriptionid");
        if (prescriptionId == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.getPrescription(prescriptionId));
            setOkResult(result, "添加成功");
        }
        return result;
    }
}
