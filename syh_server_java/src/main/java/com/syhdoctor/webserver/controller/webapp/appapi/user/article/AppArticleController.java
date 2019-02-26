package com.syhdoctor.webserver.controller.webapp.appapi.user.article;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.article.ArticleService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@Api(description = "/App/Article 咨询APP接口")
@RestController
@RequestMapping("/App/Article")
public class AppArticleController extends BaseController {
    @Autowired
    private ArticleService articleService;


    /**
     * 康养头条页面
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "康养头条页面")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "pageindex", value = "当前页", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "pagesize", value = "当前页条数", required = true, dataType = "String")
    })
    @PostMapping("/addFocusfigure")
    public Map<String, Object> getArticleList(@ApiParam(hidden = true) @RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        int pageIndex = ModelUtil.getInt(params, "pageindex", 1);
        int pageSize = ModelUtil.getInt(params, "pagesize", 20);
        try {
            result.put("data", articleService.getHomePageArticleList(pageIndex, pageSize));
            setOkResult(result, "查询成功!");
        } catch (Exception e) {
            setErrorResult(result, e.getMessage());
        }
        log.info("Web>Article>", result);
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
        try {
            long articleId = ModelUtil.getInt(params, "articleid", 0);
            if (articleId == 0) {
                setErrorResult(result, "请检查参数");
            } else {
                Map<String, Object> value = articleService.getArticleById(articleId);
                if (value != null) {
                    articleService.updateArticleClick(articleId);//添加点击量
                    result.put("data", value);
                    setOkResult(result, "查询成功!");
                } else {
                    setErrorResult(result, "查询失败!");
                }
            }
        } catch (Exception e) {
            setErrorResult(result, e.getMessage());
        }
        log.info("Web>Article>", result);
        return result;
    }
}
