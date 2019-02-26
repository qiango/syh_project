package com.syhdoctor.webtask.video.task;

import com.syhdoctor.webtask.video.service.RecordingVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class RecordingVideoTask {

    @Autowired
    private RecordingVideoService recordingVideoService;

    /**
     * 开始录制
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "30 0/1 * * * ?")
    private void start() {
        recordingVideoService.start();
    }

    /**
     * 停止录制
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    private void stop() {
        recordingVideoService.stop();
    }

    /**
     * 获取文件路径
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    private void getProperties() {
        recordingVideoService.getProperties();
    }

    /**
     * 创建token
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    private void createVideoToken() {
        recordingVideoService.createVideoToken();
    }

    @Scheduled(cron = "0 30 16 13 12 ?")
    private void consumTask() {
        if (Calendar.getInstance().get(Calendar.YEAR) == 2018) {
            recordingVideoService.createVideoToken();
        }
    }
}
