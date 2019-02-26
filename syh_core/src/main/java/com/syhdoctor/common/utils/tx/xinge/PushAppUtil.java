package com.syhdoctor.common.utils.tx.xinge;

import com.syhdoctor.common.utils.StrUtil;
import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PushAppUtil {


    private static Logger log = LoggerFactory.getLogger(PushAppUtil.class);


    //安卓推送app消息
    public static String pushAndroid(long appId, String secretKey, String title, String content, String typeName, String token) {
        //v2版本写法
        XingeApp xinge = new XingeApp(appId, secretKey);
        Message message = new Message();
        message.setType(1);
        message.setTitle(title);
        message.setContent(content);
        if (!StrUtil.isEmpty(typeName)) {
            ClickAction action = new ClickAction();
            action.setActionType(3);
            action.setIntent(typeName);
            message.setAction(action);
        }
        String result;
        try {
            result = xinge.pushSingleDevice(token, message).toString();
            log.info(result);
        } catch (Exception e) {
            result = e.getMessage();
            log.error("pushAndroid>>>>>" + e.getMessage());
        }
        return result;
        //java v3版本写法
        //        XingeApp xingeApp = new XingeApp(appId, secretKey);
//        Map<String, Object> customMp = new HashMap<>();
//        customMp.put("type", type);
//        customMp.put("typename", typeName);
//        String customStr = JsonUtil.getInstance().toJson(customMp);
//
//        ClickAction clickAction = new ClickAction();
//        clickAction.setActionType(ClickAction.TYPE_ACTIVITY);
//        clickAction.setActivity("syhscheme://com.syhdoctor.push/department_detail");
//
//        MessageAndroid messageAndroid = new MessageAndroid();
//        messageAndroid.setCustom_content(customStr);
//        messageAndroid.setAction(clickAction);
//
//        Message message = new Message();
//        message.setTitle(title);
//        message.setContent(content);
//        message.setAndroid(messageAndroid);
//
//        Map<String, Object> paramsMp = new HashMap<>();
//        paramsMp.put("audience_type", "token");
//        paramsMp.put("platform", Platform.android);
//        paramsMp.put("message", message);
//        paramsMp.put("message_type", MessageType.notify);
//        paramsMp.put("token_list", tokenList);
//
//        String params = JsonUtil.getInstance().gsonToJson(paramsMp);
//        String result;
//        try {
//            result = xingeApp.pushApp(params).toString();
//            System.out.println(result);
//            log.info(result);
//        } catch (Exception e) {
//            result = e.getMessage();
//            log.error("pushAndroid>>>>>" + e.getMessage());
//        }
//        return result;
    }


    //苹果推送app消息
    public static String pushIos(long appId, String secretKey, String title, String content, int type, String typeName, String token) {
        XingeApp xingeApp = new XingeApp(appId, secretKey);
        MessageIOS messageIOS = new MessageIOS();
        JSONObject object = new JSONObject();
        object.put("title", title);
        object.put("body", content);
        messageIOS.setAlert(object);
        Map<String, Object> custom = new HashMap<>();
        custom.put("typename", typeName);
        custom.put("type", type);
        messageIOS.setCustom(custom);
        String result;
        try {
            JSONObject jsonObject = xingeApp.pushSingleDevice(token, messageIOS, XingeApp.IOSENV_PROD);
            result = jsonObject.toString();
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }


}
