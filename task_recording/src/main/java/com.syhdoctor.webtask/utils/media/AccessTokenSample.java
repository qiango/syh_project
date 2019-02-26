package com.syhdoctor.webtask.utils.media;

public class AccessTokenSample {
    static String appId = "d7a1141a4ea3469c98a1997f3d3b110d";
    static String appCertificate = "035dab8f65e24ba0b30b9d39ad04e3a0";
    static String channelName = "201812141112484088192";
    static String uid = "0070";
    static int expireTimestamp = 0;

    public static void main(String[] args) throws Exception {
        AccessToken token = new AccessToken(appId, appCertificate, channelName, uid);
        token.addPrivilege(AccessToken.Privileges.kJoinChannel, expireTimestamp);
        String result = token.build();
        System.out.println(result);

        AccessToken t = new AccessToken("", "", "");
        t.fromString(result);

        System.out.println();
        System.out.print("\nappId:\t" + t.appId);
        System.out.print("\nappCertificate:\t" + t.appCertificate);
        System.out.print("\nCRC channelName:\t" + t.crcChannelName + " crc calculated " + Utils.crc32(channelName));
        System.out.print("\nCRC uid:\t" + t.crcUid + " crc calculated " + Utils.crc32(uid));
        System.out.print("\nts:\t" + t.message.ts);
        System.out.print("\nsalt:\t" + t.message.salt);
    }
}
