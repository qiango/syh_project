package com.syhdoctor.webserver.controller.webadmin.lecturer;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.lecturer.LecturerService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/Admin/Lecturer 讲师管理")
@RestController
@RequestMapping("/Admin/Lecturer")
public class AdminLecturerController extends BaseController {


    @Autowired
    private LecturerService lecturerService;


    /**
     * 添加讲师和修改讲师信息
     *
     * @param
     * @return
     */
    @ApiOperation(value = "添加讲师和修改讲师信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "文章id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "name", value = "name", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "photo", value = "头像", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = "手机号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "titlename", value = "职称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "hospital", value = "医院", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "department", value = "科室", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "expertise", value = "擅长", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "abstracts", value = "简介", required = true, dataType = "String"),
            //@ApiImplicitParam(paramType = "query", name = "doctorId", value = "文章id", required = true, dataType = "String"),
    })
    @PostMapping("/addUpdateLecture")
    public Map<String, Object> addUpdateLecture(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int id = ModelUtil.getInt(params, "id", 0);
        String name = ModelUtil.getStr(params, "name");
        String photo = ModelUtil.getStr(params, "photo");
        String phone = ModelUtil.getStr(params, "phone");
        String titleName = ModelUtil.getStr(params, "titlename");
        String hospital = ModelUtil.getStr(params, "hospital");
        String department = ModelUtil.getStr(params, "department");
        String expertise = ModelUtil.getStr(params, "expertise");
        String abstracts = ModelUtil.getStr(params, "abstracts");
        result.put("data", lecturerService.addUpdateLecture(id, name, photo, phone, titleName, hospital, department, expertise, abstracts, 0));
        setOkResult(result, "查询成功!");
        return result;
    }


    /**
     * 查询讲师列表
     *
     * @param
     * @return
     */
    @ApiOperation(value = "查询讲师列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "name", value = "姓名",  dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "phone", value = "手机",  dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String"),
    })
    @PostMapping("/getLecturerInfoList")
    public Map<String, Object> getLecturerInfoList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String name = ModelUtil.getStr(params, "name");
        String phone = ModelUtil.getStr(params, "phone");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", lecturerService.getLecturerInfoList(name, phone, pageIndex, pageSize));
        result.put("total", lecturerService.getLecturerInfoTotal(name, phone));
        setOkResult(result, "查询成功!");
        return result;
    }

    /**
     * 查询讲师列表
     *
     * @param
     * @return
     */
    @ApiOperation(value = "查询讲师详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "讲师ID", required = true, dataType = "String")
    })
    @PostMapping("/getLecturerInfoById")
    public Map<String, Object> getLecturerInfoById(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int id = ModelUtil.getInt(params, "id", 0);
        if (id == 0) {
            setErrorResult(result, "请检查参数");
        }
        result.put("data", lecturerService.getLecturerInfoById(id));
        setOkResult(result, "查询成功!");
        return result;
    }


}
