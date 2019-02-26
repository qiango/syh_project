package com.syhdoctor.webserver.controller.webadmin.specialistcounseling;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.specialistcounseling.SpecialistCounselingService;
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

@RestController
@Api(description = "/Admin/specialistcounseling 专病咨询")
@RequestMapping("/Admin/specialistcounseling")
public class AdminSpecialistCounselingController extends BaseController {

    @Autowired
    private SpecialistCounselingService specialistCounselingService;


    @ApiOperation(value = "专病咨询列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getSpecialistCounselingList")
    public Map<String, Object> getSpecialistCounselingList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        int pageIndex = ModelUtil.getInt(params, "pageIndex", 1);
        int pageSize = ModelUtil.getInt(params, "pageSize", 20);
        result.put("data", specialistCounselingService.getSpecialistCounselingList(id, pageIndex, pageSize));
        result.put("total", specialistCounselingService.getSpecialistCounselingListCount(id));
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "特色专科症状类别")
    @PostMapping("/getSymptomsType")
    public Map<String, Object> getSymptomsType(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", specialistCounselingService.getSymptomsType());
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "专病咨询详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
    })
    @PostMapping("/getSpecialistCounselingId")
    public Map<String, Object> getSpecialistCounselingId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", specialistCounselingService.getSpecialistCounselingId(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }


    @ApiOperation(value = "删除专病咨询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
    })
    @PostMapping("/delSpecialistCounseling")
    public Map<String, Object> delSpecialistCounseling(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", specialistCounselingService.delSpecialistCounseling(id));
            setOkResult(result, "删除成功");
        }
        return result;
    }


    @ApiOperation(value = "修改or新增专病咨询")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "picture", value = "图片", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "complextext", value = "复文本", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "color", value = "颜色", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "buttontext", value = "按钮文字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "headname", value = "头部文字", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "backgroundpicture", value = "背景图片", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "islogin", value = "是否需要登录(0:是，1否)", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "symptomtype", value = "症状类型", dataType = "String"),
    })
    @PostMapping("/updateAddSpecialistCounseling")
    public Map<String, Object> updateAddSpecialistCounseling(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String picture = ModelUtil.getStr(params, "picture");
        int sort = ModelUtil.getInt(params, "sort");
        String complextext = ModelUtil.getStr(params, "complextext");
        String color = ModelUtil.getStr(params, "color");
        String buttontext = ModelUtil.getStr(params, "buttontext");
        String headname = ModelUtil.getStr(params, "headname");
        String backgroundpicture = ModelUtil.getStr(params, "backgroundpicture");
        int islogin = ModelUtil.getInt(params, "islogin");
        List<?> symptomtype = ModelUtil.getList(params,"symptomtype",new ArrayList<>());
        result.put("data", specialistCounselingService.updateAddSpecialistCounseling(id, picture, sort, complextext, color, buttontext, headname, backgroundpicture, islogin,symptomtype));
        setOkResult(result, "修改成功");
        return result;
    }
}
