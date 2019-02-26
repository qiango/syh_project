package com.syhdoctor.common.utils;


import com.syhdoctor.common.utils.ffmpeg.AudioAttributes;
import com.syhdoctor.common.utils.ffmpeg.Encoder;
import com.syhdoctor.common.utils.ffmpeg.EncoderException;
import com.syhdoctor.common.utils.ffmpeg.EncodingAttributes;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {


    /**
     * 上传文件的临时目录
     */
    public static final String FILE_TEMP_PATH = "file/temp/";

    /**
     * 本地静态文件目录
     */
    public static final String FILE_STATIC_PATH = "file/static/";

    /**
     * 医生答本地音频文件目录
     */
    public static final String FILE_MEDIA_PATH = "file/media/";

    /**
     * 本地文件目录
     */
    public static final String FILE_LONG_PATH = "file/long/";

    /**
     * 本地apk文件目录
     */
    public static final String FILE_APK_PATH = "file/apk/";

    /**
     * 医生认证相关图片
     */
    public static final String FILE_DOCTOR_PATH = "file/doctor/";

    /**
     * 电话录音
     */
    public static final String FILE_PHONE_PATH = "file/phone/";

    /**
     * 处方相关图片
     */
    public static final String FILE_PRESCRIPTION_PATH = "file/prescription/";


    public static final String FILE_EMAIL_PATH = "file/email/";

    public static final String FILE_ARTICLE_PATH = "file/article/";

    /**
     * 获取临时文件存储目录
     *
     * @return java.lang.String  临时文件目录
     */
    public static String getTempPath(String base) {
        return base + FILE_TEMP_PATH;
    }

    public static String setFileName(String path, String filename) {
        return path + filename;
    }

    public static String getFileName(String filename) {
        if (StrUtil.isEmpty(filename)) {
            return "";
        } else {
            return filename.replaceAll("-", "/");
        }
    }


    /**
     * 复制文件
     *
     * @param fileurl1 源文件目录
     * @param fileurl2 目标文件目录
     * @return boolean 复制是否成功
     */
    public static boolean copyFile(String fileurl1, String fileurl2) {
        if (validateFile(fileurl1)) {
            File file = new File(fileurl1);
            return saveFile(file, fileurl2);
        } else {
            return false;
        }
    }


    /**
     * 复制文件
     *
     * @param fileurl1 源文件目录
     * @param file2    目标文件
     * @return boolean 复制是否成功
     */
    public static boolean copyFile(String fileurl1, File file2) {
        if (validateFile(fileurl1)) {
            File file = new File(fileurl1);
            return saveFile(file, file2);
        } else {
            return false;
        }
    }

    /**
     * 复制文件
     *
     * @param file1    源文件
     * @param fileurl2 目标文件目录
     * @return boolean 复制是否成功
     */
    public static boolean copyFile(File file1, String fileurl2) {
        return saveFile(file1, fileurl2);
    }

    /**
     * 存储文件
     *
     * @param file     文件
     * @param fileName 文件名字
     * @return boolean 存储是否成功
     */
    private static boolean saveFile(File file, String fileName) {
        if (createFile(fileName)) {
            return saveFile(file, new File(fileName));
        } else {
            return false;
        }
    }

    /**
     * 存储文件
     *
     * @param file1 文件
     * @param file2 文件
     * @return boolean 存储是否成功
     */
    private static boolean saveFile(File file1, File file2) {
        try {
            FileInputStream in = new FileInputStream(file1);
            FileOutputStream out = new FileOutputStream(file2);
            byte[] buffer = new byte[2048];
            while (true) {
                int ins = in.read(buffer);
                if (ins == -1) {
                    in.close();
                    out.flush();
                    out.close();
                    break;
                } else {
                    out.write(buffer, 0, ins);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 保存文件
     *
     * @param file     保存的文件字节
     * @param fileName 保存的文件名
     * @return boolean 是否保存成功
     */
    public static boolean saveFile(byte[] file, String fileName) throws Exception {
        if (createFile(fileName)) {
            FileOutputStream out = new FileOutputStream(fileName);
            out.write(file);
            out.flush();
            out.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建文件
     *
     * @param fileName 文件名
     * @return boolean  是否成功
     */
    public static boolean createFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return false;
        }
        if (fileName.endsWith("/")) {
            return file.exists() || file.mkdirs();
        }
        //判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        //创建目标文件
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 获取文件的绝对路径
     *
     * @param fileName 文件名
     * @return String  文件绝对路径
     */
    public static String getFilePath(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return file.getAbsolutePath();
        } else {
            return "";
        }
    }

    /**
     * 验证文件是否存在
     *
     * @param fileName 文件名
     * @return boolean  是否存在
     */
    public static boolean validateFile(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     * @return boolean  是否成功
     */
    public static boolean delFile(String fileName) {
        File file = new File(fileName);
        return file.delete();
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return boolean  是否成功
     */
    public static boolean delFile(File file) {
        return file.delete();
    }

    public static boolean saveFile(String fileName, InputStream inputStream) {
        boolean returnValue = false;
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new FileOutputStream(fileName));
            byte[] buffer = new byte[2048];
            int count = 0;
            while ((count = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
            returnValue = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                }

            } catch (Exception e1) {
                e1.printStackTrace();
                returnValue = false;
            }
        }
        return returnValue;
    }

    public static String newFile(String path) {
        if (validateFile(path)) {
            delFile(path);
        }
        createFile(path);
        return path;
    }

    public static void changeToMp3(String base, String file, String targetPath) {
        if (validateFile(file)) {
            File target = new File(targetPath);
            AudioAttributes audio = new AudioAttributes();
            Encoder encoder = new Encoder(base);
            audio.setCodec("libmp3lame");
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setFormat("mp3");
            attrs.setAudioAttributes(audio);

            try {
                encoder.encode(new File(file), target, attrs);
            } catch (IllegalArgumentException | EncoderException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:textFile/text.txt");
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(file);
            outputStream = new FileOutputStream(new File("C:/Users/Administrator/Desktop/bbbb.txt"));
            byte[] aByte = new byte[1024];
            if (inputStream.read(aByte) != 0) {
                outputStream.write(aByte);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    /**
     * 根据文件路径获取文件头信息
     *
     * @param filePath 文件路径
     * @return 文件头信息
     */
    public static String getFileHeader(String filePath) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[4];
            /*
             * int read() 从此输入流中读取一个数据字节。 int read(byte[] b) 从此输入流中将最多 b.length
             * 个字节的数据读入一个 byte 数组中。 int read(byte[] b, int off, int len)
             * 从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。
             */
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    public static String txt2String(File file) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                s = s.trim(); //去处空格
                result.append(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    // 保存要匹配的文件名
    private static String fileNamePatch = "";

    // 储存匹配结果,考虑到可能会出现同名的文件夹和文件，所以这里用一个List来存放检索出来的File对象
    public static List<File> compareResultList = new ArrayList<File>();

    /**
     * 处理开始
     */
    public static String exc(String tempFileNamePatch, String tempDiskPath) {

        // 初始化
        fileNamePatch = tempFileNamePatch;

        compareResultList = new ArrayList<>();


        // 如果没有指定盘符的话
        if (tempDiskPath == null || tempDiskPath.trim().length() == 0) {

            // 取得电脑所有的盘符路径
            File[] roots = File.listRoots();
            // 循环电脑的所有盘符进行匹配检索
            for (File root : roots) {
                FileUtil.readFolder(root);
            }

        } else {
            // 给定的盘符路径不是正确的盘符路径的场合，推出程序
            File checkFile = new File(tempDiskPath);
            if (!checkFile.isDirectory()) return null;

            // if all check ok, then begin to search
            FileUtil.readFolder(new File(tempDiskPath));
        }

        // 检索完以后，输出检索结果(文件名： 路径)
        for (File fileResult : compareResultList) {
            System.out.println(fileResult.getName() + ":   " + fileResult.getAbsolutePath());
        }
        if (compareResultList.size() > 0) {
            return compareResultList.get(0).getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * @param file: file or folder
     */
    public static void readFolder(File file) {

        if (file == null) return;

        //
        File[] subFile = file.listFiles();

        for (File fileTemp : subFile) {

            // 指定文件名的匹配比较
            compareFile(fileTemp);

            // 如果这个file是文件夹，不是文件的时候，搜索其子文件
            if (!fileTemp.isFile()) {
                readFolder(fileTemp);
            }
        }

    }

    /**
     * @param file: file or folder
     */
    public static void compareFile(File file) {
        if (file == null) return;

        // 文件名取得
        String fileName = file.getName();

//        // 如果是文件的话，取文件名的时候把文件类型扩展名去掉
//        if (file.isFile()) {
//
//            int lastIndex = (fileName.lastIndexOf(".") == -1 ? fileName.length() : fileName.lastIndexOf("."));
//
//            fileName = fileName.substring(0, lastIndex);
//        }

        // 匹配文件名,匹配的场合，将当前的文件添加到compareResultList中
        if (fileName.contains(fileNamePatch)) {
            compareResultList.add(file);
        }
    }
}
