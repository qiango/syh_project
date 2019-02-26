package com.syhdoctor.webtask.pushapp.mapper;

import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webtask.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class PushAppMapper extends BaseMapper {

    /**
     * 问诊七天后用户推送
     *
     * @return
     */
    public List<Map<String, Object>> getProblemOrderBySevenDays() {
        String sql = "select dpo.id, dpo.create_time as createtime, dpo.userid, dpoe.dplatform,dpoe.dtoken " +
                "from doctor_problem_order dpo " +
                "       left join doctor_problem_order_extend dpoe on dpoe.orderid = dpo.id " +
                "where unix_timestamp(now()) - dpo.create_time / 1000 > 604800 " +
                "  and states = 4 " +
                "  and ifnull(sevendayspush, 0) = 0";
        return queryForList(sql);
    }

    public boolean updateProblemOrderBySevenDays(long id) {
        String sql = "update doctor_problem_order set sevendayspush=1 where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 急诊七天后用户推送
     *
     * @return
     */
    public List<Map<String, Object>> getPhoneOrderBySevenDays() {
        String sql = "select dpo.id, dpo.create_time as createtime, dpo.userid,dpoe.uplatform, dpoe.utoken " +
                "from doctor_phone_order dpo " +
                "       left join doctor_problem_order_extend dpoe on dpoe.orderid = dpo.id " +
                "where unix_timestamp(now()) - dpo.create_time / 1000 > 604800 " +
                "  and status = 4 " +
                "  and ifnull(sevendayspush, 0) = 0";
        return queryForList(sql);
    }

    public boolean updatePhoneOrderOrderBySevenDays(long id) {
        String sql = "update doctor_phone_order set sevendayspush=1 where id=?";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }


    /**
     * 查询时间段内等待发送的订单
     *
     * @return
     */
    public List<Map<String, Object>> getWaitDoctorProblemOrder() {
        String sql = "select dpo.userid, " +
                "       dpo.doctorid, " +
                "       sor.id          as sorid, " +
                "       ua.name, " +
                "       dpo.create_time as createtime, " +
                "       sor.id, " +
                "       sor.orderid, " +
                "       sor.halfhour, " +
                "       sor.onehour, " +
                "       sor.threehour, " +
                "       sor.sixhour, " +
                "       sor.twelvehour, " +
                "       sor.eighteenhour, " +
                "       sor.twentyhour, " +
                "       dpoe.uplatform, " +
                "       dpoe.utoken, " +
                "       dpoe.dplatform, " +
                "       dpoe.dtoken " +
                "from doctor_problem_order dpo " +
                "       left join sending_order_rules sor on dpo.id = sor.orderid " +
                "       left join doctor_problem_order_extend dpoe on dpoe.orderid = dpo.id " +
                "       left join user_account ua on dpo.userid= ua.id " +
                "where dpo.states = 2 " +
                "  and unix_timestamp(now()) * 1000 - dpo.create_time < 74000000 " +
                "  and unix_timestamp(now()) * 1000 - dpo.create_time > 1800000 ";
        return queryForList(sql);
    }

    /**
     * 电话问诊前十分钟
     *
     * @return
     */
    public List<Map<String, Object>> getPhoneTenTimeList() {
        String sql = " SELECT " +
                " dpo.id as orderid, " +
                " dpo.userid, " +
                " dpo.doctorid, " +
                " dpo.create_time AS createtime, " +
                " dpoe.uplatform AS uplatform, " +
                " dpoe.utoken AS utoken, " +
                " dpoe.dplatform AS dplatform, " +
                " dpoe.dtoken AS dtoken  " +
                " FROM doctor_phone_order dpo " +
                " LEFT JOIN doctor_phone_order_extend dpoe ON dpoe.orderid = dpo.id  " +
                " WHERE dpo.STATUS = 2  " +
                " AND unix_timestamp(now())*1000 > dpo.subscribe_time - 600000  " +
                " AND dpo.istentime = 0 ";
        return queryForList(sql);
    }

    /**
     * 视频问诊前十分钟
     *
     * @return
     */
    public List<Map<String, Object>> getVideoTenTimeList() {
        String sql = " SELECT dpo.id          as orderid, " +
                "       dpo.userid, " +
                "       dpo.doctorid, " +
                "       dpo.create_time AS createtime, " +
                "       dpoe.uplatform  AS uplatform, " +
                "       dpoe.utoken     AS utoken, " +
                "       dpoe.dplatform  AS dplatform, " +
                "       dpoe.dtoken     AS dtoken " +
                " FROM doctor_video_order dpo " +
                "       LEFT JOIN doctor_video_order_extend dpoe ON dpoe.orderid = dpo.id " +
                " WHERE dpo.STATUS = 2 " +
                "  AND unix_timestamp(now()) * 1000 > dpo.subscribe_time - 600000 " +
                "  AND dpo.istentime = 0 ";
        return queryForList(sql);
    }

    /**
     * 视频问诊开始推送
     *
     * @return
     */
    public List<Map<String, Object>> getVideoStartList() {
        String sql = " SELECT dpo.id          as orderid, " +
                "       dpo.userid, " +
                "       dpo.doctorid, " +
                "       dpo.create_time AS createtime, " +
                "       dpoe.uplatform  AS uplatform, " +
                "       dpoe.utoken     AS utoken, " +
                "       dpoe.dplatform  AS dplatform, " +
                "       dpoe.dtoken     AS dtoken " +
                " FROM doctor_video_order dpo " +
                "       LEFT JOIN doctor_video_order_extend dpoe ON dpoe.orderid = dpo.id " +
                " WHERE dpo.STATUS = 3 " +
                "  AND unix_timestamp(now()) * 1000 > dpo.subscribe_time" +
                "  AND dpo.isstart = 0 ";
        return queryForList(sql);
    }

    public Map<String, Object> userName(long userid) {
        String sql = " select name from user_account where ifnull(delflag,0)=0 and id = ? ";
        return queryForMap(sql, userid);
    }

    /**
     * 电话订单前十分钟是否推送
     *
     * @param orderid
     * @return
     */
    public boolean phoneisTenTime(long orderid) {
        String sql = " update doctor_phone_order set istentime=1 where id =? ";
        return update(sql, orderid) > 0;
    }

    /**
     * 视频订单前十分钟是否推送
     *
     * @param orderid
     * @return
     */
    public boolean videoisTenTime(long orderid) {
        String sql = " update doctor_video_order set istentime=1 where id =? ";
        return update(sql, orderid) > 0;
    }

    /**
     * 视频订单开始时是否推送
     *
     * @param orderid
     * @return
     */
    public boolean videoisStart(long orderid) {
        String sql = " update doctor_video_order set isstart=1 where id =? ";
        return update(sql, orderid) > 0;
    }


    public boolean updateDoctorOrderRules(int id) {
        String sql = "update sending_order_rules set halfhour=true ,onehour=true ,threehour=true ,sixhour=true ,twelvehour=true ,eighteenhour=true ,twentyhour=true where id=?";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean updateDoctorOrderRules(int id, String hours) {
        String sql = "update sending_order_rules set " + hours + "=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(false);
        params.add(id);
        return update(sql, params) > 0;
    }


    /**
     * 添加push消息表
     *
     * @return
     */
    public boolean addPushApp(String title, String content, int type, String typeName, int receiveId, int receiveType, int platform,
                              String xgtoken) {
        String sql = "insert into push_app(title, content, type, typename, receiveid, receivetype, delflag, create_time,ispush,platform,xgtoken)" +
                "values (?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(title);
        params.add(content);
        params.add(type);
        params.add(typeName);
        params.add(receiveId);
        params.add(receiveType);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(0);
        params.add(platform);
        params.add(xgtoken);
        return insert(sql, params) > 0;
//        Map<String,Object> map=new HashMap<>();
//        map.put("title",title);
//        map.put("content",content);
//        map.put("type",type);
//        map.put("typeName",typeName);
//        map.put("receiveid",receiveId);
//        map.put("receivetype",receiveType);
//        map.put("create_time",UnixUtil.getNowTimeStamp());
//        map.put("ispush",0);
//        map.put("platform",platform);
//        map.put("xgtoken",xgtoken);
//        try {
//            ActivemqUtil activemqUtil=ActivemqUtil.getInstance();
//            activemqUtil.sendPushMessage("appMessage",map);
//        }catch (Exception e){
//            log.error(">>推送消息"+map+",存入MQ报错>>>" + e.getMessage());
//        }
//        return true;
    }

    public List<Map<String, Object>> getPushUserMessage() {
        String sql = "   select pa.id, " +
                "       pa.platform, " +
                "       pa.title, " +
                "       pa.content, " +
                "       pa.type, " +
                "       pa.typename, " +
                "       pa.xgtoken             token, " +
                "       pa.receivetype, " +
                "       pa.create_time         createtime, " +
                "       dpo.is_app_user_online isonline " +
                "from push_app pa " +
                "       left join doctor_problem_order dpo on pa.typename = dpo.id and ifnull(dpo.delflag, 0) = 0 and pa.type = 6 " +
                "where pa.receivetype = 1 " +
                "  and ifnull(pa.ispush, 0) = 0  ";
        return queryForList(sql);
    }

    public List<Map<String, Object>> getPushDoctorMessage() {
        String sql = "   select pa.id, " +
                "       pa.platform, " +
                "       pa.title, " +
                "       pa.content, " +
                "       pa.type, " +
                "       pa.typename, " +
                "       pa.xgtoken             token, " +
                "       pa.receivetype, " +
                "       pa.create_time         createtime, " +
                "       dpo.is_app_doctor_online isonline " +
                "from push_app pa " +
                "       left join doctor_problem_order dpo on pa.typename = dpo.id and ifnull(dpo.delflag, 0) = 0 and pa.type = 6 " +
                "where pa.receivetype = 2 " +
                "  and ifnull(pa.ispush, 0) = 0  ";
        return queryForList(sql);
    }


    public boolean updateDoctorMessage(int Id, String result) {
        String sql = "update push_app set ispush=?,result=?,sendtime=? where id= ? ";
        List<Object> params = new ArrayList<>();
        params.add(1);
        params.add(result);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(Id);
        return update(sql, params) > 0;
    }


}
