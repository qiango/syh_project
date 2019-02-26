package com.syhdoctor.webserver.service.doctor;

import com.syhdoctor.common.utils.EnumUtils.*;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.TextFixed;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.mapper.doctor.DoctorPhoneMapper;
import com.syhdoctor.webserver.mapper.video.DoctorVideoMapper;
import com.syhdoctor.webserver.service.answer.AnswerService;
import com.syhdoctor.webserver.service.system.SystemService;
import com.syhdoctor.webserver.service.wallet.DoctorWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DoctorPhoneService extends DoctorBaseService {


    @Autowired
    private DoctorPhoneMapper doctorPhoneMapper;
    @Autowired
    private SystemService systemService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private DoctorWalletService doctorWalletService;

    @Autowired
    private DoctorVideoMapper doctorVideoMapper;

    /**
     * 查询详情
     *
     * @param orderId
     * @return
     */
    public Map<String, Object> getPhoneOrderById(long orderId) {
        Map<String, Object> value = doctorPhoneMapper.getPhoneOrderById(orderId);
        if (value != null) {
            List<Map<String, Object>> list = doctorPhoneMapper.getPhoneDisease(orderId);
            if (list.size() > 0) {
                value.put("diseaselist", list);
            } else {
                value.put("diseaselist", new ArrayList<>());
            }
        } else {
            throw new ServiceException(-1, "订单号错误!");
        }

        return value;
    }

    /**
     * 查询详情
     *
     * @param orderId
     * @return
     */
    public Map<String, Object> getUserPhoneDetailed(long orderId) {
        Map<String, Object> userUserDetailed = doctorPhoneMapper.getPhoneOrderDetail(orderId);
        if (userUserDetailed != null) {
            List<Map<String, Object>> list = doctorPhoneMapper.getPhoneDisease(orderId);
            if (list.size() > 0) {
                userUserDetailed.put("diseaselist", list);
            } else {
                userUserDetailed.put("diseaselist", new ArrayList<>());
            }
            Map<String, Object> userInfo = doctorPhoneMapper.getUserInfo(ModelUtil.getLong(userUserDetailed, "id"));
            userUserDetailed.put("userinfo", userInfo);
            String diagnosis = ModelUtil.getStr(userUserDetailed, "diagnosis");
            int status = ModelUtil.getInt(userUserDetailed, "status");
            //是否填写诊后指导
            String tips = "";
            userUserDetailed.put("guidance", 0);
            if (status == PhoneOrderStateEnum.OrderSuccess.getCode()) {
                if (StrUtil.isEmpty(diagnosis)) {
                    userUserDetailed.put("guidance", 1);
                    tips = TextFixed.doctorPhoneGuidanceFailTips;
                } else {
                    userUserDetailed.put("guidance", 2);
                    tips = TextFixed.doctorPhoneGuidanceSuccessTips;
                }
                userUserDetailed.put("statusname", "已完成");
            } else if (status == PhoneOrderStateEnum.WaitRefund.getCode() || status == PhoneOrderStateEnum.OrderFail.getCode()) {
                userUserDetailed.put("status", PhoneOrderStateEnum.OrderFail.getCode());
                userUserDetailed.put("statusname", "交易失败");
                tips = TextFixed.doctorPhoneFailTips;
            } else if (status == PhoneOrderStateEnum.Paid.getCode()) {
                tips = String.format(TextFixed.doctorPhonePaidTips, UnixUtil.getDate(ModelUtil.getLong(userUserDetailed, "subscribetime"), "yyyy-MM-dd HH:mm"));
                userUserDetailed.put("statusname", "待接诊");
            } else if (status == PhoneOrderStateEnum.InCall.getCode()) {
                tips = TextFixed.doctorPhoneInCallTips;
                userUserDetailed.put("statusname", "进行中");
            }
            userUserDetailed.put("tips", tips);
            userUserDetailed.put("picturelist", doctorVideoMapper.findOrderPhoto(orderId, OrderTypeEnum.Phone.getCode()));//详情照片
        }
        return userUserDetailed;
    }

    /**
     * 保存诊疗结果
     *
     * @param orderId
     * @param diagnosis
     * @return
     */
    public boolean updatePhoneOrder(long orderId, String diagnosis) {
        return doctorPhoneMapper.updatePhoneOrder(orderId, diagnosis);
    }

    /**
     * 修改通话状态
     *
     * @param orderNo
     * @param message
     * @return
     */
    public void updatePhoneOrderStatus(String orderNo, String message) {
        Map<String, Object> orderMp = answerService.getDoctorPhoneOrderByOrderNo(orderNo);
        if (orderMp != null) {
            long orderId = ModelUtil.getLong(orderMp, "id");
            if (!"4".equals(message)) {
                //失败
                long userId = ModelUtil.getInt(orderMp, "userid");
                long doctorId = ModelUtil.getInt(orderMp, "doctorid");
                long createTime = ModelUtil.getLong(orderMp, "createtime");

                int uplatform = ModelUtil.getInt(orderMp, "uplatform");
                int dplatform = ModelUtil.getInt(orderMp, "dplatform");
                String uToken = ModelUtil.getStr(orderMp, "utoken");
                String dToken = ModelUtil.getStr(orderMp, "dtoken");

                //用户端消息开始
                systemService.addPushApp(TextFixed.messageServiceTitle,
                        String.format(TextFixed.phoneUserOrderFailText, UnixUtil.getDate(createTime, "yyyy-MM-dd")),
                        TypeNameAppPushEnum.departmentCallFailUserOrder.getCode(), String.valueOf(orderId), userId,
                        MessageTypeEnum.user.getCode(), uplatform, uToken); //app 用户 push 消息
                systemService.addMessage("", TextFixed.messageServiceTitle,
                        MessageTypeEnum.user.getCode(), String.valueOf(orderId),
                        TypeNameAppPushEnum.departmentCallFailUserOrder.getCode(), userId,
                        String.format(TextFixed.phoneUserOrderFailSystemText, UnixUtil.getDate(createTime, "yyyy-MM-dd")),
                        "");//app 用户内推送
                //用户端消息结束

                if (ModelUtil.getInt(orderMp, "callnum") < 3) {
                    //等待下次呼叫
                    doctorPhoneMapper.updatePhoneOrderStatus(orderId, PhoneOrderStateEnum.Paid.getCode());
                } else {
                    int paytype = ModelUtil.getInt(orderMp, "paytype");
                    log.info("updatePhoneOrderStatus>>>>>>>>>>>>>>>>>>>>>>>>>>" + paytype);
                    if (paytype == PayTypeEnum.ZERO.getCode() || paytype == PayTypeEnum.VipFree.getCode() || paytype == PayTypeEnum.VipZero.getCode()) {
                        doctorPhoneMapper.updatePhoneOrderStatus(orderId, PhoneOrderStateEnum.OrderFail.getCode());
                    } else {
                        //等待退款
                        doctorPhoneMapper.updatePhoneOrderStatus(orderId, PhoneOrderStateEnum.WaitRefund.getCode());
                    }
                }
            }
            doctorPhoneMapper.addPhoneOrderRecord(orderId, 1, message, null);
        }
    }

    public void updatePhoneOrderPhoneStatus(String orderNo, String state, String begin, String end, String fileServer, String recordFile) {
        //接听状态：dealing（已接）,notDeal（振铃未接听）,leak（ivr放弃）,queueLeak（排队放弃）,blackList（黑名单）,voicemail（留言）
        //Begin = 2015 - 06 - 12 20:14:50 & End = 2015 - 06 - 12 20:15:42
        Map<String, Object> orderMp = answerService.getDoctorPhoneOrderByOrderNo(orderNo);
        if (orderMp != null) {
            long orderId = ModelUtil.getLong(orderMp, "id");

            int orderStatus;
            if (ModelUtil.getInt(orderMp, "callnum") < 3) {
                //等待下次通话
                orderStatus = PhoneOrderStateEnum.Paid.getCode();
            } else {
                int paytype = ModelUtil.getInt(orderMp, "paytype");
                log.info("updatePhoneOrderPhoneStatus>>>>>>>>>>>>>>>>>>>>>>>>>>" + paytype);
                if (paytype == PayTypeEnum.ZERO.getCode() || paytype == PayTypeEnum.VipFree.getCode() || paytype == PayTypeEnum.VipZero.getCode()) {
                    orderStatus = PhoneOrderStateEnum.OrderFail.getCode();
                } else {
                    //等待退款
                    orderStatus = PhoneOrderStateEnum.WaitRefund.getCode();
                }
            }
            int phoneStatus = 0;
            String filePath = null;

            //通话已经接通
            if ("dealing".equals(state)) {
                long begintime = UnixUtil.dateTimeStamp(begin, "yyyy-MM-dd HH:mm:ss");
                long endtime = UnixUtil.dateTimeStamp(end, "yyyy-MM-dd HH:mm:ss");

                //通话时长不满
                if (endtime - begintime < TextFixed.min_phone_order_time) {
                    phoneStatus = 7;
                    filePath = fileServer + "/" + recordFile;
                } else {
                    //成功
                    int userId = ModelUtil.getInt(orderMp, "userid");
                    int doctorId = ModelUtil.getInt(orderMp, "doctorid");
                    long createTime = ModelUtil.getLong(orderMp, "createtime");
                    int uplatform = ModelUtil.getInt(orderMp, "uplatform");
                    int dplatform = ModelUtil.getInt(orderMp, "dplatform");
                    String uToken = ModelUtil.getStr(orderMp, "utoken");
                    String dToken = ModelUtil.getStr(orderMp, "dtoken");

                    //用户端消息开始
                    systemService.addPushApp(TextFixed.messageServiceTitle,
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
                    systemService.addPushApp(TextFixed.messageServiceTitle,
                            String.format(TextFixed.phoneDoctorOrderSuccessPushText, UnixUtil.getDate(createTime, "yyyy-MM-dd HH:mm:ss")),
                            TypeNameAppPushEnum.departmentCallSuccessDoctorOrder.getCode(), String.valueOf(orderId), doctorId,
                            MessageTypeEnum.doctor.getCode(), dplatform, dToken); //app 医生 push 消息
                    systemService.addMessage("", TextFixed.messageServiceTitle,
                            TypeNameAppPushEnum.departmentCallSuccessDoctorOrder.getCode(), String.valueOf(orderId),
                            MessageTypeEnum.doctor.getCode(), doctorId,
                            String.format(TextFixed.phoneDoctorOrderSuccessPushText, UnixUtil.getDate(createTime, "yyyy-MM-dd HH:mm:ss")),
                            "");//app 医生 内推送
                    //医生端消息结束

                    phoneStatus = 1;
                    filePath = fileServer + "/" + recordFile;

                    //订单完成
                    orderStatus = PhoneOrderStateEnum.OrderSuccess.getCode();
                    //添加医生钱包和交易记录
                    if (ModelUtil.getInt(orderMp, "paytype") != PayTypeEnum.ZERO.getCode()) {
                        doctorWalletService.addDoctorWallet(orderNo, ModelUtil.getInt(orderMp, "visitcategory"), doctorId, ModelUtil.getDec(orderMp, "actualmoney", BigDecimal.ZERO));
                    }
                }
            } else if ("notDeal".equals(state)) {
                phoneStatus = 2;
            } else if ("leak".equals(state)) {
                phoneStatus = 3;
            } else if ("queueLeak".equals(state)) {
                phoneStatus = 4;
            } else if ("blackList".equals(state)) {
                phoneStatus = 5;
            } else if ("voicemail".equals(state)) {
                phoneStatus = 6;
            }
            doctorPhoneMapper.updatePhoneOrderPhoneStatus(orderId, phoneStatus);
            doctorPhoneMapper.addPhoneOrderRecord(orderId, 2, state, filePath);
            doctorPhoneMapper.updatePhoneOrder(orderId, orderStatus, filePath);
        }
    }
}
