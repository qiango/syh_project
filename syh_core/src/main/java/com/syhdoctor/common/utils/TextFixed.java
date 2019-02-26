package com.syhdoctor.common.utils;

import java.math.BigDecimal;

public class TextFixed {


    public static String messageOrderTitle = "订单通知";
    public static String messageServiceTitle = "服务通知";
    public static String systemNotification = "系统通知";


    //图文订单支付成功推送用户标题
    public static String answerUserOrderPushTitleText = "订单支付成功";
    //图文订单支付成功推送用户内容
    public static String answerUserOrderPushText = "你于%s的图文订单已经支付成功,快去与医生沟通吧";
    public static String answerUserOrderSystemText = "你于%s的图文订单已经支付成功";



    //医生收到新的图文订单标题
    public static String answerDoctorOrderPushTitleText = "您有新的图文咨询订单";
    //医生收到新的图文订单内容
    public static String answerDoctorOrderText = "%s正在向您咨询，请尽快回复";
    public static String answerDoctorOrderSystemText = "患者%s正在向您咨询，请尽快回复";

    //用户图文订单完成推送内容
    public static String answerUserOrderSuccessPushText = "您的图文咨询服务已结束,请耐心等待医生填写诊后指导，谢谢";
    public static String answerUserOrderSuccessSystemText = "您的图文咨询服务已结束,请耐心等待医生填写诊后指导";
    //医生图文订单完成推送内容
    public static String answerDoctorOrderSuccessPushText = "患者%s的图文咨询服务已结束,快去对订单填写诊后指导吧";


    //一段时间后推送提醒标题
    public static String answerDoctorOrderWaitNewReplyTitleText = "你有图文订单未处理";
    //一段时间后推送提醒内容
    public static String answerDoctorOrderWaitNewReplyContentText = "%s患者向您预约图文服务,请在待接诊中回复患者吧";

    //视频订单支付成功推送用户标题
    public static String videoUserOrderPushTitleText = "预约成功";
    //视频订单支付成功推送用户内容
    public static String videoUserOrderPushText = "你于%s的视频订单已经预约成功，请您准时参加，谢谢";
    public static String videoUserOrderSystemText = "你于%s的视频订单已经预约成功";

    //医生收到新的视频订单标题
    public static String videoDoctorOrderPushTitleText = "您有新的视频咨询订单";
    //医生收到新的视频订单内容
    public static String videoDoctorOrderText = "%s患者向您预约了%s的视频问诊，请安排好时间";
    public static String videoDoctorOrderSystemText = "患者李君向您预约了%s的视频问诊";

    //用户视频问诊前十分钟
    public static String videoUserTenMinuteText = "您的视频咨询服务即将开始，请你准时参加，谢谢";
    //医生视频问诊前十分钟
    public static String videoDoctorTenMinuteText = "患者%s的视频咨询服务即将开始，请安排好时间";

    //用户视频问诊开始的时候
    public static String videoUserStartTime = "您的视频咨询服务已经开始啦，请您及时进入";
    //医生视频问诊开始的时候
    public static String videoDoctorStartTime = "患者%s的视频咨询服务已经开始啦，请您及时进入";

    //用户视频订单完成推送内容
    public static String videoUserOrderSuccessPushText = "您的视频咨询服务已结束,请耐心等待医生填写诊后指导，谢谢";
    public static String videoUserOrderSuccessSystemText = "您的视频咨询服务已结束,请耐心等待医生填写诊后指导";
    //医生视频订单完成推送内容
    public static String videoDoctorOrderSuccessPushText = "患者%s的视频咨询服务已结束,快去对订单填写诊后指导吧";


    //电话订单支付成功推送标题
    public static String phoneUserOrderPaySuccessPushTitleText = "预约成功";
    //电话订单支付成功推送内容
    public static String phoneUserOrderPaySuccessPushText = "你于%s的电话订单已经预约成功，请在预约时间保持通讯畅通，等待电话呼叫";
    public static String phoneUserOrderPaySuccessSystemText = "你于%s的电话订单已经预约成功";

