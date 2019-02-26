package com.syhdoctor.webtask.statistics.mapper;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webtask.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2019/1/3
 */
@Repository
public class StatisticsMapper extends BaseMapper {

    //昨日成交金额
    public Map<String, Object> getMoneyYesterday() {
        String sql = "SELECT sum(money) sumall" +
                "  FROM (SELECT sum(dpo.actualmoney) money" +
                "      FROM doctor_problem_order dpo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND paystatus = 1" +
                "      UNION" +
                "      SELECT sum(deo.actualmoney) money" +
                "      FROM doctor_phone_order deo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND paystatus = 1" +
                "      UNION" +
                "      SELECT sum(dvo.actualmoney) money" +
                "      FROM doctor_video_order dvo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND paystatus = 1" +
                "      UNION" +
                "      SELECT sum(dvo.amountmoney) money" +
                "      FROM rechargeable_order dvo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND paystatus = 1" +
                "      UNION" +
                "      SELECT sum(dvo.actualmoney) money" +
                "      FROM green_order dvo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND paystatus = 1" +
                "      UNION" +
                "      SELECT sum(CASE" +
                "                   WHEN order_type = '0' THEN price" +
                "                   WHEN order_type = '1' THEN renewal_fee END) money" +
                "      FROM vip_order dvo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND pay_status = 1) AS sumall";
        List<Object> list = new ArrayList<>();
        list.add(UnixUtil.getYesterdayStart());
        list.add(UnixUtil.getStart());
        list.add(UnixUtil.getYesterdayStart());
        list.add(UnixUtil.getStart());
        list.add(UnixUtil.getYesterdayStart());
        list.add(UnixUtil.getStart());
        list.add(UnixUtil.getYesterdayStart());
        list.add(UnixUtil.getStart());
        list.add(UnixUtil.getYesterdayStart());
        list.add(UnixUtil.getStart());
        list.add(UnixUtil.getYesterdayStart());
        list.add(UnixUtil.getStart());
        return queryForMap(sql, list);
    }

    //昨日订单数(不要会员)
    public Map<String, Object> getOrderCountYesterday() {
        String sql = "SELECT sum(count) sumcount" +
                "  FROM (SELECT count(dpo.id) count" +
                "      FROM doctor_problem_order dpo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND states IN (2, 4, 5, 6, 8)" +
                "      UNION" +
                "      SELECT count(deo.id) count" +
                "      FROM doctor_phone_order deo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND `status` IN (2, 4, 5, 3, 8)" +
                "      UNION" +
                "      SELECT count(dvo.id) count" +
                "      FROM doctor_video_order dvo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND `status` IN (2, 4, 5, 3, 8)" +
                "      UNION" +
                "      SELECT count(dvo.id) count" +
                "      FROM green_order dvo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND `status` IN (2, 4, 5, 3, 6)" +
                "      UNION" +
                "      SELECT count(dvo.id) count" +
                "      FROM rechargeable_order dvo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND paystatus = 1) AS sumcount";
        List<Object> list = new ArrayList<>();
        list.add(UnixUtil.getYesterdayStart());
        list.add(UnixUtil.getStart());
        list.add(UnixUtil.getYesterdayStart());
        list.add(UnixUtil.getStart());
        list.add(UnixUtil.getYesterdayStart());
        list.add(UnixUtil.getStart());
        list.add(UnixUtil.getYesterdayStart());
        list.add(UnixUtil.getStart());
        list.add(UnixUtil.getYesterdayStart());
        list.add(UnixUtil.getStart());
        return queryForMap(sql, list);
    }

    //昨日活跃用户次数
    public Map<String, Object> getUserCountYesterday() {
        String sql = "select sum(count) sumcount" +
                "      from (select count(*) count" +
                "      from (select ui.userid" +
                "            from user_integral_detailed ui" +
                "            where create_time BETWEEN ?" +
                "                      AND ?" +
                "            GROUP BY userid) a" +
                "      UNION" +
                "      select count(*) count" +
                "      from (select di.doctorid" +
                "            from doctor_integral_detailed di" +
                "            where create_time BETWEEN ?" +
                "                      AND ?" +
                "            GROUP BY doctorid) a) b";
        List<Object> list = new ArrayList<>();
        list.add(UnixUtil.getYesterdayStart());
        list.add(UnixUtil.getStart());
        list.add(UnixUtil.getYesterdayStart());
        list.add(UnixUtil.getStart());
        return queryForMap(sql, list);
    }

