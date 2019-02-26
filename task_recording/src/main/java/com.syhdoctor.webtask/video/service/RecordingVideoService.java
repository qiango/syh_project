package com.syhdoctor.webtask.video.service;

import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webtask.base.service.BaseService;
import com.syhdoctor.webtask.config.ConfigModel;
import com.syhdoctor.webtask.utils.media.AccessToken;
import com.syhdoctor.webtask.utils.media.SimpleTokenBuilder;
import com.syhdoctor.webtask.video.Mapper.RecordingVideoMapper;
import io.agora.recording.RecordingUtil;
import io.agora.recording.common.CustomConfig;
import io.agora.recording.common.RecordingEngineProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RecordingVideoService extends BaseService {

    @Autowired
    private RecordingVideoMapper recordingVideoMapper;


//    public void start() {
//        CustomConfig.basePath = ConfigModel.BASEFILEPATH + FileUtil.FILE_STATIC_PATH;
//        log.info("nativeHandle>>>>>>>>>>>>>>>>>>>" + RecordingUtil.mNativeHandle);
//        recordingUtil.startService(RecordingUtil.mNativeHandle);
//    }

//    public void stop() {
//        CustomConfig.basePath = ConfigModel.BASEFILEPATH + FileUtil.FILE_STATIC_PATH;
//        log.info("nativeHandle>>>>>>>>>>>>>>>>>>>" + RecordingUtil.mNativeHandle);
//        recordingUtil.stopService(RecordingUtil.mNativeHandle);
//    }


//    public static void main(String[] args) {
//        RecordingSDK recordingSDK = new RecordingSDK();
//        RecordingUtil recordingUtil = new RecordingUtil(recordingSDK);
//        recordingUtil.startService(139767017620656L);
////        recordingUtil.stopService(140508335687088L);
//    }


    public void createVideoToken() {
        List<Map<String, Object>> paidVideoOrderList = recordingVideoMapper.getPaidVideoOrderList();
        for (Map<String, Object> map : paidVideoOrderList) {
            long id = ModelUtil.getLong(map, "id");
            int subscribeendtime = ModelUtil.getInt(map, "subscribeendtime");
            try {
//                String token = SignalingToken.getToken(com.syhdoctor.common.config.ConfigModel.AGORA.APPID, com.syhdoctor.common.config.ConfigModel.AGORA.CERTIFICATE, com.syhdoctor.common.config.ConfigModel.AGORA.ACCOUNT, subscribeendtime);
                int useruid = ModelUtil.strToInt("1" + UnixUtil.generateNumber(7), 0);
                SimpleTokenBuilder usertoken = new SimpleTokenBuilder(com.syhdoctor.common.config.ConfigModel.AGORA.APPID, com.syhdoctor.common.config.ConfigModel.AGORA.CERTIFICATE, ModelUtil.getStr(map, "orderno"), useruid);
                usertoken.initPrivileges(SimpleTokenBuilder.Role.Role_Attendee);
                usertoken.setPrivilege(AccessToken.Privileges.kJoinChannel, subscribeendtime);
                usertoken.setPrivilege(AccessToken.Privileges.kPublishAudioStream, subscribeendtime);
                usertoken.setPrivilege(AccessToken.Privileges.kPublishVideoStream, subscribeendtime);
                usertoken.setPrivilege(AccessToken.Privileges.kPublishDataStream, subscribeendtime);
                String userResult = usertoken.buildToken();
                recordingVideoMapper.updateOrderUserToken(id, userResult, useruid);


                int doctoruid = ModelUtil.strToInt("2" + UnixUtil.generateNumber(7), 0);
                SimpleTokenBuilder doctorToken = new SimpleTokenBuilder(com.syhdoctor.common.config.ConfigModel.AGORA.APPID, com.syhdoctor.common.config.ConfigModel.AGORA.CERTIFICATE, ModelUtil.getStr(map, "orderno"), doctoruid);
                doctorToken.initPrivileges(SimpleTokenBuilder.Role.Role_Attendee);
                doctorToken.setPrivilege(AccessToken.Privileges.kJoinChannel, subscribeendtime);
                doctorToken.setPrivilege(AccessToken.Privileges.kPublishAudioStream, subscribeendtime);
                doctorToken.setPrivilege(AccessToken.Privileges.kPublishVideoStream, subscribeendtime);
                doctorToken.setPrivilege(AccessToken.Privileges.kPublishDataStream, subscribeendtime);
                String doctorResult = doctorToken.buildToken();
                recordingVideoMapper.updateOrderDoctorToken(id, doctorResult, doctoruid);

                SimpleTokenBuilder token = new SimpleTokenBuilder(com.syhdoctor.common.config.ConfigModel.AGORA.APPID, com.syhdoctor.common.config.ConfigModel.AGORA.CERTIFICATE, ModelUtil.getStr(map, "orderno"), 0);
                token.initPrivileges(SimpleTokenBuilder.Role.Role_Attendee);
                token.setPrivilege(AccessToken.Privileges.kJoinChannel, 0);
                token.setPrivilege(AccessToken.Privileges.kPublishAudioStream, 0);
                token.setPrivilege(AccessToken.Privileges.kPublishVideoStream, 0);
                token.setPrivilege(AccessToken.Privileges.kPublishDataStream, 0);
                String result = token.buildToken();

                recordingVideoMapper.updateOrderStatus(id);

                //创建或者加入渠道
                new RecordThread(recordingVideoMapper, ModelUtil.getLong(map, "id"), ModelUtil.getStr(map, "orderno"), result).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //停止录制
    public void start() {
        List<Map<String, Object>> orderList = recordingVideoMapper.startRecordingOrderList();
        for (Map<String, Object> map : orderList) {
            log.info("start nativeHandle>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + ModelUtil.getLong(map, "nativeHandle"));
            new StartThread(ModelUtil.getLong(map, "nativeHandle")).start();
            recordingVideoMapper.updateOrderRecordingStatus(ModelUtil.getLong(map, "id"), 1);
        }
    }

    //停止录制
    public void stop() {
        List<Map<String, Object>> orderList = recordingVideoMapper.stopRecordingOrderList();
        for (Map<String, Object> map : orderList) {
            log.info("stop nativeHandle>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + ModelUtil.getLong(map, "nativeHandle"));
            new StopThread(ModelUtil.getLong(map, "nativeHandle")).start();
            recordingVideoMapper.updateOrderRecordingStatus(ModelUtil.getLong(map, "id"), 0);
        }
    }

    //获取文件路径
    public void getProperties() {
        List<Map<String, Object>> orderList = recordingVideoMapper.getProperties();
        for (Map<String, Object> map : orderList) {
            log.info("Properties nativeHandle>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + ModelUtil.getLong(map, "nativeHandle"));
            new PropertiesThread(recordingVideoMapper, ModelUtil.getLong(map, "nativeHandle"), ModelUtil.getLong(map, "id")).start();
        }
    }


    /**
     * 测试时候用的接口方法，用的时候可以删除
     */
    public static void main(String[] args) {

        // 要匹配的文件名，测试用,修改的时候应该改成外面传进来的
        String tempFileNamePatch = ".mp4";
        // 指定的要搜索的盘符，测试用
        String tempDiskPath = "/home/qwq/file/video/20181213/201812122012536928192_042000/";

        String exc = FileUtil.exc(tempFileNamePatch, tempDiskPath);
    }
}

class RecordThread extends Thread {
    private RecordingVideoMapper recordingVideoMapper;
    private long orderId;
    private String orderno;
    private String token;

    public RecordThread(RecordingVideoMapper recordingVideoMapper, long orderId, String orderno, String token) {
        this.orderno = orderno;
        this.token = token;
        this.recordingVideoMapper = recordingVideoMapper;
        this.orderId = orderId;
    }

    @Override
    public void run() {
        try {
            CustomConfig.basePath = ConfigModel.BASEFILEPATH + FileUtil.FILE_STATIC_PATH;
            RecordingUtil recordingUtil = new RecordingUtil((Map<String, Object> customObj, long mNativeHandle) -> {
                recordingVideoMapper.updateOrdernNativeHandle(mNativeHandle, orderId);
            }, null);

            String[] args = new String[]{"--appId", com.syhdoctor.common.config.ConfigModel.AGORA.APPID, "--uid", "0", "--channelKey", token,
                    "--recordFileRootDir", ConfigModel.BASEFILEPATH + "file/video/", "--channel", orderno, "--appliteDir", ConfigModel.BASEFILEPATH + FileUtil.FILE_STATIC_PATH + "agora"
            };
            recordingUtil.createChannel(args);
            recordingUtil.unRegister();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class StartThread extends Thread {
    private long nativeHandle;

    public StartThread(long nativeHandle) {
        this.nativeHandle = nativeHandle;
    }

    @Override
    public void run() {
        try {
            CustomConfig.basePath = ConfigModel.BASEFILEPATH + FileUtil.FILE_STATIC_PATH;
            RecordingUtil recordingUtil = new RecordingUtil(null, null);
            recordingUtil.startService(nativeHandle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class StopThread extends Thread {
    private long nativeHandle;

    public StopThread(long nativeHandle) {
        this.nativeHandle = nativeHandle;
    }

    @Override
    public void run() {
        try {
            CustomConfig.basePath = ConfigModel.BASEFILEPATH + FileUtil.FILE_STATIC_PATH;
            RecordingUtil recordingUtil = new RecordingUtil(null, null);
            recordingUtil.stopService(nativeHandle);
            recordingUtil.leaveChannel(nativeHandle);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

class PropertiesThread extends Thread {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private RecordingVideoMapper recordingVideoMapper;
    private long nativeHandle;
    private long orderId;

    public PropertiesThread(RecordingVideoMapper recordingVideoMapper, long nativeHandle, long orderId) {
        this.recordingVideoMapper = recordingVideoMapper;
        this.nativeHandle = nativeHandle;
        this.orderId = orderId;
    }

    @Override
    public void run() {
        try {
            CustomConfig.basePath = ConfigModel.BASEFILEPATH + FileUtil.FILE_STATIC_PATH;
            RecordingUtil recordingUtil = new RecordingUtil(null, null);
            RecordingEngineProperties properties = recordingUtil.getProperties(nativeHandle);
            logger.error("properties=============================" + properties.GetStorageDir());
            String url = FileUtil.exc(".mp4", properties.GetStorageDir());
            if (!StrUtil.isEmpty(url)) {
                int file = url.indexOf("file");
                String substring = url.substring(file);
                recordingVideoMapper.updateOrderPullStatus(orderId, ModelUtil.setLocalUrl(substring));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}