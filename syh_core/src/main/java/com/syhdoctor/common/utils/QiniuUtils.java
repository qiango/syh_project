package com.syhdoctor.common.utils;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class QiniuUtils {

    public static final String ACCESSKEY = "J173awXDyxWOlML1QMScTEuVZQTng_EVzsvmRwhk";
    public static final String SECRETKEY = "ERCwns6D-Y6tndNtm2LIwEUW_JGeYsm-mgrHAAVd";

    private static Logger log = LoggerFactory.getLogger(QiniuUtils.class);

    public static String upToken(String bucket) {
        Auth auth = Auth.create(ACCESSKEY, SECRETKEY);
        log.info(bucket + " token:" + auth.uploadToken(bucket));
        return auth.uploadToken(bucket);
    }

    public static String putFile(String bucket, String key, InputStream inputStream) {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        try {
            Auth auth = Auth.create(ACCESSKEY, SECRETKEY);
            String upToken = auth.uploadToken(bucket, key);
            try {
                Response response = uploadManager.put(inputStream, key, upToken, null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                return "SUCCESS";
            } catch (QiniuException ex) {
                log.error("qniu>>112212>>>>", ex);
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    log.error("qniu>>>>2222>>", ex2);
                }
            }
        } catch (Exception ex) {
            log.error("qniu>>>>1111>>", ex);
        }
        return "FAIL";
    }

//    public static void main(String[] args) {
//
////        try {
////            QiniuUtils.putFile("syhdoctor", "112321ssdfd32321.jpg",new FileInputStream(new File("C:\\Users\\user\\Desktop\\微信图片_20180822185441.jpg")));
////        }catch (Exception e){
////
////        }
//
//    }
}
