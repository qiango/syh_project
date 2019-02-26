package com.syhdoctor.webserver.controller.webadmin.finance;

import com.syhdoctor.common.utils.*;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.finance.FinanceOrderService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "/Admin/finance 财务管理")
@RestController
@RequestMapping("/Admin/finance")
public class AdminFinanceOrderController extends BaseController {

    @Autowired
    private FinanceOrderService financeOrderService;

    @ApiOperation(value = "财务记录查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "indoccode", value = "医生编号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docname", value = "姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "status", value = "打款状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getFinanceOrderList")
    public Map<String, Object> getFinanceOrderList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "indoccode");
        String docname = ModelUtil.getStr(params, "docname");
        int status = ModelUtil.getInt(params, "status");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", financeOrderService.getFinanceOrderList(doctorid, docname, status, begintime, endtime, pageIndex, pageSize));
        result.put("total", financeOrderService.getFinanceOrderListCount(doctorid, docname, status, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "修改打款状态")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "提现订单id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "status", value = "打款状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "创建人", dataType = "String"),
    })
    @PostMapping("/addRemittanceLog")
    public Map<String, Object> addRemittanceLog(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        int status = ModelUtil.getInt(params, "status");
        long agentid = ModelUtil.getLong(params, "agentid"); // 当前用户id
        String failreason = ModelUtil.getStr(params, "failreason");
        if (id == 0 || status == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", financeOrderService.addRemittanceLog(id, status, agentid, failreason));
            setOkResult(result, "新增成功");
        }
        return result;
    }


    @ApiOperation(value = "财务记录导出")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "医生编号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docname", value = "姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "status", value = "打款状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
    })
    @PostMapping("/getFinanceOrderListAll")
    public Map<String, Object> getFinanceOrderListAll(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "indoccode");
        String docname = ModelUtil.getStr(params, "docname");
        int status = ModelUtil.getInt(params, "status");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        String customRandomString = UnixUtil.getCustomRandomString();
        List<Map<String, Object>> DoctorExportList = financeOrderService.getFinanceOrderListAll(doctorid, docname, status, begintime, endtime);
        String fileName = customRandomString + "订单.xls";
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }
        String fileStr = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_TEMP_PATH, fileName);
        FileSystemResource file = new FileSystemResource(fileStr);
        String[] strings = {"id", "orderno", "doctorid", "docname", "number", "bankname", "dootel", "examinetime", "amountmoney", "status", "failreason", "paytime"};
        ExcelUtil.createExcel(strings, DoctorExportList, file.getPath());
        result.put("data", fileStr);
        setOkResult(result, "查询成功");
        return result;
    }


    /**
     * 导出财务记录列表Excel
     *
     * @param params
     * @return
     */
    @GetMapping(value = "/FinanceExcelFile")
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
