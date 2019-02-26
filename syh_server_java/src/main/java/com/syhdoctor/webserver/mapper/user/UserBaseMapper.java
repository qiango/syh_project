package com.syhdoctor.webserver.mapper.user;

import com.syhdoctor.common.utils.EnumUtils.OrderTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.VisitCategoryEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UserBaseMapper extends BaseMapper {


    /**
     * @param userId
     * @return
     */
    public long getUserMessageCount(long userId) {
        String sql = "select count(messagetype) count " +
                "from (select message_type messagetype " +
                "      from message " +
                "      where type = 1 " +
                "        and sendid = ? " +
                "        and ifnull(`read`, 0) = 0 " +
                "      group by message_type)m";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 查询消息列表
     *
     * @param userId
     * @return
     */
    public List<Map<String, Object>> getUserMessageList(long userId, int pageIndex, int pageSize) {
        String sql = "SELECT id,url,name,message_type AS messagetype,type_name AS typename,message_text AS messagetext,message_subtext AS messagesubtext,`read` AS isread,create_time AS createtime " +
                "FROM message WHERE id IN (SELECT max( id ) FROM message WHERE type = 1 AND sendid = ? AND ifnull( delflag, 0 ) = 0 GROUP BY message_type )";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        return queryForList(pageSql(sql, " order by id desc "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 查询消息详细
     *
     * @param id
     * @return
     */
    public Map<String, Object> getUserMessageDetailed(long id) {
        String sql = "SELECT id,message_text AS messagetext FROM message WHERE id =? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return queryForMap(sql, params);
    }

    public boolean updateMessageReadStatus(int messagetype) {
        String sql = " update message set `read`=1 where message_type=? and type=1 ";
        List<Object> params = new ArrayList<>();
        params.add(messagetype);
        return update(sql, params) > 0;
    }

    public Map<String, Object> getMessageType(long id) {
        String sql = " select message_type messagetype from message where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return queryForMap(sql, params);
    }

    /**
     * 手机号码查找用户
     *
     * @param phone
     * @return
     */
    public Map<String, Object> getUser(String phone) {
        String sql = " select id,isinformation,token from user_account where phone=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(phone);
        return queryForMap(sql, params);
    }

    /**
     * 用户id查找用户
     *
     * @param userId
     * @return
     */
    public Map<String, Object> getUser(long userId) {
        String sql = " select id,headpic,name,userno,integral,gender,birthday,areas,cardno,phone,openid,isinformation from user_account where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        return queryForMap(sql, params);
    }

    /**
     * 查询用户
     *
     * @param openid 用户openID
     */
    public Map<String, Object> getUserAccount(String openid, int opentype) {
        String sql = "select uo.id,uo.userid,uo.openid from user_openid uo left join user_account ua on uo.userid=ua.id and ifnull(ua.delflag,0)=0 where uo.openid=? and uo.opentype=? ";
        List<Object> params = new ArrayList<>();
        params.add(openid);
        params.add(opentype);
        return queryForMap(sql, params);
    }

    /**
     * 查询用户
     *
     * @param openid 用户openID
     */
    public Map<String, Object> getUserOpenId(String openid, int opentype) {
        String sql = "select id,openid,userid from user_openid where openid=? and opentype=? ";
        List<Object> params = new ArrayList<>();
        params.add(openid);
        params.add(opentype);
        return queryForMap(sql, params);
    }

    /**
     * 添加用户
     *
     * @param phone           手机
     * @param token           秘钥
     * @param registerChannel 渠道
     * @return
     */
    public long addUser(String phone, String token, int registerChannel) {
        String sql = " INSERT INTO user_account (id, account, phone, userno, token, register_channel, isinformation, delflag, create_time) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        long userId = getId("user_account");
        params.add(userId);
        params.add(phone);
        params.add(phone);
        params.add(UnixUtil.addZeroForNum(userId, 1));
        params.add(token);
        params.add(registerChannel);
        params.add(0);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        insert(sql, params);
        return userId;
    }

    /**
     * 添加用户
     *
     * @param phone           手机
     * @param token           秘钥
     * @param registerChannel 渠道
     * @return
     */
    public long addUser(String phone, String name, String token, int registerChannel) {
        String sql = " INSERT INTO user_account (id, account, phone, userno, token, register_channel, isinformation, delflag, create_time,name) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        long userId = getId("user_account");
        params.add(userId);
        params.add(phone);
        params.add(phone);
        params.add(UnixUtil.addZeroForNum(userId, 1));
        params.add(token);
        params.add(registerChannel);
        params.add(0);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(name);
        insert(sql, params);
        return userId;
    }

    /**
     * 添加用户信息
     *
     * @param userid     用户id
     * @param headpic    头像
     * @param name       名字
     * @param namePinyin 名字拼音
     * @param gender     性别(0:未知，1：男，2：女，9:未说明)
     * @param birthday   生日
     * @param areas      地区
     * @return
     */
    public boolean addUserInfo(long userid, String headpic, String name, String namePinyin, int gender, String cardno, long birthday, String areas, int age) {
        String sql = " UPDATE user_account SET headpic=? , name=?, name_pinyin=? , gender=?,cardno=? ,birthday=?,areas=?,age=? WHERE id=? ";
        List<Object> params = new ArrayList<>();
        params.add(headpic);
        params.add(name);
        params.add(namePinyin);
        params.add(gender);
        params.add(cardno);
        params.add(birthday);
        params.add(areas);
        params.add(age);
        params.add(userid);
        return update(sql, params) > 0;
    }

    /**
     * 信息完善状态改为完善状态
     *
     * @param userid
     * @return
     */
    public boolean isinformation(long userid) {
        String sql = " UPDATE user_account SET isinformation=1 WHERE id=? ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        return update(sql, params) > 0;
    }


    /**
     * 用户列表
     *
     * @param name
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getUserList(String name, String phone, int pageIndex, int pageSize, IMapperResult callback) {
        String sql = " select ua.id,ua.userno,name,headpic,phone,gender,ua.create_time createtime,CONCAT(pr.value,ci.value,di.value )areas from user_account ua " +
                "left join code_area pr on pr.code=substring_index(ua.areas,',',1) " +
                "left join code_area ci on ci.code=substring_index(substring_index(areas,',',2),',',-1) " +
                "left join code_area di on di.code=substring_index(areas,',',-1) " +
                "where ifnull(ua.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and ua.phone like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        return query(pageSql(sql, " order by ua.create_time desc "), pageParams(params, pageIndex, pageSize), (ResultSet res, int row) -> {
            Map<String, Object> result = resultToMap(res);
            if (callback != null) {
                callback.result(result);
            }
            return result;
        });
    }

    /**
     * 用户列表
     *
     * @param name
     * @return
     */
    public long getUserCount(String name, String phone) {
        String sql = " select count(id) count from user_account where ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and phone like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 健康档案
     *
     * @param userId
     * @return
     */
//    public Map<String, Object> getHealth(long userId) {
//        String sql = " select uh.id,uh.height,uh.weight,uh.history,uh.treatment,uh.habitlife,ua.name,ua.gender,ua.age,ua.cardno from user_health_records uh left join user_account ua on uh.userid=ua.id where ifnull(uh.delflag,0)=0 and uh.userid=? ";
//        Map<String, Object> map= queryForMap(sql, userId);
//        if(map!=null){
//            String his= ModelUtil.getStr(map,"history");
//            if(StrUtil.isEmpty(his)){
//                map.put("history","暂无");
//            }
//        }
//        return map;
//    }
    public Map<String, Object> getHealth(long userId) {
        String sql = " select id,height,weight,history,treatment,habitlife from user_health_records where ifnull(delflag,0)=0 and userid=? ";
        Map<String, Object> map = queryForMap(sql, userId);
        if (map != null) {
            String his = ModelUtil.getStr(map, "history");
            if (StrUtil.isEmpty(his)) {
                map.put("history", "暂无");
            }
        }
        return map;
    }

    public Map<String, Object> getUserHeath(long userId) {
        String sql = " select uh.id,uh.height,uh.weight,uh.history,uh.treatment,uh.habitlife,ua.name,ua.gender,ua.age,ua.cardno from user_account ua left join user_health_records uh on ua.id=uh.userid where ifnull(ua.delflag,0)=0 and ua.id=? ";
        Map<String, Object> map = queryForMap(sql, userId);
        if (map != null) {
            String his = ModelUtil.getStr(map, "history");
            if (StrUtil.isEmpty(his)) {
                map.put("history", "暂无");
            }
            map.put("gender", ModelUtil.getInt(map, "gender") == 1 ? "男" : "女");
        }
        return map;
    }

    public Map<String, Object> getUserHeaths(long userId) {
        String sql = " select uh.id,uh.height,uh.weight,uh.history,uh.treatment,uh.habitlife,ua.name,ua.gender,ua.age,ua.cardno from user_account ua left join user_health_records uh on ua.id=uh.userid where ifnull(ua.delflag,0)=0 and ua.id=? ";
        Map<String, Object> map = queryForMap(sql, userId);
        if (map != null) {
            String his = ModelUtil.getStr(map, "history");
            if (StrUtil.isEmpty(his)) {
                map.put("history", "暂无");
            }
            map.put("gename", ModelUtil.getInt(map, "gender") == 1 ? "男" : "女");
        }
        return map;
    }

    /**
     * 健康档案
     *
     * @param userId
     * @return
     */
    public Map<String, Object> getUserHealth(long userId) {
        String sql = " select ua.id, ua.name, ua.gender, ua.birthday, uhr.weight, uhr.height, uhr.history " +
                "from user_account ua " +
                "       left join user_health_records uhr on ua.id = uhr.userid and ifnull(uhr.delflag, 0) = 0 " +
                "where ua.id = ? ";
        return queryForMap(sql, userId);
    }

    /**
     * 添加健康档案
     *
     * @param userId    用户id
     * @param height    身高
     * @param weight    体重
     * @param history   患病史
     * @param treatment 治疗方案
     * @param habitlife 生活习惯
     * @return
     */
    public boolean addHealth(long userId, double height, double weight, String history, String treatment, String habitlife) {
        String sql = " insert into user_health_records (userid, height, weight, history, treatment, habitlife, delflag, create_time) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(height);
        params.add(weight);
        params.add(history);
        params.add(treatment);
        params.add(habitlife);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    /**
     * 添加健康档案
     *
     * @param userId    用户id
     * @param height    身高
     * @param weight    体重
     * @param history   患病史
     * @param treatment 治疗方案
     * @param habitlife 生活习惯
     * @return
     */
    public boolean updateHealth(long userId, double height, double weight, String history, String treatment, String habitlife) {
        String sql = " update user_health_records set height=?,weight=?,history=?,treatment=?,habitlife=?,modify_time=? where userid=? ";
        List<Object> params = new ArrayList<>();
        params.add(height);
        params.add(weight);
        params.add(history);
        params.add(treatment);
        params.add(habitlife);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(userId);
        return update(sql, params) > 0;
    }

    /**
     * 是否签到
     *
     * @param userid 用户id
     * @return
     */
    public long userSignFlag(long userid) {
        String sql = " select count(id) count from user_sign_in where from_unixtime(sign_time/1000,'%Y-%m-%d')=date_format(now(), '%Y-%m-%d') and userid=? ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 签到
     *
     * @param userid 用户id
     * @return
     */
    public boolean userSignIn(long userid) {
        String sql = " INSERT INTO user_sign_in(userid,sign_time,delflag,create_time) VALUES (?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    /**
     * 修改积分
     *
     * @param userid
     * @param integral
     * @return
     */
    public boolean updateUserIntegral(long userid, int integral) {
        String sql = " update user_account set integral=integral+? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(integral);
        params.add(userid);
        return update(sql, params) > 0;
    }

    /**
     * 添加积分详细
     *
     * @param userid
     * @param type
     * @param integral
     * @return
     */
    public boolean addUserIntegralDetailed(long userid, int type, int integral) {
        String sql = " insert into user_integral_detailed(userid, integral, type, create_time) VALUES (?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(integral);
        params.add(type);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    /**
     * 积分详细数量
     *
     * @param userid
     * @param type
     * @return
     */
    public long getUserIntegralDetailed(long userid, int type) {
        String sql = " select count(id) count from user_integral_detailed where userid=? and type=? ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(type);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 积分详细列表
     *
     * @param userid
     * @param callback
     * @return
     */
    public List<Map<String, Object>> userIntegralList(long userid, int pageIndex, int pageSize, IMapperResult callback) {
        String sql = " select integral,type,create_time createtime from user_integral_detailed where userid=? ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        return query(pageSql(sql, " order by id desc "), pageParams(params, pageIndex, pageSize), (ResultSet res, int row) -> {
            Map<String, Object> result = resultToMap(res);
            if (callback != null) {
                callback.result(result);
            }
            return result;
        });
    }

    /**
     * 当月积分
     *
     * @param userid
     * @return
     */
    public long thisMonthUserIntegralCount(long userid) {
        String sql = " select count(integral) count from user_integral_detailed where userid=? and from_unixtime(create_time/1000,'%Y-%m')=date_format(now(),'%Y-%m') ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 总积分
     *
     * @param userid
     * @return
     */
    public long totalUserIntegralCount(long userid) {
        String sql = " select count(integral) count from user_integral_detailed where userid=?";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public Map<String, Object> getUserAreas(long userId) {
        String sql = " select ua.id, " +
                "       CONCAT(pr.value, ci.value, di.value)areas " +
                "from user_account ua " +
                "       left join code_area pr on pr.code = substring_index(ua.areas, ',', 1) " +
                "       left join code_area ci on ci.code = substring_index(substring_index(areas, ',', 2), ',', -1) " +
                "       left join code_area di on di.code = substring_index(areas, ',', -1) " +
                "where ifnull(ua.delflag, 0) = 0 " +
                "  and ua.id = ? ";
        return queryForMap(sql, userId);
    }

    public long addUserOpenid(String openid, int source, int opentype) {
        String sql = " insert into user_openid(openid,source,opentype,create_time)values(?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(openid);
        params.add(source);
        params.add(opentype);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params, "id");
    }

    //是否关注公众号
    public boolean updateUserOpen(long id, int issubscribe, int opentype) {
        String sql = " update user_openid set issubscribe=? where id=? and opentype=? ";
        List<Object> params = new ArrayList<>();
        params.add(issubscribe);
        params.add(id);
        params.add(opentype);
        return update(sql, params) > 0;
    }

    //绑定用户
    public boolean updateUserOpen(long id, long userid) {
        String sql = " update user_openid set userid=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(id);
        return update(sql, params) > 0;
    }

    //解绑
    public boolean updateUserOpen(long userid, int opentype) {
        String sql = " update user_openid set userid=0 where userid=? and opentype=? ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(opentype);
        return update(sql, params) > 0;
    }

    //设置分享人
    public boolean updateUserOpenShareUserId(long shareuserid, long id) {
        String sql = " update user_openid set shareuserid=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(shareuserid);
        params.add(id);
        return update(sql, params) > 0;
    }

    public Map<String, Object> getOpenId(long userId, int opentype) {
        String sql = " select userid,openid from user_openid where userid=? and opentype=? ";
        return queryForMap(sql, userId, opentype);
    }

    public Map<String, Object> getOpenIdType(long id) {
        String sql = " select opentype,shareuserid,shareid from user_openid where id=? ";
        return queryForMap(sql, id);
    }


    public List<Map<String, Object>> getUserOrderList(long userid, int pageIndex, int pageSize) {
        String sql = "select m.*,dp.prescriptionid " +
                "from ( " +
                "       select dpo.id, " +
                "              dpo.diagnosis, " +
                "              dpo.dis_describe   disdescribe, " +
                "              dpo.create_time as createtime, " +
                "              dpo.actualmoney    price, " +
                "              ue.id              evaluateid, " +
                "              1                  ordertype " +
                "       from doctor_problem_order dpo " +
                "              left join user_evaluate ue on dpo.id = ue.order_number and ue.ordertype = 1 " +
                "       where ifnull(dpo.delflag, 0) = 0 " +
                "         and dpo.userid = ? " +
                "         and dpo.states = 4 " +
                "       union all " +
                "       select dpo.id, " +
                "              dpo.diagnosis, " +
                "              dpo.dis_describe disdescribe, " +
                "              dpo.create_time  createtime, " +
                "              dpo.actualmoney  price, " +
                "              ue.id as         evaluateid, " +
                "              2                ordertype " +
                "       from doctor_phone_order dpo " +
                "              left join user_account ua on dpo.userid = ua.id " +
                "              left join user_evaluate ue on dpo.id = ue.order_number and ue.ordertype = 2 " +
                "       where ifnull(dpo.delflag, 0) = 0 " +
                "         and dpo.userid = ? " +
                "         and dpo.status = 4 " +
                "       union all " +
                "       select dvo.id, " +
                "              dvo.guidance     diagnosis, " +
                "              dvo.dis_describe disdescribe, " +
                "              dvo.create_time  createtime, " +
                "              dvo.actualmoney  price, " +
                "              ue.id            evaluateid, " +
                "              3                ordertype " +
                "       from doctor_video_order dvo " +
                "              left join user_evaluate ue on dvo.id = ue.order_number and ue.ordertype = 3 " +
                "       where dvo.userid = ? " +
                "         and ifnull(dvo.delflag, 0) = 0 " +
                "         and dvo.status = 4 " +
                "     ) m " +
                "       left join (select orderid,order_type ordertype,max(prescriptionid) prescriptionid " +
                "                  from doc_prescription " +
                "                  where ifnull(delflag, 0) = 0 " +
                "                  group by orderid,order_type) dp on m.id = dp.orderid and m.ordertype = dp.ordertype ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(userid);
        params.add(userid);
        return query(pageSql(sql, " order by m.createtime desc "), pageParams(params, pageIndex, pageSize), (ResultSet res, int num) -> {
            Map<String, Object> map = resultToMap(res);
            if (map != null) {
                //用户订单列表新加字段:
                //guidance:0:不显示,1:未诊断,2:已诊断
                //evaluate:0:不显示,1:未评论,2:以评论
                //record:0:不显示,1:显示问诊记录按钮(针对图文,其他默认0)
                map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
                long evaluateid = ModelUtil.getLong(map, "evaluateid");
                String diagnosis = ModelUtil.getStr(map, "diagnosis");
                int ordertype = ModelUtil.getInt(map, "ordertype");
                long prescriptionid = ModelUtil.getLong(map, "prescriptionid");
                map.put("guidance", 0);
                map.put("evaluate", 0);
                map.put("record", 0);
                if (StrUtil.isEmpty(diagnosis) && prescriptionid == 0) {
                    map.put("guidance", 1);
                } else {
                    map.put("guidance", 2);
                }

                if (evaluateid > 0) {
                    map.put("evaluate", 2);
                } else {
                    map.put("evaluate", 1);
                }
                if (ordertype == OrderTypeEnum.Answer.getCode()) {
                    map.put("record", 1);
                }
            }
            return map;
        });
    }

    /**
     * 订单用户选择的常见病症
     *
     * @param ids
     * @return
     */
    public List<Map<String, Object>> orderAnswerDiseaseList(List<Long> ids) {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        String sql = " select orderid,diseasename value " +
                "from middle_answer_disease " +
                "where orderid in (:ids) and ifnull(delflag,0)=0 ";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        return queryForList(sql, params);
    }


    /**
     * 订单用户选择的常见病症
     *
     * @param ids
     * @return
     */
    public List<Map<String, Object>> orderPhoneDiseaseList(List<Long> ids) {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        String sql = " select orderid, diseasename value  " +
                "from middle_phone_disease " +
                "where orderid in (:ids)  " +
                "  and ifnull(delflag, 0) = 0  ";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        return queryForList(sql, params);
    }

    /**
     * 订单用户选择的常见病症
     *
     * @param ids
     * @return
     */
    public List<Map<String, Object>> orderVideoDiseaseList(List<Long> ids) {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        String sql = " select orderid, diseasename value  " +
                "from middle_video_disease " +
                "where orderid in (:ids)  " +
                "  and ifnull(delflag, 0) = 0  ";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        return queryForList(sql, params);
    }

    /**
     * 健康信息
     *
     * @param userid
     * @return
     */
    public Map<String, Object> userHealthRecords(long userid) {
        String sql = " select id,userid,height,weight,is_marry ismarry from  user_health_records where ifnull(delflag,0)=0 and userid = ? ";
        return queryForMap(sql, userid);
    }

    /**
     * 月经周期
     *
     * @param userid
     * @return
     */
    public String menstrualCycle(long userid) {
        String sql = " select id from middle_user_case " +
                " where ifnull(delflag,0)=0 and basicid in (57,58,59) and userid=? ";
        Map<String, Object> map = queryForMap(sql, userid);
        return ModelUtil.getStr(map, "id");
    }

    /**
     * 经期天数
     *
     * @param userid
     * @return
     */
    public String menstruationDay(long userid) {
        String sql = " select id from middle_user_case " +
                " where ifnull(delflag,0)=0 and basicid in (60,61,62) and userid=? ";
        Map<String, Object> map = queryForMap(sql, userid);
        return ModelUtil.getStr(map, "id");
    }

    /**
     * 病例表
     *
     * @param userid
     * @return
     */
    public Map<String, Object> userCase(long userid) {
        String sql = " select id,userid,issmoking,isdrinking,ischronicillness,isallergy,issurgery,isfamilyhistory, " +
                "  isfertility,mencharage,final_menarche finalmenarche,ismenopause  from user_case " +
                " where ifnull(delflag,0)=0 and userid =? ";
        return queryForMap(sql, userid);
    }

    public Map<String, Object> genderId(long userid) {
        String sql = " select id,gender from user_account where ifnull(delflag,0)=0 and id = ? ";
        return queryForMap(sql, userid);
    }

}
