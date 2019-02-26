package com.syhdoctor.webserver.controller.webadmin.department;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.department.DepartmentService;
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

@Api(description = "/Admin/Department 科室包")
@RestController
@RequestMapping("/Admin/Department")
public class AdminDepartmentController extends BaseController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 新增修改科室包
     *
     * @return
     */
    @ApiOperation(value = "新增修改科室包")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "科室包id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "科室名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "departmentids", value = "字典科室id", dataType = "List"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登陆人id", dataType = "String"),
    })
    @PostMapping("addUpdateDepartmentPackage")
    public Map<String, Object> addUpdateDepartmentPackage(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String name = ModelUtil.getStr(params, "name");
        int sort = ModelUtil.getInt(params, "sort");
        List<?> departmentids = ModelUtil.getList(params, "departmentids", new ArrayList<>());
        long agentId = ModelUtil.getLong(params, "agentid");
        if (StrUtil.isEmpty(name)) {
            setErrorResult(result, "参数错误");
        } else {
            boolean flag = departmentService.addUpdateDepartmentPackage(id, name, sort, departmentids, agentId);
            if (flag) {
                setOkResult(result, "添加成功");
            } else {
                setErrorResult(result, "添加失败");
            }
        }
        return result;
    }

    /**
     * 科室包列表
     *
     * @return
     */
    @ApiOperation(value = "科室包列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "科室名",  dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String")
    })
    @PostMapping("getDepartmentPackageList")
    public Map<String, Object> getDepartmentPackageList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", departmentService.getDepartmentPackageList(name, pageIndex, pageSize));
        result.put("total", departmentService.getDepartmentPackageCount(name));
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * 科室包详情
     *
     * @return
     */
    @ApiOperation(value = "科室包详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "科室包id", required = true, dataType = "String"),
    })
    @PostMapping("getDepartmentPackage")
    public Map<String, Object> getDepartmentPackage(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", departmentService.getDepartmentPackage(id));
            setOkResult(result, "添加成功");
        }
        return result;
    }

    /**
     * 删除科室包
     *
     * @return
     */
    @ApiOperation(value = "删除科室包")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "科室包id", required = true, dataType = "String"),
    })
    @PostMapping("delDepartmentPackage")
    public Map<String, Object> delDepartmentPackage(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", departmentService.delDepartmentPackage(id));
            setOkResult(result, "添加成功");
        }
        return result;
    }

    /**
     * 科室包列表
     *
     * @return
     */
    @ApiOperation(value = "科室字典列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "科室名",  dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String")
    })
    @PostMapping("getDepartmentList")
    public Map<String, Object> getDepartmentList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        String name = ModelUtil.getStr(params, "name");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        Map<String, Object> result = new HashMap<>();
        result.put("data", departmentService.getDepartmentList(name,pageIndex,pageSize));
        setOkResult(result, "查询成功");
        return result;
    }
}
