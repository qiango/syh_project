package com.syhdoctor.common.utils.alidayu;

import com.alibaba.idst.nls.NlsClient;
import com.alibaba.idst.nls.NlsFuture;
import com.alibaba.idst.nls.event.NlsEvent;
import com.alibaba.idst.nls.event.NlsListener;
import com.alibaba.idst.nls.protocol.NlsRequest;
import com.alibaba.idst.nls.protocol.NlsResponse;
import com.syhdoctor.common.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;

public class LongTtsUtil implements NlsListener {


    private static Logger logger = LoggerFactory.getLogger(LongTtsUtil.class);
    private NlsClient client = new NlsClient();
    private String appKey;
    private String auth_Id;
    private String auth_Secret;
    private String tts_text;
    private String path;
    private String fileName;


    public static void saveTts(String ttsText, String path, String fileName) {
        new LongTtsUtil(ttsText, path, fileName);
    }

    public LongTtsUtil(String ttsText, String path, String fileName) {
        this.appKey = "nls-service";
        this.auth_Id = "LTAI59XCMq8Tl7x9";
        this.auth_Secret = "E86lDQpupoSkrTXQNr9JsWMxhxOq4q";
        this.tts_text = ttsText;
        this.path = path;
        this.fileName = fileName;
        this.start();
        this.sayIt();
        this.shutDown();
    }

    private void shutDown() {
        logger.info("close NLS client");
        client.close();
        logger.info("demo done");
    }

    private void start() {
        logger.info("init Nls client...");
        client.init();
    }

    private void sayIt() {
        try {
            tts_text = tts_text.replaceAll("\\|", "");
            int ttsTextLength = tts_text.length();
            String[] longTexts;
            int i = 0;
            boolean isHead = false; //标识是否是第一个头文件
            String tts_part_text;

            File file = new File(path + FileUtil.setFileName(FileUtil.FILE_ARTICLE_PATH, fileName + ".pcm"));
            FileUtil.createFile(path + FileUtil.setFileName(FileUtil.FILE_ARTICLE_PATH, fileName + ".pcm"));
            FileOutputStream outputStream = new FileOutputStream(file, true);
            longTexts = processLongText(tts_text);
            //处理文本,文本长度以50为限,截取为多个文件.
            while (ttsTextLength > 0) {
                tts_part_text = "";
                if (ttsTextLength > 280) {
                    if (i == 0) {
                        isHead = true;
                    } else {
                        isHead = false;
                    }
                    for (; i < longTexts.length; i++) {
                        tts_part_text = tts_part_text + longTexts[i];
                        if (i < longTexts.length - 1 && tts_part_text.length() + longTexts[i + 1].length() >= 280) {
                            i = i + 1;
                            break;
                        }
                    }
                } else {
                    if (i == 0) {
                        isHead = true;
                    }
                    for (; i < longTexts.length; i++) {
                        tts_part_text = tts_part_text + longTexts[i];
                    }
                }
                NlsRequest req = new NlsRequest();
                req.setApp_key("nls-service");
                req.setTts_req(tts_part_text, "16000");
                req.setTtsEncodeType("wav");
                req.setTtsVoice("xiaoyun");//男声:xiaogang
                req.setTtsVolume(50);
                req.setTtsBackgroundMusic(1, 0);
                req.authorize(auth_Id, auth_Secret);
                NlsFuture future = client.createNlsFuture(req, this);
                int total_len = 0;
                byte[] data;
                while ((data = future.read()) != null) {
                    if (data.length == 8044) {
                        // 去掉wav头,同时将多条wav转成一条pcm
                        logger.debug("data length:{} , and head is:{}", (data.length - 44), isHead ? "true" : "false");
                        outputStream.write(data, 44, data.length - 44);
                    } else {
                        outputStream.write(data, 0, data.length);
                    }
                    total_len += data.length;
                }
                logger.info("tts audio file size is :" + total_len);
                future.await(10000);
                ttsTextLength = ttsTextLength - tts_part_text.length();
            }
            outputStream.close();
            //将pcm转为wav,可以直接播放. 格式为:16kHz采样率,16bit,单声道
            PcmToWav.copyWaveFile(path + FileUtil.setFileName(FileUtil.FILE_ARTICLE_PATH, fileName + ".pcm"), path + FileUtil.setFileName(FileUtil.FILE_ARTICLE_PATH, fileName + ".wav"));
            FileUtil.changeToMp3(path, path + FileUtil.setFileName(FileUtil.FILE_ARTICLE_PATH, fileName + ".wav"), path + FileUtil.setFileName(FileUtil.FILE_ARTICLE_PATH, fileName + ".mp3"));
        } catch (Exception e) {
            logger.error("文本转语音失败" + e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(NlsEvent e) {
        NlsResponse response = e.getResponse();
        String result = "";
        if (response.getDs_ret() != null) {
            result = "get ds result: " + response.getDs_ret();
        }
        if (response.getAsr_ret() != null) {
            result += "\nget asr result: " + response.getAsr_ret();
        }
        if (response.getTts_ret() != null) {
            result += "\nget tts result: " + response.getTts_ret();
        }
        if (response.getGds_ret() != null) {
            result += "\nget gds result: " + response.getGds_ret();
        }
        if (!result.isEmpty()) {
            logger.info(result);
        } else if (response.jsonResults != null) {
            logger.info(response.jsonResults.toString());
        } else {
            logger.info("get an acknowledge package from server.");
        }
    }

    @Override
    public void onOperationFailed(NlsEvent e) {
        logger.error("Error message is: {}, Error code is: {}", e.getErrorMessage(), Integer.valueOf(e.getResponse().getStatus_code()));
    }

    //切分长文本
    private static String[] processLongText(String text) {
        text = text.replaceAll("、", "、|");
        text = text.replaceAll("，", "，|");
        text = text.replaceAll("。", "。|");
        text = text.replaceAll("；", "；|");
        text = text.replaceAll("？", "？|");
        text = text.replaceAll("！", "！|");
        text = text.replaceAll(",", ",|");
        text = text.replaceAll(";", ";|");
        text = text.replaceAll("\\?", "?|");
        text = text.replaceAll("!", "!|");
        String[] texts = text.split("\\|");
        return texts;
    }

    @Override
    public void onChannelClosed(NlsEvent e) {
        logger.info("on websocket closed.");
    }
}
