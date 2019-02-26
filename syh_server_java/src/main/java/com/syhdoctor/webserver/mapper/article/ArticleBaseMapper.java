package com.syhdoctor.webserver.mapper.article;

import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ArticleBaseMapper extends BaseMapper {


    public long getId(String tableName) {
        return super.getId(tableName);
    }

    /**
     * 查询文章详情
     *
     * @param articleId
     * @return
     */
    public Map<String, Object> getArticleById(long articleId) {
        String sql = "select articleid,author, title, subtitle, release_time as releasetime, introduction, articledetail, articlepic,click_rate,whethervoice,voice_time as voicetime,voice_url as voiceurl,sort from article where articleid= ?";
        List<Object> params = new ArrayList<>();
        params.add(articleId);
        return queryForMap(sql, params);
    }


    /**
     * 查询咨询列表
     *
     * @return
     */
    public List<Map<String, Object>> getArticleList(int pageIndex, int pageSize) {
        String sql = "select articleid,articlepic,title,subtitle,author,voice_url as voiceurl,voice_time as voicetime,whethervoice from article where ifnull(delfalg,0)=0 and unix_timestamp(now())>release_time/1000 and voice_url is not null ";
        sql = pageSql(sql, "order by sort desc,click_rate desc,release_time desc");
        List<Object> params = new ArrayList<>();
        params = pageParams(params, pageIndex, pageSize);
        return queryForList(sql, params);
    }

    /**
     * 查询咨询列表
     *
     * @return
     */
    public List<Map<String, Object>> getNewArticleList(int pageIndex, int pageSize) {
        String sql = "select articleid,articlepic,title,subtitle from article where ifnull(delfalg,0)=0";
        sql = pageSql(sql, "order by release_time desc");
        List<Object> params = new ArrayList<>();
        params = pageParams(params, pageIndex, pageSize);
        return queryForList(sql, params);
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
        String sql = "select articleid,title,subtitle,author,articlepic,click_rate,create_time,voice_url as voiceurl,voice_time as voicetime,whethervoice from article where ifnull(delfalg,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(title)) {
            sql += " and title like ?";
            params.add(String.format("%%%s%%", title));
        }
        if (!StrUtil.isEmpty(subTitle)) {
            sql += " and subtitle like ?";
            params.add(String.format("%%%s%%", subTitle));
        }
        if (!StrUtil.isEmpty(author)) {
            sql += " and author like ?";
            params.add(String.format("%%%s%%", author));
        }
        if (whethervoice >= 0) {
            sql += " AND ifnull(whethervoice,0) = ? ";
            params.add(whethervoice);
        }

        sql = pageSql(sql, " order by release_time,create_time desc ");
        params = pageParams(params, pageIndex, pageSize);
        return queryForList(sql, params);
    }

    /**
     * 查询总条数
     *
     * @param title
     * @param subTitle
     * @param author
     * @return
     */
    public long getArticleListTotal(String title, String subTitle, String author, int whethervoice) {
        String sql = "select count(articleid) as total from article where ifnull(delfalg,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(title)) {
            sql += " and title like ?";
            params.add(String.format("%%%s%%", title));
        }
        if (!StrUtil.isEmpty(subTitle)) {
            sql += " and subtitle like ?";
            params.add(String.format("%%%s%%", subTitle));
        }
        if (!StrUtil.isEmpty(author)) {
            sql += " and author like ?";
            params.add(String.format("%%%s%%", author));
        }
        if (whethervoice >= 0) {
            sql += " AND ifnull(whethervoice,0) = ? ";
            params.add(whethervoice);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), Long.class);
    }


    /**
     * 添加文章
     *
     * @param author        作者
     * @param title         标题
     * @param subTitle      副标题
     * @param releaseTime   发布时间
     * @param articledetail 文章内容
     * @param articlepic    文章封面图片
     * @param agentId       登陆人id
     * @return
     */
    public long addArticle(long articleId, String author, String title, String subTitle, long releaseTime, String articledetail,
                           String articlepic, int sort, int agentId, String localUrl, int track, int whetherVoice) {
        String sql = "insert into article(articleid,author, title, subtitle, release_time, articledetail, articlepic, sort, click_rate, start_time, end_time, delfalg, create_time, create_user,voice_url,voice_time,whethervoice) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(articleId);
        params.add(author);
        params.add(title);
        params.add(subTitle);
        params.add(releaseTime);
        params.add(articledetail);
        params.add(articlepic);
        params.add(sort);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(UnixUtil.getNowTimeStamp());
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentId);
        params.add(localUrl);
        params.add(track);
        params.add(whetherVoice);
        update(sql, params);
        return articleId;
    }

    /**
     * 添加关系表
     *
     * @param articleId
     * @param tagId
     * @return
     */
    public boolean addArticleTag(long articleId, int tagId) {
        String sql = "insert into middle_article_tag(articleid, tagid) values (?,?)";
        List<Object> params = new ArrayList<>();
        params.add(articleId);
        params.add(tagId);
        return update(sql, params) > 0;
    }

    public boolean addArticleCategory(long articleId, int categoryId) {
        String sql = "insert into middle_article_category(articleid, categoryid) values (?,?)";
        List<Object> params = new ArrayList<>();
        params.add(articleId);
        params.add(categoryId);
        return update(sql, params) > 0;
    }

    public boolean deleteArticleCategory(long articleId) {
        String sql = "delete from middle_article_category where articleid=?";
        List<Object> params = new ArrayList<>();
        params.add(articleId);
        return update(sql, params) > 0;
    }


    /**
     * 删除关系表
     *
     * @param articleId
     * @return
     */
    public boolean deleteArticleTag(long articleId) {
        String sql = "delete from middle_article_tag where articleid=?";
        List<Object> params = new ArrayList<>();
        params.add(articleId);
        return update(sql, params) > 0;
    }

    /**
     * 修改文章
     *
     * @param author        作者
     * @param title         标题
     * @param subTitle      副标题
     * @param releaseTime   发布时间
     * @param articledetail 文章内容
     * @param articlepic    文章封面图片
     * @param agentId       登陆人id
     * @param articleId     文章id
     * @return
     */
    public boolean updateArticle(String author, String title, String subTitle, long releaseTime,
                                 String articledetail,
                                 String articlepic, int sort,
                                 int agentId, long articleId,
                                 String localUrl, int track, int whetherVoice) {
        String sql = "update article set author=?, title=?, subtitle=?, release_time=?, articledetail=?, articlepic=?,sort=?,modify_time=?,modify_user=?,voice_url=?,voice_time=?,whethervoice=? where articleid=?";
        List<Object> params = new ArrayList<>();
        params.add(author);
        params.add(title);
        params.add(subTitle);
        params.add(releaseTime);
        params.add(articledetail);
        params.add(articlepic);
        params.add(sort);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentId);
        params.add(localUrl);
        params.add(track);
        params.add(whetherVoice);
        params.add(articleId);
        return update(sql, params) > 0;
    }

    /**
     * 删除文章
     *
     * @param id
     * @return
     */
    public boolean deleteArticle(int id, int agentId) {
        String sql = " update article set delfalg=1,modify_time=?,modify_user=? where articleid=?";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentId);
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean updateArticleClick(long articleId) {
        String sql = " update article set click_rate=click_rate+1 where articleid=? ";
        return update(sql, articleId) > 0;
    }
}
