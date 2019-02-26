package com.syhdoctor.webserver.service.wallet;

import com.syhdoctor.common.config.ConfigModel;
import com.syhdoctor.common.utils.EnumUtils.*;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.TextFixed;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.common.utils.alidayu.SendShortMsgUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.mapper.wallet.DoctorWalletMapper;
import com.syhdoctor.webserver.service.system.SystemService;
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
@Transactional
public abstract class DoctorWalletBaseService extends BaseService {

    @Autowired
    private DoctorWalletMapper doctorWalletMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SystemService systemService;


    public Map<String, Object> getDoctorWallet(long doctorId) {
        return doctorWalletMapper.getDoctorWallet(doctorId);
    }

    public Map<String, Object> getDoctorWalletHomepage(long doctorId) {
        Map<String, Object> doctorWallet = doctorWalletMapper.getDoctorWallet(doctorId);
        if (doctorWallet != null) {
            //是否绑定银行卡
            long count = doctorWalletMapper.getBankCardCount(doctorId);
            doctorWallet.put("bankflag", count > 0 ? 1 : 0);
            //本月电话订单数量
            long phoneCount = doctorWalletMapper.getPhoneOrderCount(doctorId, VisitCategoryEnum.phone);
            //本月急诊订单数量
            long departmentCount = doctorWalletMapper.getPhoneOrderCount(doctorId, VisitCategoryEnum.department);
            //本月图文订单数量
            long graphicCount = doctorWalletMapper.getAnswerOrderCount(doctorId, VisitCategoryEnum.graphic);
            //本月问诊订单数量
            long outpatientCount = doctorWalletMapper.getAnswerOrderCount(doctorId, VisitCategoryEnum.Outpatient);
            //本月视频订单数量
            long videoCount = 0;//todo

            doctorWallet.put("phoneCount", phoneCount);
            doctorWallet.put("departmentCount", departmentCount);
            doctorWallet.put("graphicCount", graphicCount);
            doctorWallet.put("outpatientCount", outpatientCount);
            doctorWallet.put("videoCount", videoCount);
        }
        return doctorWallet;
    }

    private TransactionTypeStateEnum visitToTransaction(int visitType) {
        VisitCategoryEnum value = VisitCategoryEnum.getValue(visitType);
        TransactionTypeStateEnum transactionTypeStateEnum = null;
        switch (value) {
            case graphic:
                transactionTypeStateEnum = TransactionTypeStateEnum.Graphic;
                break;
            /*case Outpatient:
                transactionTypeStateEnum = TransactionTypeStateEnum.Outpatient;
                break;*/
            case phone:
                transactionTypeStateEnum = TransactionTypeStateEnum.Phone;
                break;
           /* case department:
                transactionTypeStateEnum = TransactionTypeStateEnum.Department;
                break;*/
            default:
                transactionTypeStateEnum = TransactionTypeStateEnum.Graphic;
                break;
        }
        return transactionTypeStateEnum;
    }

    //添加医生余额 添加交易记录
    public boolean addDoctorWallet(String orderno, int visitcategory, long doctorId, BigDecimal actualmoney) {
        Map<String, Object> doctorWallet = doctorWalletMapper.getDoctorWallet(doctorId);
        BigDecimal doctorWalletbalance = ModelUtil.getDec(doctorWallet, "walletbalance", BigDecimal.ZERO);
        String doctorAccount = ModelUtil.getStr(doctorWallet, "phone");
        if (visitcategory == VisitCategoryEnum.phone.getCode() || visitcategory == VisitCategoryEnum.graphic.getCode()) {
            //修改用户余额
            doctorWalletMapper.updateDoctorWallet(doctorId, doctorWalletbalance.add(actualmoney));
            TransactionTypeStateEnum transactionTypeStateEnum = visitToTransaction(visitcategory);
            //添加用户交易记录
            doctorWalletMapper.addDoctorTransactionRecord(orderno, transactionTypeStateEnum, MoneyTypeEnum.Income, doctorId, doctorAccount, actualmoney, null, doctorWalletbalance.add(actualmoney));
        }
        return true;
    }

    //添加医生余额 添加交易记录
    public boolean adminAddDoctorWallet(String orderno, TransactionTypeStateEnum transactionTypeStateEnum, long doctorId, BigDecimal actualmoney) {
        Map<String, Object> doctorWallet = doctorWalletMapper.getDoctorWallet(doctorId);
        BigDecimal doctorWalletbalance = ModelUtil.getDec(doctorWallet, "walletbalance", BigDecimal.ZERO);
        String doctorAccount = ModelUtil.getStr(doctorWallet, "phone");
        //修改用户余额
        doctorWalletMapper.updateDoctorWallet(doctorId, doctorWalletbalance.add(actualmoney));
        //添加用户交易记录
        doctorWalletMapper.addDoctorTransactionRecord(orderno, transactionTypeStateEnum, MoneyTypeEnum.Income, doctorId, doctorAccount, actualmoney, null, doctorWalletbalance.add(actualmoney));
        return true;
    }

