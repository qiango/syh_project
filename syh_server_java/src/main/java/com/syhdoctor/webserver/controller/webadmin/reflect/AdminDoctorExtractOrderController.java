package com.syhdoctor.webserver.controller.webadmin.reflect;

import com.syhdoctor.common.utils.*;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.reflect.DoctorExtractOrderService;
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
@Api(description = "/Admin/Reflect 提现管理")
@RequestMapping("/Admin/Reflect")
public class AdminDoctorExtractOrderController extends BaseController {

    @Autowired
    private DoctorExtractOrderService doctorExtractOrderService;


    @ApiOperation(value = "提现记录查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生编号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docname", value = "姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "status", value = "状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "dootel", value = "手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getDoctorExtractOrderList")
    public Map<String, Object> getDoctorExtractOrderList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        String docname = ModelUtil.getStr(params, "docname");
        int status = ModelUtil.getInt(params, "status");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        String dootel = ModelUtil.getStr(params,"dootel");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", doctorExtractOrderService.getDoctorExtractOrderList( dootel,doctorid, docname, status, begintime, endtime, pageIndex, pageSize));
        result.put("total", doctorExtractOrderService.getDoctorExtractOrderCount(doctorid, docname, status, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "通过审核 打款成功")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "提现订单id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "status", value = "状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "创建人", dataType = "String"),
    })
    @PostMapping("/addExamine")
    public Map<String, Object> addExamine(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        int status = ModelUtil.getInt(params, "status");
        long agentid = ModelUtil.getLong(params, "agentid");
        String failreason = ModelUtil.getStr(params, "failreason");
        if (id == 0 || status == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", doctorExtractOrderService.addExamine(id, status, agentid, failreason));
            setOkResult(result, "新增成功");
        }
        return result;
    }


    @ApiOperation(value = "导出")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生编号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docname", value = "姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "status", value = "状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
    })
    @PostMapping("/getDoctorExtractOrderExportListAll")
    public Map<String, Object> getDoctorExtractOrderExportListAll(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        String docname = ModelUtil.getStr(params, "docname");
        int status = ModelUtil.getInt(params, "status");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        String customRandomString = UnixUtil.getCustomRandomString();
        List<Map<String, Object>> DoctorExportList = doctorExtractOrderService.getDoctorExtractOrderExportListAll(doctorid, docname, status, begintime, endtime);
        String fileName = customRandomString + "订单.xls";
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }
        String fileStr = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_TEMP_PATH, fileName);
        FileSystemResource file = new FileSystemResource(fileStr);
        String[] strings = {"id", "orderno", "indoccode", "number", "bankname", "docname", "dootel", "examinetime", "amountmoney", "status", "failreason", "paytime"};
        ExcelUtil.createExcel(strings, DoctorExportList, file.getPath());
        result.put("data", fileStr);
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * 导出提现记录列表Excel
     *
     * @param params
     * @return
     */
    @GetMapping(value = "/reflectExcelFile")
    public ResponseEntity<InputStreamResource> reflectExcelFile(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("DistributorSale>reflectExcelFile 参数 " + params);
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
