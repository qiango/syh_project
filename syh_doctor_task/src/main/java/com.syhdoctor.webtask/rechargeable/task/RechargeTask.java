package com.syhdoctor.webtask.rechargeable.task;

import com.syhdoctor.webtask.rechargeable.service.RechargeableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author qian.wang
 * @description
 * @date 2018/10/23
 */

@Component
public class RechargeTask {

    @Autowired
    private RechargeableService rechargeableService;

    /**
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "0 0/2 * * * ?")// .. 0 0/1 * * * ?
    private void pushUserApp() {
        rechargeableService.addCard(); //生成充值卡
    }

}