    //昨日活跃会员次数
    public Map<String, Object> getVipCountYesterday() {
        String sql = "select count(*) count" +
                "      from (select ui.userid" +
                "      from user_integral_detailed ui" +
                "             left join user_member um" +
                "               on ui.userid = um.userid and ifnull(um.delflag, 0) = 0 and um.is_enabled = 1 and um.is_expire = 1" +
                "      where ui.create_time BETWEEN ?" +
                "                AND ?" +
                "      GROUP BY ui.userid) a";
        List<Object> list = new ArrayList<>();
        list.add(UnixUtil.getYesterdayStart());
        list.add(UnixUtil.getStart());
        return queryForMap(sql, list);
    }

    //累计成交金额
    public Map<String, Object> getMoneyAll() {
        String sql = "SELECT sum(money) sumall" +
                "  FROM (SELECT sum(dpo.actualmoney) money" +
                "      FROM doctor_problem_order dpo" +
                "      WHERE ifnull(delflag, 0) = 0" +
                "        AND paystatus = 1" +
                "      UNION" +
                "      SELECT sum(deo.actualmoney) money" +
                "      FROM doctor_phone_order deo" +
                "      WHERE ifnull(delflag, 0) = 0" +
                "        AND paystatus = 1" +
                "      UNION" +
                "      SELECT sum(dvo.actualmoney) money" +
                "      FROM doctor_video_order dvo" +
                "      WHERE  ifnull(delflag, 0) = 0" +
                "        AND paystatus = 1" +
                "      UNION" +
                "      SELECT sum(dvo.amountmoney) money" +
                "      FROM rechargeable_order dvo" +
                "      WHERE  ifnull(delflag, 0) = 0" +
                "        AND paystatus = 1" +
                "      UNION" +
                "      SELECT sum(dvo.actualmoney) money" +
                "      FROM green_order dvo" +
                "      WHERE  ifnull(delflag, 0) = 0" +
                "        AND paystatus = 1" +
                "      UNION" +
                "      SELECT sum(CASE" +
                "                   WHEN order_type = '0' THEN price" +
                "                   WHEN order_type = '1' THEN renewal_fee END) money" +
                "      FROM vip_order dvo" +
                "      WHERE  ifnull(delflag, 0) = 0" +
                "        AND pay_status = 1) AS sumall";
        return queryForMap(sql);
    }

    //累计订单数
    public Map<String, Object> getOrderCountAll() {
        String sql = "SELECT sum(count) sumcount" +
                "      FROM (SELECT count(dpo.id) count" +
                "      FROM doctor_problem_order dpo" +
                "      WHERE ifnull(delflag, 0) = 0" +
                "        AND states IN (2, 4, 5, 6, 8)" +
                "      UNION" +
                "      SELECT count(deo.id) count" +
                "      FROM doctor_phone_order deo" +
                "      WHERE  ifnull(delflag, 0) = 0" +
                "        AND `status` IN (2, 4, 5, 3, 8)" +
                "      UNION" +
                "      SELECT count(dvo.id) count" +
                "      FROM doctor_video_order dvo" +
                "      WHERE ifnull(delflag, 0) = 0" +
                "        AND `status` IN (2, 4, 5, 3, 8)) AS sumcount";
        return queryForMap(sql);
    }

    //累计活跃用户次数
    public Map<String, Object> getUserCountAll() {
        String sql = "select sum(count) sumcount" +
                "      from (select count(*) count" +
                "      from (select ui.userid" +
                "            from user_integral_detailed ui" +
                "            GROUP BY userid) a" +
                "      UNION" +
                "      select count(*) count" +
                "      from (select di.doctorid" +
                "            from doctor_integral_detailed di" +
                "            GROUP BY doctorid) a) b";
        return queryForMap(sql);
    }

