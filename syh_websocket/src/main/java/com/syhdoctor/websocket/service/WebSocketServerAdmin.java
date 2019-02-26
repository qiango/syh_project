package com.syhdoctor.websocket.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint(value = "/websocketAdmin/{orderid}/{userno}")
@Component
public class WebSocketServerAdmin {
    static Logger log = LoggerFactory.getLogger(WebSocketServerAdmin.class);

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
//    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    public static ConcurrentHashMap<String, WebSocketServerAdmin> webSocketSet = new ConcurrentHashMap<String, WebSocketServerAdmin>();

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
        log.info("open userno==========================" + param);
        String[] messagearry = param.split("_");
        String sendUserno = messagearry[0];
        this.session = session;
        userno = orderid + sendUserno;//接收到发送消息的人员编号
//        webSocketSet.add(this);     //加入set中
        List<String> list = groupUserno.get(userno);
        if (list == null) {
            list = new ArrayList<String>();
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
    //	//连接打开时执行
    //	@OnOpen
    //	public void onOpen(@PathParam("user") String user, Session session) {
    //		currentUser = user;
    //		System.out.println("Connected ... " + session.getId());
    //	}

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam(value = "userno") String param, @PathParam(value = "orderid") String orderid) {
        log.info("close userno==========================" + param);
        String[] messagearry = param.split("_");
        String sendUserno = messagearry[0];
        userno = orderid + sendUserno;//接收到发送消息的人员编号
        List<String> list = groupUserno.get(userno);
        remove(list, param);
        if (list != null && list.size() == 0) {
            groupUserno.remove(userno);
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
        List<String> list = WebSocketServerAdmin.groupUserno.get(sendUserno);
        if (list != null) {
            for (String userno : list) {
                new WebSocketServerThread(userno, sendMessage).start();
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
        log.error("发生错误");
        error.printStackTrace();
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
        WebSocketServerAdmin.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServerAdmin.onlineCount--;
    }
}

class WebSocketServerThread extends Thread {


    private String sendUserno;

    private String sendMessage;

    public WebSocketServerThread(String sendUserno, String sendMessage) {
        this.sendUserno = sendUserno;
        this.sendMessage = sendMessage;
    }

    @Override
    public void run() {
        if (WebSocketServerAdmin.webSocketSet.get(sendUserno) == null) {
            System.out.println("当前用户不在线");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            if (WebSocketServerAdmin.webSocketSet.get(sendUserno) != null) {
                WebSocketServerAdmin.webSocketSet.get(sendUserno).sendMessage(sendMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

