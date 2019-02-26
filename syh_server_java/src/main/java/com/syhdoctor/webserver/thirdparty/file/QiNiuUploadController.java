package com.syhdoctor.webserver.thirdparty.file;

import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.common.utils.ffmpeg.Encoder;
import com.syhdoctor.common.utils.ffmpeg.MultimediaInfo;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.utils.QiniuUtils;
import io.swagger.annotations.*;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Api(description = "七牛文件上传")
@RestController
@RequestMapping("/QiNiu/Upload")
public class QiNiuUploadController extends BaseController implements IFileUpload {

    @ApiOperation(value = "图片上传限制大小(不能超过200k)")
    @ApiImplicitParams({
    })
    @RequestMapping("/uploadImgBySize")
    @Override
    public Map<String, Object> uploadImgBySize(@ApiParam(hidden = true) @RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        log.info("file>uploadImage 参数 " + params);
        try {
            if (file == null) {
                setErrorResult(result, "请选择文件");
            } else if (file.getSize() < 100000 * 1024) {
                String filename = file.getOriginalFilename();
                String type = filename.substring(filename.lastIndexOf("."));
                String key = "syh" + UnixUtil.getCustomRandomString() + type;
                String v = QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, key, file.getInputStream());
                if (v.equals("FAIL")) {
                    setErrorResult(result, "上传失败");
                } else {
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
                setErrorResult(result, "文件过大");
            }
        } catch (Exception ex) {
            log.error("home>uploadImage error", ex);
            setErrorResult(result, ex.getMessage());
        }
        return result;
    }

    @ApiOperation(value = "图片上传限制尺寸")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "width", value = "宽度", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "height", value = "高度", required = true, dataType = "String")
    })
    @PostMapping("/uploadImgByWidthAndHeight")
    @Override
    public Map<String, Object> uploadImgByWidthAndHeight(@ApiParam(hidden = true) @RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        log.info("file>uploadImage 参数 " + params);
        try {
            int width = ModelUtil.getInt(params, "width", 0);
            int height = ModelUtil.getInt(params, "height", 0);
            if (file == null) {
                setErrorResult(result, "请选择文件");
            } else if (width > 0 || height > 0) {
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
                        QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, key, file.getInputStream());
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
            } else {
                setErrorResult(result, "请选择正确的图片");
            }
        } catch (Exception ex) {
            log.error("home>uploadImage error", ex);
            setErrorResult(result, ex.getMessage());
        }
        return result;
    }

    @PostMapping("/uploadVoice")
    @Override
    public Map<String, Object> uploadVoice(Map<String, Object> params, MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        log.info("file>uploadaudio 参数 " + params);
        try {
            if (file == null) {
                setErrorResult(result, "请选择文件");
            } else if (file.getSize() < 2000 * 1024) {
                String key = "syh" + UnixUtil.getCustomRandomString() + ".mp3";
                QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, key, file.getInputStream());
                String filePath = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_LONG_PATH, key);
                if (FileUtil.validateFile(filePath)) {
                    FileUtil.delFile(filePath);
                }

                FileUtil.saveFile(file.getBytes(), filePath);
                MP3File mp3File = (MP3File) AudioFileIO.read(new File(filePath));
                MP3AudioHeader audioHeader = (MP3AudioHeader) mp3File.getAudioHeader();
                Map<String, Object> data = new HashMap<>();
                data.put("url", ConfigModel.QINIULINK + key);
                data.put("key", key);
                data.put("track", audioHeader.getTrackLength());
                result.put("data", data);
                setOkResult(result, "上传成功!");
            } else {
                setErrorResult(result, "文件过大");
            }
        } catch (Exception ex) {
            log.error("home>uploadaudio error", ex);
            setErrorResult(result, ex.getMessage());
        }
        return result;
    }

    @PostMapping("/uploadVideo")
    @Override
    public Map<String, Object> uploadVideo(Map<String, Object> params, MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        log.info("file>uploadaudio 参数 " + params);
        try {
            if (file == null) {
                setErrorResult(result, "请选择文件");
            } else {
                String key = "syh" + UnixUtil.getCustomRandomString() + ".mp4";
                QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, key, file.getInputStream());
                String filePath = ConfigModel.BASEFILEPATH + FileUtil.setFileName(FileUtil.FILE_LONG_PATH, key);
                if (FileUtil.validateFile(filePath)) {
                    FileUtil.delFile(filePath);
                }

                FileUtil.saveFile(file.getBytes(), filePath);
                long videoTime = getVideoTime(filePath);
                Map<String, Object> data = new HashMap<>();
                data.put("url", ConfigModel.QINIULINK + key);
                data.put("key", key);
                data.put("track", videoTime);
                result.put("data", data);
                setOkResult(result, "上传成功!");
            }
        } catch (Exception ex) {
            log.error("home>uploadaudio error", ex);
            setErrorResult(result, ex.getMessage());
        }
        return result;
    }

    private long getVideoTime(String video_path) {
        try {
            Encoder encoder = new Encoder(ConfigModel.BASEFILEPATH);
            MultimediaInfo info = encoder.getInfo(new File(video_path));

            return Math.round((double) info.getDuration() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    @PostMapping("/putFile")
    public Map<String, Object> putFile(@RequestParam("file") MultipartFile file) {
        log.info("voice/File>putFile   params > ");
        Map<String, Object> result = new HashMap<>();
        try {
            String filename = file.getOriginalFilename();
            String type = filename.substring(filename.lastIndexOf("."));
            String key = "abbott" + UnixUtil.getCustomRandomString() + type;
            QiniuUtils.putFile("yuer", key, file.getInputStream());
            result.put("data", QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, key, file.getInputStream()));
            setOkResult(result, "查询成功!");
        } catch (Exception ex) {
            log.error(" voice/File>putFile   ", ex);
            setErrorResult(result, ex.getMessage());
        }
        return result;
    }
}
