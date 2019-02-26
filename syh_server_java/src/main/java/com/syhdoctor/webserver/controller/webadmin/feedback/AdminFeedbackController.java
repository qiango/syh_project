package com.syhdoctor.webserver.controller.webadmin.feedback;

import com.syhdoctor.common.utils.*;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.feedback.FeedbackService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(description = "/Admin/Feedback 意见反馈接口")
@RequestMapping("/Admin/Feedback")
public class AdminFeedbackController extends BaseController {

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


    @ApiOperation(value = "意见反馈列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = " 苹果", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "system", value = "类型：1用户端,2医生端", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "status", value = " 处理状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = " 手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageIndex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/feedbackList")
    public Map<String, Object> feedbackList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int system = ModelUtil.getInt(params, "system");
        int status = ModelUtil.getInt(params, "status");
        String phone = ModelUtil.getStr(params, "phone");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageIndex = ModelUtil.getInt(params, "pageindex");
        int pageSize = ModelUtil.getInt(params, "pagesize");
        result.put("data", feedbackService.feedbackList(name, system, status, phone, begintime, endtime, pageIndex, pageSize));
        result.put("total", feedbackService.feedbackListCount(name, system, status, phone, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "意见反馈导出查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = " 苹果", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "system", value = "类型：1用户端,2医生端", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "status", value = " 处理状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = " 手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
    })
    @PostMapping("/feedbackListExport")
    public Map<String, Object> feedbackListExport(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int system = ModelUtil.getInt(params, "system");
        int status = ModelUtil.getInt(params, "status");
        String phone = ModelUtil.getStr(params, "phone");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        String customRandomString = UnixUtil.getCustomRandomString();
        List<Map<String, Object>> feedbackExportList = feedbackService.feedbackListExport(name, system, status, phone, begintime, endtime);
        String fileName = customRandomString + "订单.xls";
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }
        String fileStr = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_TEMP_PATH, fileName);
        FileSystemResource file = new FileSystemResource(fileStr);
        String[] strings = {"id", "name", "phone", "system", "content", "createtime", "status", "diagnosis"};
        ExcelUtil.createExcel(strings, feedbackExportList, file.getPath());
        result.put("data", fileStr);
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * 导出意见反馈列表Excel
     *
     * @param params
     * @return
     */
    @GetMapping(value = "/feedbackExcelFile")
    public ResponseEntity<InputStreamResource> feedbackExcelFile(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("DistributorSale>feedbackExcelFile 参数 " + params);
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


    @ApiOperation(value = "删除意见反馈")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
    })
    @PostMapping("/feedbackDel")
    public Map<String, Object> feedbackDel(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        long a = feedbackService.feedbackDel(id);
        if (a > 0) {
            setOkResult(result, "删除成功");
        } else {
            setErrorResult(result, "删除失败");
        }
        return result;
    }


    @ApiOperation(value = "点击编辑查询单个")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
    })
    @PostMapping("/feedbackOneId")
    public Map<String, Object> feedbackOneId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if(id==0){
            setErrorResult(result,"参数错误");
        }else {
            result.put("data", feedbackService.feedbackOneId(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "点击编辑查询单个 下拉框")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
    })
    @PostMapping("/feedbackOneIdType")
    public Map<String, Object> feedbackOneIdType(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if(id==0){
            setErrorResult(result,"参数错误");
        }else {
            result.put("data", feedbackService.feedbackOneIdType(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }



//    @ApiOperation(value = "编辑反馈")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
//            @ApiImplicitParam(paramType = "query", name = "diagnosis", value = "反馈结果", dataType = "String"),
//    })
//    @PostMapping("/feedbackUpdate")
//    public Map<String, Object> feedbackUpdate(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
//        Map<String, Object> result = new HashMap<>();
//        long id = ModelUtil.getLong(params, "id");
//        String diagnosis = ModelUtil.getStr(params, "diagnosis");
//        if (id == 0) {
//            setErrorResult(result, "参数错误");
//        } else {
//            feedbackService.feedbackUpdate(id, diagnosis);
//            setOkResult(result, "修改成功");
//        }
//        return result;
//    }




    @ApiOperation(value = "编辑或添加反馈")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = "电话", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "system", value = "1,用户 2,医生", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "status", value = "类型（处理状态）", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "content", value = "反馈内容", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diagnosis", value = "反馈结果", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "用户或医生的姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "createtime", value = "创建时间", dataType = "String"),
    })
    @PostMapping("/feedbackAdd")
    public Map<String, Object> feedbackAdd(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        int system = ModelUtil.getInt(params, "system");
        String phone = ModelUtil.getStr(params, "phone");
        String content = ModelUtil.getStr(params, "content");
        String diagnosis = ModelUtil.getStr(params, "diagnosis");
        int status = ModelUtil.getInt(params, "status", 1);
        String name = ModelUtil.getStr(params,"name");
        long createtime = ModelUtil.getLong(params,"createtime");
        if(id == 0){
            if (StrUtil.isEmpty(phone) || system == 0 ) {
                setErrorResult(result, "参数错误");
            } else {
                result.put("data", feedbackService.feedbackPhone(phone, system, status, content, diagnosis,createtime,name));
                setOkResult(result, "添加成功");
            }
        }else{
                result.put("data",feedbackService.feedbackUpdate(id, diagnosis));
                setOkResult(result, "修改成功");
        }
        return result;
    }


}