    //急诊订单支付成功推送标题
    public static String phoneUserDepartmentOrderPaySuccessPushTitleText = "订单支付成功";
    //急诊订单支付成功推送内容
    public static String phoneUserDepartmentOrderPaySuccessPushText = "你于%s的电话订单支付成功,请保持通讯畅通,等待电话呼叫";
    public static String phoneUserDepartmentOrderPaySuccessSystemText = "你于%s的电话订单已经预约成功";

    //用户电话订单完成推送内容
    public static String phoneUserOrderSuccessPushText = "您的电话咨询服务已结束,请耐心等待医生填写诊后指导，谢谢";
    public static String phoneUserOrderSuccessPushSystemText = "您的电话咨询服务已结束,请耐心等待医生填写诊后指导";

    //医生电话订单完成推送内容
    public static String phoneDoctorOrderSuccessPushText = "患者%s的电话咨询服务已结束,快去对订单填写诊后指导吧";

    //电话订单推送给医生标题
    public static String phoneDoctorOrderPushTitleText = "您有新的电话咨询订单";
    //电话订单推送给医生内容
    public static String phoneDoctorOrderText = "患者%s向您预约了%s的电话咨询,请安排好时间";
    public static String phoneDoctorOrderSystemText = "患者%s向您预约了%s的电话咨询";

    //急诊订单推送给医生标题
    public static String phoneDoctorDepartmentOrderPushTitleText = "您有新的电话咨询订单";
    //急诊订单推送给医生内容
    public static String phoneDoctorDepartmentOrderText = "患者%s向你预约电话服务,请保持通讯畅通,等待电话呼叫";
    public static String phoneDoctorDepartmentOrderSystemText = "患者%s向你预约电话服务,请保持通讯畅通,等待电话呼叫";
    //通话失败推送
    public static String phoneUserOrderFailText = "你于%s的电话服务订单通话呼叫失败,请保持通讯畅通,等待下次呼叫";
    public static String phoneUserOrderFailSystemText = "你于%s的电话服务订单通话呼叫失败,请保持通讯畅通,";

    //用户电话问诊前十分钟
    public static String phoneUserTenMinuteText = "您的电话咨询服务即将开始，请在预约时间保持通讯畅通，等待电话呼叫";
    public static String phoneUserTenMinuteSystemText = "您的电话咨询服务即将开始，请在预约时间保持通讯畅通";
    //医生电话问诊前十分钟
    public static String phoneDoctorTenMinuteText = "%s患者的电话咨询服务即将开始，请安排好时间";

    //电话医生填写完问诊小结(诊后指导)
    public static String phoneDoctorSummaryText = "%s医生已经完成了本次电话服务的诊后指导，快去我的问诊内查看吧";
    public static String phoneDoctorSummarySystemText = "%s医生已经完成了本次电话服务的诊后指导，快去查看吧";
    //图文医生填写完问诊小结(诊后指导)
    public static String answerDoctorSummaryText = "%s医生已经完成了本次图文服务的诊后指导，快去我的问诊内查看吧";
    public static String answerDoctorSummarySystemText = "%s医生已经完成了本次图文服务的诊后指导，快去查看吧";
    //视频医生填写完问诊小结(诊后指导)
    public static String videoDoctorSummaryText = "%s医生已经完成了本次视频服务的诊后指导，快去我的问诊内查看吧";
    public static String videoDoctorSummarySystemText = "%s医生已经完成了本次视频服务的诊后指导，快去查看吧";

    public static String phoneUserSevenDays = "你好,你七天前使用过电话服务,如果你的病情有变化或有相关的病情咨询,你可以通过电话服务继续复诊";
    public static String answerUserSevenDays = "你好,你七天前使用过图文服务,如果你的病情有变化或有相关的病情咨询,你可以通过问诊服务继续复诊";

    //图文语音聊天推送
    public static String doctorServerVoiceTitle = "%s医生给您发来一条语音";
    public static String userServerVoiceTitle = "%s给您发来一条语音";

    //图文图文聊天推送
    public static String doctorServerTextTitle = "%s医生给您发来一条消息";
    public static String userServerTextTitle = "%s给您发来一条消息";

    //图文图片聊天推送
    public static String doctorServerImageTitle = "%s医生给您发来一张图片";
    public static String userServerImageTitle = "%s给您发来一张图片";

