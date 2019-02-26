package com.syhdoctor.webserver.controller.webadmin.mongo;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.mongo.MongoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/Admin/Mongo 埋点数据")
@RestController
@RequestMapping("/Admin/Mongo")
public class AdminMongoController extends BaseController {

    @Autowired
    private MongoService mongoService;

    @ApiOperation(value = "分享列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "开始页", defaultValue = "1", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "每页条数", defaultValue = "20", dataType = "String"),
    })
    @PostMapping("/getShareList")
    public Map<String, Object> getShareList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        Page page = mongoService.shareList(pageIndex, pageSize);
        result.put("data", page.getTotalElements());
        result.put("total", page.getTotalElements());
        setOkResult(result);
        return result;
    }
}
