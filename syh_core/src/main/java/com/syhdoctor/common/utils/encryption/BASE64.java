package com.syhdoctor.common.utils.encryption;


import com.syhdoctor.common.utils.StrUtil;
import org.apache.commons.codec.binary.Base64;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BASE64 {


    public static String imageToBase64Str(String path) {
        return base64Encoding(readFile(Paths.get(path)));
    }


    public static String encodeStr(String value) {
        if (!StrUtil.isEmpty(value)) {

//            Base64 base64 = new Base64();
//            try {
//                byte[] b = value.getBytes("UTF-8");
//                b = base64.encode(b);
//                String s = new String(b);
//                return s;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            try {
                byte[] encodeBase64 = Base64.encodeBase64(value.getBytes("UTF-8"));
                return new String(encodeBase64);
            } catch (Exception e) {

            }
        }
        return "";
    }

    public static String decodeStr(String value) {
        if (!StrUtil.isEmpty(value)) {
            Base64 base64 = new Base64();
            try {
                byte[] b = value.getBytes("UTF-8");
                b = base64.decode(b);
                String s = new String(b);
                return s;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

//    public String getBase64(String value) {
//        final java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
//        try {
//            final byte[] textByte = value.getBytes("UTF-8");
//            //编码
//            final String encodedText = encoder.encodeToString(textByte);
//            return encodedText;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    /**
     * 从图片文件中读取内容。
     *
     * @param path 图片文件的路径。
     * @return 二进制图片内容的byte数组。
     */
    public static byte[] readFile(Path path) {
        byte[] imageContents = null;
        try {
            imageContents = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageContents;
    }

    /**
     * 编码图片文件，编码内容输出为{@code String}格式。
     *
     * @param imageContents 二进制图片内容的byte数组。
     * @return {@code String}格式的编码内容。
     */
    public static String base64Encoding(byte[] imageContents) {
        if (imageContents != null)
            return Base64.encodeBase64String(imageContents);
        else
            return null;
    }

    /**
     * 解码图片文件。
     *
     * @param imageContents 待解码的图片文件的字符串格式。
     * @return 解码后图片文件的二进制内容。
     */
    public static byte[] base64Decoding(String imageContents) {
        if (imageContents != null)
            return Base64.encodeBase64(imageContents.getBytes());
        else
            return null;
    }

    public static boolean Base64ToImage(String imgStr, String imgFilePath) { // 对字节数组字符串进行Base64解码并生成图片

        if (StrUtil.isEmpty(imgStr)) // 图像数据为空
            return false;
        Base64 base64 = new Base64();
        try {
            // Base64解码
            byte[] b = base64.decode(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