    public static String doctorCloseOrderText = "%s医生希望结束图文咨询服务，如还有问题请快速去问吧";

    //处方审核失败医生提示
    public static String prescriptionFailDoctorTips="您的处方笺审核未通过,请根据审核建议进行修改后重新提交";

    //用户端订单详情文案
    //首页-图文咨询(门诊)
    //待接诊
    public static String userOutpatientPaidTips = "图文咨询即将开始，可以将您的相关病例资料准备好，以便进行咨询。";
    //进行中
    public static String userOutpatientInCallTips = "图文咨询已开始，专家正在为您服务，请注意接收消息。";
    //交易完成
    public static String userOutpatientSuccessTips = "感谢您的支持，本次图文咨询已经结束，若需要复诊请到首页预约极速图文咨询服务。";
    //交易失败
    public static String userOutpatientFailTips = "您的咨询服务因故取消，如有需要可前往首页预约极速图文咨询服务或电话咨询服务。";

    //首页-电话咨询(急诊)
    //待接诊
    public static String userDepartmentPaidTips = "您的电话咨询即将开始，专业医学顾问为您服务，请保持手机通信畅通。";
    //进行中
    public static String userDepartmentInCallTips = "电话咨询已开始，请注意接听山屿海互联网医院的专属服务号码021-66623966。";
    //交易完成
    public static String userDepartmentSuccessTips = "电话咨询已经结束，若需要复诊，请到“首页”预约专业电话咨询服务。";
    //交易失败
    public static String userDepartmentFailTips = "您的咨询服务因故取消，如有需要您可前往首页预约专业电话咨询服务。";

    //专家详情-图文咨询(图文)
    //待接诊
    public static String userGraphicPaidTips = "图文咨询即将开始，可以将您的相关病例资料准备好，以便进行咨询。";
    //进行中
    public static String userGraphicInCallTips = "您的图文咨询已经开始，专家正在为您服务，请注意接收消息。";
    //交易完成
    public static String userGraphicSuccessTips = "图文咨询已结束，若需要复诊，请到“找专家”模块预约医生咨询服务。";
    //交易失败
    public static String userGraphicFailTips = "您的咨询服务因故取消，如有问题急需咨询，您可前往首页预约极速图文咨询服务。";

    //专家详情-电话咨询(电话)
    //待接诊
//    public static String userPhonePaidTips = "您的电话咨询即将开始，专业医生为您服务，请保持手机通信畅通。";
    public static String userPhonePaidTips = "您的电话咨询将在%s开始，专业医生为您服务，请保持手机通信畅通";
    //进行中
    public static String userPhoneInCallTips = "电话咨询已开始，请注意接听山屿海互联网医院的专属服务号码021-66623966。";
    //交易完成
    public static String userPhoneSuccessTips = "电话咨询已结束，若需要复诊，请到“找专家”模块预约医生咨询服务。";
    //交易失败
    public static String userPhoneFailTips = "您的咨询服务因故取消，如有问题急需咨询，您可前往首页预约专业电话咨询。";
    //专家详情-视频咨询
    //待接诊
    public static String userVideoPaidTips = "视频咨询将在%s开始，请保持手机通信畅通，建议在WIFI情况下使用。";
    //进行中
    public static String userVideoInCallTips = "视频咨询已经开始，请保持手机通信畅通，建议在WIFI情况下使用";
    //交易完成
    public static String userVideoSuccessTips = "视频咨询已经结束，若需要复诊，请到“找专家”模块预约医生咨询服务";
    //交易失败
    public static String userVideoFailTips = "您的咨询服务因故取消，如有问题急需咨询，您可前往首页预约专业电话咨询。";


