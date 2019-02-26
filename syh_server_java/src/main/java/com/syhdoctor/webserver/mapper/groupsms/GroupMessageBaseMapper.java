package com.syhdoctor.webserver.mapper.groupsms;

import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class GroupMessageBaseMapper extends BaseMapper {

    public boolean addGroupMessageUserAll(int type, String name, int messagetype, String messagetext, String typename) {
        String sql = " insert into message(url,name, type, type_name, message_type, sendid, message_text, message_subtext, delflag, create_time)  " +
                "   select  " +
                "       headpic,  " +
                "       ?,  " +
                "       ?,  " +
                "       ?,  " +
                "       ?,  " +
                "       id,  " +
                "       ?,  " +
                "       ?,  " +
                "       ?,  " +
                "       ?  " +
                "from user_account where ifnull(delflag,0) =0  ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(type);
        params.add(typename);
        params.add(messagetype);
        params.add(messagetext);
        params.add("");
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    public boolean addGroupMessageDoctorAll(int type, String name, int messagetype, String messagetext, String typename) {
        String sql = " insert into message(url,name, type, type_name, message_type, sendid, message_text, message_subtext, delflag, create_time) " +
                "  select " +
                "       doc_photo_url, " +
                "       ?, " +
                "       ?, " +
                "       ?, " +
                "       ?, " +
                "       doctorid, " +
                "       ?, " +
                "       ?, " +
                "       ?, " +
                "       ?  " +
                "from doctor_info where ifnull(delflag,0) =0 ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(type);
        params.add(typename);
        params.add(messagetype);
        params.add(messagetext);
        params.add("");
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }


    public boolean addGroupMessageUserId(int type, String name, int messagetype, String messagetext, long id, String typename) {
        String sql = " insert into message(url,name, type, type_name, message_type, sendid, message_text, message_subtext, delflag, create_time)  " +
                "   select  " +
                "       headpic,  " +
                "       ?,  " +
                "       ?,  " +
                "       ?,  " +
                "       ?,  " +
                "       id,  " +
                "       ?,  " +
                "       ?,  " +
                "       ?,  " +
                "       ?  " +
                "from user_account where ifnull(delflag,0) =0 and id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(type);
        params.add(typename);
        params.add(messagetype);
        params.add(messagetext);
        params.add("");
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(id);
        return insert(sql, params) > 0;
    }

    public boolean addGroupMessageDoctorId(int type, String name, int messagetype, String messagetext, long id, String typename) {
        String sql = " insert into message(url,name, type, type_name, message_type, sendid, message_text, message_subtext, delflag, create_time) " +
                "  select " +
                "       doc_photo_url, " +
                "       ?, " +
                "       ?, " +
                "       ?, " +
                "       ?, " +
                "       doctorid, " +
                "       ?, " +
                "       ?, " +
                "       ?, " +
                "       ?  " +
                "from doctor_info where ifnull(delflag,0) =0 and doctorid = ? ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(type);
        params.add(typename);
        params.add(messagetype);
        params.add(messagetext);
        params.add("");
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(id);
        return insert(sql, params) > 0;
    }


    public List<Map<String, Object>> userName(long id, String name, int pageIndex, int pageSize) {
        String sql = " select id,concat_ws(' ',userno,name) name from user_account where ifnull(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
//        if (id != 0) {
//            sql += " and id = ? ";
//            params.add(id);
//        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and (name like ? or userno like ?) ";
            params.add(String.format("%%%S%%", name));
            params.add(String.format("%%%S%%", name));
        }
        return queryForList(pageSql(sql, " order by id desc "), pageParams(params, pageIndex, pageSize));
    }

    public long userNameCount(long id, String name) {
        String sql = " select count(id) count from user_account where ifnull(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and id = ? ";
            params.add(id);
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and name like ? or userno like ? ";
            params.add(String.format("%%%S%%", name));
            params.add(String.format("%%%S%%", name));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public List<Map<String, Object>> doctorName(long id, String name, int pageIndex, int pageSize) {
        String sql = " select doctorid id,CONCAT_ws(' ',in_doc_code,doc_name ) name from doctor_info where ifnull(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
//        if (id != 0) {
//            sql += " and doctorid = ? ";
//            params.add(id);
//        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and (doc_name like ? or in_doc_code like ?)  ";
            params.add(String.format("%%%S%%", name));
            params.add(String.format("%%%S%%", name));
        }
        return queryForList(pageSql(sql, " order by id desc "), pageParams(params, pageIndex, pageSize));
    }

    public long doctorNameCount(long id, String name) {
        String sql = " select count(doctorid) as count from doctor_info where ifnull(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and doctorid = ? ";
            params.add(id);
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and (doc_name like ?  or in_doc_code like ?)  ";
            params.add(String.format("%%%S%%", name));
            params.add(String.format("%%%S%%", name));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

}
