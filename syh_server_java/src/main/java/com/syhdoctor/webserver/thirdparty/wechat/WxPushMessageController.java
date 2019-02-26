package com.syhdoctor.webserver.thirdparty.wechat;

import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.mapper.wechat.PushMessageMapper;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author qian.wang
 * @description 公众号推送
 * @date 2018/10/26
 */
public class WxPushMessageController extends BaseController {

    @Autowired
    private WxMpTemplateMsgService wxMpTemplateMsgService;

    @Autowired
    private PushMessageMapper pushMessageMapper;


    /**
     *功能描述 通过MQ监听实时推送消息
     * @author qian.wang
     * @date 2018/10/26
     * @param  * @param message
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    /*@JmsListener(destination = "pushMessage.queue")
    public Map<String,Object> sendMessage(String message){
        log.info("====================>> 收到消息：" + message);
        Map<String,Object> result = new HashMap<>();
        WxMpTemplateMessage wxMpTemplateMessage=new WxMpTemplateMessage();
        String openid="";
        String templateId="";
        wxMpTemplateMessage.setTemplateId(openid);//模板id
        wxMpTemplateMessage.setToUser(templateId);//推送者id
        String name="";
        String value="";
        String color="";
        WxMpTemplateData wxMpTemplateData=new WxMpTemplateData(name,value,color);
        wxMpTemplateMessage.addData(wxMpTemplateData);
        try {
            result.put("data",wxMpTemplateMsgService.sendTemplateMsg(wxMpTemplateMessage));
            //保存记录
            storeMessage(openid,message);
            return result;
        } catch (WxErrorException e) {
            setErrorResult(result, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }*/

    /**
     *功能描述 保存推送的消息
     * @author qian.wang
     * @date 2018/10/26
     * @param  * @param opendid
     * @param message
     * @return void
     */
    public void storeMessage(String opendid,String message){
        pushMessageMapper.insert(opendid,message);
    }




}
