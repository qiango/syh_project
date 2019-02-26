package com.syhdoctor.webserver.mapper.knowledge;

import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class KnowledgeBaseMapper extends BaseMapper {

    /**
     * 常见疾病列表
     *
     * @param name
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getAdminDiseaseList(String name, int pageIndex, int pageSize) {
        String sql = " select id,name,name_pinyin namepinyin,create_time createtime from common_disease where ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and (name like ? or name_pinyin like ? )";
            params.add(String.format("%%%s%%", name));
            params.add(String.format("%%%s%%", name));
        }
        return queryForList(pageSql(sql, " order by create_time desc "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 常见疾病数量
     *
     * @param name
     * @return
     */
    public long getAdminDiseaseCount(String name) {
        String sql = " select count(id) count from common_disease where ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and (name like ? or name_pinyin like ? )";
            params.add(String.format("%%%s%%", name));
            params.add(String.format("%%%s%%", name));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    /**
     * 添加常见疾病
     *
     * @param name           疾病名
     * @param summary      概要
     * @param information    基本信息
     * @param knowledge      疾病知识
     * @param classification 分类
     * @param clinical       临床表现
     * @param inspect        检查
     * @param diagnosis      诊断
     * @param treatment      治疗方案
     * @param prevention     预防
     * @param createUser
     * @return
     */
    public boolean addDisease(String name, String name_pinyin, String summary, String information, String knowledge, String classification,
                              String clinical, String inspect, String diagnosis, String treatment, String prevention, long createUser) {
        String sql = " insert into common_disease (name, " +
                "                            name_pinyin, " +
                "                            summary, " +
                "                            information, " +
                "                            knowledge, " +
                "                            classification, " +
                "                            clinical, " +
                "                            inspect, " +
                "                            diagnosis, " +
                "                            treatment, " +
                "                            prevention, " +
                "                            delflag, " +
                "                            create_time, " +
                "                            create_user) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(name_pinyin);
        params.add(summary);
        params.add(information);
        params.add(knowledge);
        params.add(classification);
        params.add(clinical);
        params.add(inspect);
        params.add(diagnosis);
        params.add(treatment);
        params.add(prevention);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(createUser);
        return insert(sql, params) > 0;
    }

    /**
     * 修改常见病
     *
     * @param id             id
     * @param name           疾病名
     * @param summary      概要
     * @param information    基本信息
     * @param knowledge      疾病知识
     * @param classification 分类
     * @param clinical       临床表现
     * @param inspect        检查
     * @param diagnosis      诊断
     * @param treatment      治疗方案
     * @param prevention     预防
     * @param createUser
     * @return
     */
    public boolean updateDisease(long id, String name, String name_pinyin, String summary, String information, String knowledge, String classification,
                                 String clinical, String inspect, String diagnosis, String treatment, String prevention, long createUser) {
        String sql = " update common_disease " +
                "set name           = ?, " +
                "    name_pinyin    = ?, " +
                "    summary       = ?, " +
                "    information    = ?, " +
                "    knowledge      = ?, " +
                "    classification = ?, " +
                "    clinical       = ?, " +
                "    inspect        = ?, " +
                "    diagnosis      = ?, " +
                "    treatment      = ?, " +
                "    prevention     = ?, " +
                "    modify_time    = ?, " +
                "    modify_user    = ? " +
                "where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(name_pinyin);
        params.add(summary);
        params.add(information);
        params.add(knowledge);
        params.add(classification);
        params.add(clinical);
        params.add(inspect);
        params.add(diagnosis);
        params.add(treatment);
        params.add(prevention);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(createUser);
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 删除常见疾病
     *
     * @param id
     * @param createUser
     * @return
     */
    public boolean delDisease(long id, long createUser) {
        String sql = " update common_disease set modify_time=?,delflag=?,modify_user=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(1);
        params.add(createUser);
        params.add(id);
        return update(sql, params) > 0;
    }


    public List<Map<String, Object>> getAppDiseaseList(String name, int pageIndex, int pageSize) {
        String sql = " select case " +
                "         when UPPER(left(name_pinyin, 1)) = '' or UPPER(left(name_pinyin, 1)) is null then '#' " +
                "         else UPPER(left(name_pinyin, 1)) end as initials, id, name " +
                "from common_disease " +
                "where ifnull(delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        return queryForList(pageSql(sql, " order by field(initials, '#') ASC, initials ASC "), pageParams(params, pageIndex, pageSize));
    }

    public List<Map<String, Object>> getAppDiseaseList(String name) {
        String sql = " select id,name value " +
                "from common_disease " +
                "where ifnull(delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        return queryForList(sql,params);
    }


    public Map<String, Object> getDisease(long diseaseid) {
        String sql = " select id,name,summary,information,knowledge,classification,clinical,inspect,diagnosis,treatment,prevention from common_disease where id=? and ifnull(delflag,0)=0 ";
        return queryForMap(sql, diseaseid);
    }

    public boolean updateDiseasePageviews(long diseaseid) {
        String sql = " update common_disease set pageviews=Pageviews+1 where id=? ";
        return update(sql, diseaseid) > 0;
    }

    public List<Map<String, Object>> getHotDiseaseList() {
        String sql = " select id,name value from common_disease where ifnull(delflag,0)=0 order by pageviews desc limit 6 ";
        return queryForList(sql);
    }
}
