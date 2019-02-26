package com.syhdoctor.webserver.controller.webadmin.tag;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api("/Admin/Tag 标签管理")
@RestController
@RequestMapping("/Admin/Tag")
public class TagController extends BaseController {


    @Autowired
    private com.syhdoctor.webserver.service.Tag.TagService tagService;

    /**
     * 标签列表
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "标签名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", dataType = "String"),
    })
    @PostMapping("/getTagList")
    public Map<String, Object> getTagList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        String name = ModelUtil.getStr(params, "name");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", tagService.getTagList(name, pageIndex, pageSize));
        result.put("total", tagService.getTagTotal(name));
        setOkResult(result, "查询成功!");
        log.info("admin>tag>getTagList" + result);
        return result;
    }

    /**
     * 标签详情
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "标签详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "tagid", value = "标签Id", dataType = "String")
    })
    @PostMapping("/getTagById")
    public Map<String, Object> getTagById(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            int tagId = ModelUtil.getInt(params, "tagid");
            if (tagId == 0) {
                setErrorResult(result, "请检查参数!");
            } else {
                result.put("data", tagService.getTabById(tagId));
            }
            setOkResult(result, "查询成功!");
        } catch (Exception e) {
            setErrorResult(result, e.getMessage());
        }
        log.info("admin/tag/getTagById" + result);
        return result;
    }

    /**
     * 添加修改标签
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "添加修改标签")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "标签Id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "标签名称", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登录人ID", dataType = "String")
    })
    @PostMapping("/addUpdateTag")
    public Map<String, Object> addUpdateTag(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            int tagId = ModelUtil.getInt(params, "id");
            String name = ModelUtil.getStr(params, "name");
            int sort = ModelUtil.getInt(params, "sort");
            int agentId = ModelUtil.getInt(params, "agentid");
            if (tagId == 0) {
                tagService.addTag(name, sort, agentId);
                setOkResult(result, "添加成功!");
            } else {
                tagService.updateTag(name, sort, agentId, tagId);
                setOkResult(result, "修改成功!");
            }
        } catch (Exception e) {
            setErrorResult(result, e.getMessage());
        }
        log.info("admin>tag>addupdateTag" + result);
        return result;
    }

    @ApiOperation(value = "删除标签")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "tagid", value = "标签Id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登录人ID", dataType = "String")
    })
    @PostMapping("/deleteTag")
    public Map<String, Object> deleteTag(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            int tagId = ModelUtil.getInt(params, "tagid");
            int anentId = ModelUtil.getInt(params, "agentid");
            if (tagId == 0) {
                setErrorResult(result, "请检查参数!");
            } else {
                tagService.deleteTag(tagId, anentId);
                setOkResult(result, "删除成功!");
            }
        } catch (Exception e) {
            setErrorResult(result, getClass().getName());
        }
        return result;
    }
}
