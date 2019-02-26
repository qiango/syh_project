package com.syhdoctor.webserver.mapper.specialspecialties;

import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SpecialSpecialtiesBaseMapper extends BaseMapper {

    public List<Map<String, Object>> getSpecialSpecialtiesList(long id, int pageIndex, int pageSize) {
        String sql = " select ss.is_login islogin,ss.color,ss.id,ss.picture,cdst.name,ss.sort,ss.create_time createtime from special_specialties ss " +
                "  LEFT join common_disease_symptoms_type cdst on cdst.id = ss.symptomtypeid and IFNULL(cdst.delflag,0) = 0 " +
                "  where IFNULL(ss.delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and ss.id = ? ";
            params.add(id);
        }
        return queryForList(pageSql(sql, " order by ss.sort desc "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 详情
     *
     * @param id
     * @return
     */

    public Map<String, Object> getSpecialSpecialtiesId(long id) {
        String sql = " select id,picture,sort,complextext,color,buttontext,headname,backgroundpicture,is_login islogin from special_specialties " +
                "  where IFNULL(delflag,0) = 0 and id = ? ";
        return queryForMap(sql, id);
    }


    /**
     * 详情
     *
     * @param id
     * @return
     */

    public Map<String, Object> getShareSpecial(long id) {
        String sql = " select typename,group_concat(name) name " +
                "from ( " +
                "       select cdst.id,cdst.name typename,cds.name " +
                "       from common_disease_symptoms_type cdst " +
                "              left join common_disease_symptoms cds on cdst.id = cds.typeid and ifnull(cds.delflag, 0) = 0 " +
                "       where typeid = ? order by rand() " +
                "       limit 2 " +
                "     ) m " +
                "group by m.id; ";
        return queryForMap(sql, id);
    }

    /**
     * 下拉框
     *
     * @return
     */
    public List<Map<String, Object>> getCommonDiseaseSymptomsType() {
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
        String sql = " select cdst.id, cdst.name " +
                "   from special_specialties ss " +
                "       left join common_disease_symptoms_type cdst on ss.symptomtypeid = cdst.id and IFNULL(cdst.delflag, 0) = 0 " +
                "  where IFNULL(ss.delflag, 0) = 0 " +
                "  and ss.id = ? ";
        return queryForList(sql, id);
    }

    public long getSpecialSpecialtiesListCount(long id) {
        String sql = " select count(ss.id) count from special_specialties ss " +
                "  LEFT join common_disease_symptoms_type cdst on cdst.id = ss.symptomtypeid and IFNULL(cdst.delflag,0) = 0 " +
                "  where IFNULL(ss.delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and ss.id = ? ";
            params.add(id);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    public boolean delSpecialSpecialties(long id) {
        String sql = " update special_specialties set delflag = 1 where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }


    public boolean addSpecialSpecialties(int islogin, String picture, int symptomtype, int sort, String complextext, String color, String buttontext, String headname, String backgroundpicture) {
        String sql = " insert into special_specialties(picture,symptomtypeid,sort,create_time,complextext,color,buttontext,headname,backgroundpicture,is_login) values(?,?,?,?,?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(picture);
        params.add(symptomtype);
        params.add(sort);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(complextext);
        params.add(color);
        params.add(buttontext);
        params.add(headname);
        params.add(backgroundpicture);
        params.add(islogin);
        return insert(sql, params) > 0;
    }

    public boolean sortClear(int sort) {
        String sql = " update special_specialties set sort = 0 where sort = ? ";
        return update(sql, sort) > 0;
    }

    public boolean updateSpecialSpecialties(int islogin, long id, String picture, int symptomtype, int sort, String complextext, String color, String buttontext, String headname, String backgroundpicture) {
        String sql = " update special_specialties set picture = ?,symptomtypeid = ?,sort = ?,modify_time = ?,complextext = ?,color = ?,buttontext = ?,headname=?,backgroundpicture=?,is_login=? where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(picture);
        params.add(symptomtype);
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


}
