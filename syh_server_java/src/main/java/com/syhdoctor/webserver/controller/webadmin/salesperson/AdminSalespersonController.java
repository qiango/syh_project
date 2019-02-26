package com.syhdoctor.webserver.controller.webadmin.salesperson;

import com.syhdoctor.common.utils.*;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.salesperson.SalespersonService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "/Admin/salesperson 销售员")
@RestController
@RequestMapping("/Admin/salesperson")
public class AdminSalespersonController extends BaseController {

    @Autowired
    private SalespersonService salespersonService;


    @ApiOperation(value = "销售员列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "invitationcode", value = "邀请码", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "姓名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = "电话", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getSalespersonList")
    public Map<String, Object> getSalespersonList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String invitationcode = ModelUtil.getStr(params, "invitationcode");
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "phone");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", salespersonService.getSalespersonList(invitationcode, name, phone, pageindex, pagesize));
        result.put("total", salespersonService.getSalespersonListCount(invitationcode, name, phone));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "销售员详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
    })
    @PostMapping("/getSalespersonId")
    public Map<String, Object> getSalespersonId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", salespersonService.getSalespersonId(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "删除销售员")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
    })
    @PostMapping("/delSalesperson")
    public Map<String, Object> delSalesperson(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", salespersonService.delSalesperson(id));
            setOkResult(result, "删除成功");
        }
        return result;
    }


    @ApiOperation(value = "新增or修改销售员")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "姓名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = "电话", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "salesmancode", value = "销售员编码", required = true, dataType = "String"),
    })
    @PostMapping("/updateAddSalesperson")
    public Map<String, Object> updateAddSalesperson(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "phone");
        String salesmancode = ModelUtil.getStr(params, "salesmancode");
        result.put("data", salespersonService.updateAddSalesperson(id, name, phone, salesmancode));
        setOkResult(result, "操作成功");
        return result;
    }


    @ApiOperation(value = "邀请医生列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "invitationcode", value = "邀请码", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "salespersonname", value = "销售员姓名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "salespersonphone", value = "销售员电话", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorname", value = "医生姓名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctortel", value = "医生电话", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getSalespersonDoctorList")
    public Map<String, Object> getSalespersonDoctorList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String invitationcode = ModelUtil.getStr(params, "invitationcode");
        String salespersonname = ModelUtil.getStr(params, "salespersonname");
        String salespersonphone = ModelUtil.getStr(params, "salespersonphone");
        String doctorname = ModelUtil.getStr(params, "doctorname");
        String doctortel = ModelUtil.getStr(params, "doctortel");
        String doccode = ModelUtil.getStr(params, "doccode");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", salespersonService.getSalespersonDoctorList(doccode, invitationcode, salespersonname, salespersonphone, doctorname, doctortel, begintime, endtime, pageindex, pagesize));
        result.put("total", salespersonService.getSalespersonDoctorListCount(doccode, invitationcode, salespersonname, salespersonphone, doctorname, doctortel, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "邀请医生导出")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "invitationcode", value = "邀请码", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "salespersonname", value = "销售员姓名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "salespersonphone", value = "销售员电话", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorname", value = "医生姓名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctortel", value = "医生电话", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
    })
    @PostMapping("/getSalespersonDoctorListAll")
    public Map<String, Object> getSalespersonDoctorListAll(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String invitationcode = ModelUtil.getStr(params, "invitationcode");
        String salespersonname = ModelUtil.getStr(params, "salespersonname");
        String salespersonphone = ModelUtil.getStr(params, "salespersonphone");
        String doctorname = ModelUtil.getStr(params, "doctorname");
        String doctortel = ModelUtil.getStr(params, "doctortel");
        String doccode = ModelUtil.getStr(params, "doccode");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        String customRandomString = UnixUtil.getCustomRandomString();
        List<Map<String, Object>> DoctorExportList = salespersonService.getSalespersonDoctorListAll(doccode, invitationcode, salespersonname, salespersonphone, doctorname, doctortel, begintime, endtime);
        String fileName = customRandomString + "订单.xls";
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }
        String fileStr = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_TEMP_PATH, fileName);
        FileSystemResource file = new FileSystemResource(fileStr);
        String[] strings = {"id", "invitationcode", "doccode", "doctorname", "doctortel", "salespersonname", "salespersonphone", "createtime"};
        ExcelUtil.createExcel(strings, DoctorExportList, file.getPath());
        result.put("data", fileStr);
        setOkResult(result, "查询成功");
        return result;
    }


    /**
     * 导出邀请医生列表Excel
     *
     * @param params
     * @return
     */
    @GetMapping(value = "/SalespersonDoctorExcelFile")
    public ResponseEntity<InputStreamResource> SalespersonDoctorExcelFile(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("DistributorSale>SalespersonDoctorExcelFile 参数 " + params);
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







}
