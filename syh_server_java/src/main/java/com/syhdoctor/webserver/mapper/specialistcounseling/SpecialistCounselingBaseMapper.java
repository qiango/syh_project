package com.syhdoctor.webserver.mapper.specialistcounseling;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import com.syhdoctor.webserver.config.ConfigModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SpecialistCounselingBaseMapper extends BaseMapper {

    public List<Map<String, Object>> getSpecialistCounselingList(long id, int pageIndex, int pageSize) {
        String sql = " SELECT " +
                " sc.id, " +
                " sc.picture, " +
                " sc.sort, " +
                " sc.color, " +
                " ( " +
                " SELECT " +
                " GROUP_CONCAT( cd.NAME ) AS NAME  " +
                " FROM " +
                " common_disease_symptoms_type cd " +
                " LEFT JOIN symptom_type st ON cd.id = st.symptomtypeid  " +
                " WHERE " +
                " st.scid = sc.id  " +
                " GROUP BY " +
                " st.scid  " +
                " ) as symptomtype  " +
                " FROM " +
                " specialist_counseling sc  " +
                " WHERE " +
                " ifnull( sc.delflag, 0 ) = 0 ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and sc.id = ? ";
            params.add(id);
        }
        return queryForList(pageSql(sql, " order by sc.sort desc "), pageParams(params, pageIndex, pageSize));
    }

    public Map<String, Object> getSpecialistCounselingId(long id) {
        String sql = "  select id,color,complextext,picture,sort,buttontext,headname,backgroundpicture,is_login islogin from specialist_counseling where ifnull(delflag,0) = 0 and id = ?  ";
        return queryForMap(sql, id);
    }

    /**
     * 下拉框
     *
     * @return
     */
    public List<Map<String, Object>> getSymptomsType() {
        String sql = " select id,name from common_disease_symptoms_type where ifnull(delflag,0) = 0 ";
        return queryForList(sql);
    }

    /**
     * 详情下拉框渲染
     *
     * @param id
     * @return
     */
    public List<?> getSymptomsType(long id) {
        String sql = " SELECT " +
                " cd.id, " +
                " cd.name " +
                "FROM " +
                " symptom_type AS st " +
                " LEFT JOIN common_disease_symptoms_type AS cd ON st.symptomtypeid = cd.id  " +
                "WHERE " +
                " st.scid = ? ";
        return queryForList(sql, id);
    }

    public long getSpecialistCounselingListCount(long id) {
        String sql = " select count(id) count from specialist_counseling where ifnull(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and id = ? ";
            params.add(id);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    public boolean delSpecialistCounseling(long id) {
        String sql = " update specialist_counseling set delflag = 1 where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }

    public long addSpecialistCounseling(String picture, int sort, String complextext, String color, String buttontext, String headname, String backgroundpicture, int islogin) {
        String sql = " insert into specialist_counseling(picture,sort,create_time,complextext,color,buttontext,headname,backgroundpicture,is_login) values(?,?,?,?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(picture);
        params.add(sort);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(complextext);
        params.add(color);
        params.add(buttontext);
        params.add(headname);
        params.add(backgroundpicture);
        params.add(islogin);
        return insert(sql, params, "id");
    }

    public boolean sortClear(int sort) {
        String sql = " update specialist_counseling set sort = 0 where sort = ? ";
        return update(sql, sort) > 0;
    }

    /**
     * 症状中间表添加
     *
     * @param scid
     * @param cdid
     * @return/
     */
    public boolean addSymptomType(long scid, long cdid) {
        String sql = " insert into symptom_type(scid,symptomtypeid,create_time) values(?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(scid);
        params.add(cdid);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    public boolean delSymptomType(long scid) {
        String sql = " DELETE from symptom_type where scid = ? ";
        return update(sql, scid) > 0;
    }


    public boolean updateSpecialistCounseling(long id, String picture, int sort, String complextext, String color, String buttontext, String headname, String backgroundpicture, int islogin) {
        String sql = " update specialist_counseling set picture = ?,sort = ?,modify_time = ?,complextext=?,color=?,buttontext=?,headname=?,backgroundpicture=?,is_login=? where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(picture);
        params.add(sort);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(complextext);
        params.add(color);
        params.add(buttontext);
        params.add(headname);
        params.add(backgroundpicture);
        params.add(islogin);
        params.add(id);
        return update(sql, params) > 0;
    }


    //app首页列表
    public List<Map<String, Object>> getSpecialList() {
        String sql = "select ss.is_login, ss.id,ss.picture,cs.name from special_specialties ss left join common_disease_symptoms_type" +
                " cs on ss.symptomtypeid=cs.id where ifnull(ss.delflag,0)=0 order by ss.sort desc limit 6";
        List<Map<String, Object>> list = queryForList(sql);
        for (Map<String, Object> map : list) {
            map.put("type", 5);
            int islogin = ModelUtil.getInt(map, "is_login");
            String url = ConfigModel.WEBLINKURL + "web/syhdoctor/#/specialdepartment?id=%s";
            if (islogin == 0) {//要登录
                url = url + "&uid=$$";
            }
            map.put("typename", String.format(url, ModelUtil.getLong(map, "id")));
        }
        return list;
    }

    //web首页列表
    public List<Map<String, Object>> getWebSpecialList() {
        String sql = "select ss.is_login islogin, ss.id,ss.picture,cs.name,cs.id typeid from special_specialties ss left join common_disease_symptoms_type" +
                " cs on ss.symptomtypeid=cs.id where ifnull(ss.delflag,0)=0 order by ss.sort desc limit 6";
        return queryForList(sql);
    }

    public List<Map<String, Object>> getDiseaseList(List<Long> typeIds) {
        String sql = " select id,name,typeid from common_disease_symptoms where ifnull(delflag,0)=0 and typeid in (:ids)  order by sort desc ";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", typeIds);
        return queryForList(sql, params);
    }


    public List<Map<String, Object>> getSpecialList(int pageSize, int pageIndex) {
        String sql = "select ss.id,ss.picture,cs.name,ss.create_time createtime,ss.color,ss.headname title,ss.buttontext,ss.backgroundpicture from special_specialties ss left join common_disease_symptoms_type" +
                " cs on ss.symptomtypeid=cs.id where ifnull(ss.delflag,0)=0 order by ss.sort desc ";
        List<Object> params = new ArrayList<>();
        List<Map<String, Object>> list = queryForList(pageSql(sql, " , ss.id desc"), pageParams(params, pageIndex, pageSize));
        for (Map<String, Object> map : list) {
            map.put("type", 5);
            int islogin = ModelUtil.getInt(map, "is_login");
            String url = ConfigModel.WEBLINKURL + "web/syhdoctor/#/specialdepartment?id=%s";
            if (islogin == 0) {//要登录
                url = url + "&uid=$$";
            }
            map.put("typename", String.format(url, ModelUtil.getLong(map, "id")));
        }
        return list;
    }

    public long getSpecialListConut() {
        String sql = "select count(ss.id) count from special_specialties ss where ifnull(ss.delflag,0)=0";
        List<Object> params = new ArrayList<>();
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public long getSpecialCountListConut() {
        String sql = "select count(ss.id) count from specialist_counseling ss where ifnull(ss.delflag,0)=0";
        List<Object> params = new ArrayList<>();
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    //app首页专病资讯列表
    public List<Map<String, Object>> getSpecialCounList() {
        String sql = "select id,picture,is_login from specialist_counseling where ifnull(delflag,0)=0 order by sort desc limit 4";
        List<Map<String, Object>> list = queryForList(sql);
        for (Map<String, Object> map : list) {
            map.put("type", 5);
            int islogin = ModelUtil.getInt(map, "is_login");
            String url = ConfigModel.WEBLINKURL + "web/syhdoctor/#/specialadvisory?id=%s";
            if (islogin == 0) {//要登录
                url = url + "&uid=$$";
            }
            map.put("typename", String.format(url, ModelUtil.getLong(map, "id")));
        }
        return list;
    }

    public List<Map<String, Object>> getSpecialCounListPage(int pageSize, int pageIndex) {
        String sql = "select id,picture,is_login,create_time createtime,color,headname title,buttontext,backgroundpicture from specialist_counseling where ifnull(delflag,0)=0 order by sort desc";
        List<Object> params = new ArrayList<>();
        List<Map<String, Object>> list = queryForList(pageSql(sql, ""), pageParams(params, pageIndex, pageSize));
//        List<Map<String, Object>> list = queryForList(sql);
        for (Map<String, Object> map : list) {
            map.put("type", 5);
            int islogin = ModelUtil.getInt(map, "is_login");
            String url = ConfigModel.WEBLINKURL + "web/syhdoctor/#/specialadvisory?id=%s";
            if (islogin == 0) {//要登录
                url = url + "&uid=$$";
            }
            map.put("typename", String.format(url, ModelUtil.getLong(map, "id")));
        }
        return list;
    }


    //app首页详情
    public Map<String, Object> getSpecialDetail(long id) {
        String sql = "select symptomtypeid id,complextext,color,buttontext,headname title,backgroundpicture from special_specialties where id=?";
        return queryForMap(sql, id);
    }

    //app首页详情
    public Map<String, Object> getSpecialCountDetail(long id) {
        String sql = "select id,complextext,picture,color,buttontext,headname title,backgroundpicture from specialist_counseling where id=?";
        return queryForMap(sql, id);
    }


}
