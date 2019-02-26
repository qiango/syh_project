package com.syhdoctor.webserver.controller.webadmin.doctor;

import com.syhdoctor.common.utils.*;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.doctor.DoctorManageService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "/Admin/Manage 医生账户管理")
@RestController
@RequestMapping("/Admin/Manage")
public class AdminDoctorWalletController extends AdminDoctorController {

    @Autowired
    private DoctorManageService doctorManageService;

    @ApiOperation(value = "医生账户管理查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "indoccode", value = "会员号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docname", value = "姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "dootel", value = "手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getDoctorManageList")
    public Map<String, Object> getDoctorManageList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String indoccode = ModelUtil.getStr(params, "indoccode");
        String docname = ModelUtil.getStr(params, "docname");
        String dootel = ModelUtil.getStr(params, "dootel");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", doctorManageService.getDoctorManageList(indoccode, docname, dootel, begintime, endtime, pageIndex, pageSize));
        result.put("total", doctorManageService.getDoctorManageCount(indoccode, docname, dootel, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "医生账户详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "编号", dataType = "String"),
    })
    @PostMapping("/getDortorInfoId")
    public Map<String, Object> getDortorInfoId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        if (doctorid != 0) {
            result.put("data", doctorManageService.getDoctorinfoId(doctorid));
            setOkResult(result, "查询成功");
        } else {
            setErrorResult(result, "参数错误");
        }
        return result;
    }

    @ApiOperation(value = "医生账户详情列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "doctorid", value = "编号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "moneyflag", value = "交易类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getDoctorInfoListId")
    public Map<String, Object> getDoctorInfoListId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long doctorid = ModelUtil.getLong(params, "doctorid");
        int moneyflag = ModelUtil.getInt(params, "moneyflag");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        if(doctorid == 0){
            setErrorResult(result,"参数错误");
        }else{
            result.put("data", doctorManageService.getDoctorinfoListId(doctorid, moneyflag, pageIndex, pageSize));
            result.put("total", doctorManageService.getDoctorinfoListCount(doctorid, moneyflag));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "导出")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "indoccode", value = "会员号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "docname", value = "姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "dootel", value = "手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
    })
    @PostMapping("/getDoctorExportListAll")
    public Map<String, Object> getDoctorExportListAll(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String indoccode = ModelUtil.getStr(result, "indoccode");
        String docname = ModelUtil.getStr(result, "docname");
        String dootel = ModelUtil.getStr(result, "dootel");
        long begintime = ModelUtil.getLong(result, "begintime");
        long endtime = ModelUtil.getLong(result, "endtime");
        String customRandomString = UnixUtil.getCustomRandomString();
        List<Map<String, Object>> DoctorExportList = doctorManageService.getDoctorExportListAll(indoccode, docname, dootel, begintime, endtime);
        String fileName = customRandomString + "订单.xls";
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }
        String fileStr = ConfigModel.BASEFILEPATH+FileUtil.setFileName(FileUtil.FILE_TEMP_PATH,fileName);
        FileSystemResource file = new FileSystemResource(fileStr);
        String[] strings = {"doctorid", "indoccode", "docname", "dootel", "createtime", "walletbalance", "integral"};
        ExcelUtil.createExcel(strings, DoctorExportList, file.getPath());
        result.put("data", fileStr);
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * 导出医生账户列表Excel
     *
     * @param params
     * @return
     */
    @GetMapping(value = "/doctorExcelFile")
    public ResponseEntity<InputStreamResource> doctorExcelFile(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("DistributorSale>doctorExcelFile 参数 " + params);
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
