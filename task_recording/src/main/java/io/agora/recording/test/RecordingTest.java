package io.agora.recording.test;


import io.agora.recording.RecordingSDK;
import io.agora.recording.common.CustomConfig;

public class RecordingTest {

    public static void main(String[] args) {
        CustomConfig.basePath = "/home/qwq/file/static/";
        RecordingSDK recordingSDK = new RecordingSDK();
//        RecordingUtil recordingUtil = new RecordingUtil(recordingSDK);
//        String[] args = new String[]{"--appId", "d7a1141a4ea3469c98a1997f3d3b110d", "--uid", "0",
//                "--channelKey", "123456","--channel", "hahhahahahha", "--appliteDir", ConfigModel.BASEFILEPATH + FileUtil.FILE_STATIC_PATH + "agora"
//        };
    }
}
