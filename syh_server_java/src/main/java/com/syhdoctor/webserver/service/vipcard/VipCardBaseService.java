package com.syhdoctor.webserver.service.vipcard;

import com.aliyuncs.exceptions.ClientException;
import com.syhdoctor.common.pay.IPayService;
import com.syhdoctor.common.utils.EnumUtils.*;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.TextFixed;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.common.utils.alidayu.SendShortMsgUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.mapper.vipcard.VipCardMapper;
import com.syhdoctor.webserver.mapper.wallet.UserWalletMapper;
import com.syhdoctor.webserver.service.wallet.UserWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public abstract class VipCardBaseService extends BaseService {

    @Autowired
    private VipCardMapper vipCardMapper;

    @Autowired
    private IPayService wechatAppPayImpl;

    @Autowired
    private IPayService aliAppPayImpl;

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Autowired
    private IPayService aliWebPayImpl;

    @Autowired
    private IPayService wechatWebPayImpl;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserWalletService userWalletService;

    /**
     * 会员列表
     *
     * @param id
     * @param begintime
     * @param endtime
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> vipCardList(long id, String vipcardno, String vipcardname, long begintime, long endtime, int pageIndex, int pageSize) {
        return vipCardMapper.vipCardList(id, vipcardno, vipcardname, begintime, endtime, pageIndex, pageSize);
    }

    /**
     * 会员详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> vipCardDetails(long id) {
        return vipCardMapper.vipCardDetails(id);
    }

    //查询用户vip
    public Map<String, Object> findVipCard(long userid) {
        return vipCardMapper.findVipCard(userid);

    }

    /**
     * 会员导出
     *
     * @param id
     * @param begintime
     * @param endtime
     * @return
     */
    public List<Map<String, Object>> vipCardExportList(long id, String vipcardno, String vipcardname, long begintime, long endtime) {
        List<Map<String, Object>> list = vipCardMapper.vipCardExportList(id, vipcardno, vipcardname, begintime, endtime);
        Map<String, Object> map = new HashMap<>();
        map.put("id", "编号");
        map.put("vipcardno", "会员卡号");
        map.put("vipcardname", "会员卡名称");
        map.put("price", "价格");
        map.put("healthconsultant", "健康顾问");
        map.put("medicalexpert", "医学专家");
        map.put("medicalgreen", "医疗绿通");
        map.put("healthconsultantceefax", "健康顾问图文次数");
        map.put("healthconsultantphone", "健康顾问电话次数");
        map.put("medicalexpertceefax", "医学专家图文次数");
        map.put("medicalexpertphone", "医学专家电话次数");
        map.put("medicalexpertvideo", "医学专家视频次数");
        map.put("medicalgreennum", "绿通次数");
        map.put("healthconsultantdiscount", "健康顾问折扣");
        map.put("medicalexpertdiscount", "医学专家折扣");
        map.put("effectivetime", "可用时间");
        map.put("createtime", "创建时间");
        list.add(0, map);
        return list;
    }

    /**
     * 会员卡行数
     *
     * @param id
     * @param begintime
     * @param endtime
     * @return
     */
    public long vipCardListCount(long id, String vipcardno, String vipcardname, long begintime, long endtime) {
        return vipCardMapper.vipCardListCount(id, vipcardno, vipcardname, begintime, endtime);
    }

    /**
     * 新增会员卡
     *
     * @param vipcardname
     * @param price
     * @param healthconsultant
     * @param medicalexpert
     * @param medicalgreen
     * @param effectivetime
     * @return
     */
    public boolean addVipCard(Double healthconsultantdiscount, Double medicalexpertdiscount, String vipcardname, BigDecimal price, BigDecimal renewal_fee, BigDecimal original_price, String healthconsultant, String medicalexpert, String medicalgreen, int ceefax, int video, Double discount, long effectivetime, int sort, long health_consultant_ceefax, long health_consultant_phone, long medical_expert_ceefax, long medical_expert_phone, long medical_expert_video) {
        return vipCardMapper.addVipCard(healthconsultantdiscount, medicalexpertdiscount, vipcardname, price, renewal_fee, original_price, healthconsultant, medicalexpert, medicalgreen, ceefax, video, discount, effectivetime, sort, health_consultant_ceefax, health_consultant_phone, medical_expert_ceefax, medical_expert_phone, medical_expert_video);
    }


    /**
     * 删除会员卡
     *
     * @param id
     * @return
     */
    public boolean delVipCard(long id) {
        return vipCardMapper.delVipCard(id);
    }


    /**
     * 修改会员
     *
     * @param id
     * @param vipcardno
     * @param vipcardname
     * @param price
     * @param healthconsultant
     * @param medicalexpert
     * @param medicalgreen
     * @param effectivetime
     * @return
     */
    public boolean updateVipCard(Double healthconsultantdiscount, Double medicalexpertdiscount, long id, String vipcardno, String vipcardname, BigDecimal price, BigDecimal renewal_fee, BigDecimal original_price, String healthconsultant, String medicalexpert, String medicalgreen, int ceefax, int video, Double discount, long effectivetime, int sort, long health_consultant_ceefax, long health_consultant_phone, long medical_expert_ceefax, long medical_expert_phone, long medical_expert_video) {

        return vipCardMapper.updateVipCard(healthconsultantdiscount, medicalexpertdiscount, id, vipcardno, vipcardname, price, renewal_fee, original_price, healthconsultant, medicalexpert, medicalgreen, ceefax, video, discount, effectivetime, sort, health_consultant_ceefax, health_consultant_phone, medical_expert_ceefax, medical_expert_phone, medical_expert_video);
    }


    /**
     * 新增或修改会员
     *
     * @param id
     * @param vipcardno
     * @param vipcardname
     * @param price
     * @param healthconsultant
     * @param medicalexpert
     * @param medicalgreen
     * @param effectivetime
     * @return
     */
    public boolean updateAddVipCard(Double healthconsultantdiscount, Double medicalexpertdiscount, long id, String vipcardno, String vipcardname, BigDecimal price, BigDecimal renewal_fee, BigDecimal original_price, String healthconsultant, String medicalexpert, String medicalgreen, int ceefax, int video, Double discount, long effectivetime, int sort, long health_consultant_ceefax, long health_consultant_phone, long medical_expert_ceefax, long medical_expert_phone, long medical_expert_video) {
        boolean a = true;
        vipCardMapper.vipSort(sort);
        if (id == 0) {
            a = addVipCard(healthconsultantdiscount, medicalexpertdiscount, vipcardname, price, renewal_fee, original_price, healthconsultant, medicalexpert, medicalgreen, ceefax, video, discount, effectivetime, sort, health_consultant_ceefax, health_consultant_phone, medical_expert_ceefax, medical_expert_phone, medical_expert_video);
        } else {
            a = updateVipCard(healthconsultantdiscount, medicalexpertdiscount, id, vipcardno, vipcardname, price, renewal_fee, original_price, healthconsultant, medicalexpert, medicalgreen, ceefax, video, discount, effectivetime, sort, health_consultant_ceefax, health_consultant_phone, medical_expert_ceefax, medical_expert_phone, medical_expert_video);
        }
        return a;
    }

    //微信app支付
    public IPayService.PayBean weChatAppPay(BigDecimal money, String ip, String orderno, long userid, int order_type) {
        Map<String, Object> user = userWalletMapper.getUserWallet(userid);
        if (ModelUtil.getInt(user, "id") == 0) {
            throw new ServiceException("用户不存在");
        }
        String notifyUrl = null;
        if (0 == order_type) {
            notifyUrl = ConfigModel.APILINKURL + "wechatPay/vipWechatAppNotifyUrl";
        } else {
            notifyUrl = ConfigModel.APILINKURL + "wechatPay/vipRenewWechatAppNotifyUrl";
        }
        // 订单名称，必填
        String body = TextFixed.body;
        if (money.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        IPayService.PayBean payBean = wechatAppPayImpl.pay(orderno, money, null, body, ip, notifyUrl, null);
        return payBean;
    }


    //微信web支付
    public IPayService.PayBean weChatWebPay(String orderNo, BigDecimal actualmoney, String ip, long userId) {
        Map<String, Object> user = userWalletMapper.getUserWallet(userId);

        if (ModelUtil.getInt(user, "id") == 0) {
            throw new ServiceException("用户不存在");
        }
        Map<String, Object> userOpenId = userWalletMapper.getUserOpenId(userId, OpenTypeEnum.Wechat);
        String openId = ModelUtil.getStr(userOpenId, "openid");
        String notifyUrl = ConfigModel.APILINKURL + "wechatPay/vipWechatWebNotifyUrl";
        // 订单名称，必填
        String body = TextFixed.body;
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        return wechatWebPayImpl.pay(orderNo, actualmoney, openId, body, ip, notifyUrl, null);
    }

    //支付宝app支付
    public IPayService.PayBean aliAppPay(BigDecimal actualmoney, String orderno, long userid, int orderType) {
        Map<String, Object> user = userWalletMapper.getUserWallet(userid);
        if (ModelUtil.getInt(user, "id") == 0) {
            throw new ServiceException("用户不存在");
        }
        // 订单名称，必填
        String subject = TextFixed.body;
        // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
        String notify_url = null;
        if (0 == orderType) {
            notify_url = ConfigModel.APILINKURL + "aliCallback/vipCardAliAppNotifyUrl";
        } else {
            notify_url = ConfigModel.APILINKURL + "aliCallback/vipReneCardAliAppNotifyUrl";
        }
        // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
        String return_url = ConfigModel.APILINKURL + "aliCallback/rechargeableAliAppReturnUrl";
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        IPayService.PayBean pay = aliAppPayImpl.pay(orderno, actualmoney, null, subject, null, notify_url, return_url);
        return pay;

    }

    //支付宝web支付
    public IPayService.PayBean aliWebPay(String orderNo, BigDecimal actualmoney, long userId) {
        Map<String, Object> user = userWalletMapper.getUserWallet(userId);
        if (ModelUtil.getInt(user, "id") == 0) {
            throw new ServiceException("用户不存在");
        }
        // 订单名称，必填
        String subject = TextFixed.body;
        // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
        String notify_url = ConfigModel.APILINKURL + "aliCallback/vipCardAliWebNotifyUrl";
        // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
        String return_url = ConfigModel.APILINKURL + "aliCallback/vipWebAliWebReturnUrl";
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        return aliWebPayImpl.pay(orderNo, actualmoney, null, subject, null, notify_url, return_url);
    }

    //钱包支付
    @Transactional
    public synchronized long wallet(BigDecimal bigDecimal, BigDecimal actualmoney, long orderid, long userid, int order_type, String orderNo) {
//        BigDecimal finalAmount = bigDecimal.subtract(actualmoney);
//        boolean res = vipCardMapper.updateWallet(userid, PriceUtil.addPrice(finalAmount));
        boolean res = userWalletService.subtractUserWallet(orderNo, TransactionTypeStateEnum.OpenVip, userid, actualmoney);
        if (res) {
            if (0 == order_type) {
                updateStatusWeAli(orderid, userid, PayTypeEnum.Wallet.getCode());//首冲
            } else {
                Map<String, Object> orderByorderId = vipCardMapper.findOrderByorderId(orderid);
                long cardid = ModelUtil.getLong(orderByorderId, "vip_cardid");
                updateStatusReneWeAli(orderid, userid, PayTypeEnum.Wallet.getCode(), orderNo, cardid);
            }
            return orderid;
        } else {
            throw new ServiceException("因为异常钱包扣款失败，请重试");
        }
    }

    //根据订单号查询订单
    public Map<String, Object> findByOrder(String orderNo) {
        return vipCardMapper.findOrderByNo(orderNo);

    }

    //前端首冲更新状态s
    public void updateStatusWeAli(long orderid, long userid, int paytype) {
        Map<String, Object> userMemById = vipCardMapper.findUserMemById(userid);
        int level = ModelUtil.getInt(userMemById, "level");//当前等级
        long upgrade_integral = vipCardMapper.findEnjoyLevel(level + 1);//升下一级所需积分
        long current_integral = ModelUtil.getLong(userMemById, "current_integral");//当前积分
        Map<String, Object> map = vipCardMapper.findNum();
        if (null == map) {
            map.put("current_integral", 1);
            map.put("price", 1);
        }
        Map<String, Object> mapOrder = vipCardMapper.findOrderByorderId(orderid);
        BigDecimal p = ModelUtil.getDec(map, "price", BigDecimal.ZERO);
        BigDecimal integer = ModelUtil.getDec(map, "current_integral", BigDecimal.ZERO);
        BigDecimal price = null;
        if (ModelUtil.getLong(mapOrder, "order_type") == 1) {//续费价格
            price = ModelUtil.getDec(mapOrder, "renewal_fee", BigDecimal.ZERO);
        } else {
            price = ModelUtil.getDec(mapOrder, "price", BigDecimal.ZERO);
        }
        BigDecimal finalInte = price.divide(p, 2);
        BigDecimal finaIn = finalInte.multiply(integer);
        long a = current_integral + finaIn.longValue();
        if (a > upgrade_integral || a == upgrade_integral) {
            level += 1;
            a = a - upgrade_integral;
            upgrade_integral = vipCardMapper.findEnjoyLevel(level + 1);//若升级,则再找下一级所需积分
        }
        //更新订单状态
        vipCardMapper.updateStatus(orderid, paytype, 2);
        //更新用户vip卡状态
        vipCardMapper.updateUserVip(userid, level, a, upgrade_integral);
        //添加用户交易记录
//        addUserRecord(orderid, userid, TransactionTypeStateEnum.OpenVip);
    }

    //前端首冲更新状态s
    public void updateStatus(long orderid, long userid, int paytype) {
        Map<String, Object> userMemById = vipCardMapper.findUserMemById(userid);
        int level = ModelUtil.getInt(userMemById, "level");//当前等级
        long upgrade_integral = vipCardMapper.findEnjoyLevel(level + 1);//升下一级所需积分
        long current_integral = ModelUtil.getLong(userMemById, "current_integral");//当前积分
        Map<String, Object> map = vipCardMapper.findNum();
        if (null == map) {
            map.put("current_integral", 1);
            map.put("price", 1);
        }
        Map<String, Object> mapOrder = vipCardMapper.findOrderByorderId(orderid);
        BigDecimal p = ModelUtil.getDec(map, "price", BigDecimal.ZERO);
        BigDecimal integer = ModelUtil.getDec(map, "current_integral", BigDecimal.ZERO);
        BigDecimal price = null;
        if (ModelUtil.getLong(mapOrder, "order_type") == 1) {//续费价格
            price = ModelUtil.getDec(mapOrder, "renewal_fee", BigDecimal.ZERO);
        } else {
            price = ModelUtil.getDec(mapOrder, "price", BigDecimal.ZERO);
        }
        BigDecimal finalInte = price.divide(p, 2);
        BigDecimal finaIn = finalInte.multiply(integer);
        long a = current_integral + finaIn.longValue();
        if (a > upgrade_integral || a == upgrade_integral) {
            level += 1;
            a = a - upgrade_integral;
            upgrade_integral = vipCardMapper.findEnjoyLevel(level + 1);//若升级,则再找下一级所需积分
        }
        //更新订单状态
        vipCardMapper.updateStatus(orderid, paytype, 2);
        //更新用户vip卡状态
        vipCardMapper.updateUserVip(userid, level, a, upgrade_integral);
        //添加用户交易记录
        //addUserRecord(orderid, userid, TransactionTypeStateEnum.OpenVip);
    }

    //后台首冲更新状态
    public boolean updateStatusBack(long orderid, long userid, int paytype, int operate_mode) {
        Map<String, Object> userMemById = vipCardMapper.findUserMemById(userid);
        int level = ModelUtil.getInt(userMemById, "level");//等级
        long upgrade_integral = vipCardMapper.findEnjoyLevel(level + 1);//升下一级所需积分
        long current_integral = ModelUtil.getLong(userMemById, "current_integral");//当前积分
        Map<String, Object> map = vipCardMapper.findNum();
        if (null == map) {
            map.put("current_integral", 1);
            map.put("price", 1);
        }
        Map<String, Object> mapOrder = vipCardMapper.findOrderByorderId(orderid);
        BigDecimal p = ModelUtil.getDec(map, "price", BigDecimal.ZERO);
        BigDecimal integer = ModelUtil.getDec(map, "current_integral", BigDecimal.ZERO);
        BigDecimal price = null;
        if (ModelUtil.getLong(mapOrder, "order_type") == 1) {//续费价格
            price = ModelUtil.getDec(mapOrder, "renewal_fee", BigDecimal.ZERO);
        } else {
            price = ModelUtil.getDec(mapOrder, "price", BigDecimal.ZERO);
        }
        BigDecimal finalInte = price.divide(p, 2);
        BigDecimal finaIn = finalInte.multiply(integer);
        long a = current_integral + finaIn.longValue();
        if (a > upgrade_integral || a == upgrade_integral) {
            level += 1;
            a = a - upgrade_integral;
            upgrade_integral = vipCardMapper.findEnjoyLevel(level + 1);//升下一级所需积分
        }
        //更新订单状态
        vipCardMapper.updateStatus(orderid, paytype, operate_mode);
        //更新用户vip卡状态
        vipCardMapper.updateUserVip(userid, level, a, upgrade_integral);
        //添加用户交易记录
        //addUserRecord(orderid, userid, TransactionTypeStateEnum.OpenVip);
        return true;
    }


    //前端续费更新状态
    public void updateStatusRene(long orderid, long userid, int paytype, String orderNo, long vipcardid) {
        Map<String, Object> userMemById = vipCardMapper.findUserMemById(userid);
        int level = ModelUtil.getInt(userMemById, "level");//当前等级
        long upgrade_integral = vipCardMapper.findEnjoyLevel(level + 1);//升下一级所需积分
        long current_integral = ModelUtil.getLong(userMemById, "current_integral");//当前积分
        Map<String, Object> map = vipCardMapper.findNum();
        if (null == map) {
            map.put("current_integral", 100);
            map.put("price", 1);
        }
        Map<String, Object> mapOrder = vipCardMapper.findOrderByorderId(orderid);
        BigDecimal p = ModelUtil.getDec(map, "price", BigDecimal.ZERO);
        BigDecimal integer = ModelUtil.getDec(map, "current_integral", BigDecimal.ZERO);
        BigDecimal price = null;
        if (ModelUtil.getLong(mapOrder, "order_type") == 1) {//续费价格
            price = ModelUtil.getDec(mapOrder, "renewal_fee", BigDecimal.ZERO);
        } else {
            price = ModelUtil.getDec(mapOrder, "price", BigDecimal.ZERO);
        }
        BigDecimal finalInte = price.divide(p, 2);
        BigDecimal finaIn = finalInte.multiply(integer);
        long a = current_integral + finaIn.longValue();
        if (a > upgrade_integral || a == upgrade_integral) {
            level += 1;
            a = a - upgrade_integral;
            upgrade_integral = vipCardMapper.findEnjoyLevel(level + 1);//升下一级所需积分
        }
        Map<String, Object> orderByNo = vipCardMapper.findOrderByNo(orderNo);
        //修改过期时间，图文，视频次数
        long medical_expert_ceefax = ModelUtil.getLong(userMemById, "medical_expert_ceefax");
        long medical_expert_phone = ModelUtil.getLong(userMemById, "medical_expert_phone");
        long medical_expert_video = ModelUtil.getLong(userMemById, "medical_expert_video");
        long health_consultant_phone = ModelUtil.getLong(userMemById, "health_consultant_phone");
        long health_consultant_ceefax = ModelUtil.getLong(userMemById, "health_consultant_ceefax");

        long expirTime = ModelUtil.getLong(userMemById, "vip_expiry_time");
        Double healthdisconut = ModelUtil.getDouble(orderByNo, "health_consultant_discount", 0);
        Double medicaldisconut = ModelUtil.getDouble(orderByNo, "medical_expert_discount", 0);
        long allmedicalceefax = medical_expert_ceefax + ModelUtil.getLong(orderByNo, "medical_expert_ceefax");
        long allmedicalphone = medical_expert_phone + ModelUtil.getLong(orderByNo, "medical_expert_phone");
        long allmedicalvideo = medical_expert_video + ModelUtil.getLong(orderByNo, "medical_expert_video");
        long allhealthphone = health_consultant_phone + ModelUtil.getLong(orderByNo, "health_consultant_phone");
        long allhealthceefax = health_consultant_ceefax + ModelUtil.getLong(orderByNo, "health_consultant_ceefax");
        long allExpirTime = getAllExpir(expirTime, vipcardid);
        //更新订单状态
        vipCardMapper.updateStatus(orderid, paytype, 2);
        vipCardMapper.updateUserRene(userid, level, a, upgrade_integral, allExpirTime, allmedicalceefax, allmedicalphone, allmedicalvideo, allhealthphone, allhealthceefax, healthdisconut, medicaldisconut);
        //添加用户交易记录
        //addUserRecord(orderid, userid, TransactionTypeStateEnum.RenewVip);
    }

    public void updateStatusReneWeAli(long orderid, long userid, int paytype, String orderNo, long vipcardid) {
        Map<String, Object> userMemById = vipCardMapper.findUserMemById(userid);
        int level = ModelUtil.getInt(userMemById, "level");//当前等级
        long upgrade_integral = vipCardMapper.findEnjoyLevel(level + 1);//升下一级所需积分
        long current_integral = ModelUtil.getLong(userMemById, "current_integral");//当前积分
        Map<String, Object> map = vipCardMapper.findNum();
        if (null == map) {
            map.put("current_integral", 100);
            map.put("price", 1);
        }
        Map<String, Object> mapOrder = vipCardMapper.findOrderByorderId(orderid);
        BigDecimal p = ModelUtil.getDec(map, "price", BigDecimal.ZERO);
        BigDecimal integer = ModelUtil.getDec(map, "current_integral", BigDecimal.ZERO);
        BigDecimal price = null;
        if (ModelUtil.getLong(mapOrder, "order_type") == 1) {//续费价格
            price = ModelUtil.getDec(mapOrder, "renewal_fee", BigDecimal.ZERO);
        } else {
            price = ModelUtil.getDec(mapOrder, "price", BigDecimal.ZERO);
        }
        BigDecimal finalInte = price.divide(p, 2);
        BigDecimal finaIn = finalInte.multiply(integer);
        long a = current_integral + finaIn.longValue();
        if (a > upgrade_integral || a == upgrade_integral) {
            level += 1;
            a = a - upgrade_integral;
            upgrade_integral = vipCardMapper.findEnjoyLevel(level + 1);//升下一级所需积分
        }
        Map<String, Object> orderByNo = vipCardMapper.findOrderByNo(orderNo);
        //修改过期时间，图文，视频次数
        long medical_expert_ceefax = ModelUtil.getLong(userMemById, "medical_expert_ceefax");
        long medical_expert_phone = ModelUtil.getLong(userMemById, "medical_expert_phone");
        long medical_expert_video = ModelUtil.getLong(userMemById, "medical_expert_video");
        long health_consultant_phone = ModelUtil.getLong(userMemById, "health_consultant_phone");
        long health_consultant_ceefax = ModelUtil.getLong(userMemById, "health_consultant_ceefax");

        long expirTime = ModelUtil.getLong(userMemById, "vip_expiry_time");
        Double healthdisconut = ModelUtil.getDouble(orderByNo, "health_consultant_discount", 0);
        Double medicaldisconut = ModelUtil.getDouble(orderByNo, "medical_expert_discount", 0);
        long allmedicalceefax = medical_expert_ceefax + ModelUtil.getLong(orderByNo, "medical_expert_ceefax");
        long allmedicalphone = medical_expert_phone + ModelUtil.getLong(orderByNo, "medical_expert_phone");
        long allmedicalvideo = medical_expert_video + ModelUtil.getLong(orderByNo, "medical_expert_video");
        long allhealthphone = health_consultant_phone + ModelUtil.getLong(orderByNo, "health_consultant_phone");
        long allhealthceefax = health_consultant_ceefax + ModelUtil.getLong(orderByNo, "health_consultant_ceefax");
        long allExpirTime = getAllExpir(expirTime, vipcardid);
        //更新订单状态
        vipCardMapper.updateStatus(orderid, paytype, 2);
        vipCardMapper.updateUserRene(userid, level, a, upgrade_integral, allExpirTime, allmedicalceefax, allmedicalphone, allmedicalvideo, allhealthphone, allhealthceefax, healthdisconut, medicaldisconut);
        //添加用户交易记录
//        addUserRecord(orderid, userid, TransactionTypeStateEnum.RenewVip);
    }

    public int answerWeChatPayStatus(long orderId) {
        Map<String, Object> answerOrder = vipCardMapper.getAnswerOrder(orderId);
        int status = ModelUtil.getInt(answerOrder, "status");
        return status;//1:未支付,2:已支付
    }

    //首冲点击立即充值生成订单和同步用户会员信息
    @Transactional
    public Map<String, Object> createOrderAndSys(long userid, long vipcardid) {
        Map<String, Object> map = vipCardMapper.insertOrder(userid, vipcardid);
        boolean a = vipCardMapper.syscoreUser(userid, vipcardid);
        if (a) {
            return map;
        } else {
            throw new ServiceException("因为异常生成订单失败,请重试");
        }
    }

    public Map<String, Object> getAmount(long orderid) {
        return vipCardMapper.getAmount(orderid);
    }

    //续费点击立即充值生成订单
    public Map<String, Object> renewalOrder(long userid, long vipcardid) {
        return vipCardMapper.renewalOrder(userid, vipcardid);
    }

    public Map<String, Object> getUserMember(long userId) {
        return vipCardMapper.getUserMember(userId);
    }

    public Map<String, Object> findOrderByorderId(long orderid) {
        return vipCardMapper.findOrderByorderId(orderid);
    }

    public int vipOrderPayStatus(long orderId) {
        Map<String, Object> answerOrder = vipCardMapper.findOrderByorderId(orderId);
        int status = ModelUtil.getInt(answerOrder, "paystatus");
        return status == PayStateEnum.Paid.getCode() ? 1 : 0;
    }

    public Map<String, Object> userVipDiscount(long userId) {
        return vipCardMapper.userVipDiscount(userId);
    }

    public Map<String, Object> vipDiscount() {
        return vipCardMapper.vipDiscount();
    }

    //后台续费更新状态
    public boolean updateStatusReneByback(long orderid, long userid, int paytype, String orderNo, int operateMode, long vipcardid) {
        Map<String, Object> userMemById = vipCardMapper.findUserMemById(userid);
        int level = ModelUtil.getInt(userMemById, "level");//等级
        long upgrade_integral = vipCardMapper.findEnjoyLevel(level + 1);//升下一级所需积分
        long current_integral = ModelUtil.getLong(userMemById, "current_integral");//当前积分
        Map<String, Object> map = vipCardMapper.findNum();
        if (null == map) {
            map.put("current_integral", 1);
            map.put("price", 1);
        }
        Map<String, Object> mapOrder = vipCardMapper.findOrderByorderId(orderid);
        BigDecimal p = ModelUtil.getDec(map, "price", BigDecimal.ZERO);
        BigDecimal integer = ModelUtil.getDec(map, "current_integral", BigDecimal.ZERO);
        BigDecimal price = null;
        if (ModelUtil.getLong(mapOrder, "order_type") == 1) {//续费价格
            price = ModelUtil.getDec(mapOrder, "renewal_fee", BigDecimal.ZERO);
        } else {
            price = ModelUtil.getDec(mapOrder, "price", BigDecimal.ZERO);
        }
        BigDecimal finalInte = price.divide(p, 2);
        BigDecimal finaIn = finalInte.multiply(integer);
        long a = current_integral + finaIn.longValue();
        if (a > upgrade_integral || a == upgrade_integral) {
            level += 1;
            a = a - upgrade_integral;
            upgrade_integral = vipCardMapper.findEnjoyLevel(level + 1);//升下一级所需积分
        }
        Map<String, Object> orderByNo = vipCardMapper.findOrderByNo(orderNo);
        //修改过期时间，图文，视频次数
        long medical_expert_ceefax = ModelUtil.getLong(userMemById, "medical_expert_ceefax");
        long medical_expert_phone = ModelUtil.getLong(userMemById, "medical_expert_phone");
        long medical_expert_video = ModelUtil.getLong(userMemById, "medical_expert_video");
        long health_consultant_phone = ModelUtil.getLong(userMemById, "health_consultant_phone");
        long health_consultant_ceefax = ModelUtil.getLong(userMemById, "health_consultant_ceefax");

        long expirTime = ModelUtil.getLong(userMemById, "vip_expiry_time");
        long allExpirTime = getAllExpir(expirTime, vipcardid);
        Double healthdisconut = ModelUtil.getDouble(orderByNo, "health_consultant_discount", 0);
        Double medicaldisconut = ModelUtil.getDouble(orderByNo, "medical_expert_discount", 0);
        long allmedicalceefax = medical_expert_ceefax + ModelUtil.getLong(orderByNo, "medical_expert_ceefax");
        long allmedicalphone = medical_expert_phone + ModelUtil.getLong(orderByNo, "medical_expert_phone");
        long allmedicalvideo = medical_expert_video + ModelUtil.getLong(orderByNo, "medical_expert_video");
        long allhealthphone = health_consultant_phone + ModelUtil.getLong(orderByNo, "health_consultant_phone");
        long allhealthceefax = health_consultant_ceefax + ModelUtil.getLong(orderByNo, "health_consultant_ceefax");

        //更新订单状态
        vipCardMapper.updateStatus(orderid, paytype, operateMode);
        vipCardMapper.updateUserRene(userid, level, a, upgrade_integral, allExpirTime, allmedicalceefax, allmedicalphone, allmedicalvideo, allhealthphone, allhealthceefax, healthdisconut, medicaldisconut);
        //添加用户交易记录
        //addUserRecord(orderid, userid, TransactionTypeStateEnum.RenewVip);
        return true;
    }

    public long getAllExpir(long expirTime, long vipcardid) {
        Map<String, Object> efftime = vipCardMapper.findCardById(vipcardid);
        int efftimes = ModelUtil.getInt(efftime, "effective_time");//会员卡可用时间
        long nowTime = UnixUtil.getNowTimeStamp();
        long allExpirTime = vipCardMapper.getTi(expirTime, efftimes);
        if (nowTime > expirTime || nowTime == expirTime) {//过期,在当前时间往后推

            allExpirTime = vipCardMapper.getTi(nowTime, efftimes);
        }
        return allExpirTime;
    }

    public boolean updateHealthConsultantCeefax(long vipid) {
        return vipCardMapper.updateHealthConsultantCeefax(vipid);
    }

    public boolean updateMedicalExpertVideo(long vipid) {
        return vipCardMapper.updateMedicalExpertVideo(vipid);
    }

    public boolean updateMedicalExpertCeefax(long vipid) {
        return vipCardMapper.updateMedicalExpertCeefax(vipid);
    }

    public boolean updateHealthConsultantPhone(long vipid) {
        return vipCardMapper.updateHealthConsultantPhone(vipid);
    }

    public boolean updateMedicalExpertPhone(long vipid) {
        return vipCardMapper.updateMedicalExpertPhone(vipid);
    }


    /**
     * 未首值的用户
     *
     * @return
     */
    public List<Map<String, Object>> userlistDropdownBox() {
        return vipCardMapper.userlistDropdownBox();
    }

    public List<Map<String, Object>> viplistDropdownBox() {
        return vipCardMapper.viplistDropdownBox();
    }

    public boolean sendMesg(String phone) {
        boolean flag = true;
        try {
            String code = UnixUtil.getCode();
            //将验证码存入redis
            Map<String, Object> map = new HashMap<>();
            map.put("code", code);
            SendShortMsgUtil.sendSms(ConfigModel.ISONLINE, phone, com.syhdoctor.common.config.ConfigModel.SMS.Login_sms_template, map);
            this.redisTemplate.opsForValue().set("recharge" + phone, code);
            this.redisTemplate.expire("recharge" + phone, com.syhdoctor.common.config.ConfigModel.SMS.timeout, TimeUnit.SECONDS);
        } catch (ClientException e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 验证验证码
     *
     * @param phone 手机号码
     * @param phone 验证码
     * @return
     */
    public int validCode(String phone, String code) {
        Object codeObject = redisTemplate.opsForValue().get("recharge" + phone);
        int result;
        if (TextFixed.def_code.equals(code) || codeObject != null) {
            if (TextFixed.def_code.equals(code) || (codeObject != null && codeObject.toString().equals(code))) {
                result = 1;
            } else {
                result = -2;
            }
        } else {
            result = -1;
        }
        return result;
    }

    public Map<String, Object> getUsermember(long id) {
        return vipCardMapper.getUserMembers(id);
    }


}
