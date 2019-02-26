package com.syhdoctor.webserver.controller.webadmin.verupdate;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.verupdate.VerupdateService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Api(description = "/Admin/Verupdate 版本管理")
@RequestMapping("/Admin/Verupdate")
public class AdminVerupdateController extends BaseController {

    @Autowired
    private VerupdateService verupdateService;

    @ApiOperation(value = "版本列表")
    @ApiImplicitParams({
    })
    @PostMapping("/verupdateList")
    public Map<String, Object> verupdateList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", verupdateService.verupdateList());
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "更新")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "url", value = "跟新地址", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "vernumber", value = "版本号", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登陆人id", dataType = "String"),
    })
    @PostMapping("/updateVerupdate")
    public Map<String, Object> updateVerupdate(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        String url = ModelUtil.getStr(params, "url");
        String vernumber = ModelUtil.getStr(params, "vernumber");
        int identification = ModelUtil.getInt(params, "identification");
        long agentid = ModelUtil.getLong(params, "agentid");
        if (StrUtil.isEmpty(vernumber) || id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", verupdateService.updateVerupdate(id, url, vernumber,identification,agentid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "版本详细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "id", required = true, dataType = "String"),
    })
    @PostMapping("/getVerupdate")
    public Map<String, Object> getVerupdate(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long id = ModelUtil.getLong(params, "id");
        if (id == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", verupdateService.getVerupdate(id));
            setOkResult(result, "查询成功");
        }
        return result;
    }
}
