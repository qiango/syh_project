package com.syhdoctor.webserver.service.wallet;

import com.syhdoctor.common.pay.IPayService;
import com.syhdoctor.common.utils.EnumUtils.*;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.mapper.wallet.UserWalletMapper;
import com.syhdoctor.webserver.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public abstract class UserWalletBaseService extends BaseService {

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Autowired
    private IPayService aliAppPayImpl;

    @Autowired
    private IPayService wechatAppPayImpl;

    @Autowired
    private IPayService aliWebPayImpl;

    @Autowired
    private IPayService wechatWebPayImpl;

    @Autowired
    private UserService userService;


    public class WalletBean {
        private long orderid;//订单id
        private String orderno;//订单编号
        private String cardname;//充值卡名
        private long userid;//用户id
        private String openid;//微信openid
        private int cardstatus;//充值卡状态 1：被使用过,0:正常
        private BigDecimal actualmoney;//订单金额

        public int getCardstatus() {
            return cardstatus;
        }

        public void setCardstatus(int cardstatus) {
            this.cardstatus = cardstatus;
        }

        public String getCardname() {
            return cardname;
        }

        public void setCardname(String cardname) {
            this.cardname = cardname;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getOrderno() {
            return orderno;
        }

        public void setOrderno(String orderno) {
            this.orderno = orderno;
        }

        public long getUserid() {
            return userid;
        }

        public void setUserid(long userid) {
            this.userid = userid;
        }

        public BigDecimal getActualmoney() {
            return actualmoney;
        }

        public void setActualmoney(BigDecimal actualmoney) {
            this.actualmoney = actualmoney;
        }

        public long getOrderid() {
            return orderid;
        }

        public void setOrderid(long orderid) {
            this.orderid = orderid;
        }

    }

    public Map<String, Object> getUserWallet(long userId) {
        return userWalletMapper.getUserWallet(userId);
    }

    public Map<String, Object> getRechargeableCard(String redeemcode) {
        return userWalletMapper.getRechargeableCard(redeemcode);
    }

    public WalletBean addRechargeableOrder(long userId, String redeemcode) {
        Map<String, Object> user = userWalletMapper.getUserWallet(userId);
        if (ModelUtil.getInt(user, "id") == 0) {
            throw new ServiceException("用户不存在");
        }
        WalletBean walletBean = new WalletBean();
        redeemcode = redeemcode.toLowerCase();
        Map<String, Object> card = userWalletMapper.getRechargeableCard(redeemcode);
        if (card == null) {
            walletBean.setCardstatus(2);
        } else if (ModelUtil.getInt(card, "useflag") == 1) {
            walletBean.setCardstatus(1);
            walletBean.setCardname(ModelUtil.getStr(card, "cardname"));
            walletBean.setActualmoney(ModelUtil.getDec(card, "amountmoney", BigDecimal.ZERO));
        } else {
            String orderNo = IdGenerator.INSTANCE.nextId();
            //rc.id, rc.useflag, rcl.lotnumber, effectivetype, begintime, endtime, principal
            long id = ModelUtil.getLong(card, "id");
            long lotid = ModelUtil.getLong(card, "lotid");
            String lotnumber = ModelUtil.getStr(card, "lotnumber");
            int effectivetype = ModelUtil.getInt(card, "effectivetype");
            long begintime = ModelUtil.getLong(card, "begintime");
            long endtime = ModelUtil.getLong(card, "endtime");
            String principal = ModelUtil.getStr(card, "principal");
            BigDecimal actualmoney = ModelUtil.getDec(card, "amountmoney", BigDecimal.ZERO);
            //是否过期
            boolean isOverdue = true;
            if (effectivetype != 1) {
                Map<String, Object> overdue = userWalletMapper.isOverdue(lotid);
                if (overdue == null) {
                    isOverdue = false;
                } else {
                    isOverdue = true;
                }
            }
            if (isOverdue) {
                //下单
                long orderId = userWalletMapper.addOrder(orderNo, id, actualmoney, userId, lotnumber, effectivetype, begintime, endtime, principal);
                walletBean.setOrderid(orderId);
                walletBean.setCardstatus(0);

                //支付成功
                updateOrderStatusSuccess(PayTypeEnum.RechargeableCard, orderNo);

                userWalletMapper.updateRechargeableCard(ModelUtil.getLong(card, "id"));
                userWalletMapper.updateNotusedcount(lotid);
                //修改余额
                addUserWallet(orderNo, TransactionTypeStateEnum.Rechargeable, userId, actualmoney);

            } else {
                walletBean.setCardstatus(3);
            }
        }
        return walletBean;
    }


    //修改充值卡状态
    public boolean updateRechargeableCard(long cardId) {
        return userWalletMapper.updateRechargeableCard(cardId);
    }


    //支付成功修改订单状态
    public boolean updateOrderStatusSuccess(PayTypeEnum payTypeEnum, String orderno) {
        return userWalletMapper.updateOrderStatusSuccess(payTypeEnum, orderno);
    }

    //添加用户余额 添加交易记录
    public boolean addUserWallet(String orderno, TransactionTypeStateEnum transactionTypeStateEnum, long userId, BigDecimal actualmoney) {
        Map<String, Object> userWallet = userWalletMapper.getUserWallet(userId);
        BigDecimal userWalletbalance = ModelUtil.getDec(userWallet, "walletbalance", BigDecimal.ZERO);
        String userAccount = ModelUtil.getStr(userWallet, "phone");
        //修改用户余额
        userWalletMapper.updateUserWallet(userId, userWalletbalance.add(actualmoney));
        //添加用户交易记录
        userWalletMapper.addUserTransactionRecord(orderno, transactionTypeStateEnum, MoneyTypeEnum.Income, userId, userAccount, actualmoney, userWalletbalance.add(actualmoney));
        return true;
    }

    //减少用户余额 用户余额
    public boolean subtractUserWallet(String orderno, TransactionTypeStateEnum transactionTypeStateEnum, long userId, BigDecimal actualmoney) {
        Map<String, Object> userWallet = userWalletMapper.getUserWallet(userId);
        BigDecimal userWalletbalance = ModelUtil.getDec(userWallet, "walletbalance", BigDecimal.ZERO);
        String userAccount = ModelUtil.getStr(userWallet, "phone");
        log.info("userWalletbalance===============" + userWalletbalance);
        log.info("actualmoney===============" + actualmoney);
        if (userWalletbalance.compareTo(actualmoney) < 0) {
            throw new ServiceException("余额不足");
        }
        //修改用户余额
        userWalletMapper.updateUserWallet(userId, userWalletbalance.subtract(actualmoney));
        //添加交易记录
        userWalletMapper.addUserTransactionRecord(orderno, transactionTypeStateEnum, MoneyTypeEnum.Expenditure, userId, userAccount, actualmoney, userWalletbalance.subtract(actualmoney));
        return true;
    }

    public WalletBean addOrder(long userId, BigDecimal actualmoney, int openType) {
        Map<String, Object> open = userService.getOpenId(userId, openType);
        Map<String, Object> user = userService.getUser(userId);

        if (ModelUtil.getInt(user, "id") == 0) {
            throw new ServiceException("用户不存在");
        }
        WalletBean walletBean = new WalletBean();
        String orderNo = IdGenerator.INSTANCE.nextId();
        //下单
        long orderId = userWalletMapper.addOrder(orderNo, actualmoney, userId);
        walletBean.setOrderid(orderId);
        walletBean.setCardstatus(0);
        walletBean.setActualmoney(actualmoney);
        walletBean.setUserid(userId);
        walletBean.setOrderno(orderNo);
        walletBean.setOpenid(ModelUtil.getStr(open, "openid"));
        return walletBean;
    }

    public IPayService.PayBean aliAppPay(String orderNo, BigDecimal actualmoney) {
        // 订单名称，必填
        String subject = "钱包充值";
        // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
        String notify_url = ConfigModel.APILINKURL + "aliCallback/rechargeableAliAppNotifyUrl";
        // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
        String return_url = ConfigModel.APILINKURL + "aliCallback/rechargeableAliAppReturnUrl";
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        return aliAppPayImpl.pay(orderNo, actualmoney, null, subject, null, notify_url, return_url);
    }

    public IPayService.PayBean aliWebPay(String orderNo, BigDecimal actualmoney) {
        // 订单名称，必填
        String subject = "钱包充值";
        // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
        String notify_url = ConfigModel.APILINKURL + "aliCallback/rechargeableAliWebNotifyUrl";
        // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
        String return_url = ConfigModel.APILINKURL + "aliCallback/rechargeableAliWebReturnUrl";
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        return aliWebPayImpl.pay(orderNo, actualmoney, null, subject, null, notify_url, return_url);
    }

    public IPayService.PayBean weChatAppPay(String orderNo, BigDecimal actualmoney, String ip) {
        String notifyUrl = ConfigModel.APILINKURL + "wechatPay/rechargeableWechatAppNotifyUrl";
        // 订单名称，必填
        String body = "钱包充值";
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        return wechatAppPayImpl.pay(orderNo, actualmoney, null, body, ip, notifyUrl, null);
    }

    public IPayService.PayBean weChatWebPay(String orderNo, BigDecimal actualmoney, String ip, String openId) {
        String notifyUrl = ConfigModel.APILINKURL + "wechatPay/rechargeableWechatWebNotifyUrl";
        // 订单名称，必填
        String body = "钱包充值";
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        return wechatWebPayImpl.pay(orderNo, actualmoney, openId, body, ip, notifyUrl, null);
    }

    //订单详细
    public Map<String, Object> getRechargeableOrder(String orderno) {
        return userWalletMapper.getRechargeableOrder(orderno);
    }

    //订单详细
    public Map<String, Object> getRechargeableOrder(long orderId) {
        return userWalletMapper.getRechargeableOrder(orderId);
    }

    public List<Map<String, Object>> transactionRecordList(MoneyTypeEnum moneyTypeEnum, long userId, int pageIndex, int pageSize) {
        return userWalletMapper.transactionRecordList(moneyTypeEnum, userId, pageIndex, pageSize);
    }

    public Map<String, Object> getTransactionRecord(long id) {
        Map<String, Object> transactionRecord = userWalletMapper.getTransactionRecord(id);
        if (transactionRecord != null) {
            int transactiontype = ModelUtil.getInt(transactionRecord, "transactiontype");
            String orderno = ModelUtil.getStr(transactionRecord, "orderno");
            TransactionTypeStateEnum value = TransactionTypeStateEnum.getValue(transactiontype);
            Map<String, Object> doctor = null;
            switch (value) {
                /*case Department:
                    doctor = userWalletMapper.getPhoneOrderDoctor(orderno);
                    break;*/
                case Phone:
                    doctor = userWalletMapper.getPhoneOrderDoctor(orderno);
                    break;
                /*case Outpatient:
                    doctor = userWalletMapper.getAnswerOrderDoctor(orderno);
                    break;*/
                case Graphic:
                    doctor = userWalletMapper.getAnswerOrderDoctor(orderno);
                    break;
                case Video:
                    doctor = userWalletMapper.getVideoOrderDoctor(orderno);
                    break;
                case VIP:
                    break;
                default:
                    break;
            }
            transactionRecord.put("doctor", doctor);
        }
        return transactionRecord;
    }

}
