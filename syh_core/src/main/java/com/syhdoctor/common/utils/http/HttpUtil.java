package com.syhdoctor.common.utils.http;


import com.alibaba.fastjson.JSONObject;
import com.syhdoctor.common.config.ConfigModel;
import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.common.utils.JsonUtil;
import com.syhdoctor.common.utils.UnixUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.File;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

    /**
     * 得到文件的流
     *
     * @param * @param url
     * @param
     * @return java.io.InputStream
     */
    public InputStream getInputStream(String url) {
        InputStream temp = null;
        try {
            log.info("params:url=" + url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    temp = body.byteStream();
                }
            }
        } catch (Exception e) {
            log.error("doGet Error > ", e);
        }
        return temp;
    }

    public Map<String, Object> get(String url, HttpParamModel params) {
        return get(url, params, true);
    }

    public Map<String, Object> get(String url) {
        return get(url, null, false);
    }

    private Map<String, Object> get(String url, HttpParamModel params, boolean isput) {
        Map<String, Object> temp = null;
        try {
            if (params == null) {
                params = new HttpParamModel();
            }

            log.info("params:url=" + url);
            log.info("params:data=" + params.toString());

            Request.Builder builder = new Request.Builder();
            builder.url(url);
            if (isput) {
                builder.put(params.getForm().build());
            }

            Request request = builder.build();

            Call call = okHttpClient.newCall(request);

            Response response = call.execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                String value = "";
                if (body != null) {
                    value = body.string();
                }
                log.info("get:return=" + value);

                temp = JsonUtil.getInstance().jsonToMap(JSONObject.parseObject(value));
            }
        } catch (Exception e) {
            log.error("doGet Error > ", e);
        }
        return temp;
    }

    public Map<String, Object> post(String url) {
        return this.post(url, new HttpParamModel());
    }

    public Map<String, Object> post(String url, HttpParamModel params) {
        if (params == null) {
            params = new HttpParamModel();
        }
        return nPost(url, params, "application/json; charset=utf-8");
    }

    /**
     * post请求（用于获取微信接口的）
     *
     * @param url
     * @param params
     * @return
     */
    public Map<String, Object> post(String url, String params) {
        if (params == null) {
            params = "";
        }
        return nPost(url, params, "application/json; charset=utf-8");
    }

    public Map<String, Object> postForm(String url, String params) {
        if (params == null) {
            params = "";
        }
        return nPost(url, params, "application/x-www-form-urlencoded; charset=utf-8");
    }


    private Map<String, Object> nPost(String url, Object params, String contentType) {
        Map<String, Object> temp = null;
        try {
            log.info("params:url=" + url);
            log.info("params:data=" + params.toString());

            RequestBody rbody = null;
            if (params instanceof String) {
                rbody = RequestBody.create(MediaType.parse(contentType), (String) params);
            } else if (params instanceof HttpParamModel) {
                rbody = ((HttpParamModel) params).getForm().build();
            }
            Request request = new Request.Builder()
                    .url(url)
                    .post(rbody)
                    .build();

            Call call = okHttpClient.newCall(request);

            Response response = call.execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                String value = "";
                if (body != null) {
                    value = body.string();
                }
                log.info("post:return=" + value);
                temp = JsonUtil.getInstance().jsonToMap(JSONObject.parseObject(value));
            }
        } catch (Exception e) {
            log.error("doGet Error > ", e);
        }
        return temp;
    }


    public Map<String, Object> phonePost(String url, Map<String, Object> params, long time) {
        Map<String, Object> temp = null;
        try {
            log.info("params:url=" + url);
            log.info("params:data=" + params.toString());
            MultipartBody.Builder multiBody = new MultipartBody.Builder();
            multiBody.setType(MultipartBody.FORM);
            if (params == null) {
                params = new HashMap<>();
            }
            Set<String> keys = params.keySet();
            for (String key : keys) {
                Object value = params.get(key);
                if (value instanceof byte[]) {
                    multiBody.addFormDataPart(key, "file", RequestBody.create(MEDIA_TYPE_FILE, (byte[]) value));
                } else if (value instanceof File) {
                    multiBody.addFormDataPart(key, "file", RequestBody.create(MEDIA_TYPE_FILE, (File) value));
                } else {
                    multiBody.addFormDataPart(key, String.valueOf(value));
                }
            }

            String authorization = String.format(ConfigModel.QIMO.ACCOUNTID + ":%s", UnixUtil.getDate(time, "yyyyMMddHHmmss"));
            final Base64.Encoder encoder = Base64.getEncoder();
            final byte[] textByte = authorization.getBytes("UTF-8");
            //编码
            final String encodedText = encoder.encodeToString(textByte);
            log.info("encodedtext=" + encodedText);

            RequestBody requestBody = multiBody.build();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("content-type", "application/json;charset:utf-8")
                    .addHeader("Authorization", encodedText)// 自定义的header
                    .post(requestBody)
                    .build();

            Call call = okHttpClient.newCall(request);

            Response response = call.execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                String value = "";
                if (body != null) {
                    value = body.string();
                }
                log.info("post:return=" + value);
                temp = JsonUtil.getInstance().jsonToMap(JSONObject.parseObject(value));
            }
        } catch (Exception e) {
            log.error("doGet Error > ", e);
        }
        return temp;
    }

    public boolean getFile(String fileUrl, String fileLocal) {
        boolean returnValue = false;
        try {
            Request request = new Request.Builder()
                    .get()
                    .url(fileUrl)
                    .build();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            log.info("getFile fileUrl > " + fileUrl);
            log.info("getFile fileLocal > " + fileLocal);
            log.info("getFile test > 1111" + response.code());
            if (response.isSuccessful()) {
                log.info("getFile test > 1112");
                ResponseBody body = response.body();
                log.info("getFile test > 1113");
                if (body != null) {
                    log.info("getFile test > 1114");
                    returnValue = FileUtil.saveFile(fileLocal, response.body().byteStream());
                }
            }
        } catch (Exception e) {
            log.error("getFile Error > ", e);
        }
        return returnValue;
    }

    public Map<String, Object> postFile(String url, Map<String, Object> params) {
        log.info("postFile > url = " + url);
        Map<String, Object> temp = null;
        MultipartBody.Builder multiBody = new MultipartBody.Builder();
        multiBody.setType(MultipartBody.FORM);
        if (params == null) {
            params = new HashMap<>();
        }
        Set<String> keys = params.keySet();
        for (String key : keys) {
            Object value = params.get(key);
            if (value instanceof byte[]) {
                multiBody.addFormDataPart(key, "file", RequestBody.create(MEDIA_TYPE_FILE, (byte[]) value));
            } else if (value instanceof File) {
                multiBody.addFormDataPart(key, "file", RequestBody.create(MEDIA_TYPE_FILE, (File) value));
            } else {
                multiBody.addFormDataPart(key, (String) value);
            }
        }
        RequestBody requestBody = multiBody.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                String value = "";
                if (body != null) {
                    value = body.string();
                }
                log.info("post:return=" + value);
                temp = JsonUtil.getInstance().jsonToMap(JSONObject.parseObject(value));
            }
        } catch (Exception e) {
            log.error("postFile Error > ", e);
        }
        return temp;
    }
}
