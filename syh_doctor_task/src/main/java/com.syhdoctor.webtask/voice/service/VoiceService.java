package com.syhdoctor.webtask.voice.service;

import com.aliyun.oss.ServiceException;
import com.syhdoctor.common.utils.*;
import com.syhdoctor.common.utils.alidayu.LongTtsUtil;
import com.syhdoctor.webtask.base.service.BaseService;
import com.syhdoctor.webtask.config.ConfigModel;
import com.syhdoctor.webtask.voice.mapper.VoiceMapper;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2018/10/25
 */
@Service
public class VoiceService extends BaseService {

    @Autowired
    private VoiceMapper voiceMapper;

    public void updateVoice(){
        List<Map<String,Object>> list=voiceMapper.getArticleList();
        for(Map<String,Object> map:list){
            long id= ModelUtil.getLong(map,"articleid");
            String articledetail=ModelUtil.getStr(map,"articledetail");
            Map<String,Object> value=changeVoice(articledetail);
            voiceMapper.updateArticle(id,value.get("key"),value.get("track"));
        }
    }

    public Map<String,Object> changeVoice(String articledetail){
        Map<String,Object> map=new HashMap<>();
        int track = 0;
        String key = "";
        String articleNoHtmlDetail = StrUtil.delHTMLTag(articledetail).replaceAll("[&nbsp;]", "").replaceAll("[ ]", "");
        log.info("addUpdateArticle >>[" + articleNoHtmlDetail + "]");
        String fileName = UnixUtil.getCustomRandomString();
        LongTtsUtil.saveTts(articleNoHtmlDetail, ConfigModel.BASEFILEPATH, fileName);
        key = "syh" + UnixUtil.getCustomRandomString() + ".mp3";
        try {
            String localFileName = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_ARTICLE_PATH, fileName + ".mp3");
            if (!StrUtil.isEmpty(localFileName) && FileUtil.validateFile(localFileName)) {
                QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, key, new FileInputStream(localFileName));
                MP3File mp3File = (MP3File) AudioFileIO.read(new File(localFileName));
                MP3AudioHeader audioHeader = (MP3AudioHeader) mp3File.getAudioHeader();
                track = audioHeader.getTrackLength();
            }
        } catch (Exception e) {
            log.error("addUpdateArticle error", e);
            throw new ServiceException("文本解析音频失败");
        }
        map.put("key",key);
        map.put("track",track);
        return map;
    }

}
