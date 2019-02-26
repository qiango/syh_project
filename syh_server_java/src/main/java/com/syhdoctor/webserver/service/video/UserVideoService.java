package com.syhdoctor.webserver.service.video;

import com.aliyuncs.exceptions.ClientException;
import com.syhdoctor.common.pay.IPayService;
import com.syhdoctor.common.utils.EnumUtils.*;
import com.syhdoctor.common.utils.*;
import com.syhdoctor.common.utils.alidayu.SendShortMsgUtil;
import com.syhdoctor.common.utils.http.HttpParamModel;
import com.syhdoctor.common.utils.http.HttpUtil;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.controller.webapp.appapi.user.answer.util.DoctorPrice;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.mapper.greenhospital.GreenHospitalMapper;
import com.syhdoctor.webserver.mapper.video.UserVideoMapper;
import com.syhdoctor.webserver.mapper.wallet.UserWalletMapper;
import com.syhdoctor.webserver.service.doctor.DoctorService;
import com.syhdoctor.webserver.service.system.SystemService;
import com.syhdoctor.webserver.service.user.UserService;
import com.syhdoctor.webserver.service.vipcard.VipCardService;
import com.syhdoctor.webserver.service.wallet.DoctorWalletService;
import com.syhdoctor.webserver.service.wallet.UserWalletService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserVideoService extends VideoBaseService {

    @Autowired
    private UserVideoMapper userVideoMapper;
    @Autowired
    private UserWalletMapper userWalletMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private DoctorPrice doctorPrice;
    @Autowired
    private VipCardService cardService;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private IPayService aliAppPayImpl;
    @Autowired
    private IPayService wechatAppPayImpl;
    @Autowired
    private GreenHospitalMapper greenHospitalMapper;
    @Autowired
    private DoctorWalletService doctorWalletService;

    public class VideoBean {
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

    public Map<String, Object> getBasicInformation(long userid) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> userWallet = userWalletService.getUserWallet(userid);
        Map<String, Object> patient = userVideoMapper.getDefaultPatient(userid);//默认患者
        Map<String, Object> scheduling = userVideoMapper.getDefaultScheduling();//默认时间
        result.put("defpatient", patient);
        result.put("defscheduling", scheduling);
        result.put("phone", ModelUtil.getStr(userWallet, "phone"));
        result.put("templatelist", getTemplateList());
        return result;
    }

    public List<Map<String, Object>> getTemplateList() {
        List<Map<String, Object>> templateList = userVideoMapper.getTemplateList();
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Map<String, Object>> tempList = new ArrayList<>();
        long key = 0;
        for (Map<String, Object> map : templateList) {
            long id = ModelUtil.getLong(map, "id");
            if (key != id) {
                Map<String, Object> fMap = new HashMap<>();
                tempList = new ArrayList<>();
                key = id;
                fMap.put("id", id);
                fMap.put("usertitle", ModelUtil.getStr(map, "usertitle"));
                fMap.put("doctortitle", ModelUtil.getStr(map, "doctortitle"));
                fMap.put("checkbox", ModelUtil.getStr(map, "checkbox"));

                Map<String, Object> cMap = new HashMap<>();
                cMap.put("content", ModelUtil.getStr(map, "content"));
                tempList.add(cMap);
                fMap.put("contentlist", tempList);
                resultList.add(fMap);
            } else {
                Map<String, Object> cMap = new HashMap<>();
                cMap.put("content", ModelUtil.getStr(map, "content"));
                tempList.add(cMap);
            }
        }
        return resultList;
    }

    public VideoBean addVideoOrder(long userId, long doctorId, List<?> diseaselist, long schedulingid) {
        VideoBean answerBean = new VideoBean();
        Map<String, Object> user = userService.getUser(userId);
        if (ModelUtil.getInt(user, "id") == 0) {
            throw new ServiceException("用户不存在");
        }
        boolean flag = userService.verifyUser(userId);//信息是否完善
//        if (flag) {
//            Map<String, Object> userAnswerOrder = userVideoMapper.getUserVidelOrder(userId);
//            if (userAnswerOrder != null) {
//                answerBean.setStatus(1);
//                answerBean.setOrderid(ModelUtil.getLong(userAnswerOrder, "id"));
//                answerBean.setUserid(ModelUtil.getLong(userAnswerOrder, "userid"));
//                answerBean.setDoctorid(ModelUtil.getLong(userAnswerOrder, "doctorid"));
//                answerBean.setOrderno(ModelUtil.getStr(userAnswerOrder, "orderno"));
//            } else {
//                log.info("doctorid==========" + doctorId);
//            }
//        }
        if (flag) {
            answerBean = saveVideoOrder(userId, doctorId, diseaselist, schedulingid);
        }
        answerBean.setIsinformation(flag ? 1 : 0);
        return answerBean;
    }

    /**
     * @param userId       用户id
     * @param doctorId     医生id
     * @param diseaselist  症状列表
     * @param schedulingid 排班id
     * @return
     */
    private VideoBean saveVideoOrder(long userId, long doctorId, List<?> diseaselist, long schedulingid) {
        VideoBean result = new VideoBean();
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
        Map<String, Object> subscribe = userVideoMapper.getSubscribe(schedulingid, doctorId);
        if (subscribe == null) {
            throw new ServiceException("该预约不存在");
        }
        if (ModelUtil.getInt(subscribe, "issubscribe") == 1) {
            throw new ServiceException("该时间段已经被预约");
        }
        long visitingstarttime = ModelUtil.getLong(subscribe, "visitingstarttime");
        long visitingendtime = ModelUtil.getLong(subscribe, "visitingendtime");

        long orderId = userVideoMapper.addVideoOrder(orderNo, userId, doctorId, price, marketPrice, originalprice, vipdiscount, VisitCategoryEnum.video.getCode(), schedulingid, visitingstarttime, visitingendtime, null, 0, 0, 0);


        Map<String, Object> userMp = userVideoMapper.getUserAccountByUserId(userId);
        String uToken = "";
        int uPlatform = 0;
        if (userMp != null) {
            uToken = ModelUtil.getStr(userMp, "xgtoken");
            uPlatform = ModelUtil.getInt(userMp, "platform");
        }
        Map<String, Object> doctorMp = userVideoMapper.getDoctorExtendsByDoctorId(doctorId);
        String dToken = "";
        int dPlatform = 0;
        if (doctorMp != null) {
            dToken = ModelUtil.getStr(doctorMp, "xgtoken");
            dPlatform = ModelUtil.getInt(doctorMp, "platform");
        }
        userVideoMapper.addVideoOrderExtend(orderId, dPlatform, dToken, uPlatform, uToken);


        //添加病症
        for (Object key : diseaselist) {
            long diseaseId = ModelUtil.strToLong(String.valueOf(key), 0);
            if (diseaseId > 0) {
                Map<String, Object> symptoms = userVideoMapper.getSymptoms(diseaseId);
                userVideoMapper.addVideoDiseaseid(orderId, diseaseId, ModelUtil.getStr(symptoms, "value"));
            }
        }

        PayTypeEnum payTypeEnum;
        log.info("type>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + type);
        //零元支付成功回调
        if (type == DoctorPrice.ZERO) {
            result.setIsfree(1);
            payTypeEnum = PayTypeEnum.ZERO;
            updateVideoStatusSuccess(orderNo, null, payTypeEnum.getCode(), userId, doctorId, schedulingid);
            addVideoOrderPushData(orderNo);
        } else if (type == DoctorPrice.VIP_Free) {
            result.setIsfree(1);
            payTypeEnum = PayTypeEnum.VipFree;
            updateVideoStatusSuccess(orderNo, null, payTypeEnum.getCode(), userId, doctorId, schedulingid);
            addVideoOrderPushData(orderNo);
        } else if (type == DoctorPrice.VIP_ZERO) {
            result.setIsfree(1);
            payTypeEnum = PayTypeEnum.VipZero;
            long vipid = ModelUtil.getLong(build, "vipid");
            cardService.updateMedicalExpertVideo(vipid);
            updateVideoStatusSuccess(orderNo, null, payTypeEnum.getCode(), userId, doctorId, schedulingid);
            addVideoOrderPushData(orderNo);
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

    //订单支付成功
    public void updateVideoStatusSuccess(String out_trade_no, String trade_no, int payType, long userid, long doctorid, long schedulingid) {
        doctorService.updateDoctorExtendVideoCount(doctorid);
        userVideoMapper.updateVideoStatusSuccess(out_trade_no, trade_no, payType);
        userVideoMapper.updateDoctorScheduling(schedulingid);
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> ans = userVideoMapper.findById(doctorid);
        Map<String, Object> videoOrderByOrderNo = userVideoMapper.getVideoOrderByOrderNo(out_trade_no);
        map.put("time", UnixUtil.getDate(ModelUtil.getLong(videoOrderByOrderNo, "subscribetime"), "yyyy-MM-dd HH:mm"));
        try {
            SendShortMsgUtil.sendSms(ConfigModel.ISONLINE, ModelUtil.getStr(ans, "doo_tel"), com.syhdoctor.common.config.ConfigModel.SMS.video_order_success_tempid, map);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        addUpdateDoctorUser(doctorid, userid);
    }


    //app推送
    public void addVideoOrderPushData(String orderNo) {
        Map<String, Object> orderMp = getVideoOrderByOrderNo(orderNo);

        int orderId = ModelUtil.getInt(orderMp, "id");
        int userId = ModelUtil.getInt(orderMp, "userid");
        int doctorId = ModelUtil.getInt(orderMp, "doctorid");
        long createTime = ModelUtil.getLong(orderMp, "createtime");
        long subscribetime = ModelUtil.getLong(orderMp, "subscribetime");
        String name = ModelUtil.getStr(orderMp, "name");
        int uplatform = ModelUtil.getInt(orderMp, "uplatform");
        int dplatform = ModelUtil.getInt(orderMp, "dplatform");
        String uToken = ModelUtil.getStr(orderMp, "utoken");
        String dToken = ModelUtil.getStr(orderMp, "dtoken");


        log.info("stringformatcreateTime>>" + UnixUtil.getDate(createTime, "yyyy-MM-dd HH:mm:ss"));
        log.info("answerUserOrderPushText>>>>" + String.format(TextFixed.videoUserOrderPushTitleText, UnixUtil.getDate(createTime, "yyyy-MM-dd HH:mm:ss")));
        //用户端消息开始
        systemService.addPushApp(TextFixed.videoUserOrderPushTitleText,
                String.format(TextFixed.videoUserOrderPushText, UnixUtil.getDate(subscribetime, "yyyy-MM-dd HH:mm:ss")),
                TypeNameAppPushEnum.VideoOrderDetail.getCode(), String.valueOf(orderId), userId, MessageTypeEnum.user.getCode(), uplatform, uToken); //app 用户 push 消息
        systemService.addMessage("", TextFixed.messageOrderTitle,
                MessageTypeEnum.user.getCode(), String.valueOf(orderId),
                TypeNameAppPushEnum.VideoOrderDetail.getCode(), userId, String.format(TextFixed.videoUserOrderSystemText, UnixUtil.getDate(createTime, "yyyy-MM-dd HH:mm:ss")), "");//app 用户内推送
        //用户端消息结束

        //医生端消息开始
        systemService.addPushApp(TextFixed.videoDoctorOrderPushTitleText,
                String.format(TextFixed.videoDoctorOrderText, name, UnixUtil.getDate(subscribetime, "yyyy-MM-dd HH:mm")),
                TypeNameAppPushEnum.VideoOrderDetail.getCode(), String.valueOf(orderId), doctorId, MessageTypeEnum.doctor.getCode(), dplatform, dToken); //app 医生 push 消息
        systemService.addMessage("", TextFixed.messageOrderTitle,
                MessageTypeEnum.doctor.getCode(), String.valueOf(orderId),
                TypeNameAppPushEnum.VideoOrderDetail.getCode(), doctorId, String.format(TextFixed.videoDoctorOrderSystemText, name, UnixUtil.getDate(subscribetime, "yyyy-MM-dd HH:mm")), "");//app 医生 内推送
        //医生端消息结束
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

    //根据订单编号查询
    public Map<String, Object> getVideoOrderByOrderNo(String orderNo) {
        return userVideoMapper.getVideoOrderByOrderNo(orderNo);
    }

    public List<Map<String, Object>> getDoctorSchedulingList(long doctorId) {
        List<Map<String, Object>> doctorSchedulingList = userVideoMapper.getDoctorSchedulingList(doctorId);
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

    public List<Map<String, Object>> appUserVideoOrderList(long userId, int pageindex, int pagesize) {
        return userVideoMapper.appUserVideoOrderList(userId, pageindex, pagesize);
    }

    public Map<String, Object> getVideoOrder(long orderId) {
        Map<String, Object> videoOrder = userVideoMapper.getVideoOrderDetailed(orderId);
        if (videoOrder != null) {
            long doctorid = ModelUtil.getLong(videoOrder, "doctorid");
            List<Map<String, Object>> list = userVideoMapper.getVideoDiseaseList(orderId);
            if (list.size() > 0) {
                videoOrder.put("diseaselist", list);
            } else {
                videoOrder.put("diseaselist", new ArrayList<>());
            }
            Map<String, Object> videoOrderDoctor = userVideoMapper.getDoctor(doctorid);
            List<Map<String, Object>> pictureList = userVideoMapper.getPictureList(orderId);//图片列表
            videoOrder.put("doctor", videoOrderDoctor);//医生详情
            videoOrder.put("picturelist", pictureList);
            String diagnosis = ModelUtil.getStr(videoOrder, "diagnosis");
            int status = ModelUtil.getInt(videoOrder, "status");
            //是否填写诊后指导
            videoOrder.put("guidance", 0);
            String tips = "";
            if (status == VideoOrderStateEnum.OrderSuccess.getCode()) {
                if (StrUtil.isEmpty(diagnosis)) {
                    videoOrder.put("guidance", 1);
                } else {
                    videoOrder.put("guidance", 2);
                }
                tips = TextFixed.userVideoSuccessTips;
                videoOrder.put("statusname", "已完成");
            } else if (status == VideoOrderStateEnum.WaitRefund.getCode() || status == VideoOrderStateEnum.OrderFail.getCode()) {
                videoOrder.put("status", VideoOrderStateEnum.OrderFail.getCode());
                videoOrder.put("statusname", "交易失败");
                tips = TextFixed.userVideoFailTips;
            } else if (status == VideoOrderStateEnum.Paid.getCode()) {
                tips = String.format(TextFixed.userVideoPaidTips, UnixUtil.getDate(ModelUtil.getLong(videoOrder, "subscribetime"), "yyyy-MM-dd HH:mm"));
                videoOrder.put("statusname", "待接诊");
            } else if (status == VideoOrderStateEnum.InCall.getCode()) {
                tips = TextFixed.userVideoInCallTips;
                videoOrder.put("statusname", "进行中");
            }
            videoOrder.put("tips", tips);

//            videoOrder.put("answerlist", userVideoMapper.getUserVideoTemplateAnserList(orderId));//问答列表
//
//            videoOrder.put("orderdiseases", userVideoMapper.getVideoOrderDiseases(orderId));//症状
//
//            videoOrder.put("photolist", userVideoMapper.getOrderPhotoList(orderId));//症状

        }
        return videoOrder;
    }

    public Map<String, Object> userIntoVideo(long orderId, String userdevicecode) {
        Map<String, Object> videoOrder = userVideoMapper.getVideoOrder(orderId);
        int status = ModelUtil.getInt(videoOrder, "status");
        if (status != VideoOrderStateEnum.InCall.getCode()) {
            throw new ServiceException("该状态不能进入直播");
        }

        if (UnixUtil.getNowTimeStamp() > ModelUtil.getLong(videoOrder, "subscribeendtime")) {
            throw new ServiceException("通话已经结束");
        }

        String userdevicecode1 = ModelUtil.getStr(videoOrder, "userdevicecode");
        if (StrUtil.isEmpty(userdevicecode1)) {
            userVideoMapper.updateVideoUserinto(orderId, userdevicecode);
        } else if (!userdevicecode.equals(userdevicecode1)) {
            throw new ServiceException("已有设备进入");
        }


        if (videoOrder != null) {
            videoOrder.put("currenttime", UnixUtil.getNowTimeStamp());
        }

        return videoOrder;
    }

    public Map<String, Object> getUserSubscribe(long userid) {
        Map<String, Object> result = new HashMap<>();
        result.put("total", userVideoMapper.getUserSubscribeCount(userid));
        result.put("subscribelist", userVideoMapper.getUserSubscribeList(userid));
        return result;
    }

    public long getUserSubscribeCount(long userid) {
        return userVideoMapper.getUserSubscribeCount(userid);
    }

    public Map<String, Object> getOrderEvaluate(long orderid, int orderType) {
        Map<String, Object> resust = new HashMap<>();
        Map<String, Object> order = null;
        switch (OrderTypeEnum.getValue(orderType)) {
            case Answer:
                order = userVideoMapper.getAnswerOrderSimple(orderid);
                break;
            case Phone:
                order = userVideoMapper.getPhoneOrderSimple(orderid);
                break;
            case Video:
                order = userVideoMapper.getVideoOrderSimple(orderid);
                break;
            default:
                break;
        }
        Map<String, Object> doctor = userVideoMapper.getDoctor(ModelUtil.getLong(order, "doctorid"));
        resust.put("doctor", doctor);
        Map<String, Object> orderEvaluate = userVideoMapper.getOrderEvaluate(orderid, orderType);
        resust.put("evaluate", orderEvaluate);
        return resust;
    }

    public boolean addOrderEvaluate(long orderid, int orderType, int isanonymous, int evaluate, String content) {
        Map<String, Object> order = null;
        switch (OrderTypeEnum.getValue(orderType)) {
            case Answer:
                order = userVideoMapper.getAnswerOrderSimple(orderid);
                break;
            case Phone:
                order = userVideoMapper.getPhoneOrderSimple(orderid);
                break;
            case Video:
                order = userVideoMapper.getVideoOrderSimple(orderid);
                break;
            default:
                break;
        }
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        Map<String, Object> orderEvaluate = userVideoMapper.getOrderEvaluate(orderid, orderType);
        if (orderEvaluate != null) {
            throw new ServiceException("该订单已经评价过");
        }
        return userVideoMapper.addOrderEvaluate(orderid, orderType, ModelUtil.getLong(order, "userid"), ModelUtil.getLong(order, "doctorid"), isanonymous, evaluate, content);
    }

    public synchronized Map<String, Object> videoWalletPay(long orderId) {
        Map<String, Object> payBean = new HashMap<>();
        Map<String, Object> videoOrder = userVideoMapper.getVideoOrder(orderId);
        if (videoOrder != null) {
            long schedulingid = ModelUtil.getLong(videoOrder, "schedulingid");
            if (userVideoMapper.findSchTime(schedulingid)) {
                throw new ServiceException("该时间已被预约，请检查");
            }
            if (ModelUtil.getInt(videoOrder, "paystatus") == 1) {
                throw new ServiceException("不能重复支付");
            }
            BigDecimal walletbalance = BigDecimal.ZERO;
            long userid = ModelUtil.getLong(videoOrder, "userid");
            Map<String, Object> userWallet = userWalletService.getUserWallet(userid);
            walletbalance = ModelUtil.getDec(userWallet, "walletbalance", BigDecimal.ZERO);
            String orderNo = ModelUtil.getStr(videoOrder, "orderno");
            BigDecimal actualmoney = ModelUtil.getDec(videoOrder, "actualmoney", BigDecimal.ZERO);
            if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
                throw new ServiceException("价格错误");
            }
            log.info("walletbalance===============" + walletbalance);
            log.info("actualmoney===============" + actualmoney);
            if (walletbalance.compareTo(actualmoney) < 0) {
                payBean.put("sufficient", 0);
            } else {
                long userId = ModelUtil.getLong(videoOrder, "userid");
                long doctorId = ModelUtil.getLong(videoOrder, "doctorid");
                updateVideoStatusSuccess(orderNo, null, PayTypeEnum.Wallet.getCode(), userId, doctorId, schedulingid);
                userWalletService.subtractUserWallet(orderNo, TransactionTypeStateEnum.Video, userId, actualmoney);

                //推送
                addVideoOrderPushData(orderNo);

                payBean.put("sufficient", 1);
                payBean.put("orderid", orderId);
            }
        } else {
            throw new ServiceException("订单不存在");
        }
        return payBean;
    }

    public synchronized IPayService.PayBean videoAliAppPay(long orderId) {
        Map<String, Object> videoOrder = userVideoMapper.getVideoOrder(orderId);
        if (userVideoMapper.findSchTime(ModelUtil.getLong(videoOrder, "schedulingid"))) {
            throw new ServiceException("该时间已被预约，请检查");
        }
        if (ModelUtil.getInt(videoOrder, "paystatus") == 1) {
            throw new ServiceException("不能重复付款");
        }
        // 订单名称，必填
        String subject = TextFixed.video_pay_dec;
        // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
        String notify_url = ConfigModel.APILINKURL + "aliCallback/videoAliAppNotifyUrl";
        // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
        String return_url = ConfigModel.APILINKURL + "aliCallback/videoAliAppReturnUrl";
        String orderNo = ModelUtil.getStr(videoOrder, "orderno");
        BigDecimal actualmoney = ModelUtil.getDec(videoOrder, "actualmoney", BigDecimal.ZERO);
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        long userId = ModelUtil.getLong(videoOrder, "userid");
        return aliAppPayImpl.pay(orderNo, actualmoney, String.valueOf(userId), subject, null, notify_url, return_url);
    }

    //问诊微信app支付
    public synchronized IPayService.PayBean videoWeChatAppPay(long orderId, String ip) {
        Map<String, Object> videoOrder = userVideoMapper.getVideoOrder(orderId);
        if (userVideoMapper.findSchTime(ModelUtil.getLong(videoOrder, "schedulingid"))) {
            throw new ServiceException("该时间已被预约，请检查");
        }
        if (ModelUtil.getInt(videoOrder, "paystatus") == 1) {
            throw new ServiceException("不能重复付款");
        }
        String notifyUrl = ConfigModel.APILINKURL + "wechatPay/videoWechatAppNotifyUrl";
        // 订单名称，必填
        String body = TextFixed.video_pay_dec;
        String orderNo = ModelUtil.getStr(videoOrder, "orderno");
        BigDecimal actualmoney = ModelUtil.getDec(videoOrder, "actualmoney", BigDecimal.ZERO);
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        return wechatAppPayImpl.pay(orderNo, actualmoney, null, body, ip, notifyUrl, null);
    }

    public int videoPayStatus(long orderId) {
        Map<String, Object> videoOrder = userVideoMapper.getVideoOrder(orderId);
        int status = ModelUtil.getInt(videoOrder, "paystatus");
        return status == PayStateEnum.Paid.getCode() ? 1 : 0;
    }

    public List<Map<String, Object>> getHospitalList(List<?> categoryids, List<?> departmentid, int pageIndex, int pageSize) {
        List<Long> longs = new ArrayList<>();
        if (categoryids.size() > 0) {
            for (Object value : categoryids) {
                longs.add(Long.parseLong(value.toString()));
            }
        }
        List<Long> longss = new ArrayList<>();
        if (departmentid.size() > 0) {
            for (Object value : departmentid) {
                longss.add(Long.parseLong(value.toString()));
            }
        }
        return userVideoMapper.hospitalList(longs, longss, pageIndex, pageSize);
    }

    public Map<String, Object> findDetail(long hospital) {
        Map<String, Object> map = new HashedMap();
        map.put("detail", userVideoMapper.findDetail(hospital));
        map.put("depart", userVideoMapper.findDepartment(hospital));
        return map;
    }

    public List<Map<String, Object>> findCategory() {
        return userVideoMapper.findCategory();
    }

    //树形科室
    public List<Map<String, Object>> getAllDepartmentListTree() {
        List<Map<String, Object>> allDepartmentList = userVideoMapper.findDeparmentListTree();
        Map<Long, List<Map<String, Object>>> tempMap = new HashMap<>();
        for (Map<String, Object> temp : allDepartmentList) {
            Long pid = ModelUtil.getLong(temp, "pid", 0);
            departmentTree(temp, pid, tempMap);
        }
        for (Map<String, Object> temp : allDepartmentList) {
            Long pid = ModelUtil.getLong(temp, "id", 0);
            temp.put("child", tempMap.get(pid));
        }
        return tempMap.get(0L);
    }

    public List<Map<String, Object>> getAllDepartmentList() {
        return userVideoMapper.findDeparmentList();
    }


    private void departmentTree(Map<String, Object> temp, long pid, Map<Long, List<Map<String, Object>>> tempMap) {
        if (tempMap.containsKey(pid)) {
            tempMap.get(pid).add(temp);
        } else {
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(temp);
            tempMap.put(pid, list);
        }
    }


    //下单绿通
    public boolean insertOrderGreen(List<?> diseaselist, long userid, long hospitalid, long departmentid, int appointmentType, int isOrdinary, int isExpert, int isUrgent) {
        Map<String, Object> sittingDoctor = doctorService.getQuestion();
        long doctorId;
        if (sittingDoctor != null) {
            doctorId = ModelUtil.getLong(sittingDoctor, "doctorid");
        } else {
            //垫底顾问
            if (ConfigModel.ISONLINE.equals("1")) {
                //垫底顾问
                doctorId = 400;
            } else {
                doctorId = 284;
            }
        }
        long id = userVideoMapper.insertOrderGreen(userid, doctorId, hospitalid, departmentid, appointmentType, isOrdinary, isExpert, isUrgent);
        //添加病症
        for (Object key : diseaselist) {
            long diseaseId = ModelUtil.strToLong(String.valueOf(key), 0);
            if (diseaseId > 0) {
                Map<String, Object> symptoms = userVideoMapper.getSymptoms(diseaseId);
                userVideoMapper.addGreenDiseaseid(id, diseaseId, ModelUtil.getStr(symptoms, "value"));
            }
        }
        return true;
    }

    public Map<String, Object> insertOrderGreenSimple(long userid, long hospitalid, long departmentid) {
        boolean greeOrder = userVideoMapper.findGreeOrder(userid, hospitalid, departmentid);
        if (greeOrder) {
            throw new ServiceException("您已在该医院该科室下过单，不可重复下单，请检查");
        }
        Map<String, Object> map = new HashedMap();
        Map<String, Object> sittingDoctor = doctorService.getQuestion();
        long doctorId;
        if (sittingDoctor != null) {
            doctorId = ModelUtil.getLong(sittingDoctor, "doctorid");
        } else {
            //垫底顾问
            if (ConfigModel.ISONLINE.equals("1")) {
                //垫底顾问
                doctorId = 400;
            } else {
                doctorId = 284;
            }
        }
        long id = userVideoMapper.insertOrderGreenSimple(doctorId, userid, hospitalid, departmentid);
        map.put("orderid", id);
        String content = TextFixed.green_order_start;
        int type = 2;
        greenHospitalMapper.insertGreenOrderChat(userid, doctorId, id, content, type);
        return map;
    }

    public List<Map<String, Object>> greenOrderList(long userid, int pageSize, int pageIndex) {
        return userVideoMapper.greenOrderList(userid, pageSize, pageIndex);
    }

    public Map<String, Object> getGreenDetail(long orderid) {
        Map<String, Object> map = new HashedMap();
        map.put("detail", userVideoMapper.getGreenDetail(orderid));
        map.put("disease", userVideoMapper.findGreenDisease(orderid));
        return map;
    }

    //微信app支付
    public IPayService.PayBean weChatAppPay(BigDecimal money, String ip, String orderno, long userid) {
        Map<String, Object> user = userWalletMapper.getUserWallet(userid);
        if (ModelUtil.getInt(user, "id") == 0) {
            throw new ServiceException("用户不存在");
        }
        String notifyUrl = ConfigModel.APILINKURL + "wechatPay/vipWechatAppNotifyUrl";
        // 订单名称，必填
        String body = "绿通服务";
        if (money.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        IPayService.PayBean payBean = wechatAppPayImpl.pay(orderno, money, null, body, ip, notifyUrl, null);
        return payBean;
    }

    //支付宝app支付
    public IPayService.PayBean aliAppPay(BigDecimal actualmoney, String orderno, long userid) {
        Map<String, Object> user = userWalletMapper.getUserWallet(userid);
        if (ModelUtil.getInt(user, "id") == 0) {
            throw new ServiceException("用户不存在");
        }
        // 订单名称，必填
        String subject = "绿通服务";
        // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
        String notify_url = ConfigModel.APILINKURL + "aliCallback/vipCardAliAppNotifyUrl";

        // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
        String return_url = ConfigModel.APILINKURL + "aliCallback/rechargeableAliAppReturnUrl";
        if (actualmoney.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("价格错误");
        }
        IPayService.PayBean pay = aliAppPayImpl.pay(orderno, actualmoney, null, subject, null, notify_url, return_url);
        return pay;

    }

    //钱包支付
    @Transactional
    public long wallet(BigDecimal bigDecimal, BigDecimal actualmoney, long orderid, long userid) {
        BigDecimal finalAmount = bigDecimal.subtract(actualmoney);
        boolean res = userVideoMapper.updateWallet(userid, PriceUtil.addPrice(finalAmount));
        if (res) {
            updateStatus(orderid, PayTypeEnum.Wallet.getCode(), userid);
            return orderid;
        } else {
            throw new ServiceException("因为异常钱包扣款失败，请重试");
        }
    }

    //添加用户交易记录
    public void addUserRecord(long orderid, long userid, TransactionTypeStateEnum t) {
        Map<String, Object> order = userVideoMapper.findOrderByorderId(orderid);
        String orderno = ModelUtil.getStr(order, "orderno");
        BigDecimal actualmoney = ModelUtil.getDec(order, "actualmoney", BigDecimal.ZERO);
        BigDecimal walletbalance = ModelUtil.getDec(order, "walletbalance", BigDecimal.ZERO);
        String userAccount = ModelUtil.getStr(order, "phone");
        userWalletMapper.addUserTransactionRecord(orderno, t, MoneyTypeEnum.Expenditure, userid, userAccount, actualmoney, walletbalance);
    }

    public void updateStatus(long orderid, int payType, long userid) {
        userVideoMapper.updateStatus(orderid, payType);
        addUserRecord(orderid, userid, TransactionTypeStateEnum.Green);
    }

    public Map<String, Object> findOrderByorderId(long orderid) {
        return userVideoMapper.findOrderByorderId(orderid);
    }

    public Map<String, Object> addAppGreen(long orderId, String content, int contentType, int questionaAnswerType) {
        return addAppGreen(orderId, content, contentType, 0, questionaAnswerType);
    }

    /**
     * app语音回答
     *
     * @param orderId
     * @param content
     * @param contentType
     * @return
     */
    @Transactional
    public Map<String, Object> addAppGreen(long orderId, String content, int contentType, long contenttime, int questionaAnswerType) {
        log.info("content: " + content);
        log.info("contenttype: " + contentType);
        Map<String, Object> problem = userVideoMapper.getProblem(orderId);
        Map<String, Object> map;
        if (problem != null) {
            int status = ModelUtil.getInt(problem, "status");
            if (status == 4 || status == 5) {
                throw new ServiceException("订单已完成或已失败");
            }
            long userid = ModelUtil.getLong(problem, "userid");
            long doctorid = ModelUtil.getLong(problem, "doctorid");

//            String doctorName = ModelUtil.getStr(problem, "docname");
//            String userName = ModelUtil.getStr(problem, "username");
//            String doctorUrl = ModelUtil.getStr(problem, "docphotourl");
//            String userUrl = ModelUtil.getStr(problem, "headpic");
//            int uplatform = ModelUtil.getInt(problem, "uplatform");
//            int dplatform = ModelUtil.getInt(problem, "dplatform");
//            String uToken = ModelUtil.getStr(problem, "utoken");
//            String dToken = ModelUtil.getStr(problem, "dtoken");
//            String title = "";
//            String messageContent = "";
//            switch (QAContentTypeEnum.getValue(contentType)) {
//                case Text:
//                    if (questionaAnswerType == 0) {//用户回复,给医生提醒
//                        title = userName;
//                    } else if (questionaAnswerType == 1) {//医生回复,给用户回复
//                        title = doctorName;
//                    }
//                    messageContent = content;
//                    break;
//                case Voice:
//                    if (questionaAnswerType == 0) {//用户回复,给医生提醒
//                        messageContent = String.format(TextFixed.userServerVoiceTitle, userName);
//                    } else if (questionaAnswerType == 1) {//医生回复,给用户回复
//                        messageContent = String.format(TextFixed.doctorServerVoiceTitle, doctorName);
//                    }
//                    break;
//                case Picture:
//                    if (questionaAnswerType == 0) {//用户回复,给医生提醒
//                        messageContent = String.format(TextFixed.userServerImageTitle, userName);
//                    } else if (questionaAnswerType == 1) {//医生回复,给用户回复
//                        messageContent = String.format(TextFixed.doctorServerImageTitle, doctorName);
//                    }
//                    break;
//                default:
//                    messageContent = "";
//                    break;
//            }
//            if (questionaAnswerType == 0) {//用户回复,给医生提醒
//                systemService.addPushApp(title, messageContent,
//                        TypeNameAppPushEnum.inquiryUserReplyOrder.getCode(),
//                        String.valueOf(orderId), doctorid,
//                        MessageTypeEnum.doctor.getCode(), dplatform, dToken); //推送push消息
//                systemService.addMessage(userUrl, userName,
//                        MessageTypeEnum.doctor.getCode(), String.valueOf(orderId),
//                        TypeNameAppPushEnum.inquiryUserReplyOrder.getCode(), doctorid,
//                        messageContent,
//                        "");
//            } else if (questionaAnswerType == 1) {//医生回复,给用户回复
//                systemService.addPushApp(title, messageContent,
//                        TypeNameAppPushEnum.inquiryDoctorReplyOrder.getCode(),
//                        String.valueOf(orderId), userid,
//                        MessageTypeEnum.user.getCode(), uplatform, uToken); //
//                systemService.addMessage(doctorUrl, doctorName,
//                        MessageTypeEnum.user.getCode(), String.valueOf(orderId),
//                        TypeNameAppPushEnum.inquiryDoctorReplyOrder.getCode(), userid,
//                        messageContent,
//                        "");
//            }
            long id = userVideoMapper.addAnswer(userid, doctorid, orderId, content, contenttime, contentType, questionaAnswerType);
            //websocket
            String userno = ModelUtil.getStr(problem, "userno");
            String doctorno = ModelUtil.getStr(problem, "doctorno");
            sendSoctet(orderId, id, userno, doctorno);
            map = userVideoMapper.getDoctorAnswer(id);
        } else {
            throw new ServiceException("订单不存在");
        }
        return map;
    }

    private void sendSoctet(long orderId, long id, String userno, String doctorno) {
        List<Map<String, Object>> userAppendList = getAppendUserSocketAnswerList(orderId, id);
        List<Map<String, Object>> doctorAppendList = getAppendUserSocketAnswerList(orderId, id);
        String userContentJson = String.format("%s|%s%s", JsonUtil.getInstance().toJson(userAppendList), orderId, userno);
        String doctorContentJson = String.format("%s|%s%s", JsonUtil.getInstance().toJson(doctorAppendList), orderId, doctorno);
        HttpUtil instance = HttpUtil.getInstance();
        HttpParamModel httpParamModel = new HttpParamModel();
        httpParamModel.add("json", userContentJson);
        HttpParamModel httpParamModelDoctor = new HttpParamModel();
        httpParamModelDoctor.add("json", doctorContentJson);
        try {
            instance.post(ConfigModel.WEBSOCKETLINKURL + "websocket/pushDataAdmin", httpParamModel);
            instance.post(ConfigModel.WEBSOCKETLINKURL + "websocket/pushDataAdmin", httpParamModelDoctor);
            log.info("socket调用成功----------------");
        } catch (Exception e) {
            log.info("socket调用失败---------------");
        }
//        WebSocketServer.sendToUser(userContentJson);
//        WebSocketServer.sendToUser(doctorContentJson);
    }

    public List<Map<String, Object>> getAppendUserSocketAnswerList(long orderId, long id) {
        return userVideoMapper.getAppendUserSocketAnswerList(orderId, id);
    }

    /**
     * 医生订单详情没有历史记录
     *
     * @param orderid
     * @return
     */
    public Map<String, Object> getDoctorGreenList(long orderid, int pageindex, int pagesize) {

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> answerOrder = userVideoMapper.getGreenOrder(orderid);
        long doctorid = ModelUtil.getLong(answerOrder, "doctorid");
        long userid = ModelUtil.getLong(answerOrder, "userid");
        int status = ModelUtil.getInt(answerOrder, "status");
        long time = ModelUtil.getLong(answerOrder, "subscribetime");
        String greencontact = ModelUtil.getStr(answerOrder, "greencontact");
        String greenphone = ModelUtil.getStr(answerOrder, "greenphone");
        String greenaddress = ModelUtil.getStr(answerOrder, "greenaddress");
        String hospitalname = ModelUtil.getStr(answerOrder, "hospitalname");
        //问答列表
        List<Map<String, Object>> answerList;
        if (status == 2 || status == 3 || status == 6) {
            //问答列表包涵历史
            answerList = userVideoMapper.getDoctorHistoryGreenList(orderid, doctorid, userid, pageindex, pagesize);
        } else {
            //问答列表没有历史
            answerList = userVideoMapper.getDoctorCurrentGreenList(orderid, pageindex, pagesize);
        }
        Map<String, Object> value = userVideoMapper.getDoctorOrderState(orderid);
        for (Map<String, Object> map : answerList) {
            int contenttype = ModelUtil.getInt(map, "contenttype");
            if (contenttype == 5) {
                Map<String, Object> maps = new HashedMap();
                maps.put("greencontact", greencontact);
                maps.put("greenphone", greenphone);
                maps.put("greenaddress", greenaddress);
                maps.put("hospitalname", hospitalname);
                maps.put("time", time);
                map.put("content", maps);
            }
        }
        result.put("answerlist", answerList);
        result.put("status", ModelUtil.getInt(value, "states"));
        result.put("examine", ModelUtil.getInt(value, "examine"));
        result.put("userno", ModelUtil.getStr(answerOrder, "userno"));
        result.put("doccode", ModelUtil.getStr(answerOrder, "doccode"));
        result.put("userid", userid);
        return result;
    }


    /**
     * 用户订单详情没有历史记录
     *
     * @param orderid
     */
    public Map<String, Object> getUserGreenList(long orderid, int pageindex, int pagesize) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> answerOrder = userVideoMapper.getGreenOrder(orderid);
        long doctorid = ModelUtil.getLong(answerOrder, "doctorid");
        long userid = ModelUtil.getLong(answerOrder, "userid");
        int status = ModelUtil.getInt(answerOrder, "status");
        long time = ModelUtil.getLong(answerOrder, "subscribetime");
        String greencontact = ModelUtil.getStr(answerOrder, "greencontact");
        String greenphone = ModelUtil.getStr(answerOrder, "greenphone");
        String greenaddress = ModelUtil.getStr(answerOrder, "greenaddress");
        String hospitalname = ModelUtil.getStr(answerOrder, "hospitalname");
        Map<String, Object> doctor = doctorService.getSimpleDoctor(doctorid);
        if (doctor != null) {
            doctor.put("status", status);
            doctor.put("statusname", GreenOrderStateEnum.getValue(status).getMessage());
        }
        //问答列表
        List<Map<String, Object>> answerList;
        if (status == 2 || status == 3 || status == 6) {
            //问答列表包涵历史
            answerList = userVideoMapper.getUserHistoryGreenList(doctorid, userid, pageindex, pagesize);
        } else {
            //问答列表没有历史
            answerList = userVideoMapper.getUserCurrentGreenList(orderid, pageindex, pagesize);
        }
        for (Map<String, Object> map : answerList) {
            int contenttype = ModelUtil.getInt(map, "contenttype");
            if (contenttype == 5) {
                Map<String, Object> maps = new HashedMap();
                maps.put("greencontact", greencontact);
                maps.put("greenphone", greenphone);
                maps.put("greenaddress", greenaddress);
                maps.put("hospitalname", hospitalname);
                maps.put("time", time);
                map.put("content", maps);
            }
        }
        result.put("doctor", doctor);
        result.put("answerlist", answerList);
        result.put("userno", ModelUtil.getStr(answerOrder, "userno"));
        result.put("doctorcode", ModelUtil.getStr(answerOrder, "doccode"));
        return result;
    }

    public boolean confirmClose(long orderid, int isclose) {
        Map<String, Object> videoOrder = userVideoMapper.getVideoOrder(orderid);
        if (videoOrder != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("orderid", orderid);
            if (isclose == 0) {
                map.put("tiptype", VideoTipTypeEnum.userRefuse.getCode());
                map.put("tipmessage", VideoTipTypeEnum.userRefuse.getMessage());
            } else {
                map.put("tiptype", VideoTipTypeEnum.userAgree.getCode());
                map.put("tipmessage", VideoTipTypeEnum.userAgree.getMessage());
                if (ModelUtil.getInt(videoOrder, "status") == VideoOrderStateEnum.InCall.getCode()) {
                    userVideoMapper.updateOrderStatus(orderid);
                    doctorWalletService.addDoctorWallet(ModelUtil.getStr(videoOrder, "orderno"), VisitCategoryEnum.video.getCode(), ModelUtil.getLong(videoOrder, "doctorid"), ModelUtil.getDec(videoOrder, "actualmoney", BigDecimal.ZERO));
                }
            }
            String json = String.format("%s|%s", JsonUtil.getInstance().toJson(map), ModelUtil.getStr(videoOrder, "doctoruid"));
            HttpParamModel httpParamModel = new HttpParamModel();
            httpParamModel.add("json", json);
            HttpUtil.getInstance().post(ConfigModel.WEBSOCKETLINKURL + "websocket/videoPushData", httpParamModel);
        } else {
            throw new ServiceException("订单不存在");
        }
        return true;
    }

    public boolean userCloseOrder(long orderid) {
        Map<String, Object> videoOrder = userVideoMapper.getVideoOrder(orderid);
        if (videoOrder != null) {
            if (ModelUtil.getInt(videoOrder, "status") != VideoOrderStateEnum.InCall.getCode()) {
                throw new ServiceException("订单已关闭或者其他错误");
            }
            Map<String, Object> map = new HashMap<>();
            map.put("orderid", orderid);
            map.put("tiptype", VideoTipTypeEnum.UserClose.getCode());
            map.put("tipmessage", VideoTipTypeEnum.UserClose.getMessage());

            String json = String.format("%s|%s", JsonUtil.getInstance().toJson(map), ModelUtil.getStr(videoOrder, "doctoruid"));
            HttpParamModel httpParamModel = new HttpParamModel();
            httpParamModel.add("json", json);
            HttpUtil.getInstance().post(ConfigModel.WEBSOCKETLINKURL + "websocket/videoPushData", httpParamModel);

            if (ModelUtil.getInt(videoOrder, "status") == VideoOrderStateEnum.InCall.getCode()) {
                userVideoMapper.updateOrderStatus(orderid);
                doctorWalletService.addDoctorWallet(ModelUtil.getStr(videoOrder, "orderno"), VisitCategoryEnum.video.getCode(), ModelUtil.getLong(videoOrder, "doctorid"), ModelUtil.getDec(videoOrder, "actualmoney", BigDecimal.ZERO));
            }
        }
        return true;
    }

    //视频订单成功推送
    public void addAnswerOrderPushData(String orderNo) {
        Map<String, Object> orderMp = getVideoOrderByOrderNo(orderNo);

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
        systemService.addPushApp(TextFixed.messageServiceTitle,
                TextFixed.videoUserOrderSuccessPushText,
                TypeNameAppPushEnum.videoSuccessOrder.getCode(), String.valueOf(orderId), userId, MessageTypeEnum.user.getCode(), uplatform, uToken); //app 用户 push 消息
        systemService.addMessage("", TextFixed.messageServiceTitle,
                MessageTypeEnum.user.getCode(), String.valueOf(orderId),
                TypeNameAppPushEnum.videoSuccessOrder.getCode(), userId,TextFixed.videoUserOrderSuccessSystemText, "");//app 用户内推送
        //用户端消息结束

        //医生端消息开始
        systemService.addPushApp(TextFixed.messageServiceTitle,
                String.format(TextFixed.videoDoctorOrderSuccessPushText, name),
                TypeNameAppPushEnum.videoSuccessOrder.getCode(), String.valueOf(orderId), doctorId, MessageTypeEnum.doctor.getCode(), dplatform, dToken); //app 医生 push 消息
        systemService.addMessage("", TextFixed.messageServiceTitle,
                MessageTypeEnum.doctor.getCode(), String.valueOf(orderId),
                TypeNameAppPushEnum.videoSuccessOrder.getCode(), doctorId, String.format(TextFixed.videoDoctorOrderSuccessPushText, name), "");//app 医生 内推送
        //医生端消息结束

        systemService.addPushSendRule(orderId);


    }
}
