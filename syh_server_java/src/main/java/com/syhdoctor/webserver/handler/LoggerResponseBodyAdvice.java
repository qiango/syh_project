package com.syhdoctor.webserver.handler;

import com.syhdoctor.webserver.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class LoggerResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        return body;
    }


    /**
     * 拦截异常
     *
     * @param e 异常信息
     * @param m 方法信息
     */
    @ExceptionHandler(value = {ServiceException.class})
    @ResponseBody
    public Map<String, Object> handleException(ServiceException e, HandlerMethod m) {
        Map<String, Object> error = new HashMap<>();
        log.error(m.getMethod().getDeclaringClass() + ">" + m.getMethod().getName(), e);
        error.put("result", e.getCode());
        error.put("message", e.getMessage());
        return error;
    }

    /**
     * 拦截异常
     *
     * @param e 异常信息
     * @param m 方法信息
     */
    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public Map<String, Object> handle(Exception e, HandlerMethod m) {
        Map<String, Object> error = new HashMap<>();
        log.error(m.getMethod().getDeclaringClass() + ">" + m.getMethod().getName(), e);
        error.put("result", -1);
        error.put("message", "系统异常，请联系管理员");
        return error;
    }
}
