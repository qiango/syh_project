package com.syhdoctor.webtask.pushapp.task;

import com.syhdoctor.webtask.pushapp.service.PushAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class PushAppTask {


    @Autowired
    private PushAppService pushAppService;

    /**
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "0/50 * * * * ?")
    private void pushUserApp() {
        pushAppService.pushUserApp(); //用户端推送

        pushAppService.getWaitDoctorProblemOrder(); //时间段推送

        pushAppService.getDepartmentOrderBySevenDays();//七天后推送

        pushAppService.getAnswerOrderBySevenDays();//七天后推送

    }

    /**
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "0/40 * * * * ?")
    private void pushDoctorApp() {
        pushAppService.pushDoctorApp(); //医生端推送
    }


    @Scheduled(cron = "57 16 * * * ?")
    private void pushAppTest() {
        if (Calendar.getInstance().get(Calendar.YEAR) == 2018) {
//            pushAppService.pushDoctorApp();
            /*pushAppService.pushUserApp();*/
//            pushAppService.getWaitDoctorProblemOrder();
        }
    }

    @Scheduled(cron = "0 0/3 * * * ?")
    private void getPhoneTenTime() {
        pushAppService.getPhoneTenTime();//电话提前十分钟推送信息

        pushAppService.getVideoTenTimeList();//视频提前十分钟推送信息

    }

    @Scheduled(cron = "0 0/1 * * * ?")
    private void getPhoneStart() {
        pushAppService.getVideoStartList();//视频开始推送消息
    }


}
