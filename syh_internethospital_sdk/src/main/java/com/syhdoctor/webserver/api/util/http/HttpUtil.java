package com.syhdoctor.webserver.api.util.http;


import com.syhdoctor.webserver.api.BaseException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class HttpUtil {

    private static Logger log = LoggerFactory.getLogger(HttpUtil.class);
    private static volatile HttpUtil instance = null;
    private OkHttpClient okHttpClient;

    private static final MediaType MEDIA_TYPE_FILE = MediaType.parse("image/png");

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
            log.error("ssl error ", e);
        }

        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
    }

    private HttpUtil() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //参数一：TimeOut等待链接的的时间TimeOut，参数二：TimeUnit时间单位
        builder.connectTimeout(10, TimeUnit.SECONDS);//点击POST是否有响应的时间
        builder.writeTimeout(30, TimeUnit.SECONDS);//请求POST数据写的时间
        builder.readTimeout(30, TimeUnit.SECONDS);//请求POST数据都的时间
        builder.sslSocketFactory(createSSLSocketFactory(), new TrustAllManager());
        builder.hostnameVerifier(new TrustAllHostnameVerifier());

        okHttpClient = builder.build();
    }

    public static HttpUtil getInstance() {
        synchronized (HttpUtil.class) {
            if (instance == null) {
                instance = new HttpUtil();
            }
        }
        return instance;
    }


    public String get(String url, Map<String, String> params) throws BaseException {
        String value = "";
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }
        try {
            log.info("params:url=" + url);
            log.info("params:" + params.toString());
            log.info("data:" + builder.build().toString());
            Request request = new Request.Builder()
                    .url(url)
                    .put(builder.build())
                    .build();

            Call call = okHttpClient.newCall(request);

            Response response = call.execute();
            log.info("get:response=" + response.toString());
            if (response.isSuccessful()) {
                ResponseBody body = response.body();

                if (body != null) {
                    value = body.string();
                }
                log.info("get:return=" + value);

            }
        } catch (Exception e) {
            log.error("doGet Error > ", e);
            throw new BaseException(e.getMessage());
        }
        return value;
    }

    public String post(String url, String params) throws BaseException {
        String value = "";
        try {
            log.info("params:url=" + url);
            log.info("params:data=" + params);
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("data", params);
            Request request = new Request.Builder()
                    .url(url)
                    .post(builder.build())
                    .build();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            log.info("post:response=" + response.toString());
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    value = body.string();
                }
                log.info("post:return=" + value);
            }
        } catch (Exception e) {
            log.error("post Error > ", e.getMessage());
            throw new BaseException(e.getMessage());
        }
        return value;
    }

}
