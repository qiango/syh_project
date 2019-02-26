package com.syhdoctor.webserver.api;

import com.syhdoctor.common.utils.JsonUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.api.request.IRequest;
import com.syhdoctor.webserver.api.response.BaseResponse;
import com.syhdoctor.webserver.api.util.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

    private static Logger log = LoggerFactory.getLogger(Client.class);
    private final static String hisId = "WEBH019";
    private final static String uname = "HUSER019";
    private final static String pwd = "admin";
    private String server;

    private Client() {
    }

    public Client(String server) {
        this.server = server + "jgpt/services/%s?&hisId=%s&uname=%s&pwd=%s";
    }

    public final <T extends BaseResponse> T excute(IRequest<T> request) throws BaseException {
        try {
            request.Validate();
        } catch (BaseException e) {
            try {
                T rsp = request.getResponseClass().newInstance();
                rsp.setStatus("0");
                rsp.setErrorType(e.getMessage());
                return rsp;
            } catch (Exception e1) {

                try {
                    T rsp = request.getResponseClass().newInstance();
                    rsp.setStatus("0");
                    rsp.setErrorType(e1.getMessage());
                    return rsp;
                } catch (Exception e2) {
                    throw new BaseException(e2.getMessage());
                }
            }
        }
        String body;
        try {
            body = HttpUtil.getInstance().post(String.format(server, request.getApiName(), hisId, uname, pwd), JsonUtil.getInstance().toJson(request));
            log.info(">>>>返回信息>>>>" + body);
        } catch (Exception e) {
            log.error(">>请求报错>>>" + e.getMessage());
            throw new BaseException(e.getMessage());
        }
        if (StrUtil.isEmpty(body)) {
            try {
                T rsp = request.getResponseClass().newInstance();
                rsp.setMsgId("服务器异常无回应");
                rsp.setStatus("0");
                return rsp;
            } catch (Exception e) {
                throw new BaseException(e.getMessage());
            }
        }
        T result = JsonUtil.getInstance().fromJson(body, request.getResponseClass());
        return result;
    }

}
