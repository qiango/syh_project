package com.syhdoctor.webserver.service.user;

import com.syhdoctor.common.config.ConfigModel;
import com.syhdoctor.common.utils.EnumUtils.IntegralTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.OrderTypeEnum;
import com.syhdoctor.common.utils.*;
import com.syhdoctor.common.utils.alidayu.SendShortMsgUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.user.UserMapper;
import com.syhdoctor.webserver.service.answer.AnswerService;
import com.syhdoctor.webserver.service.code.CodeService;
import com.syhdoctor.webserver.service.mongo.MongoService;
import com.syhdoctor.webserver.service.prescription.PrescriptionService;
import com.syhdoctor.webserver.service.system.SystemService;
import com.syhdoctor.webserver.service.video.UserVideoService;
import com.syhdoctor.webserver.service.vipcard.VipCardService;
import com.syhdoctor.webserver.thirdparty.mongodb.entity.Share;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public abstract class UserBaseService extends BaseService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CodeService codeService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private VipCardService vipCardService;

    @Autowired
    private MongoService mongoService;

    @Autowired
    private UserVideoService userVideoService;


    /**
     * 发送验证码
     *
     * @param phone
     * @return
     */
    public boolean sendAuthenticationCode(String phone) {
        boolean flag = true;
        try {
            String code = UnixUtil.getCode();
            Map<String, Object> map = new HashMap<>();
            map.put("code", code);
            //将验证码存入redis
            SendShortMsgUtil.sendSms(com.syhdoctor.webserver.config.ConfigModel.ISONLINE, phone, ConfigModel.SMS.Login_sms_template, map);
            this.redisTemplate.opsForValue().set("code" + phone, code);
            this.redisTemplate.expire("code" + phone, ConfigModel.SMS.timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            flag = false;
            log.info("发送验证码出现错误>>>>"+e.getMessage());
            e.getStackTrace();
        }
        return flag;
    }

    public Map<String, Object> getUserAreas(long userId) {
        return userMapper.getUserAreas(userId);
    }

    /**
     * 验证验证码
     *
     * @param phone 手机号码
     * @param phone 验证码
     * @return
     */
    public int validCode(String phone, String code) {
        Object codeObject = redisTemplate.opsForValue().get("code" + phone);
        int result;
        if (TextFixed.def_code.equals(code) || codeObject != null) {
            if (TextFixed.def_code.equals(code) || (codeObject != null && codeObject.toString().equals(code))) {
                deleteRedisCode("code" + phone);
                result = 1;
            } else {
                result = -2;
            }
        } else {
            result = -1;
        }
        return result;
    }

    /**
     * 清除验证码
     *
     * @param key
     */
    public void deleteRedisCode(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 登录
     *
     * @param phone           手机号码
     * @param registerChannel 渠道1：安卓，2：苹果,3：微信
     * @return
     */
    public Map<String, Object> login(String phone, int registerChannel) {
        return login(phone, registerChannel, 0);
    }

    /**
     * 登录
     *
     * @param phone           手机号码
     * @param registerChannel 渠道1：安卓，2：苹果,3：微信
     * @return
     */
    public Map<String, Object> login(String phone, int registerChannel, long openid) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> user = userMapper.getUser(phone);
        boolean flag = verifyUser(ModelUtil.getLong(user, "id"));
        if (null == user) {
            String token = UnixUtil.generateString(32);
            long uid = userMapper.addUser(phone, token, registerChannel);
            //将用户秘钥存入redis
            this.redisTemplate.opsForValue().set("token" + phone, token);
            result.put("userid", uid);
            result.put("token", token);
        } else {
            result.put("userid", ModelUtil.getLong(user, "id"));
            result.put("token", ModelUtil.getStr(user, "token"));
        }
        if (openid != 0) {
            Map<String, Object> opentIdType = userMapper.getOpenIdType(openid);
            int opentype = ModelUtil.getInt(opentIdType, "opentype");
            userMapper.updateUserOpen(ModelUtil.getLong(user, "id"), opentype);
            userMapper.updateUserOpen(openid, ModelUtil.getLong(user, "id"));

            //mongo 分享转化成功记录
            if (ModelUtil.getLong(opentIdType, "shareid") > 0) {
                Share share = new Share();
                share.setId(mongoService.getId("mongodb_share"));
                share.setPid(ModelUtil.getLong(opentIdType, "shareid"));
                share.setUsertype(3);
                share.setShareUserId(ModelUtil.getLong(opentIdType, "shareuserid"));
                share.setOpenId(openid);
                mongoService.saveShare(share);
            }
        }
        result.put("isinformation", flag ? 1 : 0);
        return result;
    }

    /**
     * 添加用户信息
     *
     * @param userid  用户id
     * @param headpic 头像
     * @param name    名字
     * @param gender  性别(0:未知，1：男，2：女，9:未说明)
     * @param cardno  身份证
     * @param areas   地区
     * @return
     */
    public boolean addUserInfo(long userid, String headpic, String name, int gender, String cardno, String areas) {
        Map<String, Object> user = userMapper.getUser(userid);
        //名字拼音
        String namePinyin = Pinyin4jUtil.getInstance().getFirstSpell(name);
        long birthday = UnixUtil.dateTimeStamp(cardno.substring(6, 14), "yyyyMMdd");
        int age = ModelUtil.strToInt(String.valueOf((UnixUtil.getNowTimeStamp() - birthday) / 365 / 24 / 60 / 60 / 1000), 0);
        user.put("age", age);
        boolean flag = userMapper.addUserInfo(userid, headpic, name, namePinyin, gender, cardno, birthday, areas, age);
        if (flag) {
            flag = userMapper.isinformation(userid);
        }
        Map<String, Object> family = userVideoService.getMasterFamily(userid);
        if (family == null) {
            userVideoService.insertFamily(0, userid, name, age, gender, ModelUtil.getStr(user, "phone"), 1, 1);
        }
        return flag;
    }

    public List<Map<String, Object>> getUserMessageList(long userId, int pageIndex, int pageSize) {
        return userMapper.getUserMessageList(userId, pageIndex, pageSize);
    }

    public Map<String, Object> getUserMessageDetailed(long id) {
        return userMapper.getUserMessageDetailed(id);
    }

    public boolean updateMessageReadStatus(long id) {
        Map<String, Object> messageType = userMapper.getMessageType(id);
        return userMapper.updateMessageReadStatus(ModelUtil.getInt(messageType, "messagetype"));
    }


    /**
     * 获取用户详细信息
     *
     * @param userid
     * @return
     */
    public Map<String, Object> getUser(long userid) {
        Map<String, Object> user = userMapper.getUser(userid);
        if (user != null) {
            if (ModelUtil.getInt(user, "gender") == 1) {
                user.put("gendername", "男");
            } else if (ModelUtil.getInt(user, "gender") == 2) {
                user.put("gendername", "女");
            } else if (ModelUtil.getInt(user, "gender") == 9) {
                user.put("gendername", "保密");
            }
            long birthday = ModelUtil.getLong(user, "birthday");
            long age = (UnixUtil.getNowTimeStamp() - birthday) / 365 / 24 / 60 / 60 / 1000;
            user.put("age", age);
            String areas = ModelUtil.getStr(user, "areas");
            systemService.getAres(user, areas);
            long userMessageCount = userMapper.getUserMessageCount(userid);
            if (userMessageCount > 0) {
                user.put("messagecount", userMessageCount);
            } else {
                user.put("messagecount", 0);
            }
            user.put("uservip", isVip(userid));
        }
        return user;
    }

    /**
     * 是否过期
     *
     * @param userId
     * @return
     */
    public Map<String, Object> isexpire(long userId) {
        log.info("userid>>>>>>>>>>>>>>>>>>>>>>" + userId);
        Map<String, Object> userMember = vipCardService.getUserMember(userId);
        Map<String, Object> result = new HashMap<>();
        if (userMember != null) {
            result.put("isvip", 1);
            result.put("level", ModelUtil.getInt(userMember, "level"));
            result.put("isexpire", ModelUtil.getInt(userMember, "isexpire", 0));
        } else {
            result.put("isvip", 0);
            result.put("level", 0);
            result.put("isexpire", 0);
        }
        return result;
    }

    /**
     * 是否会员
     *
     * @param userId
     * @return
     */
    public Map<String, Object> isVip(long userId) {
        log.info("userid>>>>>>>>>>>>>>>>>>>>>>" + userId);
        Map<String, Object> userMember = vipCardService.getUserMember(userId);
        Map<String, Object> result = new HashMap<>();
        if (userMember != null) {
            result.put("isvip", ModelUtil.getInt(userMember, "isexpire", 0) > 0 ? 1 : 0);
            result.put("level", ModelUtil.getInt(userMember, "level"));
        } else {
            result.put("isvip", 0);
            result.put("level", 0);
        }
        return result;
    }

    /**
     * 用户剩余的优惠
     *
     * @return
     */
    public Map<String, Object> userVipDiscount(long userId) {
        return vipCardService.userVipDiscount(userId);
    }

    /**
     * 会员卡优惠
     *
     * @return
     */
    public Map<String, Object> vipDiscount() {
        return vipCardService.vipDiscount();
    }

    public boolean verifyUser(long userId) {
        Map<String, Object> user = userMapper.getUser(userId);
        String headpic = ModelUtil.getStr(user, "headpic");
        String name = ModelUtil.getStr(user, "name");
        int gender = ModelUtil.getInt(user, "gender");
        String cardno = ModelUtil.getStr(user, "cardno");
        String areas = ModelUtil.getStr(user, "areas");
        return !(StrUtil.isEmpty(headpic, name, cardno, areas) || gender == 0);
    }

    /**
     * 后台用户详细
     *
     * @param userId
     * @return
     */
    public Map<String, Object> getUserByAdmin(long userId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> user = getUser(userId);
        Map<String, Object> health = new HashMap<>();
        if (user != null) {
            health = getHealth(ModelUtil.getLong(user, "id"));
        }
        result.put("user", getUser(userId));
        result.put("health", health);
        result.put("diseaselist", answerService.getDiseaseName(userId));
        return result;
    }

    /**
     * 后台用户处方列表
     *
     * @param userId
     * @return
     */
    public List<Map<String, Object>> getPrescriptionList(long userId, int pageIndex, int pageSize) {
        return prescriptionService.getUserPrescriptionList(userId, pageIndex, pageSize);
    }

    public Map<String, Object> getAppPrescription(long prescriptionid) {
        return prescriptionService.getAppPrescription(prescriptionid);
    }

    /**
     * 后台用户处方数量
     *
     * @param userId
     * @return
     */
    public long getPrescriptionCount(long userId) {
        return prescriptionService.getUserPrescriptionCount(userId);
    }

    /**
     * 后台用户订单列表
     *
     * @param userId
     * @return
     */
    public List<Map<String, Object>> AdminUserOrderList(long userId, int pageIndex, int pageSize) {
        return answerService.adminUserOrderList(userId, pageIndex, pageSize);
    }

    /**
     * 后台用户订单数量
     *
     * @param userId
     * @return
     */
    public long getOrderCount(long userId) {
        return answerService.userOrderCont(userId);
    }

    /**
     * 根据pid查找地区
     *
     * @param code
     * @return
     */
    public List<Map<String, Object>> getAreaByParentId(int code) {
        return codeService.getAreaByParentId(code);
    }


    /**
     * 用户列表
     *
     * @param name
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getUserList(String name, String phone, int pageIndex, int pageSize) {
        return userMapper.getUserList(name, phone, pageIndex, pageSize, (Map<String, Object> value) -> {
            value.put("gendername", getGenderName(ModelUtil.getInt(value, "gender")));
            return value;
        });
    }

    /**
     * 用户数量
     *
     * @param name
     * @return
     */
    public long getUserCount(String name, String phone) {
        return userMapper.getUserCount(name, phone);
    }

    private String getGenderName(int gender) {
        if (gender == 0) {
            return "未知";
        } else if (gender == 1) {
            return "男";
        } else if (gender == 2) {
            return "女";
        } else if (gender == 9) {
            return "未说明";
        } else {
            return "";
        }
    }

    /**
     * 用户健康档案
     *
     * @param userId
     * @return
     */
    public Map<String, Object> getHealth(long userId) {
        return userMapper.getHealth(userId);
    }

    public Map<String, Object> getUserHeath(long userId) {
        return userMapper.getUserHeath(userId);
    }


    public Map<String, Object> getUserHeaths(long userId) {
        return userMapper.getUserHeaths(userId);
    }

    /**
     * 用户健康档案
     *
     * @param userId
     * @return
     */
    public Map<String, Object> getUserHealth(long userId) {
        Map<String, Object> user = userMapper.getUserHealth(userId);
        if (ModelUtil.getInt(user, "gender") == 1) {
            user.put("gename", "男");
        } else if (ModelUtil.getInt(user, "gender") == 2) {
            user.put("gename", "女");
        } else if (ModelUtil.getInt(user, "gender") == 9) {
            user.put("gename", "保密");
        }
        long birthday = ModelUtil.getLong(user, "birthday");
        long age = (UnixUtil.getNowTimeStamp() - birthday) / 365 / 24 / 60 / 60 / 1000;
        user.put("age", age);
        return user;
    }

    /**
     * 更新档案
     *
     * @param userId
     * @param height
     * @param weight
     * @param history
     * @param treatment
     * @param habitlife
     * @return
     */
    public boolean updateHealth(long userId, double height, double weight, String history, String treatment, String habitlife) {
        Map<String, Object> health = getHealth(userId);
        if (health == null) {
            return userMapper.addHealth(userId, height, weight, history, treatment, habitlife);
        } else {
            return userMapper.updateHealth(userId, height, weight, history, treatment, habitlife);
        }
    }

    /**
     * 签到
     *
     * @param userid 用户id
     * @return
     */
    @Transactional
    public synchronized int userSignIn(long userid) {
        int integral = 0;
        //是否签到
        long count = userMapper.userSignFlag(userid);
        if (count == 0) {
            //签到
            userMapper.userSignIn(userid);
            //添加积分
            integral = addUserIntegral(userid, IntegralTypeEnum.SignIn.getCode());
            /*systemService.addMessage("", TextFixed.signTitle,
                    MessageTypeEnum.user.getCode(), "",
                    TypeNameAppPushEnum.doctorUserSignIn.getCode(), userid,
                    String.format(TextFixed.usersignText, 1),
                    "");//app 医生 内推送*/
        }
        return integral;
    }

    //添加积分
    public int addUserIntegral(long userid, int type) {
        if (type != 1) {
            long count = userMapper.getUserIntegralDetailed(userid, type);
            if (count > 0) {
                //不能重复添加积分
                return 0;
            }
        }
        int integral = TextFixed.user_sign_integral;
        //添加积分
        userMapper.updateUserIntegral(userid, integral);
        //添加积分明细
        userMapper.addUserIntegralDetailed(userid, type, integral);
        return integral;
    }

    public Map<String, Object> userIntegralList(long userid, int pageIndex, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        result.put("methodcount", userMapper.thisMonthUserIntegralCount(userid));
        result.put("totalcount", userMapper.totalUserIntegralCount(userid));
        result.put("integrallist", userMapper.userIntegralList(userid, pageIndex, pageSize, (Map<String, Object> value) -> {
            value.put("typename", getType(ModelUtil.getInt(value, "type")));
            return value;
        }));
        return result;
    }

    private String getType(int type) {
        if (type == IntegralTypeEnum.SignIn.getCode()) {
            return IntegralTypeEnum.SignIn.getMessage();
        } else if (type == IntegralTypeEnum.Info.getCode()) {
            return IntegralTypeEnum.Info.getMessage();
        } else if (type == IntegralTypeEnum.QA.getCode()) {
            return IntegralTypeEnum.QA.getMessage();
        } else if (type == IntegralTypeEnum.Phone.getCode()) {
            return IntegralTypeEnum.Phone.getMessage();
        } else {
            return "";
        }
    }

    public List<Map<String, Object>> getUserOrderList(long userid, int pageIndex, int pageSize) {
        List<Map<String, Object>> userOrderList = userMapper.getUserOrderList(userid, pageIndex, pageSize);
        List<Long> answerIds = new ArrayList<>();
        List<Long> phoneIds = new ArrayList<>();
        List<Long> videoIds = new ArrayList<>();
        for (Map<String, Object> map : userOrderList) {
            long orderid = ModelUtil.getLong(map, "id");
            switch (OrderTypeEnum.getValue(ModelUtil.getInt(map, "ordertype"))) {
                case Answer:
                    answerIds.add(orderid);
                    break;
                case Phone:
                    phoneIds.add(orderid);
                    break;
                case Video:
                    videoIds.add(orderid);
                    break;
                default:
                    break;
            }
        }

        if (answerIds.size() > 0) {
            List<Map<String, Object>> orderDiseaseList = userMapper.orderAnswerDiseaseList(answerIds);
            initList(userOrderList, orderDiseaseList, OrderTypeEnum.Answer.getCode());
        }

        if (phoneIds.size() > 0) {
            List<Map<String, Object>> orderDiseaseList = userMapper.orderPhoneDiseaseList(phoneIds);
            initList(userOrderList, orderDiseaseList, OrderTypeEnum.Phone.getCode());
        }

        if (videoIds.size() > 0) {
            List<Map<String, Object>> orderDiseaseList = userMapper.orderVideoDiseaseList(videoIds);
            initList(userOrderList, orderDiseaseList, OrderTypeEnum.Video.getCode());
        }

        return userOrderList;
    }

    private void initList(List<Map<String, Object>> orderList, List<Map<String, Object>> diseaseList, int ordertype) {
        List<Map<String, Object>> tempList = new ArrayList<>();
        Map<Long, Object> tempProblem = new HashMap<>();
        long tempId = 0;

        for (Map<String, Object> obj : diseaseList) {
            Long orderid = ModelUtil.getLong(obj, "orderid");
            if (orderid > 0) {
                if (orderid != tempId) {
                    tempId = orderid;
                    tempList = new ArrayList<>();
                    Map<String, Object> contentObj = new HashMap<>();
                    contentObj.put("value", ModelUtil.getStr(obj, "value"));
                    tempList.add(contentObj);

                    tempProblem.put(orderid, tempList);
                } else {
                    Map<String, Object> contentObj = new HashMap<>();
                    contentObj.put("value", ModelUtil.getStr(obj, "value"));
                    tempList.add(contentObj);
                }
            }
        }

        for (Map<String, Object> map : orderList) {
            if (ModelUtil.getInt(map, "ordertype") == ordertype) {
                map.put("diseaselist", tempProblem.get(ModelUtil.getLong(map, "id")));
            }
        }
    }

    /**
     * 健康档案完整度
     *
     * @param userid
     * @return
     */
    public long userHealthRecords(long userid) {
        Map<String, Object> gendermap = userMapper.genderId(userid);//性别(1男，2女)
        Map<String, Object> healthmap = userMapper.userHealthRecords(userid);//健康信息
        Map<String, Object> casemap = userMapper.userCase(userid);//病例表
        double num = 0;
        num = isNull(ModelUtil.getStr(healthmap, "height"), num);//身高
        num = isNull(ModelUtil.getStr(healthmap, "weight"), num);//体重
        num = isNull(ModelUtil.getStr(healthmap, "ismarry"), num);//是否结婚

        num = isNull(ModelUtil.getStr(casemap, "issmoking"), num);//是否吸烟
        num = isNull(ModelUtil.getStr(casemap, "isdrinking"), num);//是否喝酒
        num = isNull(ModelUtil.getStr(casemap, "ischronicillness"), num);//有无慢性病史
        num = isNull(ModelUtil.getStr(casemap, "isallergy"), num);//是否过敏
        num = isNull(ModelUtil.getStr(casemap, "issurgery"), num);//有无手术史
        num = isNull(ModelUtil.getStr(casemap, "isfamilyhistory"), num);//有无家族史

        int qwer = ModelUtil.getInt(gendermap, "gender");//男女
        if (qwer == 2) {
            num = isNull(ModelUtil.getStr(casemap, "isfertility"), num);//有无生育史
            num = isNull(ModelUtil.getStr(casemap, "mencharage"), num);//初潮年龄
            num = isNull(ModelUtil.getStr(casemap, "finalmenarche"), num);//末次月经时间
            num = isNull(ModelUtil.getStr(casemap, "ismenopause"), num);//是否绝经
            num = isNull(userMapper.menstrualCycle(userid), num);//月经周期
            num = isNull(userMapper.menstruationDay(userid), num);//经期天数
        }
        DecimalFormat df = new DecimalFormat("#");
        double a = 0;
        if (qwer == 1) {
            a = num / 9 * 100;
        } else {
            a = num / 15 * 100;
        }
        return Long.valueOf(df.format(a));
    }


    public double isNull(String str, double num) {
        if (!StrUtil.isEmpty(str)) {
            num += 1;
        }
        return num;
    }

}
