package com.syhdoctor.webtask.statistics.service;

import com.syhdoctor.common.utils.EnumUtils.RegisterChannelEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webtask.base.mapper.BaseMapper;
import com.syhdoctor.webtask.statistics.mapper.StatisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2019/1/3
 */
@Service
public class StatisticsService extends BaseMapper {

    @Autowired
    private StatisticsMapper statisticsMapper;

    public boolean saveStatistics() {
        Map<String, Object> moneyYesterday = statisticsMapper.getMoneyYesterday();
        Map<String, Object> moneyAll = statisticsMapper.getMoneyAll();
        Map<String, Object> orderCountYesterday = statisticsMapper.getOrderCountYesterday();
        Map<String, Object> orderCountAll = statisticsMapper.getOrderCountAll();
        Map<String, Object> userCountYesterday = statisticsMapper.getUserCountYesterday();
        Map<String, Object> userCountAll = statisticsMapper.getUserCountAll();
        Map<String, Object> vipCountYesterday = statisticsMapper.getVipCountYesterday();
        Map<String, Object> vipCountAll = statisticsMapper.getVipCountAll();
        boolean flag = statisticsMapper.saveCount(ModelUtil.getLong(moneyYesterday, "sumall"), ModelUtil.getLong(moneyAll, "sumall"), ModelUtil.getLong(orderCountYesterday, "sumcount"), ModelUtil.getLong(orderCountAll, "sumcount"),
                ModelUtil.getLong(userCountYesterday, "sumcount"), ModelUtil.getLong(userCountAll, "sumcount"), ModelUtil.getLong(vipCountYesterday, "count"), ModelUtil.getLong(vipCountAll, "count"));
        log.info(flag + "结果");
        return flag;
    }


     /*
    主页数据统计
     */

    public Map<String, Object> userBasicNumberAll() {
        Map<String, Object> map = new HashMap<>();
        int yesterdayadduser = statisticsMapper.yesterdayAddUser();
        map.put("yesterdayadduser", yesterdayadduser);//昨日新增用户
        double usernumber = statisticsMapper.userNumber(0, new ArrayList<>());   //用户总数量
        map.put("usernumber", (int) usernumber);
        int yesterdayaddvip = statisticsMapper.yesterdayAddVip();
        map.put("yesterdayaddvip", yesterdayaddvip);//昨日新增会员
        int vipnumber = statisticsMapper.vipNumber();
        map.put("vipnumber", vipnumber);//所有会员

        //性别
        DecimalFormat df = new DecimalFormat("#");
        double malenumber = statisticsMapper.userNumber(1, new ArrayList<>());   //男num
//        double femalenumber = statisticsMapper.userNumber(2, 0, 0);   //女num
        int male = Integer.valueOf(df.format(malenumber / usernumber * 100));
        int female = 100 - male;
        map.put("male", male); //男生比例
        map.put("female", female);//女生比例

        //终端
        List<Long> Androidlist = new ArrayList<>();
        Androidlist.add((long) RegisterChannelEnum.Android.getCode());
        Androidlist.add((long) RegisterChannelEnum.WechatAndroid.getCode());
        double androidnum = statisticsMapper.userNumber(0, Androidlist);//安卓数量
//        map.put("androidnum",androidnum);
        int android = Integer.valueOf(df.format(androidnum / usernumber * 100));
        map.put("android", android); //安卓比例
        List<Long> Ioslist = new ArrayList<>();
        Ioslist.add((long) RegisterChannelEnum.Ios.getCode());
        Ioslist.add((long) RegisterChannelEnum.WechatIos.getCode());
        double iosnum = statisticsMapper.userNumber(0, Ioslist);//ios数量
//        map.put("iosnum",iosnum);
        int ios = Integer.valueOf(df.format(iosnum / usernumber * 100));
        map.put("ios", ios);//ios比例
        map.put("terminalother", 100 - ios - android);//其他比例

        //渠道
        List<Long> applist = new ArrayList<>();//app
        applist.add((long) RegisterChannelEnum.Android.getCode());
        applist.add((long) RegisterChannelEnum.Ios.getCode());
        double appnum = statisticsMapper.userNumber(0, applist);
        int app = Integer.valueOf(df.format(appnum / usernumber * 100));
        map.put("app", app);//app比例
        List<Long> publicNolist = new ArrayList<>();//公众号
        publicNolist.add((long) RegisterChannelEnum.WechatAndroid.getCode());
        publicNolist.add((long) RegisterChannelEnum.WechatIos.getCode());
        double publicNonum = statisticsMapper.userNumber(0, publicNolist);
        int publicNo = Integer.valueOf(df.format(publicNonum / usernumber * 100));
        map.put("publicNo", publicNo);//公众号比例
        List<Long> leyangyunlist = new ArrayList<>();//乐养云
        leyangyunlist.add((long) RegisterChannelEnum.Kangyang.getCode());
        double leyangyunnum = statisticsMapper.userNumber(0, leyangyunlist);
        int leyangyun = Integer.valueOf(df.format(leyangyunnum / usernumber * 100));
        map.put("leyangyun", leyangyun);//乐养云比例
        map.put("channelother", 100 - app - publicNo - leyangyun);//其他
        return map;
    }

    /**
     * 用户统计
     *
     * @return
     */
    public void userStatisticsAdd() {
        Map<String, Object> map = userBasicNumberAll();
        int yesterdayadduser = ModelUtil.getInt(map, "yesterdayadduser");
        int usernumber = ModelUtil.getInt(map, "usernumber");
        int yesterdayaddvip = ModelUtil.getInt(map, "yesterdayaddvip");
        int vipnumber = ModelUtil.getInt(map, "vipnumber");
        int male = ModelUtil.getInt(map, "male");
        int female = ModelUtil.getInt(map, "female");
        int android = ModelUtil.getInt(map, "android");
        int ios = ModelUtil.getInt(map, "ios");
        int terminalother = ModelUtil.getInt(map, "terminalother");
        int app = ModelUtil.getInt(map, "app");
        int publicNo = ModelUtil.getInt(map, "publicNo");
        int leyangyun = ModelUtil.getInt(map, "leyangyun");
        int channelother = ModelUtil.getInt(map, "channelother");
        statisticsMapper.deluserStatistics();//删除旧数据
        statisticsMapper.userStatisticsAdd(yesterdayadduser, usernumber, yesterdayaddvip, vipnumber, male, female, android, ios, terminalother, app, publicNo, leyangyun, channelother);
    }


    /**
     * 年龄柱状图
     */
    public void ageAdd() {
        int ageone = statisticsMapper.ageOne();
        int agetwo = statisticsMapper.ageTwo();
        int agethree = statisticsMapper.ageThree();
        int agefour = statisticsMapper.ageFour();
        int agefive = statisticsMapper.ageFive();
        int agesix = statisticsMapper.ageSix();
        int ageseven = statisticsMapper.ageSeven();
        statisticsMapper.deluserStatisticsAge();//删除旧数据
        statisticsMapper.userStatisticsAgeAdd(ageone, agetwo, agethree, agefour, agefive, agesix, ageseven);
    }

    /**
     * 地域
     */
    public void addUserStatisticsRegion() {
        List<Map<String, Object>> list = statisticsMapper.getRegionList();
        if (list.size() > 0) {
            statisticsMapper.updateRegion();
            for (Map<String, Object> map : list) {
                String name = ModelUtil.getStr(map, "name");
                int count = ModelUtil.getInt(map, "value");
                statisticsMapper.addUserStatisticsRegion(name, count);
            }
        }
    }


}
