package com.syhdoctor.webserver.thirdparty.file;

import com.syhdoctor.common.utils.EnumUtils.FolderType;
import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import io.swagger.annotations.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Api(description = "医生上传图片专用接口")
@RestController
@RequestMapping("/localdoctor/upload")
public class LocalUploadDoctorController extends BaseController implements IFileUpload {

    @ApiOperation(value = "图片上传限制大小(不能超过200k)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "foldertype", value = "文件类型", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "indoccode", value = "医生编号", required = true, dataType = "String"),
    })
    @PostMapping("/uploadImgBySize")
    @Override
    public Map<String, Object> uploadImgBySize(@ApiParam(hidden = true) @RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        log.info("file>uploadImage 参数 " + params);
        try {
            int folderType = ModelUtil.getInt(params, "foldertype", -1);
            String inDocCode = ModelUtil.getStr(params, "indoccode");
            if (folderType == -1) {
                setErrorResult(result, "文件类型为必传");
            } else if (StrUtil.isEmpty(inDocCode)) {
                setErrorResult(result, "医生编号为必传");
            } else if (file == null) {
                setErrorResult(result, "请选择文件");
            } else if (file.getSize() < 100000 * 1024) {
                BufferedImage image = ImageIO.read(file.getInputStream());
                if (image != null) {
                    String filename = file.getOriginalFilename();
                    String type = filename.substring(filename.lastIndexOf("."));
                    String key = inDocCode + "_" + UnixUtil.getCustomRandomString() + type;
                    String folder = "";
                    if (folderType == FolderType.docPhoto.getCode()) {
                        folder = FolderType.docPhoto.getValue();
                    } else if (folderType == FolderType.card.getCode()) {
                        folder = FolderType.card.getValue();
                    } else if (folderType == FolderType.cert.getCode()) {
                        folder = FolderType.cert.getValue();
                    } else if (folderType == FolderType.certPrac.getCode()) {
                        folder = FolderType.certPrac.getValue();
                    } else if (folderType == FolderType.titleCert.getCode()) {
                        folder = FolderType.titleCert.getValue();
                    } else if (folderType == FolderType.signature.getCode()) {
                        folder = FolderType.signature.getValue();
                    } else if (folderType == FolderType.multiSitedLicRecord.getCode()) {
                        folder = FolderType.multiSitedLicRecord.getValue();
                    }
                    String localPath = FileUtil.setFileName(FileUtil.FILE_DOCTOR_PATH, inDocCode + "/" + folder + "/" + key);
                    String filePath = ConfigModel.BASEFILEPATH + localPath;
                    if (FileUtil.validateFile(filePath)) {
                        FileUtil.delFile(filePath);
                    }
                    FileUtil.saveFile(file.getBytes(), filePath);
                    Map<String, Object> data = new HashMap<>();
                    data.put("url", ConfigModel.DOCTORPICDOMAIN + ModelUtil.setLocalUrl(localPath));
                    data.put("key", ModelUtil.setLocalUrl(localPath));
                    result.put("data", data);
                    setOkResult(result, "上传成功!");
                    result.put("picdomain", ConfigModel.DOCTORPICDOMAIN);
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


    /**
     * 获取医生相关本地图片
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getLocalFile/{filename:.+}")
    public ResponseEntity<?> getLocalFile(@PathVariable String filename) {
        try {
            FileSystemResource file = new FileSystemResource(ConfigModel.BASEFILEPATH + filename.replace("-", "/"));
            String contentType = MediaType.IMAGE_JPEG_VALUE;
            String[] strArr = filename.split("\\.");
            if (strArr.length > 1) {
                String fileNameSuffix = strArr[1].toUpperCase();
                if (fileNameSuffix.equals("MP3") || fileNameSuffix.equals("MP4") || fileNameSuffix.equals("APK")) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                }
            }
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(file);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     *
     * @return
     */
    @GetMapping(value = "/downfile")
    @RequestMapping(method = RequestMethod.GET, value = "/downfile/{filename:.+}")
    public ResponseEntity<InputStreamResource> downfile(@PathVariable String filename) {
        Map<String, Object> result = new HashMap<>();
        try {
            FileSystemResource file = new FileSystemResource( ConfigModel.BASEFILEPATH + filename.replace("-", "/"));
            return responseEntity(file);
        } catch (Exception ex) {
            log.error(" DistributorSale>DelCreditOrder   ", ex);
            setErrorResult(result, ex.getMessage());
        }
        return null;
    }


    @RequestMapping(method = RequestMethod.POST, value = "/getLocalFileTest")
    public ResponseEntity<?> getLocalFiles(String filename) {
        try {
            FileSystemResource file = new FileSystemResource(ConfigModel.BASEFILEPATH + filename);
//            FileSystemResource file =new FileSystemResource("D:" + filename);
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


    @Override
    public Map<String, Object> uploadImgByWidthAndHeight(Map<String, Object> params, MultipartFile file) {
        return null;
    }


    @Override
    public Map<String, Object> uploadVoice(Map<String, Object> params, MultipartFile file) {
        return null;
    }

    @Override
    public Map<String, Object> uploadVideo(Map<String, Object> params, MultipartFile file) {
        return null;
    }
}
