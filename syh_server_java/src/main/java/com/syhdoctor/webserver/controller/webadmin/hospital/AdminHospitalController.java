package com.syhdoctor.webserver.controller.webadmin.hospital;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.hospital.HospitalService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.print.attribute.HashPrintJobAttributeSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2018/11/16
 */
@Api(description = "/Admin/Hospital 医院管理")
@RestController
@RequestMapping("/Admin/Hospital")
public class AdminHospitalController extends BaseController {

    @Autowired
    private HospitalService hospitalService;

    @ApiOperation(value = "医院列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "医院名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getHospitalList")
    public Map<String, Object> getDiseaseList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        long begintime = ModelUtil.getLong(params, "begintime");
        long endtime = ModelUtil.getLong(params, "endtime");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", hospitalService.findHospitalList(name, begintime, endtime, pageIndex, pageSize));
        result.put("total", hospitalService.hospitalCount(name, begintime, endtime));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "医院新增修改")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "医院名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "id", value = "医院id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pcdids", value = "省市区", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "address", value = "医院地址", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitaltype", value = "医院类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospitallevel", value = "医院等级", dataType = "String"),
    })
    @PostMapping("/insertHospital")
    public Map<String, Object> insertHospital(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        long id = ModelUtil.getLong(params, "id");
        List<?> pcdids = ModelUtil.getList(params, "pcdids", new ArrayList<>());  //省市区
        String address = ModelUtil.getStr(params, "address");
        int hospitaltype = ModelUtil.getInt(params, "hospitaltype");
        int hospitallevel = ModelUtil.getInt(params, "hospitallevel");
        result.put("data", hospitalService.insertHospital(name, id, pcdids, address, hospitaltype, hospitallevel));
        setOkResult(result, "插入成功");
        return result;
    }

    @ApiOperation(value = "医院类型")
    @PostMapping("/getHospitalType")
    public Map<String, Object> getHospitalType(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", hospitalService.getHospitalType());
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "医院详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "序号", dataType = "String"),
    })
    @PostMapping("/hospitalDetail")
    public Map<String, Object> hospitalDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", hospitalService.findHospitalById(id));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "医院删除")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "医院序号", dataType = "String"),
    })
    @PostMapping("/deleteHos")
    public Map<String, Object> deleteHos(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", hospitalService.deleteHos(id));
        setOkResult(result, "删除成功");
        return result;
    }

}
