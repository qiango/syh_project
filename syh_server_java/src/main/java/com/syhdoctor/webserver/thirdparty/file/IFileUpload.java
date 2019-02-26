package com.syhdoctor.webserver.thirdparty.file;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface IFileUpload {

    Map<String, Object> uploadImgByWidthAndHeight(@RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file);

    Map<String, Object> uploadImgBySize(@RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file);

    Map<String, Object> uploadVoice(@RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file);

    Map<String, Object> uploadVideo(@RequestParam Map<String, Object> params, @RequestParam("file") MultipartFile file);
}
