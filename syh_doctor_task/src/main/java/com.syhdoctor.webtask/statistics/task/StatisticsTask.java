package com.syhdoctor.webtask.statistics.task;

import com.syhdoctor.webtask.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author qian.wang
 * @description
 * @date 2019/1/3
 */
@Component
public class StatisticsTask {


    @Autowired
    private StatisticsService statisticsService;

    /**
     * 服务统计经营状况
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "0 0 3 * * ?")//每天凌晨3点
    private void saveStatistics() {
        statisticsService.saveStatistics();
    }

    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨1点
    private void userStatistics() {
        statisticsService.userStatisticsAdd();//用户统计

        statisticsService.ageAdd();//年龄柱状图

        statisticsService.addUserStatisticsRegion();//地域
    }


}
