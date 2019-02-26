package io.agora.recording;

import io.agora.recording.common.Common;

public interface RecordingEventHandler {

    void nativeObjectRef(long nativeHandle);

    void onLeaveChannel(int reason);

    void onError(int error, int stat_code);

    void onWarning(int warn);

    void onJoinChannelSuccess(String channelId, long uid);

    void onUserOffline(long uid, int reason);

    void onUserJoined(long uid, String recordingDir);

    void audioFrameReceived(long uid, int type, Common.AudioFrame frame);

    void videoFrameReceived(long uid, int type, Common.VideoFrame frame, int rotation);

    void stopCallBack();

    void recordingPathCallBack(String path);
}
