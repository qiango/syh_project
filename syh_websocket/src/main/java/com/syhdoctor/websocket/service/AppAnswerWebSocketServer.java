package com.syhdoctor.websocket.service;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.websocket.mapper.WebSocketMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint(value = "/AppAnswerSocket/{orderid}/{userno}")
@Component
public class AppAnswerWebSocketServer {
    private static Logger log = LoggerFactory.getLogger(AppAnswerWebSocketServer.class);

    //此处是解决无法注入的关键
    private static ApplicationContext applicationContext;
    //你要注入的service或者dao
    private WebSocketMapper webSocketMapper;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        AppAnswerWebSocketServer.applicationContext = applicationContext;
    }

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
//    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    public static ConcurrentHashMap<String, AppAnswerWebSocketServer> webSocketSet = new ConcurrentHashMap<String, AppAnswerWebSocketServer>();

    public static Map<String, List<String>> groupUserno = new HashMap<>();


    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //当前发消息的人员编号
    private static String userno = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(@PathParam(value = "userno") String param, @PathParam(value = "orderid") String orderid, Session session) {
        log.info("open userno==========================" + orderid + "/" + param);
        String[] messagearry = param.split("_");
        String sendUserno = messagearry[0];
        this.session = session;
        userno = orderid + sendUserno;//接收到发送消息的人员编号
//        webSocketSet.add(this);     //加入set中
        List<String> list = groupUserno.get(userno);
        if (list == null) {
            list = new ArrayList<String>();
            //此处是解决无法注入的关键
            webSocketMapper = applicationContext.getBean(WebSocketMapper.class);
            boolean flag = webSocketMapper.getUserByNO(sendUserno);
            if (flag) {
                webSocketMapper.updateAnswerUserOnline(1, ModelUtil.strToLong(orderid, 0));
            } else {
                webSocketMapper.updateAnswerDoctorOnline(1, ModelUtil.strToLong(orderid, 0));
            }
        }

        list.add(param);
        groupUserno.put(userno, list);

        webSocketSet.put(param, this);
        addOnlineCount();           //在线数加1
        log.info("有新连接加入！当前在线人数为" + getOnlineCount());
        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam(value = "userno") String param, @PathParam(value = "orderid") String orderid) {
        log.info("close userno==========================" + orderid + "/" + param);
        String[] messagearry = param.split("_");
        String sendUserno = messagearry[0];
        userno = orderid + sendUserno;//接收到发送消息的人员编号
        List<String> list = groupUserno.get(userno);
        remove(list, param);
        if (list != null && list.size() == 0) {
            groupUserno.remove(userno);
            //此处是解决无法注入的关键
            webSocketMapper = applicationContext.getBean(WebSocketMapper.class);
            boolean flag = webSocketMapper.getUserByNO(sendUserno);
            if (flag) {
                webSocketMapper.updateAnswerUserOnline(0, ModelUtil.strToLong(orderid, 0));
            } else {
                webSocketMapper.updateAnswerDoctorOnline(0, ModelUtil.strToLong(orderid, 0));
            }
        } else {
            groupUserno.put(userno, list);
        }

        webSocketSet.remove(param);  //从set中删除
        subOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /*
     * 正确
     */
    public static void remove(List<String> list, String target) {
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                String item = list.get(i);
                if (target.equals(item)) {
                    list.remove(item);
                }
            }
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端的消息:" + message);

//        //群发消息
//        for (WebSocketServer item : webSocketSet) {
//            try {
//                item.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        sendToUser(message);
    }

    /**
     * 给指定的人发送消息
     *
     * @param message
     */
    public static void sendToUser(String message) {
        String[] messagearry = message.split("[|]");
        String sendUserno = messagearry[1];
        String sendMessage = messagearry[0];
        List<String> list = AppAnswerWebSocketServer.groupUserno.get(sendUserno);
        if (list != null) {
            for (String userno : list) {
                new AppAnswerSleepThread(userno, sendMessage).start();
            }
        }
    }


    /**
     * 给所有人发消息
     *
     * @param message
     */
    private void sendAll(String message) {
        String now = getNowTime();
        String sendMessage = message.split("[|]")[0];
        //遍历HashMap
        for (String key : webSocketSet.keySet()) {
            try {
                //判断接收用户是否是当前发消息的用户
                if (!userno.equals(key)) {
                    webSocketSet.get(key).sendMessage(now + "用户" + userno + "发来消息：" + " <br/> " + sendMessage);
                    System.out.println("key = " + key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    private static String getNowTime() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(date);
        return time;
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        List<String> orderidList = session.getRequestParameterMap().get("orderid");
        List<String> usernoList = session.getRequestParameterMap().get("userno");
        if (usernoList.size() > 0 && orderidList.size() > 0) {
            String usernoParam = usernoList.get(0);
            String[] messagearry = usernoParam.split("_");
            String sendUserno = messagearry[0];
            List<String> list = groupUserno.get(userno);
            if (list != null && list.size() == 0) {
                //此处是解决无法注入的关键
                webSocketMapper = applicationContext.getBean(WebSocketMapper.class);
                boolean flag = webSocketMapper.getUserByNO(sendUserno);
                if (flag) {
                    webSocketMapper.updateAnswerUserOnline(0, ModelUtil.strToLong(orderidList.get(0), 0));
                } else {
                    webSocketMapper.updateAnswerDoctorOnline(0, ModelUtil.strToLong(orderidList.get(0), 0));
                }
            }
        }
        log.error("发生错误");
    }


    public synchronized void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


//    /**
//     * 群发自定义消息
//     */
//    public static void sendInfo(String message) throws IOException {
//        log.info(message);
//        for (WebSocketServer item : webSocketSet) {
//            try {
//                item.sendMessage(message);
//            } catch (IOException e) {
//                continue;
//            }
//        }
//    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        AppAnswerWebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        AppAnswerWebSocketServer.onlineCount--;
    }
}

class AppAnswerSleepThread extends Thread {


    private String sendUserno;

    private String sendMessage;

    public AppAnswerSleepThread(String sendUserno, String sendMessage) {
        this.sendUserno = sendUserno;
        this.sendMessage = sendMessage;
    }

    @Override
    public void run() {
        if (AppAnswerWebSocketServer.webSocketSet.get(sendUserno) == null) {
            System.out.println("当前用户不在线");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            if (AppAnswerWebSocketServer.webSocketSet.get(sendUserno) != null) {
                AppAnswerWebSocketServer.webSocketSet.get(sendUserno).sendMessage(sendMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

