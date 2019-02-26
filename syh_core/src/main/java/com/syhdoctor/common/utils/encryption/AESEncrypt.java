package com.syhdoctor.common.utils.encryption;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AESEncrypt implements IEncrypt {
    private final static String KEY = "syh^&*SYH!@#SyhS";

    private static AESEncrypt instance;

    public static AESEncrypt getInstance() {
        if (instance == null) {
            instance = new AESEncrypt();
        }
        return instance;
    }

    /**
     * AES加密字符串
     *
     * @param content 需要被加密的字符串
     * @return 密文
     */
    @Override
    public String encrypt(String content) {
        try {
            byte[] enCodeFormat = KEY.getBytes("utf-8");
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器
            byte[] result = cipher.doFinal(byteContent);// 加密
            return parseByte2HexStr(result);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密AES加密过的字符串
     *
     * @param content AES加密过过的内容
     * @return 明文
     */
    @Override
    public String decrypt(String content) {
        try {
            byte[] decryptFrom = parseHexStr2Byte(content);
            if (decryptFrom != null) {
                byte[] enCodeFormat = KEY.getBytes("utf-8");
                SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
                cipher.init(Cipher.DECRYPT_MODE, key);// 初始化为解密模式的密码器
                byte[] result = cipher.doFinal(decryptFrom);
                return new String(result); // 明文
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf 需要转换的二进制
     */
    private String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (byte value : buf) {
            String hex = Integer.toHexString(value & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toLowerCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr 需要转换的16进制
     */
    private byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
