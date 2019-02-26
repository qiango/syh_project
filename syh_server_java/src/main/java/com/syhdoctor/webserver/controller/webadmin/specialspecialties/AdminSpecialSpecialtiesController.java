package com.syhdoctor.webserver.controller.webadmin.specialspecialties;


import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.specialspecialties.SpecialSpecialtiesService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Api(description = "/Admin/specialspecialties 特色专科")
@RequestMapping("/Admin/specialspecialties")
public class AdminSpecialSpecialtiesController extends BaseController {

    @Autowired
    private SpecialSpecialtiesService specialSpecialtiesService;

    @ApiOperation(value = "特色专科列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getSpecialSpecialtiesList")
    public Map<String, Object> getSpecialSpecialtiesList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        int pageIndex = ModelUtil.getInt(params, "pageIndex", 1);
        int pageSize = ModelUtil.getInt(params, "pageSize", 20);
        result.put("data", specialSpecialtiesService.getSpecialSpecialtiesList(id, pageIndex, pageSize));
        result.put("total", specialSpecialtiesService.getSpecialSpecialtiesListCount(id));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "特色专科详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
    })
    @PostMapping("/getSpecialSpecialtiesId")
    public Map<String, Object> getSpecialSpecialtiesId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", specialSpecialtiesService.getSpecialSpecialtiesId(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "特色专科症状类别")
    @PostMapping("/getCommonDiseaseSymptomsType")
    public Map<String, Object> getCommonDiseaseSymptomsType(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", specialSpecialtiesService.getCommonDiseaseSymptomsType());
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "删除特色专科")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
    })
    @PostMapping("/delSpecialSpecialties")
    public Map<String, Object> delSpecialSpecialties(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", specialSpecialtiesService.delSpecialSpecialties(id));
            setOkResult(result, "删除成功");
        }
        return result;
    }


    @ApiOperation(value = "修改or新增特色专科")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "picture", value = "图片", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "symptomtype", value = "症状类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "complextext", value = "复文本", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "color", value = "颜色", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "buttontext", value = "按钮文字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "headname", value = "头部文字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "backgroundpicture", value = "背景图片", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "islogin", value = "是否需要登录(0:是，1否)", dataType = "String"),
    })
    @PostMapping("/updateAddSpecialSpecialties")
    public Map<String, Object> updateAddSpecialSpecialties(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String picture = ModelUtil.getStr(params, "picture");
        int symptomtype = ModelUtil.getInt(params, "symptomtype");
        int sort = ModelUtil.getInt(params, "sort");
        String complextext = ModelUtil.getStr(params, "complextext");
        String color = ModelUtil.getStr(params, "color");
        String buttontext = ModelUtil.getStr(params, "buttontext");
        String headname = ModelUtil.getStr(params, "headname");
        String backgroundpicture = ModelUtil.getStr(params, "backgroundpicture");
        int islogin = ModelUtil.getInt(params, "islogin");
        result.put("data", specialSpecialtiesService.updateAddSpecialSpecialties(islogin, id, picture, symptomtype, sort, complextext, color, buttontext, headname, backgroundpicture));
        setOkResult(result, "修改成功");
        return result;
    }


}













