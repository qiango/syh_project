package com.syhdoctor.webserver.thirdparty.ali.constants;


/**
 * 支付宝服务窗环境常量（demo中常量只是参考，需要修改成自己的常量值）
 *
 * @author taixu.zqq
 * @version $Id: AlipayServiceConstants.java, v 0.1 2014年7月24日 下午4:33:49 taixu.zqq Exp $
 */
public class AlipayServiceEnvConstants {

    /**支付宝公钥-从支付宝生活号详情页面获取*/
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh+8ldfDSoW8MA0DmkjMWLPxz9h80m3/w9OHyjfKuk+e4Y9bXzR14tLW1dpLbh9MRMB0tdm9OK+g1MDfY60m8lZxVuDGX5lOfdGb7YTuEU0b8bpbhLTXtuynrgxyIMvwJCs5YaMPS1WOPnuL8mlZYMMnOC7qn2J7+MM2ROBGAaUJizxqj13HsFrvqg5ETmyCJF0Dv+ooOHWWTOCZHZAE+mXZresKUpBPHAPscqMx6W2K59kMztjscKyNiftL36vK0COm8mBY9zAT/DSZ+yjLjPmyqlcYfIsukpFCNPqp1Cdkth5WH8OMakwETFb2iq5oUuuCclA42z+KUxBMpaGC07wIDAQAB";

    /**签名编码-视支付宝服务窗要求*/
    public static final String SIGN_CHARSET      = "GBK";

    /**字符编码-传递给支付宝的数据编码*/
    public static final String CHARSET           = "GBK";

    /**签名类型-视支付宝服务窗要求*/
    public static final String SIGN_TYPE         = "RSA2";

    /**开发者账号PID*/
    public static final String PARTNER           = "";

    // 返回格式
    public final static String FORMAT = "json";

    /** 服务窗appId  */
    //TODO !!!! 注：该appId必须设为开发者自己的生活号id
    public static final String APP_ID            = "2018091761435216";

    //TODO !!!! 注：该私钥为测试账号私钥  开发者必须设置自己的私钥 , 否则会存在安全隐患 
    public static final String PRIVATE_KEY       = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDQKuMS+2zhkzx2o9VLDrt4SPmDmBomUFUeg47hRZoFFeBNLHf0XU3OLmyze9nL/FPZGtAwuewR+/XoG4fkuafCzKm+7zUWChw7gW+KENvi2NiUq/THMZeG/Nv+npV2AcRwVBlNwkEUHmcbtwpE5ZBoPmBzjO10dbclnd6RuQraQuAE5TX+ftZMIkCowMNhXNBjWzvDNY3ukgVed/tdytbrb3bATB6FrpSvkjggOT82+Wmot8EA38wwFO0Fzf8T5UUUswrE++MgyLY10omD4h0fSz2QFEPw7r06anyQkwKI1wxUfURoFe6GDJsOz0QuKDryt3fcbBQVPl6vQzwMe0fZAgMBAAECggEBAIPOI+ko+U/7AhW8tpjHwzvuOM5xyFzqEHs5oYZRo6wEpfk3Ztx7F4CtNHaQvN7D7fNRXvD7R9Y7SwCkLe9M/kQBgpk6rMo/rJCg7BbnhdKkqr24A0z8wYzfHgyiLNCDXfYhycPKc5phhbeutfnZwQ3AMSH44YET59sRkpOCLrTy7qqT0tN4tG8CYGiYux05miLT9sBynBkfsvx9n9P0vXxlDrjJvTBmgFTQvnpvT3rhhCyrKOmHX8k+DfUWX+d8CfIufL77Z6NqgF13YGJW8w2+lQwNpVh78B3z5hsVpsRKAP/cKRCjYruakuurkC8InlAPtW3ZlExaqWoWSKzKfAECgYEA7FBjU+fvpzMNsLWwrfQfUeIZKxW2YlodC0hJtKSiCQwzfpyTli3zKzhYvzpl5Vuc6r/Qnpd/HOHTTBzO1uKy5tYroPJgZP5waUmWUBg1PBZUE1GhgG4ELCgpb6E/ze/4+O1qkOn+O6PqEapTDfjL1CSwS2BwLjEA4/MjrJ8ZFgkCgYEA4YI/3QJNZT3B/xOhKXokU9Xpg5ipMSJHALk+PYidtxzcpa6Zv4HoOJWrXH/foN0uZ6A1U4oZA7KOrOJCAEcikOSBGkeGxcEaokGembMxjyClOEsqQPsvNvBfVDVifmtCbfS8q9AkHX6arH6BEvQcHAdwaYzrJ1kRXt6sTSAsl1ECgYBt87VTi/4g4ItFJjSPq5+K/NVTC6YxutX6subAaz2NUiT/iyDMZpLXED+SsjxI7EhlnVwW/kxBhv1GU70VlKNC7nGcUEnzk8N/6aJLoAfc7lKuLnQjiozccs9cxj0mt7ozMFw+EvGclFbLY2GDhbho+60fVT6870YHydBWeCw26QKBgQDecyWcVhEStYEQpcRsrdf3+yOGdKkxlqaDyDfla0Zow0fFCB8SEouIhdusO1fVz1lfT2JLLgbO5uxnDiodLA6k70OLMNN5b6teM2zIIUPZ9FIG3d6oix9gCm1/G8o0+olk/e6/bWqyzZi2X3PjxnmUDKKO/DtbbTj9ZJqC2iIigQKBgQCICttpGUosYufZEqBgnVYbNsCBLeg6mkSBfYtocmBZcg0PHVu8npiBsy4GH7ReFTBJNbymV911xIkhbV+BpZ56shSUAIfCfATXyohXNyY7rUYbWnMz5hjcNJvqhOYofDPunhjo0RsjViMm7EFPx6HmkcfRT9rCLoog1Hj36dSNVg==";

    //TODO !!!! 注：该公钥为测试账号公钥  开发者必须设置自己的公钥 ,否则会存在安全隐患
    public static final String PUBLIC_KEY        = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0CrjEvts4ZM8dqPVSw67eEj5g5gaJlBVHoOO4UWaBRXgTSx39F1Nzi5ss3vZy/xT2RrQMLnsEfv16BuH5Lmnwsypvu81FgocO4FvihDb4tjYlKv0xzGXhvzb/p6VdgHEcFQZTcJBFB5nG7cKROWQaD5gc4ztdHW3JZ3ekbkK2kLgBOU1/n7WTCJAqMDDYVzQY1s7wzWN7pIFXnf7XcrW6292wEweha6Ur5I4IDk/NvlpqLfBAN/MMBTtBc3/E+VFFLMKxPvjIMi2NdKJg+IdH0s9kBRD8O69Omp8kJMCiNcMVH1EaBXuhgybDs9ELig68rd33GwUFT5er0M8DHtH2QIDAQAB";
    /**支付宝网关*/
    public static final String ALIPAY_GATEWAY    = "https://openapi.alipay.com/gateway.do";

    /**授权访问令牌的授权类型*/
    public static final String GRANT_TYPE        = "authorization_code";
}