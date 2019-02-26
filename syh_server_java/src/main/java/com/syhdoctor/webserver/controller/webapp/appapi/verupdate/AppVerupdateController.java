package com.syhdoctor.webserver.controller.webapp.appapi.verupdate;

import com.syhdoctor.common.utils.ModelUtil;
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
@Api(description = "/App/Verupdate 意见反馈接口")
@RequestMapping("/App/Verupdate")
public class AppVerupdateController extends BaseController {

    @Autowired
    private VerupdateService verupdateService;

    @ApiOperation(value = "检查更新")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "phonetype", value = "1:Android,2:ios", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "system", value = "1:用户端，2：医生端", required = true, dataType = "String"),
    })
    @PostMapping("/checkUpdate")
    public Map<String, Object> checkUpdate(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int type = ModelUtil.getInt(params, "phonetype");
        int system = ModelUtil.getInt(params, "system");
        result.put("data", verupdateService.checkUpdate(type, system));
        setOkResult(result, "查询成功");
        return result;
    }
}
