package com.syhdoctor.common.utils;


public class JumpLink {

    //银联获取token
    public static final String UNIONPAY_TOKEN = "https://openapi.unionpay.com/upapi/cardbin/token?app_id=%s&app_secret=%s";

    //银联银行卡信息
    public static final String UNIONPAY_CARDINFO = "https://openapi.unionpay.com/upapi/cardbin/cardinfo?token=%s&sign=%s&ts=%s";

    //七陌双向呼叫
    public static final String QIMO_WEBCALL = "http://apis.7moor.com/v20160818/webCall/webCall/N00000032760?sig=%s";


    /**
     * 安卓action医生端跳转链接  开始
     */
    //急诊详情
    public static final String ANDROID_DOCTORAPP_DEPARTMENTORDER = "syhdoctor://com.syhdoctor.doctor/department_detail?typename=%s";

    //门诊聊天详情
    public static final String ANDROID_DOCTORAPP_CHATDETAIL_ORDER = "syhdoctor://com.syhdoctor.doctor/chatdetail_detail?typename=%s";

    //门诊订单详情
    public static final String ANDROID_DOCTORAPP_ANSWERETAIL_ORDER = "syhdoctor://com.syhdoctor.doctor/graphic_detail?typename=%s";

    //视频详情
    public static final String ANDROID_DOCTORAPP_VIDEODETAIL_ORDER = "syhdoctor://com.syhdoctor.doctor/video_detail?typename=%s";

    /**
     * 安卓action医生端跳转链接  结束
     */

    /**
     * 安卓action用户端跳转链接  开始
     */
    //急诊详情
    public static final String ANDROID_USERAPP_DEPARTMENTORDER = "syhuser://com.syhdoctor.user/department_detail?typename=%s";

    //门诊聊天详情
    public static final String ANDROID_USERAPP_CHATDETAIL_ORDER = "syhuser://com.syhdoctor.user/chatdetail_detail?typename=%s";

    //门诊订单详情
    public static final String ANDROID_USERAPP_ANSWERDETAIL_ORDER = "syhuser://com.syhdoctor.user/graphic_detail?typename=%s";

    //视频详情
    public static final String ANDROID_USERAPP_VIDEODETAIL_ORDER = "syhuser://com.syhdoctor.user/video_detail?typename=%s";

    //用户端 图文诊断详情
    public static final String ANDROID_USERAPP_ANSWER_DIAGNOSISGUIDANCE = "syhuser://com.syhdoctor.user/graphic_diagnosis?typename=%s";

    //用户端 电话诊断详情
    public static final String ANDROID_USERAPP_PHONE_DIAGNOSISGUIDANCE = "syhuser://com.syhdoctor.user/phone_diagnosis?typename=%s";

    //用户端 视频诊断详情
    public static final String ANDROID_USERAPP_VIDEO_DIAGNOSISGUIDANCE = "syhuser://com.syhdoctor.user/video_diagnosis?typename=%s";

    /**
     * 安卓action用户端跳转链接  结束
     */


    //康养云信鸽推送链接
    public static final String KANGYANG_XG_URL = "http://47.96.96.132:5015/kangyang/api/pushData";

    //康养支付
    public static final String KANGYANG_PAY_URL = "http://112.124.70.173:5014/kangyang/api/payToInternetHospital";
}
