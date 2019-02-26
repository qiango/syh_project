package com.syhdoctor.webserver.controller.webadmin.code;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.code.CodeService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/Admin/Code 字典")
@RestController
@RequestMapping("/Admin/Code")
public class AdminCodeController extends BaseController {

    @Autowired
    private CodeService codeService;


    @ApiOperation(value = "药品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "药品名字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getDrugsList")
    public Map<String, Object> getDrugsList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", codeService.getDrugsList(name, pageIndex, pageSize));
        result.put("total", codeService.getDrugsCount(name));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "药品列表详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "药品id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getDrugsListId")
    public Map<String, Object> getDrugsListId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", codeService.getDrugsListId(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "药品剂型下拉框")
    @ApiImplicitParams({
    })
    @PostMapping("/codeDrugsDosageList")
    public Map<String, Object> codeDrugsDosageList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", codeService.codeDrugsDosageList());
        setOkResult(result, "成功");
        return result;
    }
    @ApiOperation(value = "修改药品")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "药品id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "drugformid", value = "药品剂型id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "drugcode", value = "国药准字号编码", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "durgname", value = "国药准字号对应名称", dataType = "String"),
    })
    @PostMapping("/updateDrug")
    public Map<String, Object> updateDrug(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params,"id");
        int drugformid = ModelUtil.getInt(params,"drugformmap");
        int drugcode = ModelUtil.getInt(params,"drugcode");
        String durgname = ModelUtil.getStr(params,"durgname");
        result.put("data", codeService.updateDrugs(id,drugformid, drugcode, durgname));
        setOkResult(result, "查询成功");
        return result;
    }


    @ApiOperation(value = "药品详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "药品id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getDrugs")
    public Map<String, Object> getDrugs(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        result.put("data", codeService.getDrugs(id));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "药品修改")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "药品id", dataType = "String"),
    })
    @PostMapping("/updateDrugs")
    public Map<String, Object> updateDrugs(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String drug_form = ModelUtil.getStr(params, "drug_form");
        String standard_desc = ModelUtil.getStr(params, "standard_desc");
        String totaldosage_unit = ModelUtil.getStr(params, "totaldosage_unit");
        String unit = ModelUtil.getStr(params, "unit");
        String manufacturing_enterprise = ModelUtil.getStr(params, "manufacturing_enterprise");
        String bidding_enterprise = ModelUtil.getStr(params, "bidding_enterprise");
        String catalog_category = ModelUtil.getStr(params, "catalog_category");
        String procurement_category = ModelUtil.getStr(params, "procurement_category");
        long agentId = ModelUtil.getLong(params, "agentid");
        if (id == 0 || StrUtil.isEmpty(drug_form, standard_desc, totaldosage_unit)) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", codeService.updateDrugs(id, drug_form, standard_desc, totaldosage_unit, unit, manufacturing_enterprise, bidding_enterprise, catalog_category, procurement_category, agentId));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    @ApiOperation(value = "职称列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "职称名字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getTitleList")
    public Map<String, Object> getTitleList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", codeService.getTitleList(name, pageIndex, pageSize));
        result.put("tatal", codeService.getTitleCount(name));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "科室列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "职称名字", dataType = "String"),
    })
    @PostMapping("/getDepartmentList")
    public Map<String, Object> getDepartmentList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        result.put("data", codeService.getAllDepartmentList(name));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "科室类型树")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "职称名字", dataType = "String"),
    })
    @PostMapping("/getTypeTree")
    public Map<String, Object> getTypeTree(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long departid = ModelUtil.getLong(params, "departid");
        result.put("data", codeService.getTypeTree(departid));
        setOkResult(result, "查询成功");
        return result;
    }


//    @ApiOperation(value = "科室列表11")
//    @ApiImplicitParams({
//    })
//    @PostMapping("/addDrugs")
//    public Map<String, Object> addDrugs(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
//        Map<String, Object> result = new HashMap<>();
//        result.put("data", codeService.adddrugs());
//        setOkResult(result, "查询成功");
//        return result;
//    }
}