    //减少医生余额 添加交易记录
    public boolean subtractDoctorWallet(String orderno, TransactionTypeStateEnum transactionTypeStateEnum, long doctorId, BigDecimal actualmoney) {
        Map<String, Object> doctorWallet = doctorWalletMapper.getDoctorWallet(doctorId);
        BigDecimal doctorWalletbalance = ModelUtil.getDec(doctorWallet, "walletbalance", BigDecimal.ZERO);
        String doctorAccount = ModelUtil.getStr(doctorWallet, "phone");
        if (doctorWalletbalance.compareTo(actualmoney) < 0) {
            throw new ServiceException("积分不足");
        }
        Map<String, Object> extractOrder = doctorWalletMapper.getExtractOrder(orderno);
        //卡号
        String cardnumber = ModelUtil.getStr(extractOrder, "cardnumber");
        //修改医生余额
        doctorWalletMapper.updateDoctorWallet(doctorId, doctorWalletbalance.subtract(actualmoney));
        //添加医生交易记录
        doctorWalletMapper.addDoctorTransactionRecord(orderno, transactionTypeStateEnum, MoneyTypeEnum.Expenditure, doctorId, doctorAccount, actualmoney, cardnumber, doctorWalletbalance.subtract(actualmoney));
        return true;
    }

    public List<Map<String, Object>> transactionRecordList(long doctorId, int pageIndex, int pageSize) {
        return doctorWalletMapper.transactionRecordList(doctorId, pageIndex, pageSize);
    }

    public Map<String, Object> getTransactionRecord(long id) {
        Map<String, Object> transactionRecord = doctorWalletMapper.getTransactionRecord(id);
        if (transactionRecord != null) {
            int moneyflag = ModelUtil.getInt(transactionRecord, "moneyflag");
            int transactiontype = ModelUtil.getInt(transactionRecord, "transactiontype");
            String orderno = ModelUtil.getStr(transactionRecord, "orderno");
            TransactionTypeStateEnum value = TransactionTypeStateEnum.getValue(transactiontype);
            Map<String, Object> user = null;
            if (moneyflag == MoneyTypeEnum.Income.getCode()) {
                switch (value) {
                    /*case Department:
                        user = doctorWalletMapper.getPhoneOrderUser(orderno);
                        if (user != null) {
                            user.put("diseaselist", doctorWalletMapper.getPhoneDiseaseList(ModelUtil.getLong(user, "id")));
                        }
                        break;*/
                    case Phone:  //电话
                        user = doctorWalletMapper.getPhoneOrderUser(orderno);
                        if (user != null) {
                            user.put("diseaselist", doctorWalletMapper.getPhoneDiseaseList(ModelUtil.getLong(user, "orderid")));
                        }
                        break;
                    /*case Outpatient:
                        user = doctorWalletMapper.getAnswerOrderUser(orderno);
                        if (user != null) {
                            user.put("diseaselist", doctorWalletMapper.getAnswerDiseaseList(ModelUtil.getLong(user, "id")));
                        }
                        break;*/
                    case Graphic: //图文咨询
                        user = doctorWalletMapper.getAnswerOrderUser(orderno);
                        if (user != null) {
                            user.put("diseaselist", doctorWalletMapper.getAnswerDiseaseList(ModelUtil.getLong(user, "orderid")));
                        }
                        break;
                    case Video:
                        break;
                    case VIP:
                        break;
                    default:
                        break;
                }
            }
            transactionRecord.put("user", user);
        }
        return transactionRecord;
    }

    /**
     * 申请提现
     *
     * @param doctorId
     * @return
     */
    public Map<String, Object> applyExtract(long doctorId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> doctorWallet = doctorWalletMapper.getDoctorWallet(doctorId);
        BigDecimal walletbalance = ModelUtil.getDec(doctorWallet, "walletbalance", BigDecimal.ZERO);
        Map<String, Object> bankCard = doctorWalletMapper.getBankCardByDoctorId(doctorId);
        result.put("bankCard", bankCard);
        result.put("servicemoney", 0);
        result.put("phone", ModelUtil.getStr(doctorWallet, "phone"));
        result.put("walletbalance", walletbalance);
        Map<String, Object> map = systemService.getExtractPage();
        result.put("minwalletbalance", ModelUtil.getDec(map, "money", BigDecimal.valueOf(0)));
        result.put("tips", ModelUtil.getStr(map, "text"));
        return result;
    }

