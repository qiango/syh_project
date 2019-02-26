package com.syhdoctor.webtask.voice.task;

import com.syhdoctor.webtask.voice.service.VoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author qian.wang
 * @description
 * @date 2018/10/25
 */
@Component
public class VoiceTask {

    @Autowired
    private VoiceService voiceService;

    /**
     * 更新文章音频
     * (秒/分/时/天/月)
     */
    @Scheduled(cron = "20 0/11 * * * ?")
    private void getPhoneOrderFile() {
        voiceService.updateVoice();
    }

}
