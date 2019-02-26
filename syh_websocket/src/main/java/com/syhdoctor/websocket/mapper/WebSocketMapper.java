package com.syhdoctor.websocket.mapper;

import com.syhdoctor.websocket.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class WebSocketMapper extends BaseMapper {

    public boolean updateAnswerUserOnline(int isOnline, long orderid) {
        String sql = " update doctor_problem_order set is_app_user_online =? where id=? and ifnull(delflag,0)=0 ";
        return update(sql, isOnline, orderid) > 0;
    }

    public boolean getUserByNO(String userno) {
        String sql = " select count(id) count from user_account where userno=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(userno);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class) > 0;
    }

    public boolean updateAnswerDoctorOnline(int isOnline, long orderid) {
        String sql = " update doctor_problem_order set is_app_doctor_online =? where id=? and ifnull(delflag,0)=0 ";
        return update(sql, isOnline, orderid) > 0;
    }

    public boolean updateVideoUserOnline(int isOnline, long orderid) {
        String sql = " update doctor_video_order set is_app_user_online =? where id=? and ifnull(delflag,0)=0 ";
        return update(sql, isOnline, orderid) > 0;
    }

    public boolean updateVideoDoctorOnline(int isOnline, long orderid) {
        String sql = " update doctor_video_order set is_app_doctor_online =? where id=? and ifnull(delflag,0)=0 ";
        return update(sql, isOnline, orderid) > 0;
    }
}
