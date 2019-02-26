package com.syhdoctor.webtask.doctor.task;


import com.syhdoctor.webtask.doctor.service.DoctorOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class DoctorOrderTask {


    @Autowired
    private DoctorOrderService doctorOrderService;


    /**
     * 双向呼叫
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "12 0/1 * * * ?")
    private void doctorDutyTask() {
        doctorOrderService.directionalCall();
    }

    /**
     * 将七陌录音拉取到本地
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "22 * 0/1 * * ?")
    private void getPhoneOrderFile() {
        doctorOrderService.getPhoneOrderFile();
    }

    /**
     * 通话三十分钟还未结束订单
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "33 * 0/1 * * ?")
    private void phoneCloseOrderList() {
        doctorOrderService.phoneCloseOrderList();
    }

    /**
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "1 0/1 * * * ?")
    private void closeAnswerOrder() {
        doctorOrderService.closeAnswerOrder();//自动关闭问诊订单
    }

    /**
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "27 0/1 * * * ?")
    private void answerRefundOrder() {
        doctorOrderService.answerRefundOrder(); //问诊退款
    }

    /**
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "34 0/1 * * * ?")
    private void phoneRefundOrder() {
        doctorOrderService.phoneRefundOrder(); //急诊退款
    }

    /**
     * (秒/分/时/天/月)
     */
//    @Scheduled(cron = "41 0/1 * * * ?")
//    private void addPushOrderOneHour() {
//        doctorOrderService.addPushOrderOneHour();//订单结束前一个小时
//    }

    /**
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "53 0/1 * * * ?")
    private void extractOrderRefund() {
        doctorOrderService.extractOrderRefund();//体现失败退款
    }

    /**
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    private void closeVideoOrder() {
        doctorOrderService.closeVideoOrder();//自动关闭问诊订单
    }

    /**
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "57 0/1 * * * ?")
    private void videoOrderRefund() {
        doctorOrderService.videoOrderRefund();//
    }


    @Scheduled(cron = "0 12 13 25 9 ?")
    private void consumTask() {
        if (Calendar.getInstance().get(Calendar.YEAR) == 2018) {
            doctorOrderService.directionalCall();
        }
    }

}
