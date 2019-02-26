package com.syhdoctor.webtask.doctorduty.mapper;

import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webtask.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class DoctorDutyMapper extends BaseMapper {


    /**
     * 查询所有医生值班规则
     *
     * @return
     */
    public List<Map<String, Object>> getDoctorDuty() {
        String sql = "select doctorid,starttime,endtime,sunday,monday,tuesday,wednesday,thursday,friday,saturday from doctor_duty_rule where ifnull(delflag,0)=0";
        return queryForList(sql);
    }

    /**
     * 添加问诊值班
     *
     * @param doctorId          医生id
     * @param visitingStartTime 值班开始时间
     * @param visitingEndTime   值班结束时间
     * @return
     */
    public boolean addDoctorInquiry(long doctorId, long visitingStartTime, long visitingEndTime) {
        String sql = "insert into doctor_inquiry(doctorid, visiting_start_time, visiting_end_time, delflag, create_time, create_user)" +
                "values (?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(visitingStartTime);
        params.add(visitingEndTime);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorId);
        return update(sql, params) > 0;
    }

    public long isOnDuty() {
        String sql = "select count(id) as count from doctor_onduty where  " +
                "    UNIX_TIMESTAMP(FROM_UNIXTIME(visiting_start_time/1000,'%Y-%m-%d'))=UNIX_TIMESTAMP(FROM_UNIXTIME(now(),'%Y-%m-%d'))";
        return jdbcTemplate.queryForObject(sql, long.class);
    }


}
