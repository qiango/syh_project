package io.agora.recording.common;
public class RecordingConfig {
  public RecordingConfig() {
    isAudioOnly = false;
    isVideoOnly = false;
    isMixingEnabled = false;
    mixResolution = "";
    decryptionMode = "";
    secret = "";
    appliteDir = "";
    recordFileRootDir = "";
    cfgFilePath = "";
    proxyServer = "";
    
    lowUdpPort = 0;//40000;
    highUdpPort = 0;//40004;
    idleLimitSec = 300;
    captureInterval = 5;
    triggerMode = 0;

    decodeVideo = Common.VIDEO_FORMAT_TYPE.VIDEO_FORMAT_DEFAULT_TYPE;
    decodeAudio = Common.AUDIO_FORMAT_TYPE.AUDIO_FORMAT_DEFAULT_TYPE;
    channelProfile = Common.CHANNEL_PROFILE_TYPE.CHANNEL_PROFILE_COMMUNICATION;
    streamType = Common.REMOTE_VIDEO_STREAM_TYPE.REMOTE_VIDEO_STREAM_HIGH;
  }
  public boolean isAudioOnly;
  public boolean isVideoOnly;
  public boolean isMixingEnabled;
  public boolean mixedVideoAudio;
  public String mixResolution;
  public String decryptionMode;
  public String secret;
  public String appliteDir;
  public String recordFileRootDir;
  public String cfgFilePath;
  //decodeVideo: default 0 (0:save as file, 1:h.264, 2:yuv, 3:jpg buffer, 4:jpg file, 5:jpg file and video file)
  public Common.VIDEO_FORMAT_TYPE decodeVideo;
  //decodeAudio:  (default 0 (0:save as file, 1:aac frame, 2:pcm frame, 3:mixed pcm frame) (Can't combine with isMixingEnabled) /option)
  public Common.AUDIO_FORMAT_TYPE decodeAudio;
  public int lowUdpPort;
  public int highUdpPort;
  public int idleLimitSec;
  public int captureInterval;
  //channelProfile:0 braodacast, 1:communicate; default is 1
  public Common.CHANNEL_PROFILE_TYPE channelProfile;
  //streamType:0:get high stream 1:get low stream; default is 0
  public Common.REMOTE_VIDEO_STREAM_TYPE streamType;
  public int triggerMode;
  public String proxyServer; //format ipv4:port
}
