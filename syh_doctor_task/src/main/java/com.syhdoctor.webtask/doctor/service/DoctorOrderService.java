package com.syhdoctor.webtask.doctor.service;

import com.aliyun.oss.ServiceException;
import com.aliyuncs.exceptions.ClientException;
import com.syhdoctor.common.answer.ProblemFactory;
import com.syhdoctor.common.pay.IPayService;
import com.syhdoctor.common.utils.EnumUtils.*;
import com.syhdoctor.common.utils.*;
import com.syhdoctor.common.utils.alidayu.SendShortMsgUtil;
import com.syhdoctor.common.utils.encryption.MD5Encrypt;
import com.syhdoctor.common.utils.http.HttpParamModel;
import com.syhdoctor.common.utils.http.HttpUtil;
import com.syhdoctor.webtask.base.service.BaseService;
import com.syhdoctor.webtask.config.ConfigModel;
import com.syhdoctor.webtask.doctor.mapper.DoctorOrderMapper;
import com.syhdoctor.webtask.pushapp.mapper.PushAppMapper;
import com.syhdoctor.webtask.system.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.syhdoctor.webtask.config.ConfigModel.QIMO.*;

@Service
public class DoctorOrderService extends BaseService {

    @Autowired
    private DoctorOrderMapper doctorOrderMapper;

    @Autowired
    private IPayService aliAppPayImpl;

    @Autowired
    private IPayService wechatAppPayImpl;

    @Autowired
    private IPayService aliWebPayImpl;

    @Autowired
    private IPayService wechatWebPayImpl;

    @Autowired
    private PushAppMapper pushAppMapper;

    @Autowired
    private SystemService systemService;


    /**
     * 双向呼叫服务
     */
    public void directionalCall() {
        List<Map<String, Object>> orderList = doctorOrderMapper.getPhoneOrderList();
        for (Map<String, Object> map : orderList) {
            int status = ModelUtil.getInt(map, "status");
            if (status == 2) {
                int callnum = ModelUtil.getInt(map, "callnum");
                long id = ModelUtil.getLong(map, "id");
                if (callnum > 2) {
                    throw new ServiceException(id + "");
                }
                String orderno = ModelUtil.getStr(map, "orderno");
                String doctorphone = ModelUtil.getStr(map, "doctorphone");
                String userphone = ModelUtil.getStr(map, "userphone");
                if (!StrUtil.isEmpty(doctorphone, userphone)) {
                    doctorphone = String.format("phoneNum:%s", doctorphone);
                    long time = UnixUtil.getNowTimeStamp();
                    String content = ACCOUNTID + APISECRET + UnixUtil.getDate(time, "yyyyMMddHHmmss");
                    String sig = MD5Encrypt.getInstance().encrypt(content).toUpperCase();
                    Map<String, Object> httpParamModel = new HashMap<>();
                    httpParamModel.put("Action", "Webcall");
                    httpParamModel.put("ServiceNo", SERVICENO);
                    httpParamModel.put("Exten", userphone);
                    httpParamModel.put("WebCallType", "asynchronous");
                    httpParamModel.put("Variable", doctorphone);
                    httpParamModel.put("CallBackUrl", ConfigModel.APILINKURL + "Qimo/webcallCallback");
                    httpParamModel.put("ActionID", orderno);
                    HttpUtil.getInstance().phonePost(String.format(JumpLink.QIMO_WEBCALL, sig), httpParamModel, time);
                    doctorOrderMapper.updatePhoneOrder(id);//修改订单状态为进行中
                }
            }
        }
    }

