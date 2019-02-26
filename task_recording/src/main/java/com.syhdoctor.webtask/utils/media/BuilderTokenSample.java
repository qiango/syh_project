package com.syhdoctor.webtask.utils.media;

public class BuilderTokenSample {
    static String appId = "8ddef1ab63324a988f9eb6373cef56cc";
    static String appCertificate = "1834204805fb4a01a96244bb0dcb8953";
    static String channelName = "20180406161348";
    static String uid = "000555";
    static int expireTimestamp = 0;

    public static void main(String[] args) throws Exception {
        SimpleTokenBuilder token = new SimpleTokenBuilder(appId, appCertificate, channelName, uid);
        token.initPrivileges(SimpleTokenBuilder.Role.Role_Attendee);
        token.setPrivilege(AccessToken.Privileges.kJoinChannel, expireTimestamp);
        token.setPrivilege(AccessToken.Privileges.kPublishAudioStream, expireTimestamp);
        token.setPrivilege(AccessToken.Privileges.kPublishVideoStream, expireTimestamp);
        token.setPrivilege(AccessToken.Privileges.kPublishDataStream, expireTimestamp);

        String result = token.buildToken();
        System.out.println(result);
    }
}
