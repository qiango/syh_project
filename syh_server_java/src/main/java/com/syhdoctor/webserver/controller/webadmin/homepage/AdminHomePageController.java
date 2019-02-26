package com.syhdoctor.webserver.controller.webadmin.homepage;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.homepage.HomePageService;
import com.syhdoctor.webserver.service.system.SystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@Api(description = "/Admin/homepage 主页")
@RestController
@RequestMapping("/Admin/homepage")
public class AdminHomePageController extends BaseController {

    @Autowired
    private HomePageService homePageService;

    @Autowired
    private SystemService systemService;



    @ApiOperation(value = "主页用户基本数据")
    @PostMapping("/getUserStatistics")
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", homePageService.getUserStatistics());
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "统计年龄柱状图")
    @PostMapping("/getUserStatisticsAge")
    public Map<String, Object> getUserStatisticsAge() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", homePageService.getUserStatisticsAge());
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "地域")
    @PostMapping("/getRegionList")
    public Map<String, Object> getRegionList() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", homePageService.getRegionList());
        setOkResult(result, "查询成功");
        return result;
    }

    @ApiOperation(value = "test")
    @PostMapping("/addUserStatisticsRegion")
    public Map<String, Object> addUserStatisticsRegion() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", systemService.getExtractPage());
        setOkResult(result, "成功");
        return result;
    }


    @ApiOperation(value = "经营状况统计")
    @PostMapping("/getStatistics")
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", homePageService.getStatistics());
        setOkResult(result, "成功");
        return result;
    }

    @ApiOperation(value = "折线统计")
    @PostMapping("/getNumByOrderType")
    public Map<String, Object> getNumByOrderType(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int type= ModelUtil.getInt(params,"type");
        long startTime=ModelUtil.getLong(params,"startTime");
        long endTime=ModelUtil.getLong(params,"endTime");
        result.put("data", homePageService.getNum(type, startTime, endTime));
        setOkResult(result, "成功");
        return result;
    }

    @ApiOperation(value = "底部统计")
    @PostMapping("/getFinalCount")
    public Map<String, Object> getFinalCount(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        long startTime=ModelUtil.getLong(params,"startTime");
        long endTime=ModelUtil.getLong(params,"endTime");
        result.put("data", homePageService.getFinalCount(startTime, endTime));
        setOkResult(result, "成功");
        return result;
    }


}
