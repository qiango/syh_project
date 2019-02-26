package com.syhdoctor.webserver.mapper.tag;

import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class TagBaseMapper extends BaseMapper {

    /**
     * 标签列表
     *
     * @param name      标签名称
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> getTagList(String name, int pageIndex, int pageSize) {
        String sql = "select id,name,sort from tag where ifnull(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and name like ?";
            params.add(String.format("%%%s%%", name));
        }
        sql = pageSql(sql, " order by create_time desc,sort asc ");
        params = pageParams(params, pageIndex, pageSize);
        return queryForList(sql, params);
    }

    /**
     * 总条数
     *
     * @param name
     * @return
     */
    public long getTagTotal(String name) {
        String sql = "select count(id) as total from tag where ifnull(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and name like ?";
            params.add(String.format("%%%s%%", name));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), Long.class);
    }

    /**
     * 查询单条标签
     *
     * @param tagId
     * @return
     */
    public Map<String, Object> getTabById(int tagId) {
        String sql = "select id,name,sort from tag where id=?";
        List<Object> params = new ArrayList<>();
        params.add(tagId);
        return queryForMap(sql, params);
    }

    /**
     * 添加标签
     *
     * @param name    标签名称
     * @param sort    排序
     * @param agentId 登录人ID
     * @return
     */
    public boolean addTag(String name, int sort, int agentId) {
        String sql = "insert into tag(name, sort, create_time, create_user) values (?,?,?,?)";
        List<Object> param = new ArrayList<>();
        param.add(name);
        param.add(sort);
        param.add(UnixUtil.getNowTimeStamp());
        param.add(agentId);
        return update(sql, param) > 0;
    }

    /**
     * 修改标签
     *
     * @param name    标签名称
     * @param sort    排序
     * @param agentId 登录人ID
     * @param tagId   标签ID
     * @return
     */
    public boolean updateTag(String name, int sort, int agentId, int tagId) {
        String sql = "update tag set name=?,sort=?,modify_time=?,modify_user=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(sort);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentId);
        params.add(tagId);
        return update(sql, params) > 0;
    }

    /**
     * 删除标签
     *
     * @param tagId   标签ID
     * @param agentId 登录人ID
     */
    public void deleteTag(int tagId, int agentId) {
        String sql = "update tag set delflag=1,modify_user=?,modify_time=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(agentId);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(tagId);
        update(sql, params);
    }
}
