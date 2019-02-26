package com.syhdoctor.common.utils;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import java.util.Map;

@Service
public class ActivemqUtil {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * 单例模式
     */
    private static volatile ActivemqUtil instance = null;

    //    public void sendMessage(String disname, String message) {
//        log.info("====================>> 发送queue消息：" + message + ",disname：" + disname);
//        Destination destination = new ActiveMQQueue(disname);
//        jmsMessagingTemplate.convertAndSend(destination, message);
//    }
/*

    @JmsListener(destination = "test.queue")
    public void getMessage(String text) {
        log.info("====================>> 收到消息：" + text);
    }
*/
    public static ActivemqUtil getInstance() {
        synchronized (JsonUtil.class) {
            if (instance == null) {
                instance = new ActivemqUtil();
            }
        }
        return instance;
    }

    public void sendPushMessage(String disname, Map<String, Object> message) {
        log.info("====================>> 发送queue消息：" + message + ",disname：" + disname);
        Destination destination = new ActiveMQQueue(disname);
        jmsMessagingTemplate.convertAndSend(destination, message);
    }

}
