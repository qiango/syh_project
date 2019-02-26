package com.syhdoctor.webtask.vipcard.task;

import com.syhdoctor.webtask.vipcard.service.VipCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author qian.wang
 * @description
 * @date 2018/11/12
 */
@Component
public class VipCardExpriTime {

    @Autowired
    private VipCardService vipCardService;


    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨1点
//    @Scheduled(cron = "0 0/1 * * * ?")
    private void getPhoneOrderFile() {
        vipCardService.updateList();
    }

}