package com.syhdoctor.webserver.mapper.user;

import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserEvaluateBaseMapper extends BaseMapper {

    public List<Map<String, Object>> getUserEvaluateList(long id, String username, String doctorname, long begintime, long endtime, int pageIndex, int pageSize) {
        String sql = " select ue.id, " +
                "       order_number ordernumber, " +
                "       ue.userid, " +
                "       ua.name username, " +
                "       ue.ordertype, " +
                "       evaluate, " +
                "       content, " +
                "       di.doctorid, " +
                "       di.doc_name doctorname, " +
                "       ue.delflag, " +
                "       ue.delreason, " +
                "       ue.create_time createtime " +
                " from user_evaluate ue " +
                "            left join user_account ua on ua.id = ue.userid and ifnull(ua.delflag, 0) = 0 " +
                "            left join doctor_info di on di.doctorid = ue.doctorid and ifnull(di.delflag, 0) = 0 " +
                " where 1=1 ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and ue.id = ? ";
            params.add(id);
        }
        if (!StrUtil.isEmpty(username)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%S%%", username));
        }
        if (!StrUtil.isEmpty(doctorname)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%S%%", doctorname));
        }
        if (begintime != 0) {
            sql += " and ue.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and ue.create_time < ? ";
            params.add(endtime);
        }
        return query(pageSql(sql, " order by ue.id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {

            }
            return map;
        });
    }


    public long getUserEvaluateListCount(long id, String username, String doctorname, long begintime, long endtime) {
        String sql = " select count(ue.id) count " +
                " from user_evaluate ue " +
                "            left join user_account ua on ua.id = ue.userid and ifnull(ua.delflag, 0) = 0 " +
                "            left join doctor_info di on di.doctorid = ue.doctorid and ifnull(di.delflag, 0) = 0 " +
                " where 1=1 ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and ue.id = ? ";
            params.add(id);
        }
        if (!StrUtil.isEmpty(username)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%S%%", username));
        }
        if (!StrUtil.isEmpty(doctorname)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%S%%", doctorname));
        }
        if (begintime != 0) {
            sql += " and ue.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and ue.create_time < ? ";
            params.add(endtime);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    public boolean delUserEvaluateList(long id) {
        String sql = " update user_evaluate set delflag = 1 where id = ? ";
        return update(sql, id) > 0;
    }

    /**
     * 删除原因
     *
     * @param id
     * @param delreason
     * @return
     */
    public boolean delReason(long id, String delreason) {
        String sql = " update user_evaluate set delreason = ?,create_time = ? where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(delreason);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(id);
        return update(sql, params) > 0;
    }


}
