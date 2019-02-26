package com.syhdoctor.webserver.service.homepage;

import com.syhdoctor.common.utils.EnumUtils.DisplaypositionEnum;
import com.syhdoctor.common.utils.EnumUtils.RegisterChannelEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.homepage.HomePageMapper;
import com.syhdoctor.webserver.service.article.ArticleService;
import com.syhdoctor.webserver.service.focusfigure.FocusfigureService;
import com.syhdoctor.webserver.service.microclass.MicroClassService;
import com.syhdoctor.webserver.service.specialistcounseling.SpecialistCounselingService;
import com.syhdoctor.webserver.service.user.UserService;
import com.syhdoctor.webserver.service.video.UserVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public abstract class HomePageBaseService extends BaseService {
    @Autowired
    private FocusfigureService focusfigureService;

    @Autowired
    private MicroClassService microClassService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private SpecialistCounselingService specialistCounselingService;

    @Autowired
    private UserVideoService userVideoService;

    @Autowired
    private HomePageMapper homePageMapper;

    /**
     * 首页
     *
     * @return
     */
    public Map<String, Object> homePage(long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("user", getSimpleUser(userId));
        //todo 是否健康测试
//        result.put("healthtest", 1);
        //banner图
        result.put("bannerlist", focusfigureService.bannerList(DisplaypositionEnum.userTop.getCode(), 6));
        //值班医生
//        result.put("doctorlist", doctorInfoService.getDoctorOnDutyList(0, 0, 1, 3));
        //课程
        result.put("courselist", microClassService.courseList(2));
        //健康知识库
//        result.put("diseaselist", knowledgeService.getAppDiseaseList());
        //资讯
        result.put("articlelist", articleService.getArticleList(1, 2));
        //专病资讯
        result.put("specialCounList", specialistCounselingService.getSpecialCounList());
        //特色专科
        result.put("special", specialistCounselingService.getSpecialList());

        result.put("subscribecount", userVideoService.getUserSubscribeCount(userId));
        Map<String, Object> vipbanner = null;
        //会员弹框
        if (userId > 0) {
            Map<String, Object> vip = userService.isVip(userId);
            if (ModelUtil.getInt(vip, "isvip") == 0) {
                List<Map<String, Object>> list = focusfigureService.bannerList(DisplaypositionEnum.UserLightning.getCode(), 1);
                if (list.size() > 0) {
                    vipbanner = list.get(0);
                }
            }
        }
        result.put("vipbanner", vipbanner);
        return result;
    }

    /**
     * 课程列表+课程分类列表
     *
     * @param typeId    课程类型id
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Cacheable(value = "MicroClass", key = "#root.methodName+#root.args[0]+#root.args[1]+#root.args[2]")
    public Map<String, Object> courseAndTypeList(long typeId, int pageIndex, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        result.put("typelist", microClassService.getCourseTypeList());
        result.put("courselist", microClassService.getAppCourseList(typeId, pageIndex, pageSize));
        return result;
    }

    public Map<String, Object> getUser(long userid) {
        return getSimpleUser(userid);
    }

    public List<Map<String, Object>> getUserMessageList(long userId, int pageIndex, int pageSize) {
        return userService.getUserMessageList(userId, pageIndex, pageSize);
    }

    public Map<String, Object> getUserMessageDetailed(long id) {
        return userService.getUserMessageDetailed(id);
    }

    public boolean updateMessageReadStatus(long id) {
        return userService.updateMessageReadStatus(id);
    }


    private Map<String, Object> getSimpleUser(long userId) {
        Map<String, Object> simpleUser = new HashMap<>();
        if (userId > 0) {
            Map<String, Object> user = userService.getUser(userId);
            simpleUser.put("id", ModelUtil.getLong(user, "id"));
            simpleUser.put("headpic", ModelUtil.getStr(user, "headpic"));
            simpleUser.put("name", ModelUtil.getStr(user, "name"));
            simpleUser.put("userno", ModelUtil.getStr(user, "userno"));
            simpleUser.put("integral", ModelUtil.getLong(user, "integral"));
            simpleUser.put("messagecount", ModelUtil.getLong(user, "messagecount"));
            simpleUser.put("uservip", userService.isexpire(userId));
        }
        return simpleUser;
    }


    /*
    主页数据统计
     */

    public Map<String, Object> getUserStatistics() {
        Map<String, Object> map = homePageMapper.getUserStatistics();

        int android = ModelUtil.getInt(map, "android");
        int ios = ModelUtil.getInt(map, "ios");
        int terminalother = ModelUtil.getInt(map, "terminalother");
        int app = ModelUtil.getInt(map, "app");
        int publicNo = ModelUtil.getInt(map, "publicNo");
        int leyangyun = ModelUtil.getInt(map, "leyangyun");
        int channelother = ModelUtil.getInt(map, "channelother");

        List<Integer> terminallist = new ArrayList<>();
        terminallist.add(android);
        terminallist.add(ios);
        terminallist.add(terminalother);
        List<Object> terminalnamelist = new ArrayList<>();
        terminalnamelist.add("Android");
        terminalnamelist.add("IOS");
        terminalnamelist.add("其他");

        List<Integer> channellist = new ArrayList<>();
        channellist.add(app);
        channellist.add(publicNo);
        channellist.add(leyangyun);
        channellist.add(channelother);
        List<Object> channelnamelist = new ArrayList<>();
        channelnamelist.add("APP");
        channelnamelist.add("公众号");
        channelnamelist.add("乐养云");
        channelnamelist.add("其他");

        map.put("terminal", terminallist);
        map.put("terminalname", terminalnamelist);
        map.put("channel", channellist);
        map.put("channelname", channelnamelist);

        return map;
    }

    /**
     * 统计年龄柱状图
     *
     * @return
     */
    public Map<String, Object> getUserStatisticsAge() {
        Map<String, Object> map = homePageMapper.getUserStatisticsAge();
        Map<String, Object> agemap = new HashMap<>();
        List<Object> agelist = new ArrayList<>();
        agelist.add(ModelUtil.getInt(map, "ageone"));
        agelist.add(ModelUtil.getInt(map, "agetwo"));
        agelist.add(ModelUtil.getInt(map, "agethree"));
        agelist.add(ModelUtil.getInt(map, "agefour"));
        agelist.add(ModelUtil.getInt(map, "agefive"));
        agelist.add(ModelUtil.getInt(map, "agesix"));
        agelist.add(ModelUtil.getInt(map, "ageseven"));
        agemap.put("age", agelist);
        List<Object> typelist = new ArrayList<>();
        typelist.add("0-18");
        typelist.add("19-30");
        typelist.add("31-40");
        typelist.add("41-50");
        typelist.add("51-55");
        typelist.add("56-60");
        typelist.add("60+");
        agemap.put("agetype", typelist);
        return agemap;
    }


    public Map<String, Object> userBasicNumberAll() {
        Map<String, Object> map = new HashMap<>();
        int yesterdayadduser = homePageMapper.yesterdayAddUser();
        map.put("yesterdayadduser", yesterdayadduser);//昨日新增用户
        double usernumber = homePageMapper.userNumber(0, new ArrayList<>());   //用户总数量
        map.put("usernumber", (int) usernumber);
        int yesterdayaddvip = homePageMapper.yesterdayAddVip();
        map.put("yesterdayaddvip", yesterdayaddvip);//昨日新增会员
        int vipnumber = homePageMapper.vipNumber();
        map.put("vipnumber", vipnumber);//所有会员

        //性别
        DecimalFormat df = new DecimalFormat("#");
        double malenumber = homePageMapper.userNumber(1, new ArrayList<>());   //男num
//        double femalenumber = homePageMapper.userNumber(2, 0, 0);   //女num
        int male = Integer.valueOf(df.format(malenumber / usernumber * 100));
        int female = 100 - male;
        map.put("male", male); //男生比例
        map.put("female", female);//女生比例

        //终端
        List<Long> Androidlist = new ArrayList<>();
        Androidlist.add((long) RegisterChannelEnum.Android.getCode());
        Androidlist.add((long) RegisterChannelEnum.WechatAndroid.getCode());
        double androidnum = homePageMapper.userNumber(0, Androidlist);//安卓数量
//        map.put("androidnum",androidnum);
        int android = Integer.valueOf(df.format(androidnum / usernumber * 100));
        map.put("android", android); //安卓比例
        List<Long> Ioslist = new ArrayList<>();
        Ioslist.add((long) RegisterChannelEnum.Ios.getCode());
        Ioslist.add((long) RegisterChannelEnum.WechatIos.getCode());
        double iosnum = homePageMapper.userNumber(0, Ioslist);//ios数量
//        map.put("iosnum",iosnum);
        int ios = Integer.valueOf(df.format(iosnum / usernumber * 100));
        map.put("ios", ios);//ios比例
        map.put("terminalother", 100 - ios - android);//其他比例

        //渠道
        List<Long> applist = new ArrayList<>();//app
        applist.add((long) RegisterChannelEnum.Android.getCode());
        applist.add((long) RegisterChannelEnum.Ios.getCode());
        double appnum = homePageMapper.userNumber(0, applist);
        int app = Integer.valueOf(df.format(appnum / usernumber * 100));
        map.put("app", app);//app比例
        List<Long> publicNolist = new ArrayList<>();//公众号
        publicNolist.add((long) RegisterChannelEnum.WechatAndroid.getCode());
        publicNolist.add((long) RegisterChannelEnum.WechatIos.getCode());
        double publicNonum = homePageMapper.userNumber(0, publicNolist);
        int publicNo = Integer.valueOf(df.format(publicNonum / usernumber * 100));
        map.put("publicNo", publicNo);//公众号比例
        List<Long> leyangyunlist = new ArrayList<>();//乐养云
        leyangyunlist.add((long) RegisterChannelEnum.Kangyang.getCode());
        double leyangyunnum = homePageMapper.userNumber(0, leyangyunlist);
        int leyangyun = Integer.valueOf(df.format(leyangyunnum / usernumber * 100));
        map.put("leyangyun", leyangyun);//乐养云比例
        map.put("channelother", 100 - app - publicNo - leyangyun);//其他
        return map;
    }

    public boolean userStatisticsAdd() {
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
        homePageMapper.deluserStatistics();//删除旧数据
        homePageMapper.userStatisticsAdd(yesterdayadduser, usernumber, yesterdayaddvip, vipnumber, male, female, android, ios, terminalother, app, publicNo, leyangyun, channelother);
        return true;
    }

    public boolean ageAdd() {
        int ageone = homePageMapper.ageOne();
        int agetwo = homePageMapper.ageTwo();
        int agethree = homePageMapper.ageThree();
        int agefour = homePageMapper.ageFour();
        int agefive = homePageMapper.ageFive();
        int agesix = homePageMapper.ageSix();
        int ageseven = homePageMapper.ageSeven();
        homePageMapper.deluserStatisticsAge();//删除旧数据
        homePageMapper.userStatisticsAgeAdd(ageone, agetwo, agethree, agefour, agefive, agesix, ageseven);
        return true;
    }

    /**
     * 地域
     *
     * @return
     */
    public Map<String, Object> getRegionList() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> list = homePageMapper.getRegionList();//已有
        List<Map<String, Object>> alllist = homePageMapper.areaAll();//所有

        if (list.size() > 0) {
//        map.put("areas", list);
            int max = ModelUtil.getInt(list.get(0), "value");
            map.put("max", max);
            int small = ModelUtil.getInt(list.get(list.size() - 1), "value");
            map.put("small", small);

            List<Map<String, Object>> levellist = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("#");
            Map<String, Object> levelmap1 = new HashMap<>();
            int max1 = Integer.valueOf(df.format(max / 5));//每阶段差距多少
            levelmap1.put("small", 1);
            levelmap1.put("max", max1);
            Map<String, Object> levelmap2 = new HashMap<>();
            int small2 = max1 + 1;
            int max2 = small2 + max1 - 1;
            levelmap2.put("small", small2);
            levelmap2.put("max", max2);
            Map<String, Object> levelmap3 = new HashMap<>();
            int small3 = max2 + 1;
            int max3 = small3 + max1 - 1;
            levelmap3.put("small", small3);
            levelmap3.put("max", max3);
            Map<String, Object> levelmap4 = new HashMap<>();
            int small4 = max3 + 1;
            int max4 = small4 + max1 - 1;
            levelmap4.put("small", small4);
            levelmap4.put("max", max4);
            Map<String, Object> levelmap5 = new HashMap<>();
            int small5 = max4 + 1;
//        int max5 = small5 + max1-1;
            levelmap5.put("small", small5);
            levelmap5.put("max", max);
            levellist.add(levelmap1);
            levellist.add(levelmap2);
            levellist.add(levelmap3);
            levellist.add(levelmap4);
            levellist.add(levelmap5);
            map.put("levellist", levellist);

            for (int i = 0; i < alllist.size(); i++) {
                String name1 = ModelUtil.getStr(alllist.get(i), "name");
                for (int j = 0; j < list.size(); j++) {
                    String name2 = ModelUtil.getStr(list.get(j), "name");
                    if (name1.equals(name2)) {
                        alllist.set(i, list.get(j));
                    }
                }
            }

            //排序
            Collections.sort(alllist, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Integer name1 = Integer.valueOf(o1.get("value").toString());//name1是从你list里面拿出来的一个
                    Integer name2 = Integer.valueOf(o2.get("value").toString()); //name1是从你list里面拿出来的第二个name
                    return name2.compareTo(name1);
                }
            });
        } else {
            Map<String, Object> levelmm = new HashMap<>();
            levelmm.put("small", 0);
            levelmm.put("max", 0);
            List<Object> list1 = new ArrayList<>();
            list1.add(levelmm);
            list1.add(levelmm);
            list1.add(levelmm);
            list1.add(levelmm);
            list1.add(levelmm);
            map.put("levellist", list1);
        }

        Map<String, Object> mapnan = new HashMap<>();
        mapnan.put("name", "南海诸岛");
        mapnan.put("value", 0);
        alllist.add(alllist.size(), mapnan);

        map.put("areas", alllist);
        return map;
    }

    public boolean addUserStatisticsRegion() {
        List<Map<String, Object>> list = homePageMapper.getRegionList();
        if (list.size() > 0) {
            homePageMapper.updateRegion();
            for (Map<String, Object> map : list) {
                String name = ModelUtil.getStr(map, "name");
                int count = ModelUtil.getInt(map, "value");
                homePageMapper.addUserStatisticsRegion(name, count);
            }
        }
        return true;
    }


    //顶部数量
    public Map<String, Object> getStatistics() {
        return homePageMapper.getStatistics();
    }

    //中间折现图
    public Map<String, Object> getNumByOrderType(int type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Object> list = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        long startTime = UnixUtil.getBeginDaySeven();
        long endTime = UnixUtil.getNowTimeStamp();
        for (int i = 6; i > -1; i--) {
            long time = UnixUtil.getBeginDaySeven(i);
            String t = sdf.format(time);
            list.add(t);
        }
        List<Map<String, Object>> numByOrderType = homePageMapper.getNumByOrderType(type, startTime, endTime);
        if (null == numByOrderType) {
            numByOrderType = new ArrayList<>();
        }
        List<Object> list1 = new ArrayList<>();
        Map<String, Object> maps = new HashMap<>();
        numByOrderType.forEach(map -> {
            String day = ModelUtil.getStr(map, "day");
            int count = ModelUtil.getInt(map, "count");
            maps.put(day, count);
        });
        list.forEach(t -> {
            if (maps.containsKey(t)) {
                list1.add(maps.get(t));
            } else {
                list1.add(0);
            }
        });
        result.put("day", list);
        result.put("value", list1);
        result.put("starttime", startTime);
        result.put("endtime", endTime);
        return result;

    }

    //中间折现图非默认
    public Map<String, Object> getNumByOrderTypeNot(int type, long startTime, long endTime) {
        List<Object> list = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> numByOrderType = homePageMapper.getNumByOrderType(type, startTime, endTime);
        if (null == numByOrderType) {
            numByOrderType = new ArrayList<>();
        }
        List<Object> list1 = new ArrayList<>();
        numByOrderType.forEach(map -> {
            String day = ModelUtil.getStr(map, "day");
            int count = ModelUtil.getInt(map, "count");
            list.add(day);
            list1.add(count);
        });
        result.put("day", list);
        result.put("value", list1);
        result.put("starttime", startTime);
        result.put("endtime", endTime);
        return result;
    }

    public Map<String, Object> getNum(int type, long startTime, long endTime) {
        if (startTime == 0 && endTime == 0) {
            return getNumByOrderType(type);
        } else {
            return getNumByOrderTypeNot(type, startTime, endTime);
        }
    }

    //底部统计
    public Map<String, Object> getFinalCount(long startTime, long endTime) {
        Map<String, Object> map = new HashMap<>();
        if (startTime == 0 && endTime == 0) {
            startTime = UnixUtil.getBeginDaySeven();
            endTime = UnixUtil.getNowTimeStamp();
        }
        int a = homePageMapper.getYesterDayNum(startTime, endTime);
        int b = homePageMapper.getTimeAddVip(startTime, endTime);
        int c = homePageMapper.getUseMoney(startTime, endTime);
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("value", a);
        map1.put("name", "访问总人数");
        Map<String, Object> map2 = new HashMap<>();
        map2.put("value", b);
        map2.put("name", "付费会员人数");
        Map<String, Object> map3 = new HashMap<>();
        map3.put("value", c);
        map3.put("name", "付费人数");
        list.add(map1);
        list.add(map2);
        list.add(map3);
        map.put("allnumPercentage", 100);
        map.put("starttime", startTime);
        map.put("endtime", endTime);
        map.put("listnum", list);
        DecimalFormat df = new DecimalFormat("#.0");
        double d = 0;
        double e = 0;
        if(a!=0){
            e=Double.valueOf(df.format((double) c / a * 100));
            d=Double.valueOf(df.format((double) b / a * 100));
        }
        map.put("paynumPercentage", e);
        map.put("vipnumPercentage", d);
        return map;
    }

}
