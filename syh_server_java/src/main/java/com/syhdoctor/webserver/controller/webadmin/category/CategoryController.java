package com.syhdoctor.webserver.controller.webadmin.category;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.category.CategoryService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(value = "/Admin/Category 文章分类管理")
@RestController
@RequestMapping("/Admin/Category")
public class CategoryController extends BaseController {


    @Autowired
    private CategoryService categoryService;

    /**
     * 下拉框查询
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "分类下拉框查询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "分类名称", required = true, dataType = "String")
    })
    @PostMapping("/getCategoryDropList")
    public Map<String, Object> getCategoryDropList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        result.put("data", categoryService.getCategoryDropList(name));
        setOkResult(result, "查找成功!");

        log.info("/admin/Category/getCategoryDropList", result);
        return result;
    }

    /**
     * 删除分类
     *
     * @return
     */
    @ApiOperation(value = "删除分类")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "分类id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登陆人id", dataType = "String")
    })
    @PostMapping("/deleteCategory")
    public Map<String, Object> deleteCategory(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int id = ModelUtil.getInt(params, "id", 0);
        int agentid = ModelUtil.getInt(params, "agentid", 0);
        if (id == 0) {
            setErrorResult(result, "请检查参数");
        } else {
            categoryService.deleteCategory(id, agentid);
            setOkResult(result, "删除成功!");
        }
        log.info("AdminRechargeableOrderMapper>Category>deleteCategory", result);
        return result;
    }

    /**
     * 查询分类列表
     *
     * @return
     */
    @ApiOperation(value = "分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "分类名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String")
    })
    @PostMapping("/getCategoryList")
    public Map<String, Object> getCategoryList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", categoryService.getCategoryList(name, pageIndex, pageSize));
        result.put("total", categoryService.getCategoryListTotal(name));
        setOkResult(result, "查找成功!");
        log.info("/admin/Category/getCategoryList", result);
        return result;
    }

    /**
     * 查询分类
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "查询单条数据")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "分类id", required = true, dataType = "String")
    })
    @PostMapping("/getCategoryById")
    public Map<String, Object> getCategoryById(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int id = ModelUtil.getInt(params, "id", 0);
        if (id == 0) {
            setErrorResult(result, "请检查参数");
        } else {
            Map<String, Object> value = categoryService.getCategoryById(id);
            if (value != null) {
                result.put("data", value);
                setOkResult(result, "查询成功!");
            } else {
                setErrorResult(result, "查询失败!");
            }
        }
        log.info("/AdminRechargeableOrderMapper/Category>getCategoryById", result);
        return result;
    }

    /**
     * 新增修改分类
     */
    @ApiOperation(value = "新增修改分类")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "分类名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pid", value = "父分类id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "id", value = "分类ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登陆人id", dataType = "String")
    })
    @PostMapping("/addUpdateCategory")
    public Map<String, Object> addUpdateCategory(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int sort = ModelUtil.getInt(params, "sort");
        int pid = ModelUtil.getInt(params, "pid");
        int agentId = ModelUtil.getInt(params, "agentid");
        int id = ModelUtil.getInt(params, "id");
        if (StrUtil.isEmpty(name)) {
            setErrorResult(result, "请检查参数!");
        } else {
            categoryService.addUpdateCategory(name, sort, pid, agentId, id);
            setOkResult(result, "保存成功!");
        }
        return result;
    }
}
