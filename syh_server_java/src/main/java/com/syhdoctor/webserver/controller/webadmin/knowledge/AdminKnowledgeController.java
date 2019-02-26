package com.syhdoctor.webserver.controller.webadmin.knowledge;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.knowledge.KnowledgeService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/Admin/Knowledge 后台健康知识库")
@RestController
@RequestMapping("/Admin/Knowledge")
public class AdminKnowledgeController extends BaseController {

    @Autowired
    private KnowledgeService knowledgeService;

    @ApiOperation(value = "常见疾病列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "疾病名或者首字母", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getDiseaseList")
    public Map<String, Object> getDiseaseList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", knowledgeService.getAdminDiseaseList(name, pageIndex, pageSize));
        result.put("total", knowledgeService.getAdminDiseaseCount(name));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "常见疾病详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "模板id", dataType = "String"),
    })
    @PostMapping("/getDisease")
    public Map<String, Object> getDisease(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", knowledgeService.getAdminDisease(id));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "删除常见疾病")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "模板id", dataType = "String"),
    })
    @PostMapping("/delDisease")
    public Map<String, Object> delDisease(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        long createUser = ModelUtil.getLong(params, "agentid");
        result.put("data", knowledgeService.delDisease(id, createUser));
        setOkResult(result, "删除成功");
        return result;
    }


    @ApiOperation(value = "添加修改常见疾病")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "疾病名", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "summary", value = "概要", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "information", value = "基本信息", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "knowledge", value = "疾病知识", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "classification", value = "分类", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "clinical", value = "临床表现", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "inspect", value = "检查", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "diagnosis", value = "诊断", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "treatment", value = "治疗方案", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "prevention", value = "预防", dataType = "String"),
    })
    @PostMapping("/addUpdateDisease")
    public Map<String, Object> addUpdateDisease(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String name = ModelUtil.getStr(params, "name");
        String summary = ModelUtil.getStr(params, "summary");
        String information = ModelUtil.getStr(params, "information");
        String knowledge = ModelUtil.getStr(params, "knowledge");
        String classification = ModelUtil.getStr(params, "classification");
        String clinical = ModelUtil.getStr(params, "clinical");
        String inspect = ModelUtil.getStr(params, "inspect");
        String diagnosis = ModelUtil.getStr(params, "diagnosis");
        String treatment = ModelUtil.getStr(params, "treatment");
        String prevention = ModelUtil.getStr(params, "prevention");
        long createUser = ModelUtil.getLong(params, "agentid");
        result.put("data", knowledgeService.addUpdateDisease(id, name, summary, information, knowledge, classification, clinical, inspect, diagnosis, treatment, prevention, createUser));
        setOkResult(result, "查询成功");
        return result;
    }
}
