package com.syhdoctor.webserver.mapper.feedback;

import com.syhdoctor.common.utils.EnumUtils.FeedbackStatusEnum;
import com.syhdoctor.common.utils.EnumUtils.MessageTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.RegisterChannelEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FeedbackBaseMapper extends BaseMapper {

    /**
     * 意见反馈
     *
     * @param userId   用户id
     * @param doctorId 医生id
     * @param type     1: android 2:ios
     * @param system   1: 用户端 2:医生端
     * @param content  反馈内容
     * @return
     */
    public boolean addFeedback(long userId, long doctorId, int type, int system, String content) {
        String sql = " insert into feedback(userid, doctorid, content, type, system, create_time) values (?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(doctorId);
        params.add(content);
        params.add(type);
        params.add(system);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }


    /**
     * 意见反馈列表
     *
     * @param name
     * @param system
     * @param status
     * @param phone
     * @param begintime
     * @param endtime
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> feedbackList(String name, int system, int status, String phone, long begintime, long endtime, int pageIndex, int pageSize) {
        String sql = " select a.id, " +
                "       a.userid, " +
                "       a.name, " +
                "       a.phone, " +
                "       a.system, " +
                "       a.content, " +
                "       a.createtime, " +
                "       a.status, " +
                "       a.diagnosis, " +
                "       a.delflag " +
                " from (SELECT f.id          id, " +
                "             ua.id         userid, " +
                "             ua.name       name, " +
                "             ua.phone      phone, " +
                "             f.content     content, " +
                "             f.system      system, " +
                "             f.create_time createtime, " +
                "             f.diagnosis   diagnosis, " +
                "             f.status      status, " +
                "             f.delflag     delflag " +
                "      FROM feedback f " +
                "             left join user_account ua on f.userid = ua.id and IFNULL(ua.delflag, 0) = 0 " +
                "      where f.system = 1 and IFNULL(f.delflag, 0) = 0 " +
                "      union all " +
                "      SELECT f.id          id, " +
                "             di.doctorid   userid, " +
                "             di.doc_name   name, " +
                "             di.doo_tel    phone, " +
                "             f.content     content, " +
                "             f.system      system, " +
                "             f.create_time createtime, " +
                "             f.diagnosis   diagnosis, " +
                "             f.status      status, " +
                "             f.delflag     delflag " +
                "      FROM feedback f " +
                "             left join doctor_info di on f.doctorid = di.doctorid and IFNULL(di.delflag, 0) = 0 " +
                "      where f.system = 2 and IFNULL(f.delflag, 0) = 0 ) a " +
                " where IFNULL(a.delflag, 0) = 0   ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and a.name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (system != 0) {
            sql += " and a.system = ? ";
            params.add(system);
        }
        if (status != 0) {
            sql += " and a.status = ? ";
            params.add(status);
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and a.phone like ? ";
            params.add(String.format("%%%S%%", phone));

        }
        if (begintime != 0) {
            sql += " and a.createtime > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and a.createtime < ? ";
            params.add(endtime);
        }
        return query(pageSql(sql, " order by a.id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("system", MessageTypeEnum.getValue(ModelUtil.getInt(map, "system")).getMessage());
                map.put("status", FeedbackStatusEnum.getValue(ModelUtil.getInt(map, "status")).getMessage());
            }
            return map;
        });

    }

    /**
     * 意见反馈列表行数
     *
     * @param name
     * @param system
     * @param status
     * @param phone
     * @param begintime
     * @param endtime
     * @return
     */
    public long feedbackListCount(String name, int system, int status, String phone, long begintime, long endtime) {
        String sql = " select count(a.id) count  " +
                "from (SELECT f.id          id,  " +
                "             ua.id         userid,  " +
                "             ua.name       name,  " +
                "             ua.phone      phone,  " +
                "             f.content     content,  " +
                "             f.system      system,  " +
                "             f.create_time createtime,  " +
                "             f.diagnosis   diagnosis,  " +
                "             f.status      status,  " +
                "             f.delflag     delflag  " +
                "      FROM feedback f  " +
                "             left join user_account ua on f.userid = ua.id and IFNULL(ua.delflag, 0) = 0  " +
                "      where f.system = 1  " +
                "        and IFNULL(f.delflag, 0) = 0  " +
                "      union all  " +
                "      SELECT f.id          id,  " +
                "             di.doctorid   userid,  " +
                "             di.doc_name   name,  " +
                "             di.doo_tel    phone,  " +
                "             f.content     content,  " +
                "             f.system      system,  " +
                "             f.create_time createtime,  " +
                "             f.diagnosis   diagnosis,  " +
                "             f.status      status,  " +
                "             f.delflag     delflag  " +
                "      FROM feedback f  " +
                "             left join doctor_info di on f.doctorid = di.doctorid and IFNULL(di.delflag, 0) = 0  " +
                "      where f.system = 2  " +
                "        and IFNULL(f.delflag, 0) = 0) a  " +
                "where IFNULL(a.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and a.name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (system != 0) {
            sql += " and a.system = ? ";
            params.add(system);
        }
        if (status != 0) {
            sql += " and a.status = ? ";
            params.add(status);
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and a.phone like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        if (begintime != 0) {
            sql += " and a.createtime > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and a.createtime < ? ";
            params.add(endtime);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    /**
     * 意见反馈列表导出
     *
     * @param name
     * @param system
     * @param status
     * @param phone
     * @param begintime
     * @param endtime
     * @return
     */
    public List<Map<String, Object>> feedbackListExport(String name, int system, int status, String phone, long begintime, long endtime) {
        String sql = "  select a.id,  " +
                "         a.name,  " +
                "         a.phone,  " +
                "         a.system,  " +
                "         a.content,  " +
                "         a.createtime,  " +
                "         a.status,  " +
                "         a.diagnosis,  " +
                "         a.delflag  " +
                "   from (SELECT f.id          id,  " +
                "               ua.id         userid,  " +
                "               ua.name       name,  " +
                "               ua.phone      phone,  " +
                "               f.content     content,  " +
                "               f.system      system,  " +
                "               f.create_time createtime,  " +
                "               f.diagnosis   diagnosis,  " +
                "               f.status      status,  " +
                "               f.delflag     delflag  " +
                "        FROM feedback f  " +
                "               left join user_account ua on f.userid = ua.id and IFNULL(ua.delflag, 0) = 0  " +
                "        where f.system = 1 and IFNULL(f.delflag, 0) = 0  " +
                "        union all  " +
                "        SELECT f.id          id,  " +
                "               di.doctorid   userid,  " +
                "               di.doc_name   name,  " +
                "               di.doo_tel    phone,  " +
                "               f.content     content,  " +
                "               f.system      system,  " +
                "               f.create_time createtime,  " +
                "               f.diagnosis   diagnosis,  " +
                "               f.status      status,  " +
                "               f.delflag     delflag  " +
                "        FROM feedback f  " +
                "               left join doctor_info di on f.doctorid = di.doctorid and IFNULL(di.delflag, 0) = 0  " +
                "        where f.system = 2 and IFNULL(f.delflag, 0) = 0 ) a  " +
                "   where IFNULL(a.delflag, 0) = 0   ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and a.name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (system != 0) {
            sql += " and a.system = ? ";
            params.add(system);
        }
        if (status != 0) {
            sql += " and a.status = ? ";
            params.add(status);
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and a.phone like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        if (begintime != 0) {
            sql += " and di.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and di.modify_time < ? ";
            params.add(endtime);
        }
        List<Map<String, Object>> list = queryForList(sql, params);
        for (Map<String, Object> map : list) {
            if (map != null) {
                map.put("system", MessageTypeEnum.getValue(ModelUtil.getInt(map, "system")).getMessage());
                map.put("createtime", UnixUtil.getDate(ModelUtil.getLong(map, "createtime"), "yyyy-MM-dd HH:mm:ss"));
                map.put("status", FeedbackStatusEnum.getValue(ModelUtil.getInt(map, "status")).getMessage());
            }
        }
        return list;
    }

    /**
     * 删除反馈
     *
     * @param id
     * @return
     */
    public long feedbackDel(long id) {
        String sql = " UPDATE feedback f set f.delflag = 1 where f.id=?  ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params);
    }


    /**
     * 点击编辑查询单个
     *
     * @param id
     * @return
     */
    public Map<String, Object> feedbackOneId(long id) {
        String sql = " select a.id, " +
                "       a.userid, " +
                "       a.name, " +
                "       a.phone, " +
                "       a.system, " +
                "       a.content, " +
                "       a.createtime, " +
                "       a.status, " +
                "       a.diagnosis, " +
                "       a.delflag " +
                "from (SELECT f.id          id, " +
                "             ua.id         userid, " +
                "             ua.name       name, " +
                "             ua.phone      phone, " +
                "             f.content     content, " +
                "             f.system      system, " +
                "             f.create_time createtime, " +
                "             f.diagnosis   diagnosis, " +
                "             f.status      status, " +
                "             f.delflag     delflag " +
                "      FROM feedback f " +
                "             left join user_account ua on f.userid = ua.id and IFNULL(ua.delflag, 0) = 0 " +
                "      where f.system = 1 " +
                "        and IFNULL(f.delflag, 0) = 0 " +
                "      union all " +
                "      SELECT f.id          id, " +
                "             di.doctorid   userid, " +
                "             di.doc_name   name, " +
                "             di.doo_tel    phone, " +
                "             f.content     content, " +
                "             f.system      system, " +
                "             f.create_time createtime, " +
                "             f.diagnosis   diagnosis, " +
                "             f.status      status, " +
                "             f.delflag     delflag " +
                "      FROM feedback f " +
                "             left join doctor_info di on f.doctorid = di.doctorid and IFNULL(di.delflag, 0) = 0 " +
                "      where f.system = 2 " +
                "        and IFNULL(f.delflag, 0) = 0) a " +
                "where IFNULL(a.delflag, 0) = 0  and a.id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return queryForMap(sql, params);
    }


    public Map<String,Object> feedbackOneIdType(long id){
        String sql = " select system,status from feedback where ifnull(delflag,0)=0 and id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return queryForMap(sql,params);
    }



    /**
     * 编辑反馈（修改）
     *
     * @param id
     * @param diagnosis
     * @return
     */
    public boolean feedbackUpdate(long id, String diagnosis) {
        String sql = " UPDATE feedback  set diagnosis = ?,status = 2 where IFNULL(delflag,0)=0 and id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(diagnosis);
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean feedbackOperationRecordAdd(long feedbackid, long createuser) {
        String sql = "  insert into feedback_operation_record ( feedbackid, delflag, create_time, create_user) " +
                " values (?, ?, ?, ?)  ";
        List<Object> params = new ArrayList<>();
        params.add(feedbackid);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(createuser);
        return insert(sql, params) > 0;
    }


    public Map<String, Object> feedbackPhone(String phone) {
        String sql = " select doctorid from doctor_info where doo_tel = ? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(phone);
        return queryForMap(sql, params);
    }


    public long feedbackAdd(int system, long userId, long doctorId, int status, String content, String diagnosis, long createtime) {
        String sql = " insert into feedback(type,system,userid,doctorid,status,content,diagnosis,create_time) values (?,?,?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(3);
        params.add(system);
        params.add(userId);
        params.add(doctorId);
        params.add(status);
        params.add(content);
        params.add(diagnosis);
        if (createtime == 0) {
            params.add(UnixUtil.getNowTimeStamp());
        } else {
            params.add(createtime);
        }
        return insert(sql, params, "id");
    }


}
