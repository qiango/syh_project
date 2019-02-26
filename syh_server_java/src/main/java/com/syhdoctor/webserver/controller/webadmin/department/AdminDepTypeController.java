package com.syhdoctor.webserver.controller.webadmin.department;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.department.CommonDiseaseService;
import com.syhdoctor.webserver.service.doctor.DoctorService;
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

/**
 * @author qian.wang
 * @description
 * @date 2018/11/7
 */

@Api(description = "/Admin/DepartmentType 科室类型症状管理")
@RestController
@RequestMapping("/Admin/DepartmentType")
public class AdminDepTypeController extends BaseController {

    @Autowired
    private CommonDiseaseService commonDiseaseService;
    @Autowired
    private DoctorService doctorService;

    @ApiOperation(value = "科室类型列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "departName", value = "科室名",  dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String"),
    })
    @PostMapping("/getDepartmentPackageList")
    public Map<String, Object> getDepartmentPackageList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String departName = ModelUtil.getStr(params, "departName");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", commonDiseaseService.findList(departName, pageIndex, pageSize));
        result.put("total", commonDiseaseService.findCount(departName));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation("常见病症状类型表")
    @PostMapping("/getCommonDiseaseSymptomsType")
    public Map<String,Object> getCommonDiseaseSymptomsType(@ApiParam(hidden = true) @RequestParam Map<String, Object> params){
        Map<String,Object> result = new HashMap<>();
        result.put("data",commonDiseaseService.getCommonDiseaseSymptomsType());
        setOkResult(result,"查询成功");
        return result;
    }

    @ApiOperation("新增或修改常见病症状类型表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "症状分类名", required = true, dataType = "String"),
    })
    @PostMapping("/updateAddCommonDiseaseSymptomsType")
    public Map<String,Object> updateAddCommonDiseaseSymptomsType(@ApiParam(hidden = true) @RequestParam Map<String, Object> params){
        Map<String,Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params,"id");
        String name = ModelUtil.getStr(params,"name");
        if(StrUtil.isEmpty(name)){
            setErrorResult(result,"参数错误");
        }else{
            result.put("data",commonDiseaseService.updateAddCommonDiseaseSymptomsType(id,name));
            setOkResult(result,"操作成功");
        }
        return result;
    }

    @ApiOperation("删除常见病症状类型表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
    })
    @PostMapping("/delType")
    public Map<String,Object> delType(@ApiParam(hidden = true) @RequestParam Map<String, Object> params){
        Map<String,Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params,"id");
        if(id == 0){
            setErrorResult(result,"参数错误");
        }else{
            result.put("data",commonDiseaseService.delType(id));
            setOkResult(result,"删除成功");
        }
        return result;
    }


    @ApiOperation("常见病症状列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "typeid", value = "症状分类id", dataType = "String"),
    })
    @PostMapping("/getCommonDiseaseSymptoms")
    public Map<String,Object> getCommonDiseaseSymptoms(@ApiParam(hidden = true) @RequestParam Map<String, Object> params){
        Map<String,Object> result = new HashMap<>();
        long typeid = ModelUtil.getLong(params,"typeid");
        if(typeid == 0){
            setErrorResult(result,"参数错误");
        }else{
            result.put("data",commonDiseaseService.getCommonDiseaseSymptoms(typeid));
            setOkResult(result,"查询成功");
        }
        return result;
    }

    @ApiOperation("删除常见病症状列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/delCommonDiseaseSymptoms")
    public Map<String,Object> delCommonDiseaseSymptoms(@ApiParam(hidden = true) @RequestParam Map<String, Object> params){
        Map<String,Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params,"id");
        if(id == 0){
            setErrorResult(result,"参数错误");
        }else{
            result.put("data",commonDiseaseService.delCommonDiseaseSymptoms(id));
            setOkResult(result,"删除成功");
        }
        return result;
    }

    @ApiOperation("新增或修改常见病症状列表 修改传入id、name 新增传入name、typeid")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "症状分类名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "typeid", value = "症状分类id", dataType = "String"),
    })
    @PostMapping("/updateAddCommonDiseaseSymptoms")
    public Map<String,Object> updateAddCommonDiseaseSymptoms(@ApiParam(hidden = true) @RequestParam Map<String, Object> params){
        Map<String,Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params,"id");
        String name = ModelUtil.getStr(params,"name");
        long typeid = ModelUtil.getLong(params,"typeid");
        if(id != 0){
            result.put("data",commonDiseaseService.updateCommonDiseaseSymptoms(id,name));
            setOkResult(result,"修改成功");
        }else if(typeid != 0){
            result.put("data",commonDiseaseService.insertCommonDiseaseSymptoms(name,typeid));
            setOkResult(result,"新增成功");
        }else{
            setErrorResult(result,"参数错误");
        }
        return result;
    }

    @ApiOperation(value = "科室类型新增")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "departid", value = "科室id",  dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "typeList", value = "类型id集合",  dataType = "String"),
    })
    @PostMapping("/insertDepart")
    public Map<String, Object> insertDepart(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String,Object> result=new HashMap<>();
//        List<?> departid = ModelUtil.getList(params, "departid", new ArrayList<>());
        List<?> typelist = ModelUtil.getList(params, "typeList", new ArrayList<>());
        long departid=ModelUtil.getLong(params,"departid");
        boolean res= commonDiseaseService.insertDepart(departid,typelist);
        if(res){
            setOkResult(result,"成功");
        }else {
            setErrorResult(result,"失败");
        }
        return result;
    }

    /**
     * 删除科室类型
     *
     * @return
     */
    @ApiOperation(value = "删除科室类型")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "科室类型id", required = true, dataType = "String"),
    })
    @PostMapping("/deleteDepart")
    public Map<String, Object> deleteDepart(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", commonDiseaseService.deleteDepart(id));
            setOkResult(result, "删除成功");
        }
        return result;
    }

    @ApiOperation(value = "类型列表")
    @ApiImplicitParams({
    })
    @PostMapping("/findType")
    public Map<String, Object> findDetail(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", commonDiseaseService.findType());
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "科视列表")
    @ApiImplicitParams({
    })
    @PostMapping("/getLastDepartments")
    public Map<String, Object> find(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String value=ModelUtil.getStr(params,"value");
        result.put("data", doctorService.getLastDepartments(value));
        setOkResult(result, "查询成功");
        return result;
    }
}
