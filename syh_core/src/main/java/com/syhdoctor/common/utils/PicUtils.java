package com.syhdoctor.common.utils;

import com.syhdoctor.common.utils.gui.ava.html.image.HtmlImageGenerator;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class PicUtils {


    private static Logger logger = LoggerFactory.getLogger(PicUtils.class);


    /**
     * 根据html生成处方单图片
     *
     * @param filePath 生成地址
     * @param html     html
     * @return
     */
    public static String generatePrescription(String filePath, String html) {
        HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
        imageGenerator.loadHtml(html);
        imageGenerator.saveAsImage(filePath);
        return filePath;
    }


    /**
     * 根据指定大小压缩图片
     *
     * @param imageBytes  源图片字节数组
     * @param desFileSize 指定图片大小，单位kb
     * @return 压缩质量后的图片字节数组
     */
    public static byte[] compressPicForScale(byte[] imageBytes, long desFileSize) {

        if (imageBytes == null || imageBytes.length <= 0 || imageBytes.length < desFileSize * 1024) {
            return imageBytes;
        }
        long srcSize = imageBytes.length;
        double accuracy = getAccuracy(srcSize / 1024);
        try {
            while (imageBytes.length > desFileSize * 1024) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
                Thumbnails.of(inputStream)
                        .scale(accuracy)
                        .outputQuality(accuracy)
                        .toOutputStream(outputStream);
                imageBytes = outputStream.toByteArray();
            }
            logger.info(" 图片原大小={}kb | 压缩后大小={}kb",
                    srcSize / 1024, imageBytes.length / 1024);
        } catch (Exception e) {
            logger.error("【图片压缩】msg=图片压缩失败!", e);
        }
        return imageBytes;
    }


    /**
     * 图片缩小
     *
     * @param path     源图片地址
     * @param waterPic 保存图片地址
     */
    public static void getThumbnail(String path, String waterPic) {
        try {
            Thumbnails.of(path).scale(0.2).outputQuality(0.42f).toFile(waterPic);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 自动调节精度(经验数值)
     *
     * @param size 源图片大小
     * @return 图片压缩质量比
     */
    private static double getAccuracy(long size) {
        double accuracy;
        if (size < 900) {
            accuracy = 0.85;
        } else if (size < 2047) {
            accuracy = 0.6;
        } else if (size < 3275) {
            accuracy = 0.44;
        } else {
            accuracy = 0.4;
        }
        return accuracy;
    }
}