    /**
     * 发送验证码
     *
     * @param doctorid
     * @return
     */
    public boolean sendExtractCode(long doctorid) {
        Map<String, Object> doctorWallet = doctorWalletMapper.getDoctorWallet(doctorid);
        String phone = ModelUtil.getStr(doctorWallet, "phone");
        boolean flag = true;
        try {
            String code = UnixUtil.getCode();
            Map<String, Object> map = new HashMap<>();
            map.put("code", code);
            //将验证码存入redis
            SendShortMsgUtil.sendSms(com.syhdoctor.webserver.config.ConfigModel.ISONLINE, phone, ConfigModel.SMS.putforward_sms_template, map);
            this.redisTemplate.opsForValue().set("Extract" + phone, code);
            this.redisTemplate.expire("Extract" + phone, ConfigModel.SMS.timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            flag = false;
            e.getStackTrace();
        }
        return flag;
    }

    /**
     * 确认体现
     *
     * @param doctorid
     * @param cardid
     * @param amountmoney
     * @return
     */
    public Map<String, Object> confirmExtract(long doctorid, long cardid, BigDecimal amountmoney, String code) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> doctorWallet = getDoctorWallet(doctorid);
        boolean flag = false;
        if (TextFixed.def_code.equals(code)) {
            flag = true;
        }
        if (!flag) {
            Object codeObject = redisTemplate.opsForValue().get("Extract" + ModelUtil.getStr(doctorWallet, "phone"));
            if (codeObject == null) {
                result.put("status", 2);
            } else if (!code.equals(codeObject.toString())) {
                result.put("status", 3);
            } else {
                flag = true;
            }
        }
        Map<String, Object> homePage = systemService.getHomePage(BasicsTypeEnum.ExtractMoney.getCode());
        if (amountmoney.compareTo(PriceUtil.findPrice(ModelUtil.getLong(homePage, "tips"))) < 0) {
            result.put("status", 4);
            flag = false;
        }

        if (flag) {
            Map<String, Object> bankCard = doctorWalletMapper.getBankCardById(cardid);
            String number = ModelUtil.getStr(bankCard, "number");
            String phone = ModelUtil.getStr(doctorWallet, "phone");
            String bankname = ModelUtil.getStr(bankCard, "bankname");
            String bankcode = ModelUtil.getStr(bankCard, "bankcode");
            String orderno = IdGenerator.INSTANCE.nextId();
            long orderId = doctorWalletMapper.addExtractOrder(orderno, cardid, number, bankname, bankcode, amountmoney, doctorid, phone);

            //修改钱包
            subtractDoctorWallet(orderno, TransactionTypeStateEnum.Extract, doctorid, amountmoney);
            result.put("orderid", orderId);
            result.put("status", 1);
        }
        return result;
    }


    public Map<String, Object> getExtractOrder(long orderId, long id) {
        Map<String, Object> extractOrder = new HashMap<>();
        if (orderId == 0) {
            Map<String, Object> transactionRecord = doctorWalletMapper.getTransactionRecord(id);
            extractOrder = doctorWalletMapper.getExtractOrder(ModelUtil.getStr(transactionRecord, "orderno"));
        } else {
            extractOrder = doctorWalletMapper.getExtractOrder(orderId);
        }
        extractOrder.put("reason", String.format("(失败原因:%s)", ModelUtil.getStr(extractOrder, "reason")));
        return extractOrder;
    }

    public Map<String, Object> findTransactionRecordList(long doctorId, MoneyTypeEnum value, long begintime, long endtime, int pageindex, int pagesize) {
        if (endtime < begintime) {
            throw new ServiceException("开始时间不能大于结束时间");
        }
        if (begintime == 0) {
            begintime = UnixUtil.getMonthFirstDate(0);
        }

        if (endtime == 0) {
            endtime = UnixUtil.getNowTimeStamp();
        }
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        //本月电话订单数量
        long phoneCount = doctorWalletMapper.getPhoneOrderCount(doctorId, VisitCategoryEnum.phone, begintime, endtime);
        //本月急诊订单数量
        long departmentCount = doctorWalletMapper.getPhoneOrderCount(doctorId, VisitCategoryEnum.department);
        //本月图文订单数量
        long graphicCount = doctorWalletMapper.getAnswerOrderCount(doctorId, VisitCategoryEnum.graphic, begintime, endtime);
        //本月问诊订单数量
        long outpatientCount = doctorWalletMapper.getAnswerOrderCount(doctorId, VisitCategoryEnum.Outpatient);
        //视频订单数量
        long videoCount = doctorWalletMapper.getVideoOrderCount(doctorId, VisitCategoryEnum.video, begintime, endtime);
        if (MoneyTypeEnum.Income.equals(value)) {
            result.put("phoneCount", phoneCount);
            result.put("graphicCount", graphicCount);
            result.put("videoCount", videoCount);
            result.put("income", doctorWalletMapper.getTransactionRecordSum(doctorId, MoneyTypeEnum.Income, begintime, endtime));
        } else if (MoneyTypeEnum.Expenditure.equals(value)) {
            result.put("expenditure", doctorWalletMapper.getTransactionRecordSum(doctorId, MoneyTypeEnum.Expenditure, begintime, endtime));
        } else {
            result.put("phoneCount", phoneCount);
            result.put("graphicCount", graphicCount);
            result.put("videoCount", videoCount);
            result.put("income", doctorWalletMapper.getTransactionRecordSum(doctorId, MoneyTypeEnum.Income, begintime, endtime));
            result.put("expenditure", doctorWalletMapper.getTransactionRecordSum(doctorId, MoneyTypeEnum.Expenditure, begintime, endtime));
        }
        result.put("begintime", begintime);
        result.put("endtime", endtime);
        map.put("statistics", result);
        map.put("recordlist", doctorWalletMapper.transactionRecordList(doctorId, value, begintime, endtime, pageindex, pagesize));
        return map;
    }
}
