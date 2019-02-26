package com.syhdoctor.webserver.controller.webadmin.order;

import com.syhdoctor.common.utils.*;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.video.DoctorVideoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2018/11/26
 */
@RestController
@Api(description = "/Admin/VideoOrder 视频订单")
@RequestMapping("/Admin/VideoOrder")
public class AdminVideoOrderController extends BaseController {

    @Autowired
    private DoctorVideoService doctorVideoService;

    @ApiOperation(value = "该医生下预约订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "patientname", value = "用户姓名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phonenumber", value = "手机号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "doctorname", value = "专家名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "visitcategory", value = "订单类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "status", value = "状态", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/findOrderList")
    public Map<String, Object> findOrderList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String patientname = ModelUtil.getStr(params, "patientname");
        String phonenumber = ModelUtil.getStr(params, "phonenumber");
        String dcotorname = ModelUtil.getStr(params, "doctorname");
        long endtime = ModelUtil.getLong(params, "endtime");
        long begintime = ModelUtil.getLong(params, "begintime");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        int status = ModelUtil.getInt(params, "status");
        int visitcategory = ModelUtil.getInt(params, "visitcategory");
        result.put("data", doctorVideoService.findOrderListAll(patientname, phonenumber, dcotorname, status, visitcategory, begintime, endtime, pageSize, pageIndex));
        result.put("total", doctorVideoService.findOrderListCountAll(patientname, phonenumber, dcotorname, status, visitcategory, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "医生订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "orderid", value = "订单id", dataType = "String"),
    })
    @PostMapping("/findOrderDetail")
    public Map<String, Object> findOrderDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long orderid = ModelUtil.getLong(params, "orderid");
        result.put("data", doctorVideoService.findOrderDetailAdmin(orderid));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "家庭成员列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "用户名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "begintime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "页码", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页长度", dataType = "String"),
    })
    @PostMapping("/findFamilyListAdmin")
    public Map<String, Object> findFamilyListAdmin(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String username = ModelUtil.getStr(params, "name");
        long endtime = ModelUtil.getLong(params, "endtime");
        long begintime = ModelUtil.getLong(params, "begintime");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", doctorVideoService.findFamilyListAdmin(username, begintime, endtime, pageSize, pageIndex));
        result.put("count", doctorVideoService.findFamilyListAdminCount(username, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "家庭成员详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号id", dataType = "String"),})
    @PostMapping("/findFamilyListAdminDetail")
    public Map<String, Object> findFamilyListAdminDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", doctorVideoService.findFamilyListAdminDetail(id));
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


    @ApiOperation(value = "医生视频列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/findDoctorSchdue")
    public Map<String, Object> findDoctorSchdue(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long time = ModelUtil.getLong(params, "time");
        String name=ModelUtil.getStr(params,"name");
        String phone=ModelUtil.getStr(params,"phone");
        String number=ModelUtil.getStr(params,"number");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", doctorVideoService.findDoctorSchdue(time, pageSize, pageIndex,name,phone,number));
        setOkResult(result, "成功");
        return result;
    }


    @ApiOperation(value = "时间列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/findTimeList")
    public Map<String, Object> findTimeList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long time = ModelUtil.getLong(params, "time");
        result.put("data", doctorVideoService.findTimeList(time));
        setOkResult(result, "成功");
        return result;
    }

    @ApiOperation(value = "导出时间列表")
    @ApiImplicitParams({
    })
    @PostMapping("/getVipCardExportList")
    public Map<String, Object> getVipCardExportList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String customRandomString = UnixUtil.getCustomRandomString();
        long time = ModelUtil.getLong(params, "time");
        List<Map<String, Object>> DoctorExportList = doctorVideoService.findDoctorSchdueList(time);
        String fileName = customRandomString + "视频排班.xls";
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

    @ApiOperation(value = "导出视频时间列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "starttime", value = "订单id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "订单id", required = true, dataType = "String"),
    })
    @PostMapping("/getVideoExportListNew")
    public Map<String, Object> getVideoExportList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String customRandomString = UnixUtil.getCustomRandomString();
        long starttime = ModelUtil.getLong(params, "starttime");
        long endtime = ModelUtil.getLong(params, "endtime");
        List<Map<String, Object>> DoctorExportList = doctorVideoService.findDoctorSchduesNew(starttime, endtime);
        String fileName = customRandomString + "视频排班.xls";
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
    @GetMapping(value = "/videoOrderExcel")
    public ResponseEntity<InputStreamResource> videoOrderExcel(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        log.info("DistributorSale>userExcelFile 参数 " + params);
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
