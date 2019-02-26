package com.syhdoctor.webserver.mapper.homepage;

import com.syhdoctor.common.utils.EnumUtils.OrderTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePageBaseMapper extends BaseMapper {


    /**
     * 用户首页基本信息
     *
     * @return
     */
    public Map<String, Object> getUserStatistics() {
        String sql = " select yesterdayadduser,usernumber,yesterdayaddvip,vipnumber,male,female,android,ios,terminalother,app,publicNo,leyangyun,channelother " +
                " from user_statistics " +
                " where ifnull(delflag, 0) = 0 ";
//                "  and TO_DAYS(now()) = TO_DAYS(from_unixtime(create_time / 1000, '%Y-%m-%d %H:%i:%s')) ";
        Map<String, Object> map = queryForMap(sql);
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

    /**
     * 统计年龄柱状图
     *
     * @return
     */
    public Map<String, Object> getUserStatisticsAge() {
        String sql = " select age_one ageone, age_two agetwo, age_three agethree, age_four agefour, age_five agefive, age_six agesix, age_seven ageseven " +
                " from user_statistics_age " +
                " where ifnull(delflag, 0) = 0 ";
//                "  and TO_DAYS(now()) = TO_DAYS(from_unixtime(create_time / 1000, '%Y-%m-%d %H:%i:%s')) ";
        return queryForMap(sql);
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

    public Map<String, Object> getStatistics() {
        String sql = "select * from management_condition where statistical_time=?";
        Map<String, Object> map = queryForMap(sql, UnixUtil.getYesterdayStart());
        if (map != null) {
            map.put("money_yesterday", PriceUtil.findPrice(ModelUtil.getLong(map, "money_yesterday")));
            map.put("money_all", PriceUtil.findPrice(ModelUtil.getLong(map, "money_all")));
        }
        return map;
    }

    public List<Map<String, Object>> getNumByOrderType(int type, long startTime, long endTime) {
        String sql = "";
        switch (OrderTypeEnum.getValue(type)) {
            case Answer:
                sql = "SELECT from_unixtime(create_time / 1000, '%Y-%m-%d') day, count(dpo.id) count " +
                        " FROM doctor_problem_order dpo " +
                        " WHERE create_time between ? and ? " +
                        " and ifnull(delflag, 0) = 0 " +
                        "  AND paystatus = 1 " +
                        " group by day ";
                break;
            case Phone:
                sql = "SELECT from_unixtime(create_time / 1000, '%Y-%m-%d') day, count(dpo.id) count " +
                        " FROM doctor_phone_order dpo " +
                        " WHERE create_time between ? and ? " +
                        " and ifnull(delflag, 0) = 0 " +
                        "  AND paystatus = 1 " +
                        " group by day ";
                break;
            case Video:
                sql = "SELECT from_unixtime(create_time / 1000, '%Y-%m-%d') day, count(dpo.id) count " +
                        " FROM doctor_video_order dpo " +
                        " WHERE create_time between ? and ? " +
                        " and ifnull(delflag, 0) = 0 " +
                        "  AND paystatus = 1 " +
                        " group by day ";
                break;
            default:
                break;
        }
        return queryForList(sql, startTime, endTime);
    }

    //总访问量
    public int getYesterDayNum(long startTime, long endTime) {
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
        list.add(startTime);
        list.add(endTime);
        list.add(startTime);
        list.add(endTime);
        return ModelUtil.getInt(queryForMap(sql, list),"sumcount");
    }

    //会员人次
    public int getTimeAddVip(long startTime, long endTime) {
        String sql = "select count(id) count from user_member where ifnull(delflag,0)=0 and is_enabled=1 and is_expire=1 and create_time between ? and ?";
        Map<String, Object> map = queryForMap(sql, startTime, endTime);
        return ModelUtil.getInt(map, "count");
    }

    //付费人次
    public int getUseMoney(long startTime, long endTime) {
        String sql = "SELECT count(*) count " +
                " FROM (SELECT userid" +
                "      FROM doctor_problem_order dpo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND paystatus = 1" +
                "      GROUP BY dpo.userid" +
                "      UNION" +
                "      SELECT userid" +
                "      FROM doctor_phone_order dpo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND paystatus = 1" +
                "      GROUP BY dpo.userid" +
                "      UNION" +
                "      SELECT userid" +
                "      FROM doctor_video_order dpo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND paystatus = 1" +
                "      GROUP BY dpo.userid" +
                "      UNION" +
                "      SELECT userid" +
                "      FROM vip_order dpo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND pay_status = 1" +
                "      GROUP BY dpo.userid" +
                "      UNION" +
                "      SELECT userid" +
                "      FROM green_order dpo" +
                "      WHERE create_time BETWEEN ?" +
                "                AND ?" +
                "        AND ifnull(delflag, 0) = 0" +
                "        AND paystatus = 1" +
                "      GROUP BY dpo.userid) count";
        List<Object> list = new ArrayList<>();
        list.add(startTime);
        list.add(endTime);
        list.add(startTime);
        list.add(endTime);
        list.add(startTime);
        list.add(endTime);
        list.add(startTime);
        list.add(endTime);
        list.add(startTime);
        list.add(endTime);
        Map<String, Object> map = queryForMap(sql, list);
        return ModelUtil.getInt(map, "count");
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

    public void deluserStatistics() {
        String sql = " update user_statistics set delflag=1 where delflag=0 ";
        update(sql);
    }

    public void deluserStatisticsAge() {
        String sql = " update user_statistics_age set delflag=1 where delflag=0 ";
        update(sql);
    }

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

    /**
     * 年龄柱状图
     *
     * @param ageone
     * @param agetwo
     * @param agethree
     * @param agefour
     * @param agefive
     * @param agesix
     * @param ageseven
     * @return
     */
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
        String sql = " SELECT areaname name,count value FROM user_statistics_region " +
                " where ifnull(delflag,0)=0 " +
                " order by count desc ";
        return queryForList(sql);
    }

    public List<Map<String, Object>> areaAll() {
        String sql = " select value name,0 value from  code_area where parentid = 0 ";
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