    //累计活跃会员次数
    public Map<String, Object> getVipCountAll() {
        String sql = "select count(*) count" +
                "     from (select ui.userid" +
                "      from user_integral_detailed ui" +
                "             left join user_member um" +
                "               on ui.userid = um.userid and ifnull(um.delflag, 0) = 0 and um.is_enabled = 1 and um.is_expire = 1" +
                "      GROUP BY ui.userid) a";
        return queryForMap(sql);
    }

    public boolean saveCount(long a, long b, long c, long d, long e, long f, long g, long h) {
        String sql = "insert into management_condition (money_yesterday,money_all,order_count_yesterday,order_count_all,user_count_yesterday,"
                + "user_count_all,vip_count_yesterday,vip_count_all,statistical_time) values(?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(a);
        params.add(b);
        params.add(c);
        params.add(d);
        params.add(e);
        params.add(f);
        params.add(g);
        params.add(h);
        params.add(UnixUtil.getYesterdayStart());
        return insert(sql, params) > 0;
    }


    /**
     * 昨日新增用户
     *
     * @return
     */
    public int yesterdayAddUser() {
        String sql = "SELECT count(id) count FROM user_account WHERE ifnull(delflag,0)=0 and TO_DAYS(now()) - TO_DAYS(from_unixtime(create_time/1000,'%Y-%m-%d %H-%i-%s')) = 1";
        Map<String, Object> map = queryForMap(sql);
        return ModelUtil.getInt(map, "count");
    }

    /**
     * 用户总量
     *
     * @param gender          1:男,2:女
     * @param registerchannel 注册渠道：1:Android 2：ios,3：微信
     * @return
     */
    public int userNumber(int gender, List<Long> registerchannel) {
        String sql = "SELECT count(id) name FROM user_account WHERE ifnull(delflag,0)=0 ";
        int num = 0;
        Map<String, Object> map = new HashMap<>();
        if (gender != 0) {

            sql += " and gender = ? ";
            num = ModelUtil.getInt(queryForMap(sql, gender), "name");

        } else if (registerchannel.size() > 0) {

            sql += " and register_channel in (:registerchannel) ";
            map.put("registerchannel", registerchannel);
            List<Map<String, Object>> map1 = queryForList(sql, map);
            Map<String, Object> maps = null;
            if (map1 != null && map1.size() > 0) {
                maps = map1.get(0);
            }
            num = ModelUtil.getInt(maps, "name");

        } else {
            num = ModelUtil.getInt(queryForMap(sql), "name");
        }
        return num;
    }

    /**
     * 昨日新增会员
     *
     * @return
     */
    public int yesterdayAddVip() {
        String sql = "select count(id) count from user_member where ifnull(delflag,0)=0 and is_enabled=1 and is_expire=1 and TO_DAYS(now()) - TO_DAYS(from_unixtime(create_time/1000,'%Y-%m-%d %H-%i-%s')) = 1";
        Map<String, Object> map = queryForMap(sql);
        return ModelUtil.getInt(map, "count");
    }

    /**
     * 所有会员
     *
     * @return
     */
    public int vipNumber() {
        String sql = "select count(id) count from user_member where ifnull(delflag,0)=0 and is_enabled=1 and is_expire=1";
        Map<String, Object> map = queryForMap(sql);
        return ModelUtil.getInt(map, "count");
    }

    /**
     * 用户统计
     *
     * @param yesterdayadduser
     * @param usernumber
     * @param yesterdayaddvip
     * @param vipnumber
     * @param male
     * @param female
     * @param android
     * @param ios
     * @param terminalother
     * @param app
     * @param publicNo
     * @param leyangyun
     * @return
     */
    public boolean userStatisticsAdd(int yesterdayadduser, int usernumber, int yesterdayaddvip, int vipnumber, int male, int female, int android, int ios, int terminalother, int app, int publicNo, int leyangyun, int channelother) {
        String sql = " insert into user_statistics (yesterdayadduser, " +
                "                             usernumber, " +
                "                             yesterdayaddvip, " +
                "                             vipnumber, " +
                "                             male, " +
                "                             female, " +
                "                             android, " +
                "                             ios, " +
                "                             terminalother, " +
                "                             app, " +
                "                             publicNo, " +
                "                             leyangyun, " +
                "                             channelother, " +
                "                             delflag, " +
                "                             create_time) " +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)  ";
        List<Object> params = new ArrayList<>();
        params.add(yesterdayadduser);
        params.add(usernumber);
        params.add(yesterdayaddvip);
        params.add(vipnumber);
        params.add(male);
        params.add(female);
        params.add(android);
        params.add(ios);
        params.add(terminalother);
        params.add(app);
        params.add(publicNo);
        params.add(leyangyun);
        params.add(channelother);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    public void deluserStatistics() {
        String sql = " update user_statistics set delflag=1 where delflag=0 ";
        update(sql);
    }


