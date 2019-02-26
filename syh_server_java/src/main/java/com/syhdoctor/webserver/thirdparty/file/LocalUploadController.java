package com.syhdoctor.webserver.thirdparty.file;

import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.common.utils.ffmpeg.Encoder;
import com.syhdoctor.common.utils.ffmpeg.MultimediaInfo;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import io.swagger.annotations.*;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.rmi.ServerException;
import java.util.HashMap;
import java.util.Map;

@Api(description = "本地文件上传")
@RestController
@RequestMapping("/Local/Upload")
public class LocalUploadController extends BaseController implements IFileUpload {

    @ApiOperation(value = "图片上传限制大小(不能超过200k)")
    @ApiImplicitParams({
    })
    @PostMapping("/uploadImgBySize")
    @Override
    public Map<String, Object> uploadImgBySize(@ApiParam(hidden = true) @RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        log.info("file>uploadImage 参数 " + params);
        try {
            if (file == null) {
                setErrorResult(result, "请选择文件");  
            } else if (file.getSize() < 200 * 1024) {
                BufferedImage image = ImageIO.read(file.getInputStream());
                if (image != null) {
                    String filename = file.getOriginalFilename();
                    String type = filename.substring(filename.lastIndexOf("."));
                    String key = "syh" + UnixUtil.getCustomRandomString() + type;
                    String fileName = FileUtil.setFileName(FileUtil.FILE_LONG_PATH, key);
                    String filePath = ConfigModel.BASEFILEPATH + fileName;
                    if (FileUtil.validateFile(filePath)) {
                        FileUtil.delFile(filePath);
                    }
                    FileUtil.saveFile(file.getBytes(), filePath);
                    Map<String, Object> data = new HashMap<>();
                    data.put("url", ConfigModel.LOCALIMGLINK + ModelUtil.setLocalUrl(fileName));
                    data.put("key", ModelUtil.setLocalUrl(fileName));
                    data.put("picdomain", ConfigModel.LOCALIMGLINK);
                    result.put("data", data);
                    setOkResult(result, "上传成功!");
                } else {
                    setErrorResult(result, "请选择正确的图片");
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

    @ApiOperation(value = "图片上传")
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
                        String fileName = FileUtil.setFileName(FileUtil.FILE_LONG_PATH, key);
                        String filePath = ConfigModel.BASEFILEPATH + fileName;
                        if (FileUtil.validateFile(filePath)) {
                            FileUtil.delFile(filePath);
                        }
                        FileUtil.saveFile(file.getBytes(), filePath);
                        Map<String, Object> data = new HashMap<>();
                        data.put("url", ConfigModel.LOCALIMGLINK + ModelUtil.setLocalUrl(fileName));
                        data.put("key", ModelUtil.setLocalUrl(fileName));
                        result.put("result", 1);
                        result.put("message", "上传成功");
                        result.put("picdomain", ConfigModel.LOCALIMGLINK);
                        result.put("data", data);
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

    @ApiOperation(value = "apk上传")
    @ApiImplicitParams({
    })
    @PostMapping("/uploadApk")
    public Map<String, Object> uploadApk(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (file == null) {
                setErrorResult(result, "请选择文件");
            }
            InputStream inputStream = file.getInputStream();
            if (inputStream != null) {
                String filename = file.getOriginalFilename();   //文件名
                String finalName=filename.replace("-","_");
                String finalNames=finalName.replace("/","_");
                String suffix = filename.substring(filename.lastIndexOf(".") + 1);
                if (!"apk".equals(suffix)) {
                    throw new ServerException("所传格式不为apk，请检查");
                }
                String fileName = FileUtil.setFileName(FileUtil.FILE_APK_PATH, finalNames);
                String filePath = ConfigModel.BASEFILEPATH + fileName;
                if (FileUtil.validateFile(filePath)) {
                    FileUtil.delFile(filePath);
                }
                FileUtil.saveFile(file.getBytes(), filePath);
                Map<String, Object> data = new HashMap<>();
                data.put("name", ConfigModel.APKPICDOMAIN + ModelUtil.setLocalUrl(fileName));
                result.put("result", 1);
                result.put("message", "上传成功");
                result.put("picdomain", ConfigModel.LOCALIMGLINK);
                result.put("data", data);
            }
        } catch (Exception ex) {
            log.error("home>uploadImage error", ex);
            setErrorResult(result, ex.getMessage());
        }
        return result;
    }

    public static void main(String[] args) {
        String filename = "shenjun.ysy";   //文件名
        String suffix = filename.substring(filename.indexOf("h") +1);
        String fileName = FileUtil.setFileName(FileUtil.FILE_APK_PATH, filename);
        String filePath = ConfigModel.BASEFILEPATH + fileName;
        File file = new File(fileName);
        System.out.println(file.exists());//文件是否存在  file.delete() 删除文件
        System.out.println(filePath);
        System.out.println(fileName);
        System.out.println(suffix);
        System.out.println(filename.indexOf(".") +1);
    }

    @PostMapping("/uploadVoice")
    @Override
    public Map<String, Object> uploadVoice(Map<String, Object> params, MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        log.info("file>uploadaudio 参数 " + params);
        try {
            if (file == null) {
                setErrorResult(result, "请选择文件");
            } else {
                String key = "syh" + UnixUtil.getCustomRandomString() + ".mp3";
                String fileName = FileUtil.setFileName(FileUtil.FILE_LONG_PATH, key);
                String filePath = ConfigModel.BASEFILEPATH + fileName;
                if (FileUtil.validateFile(filePath)) {
                    FileUtil.delFile(filePath);
                }
                FileUtil.saveFile(file.getBytes(), filePath);
                MP3File mp3File = (MP3File) AudioFileIO.read(new File(filePath));
                MP3AudioHeader audioHeader = (MP3AudioHeader) mp3File.getAudioHeader();
                Map<String, Object> data = new HashMap<>();
                data.put("url", ConfigModel.LOCALIMGLINK + fileName);
                data.put("key", ModelUtil.setLocalUrl(fileName));
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
                String fileName = FileUtil.setFileName(FileUtil.FILE_LONG_PATH, key);
                String filePath = ConfigModel.BASEFILEPATH + fileName;
                if (FileUtil.validateFile(filePath)) {
                    FileUtil.delFile(filePath);
                }
                FileUtil.saveFile(file.getBytes(), filePath);
                Encoder encoder = new Encoder(ConfigModel.BASEFILEPATH);
                MultimediaInfo info = encoder.getInfo(new File(filePath));
                long ls = info.getDuration();
                log.info("此视频时长为:" + ls / 60000 + "分" + (ls) / 1000 + "秒！");

                Map<String, Object> data = new HashMap<>();
                data.put("url", ConfigModel.LOCALIMGLINK + ModelUtil.setLocalUrl(fileName));
                data.put("key", ModelUtil.setLocalUrl(fileName));
                result.put("data", data);
                setOkResult(result, "上传成功!");
            }
        } catch (Exception ex) {
            log.error("home>uploadaudio error", ex);
            setErrorResult(result, ex.getMessage());
        }
        return result;
    }

    /**
     * 获取本地图片
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getLocalImg/{filename:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<?> getLocalImg(@PathVariable String filename) {
        try {
            FileSystemResource file = new FileSystemResource(ConfigModel.BASEFILEPATH + filename.replace("-", "/"));
            String contentType = MediaType.IMAGE_JPEG_VALUE;
            String[] strArr = filename.split("\\.");
            if (strArr.length > 1) {
                String fileNameSuffix = strArr[1].toUpperCase();
                if (fileNameSuffix.equals("MP3") || fileNameSuffix.equals("MP4")) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                }
            }
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(file);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


}
