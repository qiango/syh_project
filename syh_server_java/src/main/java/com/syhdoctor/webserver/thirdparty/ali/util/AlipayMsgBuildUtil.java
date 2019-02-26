/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.syhdoctor.webserver.thirdparty.ali.util;


import com.syhdoctor.webserver.thirdparty.ali.constants.AlipayServiceEnvConstants;

import java.util.Calendar;

/**
 * 消息构造工具
 * 
 * @author baoxing.gbx
 * @version $Id: AlipayMsgBuildUtil.java, v 0.1 Jul 24, 2014 5:47:19 PM baoxing.gbx Exp $
 */
public class AlipayMsgBuildUtil {

    /**
     * 构造单发图文消息
     * 
     * @param fromUserId
     * @return
     */
    public static String buildSingleImgTextMsg(String fromUserId) {

        StringBuilder sb = new StringBuilder();

        //构建json格式单发图文消息: 所有内容开发者请根据自有业务自行设置响应值，这里只是个样例
        sb.append("{'articles':[{'action_name':'立即查看','desc':'这是图文内容','image_url':'http://pic.alipayobjects.com/e/201311/1PaQ27Go6H_src.jpg','title':'这是标题','url':'https://www.alipay.com/'}],'msg_type':'image-text','to_user_id':'"
                  + fromUserId + "'}");

        return sb.toString();
    }

    /**
     * 构造群发图文消息
     * 
     * @return
     */
    public static String buildGroupImgTextMsg() {

        StringBuilder sb = new StringBuilder();

        //构建json格式群发图文消息: 所有内容开发者请根据自有业务自行设置响应值，这里只是个样例
        sb.append("{'articles':[{'action_name':'立即查看','desc':'这是图文内容','image_url':'http://pic.alipayobjects.com/e/201311/1PaQ27Go6H_src.jpg','title':'这是标题','url':'https://www.alipay.com/'}],'msg_type':'image-text'}");

        return sb.toString();
    }

    /**
     * 构造单发纯文本消息
     * 
     * @param fromUserId
     * @return
     */
    public static String buildSingleTextMsg(String fromUserId) {

        StringBuilder sb = new StringBuilder();

        //构建json格式单发纯文本消息体： 所有内容开发者请根据自有业务自行设置响应值，这里只是个样例
        sb.append("{'msg_type':'text','text':{'content':'这是纯文本消息'}, 'to_user_id':'" + fromUserId
                  + "'}");

        return sb.toString();
    }

    /**
     * 构造群发纯文本消息
     * 
     * @return
     */
    public static String buildGroupTextMsg() {

        StringBuilder sb = new StringBuilder();

        //构建json格式群发纯文本消息体： 所有内容开发者请根据自有业务自行设置响应值，这里只是个样例
        sb.append("{'msg_type':'text','text':{'content':'这是纯文本消息'}}");

        return sb.toString();
    }

    /**
     * 构造免登图文消息
     * 
     * @param fromUserId
     * @return
     */
    public static String buildImgTextLoginAuthMsg(String fromUserId) {

        StringBuilder sb = new StringBuilder();

        //免登连接地址，开发者需根据部署服务修改相应服务ip地址
        String url = "http://10.15.132.68:8080/AlipayFuwuDemo/loginAuth.html";

        //构建json格式的单发免登图文消息体     authType 等于 "loginAuth"表示免登消息 ： 所有内容开发者请根据自有业务自行设置响应值，这里只是个样例
        sb.append("{'articles':[{'action_name':'立即查看','desc':'这是图文内容','image_url':'http://pic.alipayobjects.com/e/201311/1PaQ27Go6H_src.jpg','title':'这是标题','url':'"
                  + url
                  + "', 'auth_type':'loginAuth'}],'msg_type':'image-text', 'to_user_id':'"
                  + fromUserId + "'}");

        return sb.toString();
    }

    /**
     * 构造基础的响应消息
     * 
     * @return
     */
    public static String buildBaseAckMsg(String fromUserId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<XML>");
        sb.append("<ToUserId><![CDATA[" + fromUserId + "]]></ToUserId>");
        sb.append("<AppId><![CDATA[" + AlipayServiceEnvConstants.APP_ID + "]]></AppId>");
        sb.append("<CreateTime>" + Calendar.getInstance().getTimeInMillis() + "</CreateTime>");
        sb.append("<MsgType><![CDATA[ack]]></MsgType>");
        sb.append("</XML>");
        return sb.toString();
    }

}
