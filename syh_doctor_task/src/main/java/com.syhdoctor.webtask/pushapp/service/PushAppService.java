package com.syhdoctor.webtask.pushapp.service;


import com.syhdoctor.common.utils.EnumUtils.MessageTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.TypeNameAppPushEnum;
import com.syhdoctor.common.utils.*;
import com.syhdoctor.common.utils.tx.xinge.PushAppUtil;
import com.syhdoctor.webtask.base.service.BaseService;
import com.syhdoctor.webtask.pushapp.mapper.PushAppMapper;
import com.syhdoctor.webtask.system.service.SystemService;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PushAppService extends BaseService {

    @Autowired
    private PushAppMapper pushAppMapper;

    @Autowired
    private SystemService systemService;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    public void getAnswerOrderBySevenDays() {
        List<Map<String, Object>> mapList = pushAppMapper.getProblemOrderBySevenDays();
        for (Map<String, Object> map : mapList) {
            long orderId = ModelUtil.getInt(map, "id");
            int userId = ModelUtil.getInt(map, "userid");
            int dplatform = ModelUtil.getInt(map, "dplatform");
            String dToken = ModelUtil.getStr(map, "dtoken");
            pushAppMapper.addPushApp(TextFixed.messageServiceTitle,
                    TextFixed.answerUserSevenDays,
                    TypeNameAppPushEnum.doNot.getCode(), String.valueOf(orderId),
                    userId, MessageTypeEnum.user.getCode(), dplatform, dToken); //app医生push消息
            pushAppMapper.updateProblemOrderBySevenDays(orderId);
        }
    }

    /**
     * 添加app push消息数据
     *
     * @param title       标题
     * @param content     内容
     * @param type        类型
     * @param typeName
     * @param receiveId   接收人ID
     * @param receiveType 接收人类型
     * @return
     */
    public void addMqPushApp(String title, String content, int type, String typeName, long receiveId, int receiveType, int platform,
                             String xgtoken) {
        Destination destination = new ActiveMQQueue("AppPush");
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("type", type);
        map.put("typename", typeName);
        map.put("receiveid", receiveId);
        map.put("receivetype", receiveType);
        map.put("platform", platform);
        map.put("xgtoken", xgtoken);
        String message = JsonUtil.getInstance().gsonToJson(map);
        jmsMessagingTemplate.convertAndSend(destination, message);
    }

    /**
     * 急诊七天后用户推送
     */
    public void getDepartmentOrderBySevenDays() {
        List<Map<String, Object>> mapList = pushAppMapper.getPhoneOrderBySevenDays();
        for (Map<String, Object> map : mapList) {
            long orderId = ModelUtil.getInt(map, "id");
            int userId = ModelUtil.getInt(map, "userid");
            int uplatform = ModelUtil.getInt(map, "uplatform");
            String uToken = ModelUtil.getStr(map, "utoken");
            pushAppMapper.addPushApp(TextFixed.messageServiceTitle,
                    TextFixed.phoneUserSevenDays,
                    TypeNameAppPushEnum.doNot.getCode(), String.valueOf(orderId),
                    userId, MessageTypeEnum.user.getCode(), uplatform, uToken); //app医生push消息
            pushAppMapper.updatePhoneOrderOrderBySevenDays(orderId);
        }
    }


    public void getWaitDoctorProblemOrder() {
        List<Map<String, Object>> mapList = pushAppMapper.getWaitDoctorProblemOrder();
        for (Map<String, Object> map : mapList) {
            int sorid = ModelUtil.getInt(map, "sorid");
            long orderId = ModelUtil.getInt(map, "orderid");
            long createTime = ModelUtil.getLong(map, "createtime");
            int doctorId = ModelUtil.getInt(map, "doctorid");
            int dplatform = ModelUtil.getInt(map, "dplatform");
            String dToken = ModelUtil.getStr(map, "dtoken");
            String name = ModelUtil.getStr(map, "name");
            String where = "";
            for (int i = 0; i < 7; i++) {
                boolean value = ModelUtil.getBoolean(map, getHourName(i), false);//查看当前时间段有没有推送过
                if (!value && UnixUtil.getNowTimeStamp() / 1000 - createTime / 1000 > getHourLong(i)) {//当前时间段没推送并且订单时间超过当前时间段
                    where = (i + 1) > 6 ? "" : getHourName(i + 1);
                    pushAppMapper.addPushApp(TextFixed.answerDoctorOrderWaitNewReplyTitleText,
                            String.format(TextFixed.answerDoctorOrderWaitNewReplyContentText, name),
                            TypeNameAppPushEnum.DoctorAfter.getCode(), String.valueOf(orderId), doctorId, MessageTypeEnum.doctor.getCode(), dplatform, dToken); //app医生push消息
                    systemService.addMessage("", TextFixed.messageServiceTitle,
                            MessageTypeEnum.doctor.getCode(), String.valueOf(orderId),
                            TypeNameAppPushEnum.DoctorAfter.getCode(), doctorId, String.format(TextFixed.answerDoctorOrderWaitNewReplyContentText, name), "");//app 医生 内推送
                }
            }
            if (!StrUtil.isEmpty(where)) {
                pushAppMapper.updateDoctorOrderRules(sorid);
                pushAppMapper.updateDoctorOrderRules(sorid, where);
            }
        }
    }

    /**
     * 电话前十分钟推送
     */
    public void getPhoneTenTime() {
        List<Map<String, Object>> mapList = pushAppMapper.getPhoneTenTimeList();
        if (mapList.size() > 0) {
            for (Map<String, Object> map : mapList) {
                long orderId = ModelUtil.getInt(map, "orderid");
                int doctorId = ModelUtil.getInt(map, "doctorid");
                int dplatform = ModelUtil.getInt(map, "dplatform");
                String dToken = ModelUtil.getStr(map, "dtoken");
                long userid = ModelUtil.getLong(map, "userid");
                Map<String, Object> usernamemap = pushAppMapper.userName(userid);
                String username = ModelUtil.getStr(usernamemap, "name");
                pushAppMapper.addPushApp(TextFixed.messageServiceTitle,
                        String.format(TextFixed.phoneDoctorTenMinuteText, username),
                        TypeNameAppPushEnum.PhoneOrderTenTime.getCode(), String.valueOf(orderId), doctorId, MessageTypeEnum.doctor.getCode(), dplatform, dToken); //app医生push消息
                pushAppMapper.addPushApp(TextFixed.messageServiceTitle,
                        TextFixed.phoneUserTenMinuteText,
                        TypeNameAppPushEnum.PhoneOrderTenTime.getCode(), String.valueOf(orderId), (int) userid, MessageTypeEnum.user.getCode(), dplatform, dToken); //app用户push消息

                systemService.addMessage("", TextFixed.messageServiceTitle,
                        MessageTypeEnum.doctor.getCode(), String.valueOf(orderId),
                        TypeNameAppPushEnum.PhoneOrderTenTime.getCode(), doctorId, String.format(TextFixed.phoneDoctorTenMinuteText, username), "");//app 医生 内推送
                systemService.addMessage("", TextFixed.messageServiceTitle,
                        MessageTypeEnum.user.getCode(), String.valueOf(orderId),
                        TypeNameAppPushEnum.PhoneOrderTenTime.getCode(), (int) userid, TextFixed.phoneUserTenMinuteSystemText, "");//app 用户 内推送
                pushAppMapper.phoneisTenTime(orderId);//修改推送状态
            }
        }
    }

    /**
     * 视频前十分钟推送
     */
    public void getVideoTenTimeList() {
        List<Map<String, Object>> mapList = pushAppMapper.getVideoTenTimeList();
        if (mapList.size() > 0) {
            for (Map<String, Object> map : mapList) {
                long orderId = ModelUtil.getInt(map, "orderid");
                int doctorId = ModelUtil.getInt(map, "doctorid");
                int dplatform = ModelUtil.getInt(map, "dplatform");
                String dToken = ModelUtil.getStr(map, "dtoken");
                long userid = ModelUtil.getLong(map, "userid");
                Map<String, Object> usernamemap = pushAppMapper.userName(userid);
                String username = ModelUtil.getStr(usernamemap, "name");
                pushAppMapper.addPushApp(TextFixed.messageServiceTitle,
                        String.format(TextFixed.videoDoctorTenMinuteText, username),
                        TypeNameAppPushEnum.VideoOrderTenTime.getCode(), String.valueOf(orderId), doctorId, MessageTypeEnum.doctor.getCode(), dplatform, dToken); //app医生push消息
                pushAppMapper.addPushApp(TextFixed.messageServiceTitle,
                        TextFixed.videoUserTenMinuteText,
                        TypeNameAppPushEnum.VideoOrderTenTime.getCode(), String.valueOf(orderId), (int) userid, MessageTypeEnum.user.getCode(), dplatform, dToken); //app用户push消息

                systemService.addMessage("", TextFixed.messageServiceTitle,
                        MessageTypeEnum.doctor.getCode(), String.valueOf(orderId),
                        TypeNameAppPushEnum.VideoOrderTenTime.getCode(), doctorId, String.format(TextFixed.videoDoctorTenMinuteText, username), "");//app 医生 内推送
                systemService.addMessage("", TextFixed.messageServiceTitle,
                        MessageTypeEnum.user.getCode(), String.valueOf(orderId),
                        TypeNameAppPushEnum.VideoOrderTenTime.getCode(), (int) userid, TextFixed.videoUserTenMinuteText, "");//app 用户 内推送
                pushAppMapper.videoisTenTime(orderId);//修改推送状态
            }
        }
    }

    /**
     * 视频开始时推送
     */
    public void getVideoStartList() {
        List<Map<String, Object>> mapList = pushAppMapper.getVideoStartList();
        if (mapList.size() > 0) {
            for (Map<String, Object> map : mapList) {
                long orderId = ModelUtil.getInt(map, "orderid");
                int doctorId = ModelUtil.getInt(map, "doctorid");
                int dplatform = ModelUtil.getInt(map, "dplatform");
                String dToken = ModelUtil.getStr(map, "dtoken");
                long userid = ModelUtil.getLong(map, "userid");
                Map<String, Object> usernamemap = pushAppMapper.userName(userid);
                String username = ModelUtil.getStr(usernamemap, "name");
                pushAppMapper.addPushApp(TextFixed.messageServiceTitle,
                        String.format(TextFixed.videoDoctorStartTime, username),
                        TypeNameAppPushEnum.VideoOrderStartTime.getCode(), String.valueOf(orderId), doctorId, MessageTypeEnum.doctor.getCode(), dplatform, dToken); //app医生push消息
                pushAppMapper.addPushApp(TextFixed.messageServiceTitle,
                        TextFixed.videoUserStartTime,
                        TypeNameAppPushEnum.VideoOrderStartTime.getCode(), String.valueOf(orderId), (int) userid, MessageTypeEnum.user.getCode(), dplatform, dToken); //app用户push消息

                systemService.addMessage("", TextFixed.messageServiceTitle,
                        MessageTypeEnum.doctor.getCode(), String.valueOf(orderId),
                        TypeNameAppPushEnum.VideoOrderStartTime.getCode(), doctorId, String.format(TextFixed.videoDoctorStartTime, username), "");//app 医生 内推送
                systemService.addMessage("", TextFixed.messageServiceTitle,
                        MessageTypeEnum.user.getCode(), String.valueOf(orderId),
                        TypeNameAppPushEnum.VideoOrderStartTime.getCode(), (int) userid, TextFixed.videoUserStartTime, "");//app 用户 内推送
                pushAppMapper.videoisStart(orderId);//修改推送状态
            }
        }
    }



    /*@JmsListener(destination = "AppPush")
    public void pushApp(String text) {
        Map<String, Object> map = JsonUtil.getInstance().jsonToMap(new JSONObject(JSONObject.parseObject(text)));
        log.info("正在消费===============》" + text);
        int receivetype = ModelUtil.getInt(map, "receivetype");
        if (receivetype == 1) {
            mqPushUserApp(map);
        } else {
            mqPushDoctorApp(map);
        }
    }*/

    public void mqPushUserApp(Map<String, Object> map) {
        int platform = ModelUtil.getInt(map, "platform");//设备: 1:安卓 2:ios
        int receivetype = ModelUtil.getInt(map, "receivetype");//用户:1  医生:2
        int id = ModelUtil.getInt(map, "id");
        long appId = 0;
        String secretKey = "";
        if (platform == 1) { //安卓推送
            appId = 2100311976L;
            secretKey = "cdfb6146d029639faa609fea8380f054";
        } else if (platform == 2) {//ios推送
            appId = 2200312286L;
            secretKey = "2161b5929a8da066adfef2668a4045b5";
        }
        String title = ModelUtil.getStr(map, "title");
        String content = ModelUtil.getStr(map, "content");
        int type = ModelUtil.getInt(map, "type");
        String typeName = ModelUtil.getStr(map, "typename");
        String token = ModelUtil.getStr(map, "xgtoken");
        String result;
        try {
            if (platform == 1) {
                result = PushAppUtil.pushAndroid(appId, secretKey, title, content, getTypeName(type, typeName, receivetype), token);
            } else {
                result = PushAppUtil.pushIos(appId, secretKey, title, content, type, typeName, token);
            }
            log.info("mqpush>>>>" + result);
        } catch (Exception e) {
            result = e.getMessage();
            log.error("mqpush>>>>" + e.getMessage());
        }
    }

    public void mqPushDoctorApp(Map<String, Object> map) {
        int platform = ModelUtil.getInt(map, "platform");//设备: 1:安卓 2:ios
        int receivetype = ModelUtil.getInt(map, "receivetype");//用户:1  医生:2
        int id = ModelUtil.getInt(map, "id");
        long appId = 0;
        String secretKey = "";
        if (platform == 1) { //医生端推送安卓
            appId = 2100312083L;
            secretKey = "9550bc0af1ee65a1b2c211ac8b0d9b02";
        } else if (platform == 2) {//医生端推送ios
            appId = 2200312287L;
            secretKey = "ef0e72f5c37f6208691abaaefc07b7cd";
        }
        String title = ModelUtil.getStr(map, "title");
        String content = ModelUtil.getStr(map, "content");
        int type = ModelUtil.getInt(map, "type");
        String typeName = ModelUtil.getStr(map, "typename");
        String token = ModelUtil.getStr(map, "xgtoken");
        String result;
        try {
            if (platform == 1) {
                result = PushAppUtil.pushAndroid(appId, secretKey, title, content, getTypeName(type, typeName, receivetype), token);
            } else {
                result = PushAppUtil.pushIos(appId, secretKey, title, content, type, typeName, token);
            }
            log.info("mqpush>>>>" + result);
        } catch (Exception e) {
            log.error("mqpush>>>>" + e.getMessage());
        }
    }

    public void pushDoctorApp() {
        List<Map<String, Object>> mapList = pushAppMapper.getPushDoctorMessage();
        String result;
        for (Map<String, Object> map : mapList) {
            int id = ModelUtil.getInt(map, "id");
            if (ModelUtil.getInt(map, "type") == TypeNameAppPushEnum.inquiryUserReplyOrder.getCode() && ModelUtil.getInt(map, "isonline") == 1) {
                //医生在线时不推信鸽
                pushAppMapper.updateDoctorMessage(id, "用户在线不推送信鸽");
            } else {
                int platform = ModelUtil.getInt(map, "platform");//设备: 1:安卓 2:ios
                int receivetype = ModelUtil.getInt(map, "receivetype");//用户:1  医生:2
                long appId = 0;
                String secretKey = "";
                if (platform == 1) { //医生端推送安卓
                    appId = 2100312083L;
                    secretKey = "9550bc0af1ee65a1b2c211ac8b0d9b02";
                } else if (platform == 2) {//医生端推送ios
                    appId = 2200312287L;
                    secretKey = "ef0e72f5c37f6208691abaaefc07b7cd";
                }
                String title = ModelUtil.getStr(map, "title");
                String content = ModelUtil.getStr(map, "content");
                int type = ModelUtil.getInt(map, "type");
                String typeName = ModelUtil.getStr(map, "typename");
                String token = ModelUtil.getStr(map, "token");
                try {
                    if (platform == 1) {
                        result = PushAppUtil.pushAndroid(appId, secretKey, title, content, getTypeName(type, typeName, receivetype), token);
                    } else {
                        result = PushAppUtil.pushIos(appId, secretKey, title, content, type, typeName, token);
                    }
                } catch (Exception e) {
                    result = e.getMessage();
                    log.error("push>>>>" + e.getMessage());
                }
                pushAppMapper.updateDoctorMessage(id, result);
            }
        }
    }

    public void pushUserApp() {
        List<Map<String, Object>> mapList = pushAppMapper.getPushUserMessage();
        String result;
        for (Map<String, Object> map : mapList) {
            int id = ModelUtil.getInt(map, "id");
            if (ModelUtil.getInt(map, "type") == TypeNameAppPushEnum.inquiryDoctorReplyOrder.getCode() && ModelUtil.getInt(map, "isonline") == 1) {
                //用户在线时不推信鸽
                pushAppMapper.updateDoctorMessage(id, "用户在线不推送信鸽");

            } else {
                int platform = ModelUtil.getInt(map, "platform");//设备: 1:安卓 2:ios
                int receivetype = ModelUtil.getInt(map, "receivetype");//用户:1  医生:2
                long appId = 0;
                String secretKey = "";
                if (platform == 1) { //安卓推送
                    appId = 2100311976L;
                    secretKey = "cdfb6146d029639faa609fea8380f054";
                } else if (platform == 2) {//ios推送
                    appId = 2200312286L;
                    secretKey = "2161b5929a8da066adfef2668a4045b5";
                }
                String title = ModelUtil.getStr(map, "title");
                String content = ModelUtil.getStr(map, "content");
                int type = ModelUtil.getInt(map, "type");
                String typeName = ModelUtil.getStr(map, "typename");
                String token = ModelUtil.getStr(map, "token");
                try {
                    if (platform == 1) {
                        result = PushAppUtil.pushAndroid(appId, secretKey, title, content, getTypeName(type, typeName, receivetype), token);
                    } else {
                        result = PushAppUtil.pushIos(appId, secretKey, title, content, type, typeName, token);
                    }
                } catch (Exception e) {
                    result = e.getMessage();
                    log.error("push>>>>" + e.getMessage());
                }
                pushAppMapper.updateDoctorMessage(id, result);
            }
        }
    }

//    @JmsListener(destination = "appMessage")
//    public String sendMessage(Map<String, Object> map) {
//        String result;
////        for (Map<String, Object> map : mapList) {
//            int platform = ModelUtil.getInt(map, "platform");//设备: 1:安卓 2:ios
//            int receivetype = ModelUtil.getInt(map, "receivetype");//用户:1  医生:2
//            int id = ModelUtil.getInt(map, "id");
//            long appId = 0;
//            String secretKey = "";
//            if (platform == 1) { //安卓推送
//                appId = 2100311976L;
//                secretKey = "cdfb6146d029639faa609fea8380f054";
//            } else if (platform == 2) {//ios推送
//                appId = 2200312286L;
//                secretKey = "2161b5929a8da066adfef2668a4045b5";
//            }
//            String title = ModelUtil.getStr(map, "title");
//            String content = ModelUtil.getStr(map, "content");
//            int type = ModelUtil.getInt(map, "type");
//            String typeName = ModelUtil.getStr(map, "typename");
//            String token = ModelUtil.getStr(map, "token");
//            try {
//                if (platform == 1) {
//                    result = PushAppUtil.pushAndroid(appId, secretKey, title, content, getTypeName(type, typeName, receivetype), token);
//                } else {
//                    result = PushAppUtil.pushIos(appId, secretKey, title, content, type, typeName, token);
//                }
//            } catch (Exception e) {
//                result = e.getMessage();
//                log.error("push>>>>" + e.getMessage());
//            }
//            pushAppMapper.updateDoctorMessage(id, result);
//
//    }

    private String getTypeName(int type, String name, int receivetype) {
        String typeName = "";
        if (receivetype == 1) {
            switch (TypeNameAppPushEnum.getValue(type)) {
                case answerOrderDetail:      //用户端门诊详情
                    typeName = String.format(JumpLink.ANDROID_USERAPP_ANSWERDETAIL_ORDER, name);
                    break;
                case inquiryDoctorReplyOrder://问诊医生回复
                    typeName = String.format(JumpLink.ANDROID_USERAPP_CHATDETAIL_ORDER, name);
                    break;
                case phoneOrderDetail://用户端急诊详情
                    typeName = String.format(JumpLink.ANDROID_USERAPP_DEPARTMENTORDER, name);
                    break;
                case VideoOrderDetail:
                    typeName = String.format(JumpLink.ANDROID_USERAPP_VIDEODETAIL_ORDER, name);
                    break;
                case PhoneOrderTenTime://电话订单前十分钟推送
                    typeName = String.format(JumpLink.ANDROID_USERAPP_DEPARTMENTORDER, name);
                    break;
                case VideoOrderTenTime://视频订单前十分钟推送
                    typeName = String.format(JumpLink.ANDROID_USERAPP_VIDEODETAIL_ORDER, name);
                    break;
                case VideoOrderStartTime://视频订单开始时推送
                    typeName = String.format(JumpLink.ANDROID_USERAPP_VIDEODETAIL_ORDER, name);
                    break;
                case PhoneDiagnosisGuidance://电话诊后指导给用户推送
                    typeName = String.format(JumpLink.ANDROID_USERAPP_DEPARTMENTORDER, name);
                    break;
                case AnswerDiagnosisGuidance://图文诊后指导给用户推送
                    typeName = String.format(JumpLink.ANDROID_USERAPP_ANSWERDETAIL_ORDER, name);
                    break;
                case VideoDiagnosisGuidance://视频诊后指导给用户推送
                    typeName = String.format(JumpLink.ANDROID_USERAPP_VIDEODETAIL_ORDER, name);
                    break;
                case departmentCallFailUserOrder://用户电话呼叫失败
                    typeName = String.format(JumpLink.ANDROID_USERAPP_DEPARTMENTORDER, name);
                    break;
                case departmentCallSuccessUserOrder:
                    typeName = String.format(JumpLink.ANDROID_USERAPP_DEPARTMENTORDER, name);
                    break;
                case answerSuccessOrder:
                    typeName = String.format(JumpLink.ANDROID_USERAPP_ANSWERDETAIL_ORDER, name);
                    break;
                case videoSuccessOrder:
                    typeName = String.format(JumpLink.ANDROID_USERAPP_VIDEODETAIL_ORDER, name);
                    break;
                default:
                    break;
            }
        } else if (receivetype == 2) {
            switch (TypeNameAppPushEnum.getValue(type)) {
                case answerOrderDetail:    //医生端门诊详情
                    typeName = String.format(JumpLink.ANDROID_DOCTORAPP_ANSWERETAIL_ORDER, name);
                    break;
                case inquiryUserReplyOrder://问诊用户回复
                    typeName = String.format(JumpLink.ANDROID_DOCTORAPP_CHATDETAIL_ORDER, name);
                    break;
                case phoneOrderDetail://医生端急诊详情
                    typeName = String.format(JumpLink.ANDROID_DOCTORAPP_DEPARTMENTORDER, name);
                    break;
                case VideoOrderDetail:
                    typeName = String.format(JumpLink.ANDROID_DOCTORAPP_VIDEODETAIL_ORDER, name);
                    break;
                case PhoneOrderTenTime://电话订单前十分钟推送
                    typeName = String.format(JumpLink.ANDROID_DOCTORAPP_DEPARTMENTORDER, name);
                    break;
                case VideoOrderTenTime://视频订单开始时推送
                    typeName = String.format(JumpLink.ANDROID_DOCTORAPP_VIDEODETAIL_ORDER, name);
                    break;
                case VideoOrderStartTime://视频订单开始时推送
                    typeName = String.format(JumpLink.ANDROID_DOCTORAPP_VIDEODETAIL_ORDER, name);
                    break;
                case departmentCallSuccessDoctorOrder://医生电话呼叫成功
                    typeName = String.format(JumpLink.ANDROID_USERAPP_DEPARTMENTORDER, name);
                    break;
                case DoctorAfter:
                    typeName = String.format(JumpLink.ANDROID_DOCTORAPP_CHATDETAIL_ORDER, name);
                case answerSuccessOrder:
                    typeName = String.format(JumpLink.ANDROID_DOCTORAPP_ANSWERETAIL_ORDER, name);
                    break;
                case videoSuccessOrder:
                    typeName = String.format(JumpLink.ANDROID_USERAPP_VIDEODETAIL_ORDER, name);
                    break;
                default:
                    break;
            }
        }
        return typeName;
    }


    private String getHourName(int i) {
        switch (i) {
            case 0:
                return "halfhour";//半小时
            case 1:
                return "onehour";//一小时
            case 2:
                return "threehour";//三小时
            case 3:
                return "sixhour";//六小时
            case 4:
                return "twelvehour";//十二小时
            case 5:
                return "eighteenhour";//十八小时
            case 6:
                return "twentyhour";//二十小时
            default:
                return "";
        }
    }

    private long getHourLong(int i) {
        switch (i) {
            case 0:
                return 1800;
            case 1:
                return 3600;
            case 2:
                return 10800;
            case 3:
                return 21600;
            case 4:
                return 43200;
            case 5:
                return 64800;
            case 6:
                return 72000;
            default:
                return 80000;
        }
    }
}
