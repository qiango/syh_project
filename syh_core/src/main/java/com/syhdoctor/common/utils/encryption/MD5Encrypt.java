package com.syhdoctor.common.utils.encryption;

import java.security.MessageDigest;

public class MD5Encrypt implements IEncrypt {


    private static MD5Encrypt instance;

    public static MD5Encrypt getInstance() {
        if (instance == null) {
            instance = new MD5Encrypt();
        }
        return instance;
    }

    @Override
    public String encrypt(String string) {
        String md5str = "";
        try {
            // 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 2 将消息变成byte数组
            byte[] input = string.getBytes();
            // 3 计算后获得字节数组,这就是那128位了
            byte[] buff = md.digest(input);
            // 4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
            md5str = encode(buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }

    /**
     * 字节流转成十六进制表示
     */
    private String encode(byte[] src) {
        String strHex;
        StringBuilder sb = new StringBuilder();
        for (byte aSrc : src) {
            strHex = Integer.toHexString(aSrc & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
        }
        return sb.toString().trim();
    }

    /**
     * MD5不可解密
     */
    @Override
    public String decrypt(String string) {
        return null;
    }
}