    /**
     * 双向呼叫服务
     */
    public void getPhoneOrderFile() {
        List<Map<String, Object>> orderList = doctorOrderMapper.getPhoneOrderFile();
        for (Map<String, Object> order : orderList) {
            //录音可以是mp4或pdf格式，分别对应视频和图文，内容举例：互联网医院编码+“/”+就诊时间（yyyyMMdd）+”/”+本次就诊号+“/”+过程文件名称（后缀为mp4或pdf）WEBH012/20170805/10014575598389588/诊疗过程2.mp4
            long id = ModelUtil.getLong(order, "id");
            String key = String.format("WEBH019/%s/%s/voice_%s.mp3", UnixUtil.timeStampDate(UnixUtil.getNowTimeStamp(), "yyyyMMdd"), ModelUtil.getStr(order, "orderno"), id);
            String fileName = FileUtil.setFileName(FileUtil.FILE_PHONE_PATH, key);
            String voice = FileUtil.newFile(ConfigModel.BASEFILEPATH + fileName);
            boolean recordurl = HttpUtil.getInstance().getFile(ModelUtil.getStr(order, "recordurl"), voice);
            if (recordurl) {
                doctorOrderMapper.updatePhoneOrder(id, ModelUtil.setLocalUrl(fileName));
            }
        }
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
            case video:
                transactionTypeStateEnum = TransactionTypeStateEnum.Video;
                break;
            default:
                transactionTypeStateEnum = TransactionTypeStateEnum.Graphic;
                break;
        }
        return transactionTypeStateEnum;
    }

    /**
     * 自动关闭视频订单
     *
     * @param
     */
    public void closeAnswerOrder() {
        List<Map<String, Object>> resultList = doctorOrderMapper.getWaitCloseAnswerOrder();
        for (Map<String, Object> map : resultList) {
            if (map != null) {
                long orderId = ModelUtil.getLong(map, "id");
                int states = ModelUtil.getInt(map, "states");
                long userId = ModelUtil.getLong(map, "userid");
                long doctorId = ModelUtil.getLong(map, "doctorid");
                int paytype = ModelUtil.getInt(map, "paytype");
                BigDecimal originalprice = ModelUtil.getDec(map, "originalprice", BigDecimal.ZERO);
                //更改订单为等待退款
                if (states == 2) {
                    if (paytype == PayTypeEnum.ZERO.getCode() || paytype == PayTypeEnum.VipFree.getCode() || paytype == PayTypeEnum.VipZero.getCode()) {
                        doctorOrderMapper.autoCloseProblemOrder(orderId, AnswerOrderStateEnum.OrderFail.getCode(), "医生没有回复，订单失败");
                    } else {
                        doctorOrderMapper.autoCloseProblemOrder(orderId, AnswerOrderStateEnum.WaitRefund.getCode(), "医生没有回复，等待退款");
                    }
                } else {
                    doctorOrderMapper.autoCloseProblemOrder(orderId, AnswerOrderStateEnum.OrderSuccess.getCode(), "订单交易完成");
                    String orderno = ModelUtil.getStr(map, "orderno");
                    int visitcategory = ModelUtil.getInt(map, "visitcategory");

                    if (paytype != PayTypeEnum.ZERO.getCode()) {

                        //添加医生钱包和交易记录
                        addDoctorWallet(orderno, visitcategory, doctorId, originalprice);
                        Map<String, Object> maps = new HashMap<>();
                        Map<String, Object> ans = doctorOrderMapper.findById(doctorId);
                        maps.put("doctor", String.format("%s医生", ModelUtil.getStr(ans, "doc_name")));
                        maps.put("money", originalprice);
                        try {
                            SendShortMsgUtil.sendSms(ConfigModel.ISONLINE, ModelUtil.getStr(ans, "doo_tel"), com.syhdoctor.common.config.ConfigModel.SMS.extract_success_tempid, maps);
                        } catch (ClientException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //所有问题不能再回答
                doctorOrderMapper.updateOrderTemplateChoiceflag(orderId);
                doctorOrderMapper.updateAnswers(orderId);//更新消息为已答
                long id = doctorOrderMapper.addAnswer(userId, doctorId, orderId, TextFixed.problem_end_tips, 0, QAContentTypeEnum.Tips.getCode(), 2);
                sendSocket(id);
            }
        }
    }

    public void sendSocket(long id) {
        Map<String, Object> doctorAnswer = doctorOrderMapper.getDoctorAnswer(id);
        String userno = ModelUtil.getStr(doctorAnswer, "userno");
        String doctorno = ModelUtil.getStr(doctorAnswer, "doctorno");
        long orderId = ModelUtil.getLong(doctorAnswer, "orderid");
        List<Map<String, Object>> userAppendList = getAppendUserSocketAnswerList(orderId, id);
        String userContentJson = String.format("%s|%s%s", JsonUtil.getInstance().toJson(userAppendList), orderId, userno);
        List<Map<String, Object>> doctorAppendList = getAppendDoctorSocketAnswerList(orderId, id);
        String doctorContentJson = String.format("%s|%s%s", JsonUtil.getInstance().toJson(doctorAppendList), orderId, doctorno);
        HttpUtil instance = HttpUtil.getInstance();
        HttpParamModel httpParamModel = new HttpParamModel();
        httpParamModel.add("json", userContentJson);
        HttpParamModel httpParamModelDoctor = new HttpParamModel();
        httpParamModelDoctor.add("json", doctorContentJson);
        try {
            if (userAppendList.size() > 0) {
                instance.post(ConfigModel.WEBSOCKETLINKURL + "websocket/answerPushData", httpParamModel);
            }

            if (doctorAppendList.size() > 0) {
                instance.post(ConfigModel.WEBSOCKETLINKURL + "websocket/answerPushData", httpParamModelDoctor);
            }
            log.info("socket调用成功----------------");
        } catch (Exception e) {
            log.info("socket调用失败---------------");
        }
    }

    /**
     * 用户订单详情没有历史记录
     *
     * @param orderid
     * @return
     */
    public List<Map<String, Object>> getAppendUserSocketAnswerList(long orderid, long id) {
        List<Map<String, Object>> orderState = doctorOrderMapper.getOrderState(orderid);
        List<Map<String, Object>> answerList = doctorOrderMapper.getAppendUserSocketAnswerList(orderid, id);
        ProblemFactory.getProblemImpl(QAContentTypeEnum.Tips).getContent(answerList, orderState);
        return answerList;
    }

    /**
     * 用户追加的消息
     *
     * @param orderid
     * @return
     */
    public List<Map<String, Object>> getAppendDoctorSocketAnswerList(long orderid, long id) {
        List<Map<String, Object>> orderState = doctorOrderMapper.getOrderState(orderid);
        //问答列表
        List<Map<String, Object>> answerList = doctorOrderMapper.getAppendDoctorSocketAnswerList(orderid, id);
        ProblemFactory.getProblemImpl(QAContentTypeEnum.Tips).getContent(answerList, orderState);
        return answerList;
    }

    /**
     * 自动关闭问诊订单
     *
     * @param
     */
    public void closeVideoOrder() {
        List<Map<String, Object>> resultList = doctorOrderMapper.getWaitCloseVideoOrder();
        for (Map<String, Object> map : resultList) {
            if (map != null) {
                long orderId = ModelUtil.getLong(map, "id");
                int status = ModelUtil.getInt(map, "status");
                long doctorId = ModelUtil.getLong(map, "doctorid");
                int paytype = ModelUtil.getInt(map, "paytype");
                int userinto = ModelUtil.getInt(map, "userinto");
                int doctorinto = ModelUtil.getInt(map, "doctorinto");
                BigDecimal originalprice = ModelUtil.getDec(map, "originalprice", BigDecimal.ZERO);

                //更改订单为等待退款
                if (status == 3 && userinto == 1 && doctorinto == 1) {
                    doctorOrderMapper.autoCloseVideoOrder(orderId, AnswerOrderStateEnum.OrderSuccess.getCode(), "订单交易完成");
                    String orderno = ModelUtil.getStr(map, "orderno");
                    int visitcategory = ModelUtil.getInt(map, "visitcategory");
                    if (paytype != PayTypeEnum.ZERO.getCode()) {
                        //添加医生钱包和交易记录
                        addDoctorWallet(orderno, visitcategory, doctorId, originalprice);
                        Map<String, Object> maps = new HashMap<>();
                        Map<String, Object> ans = doctorOrderMapper.findById(doctorId);
                        maps.put("doctor", String.format("%s医生", ModelUtil.getStr(ans, "doc_name")));
                        maps.put("money", originalprice);
                        try {
                            SendShortMsgUtil.sendSms(ConfigModel.ISONLINE, ModelUtil.getStr(ans, "doo_tel"), com.syhdoctor.common.config.ConfigModel.SMS.extract_success_tempid, maps);
                        } catch (ClientException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (paytype == PayTypeEnum.ZERO.getCode() || paytype == PayTypeEnum.VipFree.getCode() || paytype == PayTypeEnum.VipZero.getCode()) {
                        doctorOrderMapper.autoCloseVideoOrder(orderId, VideoOrderStateEnum.OrderFail.getCode(), "医生没有回复，订单失败");
                    } else {
                        doctorOrderMapper.autoCloseVideoOrder(orderId, VideoOrderStateEnum.WaitRefund.getCode(), "医生没有回复，等待退款");
                    }

                }
            }
        }
    }


    /**
     * 问诊退款
     *
     * @param
     */
    @Transactional
    public void answerRefundOrder() {
        List<Map<String, Object>> resultList = doctorOrderMapper.getAnswerRefundOrderList();
        if (resultList.size() > 0) {
            for (Map<String, Object> map : resultList) {
                if (map != null) {
                    int payType = ModelUtil.getInt(map, "paytype");
                    BigDecimal actualMoney = ModelUtil.getDec(map, "actualmoney", BigDecimal.ZERO);//退款金额
                    long id = ModelUtil.getLong(map, "id");//退款金额
                    //不是零元支付/会员免费次数/会员永久免费
                    if (payType != PayTypeEnum.ZERO.getCode() && payType != PayTypeEnum.VipFree.getCode() && payType != PayTypeEnum.VipZero.getCode()) {
                        String orderNo = ModelUtil.getStr(map, "orderno");//订单号
                        long userid = ModelUtil.getLong(map, "userid");
                        int visitcategory = ModelUtil.getInt(map, "visitcategory");
                        IPayService.ReturnBean refund = refundOrder(userid, payType, visitcategory, orderNo, actualMoney, "");

                        //更改订单为关闭
                        if (refund.isVerify()) {
                            doctorOrderMapper.autoCloseProblemOrder(id, AnswerOrderStateEnum.OrderFail.getCode(), "退款成功,订单更改为关闭,不做消息提醒");
                        } else {
                            log.info("refund fail orderno" + orderNo);
                            //退款失败邮件提醒
                        }
                    }
                }
            }
        }
    }

    public void aa() {
        wechatAppPayImpl.refund("201811191711570618192", new BigDecimal(9), "");
    }

    /**
     * 急诊退款
     *
     * @param
     */
    @Transactional
    public void phoneRefundOrder() {
        List<Map<String, Object>> resultList = doctorOrderMapper.getPhoneRefundOrderList();
        if (resultList.size() > 0) {
            for (Map<String, Object> map : resultList) {
                if (map != null) {
                    int payType = ModelUtil.getInt(map, "paytype");
                    BigDecimal actualMoney = ModelUtil.getDec(map, "actualmoney", BigDecimal.ZERO);//退款金额
                    long id = ModelUtil.getLong(map, "id");//退款金额
                    //不是零元支付
                    if (payType != PayTypeEnum.ZERO.getCode() && payType != PayTypeEnum.VipFree.getCode() && payType != PayTypeEnum.VipZero.getCode()) {
                        String orderNo = ModelUtil.getStr(map, "orderno");//订单号
                        long userid = ModelUtil.getLong(map, "userid");
                        int visitcategory = ModelUtil.getInt(map, "visitcategory");
                        IPayService.ReturnBean refund = refundOrder(userid, payType, visitcategory, orderNo, actualMoney, "");
                        //更改订单为关闭
                        if (refund.isVerify()) {
                            //TODO 推送退款消息
                            doctorOrderMapper.autoClosePhoneOrder(id, PhoneOrderStateEnum.OrderFail.getCode(), "退款成功,订单更改为关闭,不做消息提醒");

                        } else {
                            log.info("refund fail orderno" + orderNo);
                            //退款失败邮件提醒
                        }
                    }
                }
            }
        }
    }

    public void videoOrderRefund() {
        List<Map<String, Object>> refundOrderList = doctorOrderMapper.getVideoRefundOrderList();
        if (refundOrderList.size() > 0) {
            for (Map<String, Object> map : refundOrderList) {
                if (map != null) {
                    int payType = ModelUtil.getInt(map, "paytype");
                    BigDecimal actualMoney = ModelUtil.getDec(map, "actualmoney", BigDecimal.ZERO);//退款金额
                    long id = ModelUtil.getLong(map, "id");//退款金额
                    //不是零元支付
                    if (payType != PayTypeEnum.ZERO.getCode() && payType != PayTypeEnum.VipFree.getCode() && payType != PayTypeEnum.VipZero.getCode()) {
                        String orderNo = ModelUtil.getStr(map, "orderno");//订单号
                        long userid = ModelUtil.getLong(map, "userid");
                        int visitcategory = ModelUtil.getInt(map, "visitcategory");
                        IPayService.ReturnBean refund = refundOrder(userid, payType, visitcategory, orderNo, actualMoney, "");
                        //更改订单为关闭
                        if (refund.isVerify()) {
                            doctorOrderMapper.autoCloseVideoOrder(id, VideoOrderStateEnum.OrderFail.getCode(), "退款成功,订单更改为关闭,不做消息提醒");

                        } else {
                            log.info("refund fail orderno" + orderNo);
                            //退款失败邮件提醒
                        }
                    }
                }
            }
        }
    }

    private IPayService.ReturnBean refundOrder(long userId, int payType, int visitcategory, String orderNo, BigDecimal actualMoney, String remark) {
        IPayService.ReturnBean refund = new IPayService.ReturnBean();
        if (payType == PayTypeEnum.AliApp.getCode()) {
            //支付宝退款
            refund = aliAppPayImpl.refund(orderNo, actualMoney, remark);
        } else if (payType == PayTypeEnum.AliWeb.getCode()) {
            //微信退款
            refund = aliWebPayImpl.refund(orderNo, actualMoney, remark);
        } else if (payType == PayTypeEnum.WxApp.getCode()) {
            //微信退款
            refund = wechatAppPayImpl.refund(orderNo, actualMoney, remark);
        } else if (payType == PayTypeEnum.WxWeb.getCode()) {
            //微信退款
            refund = wechatWebPayImpl.refund(orderNo, actualMoney, remark);
        } else if (payType == PayTypeEnum.Wallet.getCode()) {
            //钱包退款
            TransactionTypeStateEnum transactionTypeStateEnum = visitToTransaction(visitcategory);
            addUserWallet(orderNo, transactionTypeStateEnum, userId, actualMoney);
            refund.setVerify(true);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("money", actualMoney);
        String value = getPayType(payType);
        map.put("paytype", value);
        //如果免费次数要退，短信模板要改，因为价格为0
        try {
            SendShortMsgUtil.sendSms(ConfigModel.ISONLINE, ModelUtil.getStr(doctorOrderMapper.findByUserId(userId), "phone"), com.syhdoctor.common.config.ConfigModel.SMS.refund_tempid, map);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return refund;
    }

    private static String getPayType(int payType) {
        String value = "";
        switch (PayTypeEnum.getValue(payType)) {
            case WxApp:
                value = "微信";
                break;
            case AliApp:
                value = "支付宝";
                break;
            case AliWeb:
                value = "支付宝";
                break;
            case WxWeb:
                value = "微信";
                break;
            case Wallet:
                value = "钱包";
                break;
            case UnionPay:
                value = "银联";
                break;
            default:
                value = "钱包";
                break;
        }
        return value;
    }


    //添加用户余额 添加交易记录
    public boolean addUserWallet(String orderno, TransactionTypeStateEnum transactionTypeStateEnum, long userId, BigDecimal actualmoney) {
        Map<String, Object> userWallet = doctorOrderMapper.getUserWallet(userId);
        BigDecimal userWalletbalance = ModelUtil.getDec(userWallet, "walletbalance", BigDecimal.ZERO);
        String userAccount = ModelUtil.getStr(userWallet, "phone");
        //修改用户余额
        doctorOrderMapper.updateUserWallet(userId, userWalletbalance.add(actualmoney));
        //添加用户交易记录
        doctorOrderMapper.addUserTransactionRecord(orderno, transactionTypeStateEnum, MoneyTypeEnum.Income, userId, userAccount, actualmoney, 1, userWalletbalance.add(actualmoney));
        return true;
    }

    //添加医生余额 添加交易记录
    public boolean addDoctorWallet(String orderno, int visitcategory, long doctorId, BigDecimal actualmoney) {

        Map<String, Object> doctorWallet = doctorOrderMapper.getDoctorWallet(doctorId);
        BigDecimal doctorWalletbalance = ModelUtil.getDec(doctorWallet, "walletbalance", BigDecimal.ZERO);
        String doctorAccount = ModelUtil.getStr(doctorWallet, "phone");
        if (visitcategory == VisitCategoryEnum.graphic.getCode() || visitcategory == VisitCategoryEnum.phone.getCode()) {
            //修改医生余额
            doctorOrderMapper.updateDoctorWallet(doctorId, doctorWalletbalance.add(actualmoney));
            TransactionTypeStateEnum transactionTypeStateEnum = visitToTransaction(visitcategory);
            //添加医生交易记录
            doctorOrderMapper.addDoctorTransactionRecord(orderno, transactionTypeStateEnum, MoneyTypeEnum.Income, doctorId, doctorAccount, actualmoney, null, doctorWalletbalance.add(actualmoney));
        }
        return true;
    }

    //添加医生余额 添加交易记录
    public boolean addDoctorWallet(String orderno, TransactionTypeStateEnum transactionTypeStateEnum, long doctorId, BigDecimal actualmoney, String cardnumber) {

        Map<String, Object> doctorWallet = doctorOrderMapper.getDoctorWallet(doctorId);
        BigDecimal doctorWalletbalance = ModelUtil.getDec(doctorWallet, "walletbalance", BigDecimal.ZERO);
        String doctorAccount = ModelUtil.getStr(doctorWallet, "phone");
        //修改医生余额
        doctorOrderMapper.updateDoctorWallet(doctorId, doctorWalletbalance.add(actualmoney));
        //添加医生交易记录
        doctorOrderMapper.addDoctorTransactionRecord(orderno, transactionTypeStateEnum, MoneyTypeEnum.Income, doctorId, doctorAccount, actualmoney, cardnumber, doctorWalletbalance.add(actualmoney));
        return true;
    }


    public void addPushOrderOneHour() {
        List<Map<String, Object>> list = doctorOrderMapper.addPushOrderOneHour();

        for (Map<String, Object> map : list) {
            long orderId = ModelUtil.getInt(map, "id");
            int userId = ModelUtil.getInt(map, "userid");
            int dplatform = ModelUtil.getInt(map, "dplatform");
            String dToken = ModelUtil.getStr(map, "dtoken");
            pushAppMapper.addPushApp(TextFixed.messageServiceTitle,
                    TextFixed.answerUserSevenDays,
                    TypeNameAppPushEnum.doNot.getCode(), String.valueOf(orderId),
                    userId, MessageTypeEnum.user.getCode(), dplatform, dToken); //app医生push消息
        }
    }


    public void extractOrderRefund() {
        List<Map<String, Object>> refundOrderList = doctorOrderMapper.getExtractRefundOrderList();
        for (Map<String, Object> map : refundOrderList) {
            addDoctorWallet(ModelUtil.getStr(map, "orderno"), TransactionTypeStateEnum.ExtractRefund, ModelUtil.getLong(map, "doctorid"), ModelUtil.getDec(map, "amountmoney", BigDecimal.ZERO), ModelUtil.getStr(map, "cardnumber"));
            doctorOrderMapper.updateExpertOrderRefund(ModelUtil.getLong(map, "id"));
        }
    }

    /**
     * 通话三十分钟还未结束订单
     *
     * @return
     */
    public void phoneCloseOrderList() {
        List<Map<String, Object>> list = doctorOrderMapper.phoneCloseOrderList();
        for (Map<String, Object> map : list) {
            //成功
            long orderId = ModelUtil.getLong(map, "id");
            String orderno = ModelUtil.getStr(map, "orderno");
            int visitcategory = ModelUtil.getInt(map, "visitcategory");
            int userId = ModelUtil.getInt(map, "userid");
            int doctorId = ModelUtil.getInt(map, "doctorid");
            long createTime = ModelUtil.getLong(map, "createtime");
            int paytype = ModelUtil.getInt(map, "paytype");
            int uplatform = ModelUtil.getInt(map, "uplatform");
            int dplatform = ModelUtil.getInt(map, "dplatform");
            String uToken = ModelUtil.getStr(map, "utoken");
            String dToken = ModelUtil.getStr(map, "dtoken");
            BigDecimal actualmoney = PriceUtil.findPrice(ModelUtil.getLong(map, "actualmoney"));

            //用户端消息开始
            pushAppMapper.addPushApp(TextFixed.messageServiceTitle,
                    TextFixed.phoneUserOrderSuccessPushText,
                    TypeNameAppPushEnum.departmentCallSuccessUserOrder.getCode(), String.valueOf(orderId),
                    userId, MessageTypeEnum.user.getCode(), uplatform, uToken); //app 用户 push 消息
            systemService.addMessage("", TextFixed.messageServiceTitle,
                    TypeNameAppPushEnum.departmentCallSuccessUserOrder.getCode(), String.valueOf(orderId),
                    MessageTypeEnum.user.getCode(), userId,
                    TextFixed.phoneUserOrderSuccessPushSystemText,
                    "");//app 用户内推送
            //用户端消息结束

            //医生端消息开始
            pushAppMapper.addPushApp(TextFixed.messageServiceTitle,
                    String.format(TextFixed.phoneDoctorOrderSuccessPushText, UnixUtil.getDate(createTime, "yyyy-MM-dd HH:mm:ss")),
                    TypeNameAppPushEnum.departmentCallSuccessDoctorOrder.getCode(), String.valueOf(orderId), doctorId,
                    MessageTypeEnum.doctor.getCode(), dplatform, dToken); //app 医生 push 消息
            systemService.addMessage("", TextFixed.messageServiceTitle,
                    TypeNameAppPushEnum.departmentCallSuccessDoctorOrder.getCode(), String.valueOf(orderId),
                    MessageTypeEnum.doctor.getCode(), doctorId,
                    String.format(TextFixed.phoneDoctorOrderSuccessPushText, UnixUtil.getDate(createTime, "yyyy-MM-dd HH:mm:ss")),
                    "");//app 医生 内推送
            //医生端消息结束


            //添加医生钱包和交易记录
            if (paytype != PayTypeEnum.ZERO.getCode()) {
                addDoctorWallet(orderno, visitcategory, doctorId, actualmoney);
            }

            //订单完成
            doctorOrderMapper.updatePhoneOrder(orderId, PhoneOrderStateEnum.OrderSuccess.getCode());

        }
    }

}
