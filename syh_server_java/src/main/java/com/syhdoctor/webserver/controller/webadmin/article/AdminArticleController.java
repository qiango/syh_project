package com.syhdoctor.webserver.controller.webadmin.article;


import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.article.ArticleService;
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

@Api(value = "/Admin/Article 咨询文章管理")
@RestController
@RequestMapping("/Admin/Article")
public class AdminArticleController extends BaseController {


    @Autowired
    private ArticleService service;

    /**
     * 删除文章
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "删除文章")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "articleid", value = "文章id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登陆人id", dataType = "String")
    })
    @PostMapping("/deleteArticle")
    public Map<String, Object> deleteArticle(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int articleId = ModelUtil.getInt(params, "articleid", 0);
        int agentid = ModelUtil.getInt(params, "agentid", 0);
        if (articleId == 0) {
            setErrorResult(result, "请检查参数");
        } else {
            service.deleteArticle(articleId, agentid);
            setOkResult(result, "删除成功!");
        }
        log.info("admin>Article", result);
        return result;
    }

    /**
     * 文章详情
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "文章详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "articleid", value = "文章id", required = true, dataType = "String")
    })
    @PostMapping("/getArtileById")
    public Map<String, Object> getArtileById(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        int articleId = ModelUtil.getInt(params, "articleid", 0);
        if (articleId == 0) {
            setErrorResult(result, "请检查参数");
        } else {
            Map<String, Object> value = service.getArticleById(articleId);
            if (value != null) {
                result.put("data", value);
                setOkResult(result, "查询成功!");
            } else {
                setErrorResult(result, "查询失败!");
            }
        }
        log.info("admin>Article", result);
        return result;
    }

    /**
     * 新增修改文章
     *
     * @return
     */
    @ApiOperation(value = "新增修改文章")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "articleid", value = "文章id", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "author", value = "作者", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "title", value = "文章标题", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "subtitle", value = "副标题", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "releasetime", value = "发布时间", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "articledetail", value = "文章内容", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "articlepic", value = "文章图片", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "sort", value = "排序", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "agentid", value = "登陆人id", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "tagids", value = "标签ID", dataType = "List"),
            @ApiImplicitParam(paramType = "query", name = "whethervoice", value = "是否转语音", dataType = "String"),

    })
    @PostMapping("/addUpdateArticle")
    public Map<String, Object> addArticle(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int articleId = ModelUtil.getInt(params, "articleid");
        String author = ModelUtil.getStr(params, "author");
        String title = ModelUtil.getStr(params, "title");
        String subTitle = ModelUtil.getStr(params, "subtitle");
        long releaseTime = ModelUtil.getLong(params, "releasetime");
        String articledetail = ModelUtil.getStr(params, "articledetail");
        String articlepic = ModelUtil.getStr(params, "articlepic");
        int sort = ModelUtil.getInt(params, "sort");
        int agentId = ModelUtil.getInt(params, "agentid");
        List<?> categoryIds = ModelUtil.getList(params, "categoryids", new ArrayList<>());
        List<?> tagIds = ModelUtil.getList(params, "tagids", new ArrayList<>());
        int whetherVoice = ModelUtil.getInt(params, "whethervoice"); //是否转语音

        if (StrUtil.isEmpty(title, articledetail) || releaseTime == 0) {
            setErrorResult(result, "请检查参数");
        } else {
            service.addUpdateArticle(articleId, author, title, subTitle, releaseTime, articledetail, articlepic, sort, agentId, tagIds, categoryIds, whetherVoice);
            setOkResult(result, "保存成功!");
        }
        log.info("admin>Article", result);
        return result;
    }

    /**
     * 查询文章列表
     *
     * @return
     */
    @ApiOperation(value = "询文章列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "author", value = "作者", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "title", value = "文章标题", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "subtitle", value = "副标题", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "whethervoice", value = "是否有语音", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "分页", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "分页", dataType = "String")
    })
    @PostMapping("/getArticleList")
    public Map<String, Object> getArticleList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String title = ModelUtil.getStr(params, "title");
        String subTitle = ModelUtil.getStr(params, "subtitle");
        String author = ModelUtil.getStr(params, "author");
        int whethervoice = ModelUtil.getInt(params, "whethervoice", -1);
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        result.put("data", service.getArticleList(title, subTitle, author, whethervoice, pageIndex, pageSize));
        result.put("total", service.getArticleListTotal(title, subTitle, author, whethervoice));
        setOkResult(result, "查找成功!");
        log.info("/admin/Article/getArticleList", result);
        return result;
    }
}
