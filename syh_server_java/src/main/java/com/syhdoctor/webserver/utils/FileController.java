package com.syhdoctor.webserver.utils;

import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@ApiIgnore
@RestController
@RequestMapping(value = "/file")
public class FileController extends BaseController {
    @RequestMapping(value = "/uploadImage")
    public Map<String, Object> uploadImage(@RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        log.info("file>uploadImage 参数 " + params);
        try {
            int width = ModelUtil.getInt(params, "width", 0);
            int height = ModelUtil.getInt(params, "height", 0);
            if (file == null) {
                setErrorResult(result, "请选择文件");
            } else /*if (width > 0 || height > 0)*/ {
                BufferedImage image = ImageIO.read(file.getInputStream());
                if (image != null) {
                    int iw = image.getWidth();
                    int ih = image.getHeight();
                    if ((width > 0 && width != iw) || (height > 0 && height != ih)) {
                        setErrorResult(result, "图片尺寸不正确");
                    } else {
                        String filename = file.getOriginalFilename();
                        String type = filename.substring(filename.lastIndexOf("."));
                        String key = "syh" + UnixUtil.getCustomRandomString() + type;
                        String filePath = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_LONG_PATH, key);
                        if (FileUtil.validateFile(filePath)) {
                            FileUtil.delFile(filePath);
                        }
                        FileUtil.saveFile(file.getBytes(), filePath);
                        Map<String, Object> data = new HashMap<>();
                        data.put("url", ConfigModel.QINIULINK + key);
                        data.put("key", key);
                        data.put("picdomain", ConfigModel.QINIULINK);
                        result.put("data", data);
                        setOkResult(result, "上传成功!");
                    }
                } else {
                    setErrorResult(result, "请选择正确的图片");
                }
            } /*else {
                setErrorResult(result, "请选择正确的图片");
            }*/
        } catch (Exception ex) {
            log.error("home>uploadImage error", ex);
            setErrorResult(result, ex.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/uploadaudio")
    public Map<String, Object> uploadAudio(@RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        log.info("file>uploadaudio 参数 " + params);
        try {
            if (file == null) {
                setErrorResult(result, "请选择文件");
            } else {
                String key = "syh" + UnixUtil.getCustomRandomString() + ".mp3";
                String filePath = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_LONG_PATH, key);
                if (FileUtil.validateFile(filePath)) {
                    FileUtil.delFile(filePath);
                }
                FileUtil.saveFile(file.getBytes(), filePath);
                MP3File mp3File = (MP3File) AudioFileIO.read(new File(filePath));
                MP3AudioHeader audioHeader = (MP3AudioHeader) mp3File.getAudioHeader();
                Map<String, Object> data = new HashMap<>();
                data.put("url", filePath);
                data.put("key", key);
                data.put("track", audioHeader.getTrackLength());
                result.put("data", data);
                setOkResult(result, "上传成功!");
            }
        } catch (Exception ex) {
            log.error("home>uploadaudio error", ex);
            setErrorResult(result, ex.getMessage());
        }
        return result;
    }
}
