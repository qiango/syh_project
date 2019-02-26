package com.syhdoctor.webserver.thirdparty.wechat;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.controller.webapp.appapi.doctor.QRCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("wx/qrcode")
public class QrcodeController extends BaseController {

    @Autowired
    private QRCodeUtil qrCodeUtil;

    @PostMapping("/qrcodeUrl")
    public Map<String, Object> qrcodeUrl(@RequestParam Map<String, Object> param) {
        log.info("wx/qrcode>qrcodeUrl   ");
        Map<String, Object> result = new HashMap<>();
        String sceneId = ModelUtil.getStr(param, "sceneId");
        try {
            setOkResult(result, "查询成功!");
        } catch (Exception ex) {
            log.error(" wx/qrcode>qrcodeUrl   ", ex);
            setErrorResult(result, ex.getMessage());
        }
        return result;
    }
}
