package com.syhdoctor.webserver.mapper.category;

import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class CategoryBaseMapper extends BaseMapper {


    /**
     * 查询分类
     *
     * @param name
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getCategoryList(String name, int pageIndex, int pageSize) {
        String sql = "select id,name,sort,create_time as createtime  from category where 1=1 and ifnull(delflag,0)=0 and pid=0";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and name like ?";
            params.add(String.format("%%%s%%", name));
        }
        sql = pageSql(sql, " order by sort desc ");
        params = pageParams(params, pageIndex, pageSize);
        return queryForList(sql, params);
    }

    /**
     * 查询分类总条数
     *
     * @param name
     * @return
     */
    public long getCategoryListTotal(String name) {
        String sql = "select count(id) total  from category where 1=1 and ifnull(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and name like ?";
            params.add(String.format("%%%s%%", name));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 查询分类
     *
     * @param id
     * @return
     */
    public Map<String, Object> getCategoryById(int id) {
        String sql = "select id,name,sort from category where id=? and ifnull(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return queryForMap(sql, params);
    }

    public List<?> getCategoryArticle(long articleId) {
        String sql = "select distinct c.id, c.name " +
                "from middle_article_category mc " +
                "       left join category c on mc.categoryid = c.id " +
                "where articleid = ?";
        List<Object> params = new ArrayList<>();
        params.add(articleId);
        return queryForList(sql, params);
    }

    public void addCategory(String name, int sort, int pid, int agentId) {
        String sql = "insert into category(name,sort,pid, create_time, create_user) values (?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(sort);
        params.add(pid);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentId);
        update(sql, params);
    }

    /**
     * 修改分类
     *
     * @param name    分类名称
     * @param sort    排序
     * @param pid     父id
     * @param agentId 登陆人id
     * @param id      分类id
     */
    public void updateCategory(String name, int sort, int pid, int agentId, int id) {
        String sql = "update category set name=?,pid=?,sort=?,modify_time=?,modify_user=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(pid);
        params.add(sort);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentId);
        params.add(id);
        update(sql, params);
    }

    public void deleteCategory(int id, int agentId) {
        String sql = "update category set delflag=1,modify_time=?,modify_user=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentId);
        params.add(id);
        update(sql, params);
    }

    public List<Map<String, Object>> getCategoryDropList(String name) {
        String sql = "select id,name from category where ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and name like ?";
            params.add(String.format("%%%s%%", name));
        }
        return queryForList(sql, params);
    }

}
