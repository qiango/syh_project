package com.syhdoctor.webserver.mapper.department;

import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import com.syhdoctor.common.utils.UnixUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2018/11/7
 */
public abstract class CommonDiseaseBaseMapper extends BaseMapper {


    public List<Map<String, Object>> getCommonDiseaseSymptomsType() {
        String sql = " select id,name from common_disease_symptoms_type where ifnull(delflag,0) = 0 ";
        return queryForList(sql);
    }

    public boolean insertCommonDiseaseSymptomsType(String name) {
        String sql = " insert into common_disease_symptoms_type(name,create_time) values (?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    public boolean delCommonDiseaseSymptomsType(long id) {
        String sql = " update common_disease_symptoms_type set delflag = 1 where id = ? and ifnull(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }

    //根据类型（typeid）删除症状
    public boolean delCommonDiseaseSymptomsAllTypeid(long id) {
        String sql = " update common_disease_symptoms set delflag = 1 where typeid = ? and ifnull(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean updateCommonDiseaseSymptomsType(long id, String name) {
        String sql = " update common_disease_symptoms_type set name = ? where id = ? and ifnull(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(id);
        return update(sql, params) > 0;
    }


    public List<Map<String, Object>> getCommonDiseaseSymptoms(long typeid) {
        String sql = " SELECT cds.id id, cds.name name, cds.sort sort, cds.create_time createtime, cdst.name typename " +
                " FROM common_disease_symptoms cds " +
                "       left join common_disease_symptoms_type cdst on IFNULL(cdst.delflag, 0) = 0 and cdst.id = cds.typeid " +
                " where ifnull(cds.delflag,0) = 0 and cds.typeid = ? " +
                " order by cds.create_time desc  ";
        List<Object> params = new ArrayList<>();
        params.add(typeid);
        return queryForList(sql, params);
    }


    public boolean delCommonDiseaseSymptoms(long id) {
        String sql = " update common_disease_symptoms set delflag = 1 where id = ? and ifnull(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean insertCommonDiseaseSymptoms(String name, long typeid) {
        String sql = " insert into common_disease_symptoms(name,create_time,typeid) values(?,?,?)  ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(typeid);
        return insert(sql, params) > 0;
    }

    public boolean updateCommonDiseaseSymptoms(long id, String name) {
        String sql = " update common_disease_symptoms set name = ? where id = ? and ifnull(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(id);
        return update(sql, params) > 0;
    }

    public List<Map<String, Object>> findList(String departName, int pageIndex, int pageSize) {
//        String sql="select md.id,md.create_time,cd.code,cd.value,cs.name from middle_department_symptoms_type md left join common_disease_symptoms_type cs on md.typeid=cs.id "+
//                " left join code_department cd on md.departmentid=cd.id "+
//                  " WHERE IFNULL(md.delflag,0)=0 ";
        String sql = "SELECT  " +
                " cd.id,  " +
                " cd.code,  " +
                " cd.value,  " +
                " GROUP_CONCAT(cs.NAME) as name  " +
                "FROM  " +
                " middle_department_symptoms_type md  " +
                " LEFT JOIN common_disease_symptoms_type cs ON md.typeid = cs.id  " +
                " LEFT JOIN code_department cd ON md.departmentid = cd.id   " +
                "WHERE  " +
                " IFNULL(md.delflag, 0 ) = 0  ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(departName)) {
            sql += " AND cd.value like ? ";
            params.add(String.format("%%%s%%", departName));
        }
        return queryForList(pageSql(sql, "GROUP BY cd.id order by cd.id desc "), pageParams(params, pageIndex, pageSize));
    }

    public long getfindListCount(String departName) {
        String sql = "SELECT count(*) FROM (SELECT  count(*)" +
                "FROM  " +
                " middle_department_symptoms_type md  " +
                " LEFT JOIN common_disease_symptoms_type cs ON md.typeid = cs.id  " +
                " LEFT JOIN code_department cd ON md.departmentid = cd.id   " +
                "WHERE  " +
                " IFNULL(md.delflag, 0 ) = 0  ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(departName)) {
            sql += " AND cd.value like ? ";
            params.add(String.format("%%%s%%", departName));
        }
        sql += " GROUP BY cd.id) a";
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);

    }

    public boolean insertdepartment(long departid, List<Long> typelist) {
        String sql = "insert into middle_department_symptoms_type (departmentid,delflag,create_time,typeid)" +
                " VALUES (?,?,?,?) ";
        for (long typeid : typelist) {
            List<Object> param = new ArrayList<>();
            param.add(departid);
            param.add(0);
            param.add(UnixUtil.getNowTimeStamp());
            param.add(typeid);
            insert(sql, param);
        }
        return true;
    }

    public boolean updateDepartment(long id, long departid, long typeid) {
        String sql = "update middle_department_symptoms_type set departmentid=?,typeid=? where id=?";
        List<Object> param = new ArrayList<>();
        param.add(departid);
        param.add(typeid);
        param.add(id);
        return update(sql, param) > 0;
    }

    public boolean deleteDepart(long id) {
        String sql = "update middle_department_symptoms_type set delflag=1 where departmentid=?";
        return update(sql, id) > 0;
    }

    public boolean getdepartmentid(long id) {
        String sql = "select id from  middle_department_symptoms_type where  ifnull(delflag,0)=0 and departmentid=?";
        if (queryForList(sql, id).size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Map<String, Object> findDetail(long id) {
        String sql = "select md.id,cd.code,cd.value,cs.name from middle_department_symptoms_type md left join common_disease_symptoms_type cs on md.typeid=cs.id " +
                " left join code_department cd on md.departmentid=cd.id" +
                "  WHERE md.id=?";
        return queryForMap(sql, id);
    }

    public List<Map<String, Object>> findType() {
        String sql = "select id,name from common_disease_symptoms_type where IFNULL(delflag, 0) = 0";
        return queryForList(sql);
    }

}
