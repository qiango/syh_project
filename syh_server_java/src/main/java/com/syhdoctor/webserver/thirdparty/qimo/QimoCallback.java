package com.syhdoctor.webserver.thirdparty.qimo;

import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.service.doctor.DoctorPhoneService;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Qimo")
public class QimoCallback extends BaseController {

    @Autowired
    private DoctorPhoneService doctorPhoneService;

    @ApiOperation(value = "双向呼叫异步回调")
    @ApiImplicitParams({
    })
    @RequestMapping("/webcallCallback")
    @ResponseBody
    public void webcallCallback(HttpServletRequest request) {
        log.info("phoneorder =================webcallCallback");
        String orderNo = request.getParameter("actionid");
        log.info("actionid =================" + orderNo);
        String message = request.getParameter("Message");
        log.info("message =================" + message);
        doctorPhoneService.updatePhoneOrderStatus(orderNo, message);
    }

    @ApiOperation(value = "语音推送事件回调")
    @ApiImplicitParams({
    })
    @RequestMapping("/eventCallback")
    @ResponseBody
    public void eventCallback(HttpServletRequest request) {
        log.info("phoneorder =================eventCallback");
        String orderNo = request.getParameter("WebcallActionID");
        //接听状态：dealing（已接）,notDeal（振铃未接听）,leak（ivr放弃）,queueLeak（排队放弃）,blackList（黑名单）,voicemail（留言）
        String state = request.getParameter("State");
        String fileServer = request.getParameter("FileServer");
        String recordFile = request.getParameter("RecordFile");
        //Begin = 2015 - 06 - 12 20:14:50 & End = 2015 - 06 - 12 20:15:42
        String begin = request.getParameter("Begin");
        String end = request.getParameter("End");
        doctorPhoneService.updatePhoneOrderPhoneStatus(orderNo, state, begin, end, fileServer, recordFile);
    }
}
