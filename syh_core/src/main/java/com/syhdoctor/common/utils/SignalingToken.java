package com.syhdoctor.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author qian.wang
 * @description 权限位算法
 * @date 2018/10/29
 */
public class SignalingToken {

//    account: 用户登录厂商 Agora 信令系统的账户
//    appId: 32 位 App ID 字符串
//    appCertificate: 32 位 App Certificate 字符串
//    expiredTime: 服务到期的 UTC 时间戳，用户在服务到期后，无法再登录 Agora 信令系统和使用其功能
//    static {
//        Map<String,Object> map=new HashedMap();
//        String appId="2b03fc6669e24fbe8bacee6c8cd7e297";
//        String account="wei.jiang@syhdoctor.com";
//        String certificate="fe1a0437bf217bdd34cd65053fb0fe1d";//项目中开启
//        int expiredTsInSeconds=1546271999;
//        map.put("appid",appId);
//        map.put("account",account);
//        map.put("certificate",certificate);
//        map.put("expiredTsInSeconds",expiredTsInSeconds);
//    }

    public static String getToken(String appId, String certificate, String account, long expiredTsInSeconds) throws NoSuchAlgorithmException {

        StringBuilder digest_String = new StringBuilder().append(account).append(appId).append(certificate).append(expiredTsInSeconds);
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(digest_String.toString().getBytes());
        byte[] output = md5.digest();
        String token = hexlify(output);
        String token_String = new StringBuilder().append("1").append(":").append(appId).append(":").append(expiredTsInSeconds).append(":").append(token).toString();
        return token_String;
    }

    public static String hexlify(byte[] data) {

        char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] toDigits = DIGITS_LOWER;
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return String.valueOf(out);
    }

    public static void main(String[] args) {
//      String account, String token, Signal.LoginCallback cb
        String appId = "2b03fc6669e24fbe8bacee6c8cd7e297";
        String account = "wei.jiang@syhdoctor.com";
        String certificate = "fe1a0437bf217bdd34cd65053fb0fe1d";//项目中开启
        int expiredTsInSeconds = 1546271999;
        String token = null;
        try {
            token = getToken(appId, account, certificate, expiredTsInSeconds);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println(token);
        // 登录 Agora 信令系统
//        io.agora.signal.Signal signal=new io.agora.signal.Signal(appId);
//        signal.setDoLog(true);
//        signal.login();
//        signal1.login()
    }


}
