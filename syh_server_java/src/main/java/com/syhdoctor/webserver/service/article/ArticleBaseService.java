package com.syhdoctor.webserver.service.article;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.article.ArticleMapper;
import com.syhdoctor.webserver.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public abstract class ArticleBaseService extends BaseService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CategoryService categoryService;


    /**
     * 查询文章详情
     *
     * @param articleId
     * @return
     */
    @Cacheable(value = "Article", key = "#root.methodName+#root.args[0]")
    public Map<String, Object> getArticleById(long articleId) {
        Map<String, Object> result = articleMapper.getArticleById(articleId);
        result.put("categoryids", categoryService.getCategoryArticle(articleId));
        return result;
    }

    /**
     * 查询文章详情
     *
     * @param articleId
     * @return
     */
    public Map<String, Object> getArticle(long articleId) {
        return articleMapper.getArticleById(articleId);
    }


    public boolean updateArticleClick(long articleId) {
        return articleMapper.updateArticleClick(articleId);
    }

    /**
     * 查询咨询列表
     *
     * @return
     */
    @Cacheable(value = "Article", key = "#root.methodName+#root.args[0]+#root.args[1]")
    public List<Map<String, Object>> getHomePageArticleList(int pageIndex, int pageSize) {
        return getArticleList(pageIndex, pageSize);
    }


    /**
     * 后台咨询列表
     *
     * @param title     文章标题
     * @param subTitle  副标题
     * @param author    作者
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> getArticleList(String title, String subTitle, String author, int whethervoice, int pageIndex, int pageSize) {
        return articleMapper.getArticleList(title, subTitle, author, whethervoice, pageIndex, pageSize);
    }

    /**
     * 前台资讯列表
     *
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> getArticleList(int pageIndex, int pageSize) {
        return articleMapper.getArticleList(pageIndex, pageSize);
    }

    /**
     * 获取条数
     *
     * @param title    文章标题
     * @param subTitle 副标题
     * @param author   作者
     * @return
     */
    public long getArticleListTotal(String title, String subTitle, String author, int whethervoice) {
        return articleMapper.getArticleListTotal(title, subTitle, author, whethervoice);
    }

    /**
     * 添加文章
     *
     * @param articleId     文章ID
     * @param author        作者
     * @param title         标题
     * @param subTitle      副标题
     * @param articledetail 文章内容
     * @param articlepic    文章封面图片
     * @param agentId       登陆人id
     * @return long  文章ID
     */
    @Transactional
    @CacheEvict(value = "Article", allEntries = true)
    public long addUpdateArticle(long articleId, String author, String title, String subTitle, long releaseTime, String articledetail,
                                 String articlepic, int sort, int agentId,
                                 List<?> tagIds, List<?> categoryIds, int whetherVoice) {
        int track = 0;
        String key = "";
//        if (whetherVoice == 1) {
//            String articleNoHtmlDetail = StrUtil.delHTMLTag(articledetail).replaceAll("[&nbsp;]", "").replaceAll("[ ]", "");
//            log.info("addUpdateArticle >>[" + articleNoHtmlDetail + "]");
//            String fileName = UnixUtil.getCustomRandomString();
//            LongTtsUtil.saveTts(articleNoHtmlDetail, ConfigModel.BASEFILEPATH, fileName);
//            key = "syh" + UnixUtil.getCustomRandomString() + ".mp3";
//            try {
//                String localFileName = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_ARTICLE_PATH, fileName + ".mp3");
//                if (!StrUtil.isEmpty(localFileName) && FileUtil.validateFile(localFileName)) {
//                    QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, key, new FileInputStream(localFileName));
//                    MP3File mp3File = (MP3File) AudioFileIO.read(new File(localFileName));
//                    MP3AudioHeader audioHeader = (MP3AudioHeader) mp3File.getAudioHeader();
//                    track = audioHeader.getTrackLength();
//                }
//            } catch (Exception e) {
//                log.error("addUpdateArticle error", e);
//                throw new ServiceException("文本解析音频失败");
//            }
//        }
        if (articleId > 0) {
            articleMapper.updateArticle(author, title, subTitle, releaseTime, articledetail, articlepic, sort, agentId, articleId, key, track, whetherVoice);
            articleMapper.deleteArticleTag(articleId);
            articleMapper.deleteArticleCategory(articleId);
        } else {
            articleId = articleMapper.getId("article");
            articleMapper.addArticle(articleId, author, title, subTitle, releaseTime, articledetail, articlepic, sort, agentId, key, track, whetherVoice);
        }
        if (tagIds.size() > 0) {
            for (Object value : tagIds) {
                int tagId = ((Integer) value);
                if (tagId > 0) {
                    articleMapper.addArticleTag(articleId, tagId);
                }
            }
        }
        if (categoryIds.size() > 0) {
            for (Object value : categoryIds) {
                int categoryId = (Integer) value;
                if (categoryId > 0) {
                    articleMapper.addArticleCategory(articleId, categoryId);
                }
            }
        }
        return articleId;
    }


    /**
     * 删除文章
     *
     * @param id      文章ID
     * @param agentId 登录人ID
     */
    @Transactional
    public void deleteArticle(int id, int agentId) {
        articleMapper.deleteArticle(id, agentId);
        articleMapper.deleteArticleTag(id);
        articleMapper.deleteArticleCategory(id);
    }

    /**
     * 批量删除 TODO
     *
     * @param ids
     * @param agentId
     */
    public void batchDeleteArticle(List<?> ids, int agentId) {
        for (Object value : ids) {
            int id = ModelUtil.strToInt((String) value, -1);
            if (id > 0) {
                this.deleteArticle(id, agentId);
            }
        }
    }
}
