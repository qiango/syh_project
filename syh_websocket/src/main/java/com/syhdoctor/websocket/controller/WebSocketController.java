package com.syhdoctor.websocket.controller;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.websocket.base.controller.BaseController;
import com.syhdoctor.websocket.service.AppAnswerWebSocketServer;
import com.syhdoctor.websocket.service.AppVideoWebSocketServer;
import com.syhdoctor.websocket.service.WebAnswerWebSocketServer;
import com.syhdoctor.websocket.service.WebSocketServerAdmin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/websocket")
public class WebSocketController extends BaseController {

    @PostMapping("/answerPushData")
    public Map<String, Object> answerPushData(@RequestParam Map<String, Object> param) {
        Map<String, Object> result = new HashMap<String, Object>();
        String json = ModelUtil.getStr(param, "json");
        log.info("json=================================" + json);
        WebAnswerWebSocketServer.sendToUser(json);
        AppAnswerWebSocketServer.sendToUser(json);
        result.put("operationResult", true);
        return result;
    }

    @PostMapping("/videoPushData")
    public Map<String, Object> videoPushData(@RequestParam Map<String, Object> param) {
        Map<String, Object> result = new HashMap<String, Object>();
        String json = ModelUtil.getStr(param, "json");
        log.info("json=================================" + json);
        AppVideoWebSocketServer.sendToUser(json);
        result.put("operationResult", true);
        return result;
    }

    @PostMapping("/pushDataAdmin")
    public Map<String, Object> pushDataAdmin(@RequestParam Map<String, Object> param) {
        Map<String, Object> result = new HashMap<String, Object>();
        String json = ModelUtil.getStr(param, "json");
        log.info("json=================================" + json);
        WebSocketServerAdmin.sendToUser(json);
        result.put("operationResult", true);
        return result;
    }

}
