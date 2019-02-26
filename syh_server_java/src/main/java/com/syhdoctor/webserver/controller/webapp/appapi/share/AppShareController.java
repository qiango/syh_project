package com.syhdoctor.webserver.controller.webapp.appapi.share;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.share.ShareService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/App/Share app分享")
@RestController
@RequestMapping("/App/Share")
public class AppShareController extends BaseController {

    @Autowired
    private ShareService shareService;

    @ApiOperation(value = "获取分享内容")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "sharetype", value = "分享类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sharetypeid", value = "分享类型id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
    })
    @PostMapping("/getShareContent")
    public Map<String, Object> getShareContent(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int sharetype = ModelUtil.getInt(params, "sharetype");
        long sharetypeid = ModelUtil.getLong(params, "sharetypeid");
        long userid = ModelUtil.getLong(params, "userid");
        if (sharetype == 0) {
            setErrorResult(result, "参数错误");
        } else {
            result.put("data", shareService.getShareContent(userid, sharetype, sharetypeid));
            setOkResult(result, "查询成功");
        }
        return result;
    }

    @ApiOperation(value = "添加分享记录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "sharetype", value = "分享类型", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sharetypeid", value = "分享类型id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userid", value = "用户id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "shareid", value = "分享id", dataType = "String"),
    })
    @PostMapping("/addShareContent")
    public Map<String, Object> addShareContent(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int sharetype = ModelUtil.getInt(params, "sharetype");
        long sharetypeid = ModelUtil.getLong(params, "sharetypeid");
        long userid = ModelUtil.getLong(params, "userid");
        long shareid = ModelUtil.getLong(params, "shareid");
        if (sharetype == 0) {
            setErrorResult(result, "参数错误");
        } else {
            shareService.addShareContent(userid, sharetype, sharetypeid, shareid);
            setOkResult(result, "查询成功");
        }
        return result;
    }
}
