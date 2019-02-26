package com.syhdoctor.common.utils.http.impl;

import com.alibaba.fastjson.JSONObject;
import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.common.utils.JsonUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.common.utils.encryption.BASE64;
import com.syhdoctor.common.utils.encryption.MD5Encrypt;
import com.syhdoctor.common.utils.http.BaseHttpUtil;
import com.syhdoctor.common.utils.http.HttpUtil;
import okhttp3.*;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class KdxfHttpUtil extends BaseHttpUtil {

    private static volatile KdxfHttpUtil instance = null;

    public static KdxfHttpUtil getInstance() {
        synchronized (HttpUtil.class) {
            if (instance == null) {
                instance = new KdxfHttpUtil();
            }
        }
        return instance;
    }


    @Override
    public Map<String, Object> post(String url, Object params, Object... expansion) {
        Map<String, Object> temp = new HashMap<>();
        try {
            log.info("params:url=" + url);
            log.info("params:" + params.toString());
            RequestBody rbody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), (String) params);
            String apiKey = "df596c1d0819b945323aeae79583b94c";
            String curTime = String.valueOf(System.currentTimeMillis() / 1000);

            String sdddd = "{\"auf\": \"audio/L16;rate=16000\",\"aue\": \"lame\",\"voice_name\": \"xiaoyan\",\"speed\": \"50\",\"volume\": \"50\",\"pitch\": \"50\",\"engine_type\": \"intp65\",\"text_type\": \"text\"}";
            String xParam = BASE64.encodeStr(sdddd);
            String xCheckSum = MD5Encrypt.getInstance().encrypt(apiKey + curTime + xParam);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("X-Appid", "5badce2f")
                    .addHeader("X-CurTime", curTime)
                    .addHeader("X-Param", xParam)
                    .addHeader("X-CheckSum", xCheckSum)
//                    .addHeader("X-Real-Ip", "180.168.203.226")
                    .post(rbody)
                    .build();

            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if ("audio/mpeg".equals(response.header("Content-Type"))) {
                    if (body != null) {
                        String key = "syh" + UnixUtil.getCustomRandomString() + ".mp3";
                        String filePath = FileUtil.getFilePath(expansion[0].toString()) + key;
                        if (FileUtil.validateFile(filePath)) {
                            FileUtil.delFile(filePath);
                        }
                        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                        byte[] buff = new byte[100];
                        int rc;
                        while ((rc = body.byteStream().read(buff, 0, 100)) > 0) {
                            swapStream.write(buff, 0, rc);
                        }
                        byte[] in2b = swapStream.toByteArray();
                        FileUtil.saveFile(in2b, filePath);
                        temp.put("code", 0);
                        temp.put("data", filePath);
                    }
                } else {
                    String value = body.string();
                    temp = JsonUtil.getInstance().jsonToMap(JSONObject.parseObject(value));
                    log.info("post:return=" + value);
                }
            }
        } catch (Exception e) {
            log.error("doGet Error > ", e);
        }
        return temp;
    }
}
