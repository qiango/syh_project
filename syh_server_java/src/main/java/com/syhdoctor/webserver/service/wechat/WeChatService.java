package com.syhdoctor.webserver.service.wechat;

import com.syhdoctor.common.utils.EnumUtils.OpenTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.TextFixed;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.service.user.UserService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WeChatService extends BaseService {

    @Autowired
    private UserService userService;

    /**
     * 接受微信通知
     *
     * @param value 接受微信参数
     */
    public String getWxQrCodeParam(Map<String, Object> value) {
        String str = "";
        String event = ModelUtil.getStr(value, "Event");
        String eventKey = ModelUtil.getStr(value, "EventKey");
        String fromUserName = ModelUtil.getStr(value, "FromUserName");
        String toUserName = ModelUtil.getStr(value, "ToUserName");
        String msgType = ModelUtil.getStr(value, "MsgType");
        long id = 0;
        log.info("微信事件推送---------------------");
        log.info("event---------------------" + event);
        log.info("eventKey---------------------" + eventKey);
        log.info("fromUserName---------------------" + fromUserName);
        log.info("toUserName---------------------" + toUserName);
        log.info("msgType---------------------" + msgType);
        if (("subscribe".equals(event) || "SCAN".equals(event)) && !StrUtil.isEmpty(eventKey)) {
            log.info("扫码开始 用户未关注时，进行关注后的事件推送 或者 用户已关注时的事件推送----------------");
            int doctorid = 0;
            try {
                /*int qrcodeType = 0;
                if ("subscribe".equals(event)) {
                    String[] key = eventKey.split("_");
                    if (key.length == 2) {
                        doctorid = ModelUtil.strToInt(key[1], 0);
                    } else if (key.length == 3) {
                        doctorid = ModelUtil.strToInt(key[2], 0);
                        qrcodeType = ModelUtil.strToInt(key[1], 0);
                    } else if (key.length == 1) {
                        doctorid = ModelUtil.strToInt(key[1], 0);
                    }
                } else if ("SCAN".equals(event)) {
                    String[] key = eventKey.split("_");
                    if (key.length == 2) {
                        doctorid = ModelUtil.strToInt(key[1], 0);
                        qrcodeType = ModelUtil.strToInt(key[0], 0);
                    } else {
                        doctorid = ModelUtil.strToInt(eventKey, 0);
                    }
                }*/
                id = userService.addUpdateUserOpen(fromUserName, ConfigModel.USER_CHANNEL.WECHAT_QRCODE, OpenTypeEnum.Wechat.getCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("subscribe".equals(event) && StrUtil.isEmpty(eventKey)) {
            log.info("自己关注未扫医生二维码----------------");
            id = userService.addUpdateUserOpen(fromUserName, ConfigModel.USER_CHANNEL.WECHAT, OpenTypeEnum.Wechat.getCode());
            str = firstPush(fromUserName, toUserName);
        }//TODO 微信点击事件

        if ("CLICK".equals(event)) {
            id = userService.addUpdateUserOpen(fromUserName, ConfigModel.USER_CHANNEL.WECHAT, OpenTypeEnum.Wechat.getCode());
            long userid = userService.getUserAccount(fromUserName, OpenTypeEnum.Wechat.getCode());
        }

        //取消、关注
        if ("unsubscribe".equals(event) || "subscribe".equals(event)) {
            if (id != 0) {
                if ("unsubscribe".equals(event)) {
                    userService.updateUserOpen(id, 0, OpenTypeEnum.Wechat.getCode());
                } else {
                    userService.updateUserOpen(id, 1, OpenTypeEnum.Wechat.getCode());
                }
            }
        }

        if ("text".equals(msgType)) {
        }
        //userService.updateUserAccount(userid);//更新用户最后活跃时间
        return str;
    }

    /**
     * 关注推送
     *
     * @param openid     openid
     * @param toUserName toUserName
     */
    public String firstPush(String openid, String toUserName) {
        return WxMpXmlOutMessage.TEXT()
                .content(TextFixed.wechat_subscribe_push)
                .fromUser(toUserName)
                .toUser(openid)
                .build().toXml();
    }
}
