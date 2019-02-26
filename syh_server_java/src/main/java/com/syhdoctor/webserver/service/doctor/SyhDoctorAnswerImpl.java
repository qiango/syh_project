package com.syhdoctor.webserver.service.doctor;

import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SyhDoctorAnswerImpl implements IDoctorAnswer {


    @Autowired
    private WxMpService wxMpService;

    @Override
    public WxMpService getDxMpService() {
        return wxMpService;
    }

}
