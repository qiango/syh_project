package com.syhdoctor.webserver.controller.webadmin.drugs;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.drugs.DrugsService;
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

@Api(description = "/Admin/Drugs 药品包")
@RestController
@RequestMapping("/Admin/Drugs")
public class AdminDrugsController extends BaseController {

    @Autowired
    private DrugsService drugsService;

    /**
     * 新增修改药品包
     *
     * @return
     */
    @ApiOperation(value = "新增修改药品包")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "药品包id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "药品名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "img", value = "图片", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "drugsids", value = "字典药品id", dataType = "List"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登陆人id", dataType = "String"),
    })
    @PostMapping("addUpdateDrugsPackage")
    public Map<String, Object> addUpdateDrugsPackage(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String name = ModelUtil.getStr(params, "name");
        String img = ModelUtil.getStr(params, "img");
        int sort = ModelUtil.getInt(params, "sort");
        List<?> drugsids = ModelUtil.getList(params, "drugsids", new ArrayList<>());
        long agentId = ModelUtil.getLong(params, "agentid");
        if (StrUtil.isEmpty(name,img)) {
            setErrorResult(result, "参数错误");
        } else {
            boolean flag = drugsService.addUpdateDrugsPackage(id, name,img, sort, drugsids, agentId);
            if (flag) {
                setOkResult(result, "添加成功");
            } else {
                setErrorResult(result, "添加失败");
            }
        }
        return result;
    }

    /**
     * 药品包列表
     *
     * @return
     */
    @ApiOperation(value = "药品包列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "药品名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String")
    })
    @PostMapping("getDrugsPackageList")
    public Map<String, Object> getDrugsPackageList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", drugsService.getDrugsPackageList(name, pageIndex, pageSize));
        result.put("total", drugsService.getDrugsPackageCount(name));
        setOkResult(result, "查询成功");
        return result;
    }

    /**
     * 药品包详情
     *
     * @return
     */
    @ApiOperation(value = "药品包详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "药品包id", required = true, dataType = "String"),
    })
    @PostMapping("getDrugsPackage")
    public Map<String, Object> getDrugsPackage(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", drugsService.getDrugsPackage(id));
            setOkResult(result, "添加成功");
        }
        return result;
    }

    /**
     * 删除药品包
     *
     * @return
     */
    @ApiOperation(value = "删除药品包")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "药品包id", required = true, dataType = "String"),
    })
    @PostMapping("delDrugsPackage")
    public Map<String, Object> delDrugsPackage(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", drugsService.delDrugsPackage(id));
            setOkResult(result, "添加成功");
        }
        return result;
    }

    /**
     * 药品包列表
     *
     * @return
     */
    @ApiOperation(value = "药品字典列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "药品名", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String")
    })
    @PostMapping("getDrugsList")
    public Map<String, Object> getDrugsList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int pageindex = ModelUtil.getInt(params, "pageindex", 1);
        int pagesize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", drugsService.getDrugsList(name, pageindex, pagesize));
        setOkResult(result, "查询成功");
        return result;
    }
}
