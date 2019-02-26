package com.syhdoctor.webserver.service.doctor;

import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.utils.QiniuUtils;
import me.chanjar.weixin.mp.api.WxMpService;

import java.io.File;
import java.io.FileInputStream;

public interface IDoctorAnswer {

    WxMpService getDxMpService();

    default String getMedia(String fileuri, String key, String mediaId) {
        try {
            File file = getDxMpService().getMaterialService().mediaDownload(mediaId);
            FileUtil.copyFile(file, fileuri + key + ".amr");
            FileUtil.changeToMp3(ConfigModel.BASEFILEPATH,fileuri + key + ".amr", fileuri + key + ".mp3");
            FileUtil.delFile(file);
            return QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, key, new FileInputStream(new File(fileuri + key + ".mp3")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "FAIL";
    }
}
