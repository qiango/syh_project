package com.syhdoctor.webtask.doctorduty.task;


import com.syhdoctor.webtask.doctorduty.service.DoctorDutyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DoctorDutyTask {


    @Autowired
    private DoctorDutyService doctorDutyService;


    /**
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "59 59 23 ? * SUN")
    private void doctorDutyTask() {
        doctorDutyService.getDoctorDuty();
    }

    /**
     * 是否有排班(没有需要发送邮件)
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "0/10 0/1 * * * ?")
    private void isOnDuty() {
        try {
            doctorDutyService.isOnDuty();
        } catch (Exception e) {

        }

        //doctorDutyService.isOnDuty();
    }


    /**
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "0 0/2 * * * ?")
    private void doctorDutyTaskTest() {
        //pay.sendTemplate();
    }


    /**
     * 是否有排班(没有需要发送邮件)
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "0/10 0/1 * * * ?")
    private void isOnDutyTest() {
        try {
            doctorDutyService.isOnDuty();
        } catch (Exception e) {

        }
    }

}
