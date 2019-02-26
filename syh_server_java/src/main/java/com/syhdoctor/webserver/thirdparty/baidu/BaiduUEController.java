package com.syhdoctor.webserver.thirdparty.baidu;

import com.alibaba.fastjson.JSON;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.common.utils.http.HttpUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.utils.QiniuUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiIgnore
@RestController
@RequestMapping("/baidu/upload")
public class BaiduUEController extends BaseController {

    @RequestMapping(value = "/ueditor")
    @ResponseBody
    public String ueditor(@RequestParam("action") String param, MultipartFile upfile, String source) {
        Ueditor ueditor = new Ueditor();
        if (param != null && param.equals("config")) {
            return PublicMsg.UEDITOR_CONFIG;
        } else if (param != null && param.equals("uploadimage")) {
            if (upfile != null) {
                //{state：”数据状态信息”，url：”图片回显路径”，title：”文件title”，original：”文件名称”，···}
                try {
                    return uploadImg(upfile);
                } catch (IOException e) {
                    log.error("/baidu/upload/ueditor  error", e);
                    ueditor.setState("出现异常");
                    return JSON.toJSONString(ueditor);
                }
            } else {
                ueditor.setState("文件为空");
                return JSON.toJSONString(ueditor);
            }
        } else if (param != null && param.equals("catchimage")) {
            List<?> urls = ModelUtil.getList(source, new ArrayList<>());
            List<Ueditor> list = new ArrayList<>();
            List<String> tempList = new ArrayList<>();
            for (Object obj : urls) {
                Ueditor model = new Ueditor();
                String url = (String) obj;
                if (!tempList.contains(url)) {
                    String name;
                    if (url.contains("?")) {
                        String temp = url.substring(0, url.lastIndexOf("?"));
                        name = temp.substring(temp.lastIndexOf("/") + 1);
                    } else {
                        name = url.substring(url.lastIndexOf("/") + 1);
                    }
                    String suffix = "";
                    if (name.contains(".")) {
                        suffix = name.substring(name.lastIndexOf(".") + 1);
                    }
                    String fileName = "baidu" + UnixUtil.getCustomRandomString() + "." + suffix;
                    QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, fileName, HttpUtil.getInstance().getInputStream(url));
                    model.setState("SUCCESS");
                    model.setSource(url);
                    model.setUrl(ConfigModel.QINIULINK + fileName);
                    list.add(model);
                    tempList.add(url);
                }
            }
            ueditor.setState("SUCCESS");
            Map<String, List<?>> json = new HashMap<>();
            json.put("list", list);
            return JSON.toJSONString(json);
        } else {
            ueditor.setState("不支持该操作");
            return JSON.toJSONString(ueditor);
        }
    }

    private String uploadImg(MultipartFile file) throws IOException {
        Ueditor ueditor = new Ueditor();
        String ct = file.getContentType();
        String fileType = "";
        if (ct.indexOf("/") > 0) {
            fileType = ct.substring(ct.indexOf("/") + 1);
        }
        String fileName = "baidu" + UnixUtil.getCustomRandomString() + "." + fileType;
        QiniuUtils.putFile(ConfigModel.QINIU.BUCKET, fileName, file.getInputStream());

        ueditor.setState("SUCCESS");
        ueditor.setTitle(fileName);
        ueditor.setOriginal(fileName);
        ueditor.setUrl(ConfigModel.QINIULINK + fileName);
        System.out.println(JSON.toJSONString(ueditor));
        return JSON.toJSONString(ueditor);
    }
}
