package com.syhdoctor.webserver.controller.webadmin.vipcard;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.vipcard.EnjoyLevelService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Api(description = "/Admin/EnjoyLevel 尊享等级")
@RestController
@RequestMapping("/Admin/EnjoyLevel")
public class AdminEnjoyLevelController extends BaseController {

    @Autowired
    private EnjoyLevelService enjoyLevelService;

    @ApiOperation(value = "尊享等级列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "level", value = "等级", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageIndex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getLevelList")
    public Map<String, Object> getLevelList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        int level = ModelUtil.getInt(params, "level");
        int pageIndex = ModelUtil.getInt(params, "pageIndex", 1);
        int pageSize = ModelUtil.getInt(params, "pageSize", 20);

        result.put("data", enjoyLevelService.getLevelList(id, level, pageIndex, pageSize));
        result.put("total", enjoyLevelService.getLevelListCount(id, level));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "尊享等级详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/getLevelListId")
    public Map<String, Object> getLevelListId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", enjoyLevelService.getLevelListId(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "修改或新增尊享等级")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "level", value = "等级", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "currentintegral", value = "尊享值", dataType = "String"),
    })
    @PostMapping("/updateAddLevel")
    public Map<String, Object> updateAddLevel(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        int level = ModelUtil.getInt(params, "level");
        int currentintegral = ModelUtil.getInt(params, "currentintegral");
        result.put("data", enjoyLevelService.updateAddLevel(id, level, currentintegral));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "删除尊享等级")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/delLevel")
    public Map<String, Object> delLevel(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", enjoyLevelService.delLevel(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    /*
    尊享值
     */
    @ApiOperation(value = "尊享值列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageIndex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getEnjoyValueList")
    public Map<String, Object> getEnjoyValueList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        int pageIndex = ModelUtil.getInt(params, "pageIndex", 1);
        int pageSize = ModelUtil.getInt(params, "pageSize", 20);
        result.put("data", enjoyLevelService.getEnjoyValueList(id, pageIndex, pageSize));
        result.put("total", enjoyLevelService.getEnjoyValueListCount(id));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "尊享值详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/getEnjoyValueListId")
    public Map<String, Object> getEnjoyValueListId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", enjoyLevelService.getEnjoyValueListId(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "修改或新增尊享等级")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "currentintegral", value = "等级", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "price", value = "尊享值", dataType = "String"),
    })
    @PostMapping("/updateAddEnjoyValue")
    public Map<String, Object> updateAddEnjoyValue(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        int currentintegral = ModelUtil.getInt(params, "currentintegral");
        BigDecimal price = ModelUtil.getDec(params, "price", BigDecimal.ZERO);
        result.put("data", enjoyLevelService.updateAddEnjoyValue(id, currentintegral, price));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "删除尊享值")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/delEnjoyValue")
    public Map<String, Object> delEnjoyValue(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", enjoyLevelService.delEnjoyValue(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    /*
    尊享类别
     */

    @ApiOperation(value = "尊享类别列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "type", value = "尊享类别", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageIndex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getEnjoyTypeList")
    public Map<String, Object> getEnjoyTypeList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int type = ModelUtil.getInt(params, "type");
        int pageIndex = ModelUtil.getInt(params, "pageIndex", 1);
        int pageSize = ModelUtil.getInt(params, "pageSize", 20);
        result.put("data", enjoyLevelService.getEnjoyTypeList(type, pageIndex, pageSize));
        result.put("total", enjoyLevelService.getEnjoyTypeListCount(type));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "尊享类别详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/getEnjoyTypeListId")
    public Map<String, Object> getEnjoyTypeListId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", enjoyLevelService.getEnjoyTypeListId(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "修改或新增尊享类别")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "type", value = "尊享类别", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "typedescribe", value = "类别描述", dataType = "String"),
    })
    @PostMapping("/updateAddEnjoyType")
    public Map<String, Object> updateAddEnjoyType(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        int type = ModelUtil.getInt(params, "type");
        String typedescribe = ModelUtil.getStr(params, "typedescribe");
        result.put("data", enjoyLevelService.updateAddEnjoyType(id, type, typedescribe));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "删除尊享类别")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
    })
    @PostMapping("/delEnjoyType")
    public Map<String, Object> delEnjoyType(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", enjoyLevelService.delEnjoyType(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }


}
