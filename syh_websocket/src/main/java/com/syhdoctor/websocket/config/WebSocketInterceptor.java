package com.syhdoctor.websocket.config;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.encryption.AESEncrypt;
import com.syhdoctor.common.utils.encryption.MD5Encrypt;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

@EnableWebSocketMessageBroker
public class WebSocketInterceptor extends TextWebSocketHandler implements HandshakeInterceptor {

    String secretKey="";
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String sign = ((HttpServletRequest) request).getHeader("sign");
        long userid = ModelUtil.strToLong(((HttpServletRequest) request).getParameter("userid"), 0);
        long doctorid = ModelUtil.strToLong(((HttpServletRequest) request).getParameter("doctorid"), 0);
        BigDecimal amountmoney = ModelUtil.strToDec2(((HttpServletRequest) request).getParameter("amountmoney"), BigDecimal.ZERO);
        String timespan = ((HttpServletRequest) request).getParameter("timespan");
        if (!StrUtil.isEmpty(timespan, sign)) {
            System.out.println(String.format("%s|%s|%s|%s|%s", secretKey, userid, doctorid, timespan, amountmoney));
            String cipherMd5 = MD5Encrypt.getInstance().encrypt(String.format("%s|%s|%s|%s|%s", secretKey, userid, doctorid, timespan, amountmoney));
            String cipherText = AESEncrypt.getInstance().encrypt(cipherMd5);
            String originalCipherText = MD5Encrypt.getInstance().encrypt(cipherText);
            System.out.println(sign);
            System.out.println(originalCipherText);
            if (sign.equals(originalCipherText)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
