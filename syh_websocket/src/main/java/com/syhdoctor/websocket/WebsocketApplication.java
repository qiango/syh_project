package com.syhdoctor.websocket;

import com.syhdoctor.websocket.service.AppAnswerWebSocketServer;
import com.syhdoctor.websocket.service.WebAnswerWebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@ServletComponentScan
public class WebsocketApplication {
    public static void main(String[] args) {
//        SpringApplication.run(WebsocketApplication.class, args);

        SpringApplication springApplication = new SpringApplication(WebsocketApplication.class);
        ConfigurableApplicationContext configurableApplicationContext = springApplication.run(args);
        //解决WebSocket不能注入的问题
        AppAnswerWebSocketServer.setApplicationContext(configurableApplicationContext);
        WebAnswerWebSocketServer.setApplicationContext(configurableApplicationContext);
    }


}