    //医生端订单详情文案
    //电话
    //待接诊
    public static String doctorPhonePaidTips = "电话咨询将于%s开始，请保持手机通信畅通。";
    //进行中
    public static String doctorPhoneInCallTips = "您的电话咨询已开始，请保持手机通信畅通。";
    //交易完成 未填写诊断
    public static String doctorPhoneGuidanceFailTips = "感谢您的耐心解答，请您填写诊后指导，谢谢。";
    //交易完成 已经填写诊断
    public static String doctorPhoneGuidanceSuccessTips = "咨询已完成，感谢您的耐心解答。";
    //交易失败
    public static String doctorPhoneFailTips = "非常抱歉，您的订单因故取消，请您谅解。";
    //图文
    //待接诊
    public static String doctorGraphicPaidTips = "您有一个新的图文咨询订单，请及时接诊，非常感谢。";
    //进行中
    public static String doctorGraphicInCallTips = "图文咨询已开始，请及时为患者解答，非常感谢。";
    //交易完成 未填写诊断
    public static String doctorGraphicGuidanceFailTips = "感谢您的耐心解答，请您填写诊后指导，谢谢。";
    //交易完成 已经填写诊断
    public static String doctorGraphicGuidanceSuccessTips = "咨询已完成，感谢您的耐心解答。";
    //交易失败
    public static String doctorGraphicFailTips = "非常抱歉，您的订单因故取消，请您谅解。";

    //专家详情-视频咨询
    //待接诊
    public static String doctorVideoPaidTips = "视频咨询将于%s开始，请您准时参加并保持手机畅通。";
    //进行中
    public static String doctorVideoInCallTips = "您的视频咨询已开始，咨询时长%s分钟，请您尽快进入视频咨询。";
    //交易完成 未填写诊断
    public static String doctorVideoGuidanceFailTips = "感谢您的耐心解答，请您填写诊后指导，谢谢。";
    //交易完成 已经填写诊断
    public static String doctorVideoGuidanceSuccessTips = "咨询已完成，感谢您的耐心解答。";
    //交易失败
    public static String doctorVideoFailTips = "非常抱歉，您的订单因故取消，请您谅解。";

    //医生诊所图文文案
    public static String doctorAnswercClinicTips = "1、该服务为图文形式的问诊服务\n2、图文服务订单的服务时长为24小时，请您收到订单后尽快回复用户\n3、您可自主选择日常接诊的周期及时间段\n4、我们会根据您的排班时间，为您安排问诊订单\n";
    //医生诊所电话文案
    public static String doctorPhoneClinicTips = "1、该服务为电话问诊服务\n2、您可选择多个日期的不同时间段进行预约排班，可以持续添加预约排班日期\n3、自主选择坐班时间，选择完成后将有客服与您电话确认时间。\n4、届时将会有短息，app短消息的形式提醒您\n5、坐诊期间，平台收到用户的问诊订单，会进行双向电话呼叫，为您和患者接通问诊服务";
    //医生诊所视频文案
    public static String doctorVideoClinicTips = "1. 该服务为视频问诊服务。\n2. 您可以同时安排多天的视频问诊服务时间。\n3. 每次视频问诊的服务时间为20分钟，每次视频问诊至少间隔10分钟。\n4. 视频问诊开始前的15分钟会有手机短信通知。\n5. 您可以通过手机短信中的链接地址或“我的订单”进入视频问诊房间。\n6. 视频问诊结束后，需要填写诊后指导建议。";

    //图文文案
    public static String problem_start_tips = "问诊已经开始,本次问诊时间持续24小时";
    public static String problem_end_tips = "该问诊已结束";
    public static String problem_doctor_default = "您好，我是%s医院的%s医生，我已收到您的咨询单，我将尽快为您解答";
    public static String problem_user_tips = "您好，我是医学小助手，请您先配合如实回答几个问题，大概1-3分钟，我会规整好提交给医生，让医生能更好地为您服务。";
    public static String problem_doctor_tips = "患者已回答问诊小问题,您可以更加了解他的情况哦";
    public static String problem_submit_tips = "感谢您的回答，确认无误后我将转交给医生，稍后医生将为您解答。";

    //用户收到消息是否结束本次图文问诊
    public static String answerUserOrderText = "您好，请问您还有其他问题需要咨询吗？若没有，我将结束本次咨询服务。";
    //用户同意结束咨询用户收到消息
    public static String answerUserCloseOrderText = "感谢您的咨询，稍后会为您出具诊后指导，请在“我的问诊”中查看，祝您身体健康!";
    //用户不同意结束咨询用户收到消息
    public static String answerUserNotCloseOrderText = "您可以继续向我咨询问题，我将尽快为您解答。";
    //用户不同意结束咨询医生收到消息
    public static String answerDoctorNotCloseOrderText = "我还有问题咨询，请帮我解答。";
    //用户同意结束咨询医生收到消息
    public static String answerDoctorCloseOrderText = "患者已关闭咨询，感谢您的解答，请您填写诊后指导，谢谢";

