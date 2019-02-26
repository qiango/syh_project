package com.syhdoctor.webserver.service.answer;

import com.aliyuncs.exceptions.ClientException;
import com.syhdoctor.common.answer.ProblemFactory;
import com.syhdoctor.common.pay.IPayService;
import com.syhdoctor.common.utils.EnumUtils.*;
import com.syhdoctor.common.utils.*;
import com.syhdoctor.common.utils.alidayu.SendShortMsgUtil;
import com.syhdoctor.common.utils.http.HttpParamModel;
import com.syhdoctor.common.utils.http.HttpUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.controller.webapp.appapi.user.answer.util.DoctorPrice;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.mapper.answer.AnswerMapper;
import com.syhdoctor.webserver.mapper.video.DoctorVideoMapper;
import com.syhdoctor.webserver.service.doctor.DoctorService;
import com.syhdoctor.webserver.service.prescription.PrescriptionService;
import com.syhdoctor.webserver.service.system.SystemService;
import com.syhdoctor.webserver.service.user.UserService;
import com.syhdoctor.webserver.service.video.DoctorVideoService;
import com.syhdoctor.webserver.service.video.UserVideoService;
import com.syhdoctor.webserver.service.vipcard.VipCardService;
import com.syhdoctor.webserver.service.wallet.DoctorWalletService;
import com.syhdoctor.webserver.service.wallet.UserWalletService;
import com.syhdoctor.webserver.utils.QiniuUtils;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public abstract class AnswerBaseService extends BaseService {

    @Autowired
    private DoctorPrice doctorPrice;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private DoctorVideoMapper doctorVideoMapper;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private UserService userService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private IPayService aliAppPayImpl;

    @Autowired
    private IPayService wechatAppPayImpl;

    @Autowired
    private IPayService aliWebPayImpl;

    @Autowired
    private IPayService wechatWebPayImpl;

    @Autowired
    private SystemService systemService;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private DoctorWalletService doctorWalletService;

    @Autowired
    private VipCardService cardService;

    @Autowired
    private UserVideoService userVideoService;

    @Autowired
    private DoctorVideoService doctorVideoService;

    public class AnswerBean {
        private int isfree; //isfree=1 零元支付
        private String orderno; //订单编号
        private long orderid;//订单id
        private BigDecimal price;//价格
        private long userid;//用户id
        private long doctorid;//医生id
        private int status;//该用户是否存在未结束订单
        private String des;//支付描述
        private BigDecimal walletbalance;//钱包余额
        private int isinformation;

        public BigDecimal getWalletbalance() {
            return walletbalance;
        }

        public void setWalletbalance(BigDecimal walletbalance) {
            this.walletbalance = walletbalance;
        }

        public int getIsinformation() {
            return isinformation;
        }

        public void setIsinformation(int isinformation) {
            this.isinformation = isinformation;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public long getOrderid() {
            return orderid;
        }

        public void setOrderid(long orderid) {
            this.orderid = orderid;
        }

        public int getIsfree() {
            return isfree;
        }

        public void setIsfree(int isfree) {
            this.isfree = isfree;
        }

        public String getOrderno() {
            return orderno;
        }

        public void setOrderno(String orderno) {
            this.orderno = orderno;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public long getUserid() {
            return userid;
        }

        public void setUserid(long userid) {
            this.userid = userid;
        }

        public long getDoctorid() {
            return doctorid;
        }

        public void setDoctorid(long doctorid) {
            this.doctorid = doctorid;
        }
    }


    public List<Map<String, Object>> getOnDuctDoctorList(String doctorName) {
        return answerMapper.getOnDuctDoctorList(doctorName);
    }

    public Map<String, Object> getUserById(int id) {
        return answerMapper.getUserById(id);
    }

    /**
     * @param id
     * @param payType
     * @param payStatus
     * @param price
     * @param orderRemark
     * @return
     */
    public boolean updateDoctorPhoneOrder(int id, int payType, int payStatus, BigDecimal price, String orderRemark, long agenId) {
        return answerMapper.updateDoctorPhoneOrder(id, payType, payStatus, price, orderRemark, agenId);
    }

    public Map<String, Object> getDoctorPhoneOrderById(int id) {
        Map<String, Object> result = answerMapper.getDoctorPhoneOrderById(id);
        if (result != null) {
            result.put("diseaselist", answerMapper.getMiddlePhoneDiseaseList(id));
            result.put("paytypelist", PayTypeEnum.getList());
            result.put("disease", answerMapper.findDepartTypePhone(id));
        }
        return result;
    }

    /**
     * 后台添加急诊订单
     *
     * @param doctorId    医生ID
     * @param userId      用户ID
     * @param doctorPhone 医生电话
     * @param price       咨询价格
     * @param payStatus   支付状态
     * @param payType     支付类型
     * @param orderRemark 订单备注
     * @param agenId      登录人ID
     * @param diseaseList 病症list
     */
    public void addAdminDoctorPhoneOrder(long doctorId, long userId, String doctorPhone, String userPhone, BigDecimal price,
                                         int payStatus, long createTime, int payType, String orderRemark, long agenId, List<?> diseaseList) {
        String orderNo = IdGenerator.INSTANCE.nextId();
        int status = 0;
        if (payStatus == PayStateEnum.UnPaid.getCode()) {
            status = PhoneOrderStateEnum.UnPaid.getCode();
        } else if (payStatus == PayStateEnum.Paid.getCode()) {
            status = PhoneOrderStateEnum.Paid.getCode();
        }
        long orderId = answerMapper.addAdminDoctorPhoneOrder(orderNo, doctorId, userId, doctorPhone, userPhone, price, payStatus,
                createTime, status, payType, orderRemark, agenId);
        if (diseaseList.size() > 0) {
            for (Object obj : diseaseList) {
                long diseaseId = ModelUtil.strToLong(String.valueOf(obj), 0);
                Map<String, Object> symptoms = answerMapper.getSymptoms(diseaseId);
                if (symptoms != null) {
                    answerMapper.addPhoneDiseaseid(orderId, diseaseId, ModelUtil.getStr(symptoms, "value"));
                }
            }
        }
    }


    /**
     * @param userId   用户id
     * @param doctorId 医生id
     */
    private AnswerBean saveAnswerOrder(long userId, long doctorId, VisitCategoryEnum visitCategoryEnum, List<?> diseaselist) {
        return saveAnswerOrder(userId, doctorId, visitCategoryEnum, diseaselist, null, new ArrayList<>(), 0, 0, 0);
    }

    /**
     * @param userId   用户id
     * @param doctorId 医生id
     */
    private AnswerBean saveAnswerOrderNew(long userId, long doctorId, VisitCategoryEnum visitCategoryEnum, List<?> diseaselist) {
        AnswerBean result = new AnswerBean();
        String orderNo = IdGenerator.INSTANCE.nextId();
        Map<String, Object> build = this.doctorPrice.setPriceType(visitCategoryEnum)
                .setUserId(userId)
                .setDoctorId(doctorId)
                .build()
                .result();
        int type = ModelUtil.getInt(build, "type");
        BigDecimal price = ModelUtil.getDec(build, "price", BigDecimal.ZERO);
        BigDecimal marketPrice = ModelUtil.getDec(build, "originalprice", BigDecimal.ZERO);
        BigDecimal originalprice = ModelUtil.getDec(build, "doctorprice", BigDecimal.ZERO);
        Double vipdiscount = ModelUtil.getDouble(build, "vipdiscount", 1);
        log.info("price:" + price);
        log.info("type:" + type);
        long orderId;
        try {
            orderId = answerMapper.addAnswerOrder(orderNo, userId, doctorId, price, marketPrice, originalprice, vipdiscount, visitCategoryEnum.getCode(), null, 0, 0, 0);
        } catch (Exception e) {
            throw new ServiceException("您输入的内容有特殊字符，请检查");
        }

        Map<String, Object> userMp = answerMapper.getUserAccountByUserId(userId);
        String uToken = "";
        int uPlatform = 0;
        if (userMp != null) {
            uToken = ModelUtil.getStr(userMp, "xgtoken");
            uPlatform = ModelUtil.getInt(userMp, "platform");
        }
        Map<String, Object> doctorMp = answerMapper.getDoctorExtendsByDoctorId(doctorId);
        String dToken = "";
        int dPlatform = 0;
        if (doctorMp != null) {
            dToken = ModelUtil.getStr(doctorMp, "xgtoken");
            dPlatform = ModelUtil.getInt(doctorMp, "platform");
        }
        answerMapper.addAnswerOrderExtend(orderId, dPlatform, dToken, uPlatform, uToken);


        for (Object key : diseaselist) {
            long diseaseId = ModelUtil.strToLong(String.valueOf(key), 0);
            if (diseaseId > 0) {
                Map<String, Object> symptoms = answerMapper.getSymptoms(diseaseId);
                answerMapper.addAnswerDiseaseid(orderId, diseaseId, ModelUtil.getStr(symptoms, "value"));
            }
        }

        //机器人
        addProblemTemplateNew(orderId);
        PayTypeEnum payTypeEnum;
        if (type == DoctorPrice.ZERO) {
            result.setIsfree(1);
            payTypeEnum = PayTypeEnum.ZERO;
            updateAnswerStatusSuccess(orderNo, null, payTypeEnum.getCode(), userId, doctorId);
            addAnswerOrderPushData(orderNo);
        } else if (type == DoctorPrice.VIP_Free) {
            result.setIsfree(1);
            payTypeEnum = PayTypeEnum.VipFree;
            updateAnswerStatusSuccess(orderNo, null, payTypeEnum.getCode(), userId, doctorId);
            addAnswerOrderPushData(orderNo);
        } else if (type == DoctorPrice.VIP_ZERO) {
            result.setIsfree(1);
            payTypeEnum = PayTypeEnum.VipZero;
            long vipid = ModelUtil.getLong(build, "vipid");
            if (visitCategoryEnum == VisitCategoryEnum.Outpatient) {
                cardService.updateHealthConsultantCeefax(vipid);
            } else if (visitCategoryEnum == VisitCategoryEnum.graphic) {
                cardService.updateMedicalExpertCeefax(vipid);
            }
            updateAnswerStatusSuccess(orderNo, null, payTypeEnum.getCode(), userId, doctorId);
            addAnswerOrderPushData(orderNo);
        }
        result.setOrderno(orderNo);
        result.setOrderid(orderId);
        result.setPrice(price);
        result.setDoctorid(doctorId);
        result.setUserid(userId);
        result.setStatus(0);
        result.setWalletbalance(ModelUtil.getDec(userMp, "walletbalance", BigDecimal.ZERO));
        result.setDes(TextFixed.problem_pay_dec);
        return result;
    }

    //图文订单支付成功推送
    public void addAnswerOrderPushData(String orderNo) {
        Map<String, Object> orderMp = getAnswerOrderByOrderNo(orderNo);

        int orderId = ModelUtil.getInt(orderMp, "id");
        int userId = ModelUtil.getInt(orderMp, "userid");
        int doctorId = ModelUtil.getInt(orderMp, "doctorid");
        long createTime = ModelUtil.getLong(orderMp, "createtime");
        String name = ModelUtil.getStr(orderMp, "name");
        int uplatform = ModelUtil.getInt(orderMp, "uplatform");
        int dplatform = ModelUtil.getInt(orderMp, "dplatform");
        String uToken = ModelUtil.getStr(orderMp, "utoken");
        String dToken = ModelUtil.getStr(orderMp, "dtoken");


        //用户端消息开始
        systemService.addPushApp(TextFixed.answerUserOrderPushTitleText,
                String.format(TextFixed.answerUserOrderPushText, UnixUtil.getDate(createTime, "yyyy-MM-dd HH:mm:ss")),
                TypeNameAppPushEnum.answerOrderDetail.getCode(), String.valueOf(orderId), userId, MessageTypeEnum.user.getCode(), uplatform, uToken); //app 用户 push 消息
        systemService.addMessage("", TextFixed.messageOrderTitle,
                MessageTypeEnum.user.getCode(), String.valueOf(orderId),
                TypeNameAppPushEnum.answerOrderDetail.getCode(), userId, String.format(TextFixed.answerUserOrderSystemText, UnixUtil.getDate(createTime, "yyyy-MM-dd HH:mm:ss")), "");//app 用户内推送
        //用户端消息结束

        //医生端消息开始
        systemService.addPushApp(TextFixed.answerDoctorOrderPushTitleText,
                String.format(TextFixed.answerDoctorOrderText, name),
                TypeNameAppPushEnum.answerOrderDetail.getCode(), String.valueOf(orderId), doctorId, MessageTypeEnum.doctor.getCode(), dplatform, dToken); //app 医生 push 消息
        systemService.addMessage("", TextFixed.messageOrderTitle,
                MessageTypeEnum.doctor.getCode(), String.valueOf(orderId),
                TypeNameAppPushEnum.answerOrderDetail.getCode(), doctorId, String.format(TextFixed.answerDoctorOrderSystemText, name), "");//app 医生 内推送
        //医生端消息结束

        systemService.addPushSendRule(orderId);


    }

    /**
     * @param userId   用户id
     * @param doctorId 医生id
     */
    @Transactional
    public AnswerBean savePhoneOrder(long userId, long doctorId, VisitCategoryEnum visitCategoryEnum, List<?> diseaselist, long schedulingid) {
        long visitingstarttime = 0;
        long visitingendtime = 0;
        if (schedulingid != 0 && visitCategoryEnum.equals(VisitCategoryEnum.phone)) {
            Map<String, Object> subscribe = answerMapper.getPhoneSubscribe(schedulingid, doctorId);
            if (subscribe == null) {
                throw new ServiceException("该预约不存在");
            }
            if (ModelUtil.getInt(subscribe, "issubscribe") == 1) {
                throw new ServiceException("该时间段已经被预约");
            }
            visitingstarttime = ModelUtil.getLong(subscribe, "visitingstarttime");
            visitingendtime = ModelUtil.getLong(subscribe, "visitingendtime");
        }
        return savePhoneOrder(userId, doctorId, visitCategoryEnum, diseaselist, schedulingid, visitingstarttime, visitingendtime, null, null, 0, 0, 0);
    }

    //电话订单支付成功推送
    public void addPhoneOrderPushData(String orderNo) {
        Map<String, Object> orderMp = getDoctorPhoneOrderByOrderNo(orderNo);
        int orderId = ModelUtil.getInt(orderMp, "id");
        int userId = ModelUtil.getInt(orderMp, "userid");
        int doctorId = ModelUtil.getInt(orderMp, "doctorid");
        long createTime = ModelUtil.getLong(orderMp, "createtime");
        long subscribetime = ModelUtil.getLong(orderMp, "subscribetime");
        int visitcategory = ModelUtil.getInt(orderMp, "visitcategory");
        String time = UnixUtil.getDate(subscribetime, "yyyy-MM-dd HH:mm:ss");

        int uplatform = ModelUtil.getInt(orderMp, "uplatform");
        int dplatform = ModelUtil.getInt(orderMp, "dplatform");
        String uToken = ModelUtil.getStr(orderMp, "utoken");
        String dToken = ModelUtil.getStr(orderMp, "dtoken");

        String name = ModelUtil.getStr(orderMp, "username");

        String userTitle = TextFixed.phoneUserOrderPaySuccessPushTitleText;
        String userText = String.format(TextFixed.phoneUserOrderPaySuccessPushText, time);
        String userSystemText = String.format(TextFixed.phoneUserOrderPaySuccessSystemText, time);
        ;

        String doctorTitle = TextFixed.phoneDoctorOrderPushTitleText;
        String doctorText = String.format(TextFixed.phoneDoctorOrderText, name, time);
        String doctorSystemText = String.format(TextFixed.phoneDoctorOrderSystemText, name, time);
        if (visitcategory == 7) {//首页进去
            time = UnixUtil.getDate(createTime, "yyyy-MM-dd");
            userTitle = TextFixed.phoneUserDepartmentOrderPaySuccessPushTitleText;
            userText = String.format(TextFixed.phoneUserDepartmentOrderPaySuccessPushText, time);
            userSystemText = String.format(TextFixed.phoneUserDepartmentOrderPaySuccessSystemText, time);

            doctorTitle = TextFixed.phoneDoctorDepartmentOrderPushTitleText;
            doctorText = String.format(TextFixed.phoneDoctorDepartmentOrderText, name);
            doctorSystemText = String.format(TextFixed.phoneDoctorDepartmentOrderSystemText, name);
        }


        //用户端消息开始
        systemService.addPushApp(userTitle,
                userText,
                TypeNameAppPushEnum.phoneOrderDetail.getCode(), String.valueOf(orderId), userId, MessageTypeEnum.user.getCode(), uplatform, uToken); //app 用户 push 消息
        systemService.addMessage("", TextFixed.messageOrderTitle,
                MessageTypeEnum.user.getCode(), String.valueOf(orderId),
                TypeNameAppPushEnum.phoneOrderDetail.getCode(), userId,
                userSystemText, "");//app 用户内推送
        //用户端消息结束

        //医生端消息开始
        systemService.addPushApp(doctorTitle,
                doctorText,
                TypeNameAppPushEnum.phoneOrderDetail.getCode(), String.valueOf(orderId), doctorId, MessageTypeEnum.doctor.getCode(), dplatform, dToken); //app 医生 push 消息
        systemService.addMessage("", TextFixed.messageOrderTitle,
                MessageTypeEnum.doctor.getCode(), String.valueOf(orderId),
                TypeNameAppPushEnum.phoneOrderDetail.getCode(), doctorId, doctorSystemText, "");//app 医生 内推送
        //医生端消息结束
    }

    //图文订单聊天推送
    public void addDoctorAnswerPushData(long orderId, int contentType, int questionaAnswerType) {
        Map<String, Object> problem = answerMapper.getProblem(orderId);
        long userid = ModelUtil.getLong(problem, "userid");
        long doctorid = ModelUtil.getLong(problem, "doctorid");

        String doctorName = ModelUtil.getStr(problem, "docname");
        String userName = ModelUtil.getStr(problem, "username");
        String doctorUrl = ModelUtil.getStr(problem, "docphotourl");
        String userUrl = ModelUtil.getStr(problem, "headpic");
        int uplatform = ModelUtil.getInt(problem, "uplatform");
        int dplatform = ModelUtil.getInt(problem, "dplatform");
        String uToken = ModelUtil.getStr(problem, "utoken");
        String dToken = ModelUtil.getStr(problem, "dtoken");
        int useronline = ModelUtil.getInt(problem, "useronline");
        int doctoronline = ModelUtil.getInt(problem, "doctoronline");
        String messageContent = "";
        TypeNameAppPushEnum typeNameAppPushEnum = TypeNameAppPushEnum.inquiryDoctorReplyOrder;
        switch (QAContentTypeEnum.getValue(contentType)) {
            case Text:
                if (questionaAnswerType == 0) {//用户回复,给医生提醒
                    messageContent = String.format(TextFixed.userServerTextTitle, userName);
                } else if (questionaAnswerType == 1) {//医生回复,给用户回复
                    messageContent = String.format(TextFixed.doctorServerTextTitle, doctorName);
                }
                break;
            case Voice:
                if (questionaAnswerType == 0) {//用户回复,给医生提醒
                    messageContent = String.format(TextFixed.userServerVoiceTitle, userName);
                } else if (questionaAnswerType == 1) {//医生回复,给用户回复
                    messageContent = String.format(TextFixed.doctorServerVoiceTitle, doctorName);
                }
                break;
            case Picture:
                if (questionaAnswerType == 0) {//用户回复,给医生提醒
                    messageContent = String.format(TextFixed.userServerImageTitle, userName);
                } else if (questionaAnswerType == 1) {//医生回复,给用户回复
                    messageContent = String.format(TextFixed.doctorServerImageTitle, doctorName);
                }
                break;
            case Prescription:
                if (questionaAnswerType == 1) {//医生回复,给用户回复
                    messageContent = String.format(TextFixed.doctorServerImageTitle, doctorName);
                } else {
                    return;
                }
                break;
            case DoctorClose:
                if (questionaAnswerType == 1) {//医生请求关闭
                    messageContent = String.format(TextFixed.doctorCloseOrderText, doctorName);
                    typeNameAppPushEnum = TypeNameAppPushEnum.DoctorClose;
                    doctorName = TextFixed.messageServiceTitle;
                } else {
                    return;
                }
                break;
            case UserAgreenCloseToUser:
                return;
            case UserAgreenCloseToDoctor:
                return;
            case UserNotAgreenCloseToDoctor:
                return;
            case UserNotAgreenCloseToUser:
                return;
            default:
                messageContent = "";
                return;
        }
        if (questionaAnswerType == 0 && doctoronline != 1) {//用户回复,给医生提醒
            systemService.addPushApp(TextFixed.messageServiceTitle, messageContent,
                    TypeNameAppPushEnum.inquiryUserReplyOrder.getCode(),
                    String.valueOf(orderId), doctorid,
                    MessageTypeEnum.doctor.getCode(), dplatform, dToken); //推送push消息
            systemService.addMessage(userUrl, userName,
                    MessageTypeEnum.doctor.getCode(), String.valueOf(orderId),
                    TypeNameAppPushEnum.inquiryUserReplyOrder.getCode(), doctorid,
                    messageContent,
                    "");
        } else if (questionaAnswerType == 1 && useronline != 1) {//医生回复,给用户回复
            systemService.addPushApp(TextFixed.messageServiceTitle, messageContent,
                    typeNameAppPushEnum.getCode(),
                    String.valueOf(orderId), userid,
                    MessageTypeEnum.user.getCode(), uplatform, uToken); //
            systemService.addMessage(doctorUrl, doctorName,
                    MessageTypeEnum.user.getCode(), String.valueOf(orderId),
                    typeNameAppPushEnum.getCode(), userid,
                    messageContent,
                    "");
        }
    }

    /**
     * 医生患者问诊加急诊列表
     *
     * @param doctorId
     * @return
     */
    public List<Map<String, Object>> doctorUserOrderList(long doctorId, long userId) {
        return answerMapper.doctorUserOrderList(doctorId, userId);
    }


    /**
     * 后台用户问诊加急诊列表
     *
     * @return
     */
    public List<Map<String, Object>> adminUserOrderList(long userId, int pageIndex, int pageSize) {
        return answerMapper.userOrderList(userId, pageIndex, pageSize);
    }


    /**
     * 患者问诊加急诊数量
     *
     * @return
     */
    public long userOrderCont(long userId) {
        return answerMapper.userOrderCount(userId);
    }


    /**
     * 后台问诊列表
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> adminAnswerOrderList(long departid, long departTypeid, String
            userName, String doctorName, String doctorPhone, String userPhone, long startTime, long endTime, int state,
                                                          int pageIndex, int pageSize) {
        return answerMapper.adminAnswerOrderList(departid, departTypeid, userName, doctorName, doctorPhone, userPhone, startTime, endTime, state, pageIndex, pageSize);
    }

    /**
     * 后台问诊列表
     *
     * @return
     */
    public long adminAnswerOrderCount(long departid, long departTypeid, String userName, String doctorName, String
            doctorPhone, String userPhone, long startTime, long endTime, int state) {
        return answerMapper.adminAnswerOrderCount(departid, departTypeid, userName, doctorName, doctorPhone, userPhone, startTime, endTime, state);
    }

    /**
     * 后台急诊列表
     *
     * @param pageindex
     * @param pagesize
     * @return
     */
    public List<Map<String, Object>> adminPhoneOrderList(long departid, long departTypeid, String userName, String
            doctorName, String doctorPhone, String userPhone, long startTime, long endTime, int state, int pageindex,
                                                         int pagesize) {
        return answerMapper.adminPhoneOrderLists(departid, departTypeid, userName, doctorName, doctorPhone, userPhone, startTime, endTime, state, pageindex, pagesize);
    }

    public List<Map<String, Object>> adminPhoneOrderListExecl(String userName, String doctorName, String
            doctorPhone, String userPhone, long startTime, long endTime, int state) {
        List<Map<String, Object>> phoneOrder = answerMapper.adminPhoneOrderList(userName, doctorName, doctorPhone, userPhone, startTime, endTime, state, 1, 9999999);
        Map<String, Object> head = new HashMap<>();
        head.put("id", "编号");
        head.put("orderno", "订单编号");
        head.put("username", "用户姓名");
        head.put("doctorname", "编号");
        head.put("statesname", "订单状态");
        head.put("actualmoney", "价格");
        head.put("doctorphone", "医生手机号");
        head.put("userphone", "用户手机号");
        head.put("createtime", "创建时间");
        head.put("recordurl", "语音url");
        head.put("phonestatusname", "通话状态");
        phoneOrder.add(0, head);
        return phoneOrder;
    }

    /**
     * 后台问诊列表
     *
     * @return
     */
    public long adminPhoneOrderCount(long departid, long departTypeid, String userName, String doctorName, String
            doctorPhone, String userPhone, long startTime, long endTime, int state) {
        return answerMapper.adminPhoneOrderCount(departid, departTypeid, userName, doctorName, doctorPhone, userPhone, startTime, endTime, state);
    }

    /**
     * app医生端问诊列表
     *
     * @param doctorid
     * @param pageindex
     * @param pagesize
     * @return
     */
    public List<Map<String, Object>> appDoctorAnswerOrderList(long doctorid, int status, int intotype, int pageindex,
                                                              int pagesize) {
        List<Map<String, Object>> answerOrderList = answerMapper.appDoctorAnswerOrderList(doctorid, status, intotype, pageindex, pagesize);
        List<Long> ids = new ArrayList<>();
        for (Map<String, Object> map : answerOrderList) {
            ids.add(ModelUtil.getLong(map, "id"));
        }
        if (ids.size() > 0) {
            List<Map<String, Object>> orderDiseaseList = answerMapper.orderAnswerDiseaseList(ids);
            initList(answerOrderList, orderDiseaseList);
        }
        return answerOrderList;
    }


    /**
     * app医生端急诊列表
     *
     * @param doctorid
     * @param pageindex
     * @param pagesize
     * @return
     */
    public List<Map<String, Object>> appDoctorPhoneOrderList(long doctorid, int status, int intotype, int pageindex, int pagesize) {
        List<Map<String, Object>> phoneOrderList = answerMapper.appDoctorPhoneOrderList(doctorid, status, intotype, pageindex, pagesize);
        List<Long> ids = new ArrayList<>();
        for (Map<String, Object> map : phoneOrderList) {
            ids.add(ModelUtil.getLong(map, "id"));
        }
        if (ids.size() > 0) {
            List<Map<String, Object>> orderDiseaseList = answerMapper.orderPhoneDiseaseList(ids);
            initList(phoneOrderList, orderDiseaseList);
        }
        return phoneOrderList;
    }


    /**
     * app医生端急诊列表
     *
     * @param doctorid
     * @param pageindex
     * @param pagesize
     * @return
     */
    public List<Map<String, Object>> appDoctorVideoOrderList(long doctorid, int status, int intotype, int pageindex,
                                                             int pagesize) {
        List<Map<String, Object>> phoneOrderList = doctorVideoService.appDoctorVideoOrderList(doctorid, status, intotype, pageindex, pagesize);
        List<Long> ids = new ArrayList<>();
        for (Map<String, Object> map : phoneOrderList) {
            ids.add(ModelUtil.getLong(map, "id"));
        }
        if (ids.size() > 0) {
            List<Map<String, Object>> orderDiseaseList = doctorVideoService.orderVideoDiseaseList(ids);
            initList(phoneOrderList, orderDiseaseList);
        }
        return phoneOrderList;
    }


    /**
     * app用户端端问诊列表
     *
     * @param userId
     * @param pageindex
     * @param pagesize
     * @return
     */
    public List<Map<String, Object>> appUserAnswerOrderList(long userId, int pageindex, int pagesize) {
        List<Map<String, Object>> phoneOrderList = answerMapper.appUserAnswerOrderList(userId, pageindex, pagesize);
        List<Long> ids = new ArrayList<>();
        for (Map<String, Object> map : phoneOrderList) {
            ids.add(ModelUtil.getLong(map, "id"));
        }
        if (ids.size() > 0) {
            List<Map<String, Object>> orderDiseaseList = answerMapper.orderAnswerDiseaseList(ids);
            initList(phoneOrderList, orderDiseaseList);
        }
        return phoneOrderList;
    }

    /**
     * app用户端端急诊列表
     *
     * @param userId
     * @param pageindex
     * @param pagesize
     * @return
     */
    public List<Map<String, Object>> appUserPhoneOrderList(long userId, int pageindex, int pagesize) {
        List<Map<String, Object>> phoneOrderList = answerMapper.appUserPhoneOrderList(userId, pageindex, pagesize);
        List<Long> ids = new ArrayList<>();
        for (Map<String, Object> map : phoneOrderList) {
            ids.add(ModelUtil.getLong(map, "id"));
        }
        if (ids.size() > 0) {
            List<Map<String, Object>> orderDiseaseList = answerMapper.orderPhoneDiseaseList(ids);
            initList(phoneOrderList, orderDiseaseList);
        }
        return phoneOrderList;
    }

    public List<Map<String, Object>> appUserVideoOrderList(long userId, int pageindex, int pagesize) {
        List<Map<String, Object>> videoOrderList = userVideoService.appUserVideoOrderList(userId, pageindex, pagesize);
        List<Long> ids = new ArrayList<>();
        for (Map<String, Object> map : videoOrderList) {
            ids.add(ModelUtil.getLong(map, "id"));
        }
        if (ids.size() > 0) {
            List<Map<String, Object>> orderDiseaseList = userVideoService.orderVideoDiseaseList(ids);
            initList(videoOrderList, orderDiseaseList);
        }
        return videoOrderList;
    }

    private void initList(List<Map<String, Object>> orderList, List<Map<String, Object>> diseaseList) {
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
            List<Map<String, Object>> diseaselist = (List<Map<String, Object>>) tempProblem.get(ModelUtil.getLong(map, "id"));
            if (diseaselist != null && diseaselist.size() > 6) {
                diseaselist = diseaselist.subList(0, 6);
            }
            map.put("diseaselist", diseaselist);
        }
    }


    /**
     * todo 兼容
     * 医生订单详情没有历史记录
     *
     * @param orderid
     * @return
     */
    public Map<String, Object> getDoctorAnswerList(long orderid, int pageindex, int pagesize, int flag) {
        //更新未读消息为0
        if (flag == 1) {//当医生端查看时
            answerMapper.updateNum(orderid);
        }
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> answerOrder = answerMapper.getAnswerOrder(orderid);
        long doctorid = ModelUtil.getLong(answerOrder, "doctorid");
        long userid = ModelUtil.getLong(answerOrder, "userid");
        int states = ModelUtil.getInt(answerOrder, "states");
        String diagnosis = ModelUtil.getStr(answerOrder, "diagnosis");
        //问答列表
        List<Map<String, Object>> answerList;
        if (states == 2 || states == 6) {
            //问答列表包涵历史
            answerList = answerMapper.getDoctorHistoryAnswerList(doctorid, userid, pageindex, pagesize);
        } else {
            //问答列表没有历史
            answerList = answerMapper.getDoctorCurrentAnswerList(orderid, pageindex, pagesize);
        }
        List<Long> idsDiseaseDoctor = new ArrayList<>();
        List<Long> idPrescriptionList = new ArrayList<>();
        List<Long> idsUserInfo = new ArrayList<>();
        getIdsByDoctor(answerList, idsUserInfo, idsDiseaseDoctor, idPrescriptionList);

        if (idsUserInfo.size() > 0) {
            List<Map<String, Object>> userInfoList = answerMapper.userInfoList(idsUserInfo);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.UserInfo).getContent(answerList, userInfoList);
        }
        if (idsDiseaseDoctor.size() > 0) {
            //医生显示
            List<Map<String, Object>> doctorAnswerDiseaseTemplateList = answerMapper.doctorAnswerDiseaseTemplateList(idsDiseaseDoctor);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.DiseaseDoctor).getContent(answerList, doctorAnswerDiseaseTemplateList);
        }
        if (idPrescriptionList.size() > 0) {
            //处方
            List<Map<String, Object>> prescriptionList = answerMapper.prescriptionList(idPrescriptionList);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.Prescription).getContent(answerList, prescriptionList);
        }
        Map<String, Object> value = answerMapper.getDoctorOrderState(orderid);
        int guidance = 0;
        if (AnswerOrderStateEnum.OrderSuccess.getCode() == states) {
            if (StrUtil.isEmpty(diagnosis)) {
                guidance = 1;
            } else {
                guidance = 2;
            }
        }
        result.put("guidance", guidance);
        result.put("answerlist", answerList);
        result.put("status", ModelUtil.getInt(value, "states"));
        result.put("examine", ModelUtil.getInt(value, "examine"));
        result.put("doctorcode", ModelUtil.getStr(answerOrder, "doccode"));
        result.put("doctorid", ModelUtil.getStr(answerOrder, "doctorid"));
        result.put("userid", userid);
        return result;
    }

    /**
     * 用户追加的消息
     *
     * @param orderid
     * @return
     */
    public List<Map<String, Object>> getAppendDoctorAnswerList(long orderid, long id) {
        //问答列表
        List<Map<String, Object>> answerList = answerMapper.getAppendDoctorAnswerList(orderid, id);
        List<Long> idsDiseaseDoctor = new ArrayList<>();
        List<Long> idPrescriptionList = new ArrayList<>();
        List<Long> idsUserInfo = new ArrayList<>();
        getIdsByDoctor(answerList, idsUserInfo, idsDiseaseDoctor, idPrescriptionList);

        if (idsUserInfo.size() > 0) {
            List<Map<String, Object>> userInfoList = answerMapper.userInfoList(idsUserInfo);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.UserInfo).getContent(answerList, userInfoList);
        }
        if (idsDiseaseDoctor.size() > 0) {
            //医生显示
            List<Map<String, Object>> doctorAnswerDiseaseTemplateList = answerMapper.doctorAnswerDiseaseTemplateList(idsDiseaseDoctor);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.DiseaseDoctor).getContent(answerList, doctorAnswerDiseaseTemplateList);
        }
        if (idPrescriptionList.size() > 0) {
            //处方
            List<Map<String, Object>> prescriptionList = answerMapper.prescriptionList(idPrescriptionList);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.Prescription).getContent(answerList, prescriptionList);
        }
        return answerList;
    }

    /**
     * 医生指定消息
     *
     * @param answerid
     * @return
     */
    public Map<String, Object> getDoctorAnswerList(long answerid) {
        //问答列表
        List<Map<String, Object>> answerList = answerMapper.getDoctorAnswerList(answerid);
        List<Long> idsDiseaseDoctor = new ArrayList<>();
        List<Long> idPrescriptionList = new ArrayList<>();
        List<Long> idsUserInfo = new ArrayList<>();
        getIdsByDoctor(answerList, idsUserInfo, idsDiseaseDoctor, idPrescriptionList);

        if (idsUserInfo.size() > 0) {
            List<Map<String, Object>> userInfoList = answerMapper.userInfoList(idsUserInfo);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.UserInfo).getContent(answerList, userInfoList);
        }
        if (idsDiseaseDoctor.size() > 0) {
            //医生显示
            List<Map<String, Object>> doctorAnswerDiseaseTemplateList = answerMapper.doctorAnswerDiseaseTemplateList(idsDiseaseDoctor);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.DiseaseDoctor).getContent(answerList, doctorAnswerDiseaseTemplateList);
        }
        if (idPrescriptionList.size() > 0) {
            //处方
            List<Map<String, Object>> prescriptionList = answerMapper.prescriptionList(idPrescriptionList);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.Prescription).getContent(answerList, prescriptionList);
        }
        return answerList.size() > 0 ? answerList.get(0) : null;
    }

    /**
     * 用户追加的消息
     *
     * @param orderid
     * @return
     */
    public List<Map<String, Object>> getAppendDoctorAnswerListNew(long orderid, long id) {
        //问答列表
        List<Map<String, Object>> answerList = answerMapper.getAppendDoctorAnswerList(orderid, id);
        List<Long> idsDiseaseDoctor = new ArrayList<>();
        List<Long> idPrescriptionList = new ArrayList<>();
        List<Long> idsUserInfo = new ArrayList<>();
        getIdsByDoctor(answerList, idsUserInfo, idsDiseaseDoctor, idPrescriptionList);

        if (idsUserInfo.size() > 0) {
            List<Map<String, Object>> userInfoList = answerMapper.userInfoList(idsUserInfo);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.UserInfo).getContent(answerList, userInfoList);
        }
        //医生显示
        List<Map<String, Object>> doctorAnswerDiseaseTemplateList = answerMapper.doctorAnswerDiseaseTemplateList(orderid);
        getContent(answerList, doctorAnswerDiseaseTemplateList);
        if (idPrescriptionList.size() > 0) {
            //处方
            List<Map<String, Object>> prescriptionList = answerMapper.prescriptionList(idPrescriptionList);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.Prescription).getContent(answerList, prescriptionList);
        }
        return answerList;
    }

    /**
     * 用户追加的消息
     *
     * @param orderid
     * @return
     */
    public List<Map<String, Object>> getAppendDoctorSocketAnswerListNew(long orderid, long id) {
        //问答列表
        List<Map<String, Object>> answerList = answerMapper.getAppendDoctorSocketAnswerList(orderid, id);
        List<Long> idsDiseaseDoctor = new ArrayList<>();
        List<Long> idPrescriptionList = new ArrayList<>();
        List<Long> idsUserInfo = new ArrayList<>();
        getIdsByDoctor(answerList, idsUserInfo, idsDiseaseDoctor, idPrescriptionList);

        if (idsUserInfo.size() > 0) {
            List<Map<String, Object>> userInfoList = answerMapper.userInfoList(idsUserInfo);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.UserInfo).getContent(answerList, userInfoList);
        }
        //医生显示
        List<Map<String, Object>> doctorAnswerDiseaseTemplateList = answerMapper.doctorAnswerDiseaseTemplateList(orderid);
        getContent(answerList, doctorAnswerDiseaseTemplateList);
        if (idPrescriptionList.size() > 0) {
            //处方
            List<Map<String, Object>> prescriptionList = answerMapper.prescriptionList(idPrescriptionList);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.Prescription).getContent(answerList, prescriptionList);
        }
        return answerList;
    }

    /**
     * 用户追加的消息
     *
     * @param orderid
     * @return
     */
    public List<Map<String, Object>> getAppendDoctorSocketAnswerList(long orderid, long id) {
        List<Map<String, Object>> orderState = answerMapper.getOrderState(orderid);
        //问答列表
        List<Map<String, Object>> answerList = answerMapper.getAppendDoctorSocketAnswerList(orderid, id);
        List<Long> idsDiseaseDoctor = new ArrayList<>();
        List<Long> idPrescriptionList = new ArrayList<>();
        List<Long> idsUserInfo = new ArrayList<>();
        getIdsByDoctor(answerList, idsUserInfo, idsDiseaseDoctor, idPrescriptionList);

        if (idsUserInfo.size() > 0) {
            List<Map<String, Object>> userInfoList = answerMapper.userInfoList(idsUserInfo);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.UserInfo).getContent(answerList, userInfoList);
        }
        if (idsDiseaseDoctor.size() > 0) {
            //医生显示
            List<Map<String, Object>> doctorAnswerDiseaseTemplateList = answerMapper.doctorAnswerDiseaseTemplateList(idsDiseaseDoctor);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.DiseaseDoctor).getContent(answerList, doctorAnswerDiseaseTemplateList);
        }
        if (idPrescriptionList.size() > 0) {
            //处方
            List<Map<String, Object>> prescriptionList = answerMapper.prescriptionList(idPrescriptionList);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.Prescription).getContent(answerList, prescriptionList);
        }

        ProblemFactory.getProblemImpl(QAContentTypeEnum.Tips).getContent(answerList, orderState);
        return answerList;
    }

    /**
     * 医生订单详情没有历史记录
     *
     * @param orderid
     * @return
     */
    public Map<String, Object> getDoctorAnswerListNew(long orderid, int pageindex, int pagesize) {

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> answerOrder = answerMapper.getAnswerOrder(orderid);
        long doctorid = ModelUtil.getLong(answerOrder, "doctorid");
        long userid = ModelUtil.getLong(answerOrder, "userid");
        int states = ModelUtil.getInt(answerOrder, "states");
        int issubmit = ModelUtil.getInt(answerOrder, "issubmit");
        //问答列表
        List<Map<String, Object>> answerList;
        if (states == 2 || states == 6) {
            //问答列表包涵历史
            answerList = answerMapper.getDoctorHistoryAnswerList(doctorid, userid, pageindex, pagesize);
        } else {
            //问答列表没有历史
            answerList = answerMapper.getDoctorCurrentAnswerList(orderid, pageindex, pagesize);
        }
        List<Long> idsDiseaseDoctor = new ArrayList<>();
        List<Long> idPrescriptionList = new ArrayList<>();
        List<Long> idsUserInfo = new ArrayList<>();
        getIdsByDoctor(answerList, idsUserInfo, idsDiseaseDoctor, idPrescriptionList);

        if (idsUserInfo.size() > 0) {
            List<Map<String, Object>> userInfoList = answerMapper.userInfoList(idsUserInfo);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.UserInfo).getContent(answerList, userInfoList);
        }
        //医生显示
        List<Map<String, Object>> doctorAnswerDiseaseTemplateList = answerMapper.doctorAnswerDiseaseTemplateList(orderid);
        getContent(answerList, doctorAnswerDiseaseTemplateList);
        if (idPrescriptionList.size() > 0) {
            //处方
            List<Map<String, Object>> prescriptionList = answerMapper.prescriptionList(idPrescriptionList);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.Prescription).getContent(answerList, prescriptionList);
        }
        Map<String, Object> value = answerMapper.getDoctorOrderState(orderid);
        result.put("answerlist", answerList);
        result.put("status", ModelUtil.getInt(value, "states"));
        result.put("examine", ModelUtil.getInt(value, "examine"));
        result.put("userid", userid);
        result.put("issubmit", issubmit);
        return result;
    }

    public List<Map<String, Object>> getContent
            (List<Map<String, Object>> answerList, List<Map<String, Object>> answerDiseaseTemplateList) {
        List<Map<String, Object>> tempList = new ArrayList<>();
        List<Map<String, Object>> mapList = new ArrayList<>();
        long tempId = 0;

        for (Map<String, Object> obj : answerDiseaseTemplateList) {
            Long id = ModelUtil.getLong(obj, "id");
            if (id > 0) {
                if (id != tempId) {
                    tempId = id;
                    Map<String, Object> problemObj = new HashMap<>();
                    problemObj.put("id", id);
                    problemObj.put("templateid", ModelUtil.getLong(obj, "templateid"));
                    problemObj.put("doctortitle", ModelUtil.getStr(obj, "doctortitle"));
                    problemObj.put("checkbox", ModelUtil.getBoolean(obj, "checkbox", false));

                    tempList = new ArrayList<>();
                    Map<String, Object> contentObj = new HashMap<>();
                    if (ModelUtil.getLong(obj, "answerid") > 0) {
                        contentObj.put("anscontent", ModelUtil.getStr(obj, "content"));
                        contentObj.put("useranswerid", ModelUtil.getLong(obj, "answerid"));
                        tempList.add(contentObj);
                    }
                    problemObj.put("diseaselist", tempList);
                    mapList.add(problemObj);
                } else {
                    Map<String, Object> contentObj = new HashMap<>();
                    if (ModelUtil.getLong(obj, "answerid") > 0) {
                        contentObj.put("anscontent", ModelUtil.getStr(obj, "content"));
                        contentObj.put("useranswerid", ModelUtil.getLong(obj, "answerid"));
                        tempList.add(contentObj);
                    }
                }
            }
        }

        for (Map<String, Object> map : answerList) {
            int contenttype = ModelUtil.getInt(map, "contenttype");
            if (QAContentTypeEnum.DiseaseDoctor.getCode() == contenttype) {
                map.put("content", mapList);
            }
        }
        return answerList;
    }

    public Map<String, Object> getUserAnswerDetailed(long orderId) {
        Map<String, Object> userAnswerDetailed = answerMapper.getUserAnswerDetailed(orderId);
        if (userAnswerDetailed != null) {
            List<Map<String, Object>> list = answerMapper.getAnswerDisease(orderId);
            if (list.size() > 0) {
                userAnswerDetailed.put("diseaselist", list);
            } else {
                userAnswerDetailed.put("diseaselist", new ArrayList<>());
            }
            Map<String, Object> doctor = answerMapper.getDoctor(ModelUtil.getLong(userAnswerDetailed, "doctorid"));
            userAnswerDetailed.put("picturelist", answerMapper.getPictureList(orderId, OrderTypeEnum.Answer.getCode()));
            userAnswerDetailed.put("doctor", doctor);
            String diagnosis = ModelUtil.getStr(userAnswerDetailed, "diagnosis");
            int status = ModelUtil.getInt(userAnswerDetailed, "status");
            int visitcategory = ModelUtil.getInt(userAnswerDetailed, "visitcategory");
            //是否填写诊后指导
            userAnswerDetailed.put("guidance", 0);
            String tips = "";

            if (status == AnswerOrderStateEnum.OrderSuccess.getCode()) {
                Map<String, Object> prescription = answerMapper.getPrescriptionByOrderId(orderId);
                if (StrUtil.isEmpty(diagnosis) && prescription == null) {
                    userAnswerDetailed.put("guidance", 1);
                } else {
                    userAnswerDetailed.put("guidance", 2);
                }

                if (visitcategory == VisitCategoryEnum.Outpatient.getCode()) {
                    tips = TextFixed.userOutpatientSuccessTips;
                } else {
                    tips = TextFixed.userGraphicSuccessTips;
                }
                userAnswerDetailed.put("statusname", "已完成");
            } else if (status == AnswerOrderStateEnum.WaitRefund.getCode() || status == AnswerOrderStateEnum.OrderFail.getCode()) {
                userAnswerDetailed.put("status", AnswerOrderStateEnum.OrderFail.getCode());
                userAnswerDetailed.put("statusname", "交易失败");
                if (visitcategory == VisitCategoryEnum.Outpatient.getCode()) {
                    tips = TextFixed.userOutpatientFailTips;
                } else {
                    tips = TextFixed.userGraphicFailTips;
                }
            } else if (status == AnswerOrderStateEnum.Paid.getCode()) {
                if (visitcategory == VisitCategoryEnum.Outpatient.getCode()) {
                    tips = TextFixed.userOutpatientPaidTips;
                } else {
                    tips = TextFixed.userGraphicPaidTips;
                }
                userAnswerDetailed.put("statusname", "待接诊");
            } else if (status == AnswerOrderStateEnum.WaitReply.getCode()) {
                if (visitcategory == VisitCategoryEnum.Outpatient.getCode()) {
                    tips = TextFixed.userOutpatientInCallTips;
                } else {
                    tips = TextFixed.userGraphicInCallTips;
                }
                userAnswerDetailed.put("statusname", "进行中");
            }
            userAnswerDetailed.put("tips", tips);
        }
        return userAnswerDetailed;
    }

    public Map<String, Object> getUserPhoneDetailed(long orderId) {
        Map<String, Object> userPhoneDetailed = answerMapper.getUserPhoneDetailed(orderId);
        if (userPhoneDetailed != null) {
            List<Map<String, Object>> list = answerMapper.getPhoneDisease(orderId);
            if (list.size() > 0) {
                userPhoneDetailed.put("diseaselist", list);
            } else {
                userPhoneDetailed.put("diseaselist", new ArrayList<>());
            }
            Map<String, Object> doctor = answerMapper.getDoctor(ModelUtil.getLong(userPhoneDetailed, "doctorid"));
            userPhoneDetailed.put("picturelist", answerMapper.getPictureList(orderId, OrderTypeEnum.Phone.getCode()));
            userPhoneDetailed.put("doctor", doctor);
            String diagnosis = ModelUtil.getStr(userPhoneDetailed, "diagnosis");
            int status = ModelUtil.getInt(userPhoneDetailed, "status");
            int visitcategory = ModelUtil.getInt(userPhoneDetailed, "visitcategory");
            //是否填写诊后指导
            userPhoneDetailed.put("guidance", 0);

            String tips = "";
            if (status == PhoneOrderStateEnum.OrderSuccess.getCode()) {
                if (StrUtil.isEmpty(diagnosis)) {
                    userPhoneDetailed.put("guidance", 1);
                } else {
                    userPhoneDetailed.put("guidance", 2);
                }
                if (visitcategory == VisitCategoryEnum.department.getCode()) {
                    tips = TextFixed.userDepartmentSuccessTips;
                } else {
                    tips = TextFixed.userPhoneSuccessTips;
                }
                userPhoneDetailed.put("statusname", "已完成");
            } else if (status == PhoneOrderStateEnum.WaitRefund.getCode() || status == PhoneOrderStateEnum.OrderFail.getCode()) {
                userPhoneDetailed.put("status", PhoneOrderStateEnum.OrderFail.getCode());
                userPhoneDetailed.put("statusname", "交易失败");
                if (visitcategory == VisitCategoryEnum.department.getCode()) {
                    tips = TextFixed.userDepartmentFailTips;
                } else {
                    tips = TextFixed.userPhoneFailTips;
                }
            } else if (status == PhoneOrderStateEnum.Paid.getCode()) {
                if (visitcategory == VisitCategoryEnum.department.getCode()) {
                    tips = TextFixed.userDepartmentPaidTips;
                } else {
                    tips = String.format(TextFixed.userPhonePaidTips, UnixUtil.timeStampDate(ModelUtil.getLong(userPhoneDetailed, "subscribetime"), "yyyy-MM-dd HH:mm"));
                }
                userPhoneDetailed.put("statusname", "待接诊");
            } else if (status == PhoneOrderStateEnum.InCall.getCode()) {
                if (visitcategory == VisitCategoryEnum.department.getCode()) {
                    tips = TextFixed.userDepartmentInCallTips;
                } else {
                    tips = TextFixed.userPhoneInCallTips;
                }
                userPhoneDetailed.put("statusname", "进行中");
            }
            userPhoneDetailed.put("tips", tips);
        }
        return userPhoneDetailed;
    }

    /**
     * 用户订单详情没有历史记录
     *
     * @param orderid
     * @return
     */
    public Map<String, Object> getUserAnswerList(long orderid, int pageindex, int pagesize) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> answerOrder = answerMapper.getAnswerOrder(orderid);
        long doctorid = ModelUtil.getLong(answerOrder, "doctorid");
        long userid = ModelUtil.getLong(answerOrder, "userid");
        int states = ModelUtil.getInt(answerOrder, "states");
        int issubmit = ModelUtil.getInt(answerOrder, "issubmit");
        String diagnosis = ModelUtil.getStr(answerOrder, "diagnosis");
        Map<String, Object> doctor = doctorService.getSimpleDoctor(doctorid);
        if (doctor != null) {
            doctor.put("status", states);
            doctor.put("issubmit", issubmit);
            doctor.put("statusname", AnswerOrderStateEnum.getValue(states).getMessage());
        }
        //问答列表
        List<Map<String, Object>> answerList;
        if (states == 2 || states == 6) {
            //问答列表包涵历史
            answerList = answerMapper.getUserHistoryAnswerList(doctorid, userid, pageindex, pagesize);
        } else {
            //问答列表没有历史
            answerList = answerMapper.getUserCurrentAnswerList(orderid, pageindex, pagesize);
        }

        List<Long> idsDiseaseUser = new ArrayList<>();
        List<Long> idPrescriptionList = new ArrayList<>();
        List<Long> idsUserInfo = new ArrayList<>();
        getIdsByUser(answerList, idsDiseaseUser, idsUserInfo, idPrescriptionList);
        if (idsUserInfo.size() > 0) {
            List<Map<String, Object>> userInfoList = answerMapper.userInfoList(idsUserInfo);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.UserInfo).getContent(answerList, userInfoList);
        }
        if (idsDiseaseUser.size() > 0) {
            //用户显示
            List<Map<String, Object>> userAnswerDiseaseTemplateList = answerMapper.userAnswerDiseaseTemplateList(idsDiseaseUser);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.DiseaseUser).getContent(answerList, userAnswerDiseaseTemplateList);
        }
        if (idPrescriptionList.size() > 0) {
            //处方
            List<Map<String, Object>> prescriptionList = answerMapper.prescriptionList(idPrescriptionList);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.Prescription).getContent(answerList, prescriptionList);
        }
        int guidance = 0;
        if (AnswerOrderStateEnum.OrderSuccess.getCode() == states) {
            if (StrUtil.isEmpty(diagnosis)) {
                guidance = 1;
            } else {
                guidance = 2;
            }
        }
        result.put("guidance", guidance);
        result.put("doctor", doctor);
        result.put("answerlist", answerList);
        result.put("userno", ModelUtil.getStr(answerOrder, "userno"));
        result.put("doccode", ModelUtil.getStr(answerOrder, "doccode"));
        return result;
    }


    private void getIdsByDoctor
            (List<Map<String, Object>> answerList, List<Long> idsUserInfo, List<Long> idsDiseaseDoctor, List<Long> idPrescriptionList) {
        for (Map<String, Object> map : answerList) {
            int contenttype = ModelUtil.getInt(map, "contenttype");
            long id = ModelUtil.getLong(map, "id");
            if (id > 0) {
                if (QAContentTypeEnum.DiseaseDoctor.getCode() == contenttype) {
                    idsDiseaseDoctor.add(id);
                } else if (QAContentTypeEnum.Prescription.getCode() == contenttype || QAContentTypeEnum.InExaminePrescription.getCode() == contenttype || QAContentTypeEnum.PrescriptionFail.getCode() == contenttype) {
                    idPrescriptionList.add(id);
                } else if (QAContentTypeEnum.UserInfo.getCode() == contenttype) {
                    idsUserInfo.add(id);
                }
            }
        }
    }

    private void getIdsByUser
            (List<Map<String, Object>> answerList, List<Long> idsDiseaseUser, List<Long> idsUserInfo, List<Long> idPrescriptionList) {
        for (Map<String, Object> map : answerList) {
            int contenttype = ModelUtil.getInt(map, "contenttype");
            long id = ModelUtil.getLong(map, "id");
            if (id > 0) {
                if (QAContentTypeEnum.DiseaseUser.getCode() == contenttype) {
                    idsDiseaseUser.add(id);
                } else if (QAContentTypeEnum.Prescription.getCode() == contenttype || QAContentTypeEnum.InExaminePrescription.getCode() == contenttype || QAContentTypeEnum.PrescriptionFail.getCode() == contenttype) {
                    idPrescriptionList.add(id);
                } else if (QAContentTypeEnum.UserInfo.getCode() == contenttype) {
                    idsUserInfo.add(id);
                }
            }
        }
    }

    /**
     * app回答
     *
     * @param orderId
     * @param content
     * @param contentType 1 医生 0 用户
     * @return
     */
    public Map<String, Object> addAppAnswer(long orderId, String content, int contentType, int questionaAnswerType) {
        return addAppAnswer(orderId, content, contentType, 0, questionaAnswerType);
    }

    /**
     * 不需要审核
     *
     * @param orderId
     * @param doctorid
     * @param diagnosis
     * @param druglist
     * @param contentType
     * @param questionaAnswerType
     * @return
     */
    /*public Map<String, Object> addAppAnswerPrescription(long orderId, long doctorid, String diagnosis, List<?> druglist, int contentType, int questionaAnswerType) {
        long prescriptionId = prescriptionService.sendPrescription(orderId, doctorid, diagnosis, druglist, OrderTypeEnum.Answer.getCode());
        return addAppAnswer(orderId, String.valueOf(prescriptionId), contentType, 0, questionaAnswerType);
    }*/

    /**
     * 需要审核
     *
     * @param orderId
     * @param doctorid
     * @param diagnosis
     * @param druglist
     * @return
     */
    public Map<String, Object> addAppAnswerPrescriptionExamine(long orderId, long doctorid, String diagnosis, List<?> druglist, int contentType, int questionaAnswerType) {
        Map<String, Object> check = doctorService.getCheck().get(0);
        int onecheck = ModelUtil.getInt(check, "onecheck");//审核是否自动审核
        int twocheck = ModelUtil.getInt(check, "twocheck");//审核是否自动审核
        if (onecheck == 1 && twocheck == 1) {
            contentType = QAContentTypeEnum.Prescription.getCode();
        }
        long prescriptionId = prescriptionService.sendPrescriptionExamine(orderId, doctorid, diagnosis, druglist, OrderTypeEnum.Answer.getCode());
        return addAppAnswer(orderId, String.valueOf(prescriptionId), contentType, 0, questionaAnswerType);
    }

    /**
     * 修改处方
     *
     * @param diagnosis
     * @param druglist
     * @return
     */
    public boolean updateAppAnswerPrescriptionExamine(long preidold, long orderId, long doctorid, String diagnosis, List<?> druglist, int contentType, long answerid) {
        long prescriptionId = prescriptionService.sendPrescriptionExamine(orderId, doctorid, diagnosis, druglist, OrderTypeEnum.Answer.getCode());
        answerMapper.updatePresion(preidold);//修改原处方为已处理
        if (answerid == 0) {
            answerid = ModelUtil.getLong(answerMapper.findDoctorAnswer(preidold), "id");
        }
        Map<String, Object> check = doctorService.getCheck().get(0);
        int onecheck = ModelUtil.getInt(check, "onecheck");//审核是否自动审核
        int twocheck = ModelUtil.getInt(check, "twocheck");//审核是否自动审核
        if (onecheck == 1 && twocheck == 1) {
            contentType = QAContentTypeEnum.Prescription.getCode();
        }
        answerMapper.updateDoctorAnswer(prescriptionId, contentType, answerid);
        return true;
    }

    /**
     * 修改处方
     *
     * @return
     */
    public Map<String, Object> getPrescription(long prescriptionId) {
        return prescriptionService.getPrescription(prescriptionId);
    }

    /**
     * h5回答
     *
     * @param orderId
     * @param content
     * @param contentType
     * @return
     */
    public Map<String, Object> addH5Answer(long orderId, String content, long contenttime, int contentType,
                                           int questionaAnswerType) {
//        String result = getSoundRecording("u0KguVuZvAzql9z8r7ViPb93Te8hqPIr29N9KGgjf3E2fnVKbEwLgynbCDbXDeLm", 406, 1000);
        log.info("content: " + content);
        log.info("contenttype: " + contentType);
        Map<String, Object> problem = answerMapper.getProblem(orderId);
        Map<String, Object> map = new HashMap<>();
        if (problem != null) {
            int states = ModelUtil.getInt(problem, "states");
            if (states == 2 || states == 6) {
                long userid = ModelUtil.getLong(problem, "userid");
                long doctorid = ModelUtil.getLong(problem, "doctorid");
                long id = answerMapper.addAnswer(userid, doctorid, orderId, content, contenttime, contentType, questionaAnswerType);
                if (contentType == QAContentTypeEnum.Voice.getCode()) {
                    String result = getSoundRecording(content, ModelUtil.getLong(problem, "doctorid"), id);
                    if (!"FAIL".equals(result)) {
                        log.info(result);
                        answerMapper.updateAnswer(result, id);
                    }
                }
                map = answerMapper.getDoctorAnswer(id);
            } else {
                throw new ServiceException("订单已关闭");
            }
        } else {
            throw new ServiceException("订单不存在");
        }
        return map;
    }

    /**
     * app回答
     *
     * @param orderId
     * @param content
     * @param contentType
     * @return
     */
    @Transactional
    public Map<String, Object> addAppAnswer(long orderId, String content, int contentType, long contenttime,
                                            int questionaAnswerType) {
        Map<String, Object> problem = answerMapper.getProblem(orderId);
        Map<String, Object> map;
        if (problem != null) {
            int states = ModelUtil.getInt(problem, "states");
            if (states == 2 || states == 6) {
                long userid = ModelUtil.getLong(problem, "userid");
                long doctorid = ModelUtil.getLong(problem, "doctorid");
                int doctoronline = ModelUtil.getInt(problem, "doctoronline");
                addDoctorAnswerPushData(orderId, contentType, questionaAnswerType);
                long id = answerMapper.addAnswer(userid, doctorid, orderId, content, contenttime, contentType, questionaAnswerType);
                //websocket
                sendSocket(id);

                if (questionaAnswerType == 1) {
                    answerMapper.updateAnswer(orderId);
                }
                //当用户发消息且医生不在线
                if (questionaAnswerType == 0 && doctoronline == 0) {
                    answerMapper.updateAnswerread(orderId);
                }
                map = answerMapper.getDoctorAnswer(id);
            } else {
                throw new ServiceException("订单已关闭");
            }
        } else {
            throw new ServiceException("订单不存在");
        }
        return map;
    }


    public void sendSocket(long id) {
        Map<String, Object> doctorAnswer = answerMapper.getDoctorAnswer(id);
        String userno = ModelUtil.getStr(doctorAnswer, "userno");
        String doctorno = ModelUtil.getStr(doctorAnswer, "doctorno");
        long orderId = ModelUtil.getLong(doctorAnswer, "orderid");
        List<Map<String, Object>> userAppendList = getAppendUserSocketAnswerList(orderId, id);
        List<Map<String, Object>> doctorAppendList = getAppendDoctorSocketAnswerList(orderId, id);
        String userContentJson = String.format("%s|%s%s", JsonUtil.getInstance().toJson(userAppendList), orderId, userno);
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
     * 查询详情
     *
     * @param orderId
     * @return
     */
    public Map<String, Object> getUserProblemDetailed(long orderId) {
        Map<String, Object> userUserDetailed = answerMapper.getProblemOrderDetail(orderId);
        if (userUserDetailed != null) {
            List<Map<String, Object>> list = answerMapper.getAnswerDisease(orderId);
            if (list.size() > 0) {
                userUserDetailed.put("diseaselist", list);
            } else {
                userUserDetailed.put("diseaselist", new ArrayList<>());
            }
            Map<String, Object> userInfo = answerMapper.getUserInfo(ModelUtil.getLong(userUserDetailed, "id"));
            userUserDetailed.put("userinfo", userInfo);
            String diagnosis = ModelUtil.getStr(userUserDetailed, "diagnosis");
            int status = ModelUtil.getInt(userUserDetailed, "status");
            //是否填写诊后指导
            String tips = "";
            userUserDetailed.put("guidance", 0);
            if (status == AnswerOrderStateEnum.OrderSuccess.getCode()) {
                if (StrUtil.isEmpty(diagnosis)) {
                    userUserDetailed.put("guidance", 1);
                    tips = TextFixed.doctorGraphicGuidanceFailTips;
                } else {
                    userUserDetailed.put("guidance", 2);
                    tips = TextFixed.doctorGraphicGuidanceSuccessTips;
                }
                userUserDetailed.put("statusname", "已完成");
            } else if (status == AnswerOrderStateEnum.WaitRefund.getCode() || status == AnswerOrderStateEnum.OrderFail.getCode()) {
                userUserDetailed.put("status", AnswerOrderStateEnum.OrderFail.getCode());
                userUserDetailed.put("statusname", "交易失败");
                tips = TextFixed.doctorGraphicFailTips;
            } else if (status == AnswerOrderStateEnum.Paid.getCode()) {
                tips = TextFixed.doctorGraphicPaidTips;
                userUserDetailed.put("statusname", "待接诊");
            } else if (status == AnswerOrderStateEnum.WaitReply.getCode()) {
                tips = TextFixed.doctorGraphicInCallTips;
                userUserDetailed.put("statusname", "进行中");
            }
            userUserDetailed.put("tips", tips);
            userUserDetailed.put("picturelist", doctorVideoMapper.findOrderPhoto(orderId, OrderTypeEnum.Answer.getCode()));//详情照片
        }

        return userUserDetailed;
    }

    /**
     * 发送处方
     *
     * @param orderId
     * @param oftenPrescriptionId
     * @return
     */
    @Transactional
    public Map<String, Object> sendPrescription(long orderId, String presNo, long oftenPrescriptionId) {
        log.info("orderId: " + orderId);
        log.info("prescriptionId: " + oftenPrescriptionId);
        Map<String, Object> problem = answerMapper.getProblem(orderId);
        Map<String, Object> map = new HashMap<>();
        if (problem != null) {
            int states = ModelUtil.getInt(problem, "states");
            if (states == 2 || states == 6) {
                long userId = ModelUtil.getLong(problem, "userid", 0);
                //生成处方
                long prescriptionid = prescriptionService.addPrescriptionByOften(oftenPrescriptionId, presNo, orderId, OrderTypeEnum.Answer.getCode());
                long doctorid = ModelUtil.getLong(problem, "doctorid");
                addDoctorAnswerPushData(orderId, QAContentTypeEnum.InExaminePrescription.getCode(), 1);
                long answerId = answerMapper.addAnswer(userId, doctorid, orderId, String.valueOf(prescriptionid), QAContentTypeEnum.InExaminePrescription.getCode(), 1);
                answerMapper.updateAnswer(orderId);
                //websocket
                sendSocket(answerId);

                map = answerMapper.getDoctorAnswer(answerId);
                if (ModelUtil.getInt(map, "contenttype") == QAContentTypeEnum.Prescription.getCode() || ModelUtil.getInt(map, "contenttype") == QAContentTypeEnum.InExaminePrescription.getCode() || ModelUtil.getInt(map, "contenttype") == QAContentTypeEnum.PrescriptionFail.getCode()) {
                    Map<String, Object> prescription = answerMapper.getPrescription(prescriptionid);
                    map.put("content", prescription);
                }
            } else {
                throw new ServiceException("订单已关闭");
            }
        } else {
            throw new ServiceException("订单不存在");
        }
        return map;
    }

    public boolean confirmCloseOrder(long orderid) {
        Map<String, Object> answerNew = answerMapper.getAnswerNew(orderid);
        if (answerNew != null) {
            long time = ModelUtil.getLong(answerNew, "create_time");
            long nowTime = UnixUtil.getNowTimeStamp();
            long fina = nowTime - time;
            if (fina < TextFixed.doctor_close_time) {
                throw new ServiceException("五分钟内不可重复结束订单");
            }
        }
        addAppAnswer(orderid, TextFixed.answerUserOrderText, QAContentTypeEnum.DoctorClose.getCode(), 0, 1);
        return true;
    }

    /**
     * 用户取消订单
     *
     * @param orderId 订单id
     */
    public boolean closeOrderUserAnswer(long orderId) {
        Map<String, Object> answerOrder = answerMapper.getAnswerOrder(orderId);
        int states = ModelUtil.getInt(answerOrder, "states");
        int paytype = ModelUtil.getInt(answerOrder, "paytype");
        long doctorId = ModelUtil.getLong(answerOrder, "doctorid");
        long userId = ModelUtil.getLong(answerOrder, "userid");
        BigDecimal originalprice = ModelUtil.getDec(answerOrder, "originalprice", BigDecimal.ZERO);
        String orderno = ModelUtil.getStr(answerOrder, "orderno");
        int visitcategory = ModelUtil.getInt(answerOrder, "visitcategory");
        //更改订单为等待退款
        if (states == 2) {
            if (paytype != PayTypeEnum.ZERO.getCode() && paytype != PayTypeEnum.VipFree.getCode() && paytype != PayTypeEnum.VipZero.getCode()) {
                answerMapper.autoCloseProblemOrder(orderId, AnswerOrderStateEnum.WaitRefund.getCode(), "用户取消订单");
            } else {
                answerMapper.autoCloseProblemOrder(orderId, AnswerOrderStateEnum.OrderFail.getCode(), "用户取消订单");
            }
        } else if (states == 6) {
            Map<String, Object> maps = new HashMap<>();
            Map<String, Object> ans = answerMapper.findById(doctorId);
            maps.put("doctor", String.format("%s医生", ModelUtil.getStr(ans, "doc_name")));
            maps.put("money", originalprice);
            try {
                SendShortMsgUtil.sendSms(ConfigModel.ISONLINE, ModelUtil.getStr(ans, "doo_tel"), com.syhdoctor.common.config.ConfigModel.SMS.extract_success_tempid, maps);
            } catch (ClientException e) {
                e.printStackTrace();
            }
            answerMapper.autoCloseProblemOrder(orderId, AnswerOrderStateEnum.OrderSuccess.getCode(), "订单交易完成");
            if (paytype != PayTypeEnum.ZERO.getCode()) {
                //添加医生钱包和交易记录
                doctorWalletService.addDoctorWallet(orderno, visitcategory, doctorId, originalprice);
            }
        }
        answerMapper.updateOrderTemplateChoiceflag(orderId);
        answerMapper.updateAnswers(orderId);//更新消息为已答
        long id = answerMapper.addAnswer(userId, doctorId, orderId, TextFixed.problem_end_tips, 0, QAContentTypeEnum.Tips.getCode(), 2);
        sendSocket(id);
        return true;
    }

    //图文订单完成或者失败推送
    public void answerOrderSuccessFailPushData(String orderNo, TypeNameAppPushEnum typeNameAppPushEnum) {
        Map<String, Object> orderMp = getAnswerOrderByOrderNo(orderNo);

        int orderId = ModelUtil.getInt(orderMp, "id");
        int userId = ModelUtil.getInt(orderMp, "userid");
        int doctorId = ModelUtil.getInt(orderMp, "doctorid");
        String name = ModelUtil.getStr(orderMp, "name");
        int uplatform = ModelUtil.getInt(orderMp, "uplatform");
        int dplatform = ModelUtil.getInt(orderMp, "dplatform");
        String uToken = ModelUtil.getStr(orderMp, "utoken");
        String dToken = ModelUtil.getStr(orderMp, "dtoken");

        String doctorText = "";
        String userText = "";
        String userSysText = "";
        switch (typeNameAppPushEnum) {
            case answerSuccessOrder:
                doctorText = String.format(TextFixed.answerDoctorOrderSuccessPushText, name);
                userText = TextFixed.answerUserOrderSuccessPushText;
                userSysText = TextFixed.answerUserOrderSuccessSystemText;
                break;
            case answerFailOrder:
                return;
            default:
                return;
        }

        //用户端消息开始
        systemService.addPushApp(TextFixed.messageServiceTitle,
                userText,
                typeNameAppPushEnum.getCode(), String.valueOf(orderId), userId, MessageTypeEnum.user.getCode(), uplatform, uToken); //app 用户 push 消息
        systemService.addMessage("", TextFixed.messageServiceTitle,
                MessageTypeEnum.user.getCode(), String.valueOf(orderId),
                typeNameAppPushEnum.getCode(), userId, userSysText, "");//app 用户内推送
        //用户端消息结束

        //医生端消息开始
        systemService.addPushApp(TextFixed.messageServiceTitle,
                doctorText,
                typeNameAppPushEnum.getCode(), String.valueOf(orderId), doctorId, MessageTypeEnum.doctor.getCode(), dplatform, dToken); //app 医生 push 消息
        systemService.addMessage("", TextFixed.messageServiceTitle,
                MessageTypeEnum.doctor.getCode(), String.valueOf(orderId),
                typeNameAppPushEnum.getCode(), doctorId, doctorText, "");//app 医生 内推送
    }

    public boolean userAgreenCloseOrder(long orderid, int agree) {
        answerMapper.updateAnswers(orderid);//更新消息为已答
        Map<String, Object> answerOrder = answerMapper.getAnswerOrder(orderid);
        int states = ModelUtil.getInt(answerOrder, "states");
        int paytype = ModelUtil.getInt(answerOrder, "paytype");
        long doctorId = ModelUtil.getLong(answerOrder, "doctorid");
        long userId = ModelUtil.getLong(answerOrder, "userid");
        BigDecimal originalprice = ModelUtil.getDec(answerOrder, "originalprice", BigDecimal.ZERO);
        String orderno = ModelUtil.getStr(answerOrder, "orderno");
        int visitcategory = ModelUtil.getInt(answerOrder, "visitcategory");
        String usertext = TextFixed.answerUserNotCloseOrderText;
        int usertype = QAContentTypeEnum.UserNotAgreenCloseToUser.getCode();
        String doctortext = TextFixed.answerDoctorNotCloseOrderText;
        int doctortype = QAContentTypeEnum.UserNotAgreenCloseToDoctor.getCode();
        if (agree == 1) {//同意
            usertext = TextFixed.answerUserCloseOrderText;
            usertype = QAContentTypeEnum.UserAgreenCloseToUser.getCode();
            doctortext = TextFixed.answerDoctorCloseOrderText;
            doctortype = QAContentTypeEnum.UserAgreenCloseToDoctor.getCode();
            //给用户自己发
            addAppAnswer(orderid, usertext, usertype, 0, 1);
            //给医生发
            addAppAnswer(orderid, doctortext, doctortype, 0, 0);
            //更改订单为等待退款
            if (states == 2) {
                if (paytype != PayTypeEnum.ZERO.getCode() && paytype != PayTypeEnum.VipFree.getCode() && paytype != PayTypeEnum.VipZero.getCode()) {
                    answerMapper.autoCloseProblemOrder(orderid, AnswerOrderStateEnum.WaitRefund.getCode(), "医生关闭订单");
                } else {
                    answerMapper.autoCloseProblemOrder(orderid, AnswerOrderStateEnum.OrderFail.getCode(), "医生关闭订单");
                }
            } else if (states == 6) {
                Map<String, Object> maps = new HashMap<>();
                Map<String, Object> ans = answerMapper.findById(doctorId);
                maps.put("doctor", String.format("%s医生", ModelUtil.getStr(ans, "doc_name")));
                maps.put("money", originalprice);
                try {
                    SendShortMsgUtil.sendSms(ConfigModel.ISONLINE, ModelUtil.getStr(ans, "doo_tel"), com.syhdoctor.common.config.ConfigModel.SMS.extract_success_tempid, maps);
                } catch (ClientException e) {
                    e.printStackTrace();
                }
                answerMapper.autoCloseProblemOrder(orderid, AnswerOrderStateEnum.OrderSuccess.getCode(), "订单交易完成");
                if (paytype != PayTypeEnum.ZERO.getCode()) {
                    //添加医生钱包和交易记录
                    doctorWalletService.addDoctorWallet(orderno, visitcategory, doctorId, originalprice);
                }
            }
            answerMapper.updateOrderTemplateChoiceflag(orderid);
            long id = answerMapper.addAnswer(userId, doctorId, orderid, TextFixed.problem_end_tips, 0, QAContentTypeEnum.Tips.getCode(), 2);
            sendSocket(id);
        } else {
            //给用户自己发
            addAppAnswer(orderid, usertext, usertype, 0, 1);
            //给医生发
            addAppAnswer(orderid, doctortext, doctortype, 0, 0);
        }
        return true;
    }

    @Autowired
    private WxMpService wxMpService;

    public String getMedia(String fileuri, String key, String mediaId) {
        try {
            File file = wxMpService.getMaterialService().mediaDownload(mediaId);
            FileUtil.copyFile(file, fileuri + ".amr");
            FileUtil.changeToMp3(ConfigModel.BASEFILEPATH, fileuri + ".amr", fileuri + ".mp3");
            FileUtil.delFile(file);
            return QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, key + ".mp3", new FileInputStream(new File(fileuri + ".mp3")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "FAIL";
    }


    /**
     * 拉取微信语音
     *
     * @param mediaId
     * @param doctorId
     * @param answerId
     * @return
     */
    public String getSoundRecording(String mediaId, long doctorId, long answerId) {
        log.info("mediaId" + mediaId);
        String key = String.format("answer_%s_%s_%s", doctorId, answerId, UnixUtil.getNowTimeStamp());
        String filePath = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_MEDIA_PATH, key);
        String result = "FAIL";
        if (!StrUtil.isEmpty(mediaId)) {
            result = getMedia(filePath, key, mediaId);
        }
        log.info("result" + result);
        if ("FAIL".equals(result)) {
            return result;
        } else {
            return key + ".mp3";
        }
    }


    /**
     * 常见病症列表
     *
     * @return
     */
    public List<Map<String, Object>> getSymptomsList(long typeId, int symptomstype) {
        List<Map<String, Object>> symptomsList;
        if (symptomstype == 0) {
            symptomsList = answerMapper.getSymptomsOrderBySortList(typeId);
        } else {
            symptomsList = answerMapper.getSymptomsList(typeId);
        }
        Map<String, Object> otherSymptoms = answerMapper.getOtherSymptoms(typeId);
        symptomsList.add(otherSymptoms);
        return symptomsList;
    }

    /**
     * 常见病症列表
     *
     * @return
     */
    public Map<String, Object> getSymptomsLists(long typeId, int symptomstype) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> symptomsList;
        if (symptomstype == 0) {
            symptomsList = answerMapper.getSymptomsOrderBySortList(typeId);
        } else {
            symptomsList = answerMapper.getSymptomsList(typeId);
        }
        Map<String, Object> otherSymptoms = answerMapper.getOtherSymptoms(typeId);
        symptomsList.add(otherSymptoms);
        result.put("symptomsList", symptomsList);
        result.put("backUrl", ModelUtil.getStr(answerMapper.getSpeci(typeId), "backgroundpicture"));
        return result;
    }

    /**
     * 常见病症列表带层级
     *
     * @return
     */
    public List<Map<String, Object>> getAppSymptomsTypeListByDoctorId(long doctorId) {
        //数据源
        List<Map<String, Object>> symptomsTypeList = getSymptomsTypeList(answerMapper.getSymptomsTypeListByDoctorId(doctorId));
        if (symptomsTypeList.size() == 0) {
            return getSymptomsTypeList(answerMapper.getSymptomsTypeListByOther());
        }
        return symptomsTypeList;
    }


    /**
     * 专病咨询 常见病症列表带层级
     * params scid 专病咨询id
     *
     * @return
     */
    public List<Map<String, Object>> getCounselingSymptomsTypeList(long scid) {
        //数据源
        List<Map<String, Object>> symptomsTypeList = getSymptomsTypeList(answerMapper.getCounselingSymptomsTypeList(scid));
        if (symptomsTypeList.size() == 0) {
            return getSymptomsTypeList(answerMapper.getSymptomsTypeListByOther());
        }
        return symptomsTypeList;
    }

    /**
     * 专科咨询 常见病症列表带层级
     * params typeId 症状类型id
     *
     * @return
     */
    public List<Map<String, Object>> getAppSymptomsTypeList(long typeId) {
        //数据源
        List<Map<String, Object>> symptomsTypeList = answerMapper.getSymptomsTypeList(typeId);
        return getSymptomsTypeList(symptomsTypeList);
    }

    /**
     * 常见病症列表带层级
     *
     * @return
     */
    public List<Map<String, Object>> getAppSymptomsTypeList() {
        //数据源
        List<Map<String, Object>> symptomsTypeList = answerMapper.getSymptomsTypeList();
        return getSymptomsTypeList(symptomsTypeList);
    }

    private List<Map<String, Object>> getSymptomsTypeList(List<Map<String, Object>> symptomsTypeList) {
        //分类集合
        List<Map<String, Object>> typeList = new ArrayList<>();
        //病症集合
        List<Map<String, Object>> symptomsList = new ArrayList<>();
        if (symptomsTypeList.size() > 0) {
            //用户对象
            long key = 0;
            for (Map map : symptomsTypeList) {
                Map<String, Object> symptomsMap = new HashMap<>();
                long typeid = ModelUtil.getLong(map, "typeid");
                String typename = ModelUtil.getStr(map, "typevalue");
                if (typeid != key) {
                    key = typeid;
                    //分类对象
                    Map<String, Object> typeMap = new HashMap<>();
                    symptomsList = new ArrayList<>();
                    typeMap.put("typeid", key);
                    typeMap.put("typename", typename);
                    typeMap.put("symptomslist", symptomsList);
                    symptomsMap.put("id", ModelUtil.getLong(map, "id"));
                    symptomsMap.put("value", ModelUtil.getStr(map, "value"));
                    symptomsList.add(symptomsMap);
                    typeList.add(typeMap);
                } else {
                    symptomsMap.put("id", ModelUtil.getLong(map, "id"));
                    symptomsMap.put("value", ModelUtil.getStr(map, "value"));
                    symptomsList.add(symptomsMap);
                }
            }
        }
        return typeList;
    }

    /**
     * 常见病症列表
     *
     * @return
     */
    public List<Map<String, Object>> getAdminSymptomsList(long typeId) {
        List<Map<String, Object>> symptomsList = answerMapper.getSymptomsList(typeId);
        Map<String, Object> otherSymptoms = answerMapper.getOtherSymptoms(typeId);
        symptomsList.add(otherSymptoms);
        return symptomsList;
    }

    /**
     * 常见病症
     *
     * @return
     */
    public List<Map<String, Object>> getAdminUserSymptomsList(long userId) {
        return answerMapper.getAdminSymptomsList(userId);
    }

    /**
     * 症状列表
     *
     * @param userid
     * @return
     */
    public List<Map<String, Object>> getDiseaseName(long userid) {
        return answerMapper.getDiseaseName(userid);
    }

    /**
     * 常见病症
     *
     * @return
     */
    public List<Map<String, Object>> basicsList() {
        return answerMapper.basicsList();
    }

    /**
     * @param orderId
     * @return
     */
    public boolean addProblemTemplate(long orderId) {
        //模板列表
        List<Map<String, Object>> templateList = answerMapper.getTemplateList();
        Map<String, Object> problem = answerMapper.getProblem(orderId);
        long userId = ModelUtil.getLong(problem, "userid");
        long doctorId = ModelUtil.getLong(problem, "doctorid");
        //添加订单问答
        answerMapper.addAnswer(userId, doctorId, orderId, TextFixed.problem_start_tips, QAContentTypeEnum.Tips.getCode(), 2);
        answerMapper.addAnswer(userId, doctorId, orderId, String.valueOf(userId), QAContentTypeEnum.UserInfo.getCode(), 0);
        answerMapper.addAnswer(userId, doctorId, orderId, TextFixed.problem_user_tips, QAContentTypeEnum.UserTips.getCode(), 2);
        for (Map<String, Object> templateMap : templateList) {
            long templateId = ModelUtil.getLong(templateMap, "id");
            String usertitle = ModelUtil.getStr(templateMap, "usertitle");
            String doctortitle = ModelUtil.getStr(templateMap, "doctortitle");
            int checkbox = ModelUtil.getInt(templateMap, "checkbox");
            //添加订单模板问题
            long problemTemplateId = answerMapper.addProblemTemplate(orderId, usertitle, doctortitle, checkbox);
            answerMapper.addAnswer(userId, doctorId, orderId, String.valueOf(problemTemplateId), QAContentTypeEnum.DiseaseUser.getCode(), 2);
            List<Map<String, Object>> templateAnswerList = answerMapper.getTemplateAnswerList(templateId);
            for (Map<String, Object> answerMap : templateAnswerList) {
                //添加模板问题答案
                answerMapper.addProblemTemplateAnswer(problemTemplateId, ModelUtil.getStr(answerMap, "content"));
            }
        }
        return true;
    }

    /**
     * 将系统模板问题答案cp订单对应的问题和答案表中
     *
     * @param orderId
     * @return
     */
    public boolean addProblemTemplateNew(long orderId) {
        //模板列表
        List<Map<String, Object>> templateList = answerMapper.getTemplateList();
        Map<String, Object> problem = answerMapper.getProblem(orderId);
        long userId = ModelUtil.getLong(problem, "userid");
        long doctorId = ModelUtil.getLong(problem, "doctorid");
        String docname = ModelUtil.getStr(problem, "docname");
        String workinstname = ModelUtil.getStr(problem, "workinstname");
        //添加订单问答
        answerMapper.addAnswer(userId, doctorId, orderId, TextFixed.problem_start_tips, QAContentTypeEnum.Tips.getCode(), 2);
        answerMapper.addAnswer(userId, doctorId, orderId, String.valueOf(userId), QAContentTypeEnum.UserInfo.getCode(), 0);
        answerMapper.addAnswer(userId, doctorId, orderId, String.format(TextFixed.problem_doctor_default, workinstname, docname), QAContentTypeEnum.Text.getCode(), 1);
        answerMapper.addAnswer(userId, doctorId, orderId, TextFixed.problem_user_tips, QAContentTypeEnum.UserTips.getCode(), 2);
        for (Map<String, Object> templateMap : templateList) {
            long templateId = ModelUtil.getLong(templateMap, "id");
            String usertitle = ModelUtil.getStr(templateMap, "usertitle");
            String doctortitle = ModelUtil.getStr(templateMap, "doctortitle");
            int checkbox = ModelUtil.getInt(templateMap, "checkbox");
            //添加订单模板问题
            long problemTemplateId = answerMapper.addProblemTemplate(orderId, usertitle, doctortitle, checkbox);
            answerMapper.addAnswer(userId, doctorId, orderId, String.valueOf(problemTemplateId), QAContentTypeEnum.DiseaseUser.getCode(), 2);
            List<Map<String, Object>> templateAnswerList = answerMapper.getTemplateAnswerList(templateId);
            for (Map<String, Object> answerMap : templateAnswerList) {
                //添加模板问题答案
                answerMapper.addProblemTemplateAnswer(problemTemplateId, ModelUtil.getStr(answerMap, "content"));
            }
        }
        answerMapper.addAnswer(userId, doctorId, orderId, TextFixed.problem_submit_tips, QAContentTypeEnum.Submit.getCode(), 2);
        return true;
    }

    /**
     * @param templateId
     * @param answerId
     * @return
     */
    public Map<String, Object> addUserAnser(long orderId, long templateId, long answerId) {
        Map<String, Object> result = new HashMap<>();
        int i = 1;
        Map<String, Object> problem = answerMapper.getProblem(orderId);
        Map<String, Object> problemTemplateOrder = answerMapper.getProblemTemplateOrder(templateId);
        if (problem != null && (ModelUtil.getInt(problemTemplateOrder, "states") == 2 || ModelUtil.getInt(problemTemplateOrder, "states") == 6)) {
            long userid = ModelUtil.getLong(problem, "userid");
            long doctorid = ModelUtil.getLong(problem, "doctorid");
            //判断该答案是否已经存在
            long userAnserCount = answerMapper.getUserAnserCount(templateId, answerId);
            if (userAnserCount == 0) {
                //模板详情
                Map<String, Object> problemTemplate = answerMapper.getProblemTemplate(templateId);
                //是否多选
                int checkbox = ModelUtil.getInt(problemTemplate, "checkbox");
                //该模板回答次数
                long userAnserCount1 = answerMapper.getUserAnserCount(templateId);
                if (checkbox == 1 || userAnserCount1 == 0) {
                    long answerCount = answerMapper.getAnswerCount(orderId);
                    if (answerCount == 0) {
                        //添加订单问答
                        long id = answerMapper.addAnswer(userid, doctorid, orderId, TextFixed.problem_doctor_tips, QAContentTypeEnum.DoctorTips.getCode(), 2);

                        //websocket
                        result.put("answerid", id);
                    }
                    answerMapper.delAnswer(orderId, String.valueOf(templateId));
                    //添加消息
                    long id1 = answerMapper.addAnswer(userid, doctorid, orderId, String.valueOf(templateId), QAContentTypeEnum.DiseaseDoctor.getCode(), 2);
                    //websocket
                    result.put("answerid1", id1);
                    //添加回答
                    answerMapper.addUserAnser(templateId, answerId);
                    //将回答状态改为已经回答过
                    answerMapper.updateProblemTemplate(templateId);
                } else {
                    i = -2;
                }
            } else {
                i = -1;
            }
        } else {
            i = -3;
        }
        result.put("status", i);
        return result;
    }

    /**
     * 用户回答模板问题
     *
     * @return
     */
    public int addUserAnser(long orderId, long templateId, List<?> answerList) {
        int i = 1;
        Map<String, Object> problem = answerMapper.getProblem(orderId);
        Map<String, Object> problemTemplate = answerMapper.getProblemTemplate(templateId);
        //是否多选
        int checkbox = ModelUtil.getInt(problemTemplate, "checkbox");
        if (checkbox == 0 && answerList.size() > 0) {
            return -2;
        }
        if (problem != null && (ModelUtil.getInt(problem, "states") == 2 || ModelUtil.getInt(problem, "states") == 6)) {
            answerMapper.delUserAnser(templateId);
            for (Object key : answerList) {
                long answerId = ModelUtil.strToLong(String.valueOf(key), 0);
                //添加回答
                answerMapper.addUserAnser(templateId, answerId);
            }
        } else {
            i = -3;
        }
        return i;
    }

    /**
     * 用户回答模板问题
     *
     * @return
     */
    public int submitUserAnser(long orderId, List<?> templatelist) {
        int i = 1;
        Map<String, Object> problem = answerMapper.getProblem(orderId);
        if (ModelUtil.getInt(problem, "issubmit") == 1) {
            throw new ServiceException("不能重复提交");
        }
        long userid = ModelUtil.getLong(problem, "userid");
        long doctorid = ModelUtil.getLong(problem, "doctorid");
        //订单已经回答过
        answerMapper.updateProblemOrder(orderId);
        //添加消息
        answerMapper.addAnswer(userid, doctorid, orderId, "", QAContentTypeEnum.DiseaseDoctor.getCode(), 2);
        return i;
    }

    /**
     * 问诊订单详细
     *
     * @param id
     * @return
     */
    public Map<String, Object> getAnswerOrder(long id) {
        Map<String, Object> answerOrder = answerMapper.getAnswerOrder(id);
        if (answerOrder != null) {
            answerOrder.put("user", userService.getUser(ModelUtil.getLong(answerOrder, "userid")));
            answerOrder.put("doctor", doctorService.getSimpleDoctor(ModelUtil.getLong(answerOrder, "doctorid")));
            answerOrder.put("diseaselist", answerMapper.getOrderDiseaseList(ModelUtil.getLong(answerOrder, "id")));
            answerOrder.put("paytype", PayTypeEnum.getValue(ModelUtil.getInt(answerOrder, "paytype")).getMessage());
            answerOrder.put("paystatus", ModelUtil.getInt(answerOrder, "paystatus") == 1 ? "已支付" : "未支付");
            answerOrder.put("prescriptionlist", answerMapper.getPrescriptionList(ModelUtil.getLong(answerOrder, "id")));
            answerOrder.put("disease", answerMapper.findDepartType(ModelUtil.getLong(answerOrder, "id")));
        }
        return answerOrder;
    }

    public Map<String, Object> getUser(long userId) {
        return userService.getUser(userId);
    }

    /**
     * 问诊订单详细
     *
     * @param doctorId
     * @return
     */
    public Map<String, Object> getAnswerPrice(long doctorId) {
        return answerMapper.getAnswerPrice(doctorId);
    }

    /**
     * 电话订单详细
     *
     * @param
     * @return
     */
    public Map<String, Object> getPhonePrice(long doctorid) {
        return answerMapper.getPhonePrice(doctorid);
    }

    /**
     * 电话订单详细
     *
     * @param
     * @return
     */
    public Map<String, Object> getVideoPrice(long doctorid) {
        return answerMapper.getVideoPrice(doctorid);
    }

    /**
     * 急诊价格
     *
     * @param
     * @return
     */
    public Map<String, Object> getDepartmentPrice() {
        return answerMapper.getDepartmentPrice();
    }

    /**
     * 门诊价格
     *
     * @param
     * @return
     */
    public Map<String, Object> getOutpatientPrice() {
        return answerMapper.getOutpatientPrice();
    }


    /**
     * 模板列表
     *
     * @param usertitle
     * @param doctortitle
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getDiseaseTemplateList(String usertitle, String doctortitle, int pageIndex,
                                                            int pageSize) {
        return answerMapper.getDiseaseTemplateList(usertitle, doctortitle, pageIndex, pageSize);
    }

    /**
     * @param usertitle
     * @param doctortitle
     * @return
     */
    public long getDiseaseTemplateCount(String usertitle, String doctortitle) {
        return answerMapper.getDiseaseTemplateCount(usertitle, doctortitle);
    }

    /**
     * 模板详细
     *
     * @param id
     * @return
     */
    public Map<String, Object> getDiseaseTemplate(long id) {
        Map<String, Object> diseaseTemplate = answerMapper.getDiseaseTemplate(id);
        if (diseaseTemplate != null) {
            List<Map<String, Object>> templateAnswerList = answerMapper.getTemplateAnswerList(id);
            if (null != templateAnswerList) {
                for (Map<String, Object> map : templateAnswerList) {
                    map.put("isedit", 0);
                }
            }
            diseaseTemplate.put("answerlist", templateAnswerList);
        }
        return diseaseTemplate;
    }

    /**
     * 删除答案
     *
     * @param id
     * @return
     */
    public long deleteAnswer(long id) {
        return answerMapper.deleteAnwser(id);
    }

    /**
     * 添加修改模板
     *
     * @param id
     * @param usertitle
     * @param doctortitle
     * @param sort
     * @param checkbox
     * @param createUser
     * @return
     */
    public boolean addUpdateDiseaseTemplate(long id, String usertitle, String doctortitle, int sort,
                                            int checkbox, List<?> answerlist, long createUser) {
        if (id == 0) {
            id = answerMapper.addDiseaseTemplate(usertitle, doctortitle, sort, checkbox, createUser);
        } else {
            answerMapper.updateDiseaseTemplate(id, usertitle, doctortitle, sort, checkbox, createUser);
            answerMapper.delTemplateAnswer(id, createUser);
        }
        for (Object object : answerlist) {
            Map<?, ?> map = (Map<?, ?>) object;
            String content = ModelUtil.getStr(map, "content");
            answerMapper.addTemplateAnswer(id, content, createUser);
        }
        return false;
    }

    /**
     * 删除模板
     *
     * @param id
     * @param createUser
     * @return
     */
    public boolean delDiseaseTemplate(long id, long createUser) {
        answerMapper.delDiseaseTemplate(id, createUser);
        answerMapper.delTemplateAnswer(id, createUser);
        return true;
    }

    public Map<String, Object> getUserAnswerOrder(long userId, long doctorid) {
        return answerMapper.getUserDoctorAnswerOrder(userId, doctorid);
    }

    public List<Map<String, Object>> getDoctorVideoSchedulingList(long doctorId) {
        List<Map<String, Object>> doctorSchedulingList = answerMapper.getDoctorVideoSchedulingList(doctorId);
        List<Map<String, Object>> tempList = new ArrayList<>();
        List<Map<String, Object>> resultList = new ArrayList<>();
        String key = "";
        for (Map<String, Object> map : doctorSchedulingList) {
            String daytime = ModelUtil.getStr(map, "daytime");
            if (!key.equals(daytime)) {
                key = daytime;
                Map<String, Object> dayObj = new HashMap<>();
                dayObj.put("daytime", daytime);
                tempList = new ArrayList<>();
                Map<String, Object> contentObj = new HashMap<>();
                contentObj.put("id", ModelUtil.getLong(map, "id"));
                contentObj.put("startendtime", ModelUtil.getStr(map, "starttime"));
                contentObj.put("issubscribe", ModelUtil.getInt(map, "issubscribe"));
                tempList.add(contentObj);
                dayObj.put("timelist", tempList);
                resultList.add(dayObj);
            } else {
                Map<String, Object> contentObj = new HashMap<>();
                contentObj.put("id", ModelUtil.getLong(map, "id"));
                contentObj.put("startendtime", ModelUtil.getStr(map, "starttime"));
                contentObj.put("issubscribe", ModelUtil.getInt(map, "issubscribe"));
                tempList.add(contentObj);
            }
        }
        return resultList;
    }

    public List<Map<String, Object>> getDoctorPhoneSchedulingList(long doctorId) {
        List<Map<String, Object>> doctorSchedulingList = answerMapper.getDoctorPhoneSchedulingList(doctorId);
        List<Map<String, Object>> tempList = new ArrayList<>();
        List<Map<String, Object>> resultList = new ArrayList<>();
        String key = "";
        for (Map<String, Object> map : doctorSchedulingList) {
            String daytime = ModelUtil.getStr(map, "daytime");
            if (!key.equals(daytime)) {
                key = daytime;
                Map<String, Object> dayObj = new HashMap<>();
                dayObj.put("daytime", daytime);
                tempList = new ArrayList<>();
                Map<String, Object> contentObj = new HashMap<>();
                contentObj.put("id", ModelUtil.getLong(map, "id"));
                contentObj.put("startendtime", ModelUtil.getStr(map, "starttime"));
                contentObj.put("issubscribe", ModelUtil.getInt(map, "issubscribe"));
                tempList.add(contentObj);
                dayObj.put("timelist", tempList);
                resultList.add(dayObj);
            } else {
                Map<String, Object> contentObj = new HashMap<>();
                contentObj.put("id", ModelUtil.getLong(map, "id"));
                contentObj.put("startendtime", ModelUtil.getStr(map, "starttime"));
                contentObj.put("issubscribe", ModelUtil.getInt(map, "issubscribe"));
                tempList.add(contentObj);
            }
        }
        return resultList;
    }

    public Map<String, Object> getUserOrderInfo(long userId, long doctorId, int orderType) {
        Map<String, Object> result = new HashMap<>();
        boolean flag = userService.verifyUser(userId);//信息是否完善
        result.put("isinformation", flag ? 1 : 0);
        Map<String, Object> order = null;
        if (orderType == OrderTypeEnum.Answer.getCode()) {
            if (doctorId > 0) {
                order = answerMapper.getUserDoctorAnswerOrder(userId, doctorId);
            } else {
                order = answerMapper.getUserAnswerOrder(userId);
            }
        } else if (orderType == OrderTypeEnum.Phone.getCode()) {
            if (doctorId > 0) {
                order = answerMapper.getUserDoctorPhoneOrder(userId, doctorId);
            } else {
                order = answerMapper.getUserPhoneOrder(userId);
            }
        }

        if (order != null) {
            result.put("status", 1);
            result.put("orderid", ModelUtil.getLong(order, "id"));
        } else {
            result.put("status", 0);
        }
        result.put("ordertype", orderType);
        return result;
    }

    public Map<String, Object> getOrderView(long userId, long doctorId, int orderType, long typeid, long symptomsid) {
        Map<String, Object> result = new HashMap<>();
        if (typeid > 0) {
            getAppSymptomsTypeList(typeid);
            result.put("symptomstypelist", getAppSymptomsTypeList(typeid));
        } else if (symptomsid > 0) {
            result.put("symptomstypelist", getCounselingSymptomsTypeList(symptomsid));
        } else {
            result.put("symptomstypelist", getAppSymptomsTypeList());
        }
        if (OrderTypeEnum.Phone.getCode() == orderType) {
            result.put("schedulinglist", getDoctorPhoneSchedulingList(doctorId));
        } else if (OrderTypeEnum.Video.getCode() == orderType) {
            result.put("schedulinglist", getDoctorVideoSchedulingList(doctorId));
        }
        result.put("records", userService.userHealthRecords(userId));
        result.put("sicktimelist", answerMapper.getSickTimeList());
        return result;
    }

    public AnswerBean addOrder(long userId, long doctorId, int ordertype, List<?> diseaselist, long schedulingid, String disdescribe, List<?> picturelist, long diseasetimeid, int gohospital, int issuredis) {
        AnswerBean answerBean = new AnswerBean();
        Map<String, Object> user = userService.getUser(userId);
        if (ModelUtil.getInt(user, "id") == 0) {
            throw new ServiceException("用户不存在");
        }
        boolean flag = userService.verifyUser(userId);//信息是否完善
        if (flag) {
            Map<String, Object> order = null;
            VisitCategoryEnum visitCategoryEnum;
            //坐班医生
            Map<String, Object> sittingDoctor;
            long visitingstarttime = 0;
            long visitingendtime = 0;
            if (ordertype == OrderTypeEnum.Answer.getCode()) {
                if (doctorId > 0) {
                    visitCategoryEnum = VisitCategoryEnum.graphic;//医生主页图文
                    order = answerMapper.getUserDoctorAnswerOrder(userId, doctorId);
                } else {
                    if (diseaselist.size() == 0) {
                        throw new ServiceException("请选择症状");
                    }
                    visitCategoryEnum = VisitCategoryEnum.Outpatient;//首页图文
                    order = answerMapper.getUserAnswerOrder(userId);
                    sittingDoctor = doctorService.getAnswerSittingDoctor();//查找坐班医生
                    doctorId = ModelUtil.getLong(sittingDoctor, "doctorid");
                }
            } else if (ordertype == OrderTypeEnum.Phone.getCode()) {
                if (doctorId > 0) {
                    visitCategoryEnum = VisitCategoryEnum.phone;//医生主页电话
                    order = answerMapper.getUserDoctorPhoneOrder(userId, doctorId);
                    //医生主页图文必须选择预约时间 没有症状
                    if (schedulingid > 0) {
                        Map<String, Object> subscribe = answerMapper.getPhoneSubscribe(schedulingid, doctorId);
                        if (subscribe == null) {
                            throw new ServiceException("该预约不存在");
                        }
                        if (ModelUtil.getInt(subscribe, "issubscribe") == 1) {
                            throw new ServiceException("该时间段已经被预约");
                        }
                        visitingstarttime = ModelUtil.getLong(subscribe, "visitingstarttime");
                        visitingendtime = ModelUtil.getLong(subscribe, "visitingendtime");
                    } else {
                        throw new ServiceException("请选择预约时间");
                    }
                } else {
                    visitCategoryEnum = VisitCategoryEnum.department;//首页电话
                    order = answerMapper.getUserPhoneOrder(userId);
                    //首页电话必须选择症状 没有预约时间
                    if (diseaselist.size() == 0) {
                        throw new ServiceException("请选择症状");
                    }
                    sittingDoctor = doctorService.getPhoneSittingDoctor();//查找坐班医生
                    doctorId = ModelUtil.getLong(sittingDoctor, "doctorid");
                }
            } else if (ordertype == OrderTypeEnum.Video.getCode()) {
                visitCategoryEnum = VisitCategoryEnum.video;
                if (schedulingid > 0) {
                    Map<String, Object> subscribe = answerMapper.getVideoSubscribe(schedulingid, doctorId);
                    if (subscribe == null) {
                        throw new ServiceException("该预约不存在");
                    }
                    if (ModelUtil.getInt(subscribe, "issubscribe") == 1) {
                        throw new ServiceException("该时间段已经被预约");
                    }
                    visitingstarttime = ModelUtil.getLong(subscribe, "visitingstarttime");
                    visitingendtime = ModelUtil.getLong(subscribe, "visitingendtime");
                } else {
                    throw new ServiceException("请选择预约时间");
                }
            } else {
                throw new ServiceException("订单类型错误");
            }
            if (order != null) {
                answerBean.setStatus(1);
                answerBean.setOrderid(ModelUtil.getLong(order, "id"));
                answerBean.setUserid(ModelUtil.getLong(order, "userid"));
                answerBean.setDoctorid(ModelUtil.getLong(order, "doctorid"));
                answerBean.setOrderno(ModelUtil.getStr(order, "orderno"));
            } else {
                if (doctorId == 0) {
                    //垫底顾问
                    if (ConfigModel.ISONLINE.equals("1")) {
                        //垫底顾问
                        doctorId = 400;
                    } else {
                        doctorId = 284;
                    }
                }
                if (ordertype == OrderTypeEnum.Answer.getCode()) {
                    answerBean = saveAnswerOrder(userId, doctorId, visitCategoryEnum, diseaselist, disdescribe, picturelist, diseasetimeid, gohospital, issuredis);
                } else if (ordertype == OrderTypeEnum.Phone.getCode()) {
                    answerBean = savePhoneOrder(userId, doctorId, visitCategoryEnum, diseaselist, schedulingid, visitingstarttime, visitingendtime, disdescribe, picturelist, diseasetimeid, gohospital, issuredis);
                } else if (ordertype == OrderTypeEnum.Video.getCode()) {
                    answerBean = saveVideoOrder(userId, doctorId, diseaselist, schedulingid, visitingstarttime, visitingendtime, disdescribe, picturelist, diseasetimeid, gohospital, issuredis);
                }
            }
        } else {
            throw new ServiceException("请先完善信息");
        }
        return answerBean;
    }

    /**
     * @param userId       用户id
     * @param doctorId     医生id
     * @param diseaselist  症状列表
     * @param schedulingid 排班id
     * @return
     */
    private AnswerBean saveVideoOrder(long userId, long doctorId, List<?> diseaselist, long schedulingid, long visitingstarttime, long visitingendtime, String disdescribe, List<?> picturelist, long diseasetimeid, int gohospital, int issuredis) {
        AnswerBean result = new AnswerBean();
        String orderNo = IdGenerator.INSTANCE.nextId();
        Map<String, Object> build = this.doctorPrice.setPriceType(VisitCategoryEnum.video)
                .setUserId(userId)
                .setDoctorId(doctorId)
                .build()
                .result();
        if (ModelUtil.getInt(build, "whetheropen") == 0) {
            throw new ServiceException("医生暂未开通该功能");
        }
        int type = ModelUtil.getInt(build, "type");
        BigDecimal price = ModelUtil.getDec(build, "price", BigDecimal.ZERO);
        BigDecimal marketPrice = ModelUtil.getDec(build, "originalprice", BigDecimal.ZERO);
        BigDecimal originalprice = ModelUtil.getDec(build, "doctorprice", BigDecimal.ZERO);
        Double vipdiscount = ModelUtil.getDouble(build, "vipdiscount", 1);
        log.info("price:" + price);
        log.info("type:" + type);
        long orderId;
        try {
            orderId = answerMapper.addVideoOrder(orderNo, userId, doctorId, price, marketPrice, originalprice, vipdiscount, VisitCategoryEnum.video.getCode(), schedulingid, visitingstarttime, visitingendtime, disdescribe, diseasetimeid, gohospital, issuredis);
        } catch (Exception e) {
            throw new ServiceException("您输入的内容有特殊字符，请检查");
        }

        Map<String, Object> userMp = answerMapper.getUserAccountByUserId(userId);
        String uToken = "";
        int uPlatform = 0;
        if (userMp != null) {
            uToken = ModelUtil.getStr(userMp, "xgtoken");
            uPlatform = ModelUtil.getInt(userMp, "platform");
        }
        Map<String, Object> doctorMp = answerMapper.getDoctorExtendsByDoctorId(doctorId);
        String dToken = "";
        int dPlatform = 0;
        if (doctorMp != null) {
            dToken = ModelUtil.getStr(doctorMp, "xgtoken");
            dPlatform = ModelUtil.getInt(doctorMp, "platform");
        }
        answerMapper.addVideoOrderExtend(orderId, dPlatform, dToken, uPlatform, uToken);

        //添加病症
        if (diseaselist != null) {
            for (Object key : diseaselist) {
                long diseaseId = ModelUtil.strToLong(String.valueOf(key), 0);
                if (diseaseId > 0) {
                    Map<String, Object> symptoms = answerMapper.getSymptoms(diseaseId);
                    answerMapper.addVideoDiseaseid(orderId, diseaseId, ModelUtil.getStr(symptoms, "value"));
                }
            }
        }
        //添加图片
        if (picturelist != null) {
            for (Object key : picturelist) {
                if (key != null && !StrUtil.isEmpty(key.toString())) {
                    answerMapper.addOrderPicture(orderId, key.toString(), OrderTypeEnum.Video.getCode());
                }
            }
        }

        PayTypeEnum payTypeEnum;
        log.info("type>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + type);
        //零元支付成功回调
        if (type == DoctorPrice.ZERO) {
            result.setIsfree(1);
            payTypeEnum = PayTypeEnum.ZERO;
            userVideoService.updateVideoStatusSuccess(orderNo, null, payTypeEnum.getCode(), userId, doctorId, schedulingid);
            userVideoService.addVideoOrderPushData(orderNo);
        } else if (type == DoctorPrice.VIP_Free) {
            result.setIsfree(1);
            payTypeEnum = PayTypeEnum.VipFree;
            userVideoService.updateVideoStatusSuccess(orderNo, null, payTypeEnum.getCode(), userId, doctorId, schedulingid);
            userVideoService.addVideoOrderPushData(orderNo);
        } else if (type == DoctorPrice.VIP_ZERO) {
            result.setIsfree(1);
            payTypeEnum = PayTypeEnum.VipZero;
            long vipid = ModelUtil.getLong(build, "vipid");
            cardService.updateMedicalExpertVideo(vipid);
            userVideoService.updateVideoStatusSuccess(orderNo, null, payTypeEnum.getCode(), userId, doctorId, schedulingid);
            userVideoService.addVideoOrderPushData(orderNo);
        }
        result.setOrderno(orderNo);
        result.setOrderid(orderId);
        result.setPrice(price);
        result.setDoctorid(doctorId);
        result.setUserid(userId);
        result.setStatus(0);
        result.setWalletbalance(ModelUtil.getDec(userMp, "walletbalance", BigDecimal.ZERO));
        result.setDes(TextFixed.video_pay_dec);
        return result;
    }

    /**
     * @param userId   用户id
     * @param doctorId 医生id
     */
    @Transactional
    public AnswerBean savePhoneOrder(long userId, long doctorId, VisitCategoryEnum visitCategoryEnum, List<?> diseaselist, long schedulingid, long visitingstarttime, long visitingendtime, String disdescribe, List<?> picturelist, long diseasetimeid, int gohospital, int issuredis) {
        AnswerBean result = new AnswerBean();
        String orderNo = IdGenerator.INSTANCE.nextId();
        String isonline = ConfigModel.ISONLINE;
        orderNo = isonline + orderNo;
        Map<String, Object> build = this.doctorPrice.setPriceType(visitCategoryEnum)
                .setUserId(userId)
                .setDoctorId(doctorId)
                .build()
                .result();
        int type = ModelUtil.getInt(build, "type");
        BigDecimal price = ModelUtil.getDec(build, "price", BigDecimal.ZERO);
        BigDecimal marketprice = ModelUtil.getDec(build, "originalprice", BigDecimal.ZERO);
        BigDecimal originalprice = ModelUtil.getDec(build, "doctorprice", BigDecimal.ZERO);
        Double vipdiscount = ModelUtil.getDouble(build, "vipdiscount", 1);

        log.info("price:" + price);
        log.info("type:" + type);
        Map<String, Object> user = userService.getUser(userId);
        Map<String, Object> simpleDoctor = doctorService.getSimpleDoctor(doctorId);
        String userPhone = ModelUtil.getStr(user, "phone");
        String doctorPhone = ModelUtil.getStr(simpleDoctor, "phone");
        long orderId;
        try {
            orderId = answerMapper.addPhoneOrder(orderNo, userId, doctorId, userPhone, doctorPhone, price, marketprice, originalprice, vipdiscount, visitCategoryEnum.getCode(), schedulingid, visitingstarttime, visitingendtime, disdescribe, diseasetimeid, gohospital, issuredis);
        } catch (Exception e) {
            throw new ServiceException("您输入的内容有特殊字符，请检查");
        }

        if (diseaselist != null) {
            if (diseaselist.size() > 0) {
                for (Object key : diseaselist) {
                    long diseaseId = ModelUtil.strToLong(String.valueOf(key), 0);
                    if (diseaseId > 0) {
                        Map<String, Object> symptoms = answerMapper.getSymptoms(diseaseId);
                        answerMapper.addPhoneDiseaseid(orderId, diseaseId, ModelUtil.getStr(symptoms, "value"));
                    }
                }
            }
        }

        //添加图片
        if (picturelist != null) {
            for (Object key : picturelist) {
                if (key != null && !StrUtil.isEmpty(key.toString())) {
                    answerMapper.addOrderPicture(orderId, key.toString(), OrderTypeEnum.Phone.getCode());
                }
            }
        }

        Map<String, Object> userMp = answerMapper.getUserAccountByUserId(userId);
        String uToken = "";
        int uPlatform = 0;
        if (userMp != null) {
            uToken = ModelUtil.getStr(userMp, "xgtoken");
            uPlatform = ModelUtil.getInt(userMp, "platform");
        }
        Map<String, Object> doctorMp = answerMapper.getDoctorExtendsByDoctorId(doctorId);
        String dToken = "";
        int dPlatform = 0;
        if (doctorMp != null) {
            dToken = ModelUtil.getStr(doctorMp, "xgtoken");
            dPlatform = ModelUtil.getInt(doctorMp, "platform");
        }
        answerMapper.addPhoneOrderExtend(orderId, dPlatform, dToken, uPlatform, uToken);


        PayTypeEnum payTypeEnum;
        if (type == DoctorPrice.ZERO) {
            payTypeEnum = PayTypeEnum.ZERO;
            result.setIsfree(1);
            updatePhoneStatusSuccess(orderNo, null, payTypeEnum.getCode(), userId, doctorId);
            addPhoneOrderPushData(orderNo);
        } else if (type == DoctorPrice.VIP_Free) {
            payTypeEnum = PayTypeEnum.VipFree;
            result.setIsfree(1);
            updatePhoneStatusSuccess(orderNo, null, payTypeEnum.getCode(), userId, doctorId);
            addPhoneOrderPushData(orderNo);
        } else if (type == DoctorPrice.VIP_ZERO) {
            payTypeEnum = PayTypeEnum.VipZero;
            long vipid = ModelUtil.getLong(build, "vipid");
            if (visitCategoryEnum == VisitCategoryEnum.department) {
                cardService.updateHealthConsultantPhone(vipid);
            } else if (visitCategoryEnum == VisitCategoryEnum.phone) {
                cardService.updateMedicalExpertPhone(vipid);
            }
            result.setIsfree(1);
            updatePhoneStatusSuccess(orderNo, null, payTypeEnum.getCode(), userId, doctorId);
            addPhoneOrderPushData(orderNo);
        }

        result.setOrderno(orderNo);
        result.setOrderid(orderId);
        result.setPrice(price);
        result.setUserid(userId);
        result.setStatus(0);
        result.setDes(TextFixed.phone_pay_dec);
        result.setDoctorid(doctorId);
        result.setWalletbalance(ModelUtil.getDec(userMp, "walletbalance", BigDecimal.ZERO));
        return result;
    }

    /**
     * @param userId   用户id
     * @param doctorId 医生id
     */
    private AnswerBean saveAnswerOrder(long userId, long doctorId, VisitCategoryEnum visitCategoryEnum, List<?> diseaselist, String disdescribe, List<?> picturelist, long diseasetimeid, int gohospital, int issuredis) {
        AnswerBean result = new AnswerBean();
        String orderNo = IdGenerator.INSTANCE.nextId();
        Map<String, Object> build = this.doctorPrice.setPriceType(visitCategoryEnum)
                .setUserId(userId)
                .setDoctorId(doctorId)
                .build()
                .result();
        int type = ModelUtil.getInt(build, "type");
        BigDecimal price = ModelUtil.getDec(build, "price", BigDecimal.ZERO);
        BigDecimal marketPrice = ModelUtil.getDec(build, "originalprice", BigDecimal.ZERO);
        BigDecimal originalprice = ModelUtil.getDec(build, "doctorprice", BigDecimal.ZERO);
        Double vipdiscount = ModelUtil.getDouble(build, "vipdiscount", 1);
        log.info("price:" + price);
        log.info("type:" + type);
        long orderId;
        try {
            orderId = answerMapper.addAnswerOrder(orderNo, userId, doctorId, price, marketPrice, originalprice, vipdiscount, visitCategoryEnum.getCode(), disdescribe, diseasetimeid, gohospital, issuredis);
        } catch (Exception e) {
            throw new ServiceException("您输入的内容有特殊字符，请检查");
        }

        Map<String, Object> userMp = answerMapper.getUserAccountByUserId(userId);
        String uToken = "";
        int uPlatform = 0;
        if (userMp != null) {
            uToken = ModelUtil.getStr(userMp, "xgtoken");
            uPlatform = ModelUtil.getInt(userMp, "platform");
        }
        Map<String, Object> doctorMp = answerMapper.getDoctorExtendsByDoctorId(doctorId);
        String dToken = "";
        int dPlatform = 0;
        if (doctorMp != null) {
            dToken = ModelUtil.getStr(doctorMp, "xgtoken");
            dPlatform = ModelUtil.getInt(doctorMp, "platform");
        }
        answerMapper.addAnswerOrderExtend(orderId, dPlatform, dToken, uPlatform, uToken);


        //添加病症
        if (diseaselist != null) {
            for (Object key : diseaselist) {
                long diseaseId = ModelUtil.strToLong(String.valueOf(key), 0);
                if (diseaseId > 0) {
                    Map<String, Object> symptoms = answerMapper.getSymptoms(diseaseId);
                    answerMapper.addAnswerDiseaseid(orderId, diseaseId, ModelUtil.getStr(symptoms, "value"));
                }
            }
        }


        //添加图片
        if (picturelist != null) {
            for (Object key : picturelist) {
                if (key != null && !StrUtil.isEmpty(key.toString())) {
                    answerMapper.addOrderPicture(orderId, key.toString(), OrderTypeEnum.Answer.getCode());
                }
            }
        }

        //机器人
        addProblemTemplate(orderId);
        PayTypeEnum payTypeEnum;
        if (type == DoctorPrice.ZERO) {
            result.setIsfree(1);
            payTypeEnum = PayTypeEnum.ZERO;
            updateAnswerStatusSuccess(orderNo, null, payTypeEnum.getCode(), userId, doctorId);
            addAnswerOrderPushData(orderNo);
        } else if (type == DoctorPrice.VIP_Free) {
            result.setIsfree(1);
            payTypeEnum = PayTypeEnum.VipFree;
            updateAnswerStatusSuccess(orderNo, null, payTypeEnum.getCode(), userId, doctorId);
            addAnswerOrderPushData(orderNo);
        } else if (type == DoctorPrice.VIP_ZERO) {
            result.setIsfree(1);
            payTypeEnum = PayTypeEnum.VipZero;
            long vipid = ModelUtil.getLong(build, "vipid");
            if (visitCategoryEnum == VisitCategoryEnum.Outpatient) {
                cardService.updateHealthConsultantCeefax(vipid);
            } else if (visitCategoryEnum == VisitCategoryEnum.graphic) {
                cardService.updateMedicalExpertCeefax(vipid);
            }
            updateAnswerStatusSuccess(orderNo, null, payTypeEnum.getCode(), userId, doctorId);
            addAnswerOrderPushData(orderNo);
        }
        result.setOrderno(orderNo);
        result.setOrderid(orderId);
        result.setPrice(price);
        result.setDoctorid(doctorId);
        result.setUserid(userId);
        result.setStatus(0);
        result.setWalletbalance(ModelUtil.getDec(userMp, "walletbalance", BigDecimal.ZERO));
        result.setDes(TextFixed.problem_pay_dec);
        return result;
    }

    /**
     * @param userId
     * @param doctorId
     * @param diseaselist
     * @return
     */
    public AnswerBean addAnswerOrder(long userId, long doctorId, List<?> diseaselist) {
        Map<String, Object> user = userService.getUser(userId);
        if (ModelUtil.getInt(user, "id") == 0) {
            throw new ServiceException("用户不存在");
        }
        AnswerBean answerBean = new AnswerBean();
        boolean flag = userService.verifyUser(userId);//信息是否完善
        if (flag) {
            Map<String, Object> userAnswerOrder;
            if (doctorId > 0) {
                userAnswerOrder = answerMapper.getUserDoctorAnswerOrder(userId, doctorId);
            } else {
                userAnswerOrder = answerMapper.getUserAnswerOrder(userId);
            }
            if (userAnswerOrder != null) {
                answerBean.setStatus(1);
                answerBean.setOrderid(ModelUtil.getLong(userAnswerOrder, "id"));
                answerBean.setUserid(ModelUtil.getLong(userAnswerOrder, "userid"));
                answerBean.setDoctorid(ModelUtil.getLong(userAnswerOrder, "doctorid"));
                answerBean.setOrderno(ModelUtil.getStr(userAnswerOrder, "orderno"));
            } else {
                VisitCategoryEnum department;
                if (doctorId == 0) {
                    department = VisitCategoryEnum.Outpatient;
                    //坐班医生
                    Map<String, Object> answerSittingDoctor = doctorService.getAnswerSittingDoctor();
                    if (answerSittingDoctor != null) {
                        doctorId = ModelUtil.getLong(answerSittingDoctor, "doctorid");
                    } else {
                        //垫底顾问
                        if (ConfigModel.ISONLINE.equals("1")) {
                            //垫底顾问
                            doctorId = 400;
                        } else {
                            doctorId = 284;
                        }
                    }
                } else {
                    department = VisitCategoryEnum.graphic;
                }
                log.info("doctorid==========" + doctorId);
                if (doctorId == 0) {
                    throw new ServiceException("没有医生");
                }
                if (diseaselist.size() == 0) {
                    throw new ServiceException("请选择症状");
                }
                answerBean = saveAnswerOrder(userId, doctorId, department, diseaselist);
            }
        }
        answerBean.setIsinformation(flag ? 1 : 0);
        return answerBean;
    }

    public AnswerBean addAnswerOrderNew(long userId, long doctorId, List<?> diseaselist) {
        Map<String, Object> user = userService.getUser(userId);
        if (ModelUtil.getInt(user, "id") == 0) {
            throw new ServiceException("用户不存在");
        }
        AnswerBean answerBean = new AnswerBean();
        boolean flag = userService.verifyUser(userId);//信息是否完善
        if (flag) {
            Map<String, Object> userAnswerOrder;
            if (doctorId > 0) {
                userAnswerOrder = answerMapper.getUserDoctorAnswerOrder(userId, doctorId);
            } else {
                userAnswerOrder = answerMapper.getUserAnswerOrder(userId);
            }
            if (userAnswerOrder != null) {
                answerBean.setStatus(1);
                answerBean.setOrderid(ModelUtil.getLong(userAnswerOrder, "id"));
                answerBean.setUserid(ModelUtil.getLong(userAnswerOrder, "userid"));
                answerBean.setDoctorid(ModelUtil.getLong(userAnswerOrder, "doctorid"));
                answerBean.setOrderno(ModelUtil.getStr(userAnswerOrder, "orderno"));
            } else {
                VisitCategoryEnum department;
                if (doctorId == 0) {
                    department = VisitCategoryEnum.Outpatient;
                    //坐班医生
                    Map<String, Object> answerSittingDoctor = doctorService.getAnswerSittingDoctor();
                    if (answerSittingDoctor != null) {
                        doctorId = ModelUtil.getLong(answerSittingDoctor, "doctorid");
                    } else {
                        if (ConfigModel.ISONLINE.equals("1")) {
                            //垫底顾问
                            doctorId = 400;
                        } else {
                            doctorId = 284;
                        }
                    }
                } else {
                    department = VisitCategoryEnum.graphic;
                }
                log.info("doctorid==========" + doctorId);
                if (doctorId == 0) {
                    throw new ServiceException("没有医生");
                }
                if (diseaselist.size() == 0) {
                    throw new ServiceException("请选择症状");
                }
                answerBean = saveAnswerOrderNew(userId, doctorId, department, diseaselist);
            }
        }
        answerBean.setIsinformation(flag ? 1 : 0);
        return answerBean;
    }

    public Map<String, Object> getUserDoctorPhoneOrder(long userId, long doctorId) {
        return answerMapper.getUserDoctorPhoneOrder(userId, doctorId);
    }

    public AnswerBean addPhoneOrder(long userId, long doctorId, List<?> diseaselist, long schedulingid) {
        Map<String, Object> user = userService.getUser(userId);
        if (ModelUtil.getInt(user, "id") == 0) {
            throw new ServiceException("用户不存在");
        }
        AnswerBean answerBean = new AnswerBean();
        boolean flag = userService.verifyUser(userId);//信息是否完善
        if (flag) {
            Map<String, Object> userPhoneOrder = answerMapper.getUserDoctorPhoneOrder(userId, doctorId);
            if (userPhoneOrder != null) {
                answerBean.setStatus(1);
                answerBean.setOrderid(ModelUtil.getLong(userPhoneOrder, "id"));
                answerBean.setUserid(ModelUtil.getLong(userPhoneOrder, "userid"));
                answerBean.setDoctorid(ModelUtil.getLong(userPhoneOrder, "doctorid"));
                answerBean.setOrderno(ModelUtil.getStr(userPhoneOrder, "orderno"));
            } else {
                if (diseaselist.size() == 0) {
                    throw new ServiceException("请选择症状");
                }
                answerBean = savePhoneOrder(userId, doctorId, VisitCategoryEnum.phone, diseaselist, schedulingid);
            }
        }
        answerBean.setIsinformation(flag ? 1 : 0);
        return answerBean;
    }

    public AnswerBean addPhoneOrder(long userId, List<?> diseaselist) {
        Map<String, Object> user = userService.getUser(userId);
        if (ModelUtil.getInt(user, "id") == 0) {
            throw new ServiceException("用户不存在");
        }
        AnswerBean answerBean = new AnswerBean();
        boolean flag = userService.verifyUser(userId);//信息是否完善
        if (flag) {
            Map<String, Object> userPhoneOrder = answerMapper.getUserPhoneOrder(userId);
            if (userPhoneOrder != null) {
                answerBean.setStatus(1);
                answerBean.setOrderid(ModelUtil.getLong(userPhoneOrder, "id"));
                answerBean.setUserid(ModelUtil.getLong(userPhoneOrder, "userid"));
                answerBean.setDoctorid(ModelUtil.getLong(userPhoneOrder, "doctorid"));
                answerBean.setOrderno(ModelUtil.getStr(userPhoneOrder, "orderno"));
            } else {
                long doctorId;
                //坐班医生
                Map<String, Object> phoneSittingDoctor = doctorService.getPhoneSittingDoctor();
                if (phoneSittingDoctor != null) {
                    doctorId = ModelUtil.getLong(phoneSittingDoctor, "doctorid");
                } else {
                    if (ConfigModel.ISONLINE.equals("1")) {
                        //垫底顾问
                        doctorId = 400;
                    } else {
                        doctorId = 284;
                    }
                }
                if (doctorId == 0) {
                    throw new ServiceException("没有可用医生");
                }
                if (diseaselist.size() == 0) {
                    throw new ServiceException("请选择症状");
                }
                answerBean = savePhoneOrder(userId, doctorId, VisitCategoryEnum.department, diseaselist, 0);
            }
        }
        answerBean.setIsinformation(flag ? 1 : 0);
        return answerBean;
    }

    public synchronized Map<String, Object> answerWalletPay(long orderId) {
        Map<String, Object> answerOrder = answerMapper.getAnswerOrder(orderId);
        if (ModelUtil.getInt(answerOrder, "paystatus") == 1) {
            throw new ServiceException("不能重复支付");
        }
        BigDecimal walletbalance = ModelUtil.getDec(answerOrder, "walletbalance", BigDecimal.ZERO);
        String orderNo = ModelUtil.getStr(answerOrder, "orderno");
        BigDecimal actualmoney = ModelUtil.getDec(answerOrder, "actualmoney", BigDecimal.ZERO);
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        Map<String, Object> payBean = new HashMap<>();
        log.info("walletbalance===============" + walletbalance);
        log.info("actualmoney===============" + actualmoney);
        if (walletbalance.compareTo(actualmoney) < 0) {
            payBean.put("sufficient", 0);
        } else {
            TransactionTypeStateEnum transactionTypeStateEnum = TransactionTypeStateEnum.Graphic;
            long userId = ModelUtil.getLong(answerOrder, "userid");
            long doctorId = ModelUtil.getLong(answerOrder, "doctorid");
            updateAnswerStatusSuccess(orderNo, null, PayTypeEnum.Wallet.getCode(), userId, doctorId);
            userWalletService.subtractUserWallet(orderNo, transactionTypeStateEnum, userId, actualmoney);
            addAnswerOrderPushData(orderNo);
            payBean.put("sufficient", 1);
            payBean.put("orderid", orderId);
        }
        return payBean;
    }

    public IPayService.PayBean answerAliAppPay(long orderId) {
        // 订单名称，必填
        String subject = TextFixed.problem_pay_dec;
        // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
        String notify_url = ConfigModel.APILINKURL + "aliCallback/answerAliAppNotifyUrl";
        // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
        String return_url = ConfigModel.APILINKURL + "aliCallback/answerAliAppReturnUrl";
        Map<String, Object> answerOrder = answerMapper.getAnswerOrder(orderId);
        String orderNo = ModelUtil.getStr(answerOrder, "orderno");
        BigDecimal actualmoney = ModelUtil.getDec(answerOrder, "actualmoney", BigDecimal.ZERO);
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        long userId = ModelUtil.getLong(answerOrder, "userid");
        return aliAppPayImpl.pay(orderNo, actualmoney, String.valueOf(userId), subject, null, notify_url, return_url);
    }

    public synchronized IPayService.PayBean phoneAliAppPay(long orderId) {
        Map<String, Object> phoneOrder = answerMapper.getDoctorPhoneOrderById(orderId);
        long schedulingid = ModelUtil.getLong(phoneOrder, "schedulingid");
        if (answerMapper.findSchTime(schedulingid) && schedulingid != 0) {
            throw new ServiceException("该时间已被预约，请检查");
        }
        // 订单名称，必填
        String subject = TextFixed.phone_pay_dec;
        // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
        String notify_url = ConfigModel.APILINKURL + "aliCallback/phoneAliAppNotifyUrl";
        // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
        String return_url = ConfigModel.APILINKURL + "aliCallback/phoneAliAppReturnUrl";
        String orderNo = ModelUtil.getStr(phoneOrder, "orderno");
        BigDecimal actualmoney = ModelUtil.getDec(phoneOrder, "actualmoney", BigDecimal.ZERO);
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        long userId = ModelUtil.getLong(phoneOrder, "userid");
        return aliAppPayImpl.pay(orderNo, actualmoney, String.valueOf(userId), subject, null, notify_url, return_url);
    }

    public Map<String, Object> phoneKangyangPay(long orderId) {
        Map<String, Object> phoneOrder = answerMapper.getDoctorPhoneOrderById(orderId);
        String orderNo = ModelUtil.getStr(phoneOrder, "orderno");
        BigDecimal actualmoney = ModelUtil.getDec(phoneOrder, "actualmoney", BigDecimal.ZERO);
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        Map<String, Object> payBean = new HashMap<>();
        log.info("actualmoney===============" + actualmoney);
        TransactionTypeStateEnum transactionTypeStateEnum = TransactionTypeStateEnum.Phone;

        Map<String, Object> params = new HashMap<>();
        params.put("userid", ModelUtil.getStr(phoneOrder, "kangyanguserid"));
        params.put("jumptype", String.valueOf(3));
        params.put("pay_amount", String.valueOf(actualmoney));
        params.put("order_id", ModelUtil.getStr(phoneOrder, "id"));
        String s = JsonUtil.getInstance().gsonToJson(params);
        Map<String, Object> post = HttpUtil.getInstance().post(JumpLink.KANGYANG_PAY_URL, s);
        int retcode = ModelUtil.getInt(post, "retcode");
        if (retcode == 1) {
            long userId = ModelUtil.getLong(phoneOrder, "userid");
            long doctorId = ModelUtil.getLong(phoneOrder, "doctorid");
            updatePhoneStatusSuccess(orderNo, null, PayTypeEnum.Kangyang.getCode(), userId, doctorId);
            addPhoneOrderPushData(orderNo);
            payBean.put("orderid", orderId);
        } else {
            throw new ServiceException(ModelUtil.getStr(ModelUtil.getMap(post, "msg"), "prompt"));
        }
        return payBean;
    }

    public Map<String, Object> answerKangyangPay(long orderId) {
        Map<String, Object> answerOrder = answerMapper.getAnswerOrder(orderId);
        String orderNo = ModelUtil.getStr(answerOrder, "orderno");
        BigDecimal actualmoney = ModelUtil.getDec(answerOrder, "actualmoney", BigDecimal.ZERO);
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        Map<String, Object> payBean = new HashMap<>();
        log.info("actualmoney===============" + actualmoney);

        Map<String, Object> params = new HashMap<>();
        params.put("userid", ModelUtil.getStr(answerOrder, "kangyanguserid"));
        params.put("jumptype", String.valueOf(3));
        params.put("pay_amount", String.valueOf(actualmoney));
        params.put("order_id", ModelUtil.getStr(answerOrder, "id"));
        String s = JsonUtil.getInstance().gsonToJson(params);
        Map<String, Object> post = HttpUtil.getInstance().post(JumpLink.KANGYANG_PAY_URL, s);
        int retcode = ModelUtil.getInt(post, "retcode");
        if (retcode == 1) {
            long userId = ModelUtil.getLong(answerOrder, "userid");
            long doctorId = ModelUtil.getLong(answerOrder, "doctorid");
            updatePhoneStatusSuccess(orderNo, null, PayTypeEnum.Kangyang.getCode(), userId, doctorId);
            addPhoneOrderPushData(orderNo);
            payBean.put("orderid", orderId);
        } else {
            throw new ServiceException(ModelUtil.getStr(ModelUtil.getMap(post, "msg"), "prompt"));
        }
        return payBean;
    }


    public synchronized Map<String, Object> phoneWalletPay(long orderId) {
        Map<String, Object> phoneOrder = answerMapper.getDoctorPhoneOrderById(orderId);
        long schedulingid = ModelUtil.getLong(phoneOrder, "schedulingid");
        if (answerMapper.findSchTime(schedulingid) && schedulingid != 0) {//从首页进来的急诊不需要判断预约时间
            throw new ServiceException("该时间已被预约，请检查");
        }

        if (ModelUtil.getInt(phoneOrder, "paystatus") == 1) {
            throw new ServiceException("不能重复支付");
        }


        BigDecimal walletbalance = ModelUtil.getDec(phoneOrder, "walletbalance", BigDecimal.ZERO);
        String orderNo = ModelUtil.getStr(phoneOrder, "orderno");
        BigDecimal actualmoney = ModelUtil.getDec(phoneOrder, "actualmoney", BigDecimal.ZERO);
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        Map<String, Object> payBean = new HashMap<>();
        log.info("walletbalance===============" + walletbalance);
        log.info("actualmoney===============" + actualmoney);
        if (walletbalance.compareTo(actualmoney) < 0) {
            payBean.put("sufficient", 0);
        } else {
            TransactionTypeStateEnum transactionTypeStateEnum = TransactionTypeStateEnum.Phone;
            long userId = ModelUtil.getLong(phoneOrder, "userid");
            long doctorId = ModelUtil.getLong(phoneOrder, "doctorid");
            updatePhoneStatusSuccess(orderNo, null, PayTypeEnum.Wallet.getCode(), userId, doctorId);
            userWalletService.subtractUserWallet(orderNo, transactionTypeStateEnum, userId, actualmoney);
            addPhoneOrderPushData(orderNo);

            payBean.put("sufficient", 1);
            payBean.put("orderid", orderId);
        }
        return payBean;
    }

    //问诊支付宝web支付
    public IPayService.PayBean answerAliWebPay(long orderId) {
        // 订单名称，必填
        String subject = TextFixed.problem_pay_dec;
        // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
        String notify_url = ConfigModel.APILINKURL + "aliCallback/answerAliWebNotifyUrl";
        // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
        String return_url = ConfigModel.APILINKURL + "aliCallback/answerAliWebReturnUrl";
        Map<String, Object> answerOrder = answerMapper.getAnswerOrder(orderId);
        String orderNo = ModelUtil.getStr(answerOrder, "orderno");
        BigDecimal actualmoney = ModelUtil.getDec(answerOrder, "actualmoney", BigDecimal.ZERO);
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        long userId = ModelUtil.getLong(answerOrder, "userid");
        return aliWebPayImpl.pay(orderNo, actualmoney, String.valueOf(userId), subject, null, notify_url, return_url);
    }

    //急诊支付宝web支付
    public synchronized IPayService.PayBean phoneAliWebPay(long orderId) {
        Map<String, Object> phoneOrder = answerMapper.getDoctorPhoneOrderById(orderId);
        long schedulingid = ModelUtil.getLong(phoneOrder, "schedulingid");
        if (answerMapper.findSchTime(schedulingid)) {
            throw new ServiceException("该时间已被预约，请检查");
        }
        // 订单名称，必填
        String subject = TextFixed.phone_pay_dec;
        // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
        String notify_url = ConfigModel.APILINKURL + "aliCallback/phoneAliWebNotifyUrl";
        // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
        String return_url = ConfigModel.APILINKURL + "aliCallback/phoneAliWebReturnUrl";
        String orderNo = ModelUtil.getStr(phoneOrder, "orderno");
        BigDecimal actualmoney = ModelUtil.getDec(phoneOrder, "actualmoney", BigDecimal.ZERO);
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        long userId = ModelUtil.getLong(phoneOrder, "userid");
        return aliWebPayImpl.pay(orderNo, actualmoney, String.valueOf(userId), subject, null, notify_url, return_url);
    }

    //问诊微信app支付
    public IPayService.PayBean answerWeChatAppPay(long orderId, String ip) {
        String notifyUrl = ConfigModel.APILINKURL + "wechatPay/answerWechatAppNotifyUrl";
        // 订单名称，必填
        String body = TextFixed.problem_pay_dec;
        Map<String, Object> answerOrder = answerMapper.getAnswerOrder(orderId);
        String orderNo = ModelUtil.getStr(answerOrder, "orderno");
        BigDecimal actualmoney = ModelUtil.getDec(answerOrder, "actualmoney", BigDecimal.ZERO);
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        return wechatAppPayImpl.pay(orderNo, actualmoney, null, body, ip, notifyUrl, null);
    }

    //问诊微信web支付
    public IPayService.PayBean answerWeChatWebPay(long orderId, String ip) {
        String notifyUrl = ConfigModel.APILINKURL + "wechatPay/answerWechatWebNotifyUrl";
        // 订单名称，必填
        String body = TextFixed.problem_pay_dec;
        Map<String, Object> answerOrder = answerMapper.getAnswerOrder(orderId);
        String orderNo = ModelUtil.getStr(answerOrder, "orderno");
        BigDecimal actualmoney = ModelUtil.getDec(answerOrder, "actualmoney", BigDecimal.ZERO);
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        String openId = getOpenId(ModelUtil.getLong(answerOrder, "userid"), OpenTypeEnum.Wechat.getCode());
        return wechatWebPayImpl.pay(orderNo, actualmoney, openId, body, ip, notifyUrl, null);
    }

    private String getOpenId(long userId, int opentype) {
        Map<String, Object> open = userService.getOpenId(userId, opentype);
        return ModelUtil.getStr(open, "openid");
    }

    public int answerWeChatPayStatus(long orderId) {
        Map<String, Object> answerOrder = answerMapper.getAnswerOrder(orderId);
        int status = ModelUtil.getInt(answerOrder, "paystatus");
        return status == PayStateEnum.Paid.getCode() ? 1 : 0;
    }

    //急诊微信app支付
    public synchronized IPayService.PayBean phoneWeChatAppPay(long orderId, String ip) {
        Map<String, Object> phoneOrder = answerMapper.getDoctorPhoneOrderById(orderId);
        long schedulingid = ModelUtil.getLong(phoneOrder, "schedulingid");
        if (answerMapper.findSchTime(schedulingid) && schedulingid != 0) {
            throw new ServiceException("该时间已被预约，请检查");
        }
        String notifyUrl = ConfigModel.APILINKURL + "wechatPay/phoneWechatAppNotifyUrl";
        // 订单名称，必填
        String body = TextFixed.phone_pay_dec;

        String orderNo = ModelUtil.getStr(phoneOrder, "orderno");
        BigDecimal actualmoney = ModelUtil.getDec(phoneOrder, "actualmoney", BigDecimal.ZERO);
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        return wechatAppPayImpl.pay(orderNo, actualmoney, null, body, ip, notifyUrl, null);
    }

    //急诊微信web支付
    public synchronized IPayService.PayBean phoneWeChatWebPay(long orderId, String ip) {
        Map<String, Object> phoneOrder = answerMapper.getDoctorPhoneOrderById(orderId);
        long schedulingid = ModelUtil.getLong(phoneOrder, "schedulingid");
        if (answerMapper.findSchTime(schedulingid)) {
            throw new ServiceException("该时间已被预约，请检查");
        }
        String notifyUrl = ConfigModel.APILINKURL + "wechatPay/phoneWechatWebNotifyUrl";
        // 订单名称，必填
        String body = TextFixed.phone_pay_dec;

        String orderNo = ModelUtil.getStr(phoneOrder, "orderno");
        BigDecimal actualmoney = ModelUtil.getDec(phoneOrder, "actualmoney", BigDecimal.ZERO);
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        String openId = getOpenId(ModelUtil.getLong(phoneOrder, "userid"), OpenTypeEnum.Wechat.getCode());
        return wechatWebPayImpl.pay(orderNo, actualmoney, openId, body, ip, notifyUrl, null);
    }

    public int phoneWeChatPayStatus(long orderId) {
        Map<String, Object> answerOrder = answerMapper.getDoctorPhoneOrderById(orderId);
        int status = ModelUtil.getInt(answerOrder, "paystatus");
        return status == PayStateEnum.Paid.getCode() ? 1 : 0;
    }

    /**
     * 添加修改医粉关系
     *
     * @param doctorid
     * @param userid
     */
    private void addUpdateDoctorUser(long doctorid, long userid) {
        long count = doctorService.getDoctorUserCount(doctorid, userid);
        if (count == 0) {
            //添加修改关系
            doctorService.addDoctorUser(doctorid, userid);
        } /*else {
            //将患者改成老患者
            doctorService.updateDoctorUser(doctorid, userid);
        }*/
    }

    public Map<String, Object> getAnswerOrderByOrderNo(String orderNo) {
        return answerMapper.getAnswerOrderByOrderNo(orderNo);
    }

    public void updateAnswerStatusSuccess(String out_trade_no, String trade_no, int payType, long userid,
                                          long doctorid) {
        doctorService.updateDoctorExtendAnswerCount(doctorid);
        answerMapper.updateAnswerStatusSuccess(out_trade_no, trade_no, payType);
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> ans = answerMapper.findById(doctorid);

        map.put("doctor", ModelUtil.getStr(ans, "doc_name"));
        try {
            SendShortMsgUtil.sendSms(ConfigModel.ISONLINE, ModelUtil.getStr(ans, "doo_tel"), com.syhdoctor.common.config.ConfigModel.SMS.answer_order_success_tempid, map);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        addUpdateDoctorUser(doctorid, userid);
    }

    public void updateAnswerStatusFail(String out_trade_no, String trade_no, String remark, int payType) {
        answerMapper.updateAnswerStatusFail(out_trade_no, trade_no, remark, payType);
    }

    public Map<String, Object> getDoctorPhoneOrderByOrderNo(String orderNo) {
        return answerMapper.getDoctorPhoneOrderByOrderNo(orderNo);
    }


    public void updatePhoneStatusSuccess(String out_trade_no, String trade_no, int payType, long userid,
                                         long doctorid) {
        doctorService.updateDoctorExtendPhoneCount(doctorid);
        answerMapper.updatePhoneStatusSuccess(out_trade_no, trade_no, payType);

        //急诊订单下单发送短信
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> ans = answerMapper.findById(doctorid);
        Map<String, Object> order = answerMapper.getPhoneOrder(out_trade_no);
        map.put("time", UnixUtil.getDate(ModelUtil.getLong(order, "subscribetime"), "yyyy-MM-dd HH:mm"));
        long schedulingid = ModelUtil.getLong(order, "schedulingid");
        if (schedulingid > 0) {
            answerMapper.updateSubscribe(schedulingid);
        }
        try {
            if (ModelUtil.getInt(order, "visitcategory") == VisitCategoryEnum.phone.getCode()) {
                SendShortMsgUtil.sendSms(ConfigModel.ISONLINE, ModelUtil.getStr(ans, "doo_tel"), com.syhdoctor.common.config.ConfigModel.SMS.phone_order_success_tempid, map);
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        addUpdateDoctorUser(doctorid, userid);
    }

    public void updatePhoneStatusFail(String out_trade_no, String trade_no, String remark, int payType) {
        answerMapper.updatePhoneStatusFail(out_trade_no, trade_no, remark, payType);
    }

    public Map<String, Object> getProblemOrder(String out_trade_no) {
        return answerMapper.getProblemOrder(out_trade_no);
    }

    public Map<String, Object> getPhoneOrder(String out_trade_no) {
        return answerMapper.getPhoneOrder(out_trade_no);
    }

    /**
     * todo 兼容
     *
     * @param orderId
     * @param ordedType
     * @return
     */
    public Map<String, Object> getOrder(long orderId, int ordedType) {
        Map<String, Object> map = new HashMap<>();
        if (ordedType == 1) {
            Map<String, Object> answerOrder = answerMapper.getAnswerOrder(orderId);
            BigDecimal actualmoney = ModelUtil.getDec(answerOrder, "actualmoney", BigDecimal.ZERO);
            BigDecimal originalprice = ModelUtil.getDec(answerOrder, "originalprice", BigDecimal.ZERO);
            map.put("orderid", ModelUtil.getLong(answerOrder, "id"));
            map.put("dec", TextFixed.problem_pay_dec);
            map.put("actualmoney", actualmoney);
            map.put("walletbalance", ModelUtil.getDec(answerOrder, "walletbalance", BigDecimal.ZERO));
            long userid = ModelUtil.getLong(answerOrder, "userid");
            Map<String, Object> isVip = userService.isVip(userid);
            int isvip = ModelUtil.getInt(isVip, "isvip");
            int visitcategory = ModelUtil.getInt(answerOrder, "visitcategory");
            map.put("uservip", isVip);
            BigDecimal marketprice = ModelUtil.getDec(answerOrder, "marketprice", BigDecimal.ZERO);
            if (isvip == 1) {
                map.put("marketprice", StrUtil.getIntegerBigDecimal(marketprice));
            } else {
                int ceefax = 0;
                double discount = 0;
                Map<String, Object> vipMember = cardService.vipDiscount();
                if (visitcategory == VisitCategoryEnum.graphic.getCode()) {
                    ceefax = ModelUtil.getInt(vipMember, "medicalexpertceefax");
                    discount = ModelUtil.getDouble(vipMember, "medicalexpertdiscount", 1);
                } else if (visitcategory == VisitCategoryEnum.Outpatient.getCode()) {
                    ceefax = ModelUtil.getInt(vipMember, "healthconsultantceefax");
                    discount = ModelUtil.getDouble(vipMember, "healthconsultantdiscount", 1);
                }
                if (ceefax > 0 || ceefax == -1000) {
                    map.put("userdiscountname", String.format("尊享免费,立省%s元", StrUtil.getIntegerBigDecimal(actualmoney)));
                } else {
                    map.put("userdiscountname", String.format("尊享%s,立省%s元", StrUtil.getDiscount(discount), StrUtil.getIntegerBigDecimal(actualmoney).subtract(StrUtil.getIntegerBigDecimal(originalprice))));
                }
            }
        } else if (ordedType == 2) {
            Map<String, Object> phoneOrder = answerMapper.getPhoneOrder(orderId);
            BigDecimal actualmoney = ModelUtil.getDec(phoneOrder, "actualmoney", BigDecimal.ZERO);
            BigDecimal marketprice = ModelUtil.getDec(phoneOrder, "marketprice", BigDecimal.ZERO);
            BigDecimal originalprice = ModelUtil.getDec(phoneOrder, "originalprice", BigDecimal.ZERO);
            map.put("orderid", ModelUtil.getLong(phoneOrder, "id"));
            map.put("actualmoney", actualmoney);
            map.put("walletbalance", ModelUtil.getDec(phoneOrder, "walletbalance", BigDecimal.ZERO));
            map.put("dec", TextFixed.phone_pay_dec);
            long userid = ModelUtil.getLong(phoneOrder, "userid");
            Map<String, Object> isVip = userService.isVip(userid);
            int isvip = ModelUtil.getInt(isVip, "isvip");
            int visitcategory = ModelUtil.getInt(phoneOrder, "visitcategory");
            map.put("uservip", isVip);
            if (isvip == 1) {
                map.put("marketprice", StrUtil.getIntegerBigDecimal(marketprice));
            } else {
                int ceefax = 0;
                double discount = 0;
                Map<String, Object> vipMember = cardService.vipDiscount();
                if (visitcategory == VisitCategoryEnum.phone.getCode()) {
                    ceefax = ModelUtil.getInt(vipMember, "medicalexpertphone");
                    discount = ModelUtil.getDouble(vipMember, "medicalexpertdiscount", 1);
                } else if (visitcategory == VisitCategoryEnum.department.getCode()) {
                    ceefax = ModelUtil.getInt(vipMember, "healthconsultantphone");
                    discount = ModelUtil.getDouble(vipMember, "healthconsultantdiscount", 1);
                }
                if (ceefax > 0 || ceefax == -1000) {
                    map.put("userdiscountname", String.format("尊享免费,立省%s元", StrUtil.getIntegerBigDecimal(actualmoney)));
                } else {
                    map.put("userdiscountname", String.format("尊享%s,立省%s元", StrUtil.getDiscount(discount), StrUtil.getIntegerBigDecimal(actualmoney).subtract(StrUtil.getIntegerBigDecimal(originalprice))));
                }
            }
        } else if (ordedType == 4) {
            Map<String, Object> phoneOrder = answerMapper.getVideoOrder(orderId);
            BigDecimal actualmoney = ModelUtil.getDec(phoneOrder, "actualmoney", BigDecimal.ZERO);
            BigDecimal marketprice = ModelUtil.getDec(phoneOrder, "marketprice", BigDecimal.ZERO);
            BigDecimal originalprice = ModelUtil.getDec(phoneOrder, "originalprice", BigDecimal.ZERO);
            map.put("orderid", ModelUtil.getLong(phoneOrder, "id"));
            map.put("actualmoney", actualmoney);
            map.put("walletbalance", ModelUtil.getDec(phoneOrder, "walletbalance", BigDecimal.ZERO));
            map.put("dec", TextFixed.video_pay_dec);
            long userid = ModelUtil.getLong(phoneOrder, "userid");
            Map<String, Object> isVip = userService.isVip(userid);
            int isvip = ModelUtil.getInt(isVip, "isvip");
            int visitcategory = ModelUtil.getInt(phoneOrder, "visitcategory");
            map.put("uservip", isVip);
            if (isvip == 1) {
                map.put("marketprice", StrUtil.getIntegerBigDecimal(marketprice));
            } else {
                int ceefax = 0;
                double discount = 0;
                Map<String, Object> vipMember = cardService.vipDiscount();
                ceefax = ModelUtil.getInt(vipMember, "medicalexpertvideo");
                discount = ModelUtil.getDouble(vipMember, "medicalexpertdiscount", 1);
                if (ceefax > 0 || ceefax == -1000) {
                    map.put("userdiscountname", String.format("尊享免费,立省%s元", StrUtil.getIntegerBigDecimal(actualmoney)));
                } else {
                    map.put("userdiscountname", String.format("尊享%s,立省%s元", StrUtil.getDiscount(discount), StrUtil.getIntegerBigDecimal(actualmoney).subtract(StrUtil.getIntegerBigDecimal(originalprice))));
                }
            }
        } else if (ordedType == 3) {
            Map<String, Object> phoneOrder = cardService.getAmount(orderId);
            int ordertype = ModelUtil.getInt(phoneOrder, "orderType");//订单类型
            BigDecimal actualmoney = null;
            String dec = TextFixed.vip_card;
            if (ordertype == 1) {
                dec = TextFixed.vip_cards;
                actualmoney = ModelUtil.getDec(phoneOrder, "renewalfee", BigDecimal.ZERO);
            } else {
                actualmoney = ModelUtil.getDec(phoneOrder, "price", BigDecimal.ZERO);
            }
            map.put("orderid", orderId);
            map.put("actualmoney", actualmoney);
            map.put("walletbalance", ModelUtil.getDec(phoneOrder, "walletbalance", BigDecimal.ZERO));
            map.put("dec", dec);
            map.put("vipdiscount", null);
        }
        return map;
    }

    public Map<String, Object> getOrderDetailed(long orderId, int ordedType) {
        Map<String, Object> map = new HashMap<>();
        String kangyangId = "";
        if (ordedType == OrderTypeEnum.Answer.getCode()) {
            Map<String, Object> answerOrder = answerMapper.getAnswerOrder(orderId);
            kangyangId = ModelUtil.getStr(answerOrder, "kangyanguserid");
            BigDecimal actualmoney = ModelUtil.getDec(answerOrder, "actualmoney", BigDecimal.ZERO);
            BigDecimal originalprice = ModelUtil.getDec(answerOrder, "originalprice", BigDecimal.ZERO);
            map.put("orderid", ModelUtil.getLong(answerOrder, "id"));
            map.put("dec", TextFixed.problem_pay_dec);
            map.put("actualmoney", actualmoney);
            map.put("walletbalance", ModelUtil.getDec(answerOrder, "walletbalance", BigDecimal.ZERO));
            long userid = ModelUtil.getLong(answerOrder, "userid");
            Map<String, Object> isVip = userService.isVip(userid);
            int isvip = ModelUtil.getInt(isVip, "isvip");
            int visitcategory = ModelUtil.getInt(answerOrder, "visitcategory");
            map.put("uservip", isVip);
            BigDecimal marketprice = ModelUtil.getDec(answerOrder, "marketprice", BigDecimal.ZERO);
            if (isvip == 1) {
                map.put("marketprice", StrUtil.getIntegerBigDecimal(marketprice));
            } else {
                int ceefax = 0;
                double discount = 0;
                Map<String, Object> vipMember = cardService.vipDiscount();
                if (visitcategory == VisitCategoryEnum.graphic.getCode()) {
                    ceefax = ModelUtil.getInt(vipMember, "medicalexpertceefax");
                    discount = ModelUtil.getDouble(vipMember, "medicalexpertdiscount", 1);
                } else if (visitcategory == VisitCategoryEnum.Outpatient.getCode()) {
                    ceefax = ModelUtil.getInt(vipMember, "healthconsultantceefax");
                    discount = ModelUtil.getDouble(vipMember, "healthconsultantdiscount", 1);
                }
                if (ceefax > 0 || ceefax == -1000) {
                    map.put("userdiscountname", String.format("尊享免费,立省%s元", StrUtil.getIntegerBigDecimal(actualmoney)));
                } else {
                    map.put("userdiscountname", String.format("尊享%s,立省%s元", StrUtil.getDiscount(discount), StrUtil.getIntegerBigDecimal(actualmoney).subtract(StrUtil.getIntegerBigDecimal(originalprice))));
                }
            }
        } else if (ordedType == OrderTypeEnum.Phone.getCode()) {
            Map<String, Object> phoneOrder = answerMapper.getPhoneOrder(orderId);
            kangyangId = ModelUtil.getStr(phoneOrder, "kangyanguserid");
            BigDecimal actualmoney = ModelUtil.getDec(phoneOrder, "actualmoney", BigDecimal.ZERO);
            BigDecimal marketprice = ModelUtil.getDec(phoneOrder, "marketprice", BigDecimal.ZERO);
            BigDecimal originalprice = ModelUtil.getDec(phoneOrder, "originalprice", BigDecimal.ZERO);
            map.put("orderid", ModelUtil.getLong(phoneOrder, "id"));
            map.put("actualmoney", actualmoney);
            map.put("walletbalance", ModelUtil.getDec(phoneOrder, "walletbalance", BigDecimal.ZERO));
            map.put("dec", TextFixed.phone_pay_dec);
            long userid = ModelUtil.getLong(phoneOrder, "userid");
            Map<String, Object> isVip = userService.isVip(userid);
            int isvip = ModelUtil.getInt(isVip, "isvip");
            int visitcategory = ModelUtil.getInt(phoneOrder, "visitcategory");
            map.put("uservip", isVip);
            if (isvip == 1) {
                map.put("marketprice", StrUtil.getIntegerBigDecimal(marketprice));
            } else {
                int ceefax = 0;
                double discount = 0;
                Map<String, Object> vipMember = cardService.vipDiscount();
                if (visitcategory == VisitCategoryEnum.phone.getCode()) {
                    ceefax = ModelUtil.getInt(vipMember, "medicalexpertphone");
                    discount = ModelUtil.getDouble(vipMember, "medicalexpertdiscount", 1);
                } else if (visitcategory == VisitCategoryEnum.department.getCode()) {
                    ceefax = ModelUtil.getInt(vipMember, "healthconsultantphone");
                    discount = ModelUtil.getDouble(vipMember, "healthconsultantdiscount", 1);
                }
                if (ceefax > 0 || ceefax == -1000) {
                    map.put("userdiscountname", String.format("尊享免费,立省%s元", StrUtil.getIntegerBigDecimal(actualmoney)));
                } else {
                    map.put("userdiscountname", String.format("尊享%s,立省%s元", StrUtil.getDiscount(discount), StrUtil.getIntegerBigDecimal(actualmoney).subtract(StrUtil.getIntegerBigDecimal(originalprice))));
                }
            }
        } else if (ordedType == OrderTypeEnum.Video.getCode()) {
            Map<String, Object> phoneOrder = answerMapper.getVideoOrder(orderId);
            kangyangId = ModelUtil.getStr(phoneOrder, "kangyanguserid");
            BigDecimal actualmoney = ModelUtil.getDec(phoneOrder, "actualmoney", BigDecimal.ZERO);
            BigDecimal marketprice = ModelUtil.getDec(phoneOrder, "marketprice", BigDecimal.ZERO);
            BigDecimal originalprice = ModelUtil.getDec(phoneOrder, "originalprice", BigDecimal.ZERO);
            map.put("orderid", ModelUtil.getLong(phoneOrder, "id"));
            map.put("actualmoney", actualmoney);
            map.put("walletbalance", ModelUtil.getDec(phoneOrder, "walletbalance", BigDecimal.ZERO));
            map.put("dec", TextFixed.video_pay_dec);
            long userid = ModelUtil.getLong(phoneOrder, "userid");
            Map<String, Object> isVip = userService.isVip(userid);
            int isvip = ModelUtil.getInt(isVip, "isvip");
            map.put("uservip", isVip);
            if (isvip == 1) {
                map.put("marketprice", StrUtil.getIntegerBigDecimal(marketprice));
            } else {
                int ceefax = 0;
                double discount = 0;
                Map<String, Object> vipMember = cardService.vipDiscount();
                ceefax = ModelUtil.getInt(vipMember, "medicalexpertvideo");
                discount = ModelUtil.getDouble(vipMember, "medicalexpertdiscount", 1);
                if (ceefax > 0 || ceefax == -1000) {
                    map.put("userdiscountname", String.format("尊享免费,立省%s元", StrUtil.getIntegerBigDecimal(actualmoney)));
                } else {
                    map.put("userdiscountname", String.format("尊享%s,立省%s元", StrUtil.getDiscount(discount), StrUtil.getIntegerBigDecimal(actualmoney).subtract(StrUtil.getIntegerBigDecimal(originalprice))));
                }
            }
        } else if (ordedType == OrderTypeEnum.Vip.getCode()) {
            Map<String, Object> phoneOrder = cardService.getAmount(orderId);
            kangyangId = ModelUtil.getStr(phoneOrder, "kangyanguserid");
            int ordertype = ModelUtil.getInt(phoneOrder, "orderType");//订单类型
            BigDecimal actualmoney = null;
            String dec = TextFixed.vip_card;
            if (ordertype == 1) {
                dec = TextFixed.vip_cards;
                actualmoney = ModelUtil.getDec(phoneOrder, "renewalfee", BigDecimal.ZERO);
            } else {
                actualmoney = ModelUtil.getDec(phoneOrder, "price", BigDecimal.ZERO);
            }
            map.put("orderid", orderId);
            map.put("actualmoney", actualmoney);
            map.put("walletbalance", ModelUtil.getDec(phoneOrder, "walletbalance", BigDecimal.ZERO));
            map.put("dec", dec);
            map.put("vipdiscount", null);
        }
        if (StrUtil.isEmpty(kangyangId)) {
            map.put("iskangyang", 0);
        } else {
            map.put("iskangyang", 1);
        }
        return map;
    }

    /**
     * 用户订单详情没有历史记录
     *
     * @param orderid
     * @return
     */
    public List<Map<String, Object>> getAppendUserSocketAnswerList(long orderid, long id) {
        List<Map<String, Object>> orderState = answerMapper.getOrderState(orderid);
        List<Map<String, Object>> answerList = answerMapper.getAppendUserSocketAnswerList(orderid, id);
        List<Long> idsDiseaseUser = new ArrayList<>();
        List<Long> idPrescriptionList = new ArrayList<>();
        List<Long> idsUserInfo = new ArrayList<>();
        getIdsByUser(answerList, idsDiseaseUser, idsUserInfo, idPrescriptionList);
        if (idsUserInfo.size() > 0) {
            List<Map<String, Object>> userInfoList = answerMapper.userInfoList(idsUserInfo);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.UserInfo).getContent(answerList, userInfoList);
        }
        if (idsDiseaseUser.size() > 0) {
            //用户显示
            List<Map<String, Object>> userAnswerDiseaseTemplateList = answerMapper.userAnswerDiseaseTemplateList(idsDiseaseUser);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.DiseaseUser).getContent(answerList, userAnswerDiseaseTemplateList);
        }
        if (idPrescriptionList.size() > 0) {
            //处方
            List<Map<String, Object>> prescriptionList = answerMapper.prescriptionList(idPrescriptionList);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.Prescription).getContent(answerList, prescriptionList);
        }
        ProblemFactory.getProblemImpl(QAContentTypeEnum.Tips).getContent(answerList, orderState);

        return answerList;
    }

    /**
     * 用户订单详情没有历史记录
     *
     * @param orderid
     * @return
     */
    public List<Map<String, Object>> getAppendUserAnswerList(long orderid, long id) {
        Map<String, Object> answerOrder = answerMapper.getAnswerOrder(orderid);
        int states = ModelUtil.getInt(answerOrder, "states");
        long doctorid = ModelUtil.getLong(answerOrder, "doctorid");
        long userid = ModelUtil.getLong(answerOrder, "userid");
        List<Map<String, Object>> answerList = new ArrayList<>();
        if (states == 2 || states == 6) {
            answerList = answerMapper.getAppendUserAnswerList(doctorid, userid, id);
        } else {
            answerList = answerMapper.getAppendUserAnswerList(orderid, id);
        }
        List<Long> idsDiseaseUser = new ArrayList<>();
        List<Long> idPrescriptionList = new ArrayList<>();
        List<Long> idsUserInfo = new ArrayList<>();
        getIdsByUser(answerList, idsDiseaseUser, idsUserInfo, idPrescriptionList);
        if (idsUserInfo.size() > 0) {
            List<Map<String, Object>> userInfoList = answerMapper.userInfoList(idsUserInfo);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.UserInfo).getContent(answerList, userInfoList);
        }
        if (idsDiseaseUser.size() > 0) {
            //用户显示
            List<Map<String, Object>> userAnswerDiseaseTemplateList = answerMapper.userAnswerDiseaseTemplateList(idsDiseaseUser);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.DiseaseUser).getContent(answerList, userAnswerDiseaseTemplateList);
        }
        if (idPrescriptionList.size() > 0) {
            //处方
            List<Map<String, Object>> prescriptionList = answerMapper.prescriptionList(idPrescriptionList);
            ProblemFactory.getProblemImpl(QAContentTypeEnum.Prescription).getContent(answerList, prescriptionList);
        }
        return answerList;
    }

    public List<Map<String, Object>> findTypeTree() {
        List<Map<String, Object>> list = answerMapper.findTypeOne();
        for (Map<String, Object> map : list) {
            long id = ModelUtil.getLong(map, "value");
            List<Map<String, Object>> typeTwo = answerMapper.findTypeTwo(id);
            if (typeTwo.size() > 0) {
                map.put("children", typeTwo);
            }
        }
        return list;
    }

    public List<Map<String, Object>> findTypeOne() {
        List<Map<String, Object>> list = answerMapper.findTypeOne();
        return list;
    }

    public List<Map<String, Object>> findTypeTwo(long typeid) {
        List<Map<String, Object>> typeTwo = answerMapper.findTypeTwo(typeid);
        return typeTwo;
    }

    public List<Map<String, Object>> findDepartType(long typeid) {
        return answerMapper.findDepartType(typeid);
    }

    public List<Map<String, Object>> findDepartTypePhone(long typeid) {
        return answerMapper.findDepartTypePhone(typeid);
    }

    /**
     * 问诊值班表导出
     *
     * @return
     */
    public List<Map<String, Object>> getDoctorInquiryListExport(long begintime, long endtime, String name, String phone, String number) {
        List<Map<String, Object>> list = answerMapper.getDoctorInquiryListExport(begintime, endtime, name, phone, number);
        Map<String, Object> map = new HashMap<>();
        map.put("doctorid", "id");
        map.put("docname", "医生姓名");
        map.put("docno", "医生编号");
        map.put("phone", "医生电话");
        map.put("starttime1", "订单时间（月）");
        map.put("starttime", "开始时间");
        map.put("endtime", "结束时间");
        list.add(0, map);
        return list;
    }

    public List<Map<String, Object>> getDoctorInquiryList(long begintime, long endtime, int pageIndex, int pageSize, String name, String phone, String number) {
        return answerMapper.getDoctorInquiryList(begintime, endtime, pageIndex, pageSize, name, phone, number);
    }

    public long getDoctorInquiryListCount(long begintime, long endtime, String name, String phone, String number) {
        return answerMapper.getDoctorInquiryListCount(begintime, endtime, name, phone, number);
    }


}