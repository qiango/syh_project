package com.syhdoctor.webserver.controller.webadmin.focusfigure;

import com.syhdoctor.common.utils.EnumUtils.TypeNameBannerEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.focusfigure.FocusfigureService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(description = "/Admin/Focusfigure 添加banner图")
@RestController
@RequestMapping("/Admin/Focusfigure")
public class AdminFocusfigureController extends BaseController {


    @Autowired
    private FocusfigureService focusfigureService;

    @ApiOperation(value = "banner位置树")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "albumid", value = "显示位置", required = true, dataType = "String")
    })
    @PostMapping("/getAlbumDropList")
    public Map<String, Object> getAlbumDropList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int albumid = ModelUtil.getInt(params, "albumid", 0);
        result.put("data", focusfigureService.getAlbumDropList(albumid));
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "宽高")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "albumid", value = "显示位置", required = true, dataType = "String")
    })
    @PostMapping("/getAlbumDropListId")
    public Map<String, Object> getAlbumDropListId(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int albumid = ModelUtil.getInt(params, "albumid", 0);
        if(albumid == 0){
            setErrorResult(result,"参数错误");
        }else{
            result.put("data", focusfigureService.getAlbumDropListId(albumid));
            setOkResult(result, "查询成功!");
        }
        return result;
    }


    @ApiOperation(value = "查询广告列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "albumid", value = "显示位置", required = true, dataType = "String")
    })
    @PostMapping("/getFocusfigureList")
    public Map<String, Object> getFocusfigureList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int albumid = ModelUtil.getInt(params, "albumid");
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", focusfigureService.getFocusfigureList(albumid, pageIndex, pageSize));
        result.put("total", focusfigureService.getFocusfigureListTotal(albumid));
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "查询是用用户端还是医生端")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "albumid", value = "显示位置", required = true, dataType = "String")
    })
    @PostMapping("/getType")
    public Map<String, Object> getType(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int albumid = ModelUtil.getInt(params, "albumid");
        result.put("data", focusfigureService.getTypes(albumid));
        setOkResult(result, "查询成功!");
        return result;
    }



    @ApiOperation(value = "查询广告列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "fousfigureid", value = "广告ID", required = true, dataType = "String")
    })
    @PostMapping("/getFocusfigureById")
    public Map<String, Object> getFocusfigureById(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int fousfigureId = ModelUtil.getInt(params, "fousfigureid");
        result.put("data", focusfigureService.getFocusfigureById(fousfigureId));
        setOkResult(result, "查询成功!");
        return result;
    }


    @ApiOperation(value = "根据栏目查询大小图片尺寸")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "albumid", value = "栏目id", required = true, dataType = "String")
    })
    @PostMapping("/getImageSize")
    public Map<String, Object> getImageSize(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int albumid = ModelUtil.getInt(params, "albumid");
        result.put("data", focusfigureService.getImageSize(albumid));
        setOkResult(result, "查询成功!");
        return result;
    }

    @ApiOperation(value = "删除广告")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "fousfigureid", value = "广告ID", required = true, dataType = "String")
    })
    @PostMapping("/deleteFousfigure")
    public Map<String, Object> deleteFousfigure(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int fousfigureId = ModelUtil.getInt(params, "fousfigureid");
        int agentId = ModelUtil.getInt(params, "agentid", 1);
        result.put("data", focusfigureService.deleteFousfigure(fousfigureId, agentId));
        setOkResult(result, "删除成功!");
        return result;
    }


    /**
     * 添加banner图
     *
     * @param params
     */
    @ApiOperation(value = "添加banner")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "albumid", value = "显示位置", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "focusfigureid", value = "广告图ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "bigimg", value = "大图", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "smallimg", value = "小图", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "instructions", value = "说明", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "type", value = "类型", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "typename", value = "跳转", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sharepic", value = "分享图片", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sharetitle", value = "分享标题", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sharecontent", value = "分享内容", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登录人ID", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "starttime", value = "开始时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "endtime", value = "结束时间", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "cycle", value = "周期", dataType = "String")
    })
    @PostMapping("/addUpdateFocusfigure")
    public Map<String, Object> addUpdateFocusfigure(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int focusfigureid = ModelUtil.getInt(params, "focusfigureid");
        int albumId = ModelUtil.getInt(params, "albumid", 0);
        String bigImg = ModelUtil.getStr(params, "bigimg");
        String smallImg = ModelUtil.getStr(params, "smallimg");
        String instructions = ModelUtil.getStr(params, "instructions");
        int type = ModelUtil.getInt(params, "type", 0);
        String typeName = ModelUtil.getStr(params, "typename");
        int sort = ModelUtil.getInt(params, "sort", 0);
        String sharePic = ModelUtil.getStr(params, "sharepic");
        String shareTitle = ModelUtil.getStr(params, "sharetitle");
        String shareContent = ModelUtil.getStr(params, "sharecontent");
        Long starttime = ModelUtil.getLong(params, "starttime", null);
        Long endtime = ModelUtil.getLong(params, "endtime", null);
        int agentId = ModelUtil.getInt(params, "agentid");
        int cycle = ModelUtil.getInt(params, "cycle");
        if (type == TypeNameBannerEnum.webLink.getCode() && StrUtil.isEmpty(typeName) && !StrUtil.isHttpUrl(typeName)) {
            setErrorResult(result, "请检查参数");
        } else if (type == TypeNameBannerEnum.microclassDetail.getCode() && StrUtil.isEmpty(typeName)) {
            setErrorResult(result, "请检查参数");
        } else if (albumId == 0 || StrUtil.isEmpty(bigImg) || type == 0) {
            setErrorResult(result, "请检查参数");
        } else {
            result.put("data", focusfigureService.addUpdateFocusfigure(cycle,albumId, bigImg, smallImg, instructions, type, typeName, sort, sharePic, shareTitle, shareContent, agentId, focusfigureid, starttime, endtime));
            setOkResult(result, "操作成功!");
        }
        return result;
    }
}
