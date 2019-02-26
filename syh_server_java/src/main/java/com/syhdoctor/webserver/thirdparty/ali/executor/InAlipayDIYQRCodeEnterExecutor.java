/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.syhdoctor.webserver.thirdparty.ali.executor;

import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayOpenPublicMessageCustomSendRequest;
import com.alipay.api.response.AlipayOpenPublicMessageCustomSendResponse;
import com.syhdoctor.webserver.thirdparty.ali.common.MyException;
import com.syhdoctor.webserver.thirdparty.ali.factory.AlipayAPIClientFactory;
import com.syhdoctor.webserver.thirdparty.ali.util.AlipayMsgBuildUtil;
import net.sf.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 自定义二维码进入服务窗事件处理器
 * 
 * @author taixu.zqq
 * @version $Id: InAlipayDIYQRCodeEnterExecutor.java, v 0.1 2014年7月24日 下午9:22:02 taixu.zqq Exp $
 */
public class InAlipayDIYQRCodeEnterExecutor implements ActionExecutor {

    /** 线程池 */
    private static ExecutorService executors = Executors.newSingleThreadExecutor();

    /** 业务参数 */
    private JSONObject bizContent;

    public InAlipayDIYQRCodeEnterExecutor(JSONObject bizContent) {
        this.bizContent = bizContent;
    }

    public InAlipayDIYQRCodeEnterExecutor() {
        super();
    }

    /** 
     * @see com.alipay.executor.ActionExecutor#executor(java.util.Map)
     */
    @Override
    public String execute() throws MyException {
        //自身业务处理
        //理论上，自定义二维码会有sceneId设置，通过该id，开发者开始知道是哪个自定义二维码进入

        String syncResponseMsg = "";
        try {
            JSONObject actionParam = JSONObject.fromObject(bizContent.getString("ActionParam"));
            JSONObject scene = JSONObject.fromObject(actionParam.get("scene"));
            String sceneId = scene.getString("sceneId");
            System.out.println("sceneId:" + sceneId);

            //取得发起请求的支付宝账号id
            final String fromUserId = bizContent.getString("FromUserId");

            //1. 首先同步构建ACK响应
            syncResponseMsg = AlipayMsgBuildUtil.buildBaseAckMsg(fromUserId);
            
            //2. 异步发送消息，根据不同的sceneId推送不同的消息（这里的sceneId的意义由商户自己定义）
            if("1".equals(sceneId)){
            	
            	executors.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            // 2.1 构建一个业务响应消息，开发者根据自行业务构建，这里只是一个简单的样例
                            String requestMsg = "{'msg_type':'text','text':{'content':'通过sceneId为1的二维码关注服务窗'}, 'to_user_id':'" + fromUserId + "'}";

                            AlipayClient alipayClient = AlipayAPIClientFactory.getAlipayClient();
                            AlipayOpenPublicMessageCustomSendRequest request = new AlipayOpenPublicMessageCustomSendRequest();
                            request.setBizContent(requestMsg);

                            // 2.2 使用SDK接口类发送响应
                            AlipayOpenPublicMessageCustomSendResponse response = alipayClient
                                .execute(request);

                            // 2.3 开发者根据响应结果处理结果
                            //这里只是简单的打印，请开发者根据实际情况自行进行处理
                            if (null != response && response.isSuccess()) {
                                System.out.println("异步发送成功，结果为：" + response.getBody());
                            } else {
                                System.out.println("异步发送失败 code=" + response.getCode() + "msg：" + response.getMsg());
                            }
                        } catch (Exception e) {
                            System.out.println("异步发送失败");
                        }
                    }
                });
            }else if ("2".equals(sceneId)) {
            	
            	executors.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            // 2.1 构建一个业务响应消息，开发者根据自行业务构建，这里只是一个简单的样例
                            String requestMsg = "{'msg_type':'text','text':{'content':'通过sceneId为2的二维码关注服务窗'}, 'to_user_id':'" + fromUserId + "'}";

                            AlipayClient alipayClient = AlipayAPIClientFactory.getAlipayClient();
                            AlipayOpenPublicMessageCustomSendRequest request = new AlipayOpenPublicMessageCustomSendRequest();
                            request.setBizContent(requestMsg);

                            // 2.2 使用SDK接口类发送响应
                            AlipayOpenPublicMessageCustomSendResponse response = alipayClient
                                .execute(request);

                            // 2.3 开发者根据响应结果处理结果
                            //这里只是简单的打印，请开发者根据实际情况自行进行处理
                            if (null != response && response.isSuccess()) {
                                System.out.println("异步发送成功，结果为：" + response.getBody());
                            } else {
                                System.out.println("异步发送失败 code=" + response.getCode() + "msg：" + response.getMsg());
                            }
                        } catch (Exception e) {
                            System.out.println("异步发送失败");
                        }
                    }
                });
			}
        } catch (Exception exception) {
            throw new MyException("转换json错误，检查数据格式");
        }

        // 同步返回ACK响应
        return syncResponseMsg;
    }
}
