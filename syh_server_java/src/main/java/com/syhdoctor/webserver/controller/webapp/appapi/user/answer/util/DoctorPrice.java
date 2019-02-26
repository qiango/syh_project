package com.syhdoctor.webserver.controller.webapp.appapi.user.answer.util;

import com.syhdoctor.common.utils.EnumUtils.VisitCategoryEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.service.answer.AnswerService;
import com.syhdoctor.webserver.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class DoctorPrice {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;


    private VisitCategoryEnum typeEnum;

    public static final int ZERO = 1;
    public static final int VIP_Free = 2;
    public static final int VIP_ZERO = 3;
    public static final int VIP_DISCOUNT = 4;
    public static final int ORIGINALPRICE = 5;

    public DoctorPrice setPriceType(VisitCategoryEnum priceType) {
        typeEnum = priceType;
        return this;
    }

    private long userId;//原价

    private long doctorId;//原价

    private Map<String, Object> result;

    public Map<String, Object> result() {
        return result;
    }

    public DoctorPrice setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public DoctorPrice setDoctorId(long doctorId) {
        this.doctorId = doctorId;
        return this;
    }

    public DoctorPrice show() {
        if (userId == 0 && doctorId == 0) {
            throw new ServiceException("医生或者用户id不能为空");
        }
        if (typeEnum == null) {
            throw new ServiceException("请设置价格的类型");
        }
        switch (typeEnum) {
            case graphic:
                result = getShowAnswerPrice();
                break;
            case phone:
                result = getShowPhonePrice();
                break;
            case Outpatient:
                result = getShowOutpatientPrice();
                break;
            case department:
                result = getShowDepartmentPrice();
                break;
            case video:
                result = getShowVideoPrice();
                break;
            default:
                break;
        }
        return this;
    }

    public DoctorPrice build() {
        if (userId == 0 && doctorId == 0) {
            throw new ServiceException("医生或者用户id不能为空");
        }
        if (typeEnum == null) {
            throw new ServiceException("请设置价格的类型");
        }
        switch (typeEnum) {
            case graphic:
                result = getBuildAnswerPrice();
                break;
            case phone:
                result = getBuildPhonePrice();
                break;
            case Outpatient:
                result = getBuildOutpatientPrice();
                break;
            case department:
                result = getBuildDepartmentPrice();
                break;
            case video:
                result = getBuildVidelPrice();
                break;
            default:
                break;
        }
        return this;
    }


    //顾问图文
    private Map<String, Object> getShowOutpatientPrice() {
        //获取原价
        Map<String, Object> phonePrice = answerService.getOutpatientPrice();
        BigDecimal originalPrice = ModelUtil.getDec(phonePrice, "price", BigDecimal.ZERO);

        Map<String, Object> uservipdiscount = userService.userVipDiscount(userId);
        int vipCount = ModelUtil.getInt(uservipdiscount, "healthconsultantceefax", 0);
        double vipDiscount = ModelUtil.getDouble(uservipdiscount, "healthconsultantdiscount", 1);

        Map<String, Object> VIPDiscount = userService.vipDiscount();
        int userCount = ModelUtil.getInt(VIPDiscount, "healthconsultantceefax", 0);
        double userDiscount = ModelUtil.getDouble(VIPDiscount, "healthconsultantdiscount", 1);

        return showResult(userId, originalPrice, vipCount, vipDiscount, userCount, userDiscount);
    }

    //顾问电话
    private Map<String, Object> getShowDepartmentPrice() {
        //获取原价
        Map<String, Object> phonePrice = answerService.getDepartmentPrice();
        BigDecimal originalPrice = ModelUtil.getDec(phonePrice, "price", BigDecimal.ZERO);

        Map<String, Object> uservipdiscount = userService.userVipDiscount(userId);
        int vipCount = ModelUtil.getInt(uservipdiscount, "healthconsultantphone", 0);
        double vipDiscount = ModelUtil.getDouble(uservipdiscount, "healthconsultantdiscount", 1);

        Map<String, Object> VIPDiscount = userService.vipDiscount();
        int userCount = ModelUtil.getInt(VIPDiscount, "healthconsultantphone", 0);
        double userDiscount = ModelUtil.getDouble(VIPDiscount, "healthconsultantdiscount", 1);

        return showResult(userId, originalPrice, vipCount, vipDiscount, userCount, userDiscount);
    }

    //专家图文
    private Map<String, Object> getShowAnswerPrice() {
        Map<String, Object> result = new HashMap<>();
        //获取原价
        Map<String, Object> answerPrice = answerService.getAnswerPrice(doctorId);
        int whetheropen = ModelUtil.getInt(answerPrice, "whetheropen", 0);
        if (whetheropen == 1) {
            BigDecimal originalPrice = ModelUtil.getDec(answerPrice, "price", BigDecimal.ZERO);

            Map<String, Object> uservipdiscount = userService.userVipDiscount(userId);
            int vipCount = ModelUtil.getInt(uservipdiscount, "medicalexpertceefax", 0);
            double vipDiscount = ModelUtil.getDouble(uservipdiscount, "medicalexpertdiscount", 1);

            Map<String, Object> VIPDiscount = userService.vipDiscount();
            int userCount = ModelUtil.getInt(VIPDiscount, "medicalexpertceefax", 0);
            double userDiscount = ModelUtil.getDouble(VIPDiscount, "medicalexpertdiscount", 1);
            result = showResult(userId, originalPrice, vipCount, vipDiscount, userCount, userDiscount);
        }
        result.put("whetheropen", whetheropen);

        return result;
    }

    //专家电话
    private Map<String, Object> getShowPhonePrice() {
        Map<String, Object> result = new HashMap<>();
        //获取原价
        Map<String, Object> phonePrice = answerService.getPhonePrice(doctorId);
        BigDecimal originalPrice = ModelUtil.getDec(phonePrice, "price", BigDecimal.ZERO);
        int whetheropen = ModelUtil.getInt(phonePrice, "whetheropen", 0);
        if (whetheropen == 1) {

            Map<String, Object> uservipdiscount = userService.userVipDiscount(userId);
            int vipCount = ModelUtil.getInt(uservipdiscount, "medicalexpertphone", 0);
            double vipDiscount = ModelUtil.getDouble(uservipdiscount, "medicalexpertdiscount", 1);

            Map<String, Object> VIPDiscount = userService.vipDiscount();
            int userCount = ModelUtil.getInt(VIPDiscount, "medicalexpertphone", 0);
            double userDiscount = ModelUtil.getDouble(VIPDiscount, "medicalexpertdiscount", 1);
            result = showResult(userId, originalPrice, vipCount, vipDiscount, userCount, userDiscount);
        }
        result.put("whetheropen", whetheropen);
        return result;
    }

    //专家电话
    private Map<String, Object> getShowVideoPrice() {
        Map<String, Object> result = new HashMap<>();
        //获取原价
        Map<String, Object> phonePrice = answerService.getVideoPrice(doctorId);
        BigDecimal originalPrice = ModelUtil.getDec(phonePrice, "price", BigDecimal.ZERO);
        int whetheropen = ModelUtil.getInt(phonePrice, "whetheropen", 0);
        if (whetheropen == 1) {
            Map<String, Object> uservipdiscount = userService.userVipDiscount(userId);
            int vipCount = ModelUtil.getInt(uservipdiscount, "medicalexpertvideo", 0);
            double vipDiscount = ModelUtil.getDouble(uservipdiscount, "medicalexpertdiscount", 1);

            Map<String, Object> VIPDiscount = userService.vipDiscount();
            int userCount = ModelUtil.getInt(VIPDiscount, "medicalexpertvideo", 0);
            double userDiscount = ModelUtil.getDouble(VIPDiscount, "medicalexpertdiscount", 1);
            result = showResult(userId, originalPrice, vipCount, vipDiscount, userCount, userDiscount);
        }
        result.put("whetheropen", whetheropen);
        return result;
    }

    private Map<String, Object> showResult(long userId, BigDecimal originalPrice, int vipCount, double vipDiscount, int userCount, double userDiscount) {
        Map<String, Object> result = new HashMap<>();
        //获取是否是Vip
        Map<String, Object> vip = userService.isVip(userId);
        if (ModelUtil.getInt(vip, "isvip", 0) == 1) {
            if (originalPrice.compareTo(BigDecimal.ZERO) == 0) {
                result.put("vipdiscountname", "免费");
            } else if (vipCount == -1000) {
                result.put("vipdiscountname", "尊享免费");
            } else if (vipCount > 0) {
                result.put("vipdiscountname", String.format("免费%s次", vipCount));
            } else {
                result.put("vipdiscountname", String.format("￥%s", StrUtil.getIntegerBigDecimal(originalPrice)));
            }
            result.put("viporiginalprice", String.format("￥%s", StrUtil.getIntegerBigDecimal(originalPrice.divide(new BigDecimal(vipDiscount), 2, RoundingMode.HALF_UP))));
        } else {
            if (originalPrice.compareTo(BigDecimal.ZERO) == 0) {
                result.put("userdiscountname", "免费");
            } else if (userCount == -1000) {
                result.put("userdiscountname", "会员可享无限免费");
            } else if (userCount > 0) {
                result.put("userdiscountname", String.format("会员可享免费%s次", userCount));
            } else {
                result.put("userdiscountname", String.format("会员可享%s", StrUtil.getDiscount(userDiscount)));
            }
            result.put("useroriginalprice", String.format("￥%s", StrUtil.getIntegerBigDecimal(originalPrice.divide(new BigDecimal(userDiscount), 2, RoundingMode.HALF_UP))));
        }
        result.put("isvip", ModelUtil.getInt(vip, "isvip", 0));
        return result;
    }


    //顾问图文
    private Map<String, Object> getBuildOutpatientPrice() {
        //获取原价
        Map<String, Object> phonePrice = answerService.getOutpatientPrice();
        BigDecimal originalPrice = ModelUtil.getDec(phonePrice, "price", BigDecimal.ZERO);

        Map<String, Object> uservipdiscount = userService.userVipDiscount(userId);
        long id = ModelUtil.getLong(uservipdiscount, "id");
        int vipCount = ModelUtil.getInt(uservipdiscount, "healthconsultantceefax", 0);
        double vipDiscount = ModelUtil.getDouble(uservipdiscount, "healthconsultantdiscount", 1);

        Map<String, Object> VIPDiscount = userService.vipDiscount();
        double userDiscount = ModelUtil.getDouble(VIPDiscount, "healthconsultantdiscount", 1);

        return buildResult(userId, id, originalPrice, vipCount, vipDiscount, userDiscount);
    }

    //顾问电话
    private Map<String, Object> getBuildDepartmentPrice() {
        //获取原价
        Map<String, Object> phonePrice = answerService.getDepartmentPrice();
        BigDecimal originalPrice = ModelUtil.getDec(phonePrice, "price", BigDecimal.ZERO);

        Map<String, Object> uservipdiscount = userService.userVipDiscount(userId);
        long id = ModelUtil.getLong(uservipdiscount, "id");
        int vipCount = ModelUtil.getInt(uservipdiscount, "healthconsultantphone", 0);
        double vipDiscount = ModelUtil.getDouble(uservipdiscount, "healthconsultantdiscount", 1);

        Map<String, Object> VIPDiscount = userService.vipDiscount();
        double userDiscount = ModelUtil.getDouble(VIPDiscount, "healthconsultantdiscount", 1);

        return buildResult(userId, id, originalPrice, vipCount, vipDiscount, userDiscount);
    }

    //专家图文
    private Map<String, Object> getBuildAnswerPrice() {
        Map<String, Object> result = new HashMap<>();
        //获取原价
        Map<String, Object> answerPrice = answerService.getAnswerPrice(doctorId);
        int whetheropen = ModelUtil.getInt(answerPrice, "whetheropen", 0);
        if (whetheropen == 1) {
            BigDecimal originalPrice = ModelUtil.getDec(answerPrice, "price", BigDecimal.ZERO);

            Map<String, Object> uservipdiscount = userService.userVipDiscount(userId);
            long id = ModelUtil.getLong(uservipdiscount, "id");
            int vipCount = ModelUtil.getInt(uservipdiscount, "medicalexpertceefax", 0);
            double vipDiscount = ModelUtil.getDouble(uservipdiscount, "medicalexpertdiscount", 1);

            Map<String, Object> VIPDiscount = userService.vipDiscount();
            double userDiscount = ModelUtil.getDouble(VIPDiscount, "medicalexpertdiscount", 1);
            result = buildResult(userId, id, originalPrice, vipCount, vipDiscount, userDiscount);
        }
        result.put("whetheropen", whetheropen);

        return result;
    }

    //专家电话
    private Map<String, Object> getBuildPhonePrice() {
        Map<String, Object> result = new HashMap<>();
        //获取原价
        Map<String, Object> phonePrice = answerService.getPhonePrice(doctorId);
        BigDecimal originalPrice = ModelUtil.getDec(phonePrice, "price", BigDecimal.ZERO);
        int whetheropen = ModelUtil.getInt(phonePrice, "whetheropen", 0);
        if (whetheropen == 1) {

            Map<String, Object> uservipdiscount = userService.userVipDiscount(userId);
            long id = ModelUtil.getLong(uservipdiscount, "id");
            int vipCount = ModelUtil.getInt(uservipdiscount, "medicalexpertphone", 0);
            double vipDiscount = ModelUtil.getDouble(uservipdiscount, "medicalexpertdiscount", 1);

            Map<String, Object> VIPDiscount = userService.vipDiscount();
            double userDiscount = ModelUtil.getDouble(VIPDiscount, "medicalexpertdiscount", 1);
            result = buildResult(userId, id, originalPrice, vipCount, vipDiscount, userDiscount);
        }
        result.put("whetheropen", whetheropen);
        return result;
    }

    //专家电话
    private Map<String, Object> getBuildVidelPrice() {
        Map<String, Object> result = new HashMap<>();
        //获取原价
        Map<String, Object> phonePrice = answerService.getVideoPrice(doctorId);
        BigDecimal originalPrice = ModelUtil.getDec(phonePrice, "price", BigDecimal.ZERO);
        int whetheropen = ModelUtil.getInt(phonePrice, "whetheropen", 0);
        if (whetheropen == 1) {
            Map<String, Object> uservipdiscount = userService.userVipDiscount(userId);
            long id = ModelUtil.getLong(uservipdiscount, "id");
            int vipCount = ModelUtil.getInt(uservipdiscount, "medicalexpertvideo", 0);
            double vipDiscount = ModelUtil.getDouble(uservipdiscount, "medicalexpertdiscount", 1);

            Map<String, Object> VIPDiscount = userService.vipDiscount();
            double userDiscount = ModelUtil.getDouble(VIPDiscount, "medicalexpertdiscount", 1);
            result = buildResult(userId, id, originalPrice, vipCount, vipDiscount, userDiscount);
        }
        result.put("whetheropen", whetheropen);
        return result;
    }


    private Map<String, Object> buildResult(long userId, long vipId, BigDecimal originalPrice, int vipCount, double vipDiscount, double userDiscount) {
        Map<String, Object> result = new HashMap<>();
        //获取是否是Vip
        Map<String, Object> vip = userService.isVip(userId);
        int type = 0;
        if (ModelUtil.getInt(vip, "isvip", 0) == 1) {
            if (originalPrice.compareTo(BigDecimal.ZERO) == 0) {
                type = ZERO;
                result.put("price", BigDecimal.ZERO);//实付
                result.put("originalprice", BigDecimal.ZERO);//应付
            } else if (vipCount == -1000) {
                type = VIP_Free;
                result.put("price", BigDecimal.ZERO);
                result.put("originalprice", BigDecimal.ZERO);
            } else if (vipCount > 0) {
                type = VIP_ZERO;
                result.put("price", BigDecimal.ZERO);
                result.put("originalprice", BigDecimal.ZERO);
            } else if (vipCount == 0) {
                type = VIP_DISCOUNT;
                result.put("price", StrUtil.getIntegerBigDecimal(originalPrice));
                result.put("originalprice", StrUtil.getIntegerBigDecimal(originalPrice.divide(new BigDecimal(vipDiscount), 2, RoundingMode.HALF_UP)));
            } else {
                type = ORIGINALPRICE;
                result.put("price", StrUtil.getIntegerBigDecimal(originalPrice));
                result.put("originalprice", StrUtil.getIntegerBigDecimal(originalPrice.divide(new BigDecimal(vipDiscount), 2, RoundingMode.HALF_UP)));
            }
            result.put("doctorprice", StrUtil.getIntegerBigDecimal(originalPrice));//医生设置的价格
            result.put("vipdiscount", vipDiscount);
            result.put("vipid", vipId);
        } else {
            if (originalPrice.compareTo(BigDecimal.ZERO) == 0) {
                type = ZERO;
                result.put("price", BigDecimal.ZERO);
                result.put("originalprice", BigDecimal.ZERO);
            } else {
                type = ORIGINALPRICE;
                result.put("price", StrUtil.getIntegerBigDecimal(originalPrice.divide(new BigDecimal(userDiscount), 2, RoundingMode.HALF_UP)));
                result.put("originalprice", StrUtil.getIntegerBigDecimal(originalPrice.divide(new BigDecimal(userDiscount), 2, RoundingMode.HALF_UP)));
            }
            result.put("vipdiscount", 1);
            result.put("doctorprice", StrUtil.getIntegerBigDecimal(originalPrice));//医生设置的价格
        }
        result.put("type", type);
        result.put("isvip", ModelUtil.getInt(vip, "isvip", 0));
        return result;
    }
}
