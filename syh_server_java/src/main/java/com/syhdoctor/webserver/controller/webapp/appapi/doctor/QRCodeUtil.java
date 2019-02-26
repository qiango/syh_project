package com.syhdoctor.webserver.controller.webapp.appapi.doctor;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class QRCodeUtil {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected WxMpService wxMpService;

    public String qrcode(String sceneId) {
        try {
            WxMpQrCodeTicket ticket = wxMpService.getQrcodeService().qrCodeCreateLastTicket(sceneId);
            return wxMpService.getQrcodeService().qrCodePictureUrl(ticket.getTicket());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
