package com.syhdoctor.webserver.service.wallet;

import com.aliyuncs.exceptions.ClientException;
import com.syhdoctor.common.config.ConfigModel;
import com.syhdoctor.common.utils.EnumUtils.IdGenerator;
import com.syhdoctor.common.utils.EnumUtils.PayStateEnum;
import com.syhdoctor.common.utils.EnumUtils.PayTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.TransactionTypeStateEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.TextFixed;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.common.utils.alidayu.SendShortMsgUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.mapper.wallet.RechargeableOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public abstract class RechargeableOrderBaseService extends BaseService {

    @Autowired
    private RechargeableOrderMapper rechargeableOrderMapper;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private DoctorWalletService doctorWalletService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 充值记录查询
     *
     * @param id
     * @param amountmoney
     * @param lotnumber
     * @param name
     * @param paytype
     * @param begintime
     * @param endtime
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getRechargeableOrderList(long id, BigDecimal amountmoney, String lotnumber, String name, int paytype, long begintime, long endtime, int pageIndex, int pageSize) {
        return rechargeableOrderMapper.getRechargeableOrderList(id, amountmoney, lotnumber, name, paytype, begintime, endtime, pageIndex, pageSize);

    }

    /**
     * 行数
     *
     * @param id
     * @param amountmoney
     * @param lotnumber
     * @param name
     * @param paytype
     * @param begintime
     * @param endtime
     * @return
     */
    public long getRechargeableOrdersCount(long id, BigDecimal amountmoney, String lotnumber, String name, int paytype, long begintime, long endtime) {
        return rechargeableOrderMapper.getRechargeableOrderCount(id, amountmoney, lotnumber, name, paytype, begintime, endtime);
    }

    /**
     * 充值记录导出
     *
     * @param id
     * @param amountmoney
     * @param lotnumber
     * @param name
     * @param paytype
     * @param begintime
     * @param endtime
     * @return
     */
    public List<Map<String, Object>> getRechargeableOrderExportListAll(long id, BigDecimal amountmoney, String lotnumber, String name, int paytype, long begintime, long endtime) {
        List<Map<String, Object>> list = rechargeableOrderMapper.getRechargeableOrderExportListAll(id, amountmoney, lotnumber, name, paytype, begintime, endtime);
        Map<String, Object> map = new HashMap<>();
        map.put("id", "编号");
        map.put("paytype", "充值方式");
        map.put("amountmoney", "金额");
        map.put("lotnumber", "批号");
        map.put("redeemcode", "兑换码");
        map.put("begintime", "生成时间");
        map.put("userid", "账户编号");
        map.put("name", "账户姓名");
        list.add(0, map);
        return list;
    }

    public List<Map<String, Object>> getRechargeableOrderExportLot(BigDecimal amountmoney, String lotnumber, long begintime, long endtime) {
        List<Map<String, Object>> list = rechargeableOrderMapper.getRechargeableOrderExportLot(amountmoney, lotnumber, begintime, endtime);
        Map<String, Object> map = new HashMap<>();
        map.put("id", "编号");
        map.put("amountmoney", "金额");
        map.put("lotnumber", "批号");
        map.put("create_time", "生成时间");
        map.put("effectivetype", "有效期");
        map.put("total", "生成数量");
        map.put("principal", "负责人");
        map.put("createcardstatus", "充值卡是否生成完成");
        map.put("notusedcount", "未使用数量");
        map.put("begintime", "开始时间");
        map.put("endtime", "结束时间");
        list.add(0, map);
        return list;
    }


    public void createDetail(long id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                rechargeableOrderMapper.createDetail(id);
            }
        }) {
        }.start();
    }

    public long addRechargeablecardlot(BigDecimal amountmoney, int effectivetype, long begintime, long endtime, long total, String principal, String channelprefix, long createuser) {
        if (effectivetype == 2) {
            if (endtime < begintime || endtime == begintime) {
                throw new ServiceException("开始结束时间不合法");
            }
        }
        return rechargeableOrderMapper.addRechargeablecardlot(amountmoney, effectivetype, begintime, endtime, total, principal, channelprefix, createuser);
    }

    public void deleteLot(long id) {
        rechargeableOrderMapper.deleteLot(id);
    }

    public Map<String, Object> veriy(long id, long beginTime) {
        long overTime = 6 * 1000;
        Map<String, Object> maps = new HashMap<>();
        for (long i = 0; i >= 0; i++) {
            System.out.println(i);
            long nowTime = System.currentTimeMillis();
            Map<String, Object> map = getResult(id);
            int status = ModelUtil.getInt(map, "createcardstatus");
            if (status == 1) {
                maps.put("result", 1);
                break;
            }
            long time = nowTime - beginTime;
            if (time > overTime) {
                maps.put("result", 0);
                deleteLot(id);
                break;
            }
        }
        return maps;
    }


    public List<Map<String, Object>> findValue() {
        return rechargeableOrderMapper.findValue();
    }


    public List<Map<String, Object>> getRechargeableOrderList(BigDecimal amountmoney, String lotnumber, long begintime, long endtime, int pageIndex, int pageSize) {
        return rechargeableOrderMapper.getRechargeableOrderList(amountmoney, lotnumber, begintime, endtime, pageIndex, pageSize);

    }


    public long getRechargeableOrderCount(BigDecimal amountmoney, String lotnumber, long begintime, long endtime) {
        return rechargeableOrderMapper.getRechargeableOrderCount(amountmoney, lotnumber, begintime, endtime);
    }


    public List<Map<String, Object>> getDetailList(long id, int pageIndex, int pageSize) {
        return rechargeableOrderMapper.getDetailList(id, pageIndex, pageSize);
    }

    public List<Map<String, Object>> getDetailList(long id) {
        List<Map<String, Object>> detailList = rechargeableOrderMapper.getDetailList(id);
        Map<String, Object> map = new HashMap<>();
        map.put("id", "编号");
        map.put("redeemcode", "兑换码");
        map.put("amountmoney", "面值");
        map.put("lotnumber", "批号");
        map.put("effectivetype", "有效期");
        map.put("begintime", "开始时间");
        map.put("endtime", "结束时间");
        map.put("principal", "负责人");
        detailList.add(0, map);
        return detailList;
    }

    public Map<String, Object> getDetailHeader(long id) {
        Map<String, Object> head = rechargeableOrderMapper.getHead(id);
        return head;
    }

    public long getDetailListCount(long id) {
        return rechargeableOrderMapper.getDetailListCount(id);
    }

    public Map<String, Object> getUser(String userno) {
        return rechargeableOrderMapper.getUser(userno);
    }

    //用户充值
    public void addRechargeablecardByuser(String userno, int payType, int applicableType, BigDecimal amount, String cardCode, String remark, String verificationCode, long createuserid, int operateMode) {
        Map<String, Object> user = rechargeableOrderMapper.getUser(userno);
        if (ModelUtil.getLong(user, "id") == 0) {
            throw new ServiceException("用户不存在");
        }
        long userId = ModelUtil.getLong(user, "id");
        String orderNo = IdGenerator.INSTANCE.nextId();
        //下单
        Map<String, Object> key = rechargeableOrderMapper.insertRecharOrderByUser(userId, payType, applicableType, amount, cardCode, remark, createuserid, operateMode, orderNo);

        if (payType == 5) {
            //更新充值卡
            userWalletService.updateRechargeableCard(ModelUtil.getLong(key, "cardid"));
            //支付成功
            updateOrderStatusSuccess(PayTypeEnum.RechargeableCard, orderNo);
        } else {
            //支付成功
            updateOrderStatusSuccess(PayTypeEnum.Wallet, orderNo);
        }
        //修改余额
        userWalletService.addUserWallet(orderNo, TransactionTypeStateEnum.getValue(applicableType), userId, ModelUtil.getDec(key, "amountmoney", BigDecimal.ZERO));
    }


    public Map<String, Object> getDoctor(String doctorno) {
        return rechargeableOrderMapper.getDoctor(doctorno);
    }

    public List<Map<String, Object>> getDoctorList(String name) {
        return rechargeableOrderMapper.getDoctorList(name);
    }

    //医生充值
    public void addRechargeablecardbyDoctor(List<?> doctorinfo, int applicableType, String remark, long createuserid, int operateMode) {
        for (Object o : doctorinfo) {
//            String doctorno = o.toString();
            Map<String, Object> map = (Map<String, Object>) o;
            String doctorno = ModelUtil.getStr(map, "doctorno");
            BigDecimal amountmoney = ModelUtil.getDec(map, "amount", BigDecimal.ZERO);
            Map<String, Object> data = rechargeableOrderMapper.getDoctor(doctorno);
            if (ModelUtil.getInt(data, "doctorid") == 0) {
                throw new ServiceException("该医生不存在");
            }
            String orderNo = IdGenerator.INSTANCE.nextId();
            long userId = ModelUtil.getLong(data, "doctorid");
            BigDecimal amount = rechargeableOrderMapper.insertRecharOrderByDoctor(userId, orderNo, amountmoney, applicableType, remark, createuserid, operateMode);

            //支付成功
            updateOrderStatusSuccess(PayTypeEnum.Wallet, orderNo);
            long doctorid = ModelUtil.getLong(data, "doctorid");

            //修改医生积分
            doctorWalletService.adminAddDoctorWallet(orderNo, TransactionTypeStateEnum.getValue(applicableType), doctorid, amount);
        }
    }

    //支付成功修改订单状态
    public boolean updateOrderStatusSuccess(PayTypeEnum payTypeEnum, String orderno) {
        return userWalletService.updateOrderStatusSuccess(payTypeEnum, orderno);
    }

    public boolean sendMesg(String phone) {
        boolean flag = true;
        try {
            String code = UnixUtil.getCode();
            //将验证码存入redis
            Map<String, Object> map = new HashMap<>();
            map.put("code", code);
            SendShortMsgUtil.sendSms(com.syhdoctor.webserver.config.ConfigModel.ISONLINE, phone, ConfigModel.SMS.Login_sms_template, map);
            this.redisTemplate.opsForValue().set("recharge" + phone, code);
            this.redisTemplate.expire("recharge" + phone, ConfigModel.SMS.timeout, TimeUnit.SECONDS);
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

    public Map<String, Object> getResult(long id) {
        return rechargeableOrderMapper.getResult(id);
    }

    public int rechargeableOrderPayStatus(long orderId) {
        Map<String, Object> answerOrder = rechargeableOrderMapper.getRechargeableOrder(orderId);
        int status = ModelUtil.getInt(answerOrder, "paystatus");
        return status == PayStateEnum.Paid.getCode() ? 1 : 0;
    }

    public List<Map<String, Object>> doctorRechargeableOrderList(String name, String phone, int applicabletype, long begintime, long endtime, int pageIndex, int pageSize) {
        return rechargeableOrderMapper.doctorRechargeableOrderList(name, phone, applicabletype, begintime, endtime, pageIndex, pageSize);
    }

    public List<Map<String, Object>> doctorRechargeableOrderListExport(String name, String phone, int applicabletype, long begintime, long endtime) {
        List<Map<String, Object>> list = rechargeableOrderMapper.doctorRechargeableOrderListExport(name, phone, applicabletype, begintime, endtime);
        Map<String, Object> map = new HashMap<>();
        map.put("id", "编号");
        map.put("name", "医生姓名");
        map.put("docphone", "手机号");
        map.put("amountmoney", "金额");
        map.put("remark", "备注");
        map.put("applicabletype", "试用类型");
        map.put("rechargeabletime", "充值时间");
        list.add(0, map);
        return list;
    }

    public long doctorRechargeableOrderListCount(String name, String phone, int applicabletype, long begintime, long endtime) {
        return rechargeableOrderMapper.doctorRechargeableOrderListCount(name, phone, applicabletype, begintime, endtime);
    }


}