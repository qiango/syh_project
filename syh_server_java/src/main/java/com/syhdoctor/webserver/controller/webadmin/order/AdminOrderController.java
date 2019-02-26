package com.syhdoctor.webserver.controller.webadmin.order;

import com.syhdoctor.common.utils.EnumUtils.QAContentTypeEnum;
import com.syhdoctor.common.utils.*;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.answer.AnswerService;
import com.syhdoctor.webserver.service.video.DoctorVideoService;
import com.syhdoctor.webserver.utils.QiniuUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(description = "/Admin/Order 订单列表")
@RequestMapping("/Admin/Order")
public class AdminOrderController extends BaseController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private DoctorVideoService doctorVideoService;

    @ApiOperation(value = "患者症状")
    @PostMapping("/getSymptomsList")
    public Map<String, Object> getSymptomsList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long typeid = ModelUtil.getLong(params, "typeid");
        result.put("data", answerService.getAdminSymptomsList(typeid));
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "后台急诊订单查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "订单ID", dataType = "String")
    })
    @PostMapping("/getDoctorPhoneOrderById")
    public Map<String, Object> getDoctorPhoneOrderById(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int id = ModelUtil.getInt(params, "id");
        result.put("data", answerService.getDoctorPhoneOrderById(id));
        setOkResult(result, "查询成功!");
        return result;
    }


    @ApiOperation(value = "查询医生信息")
    @PostMapping("/getOnDuctDoctorList")
    public Map<String, Object> getOnDuctDoctorList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String doctorName = ModelUtil.getStr(params, "doctorname");
        result.put("data", answerService.getOnDuctDoctorList(doctorName));
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "查询用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "输入的ID", dataType = "String")
    })
    @PostMapping("/getUserById")
    public Map<String, Object> getUserById(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int id = ModelUtil.getInt(params, "id");
        result.put("data", answerService.getUserById(id));
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "修改订单状态")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "订单ID", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "paytype", value = "支付类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "paystatus", value = "支付状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "price", value = "价格", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "orderremark", value = "订单备注", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agenid", value = "登录人ID", dataType = "String"),
    })
    @PostMapping("/updateAdminDoctorPhoneOrder")
    public Map<String, Object> updateAdminDoctorPhoneOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int id = ModelUtil.getInt(params, "id");
        int payType = ModelUtil.getInt(params, "paytype");
        int payStatus = ModelUtil.getInt(params, "paystatus");
        BigDecimal price = ModelUtil.getDec(params, "price", BigDecimal.ZERO);
        String orderRemark = ModelUtil.getStr(params, "orderremark");
        long agenId = ModelUtil.getLong(params, "agenid");
        answerService.updateDoctorPhoneOrder(id, payType, payStatus, price, orderRemark, agenId);
        setOkResult(result, "修改成功!");
        return result;
    }

    /**
     * @param params
     * @return
     */
    @ApiOperation(value = "后台添加急诊订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生ID", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户ID", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorphone", value = "医生手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userphone", value = "用户手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "price", value = "价格", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "paystatus", value = "支付状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "paytype", value = "支付类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "orderremark", value = "订单备注", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agenid", value = "登录人ID", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "createtime", value = "预约时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diseaselist", value = "患者症状", dataType = "String")
    })
    @PostMapping("/addAdminDoctorPhoneOrder")
    public Map<String, Object> addAdminDoctorPhoneOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorId = ModelUtil.getLong(params, "doctorid");
        long userId = ModelUtil.getLong(params, "userid");
        String doctorPhone = ModelUtil.getStr(params, "doctorphone");
        String userPhone = ModelUtil.getStr(params, "userphone");
        BigDecimal price = ModelUtil.getDec(params, "price", BigDecimal.ZERO);
        int payStatus = ModelUtil.getInt(params, "paystatus", 0);
        int payType = ModelUtil.getInt(params, "paytype", 0);
        String orderRemark = ModelUtil.getStr(params, "orderremark");
        long agenId = ModelUtil.getLong(params, "agenid");
        long createTime = ModelUtil.getLong(params, "createtime");

        List<?> diseaseList = ModelUtil.getList(params, "diseaselist", new ArrayList<>());
        if (userId == 0 || doctorId == 0) {
            setErrorResult(result, "请检查参数!");
        } else {
            answerService.addAdminDoctorPhoneOrder(doctorId, userId, doctorPhone, userPhone, price, payStatus, createTime, payType, orderRemark, agenId, diseaseList);
            setOkResult(result, "保存成功!");
        }
        return result;
    }


    @ApiOperation(value = "订单状态列表")
    @ApiImplicitParams({
    })
    @PostMapping("/basicsList")
    public Map<String, Object> basicsList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", answerService.basicsList());
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "问诊订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "username", value = "用户名字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorname", value = "医生名字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorphone", value = "医生手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userphone", value = "用户手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "starttime", value = "订单开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "订单结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "states", value = "订单状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "typename", value = "症状类型id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diseasename", value = "症状id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getAnswerOrderList")
    public Map<String, Object> getAnswerOrderList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String userName = ModelUtil.getStr(params, "username");
        String doctorName = ModelUtil.getStr(params, "doctorname");
        String doctorPhone = ModelUtil.getStr(params, "doctorphone");
        String userPhone = ModelUtil.getStr(params, "userphone");
        long startTime = ModelUtil.getLong(params, "starttime");
        long endTime = ModelUtil.getLong(params, "endtime");
        long departTypeid = ModelUtil.getLong(params, "typename");
        long departid = ModelUtil.getLong(params, "diseasename");
        int states = ModelUtil.getInt(params, "states");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);

        result.put("data", answerService.adminAnswerOrderList(departid, departTypeid, userName, doctorName, doctorPhone, userPhone, startTime, endTime, states, pageIndex, pageSize));
        result.put("total", answerService.adminAnswerOrderCount(departid, departTypeid, userName, doctorName, doctorPhone, userPhone, startTime, endTime, states));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "问诊订单详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "订单id", dataType = "String"),
    })
    @PostMapping("/getAnswerOrder")
    public Map<String, Object> getAnswerOrder(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.getAnswerOrder(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    @ApiOperation(value = "急诊订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "username", value = "用户名字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorname", value = "医生名字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorphone", value = "医生手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userphone", value = "用户手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "starttime", value = "订单开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "订单结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "states", value = "订单状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "typename", value = "症状类型id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diseasename", value = "症状id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getPhoneOrderList")
    public Map<String, Object> getPhoneOrderList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String username = ModelUtil.getStr(params, "username");
        String doctorname = ModelUtil.getStr(params, "doctorname");
        String doctorPhone = ModelUtil.getStr(params, "doctorphone");
        String userPhone = ModelUtil.getStr(params, "userphone");
        long startTime = ModelUtil.getLong(params, "starttime");
        long endTime = ModelUtil.getLong(params, "endtime");
        int states = ModelUtil.getInt(params, "states");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        long departTypeid = ModelUtil.getLong(params, "typename");
        long departid = ModelUtil.getLong(params, "diseasename");
        result.put("data", answerService.adminPhoneOrderList(departid, departTypeid, username, doctorname, doctorPhone, userPhone, startTime, endTime, states, pageIndex, pageSize));
        result.put("total", answerService.adminPhoneOrderCount(departid, departTypeid, username, doctorname, doctorPhone, userPhone, startTime, endTime, states));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "导出订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "username", value = "用户名字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorname", value = "医生名字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorphone", value = "医生手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userphone", value = "用户手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "starttime", value = "订单开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "订单结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "states", value = "订单状态", dataType = "String")
    })
    @PostMapping("/getPhoneOrderExeclList")
    public Map<String, Object> getPhoneOrderExeclList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String username = ModelUtil.getStr(params, "username");
        String doctorname = ModelUtil.getStr(params, "doctorname");
        String doctorPhone = ModelUtil.getStr(params, "doctorphone");
        String userPhone = ModelUtil.getStr(params, "userphone");
        long startTime = ModelUtil.getLong(params, "starttime");
        long endTime = ModelUtil.getLong(params, "endtime");
        int states = ModelUtil.getInt(params, "states");

        String customRandomString = UnixUtil.getCustomRandomString();
        List<Map<String, Object>> phoneOrderList = answerService.adminPhoneOrderListExecl(username, doctorname, doctorPhone, userPhone, startTime, endTime, states);
        String fileName = customRandomString + "订单.xls";
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }
        String fileStr = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_TEMP_PATH, fileName);
        FileSystemResource file = new FileSystemResource(fileStr);
        String[] strings = {"id", "orderno", "username", "doctorname", "statesname", "actualmoney", "doctorphone", "userphone", "createtime", "recordurl", "phonestatusname"};
        ExcelUtil.createExcel(strings, phoneOrderList, file.getPath());
        result.put("data", fileStr);
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * 导出销售积分订单Excel
     *
     * @param params
     * @return
     */
    @GetMapping(value = "/downOrderExcel")
    public ResponseEntity<InputStreamResource> downExcelFile(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("DistributorSale>downExcelFile 参数 " + params);
        String fileStr = ModelUtil.getStr(params, "filestr");
        try {
            FileSystemResource file = new FileSystemResource(fileStr);
            return responseEntity(file);
        } catch (Exception ex) {
            log.error(" DistributorSale>DelCreditOrder   ", ex);
            setErrorResult(result, ex.getMessage());
        }
        return null;
    }


    @ApiOperation(value = "问诊默认小问题模板列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "usertitle", value = "用户标题", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctortitle", value = "医生标题", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getDiseaseTemplateList")
    public Map<String, Object> getDiseaseTemplateList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String usertitle = ModelUtil.getStr(params, "usertitle");
        String doctortitle = ModelUtil.getStr(params, "doctortitle");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", answerService.getDiseaseTemplateList(usertitle, doctortitle, pageIndex, pageSize));
        result.put("total", answerService.getDiseaseTemplateCount(usertitle, doctortitle));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "问诊小问题模板详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "模板id", dataType = "String"),
    })
    @PostMapping("/getDiseaseTemplate")
    public Map<String, Object> getDiseaseTemplate(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", answerService.getDiseaseTemplate(id));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "删除小问题模板")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "模板id", dataType = "String"),
    })
    @PostMapping("/delDiseaseTemplate")
    public Map<String, Object> delDiseaseTemplate(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        long createUser = ModelUtil.getLong(params, "agentid");
        result.put("data", answerService.delDiseaseTemplate(id, createUser));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "删除答案")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "答案id", dataType = "String"),
    })
    @PostMapping("/deleteAnswer")
    public Map<String, Object> deleteAnswer(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", answerService.deleteAnswer(id));
        setOkResult(result, "删除成功");
        return result;
    }

    @ApiOperation(value = "添加修改问诊小问题模板")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "模板id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "usertitle", value = "用户标题", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctortitle", value = "医生标题", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "checkbox", value = "是否多选0:否,1是", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "answerlist", value = "答案列表(content)", required = true, dataType = "String"),
    })
    @PostMapping("/addUpdateDiseaseTemplate")
    public Map<String, Object> addUpdateDiseaseTemplate(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String usertitle = ModelUtil.getStr(params, "usertitle");
        String doctortitle = ModelUtil.getStr(params, "doctortitle");
        int sort = ModelUtil.getInt(params, "sort");
        int checkbox = ModelUtil.getInt(params, "checkbox");
        long createUser = ModelUtil.getLong(params, "agentid");
        List<?> answerlist = ModelUtil.getList(params, "answerlist", new ArrayList<>());
        if (StrUtil.isEmpty(usertitle, doctortitle) || answerlist.size() == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.addUpdateDiseaseTemplate(id, usertitle, doctortitle, sort, checkbox, answerlist, createUser));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    @ApiOperation(value = "订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单编号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页数据量", defaultValue = "20", dataType = "String")
    })
    @PostMapping("/getDoctorAnswerList")
    public Map<String, Object> getDoctorAnswerList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.getDoctorAnswerList(orderid, pageindex, pagesize, 0));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    @ApiOperation(value = "h5回答")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "content", value = "发送内容", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "contenttime", value = "时长", required = true, dataType = "String"),
    })
    @PostMapping("/addH5Answer")
    public Map<String, Object> addH5Answer(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        String content = ModelUtil.getStr(params, "content");
        long contenttime = ModelUtil.getLong(params, "contenttime");
        if (orderid == 0) {
            setOkResult(result, "参数错误");
        } else {
            result.put("data", answerService.addH5Answer(orderid, content, contenttime, QAContentTypeEnum.Voice.getCode(), 1));
            setOkResult(result, "添加成功");
        }
        return result;
    }

    @ApiOperation(value = "发送图片回答")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/sendImg")
    public Map<String, Object> sendImg(@ApiParam(hidden = true) @RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        try {
            if (orderid == 0) {
                setOkResult(result, "参数错误");
            } else {
                if (file != null) {
                    BufferedImage image = ImageIO.read(file.getInputStream());
                    if (image != null) {
                        String filename = file.getOriginalFilename();
                        String type = filename.substring(filename.lastIndexOf("."));
                        String key = "syh" + UnixUtil.getCustomRandomString() + type;
                        QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, key, file.getInputStream());
                        String filePath = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_LONG_PATH, key);
                        if (FileUtil.validateFile(filePath)) {
                            FileUtil.delFile(filePath);
                        }
                        FileUtil.saveFile(file.getBytes(), filePath);
                        result.put("data", answerService.addAppAnswer(orderid, key, QAContentTypeEnum.Picture.getCode(), 1));
                        setOkResult(result, "添加成功");
                    } else {
                        setErrorResult(result, "请选择正确的图片");
                    }
                } else {
                    setErrorResult(result, "请上传文件");
                }
            }
        } catch (Exception ex) {
            log.error("home>uploadImage error", ex);
            setErrorResult(result, ex.getMessage());
        }
        return result;
    }

    @ApiOperation(value = "发送处方图片回答(直接发送)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/sendPrescriptionImg")
    public Map<String, Object> sendPrescriptionImg(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        long orderid = ModelUtil.getLong(params, "orderid");
        long prescriptionId = ModelUtil.getLong(params, "prescriptionid");
        long answerid = ModelUtil.getLong(params, "answerid");
        String diagnosis = ModelUtil.getStr(params, "diagnosis");
        List<?> druglist = ModelUtil.getList(params, "druglist", new ArrayList<>());
        try {

            if (prescriptionId != 0) {
                if (answerid == 0) {
                    setErrorResult(result, "参数错误");
                    return result;
                }
                answerService.updateAppAnswerPrescriptionExamine(prescriptionId,orderid, doctorid, diagnosis, druglist, QAContentTypeEnum.InExaminePrescription.getCode(), answerid);
                result.put("data", answerService.getDoctorAnswerList(answerid));
                setOkResult(result, "修改成功");
                return result;
            } else {
                if (orderid == 0 || doctorid == 0) {
                    setErrorResult(result, "参数错误");
                } else {
                    result.put("data", answerService.addAppAnswerPrescriptionExamine(orderid, doctorid, diagnosis, druglist, QAContentTypeEnum.InExaminePrescription.getCode(), 1));
                    setOkResult(result, "添加成功");
                }
            }
        } catch (Exception ex) {
            log.error("home>uploadImage error---------------", ex);
            log.error("异常为---------------------" + ex.getMessage());
            setErrorResult(result, ex.getMessage());
        }
        return result;
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

    @ApiOperation(value = "发送文字回答")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "content", value = "发送内容", required = true, dataType = "String"),
    })
    @PostMapping("/sendText")
    public Map<String, Object> sendText(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        String content = ModelUtil.getStr(params, "content");
        if (orderid == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.addAppAnswer(orderid, content, QAContentTypeEnum.Text.getCode(), 1));
            setOkResult(result, "添加成功");
        }
        return result;
    }

    /*@ApiOperation(value = "发送处方(常用处方转为处方)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "presno", value = "处方编号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "oftenprescriptionid", value = "常用处方id", required = true, dataType = "String"),
    })
    @PostMapping("/sendPrescription")
    public Map<String, Object> sendPrescription(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        String presNo = ModelUtil.getStr(params, "presno");
        long oftenPrescriptionId = ModelUtil.getLong(params, "oftenprescriptionid");
        if (orderid == 0 || StrUtil.isEmpty(presNo)) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", answerService.sendPrescription(orderid, presNo, oftenPrescriptionId));
            setOkResult(result, "添加成功");
        }
        return result;
    }*/

    @ApiOperation(value = "查询梯形症状")
    @ApiImplicitParams({
    })
    @PostMapping("/viewDiseaseTree")
    public Map<String, Object> viewDisease(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", answerService.findTypeTree());
        setOkResult(result, "添加成功");
        return result;
    }

    @ApiOperation(value = "查询症状类型")
    @ApiImplicitParams({
    })
    @PostMapping("/viewDiseaseOne")
    public Map<String, Object> viewDiseaseOne(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", answerService.findTypeOne());
        setOkResult(result, "添加成功");
        return result;
    }

    @ApiOperation(value = "查询症状下名称")
    @ApiImplicitParams({
    })
    @PostMapping("/viewDiseaseTwo")
    public Map<String, Object> viewDiseaseTwo(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long typeid = ModelUtil.getLong(params, "id");
        result.put("data", answerService.findTypeTwo(typeid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "查询问诊订单症状列表")
    @ApiImplicitParams({
    })
    @PostMapping("/findDepartType")
    public Map<String, Object> findDepartType(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        result.put("data", answerService.findDepartType(orderid));
        setOkResult(result, "添加成功");
        return result;
    }

    @ApiOperation(value = "查询急诊订单症状列表 ")
    @ApiImplicitParams({
    })
    @PostMapping("/findDepartTypePhone")
    public Map<String, Object> findDepartTypePhone(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        result.put("data", answerService.findDepartTypePhone(orderid));
        setOkResult(result, "添加成功");
        return result;
    }

    @ApiOperation(value = "医生电话列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "time", value = "时间", dataType = "String"),
    })
    @PostMapping("/findDoctorSchdue")
    public Map<String, Object> findDoctorSchdue(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long time = ModelUtil.getLong(params, "time");
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "phone");
        String number = ModelUtil.getStr(params, "number");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", doctorVideoService.findDoctorSchduePhone(time, pageSize, pageIndex, name, phone, number));
        setOkResult(result, "成功");
        return result;
    }


    @ApiOperation(value = "时间列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "time", value = "时间", dataType = "String"),
    })
    @PostMapping("/findTimeList")
    public Map<String, Object> findTimeList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long time = ModelUtil.getLong(params, "time");
        result.put("data", doctorVideoService.findTimeListPhone(time));
        setOkResult(result, "成功");
        return result;
    }

    @ApiOperation(value = "电话订单导出时间列表")
    @ApiImplicitParams({
    })
    @PostMapping("/getPhoneExportList")
    public Map<String, Object> getPhoneExportList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String customRandomString = UnixUtil.getCustomRandomString();
        long time = ModelUtil.getLong(params, "time");
        List<Map<String, Object>> DoctorExportList = doctorVideoService.findDoctorSchduePhoneList(time);
        String fileName = customRandomString + "电话排班.xls";
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }
        String fileStr = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_TEMP_PATH, fileName);
        FileSystemResource file = new FileSystemResource(fileStr);
        ExcelUtil.createExcelNotKey(time, DoctorExportList, file.getPath());
        result.put("data", fileStr);
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "电话订单导出时间列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "starttime", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/getPhoneExportListNew")
    public Map<String, Object> getPhoneExportListNew(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String customRandomString = UnixUtil.getCustomRandomString();
        long starttime = ModelUtil.getLong(params, "starttime");
        long endtime = ModelUtil.getLong(params, "endtime");
        List<Map<String, Object>> DoctorExportList = doctorVideoService.findDoctorSchduePhonesNew(starttime, endtime);
        String fileName = customRandomString + "电话排班.xls";
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }
        String fileStr = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_TEMP_PATH, fileName);
        FileSystemResource file = new FileSystemResource(fileStr);
        ExcelUtil.createExcelNotKeyNew(DoctorExportList, file.getPath());
        result.put("data", fileStr);
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * 导出时间列表列表Excel
     *
     * @param params
     * @return
     */
    @GetMapping(value = "/phoneOrderExcel")
    public ResponseEntity<InputStreamResource> videoOrderExcel(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("DistributorSale>userExcelFile 参数 " + params);
        String fileStr = ModelUtil.getStr(params, "filestr");
        try {
            FileSystemResource file = new FileSystemResource(fileStr);
            return responseEntity(file);
        } catch (Exception ex) {
            log.error(" DistributorSale>DelCreditOrder", ex);
            setErrorResult(result, ex.getMessage());
        }
        return null;
    }

    @ApiOperation(value = "问诊值班表导出")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "visitingstarttime", value = "问诊开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
    })
    @PostMapping("/getDoctorInquiryListExport")
    public Map<String, Object> getDoctorInquiryListExport(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long visitingstarttime = ModelUtil.getLong(params, "visitingstarttime");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "phone");
        String number = ModelUtil.getStr(params, "number");
        List<Map<String, Object>> datas = answerService.getDoctorInquiryListExport(begintime, endtime, name, phone, number);
        String customRandomstring = UnixUtil.getCustomRandomString();
        String fileName = customRandomstring + "订单.xls";
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }
        String fileStr = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_TEMP_PATH, fileName);
        FileSystemResource file = new FileSystemResource(fileStr);
        String[] strings = {"doctorid", "docname", "docno", "phone", "starttime1", "starttime", "endtime"};
        ExcelUtil.createExcel(strings, datas, file.getPath());
        result.put("data", fileStr);
        setOkResult(result, "查询成功");
        return result;
    }


    @GetMapping(value = "/doctorInquiryExcelFile")
    public ResponseEntity<InputStreamResource> doctorInquiryExcelFile(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("DistributorSale>doctorInquiryExcelFile 参数 " + params);
        String Strfile = ModelUtil.getStr(params, "filestr");
        FileSystemResource file = new FileSystemResource(Strfile);
        try {
            return responseEntity(file);
        } catch (IOException ex) {
            log.error(" DistributorSale>DelCreditOrder   ", ex);
            setErrorResult(result, ex.getMessage());
        }
        return null;
    }

    @ApiOperation(value = "问诊值班表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getDoctorInquiryList")
    public Map<String, Object> getDoctorInquiryList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long visitingstarttime = ModelUtil.getLong(params, "visitingstarttime");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "phone");
        String number = ModelUtil.getStr(params, "number");
        result.put("data", answerService.getDoctorInquiryList(begintime, endtime, pageIndex, pageSize, name, phone, number));
        result.put("total", answerService.getDoctorInquiryListCount(begintime, endtime, name, phone, number));
        setOkResult(result, "查询成功");
        return result;
    }


}