    /**
     * 0-18
     *
     * @return
     */
    public int ageOne() {
        String sql = "select count(id) count from user_account where ifnull(delflag,0)=0 and age between 0 and 18";
        int num = ModelUtil.getInt(queryForMap(sql), "count");
        return num;
    }

    /**
     * 19-30
     *
     * @return
     */
    public int ageTwo() {
        String sql = "select count(id) count from user_account where ifnull(delflag,0)=0 and age between 19 and 30";
        int num = ModelUtil.getInt(queryForMap(sql), "count");
        return num;
    }

    /**
     * 31-40
     *
     * @return
     */
    public int ageThree() {
        String sql = "select count(id) count from user_account where ifnull(delflag,0)=0 and age between 31 and 40";
        int num = ModelUtil.getInt(queryForMap(sql), "count");
        return num;
    }

    /**
     * 41-50
     *
     * @return
     */
    public int ageFour() {
        String sql = "select count(id) count from user_account where ifnull(delflag,0)=0 and age between 41 and 50";
        int num = ModelUtil.getInt(queryForMap(sql), "count");
        return num;
    }

    /**
     * 51-55
     *
     * @return
     */
    public int ageFive() {
        String sql = "select count(id) count from user_account where ifnull(delflag,0)=0 and age between 51 and 55";
        int num = ModelUtil.getInt(queryForMap(sql), "count");
        return num;
    }

    /**
     * 56-60
     *
     * @return
     */
    public int ageSix() {
        String sql = "select count(id) count from user_account where ifnull(delflag,0)=0 and age between 56 and 60";
        int num = ModelUtil.getInt(queryForMap(sql), "count");
        return num;
    }

    /**
     * 60+
     *
     * @return
     */
    public int ageSeven() {
        String sql = "select count(id) count from user_account where ifnull(delflag,0)=0 and age > 60";
        int num = ModelUtil.getInt(queryForMap(sql), "count");
        return num;
    }

    public void deluserStatisticsAge() {
        String sql = " update user_statistics_age set delflag=1 where delflag=0 ";
        update(sql);
    }

    public boolean userStatisticsAgeAdd(int ageone, int agetwo, int agethree, int agefour, int agefive, int agesix, int ageseven) {
        String sql = " insert into user_statistics_age (age_one, " +
                "                                 age_two, " +
                "                                 age_three, " +
                "                                 age_four, " +
                "                                 age_five, " +
                "                                 age_six, " +
                "                                 age_seven, " +
                "                                 delflag, " +
                "                                 create_time) " +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(ageone);
        params.add(agetwo);
        params.add(agethree);
        params.add(agefour);
        params.add(agefive);
        params.add(agesix);
        params.add(ageseven);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    /**
     * 地域
     *
     * @return
     */
    public List<Map<String, Object>> getRegionList() {
        String sql = " select ca.value name,count(left(ua.areas,6)) value " +
                " from user_account ua " +
                "       left join code_area ca on left(ua.areas,6) = ca.code " +
                " where ifnull(ua.delflag,0) = 0 " +
                " group by left(ua.areas,6), ca.value" +
                " order by value desc ";
        return queryForList(sql);
    }

    /**
     * 新增地域到统计表
     *
     * @param areaname
     * @param count
     * @return
     */
    public boolean addUserStatisticsRegion(String areaname, int count) {
        String sql = "  insert into user_statistics_region (areaname, count, delflag, create_time) " +
                " values (?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(areaname);
        params.add(count);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    public boolean updateRegion() {
        String sql = " update user_statistics_region set delflag = 1 where delflag=0  ";
        return update(sql) > 0;
    }


}
