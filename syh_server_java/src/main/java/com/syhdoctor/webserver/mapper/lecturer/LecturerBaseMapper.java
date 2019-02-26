package com.syhdoctor.webserver.mapper.lecturer;

import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LecturerBaseMapper extends BaseMapper {


    public boolean addLecture(String name, String photo, String phone, String titleName, String hospital, String department, String expertise, String abstracts, long doctorId) {
        String sql = "insert into lecturer_info(name, photo, phone, title_name , hospital, department, expertise, abstract, create_time, doctorid)" +
                "values (?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(photo);
        params.add(phone);
        params.add(titleName);
        params.add(hospital);
        params.add(department);
        params.add(expertise);
        params.add(abstracts);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorId);
        return update(sql, params) > 0;
    }

    public List<Map<String, Object>> getLecturerInfoList(String name, String phone, int pageIndex, int pageSize) {
        String sql = "select id,name, photo, phone, title_name as titlename, hospital, department, expertise, abstract, create_time as createtime, doctorid " +
                "from lecturer_info where 1=1";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and name like ?";
            params.add(String.format("%%%s%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and phone like ?";
            params.add(String.format("%%%s%%", phone));
        }
        return queryForList(pageSql(sql, " order by  id desc "), pageParams(params, pageIndex, pageSize));
    }

    public Map<String, Object> getLecturerInfoById(int id) {
        String sql = "select id,name, photo, phone, title_name as titlename, hospital, department, expertise,  abstract as abstracts, create_time as createtime, doctorid " +
                "from lecturer_info where 1=1 and id=?";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return queryForMap(sql, params);
    }

    public long getLecturerInfoTotal(String name, String phone) {
        String sql = "select count(id) as count from lecturer_info where 1=1";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and name like ?";
            params.add(String.format("%%%s%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and phone like ?";
            params.add(String.format("%%%s%%", phone));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);

    }

    public boolean updateLecture(int id, String name, String photo, String phone, String titleName, String hospital, String department, String expertise, String abstracts, long doctorId) {
        String sql = "update lecturer_info set name=?, photo=?, phone=?, title_name=?, hospital=?, department=?, expertise=?, abstract=?, modify_time=?, doctorid=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(photo);
        params.add(phone);
        params.add(titleName);
        params.add(hospital);
        params.add(department);
        params.add(expertise);
        params.add(abstracts);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorId);
        params.add(id);
        return update(sql, params) > 0;
    }


    public Map<String, Object> getLecturer(long doctorid) {
        String sql = " select name,photo,title_name title,hospital,department from lecturer_info where id=? ";
        return queryForMap(sql, doctorid);
    }

    public Map<String, Object> getLecturerByDoctor(long doctorid) {
        String sql = " select name,photo,title_name title,hospital,department from lecturer_info where doctorid=? ";
        return queryForMap(sql, doctorid);
    }

    /**
     * 讲师详情
     *
     * @param phone
     * @return
     */
    public Map<String, Object> getLecturer(String phone) {
        String sql = " select id from lecturer_info where phone = ? ";
        List<Object> params = new ArrayList<>();
        params.add(phone);
        return queryForMap(sql, params);
    }
    /**
     * 医生根据手机查询医生详情
     *
     * @param phone
     * @return
     */
    public Map<String, Object> getDoctorByPhone(String phone) {
        String sql = " select doc_photo_url as docphotourl from doctor_info where doo_tel=? and ifnull(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(phone);
        return queryForMap(sql, params);
    }


    public boolean updateLecturer(String name, String photo, String phone, String title, String hospital, String department, String expertise, String abstracts, long userId, long doctorId) {
        String sql = " update lecturer_info set name=?, photo=?, phone=?, title_name=?, hospital=?, department=?,expertise=?, abstract=?, modify_time=?, modify_user=? where doctorid=? ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(photo);
        params.add(phone);
        params.add(title);
        params.add(hospital);
        params.add(department);
        params.add(expertise);
        params.add(abstracts);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(userId);
        params.add(doctorId);
        return update(sql, params) > 0;
    }
}