    //绿通聊天文案
    public static String green_order = "您好，无法为您预约到心仪的医生，绿通订单已取消，订单费用将在3-5个工作日内退还给您，请您谅解，谢谢";
    public static String green_order_x = "系统通知";
    public static String green_order_over = "您好，您的绿通服务已完成，订单结束";
    public static String green_order_start = "您好，我是绿通服务的医学顾问，我已收到您的预约单，我将尽快为您处理，请稍等。";


    //二维码logo
    public static String qrcode_logo = "doctor_logo.png";

    //会员卡
    public static String body = "vip会员卡购买";

    //默认验证码
    public static String def_code = "6666";

    //后台充值接收验证码号码
    public static String phone = "15386173000"; //

    //        public static String phone = "13437194372"; //
    //订单结束时间带毫秒
    public static long auto_problem_order_time = 24 * 60 * 60 * 1000;

    //电话订单有效时间带毫秒
    public static long min_phone_order_time = 3 * 60 * 1000;

    //新患者时间
    public static long new_user_time = 2 * 60 * 60 * 1000;

    //医生关闭订单提示间隔时间
    public static long doctor_close_time = 5 * 60 * 1000;

    //医生新订单时间
    public static long doctor_homepage_new_order = 120000000;

    //用户最低充值金额；
    public static BigDecimal user_min_walletbalance = new BigDecimal(10);

    //电话支付描述
    public static String phone_pay_dec = "山屿海互联网医院-电话咨询";

    //图文支付描述
    public static String problem_pay_dec = "山屿海互联网医院-图文咨询";

    //视频支付描述
    public static String video_pay_dec = "山屿海互联网医院-视频咨询";

    //电话订单自动评价内容
    public static String doctor_phone_order_auto_diagnosis = "医生没有评价";

    //充值尊享会员
    public static String vip_card = "开通尊享会员";
    //续费尊享会员
    public static String vip_cards = "续费尊享会员";

    //医生签到添加的积分
    public static int doctor_sign_integral = 1;

    //用户签到添加的积分
    public static int user_sign_integral = 1;

    //公众号关注欢迎语
    public static String wechat_subscribe_push = "欢迎关注山屿海健康";

    //分享
    public static class Share {
        //大学列表
        public static String course_list_tile = "山屿海医生康养大学";
        public static String course_list_desc = "医学专家亲自授课";
        public static String course_list_imgurl = "https://resource.syhdoctor.com/syh20181204165929307957.png";

        //大学详情
        public static String course_detail_imgurl = "https://resource.syhdoctor.com/syh20181204165929307957.png";

        //头条列表
        public static String article_list_tile = "山屿海医生健康头条";
        public static String article_list_desc = "每天一条健康到永久";
        public static String article_list_imgurl = "https://resource.syhdoctor.com/syh20181204165929307957.png";

        //个人中心
        public static String personalcenter_title = "山屿海医生-首家中老年人专科互联网医院";
        public static String personalcenter_desc = "您的健康 我们来守护";
        public static String personalcenter_imgurl = "https://resource.syhdoctor.com/syh20181204165929307957.png";

        //头条详情
        public static String article_detail_imgurl = "https://resource.syhdoctor.com/syh20181204165929307957.png";

        //特色专科
        public static String specialties_detail_tile = "山屿海医生[%s]特色专科";
        public static String specialties_detail_desc = "专科治疗[%s]问题";
        public static String specialties_detail_imgurl = "https://resource.syhdoctor.com/syh20181204165929307957.png";

        //电话咨询
        public static String phone_title = "山屿海医生电话咨询";
        public static String phone_desc = "正规医生60秒接诊";
        public static String phone_imgurl = "https://resource.syhdoctor.com/syh20181204165929307957.png";

    }


}