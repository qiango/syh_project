package com.syhdoctor.webtask.voice.mapper;

import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webtask.base.mapper.BaseMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2018/10/25
 */
@Repository
public class VoiceMapper extends BaseMapper {

    public List<Map<String, Object>> getArticleList() {
        String sql = "select articleid,articledetail from article where ifnull(delfalg,0)=0 and (voice_url is null or voice_url='') and whethervoice=1 ";
        return queryForList(sql);
    }

    @CacheEvict(value = "Article", allEntries = true)
    public boolean updateArticle(long articleId, Object localUrl, Object track) {
        String sql = "update article set modify_time=?,voice_url=?,voice_time=? where articleid=?";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(localUrl);
        params.add(track);
        params.add(articleId);
        return update(sql, params) > 0;
    }

    public boolean updateArticleErro(long articleId, String reason) {
        String sql = "update article set modify_time=?,is_handle=1,reason=? where articleid=?";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(reason);
        params.add(articleId);
        return update(sql, params) > 0;
    }
}
