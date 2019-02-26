package com.syhdoctor.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigModel {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    public static String BASEFILEPATH;

    //ali
    public static class AliPay {
        // 商户appid
        public final static String APPID = "2018091761410190";
        // 私钥
        public final static String RSA_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCBeyVIUg1EVhic6A3PJZ5xpSToZN/INwoPqaQtN2Qtb27r28xc+jH4imE1avF8yZelOPyEa20UobbYxWosMwt1Dy1/2dOGox1/ZOmfRp0uNDff9eCXYzzaVFYeGJ6sChe5+CLWkcXtvUR+xeCOqjo2fa2hmHyn2mMSUQSCcJMJIcYCIRD8MKBg1An7biMKGurBKK/3odqsuiD2sQozm6YU3zso8Nasu6sUApBBsnRwKpIjlbYz9ajJ1Ge/hre7I07sjtOG+RHK5vGRstd+K4yVM7D8DjJPWW7UjKfORt39uW/tpS/rMnm54Zh4KuWPnrWIdsqOM4W0Dc/82N64SpPtAgMBAAECggEAe6CG0mx1el/yFC6EsNosVDnn6XQXLuVOV/ITbZDLt44EDHLKx6OjfrkxY31+oTUh84FbqVugxOg62vJzE0yRje019jKXAqBPk8lYPtl/ThRRnSb4j9MS0SthJk8/gOgKC2heaxy4LOxfr+NjKxRhjJGm7PnZW7LqkRyF/71Yxofx3AuQC6Yc9Qzcb7aQtnYVvyyHzF/UAQmggeMcf+LLUnnTZJTidce7z7pReEilYs6bOpBlIk1Yj/s5jqHOVn0mBTR5PcH5w/hdMiPpc5kvDAC/ZbrBe+q/feCkk84K20uLxFaTUu7BpQkhsRHlnyHMz/KVMB4/GSQrRGfdq7BIgQKBgQDk+sqPJnDY5Zki5yl246oMxysLGXjNIHBZLysA6T14xyTS7K8/4Sv2cL5wubxpWVNYfX5BuCzNoUb3tPCDXQRxFxARCipTXhbk3MQff0naQhczYzY4vzyBelmBAxg4bY3HqkICKC/sV/x7fiZaGSbW/tJOTAnNA6bkrqMPBcchHQKBgQCQwp32goBrzU6/MjxoFeKCqCJBgZycM/x3s3p9Ta0qAD7BsOCoOIQnY4JNyILJ4vkbpcxw2QUjy6jZrPNyO6BEiXJcl521jj6+TUIQwEz6/KZOyLnVnYPBtqjNmsobovri6AW2rauQUhmVWIZvp7IFbeHlrS6HFQwmrqRtfKIVEQKBgD3uz2bUoOUphLf6Ci8L+Oyn2i6QXyosQfb9WQZQD75hLsaWGfiPMNLhP8FBm4JrFK30k3e3FvcvTgClwf/l4tOrxz60nyoZYtvYEnvqc9FC2v+dMAc9QmJK83Z/ncd1akm89UmcEQo96h6Eyl7IOscf+UYQLv5ypGtqCNSBXZDJAoGBAISZwRiNchVNe1SoL60oXtRQLxDSKSLse9cqEIm3HWfb0aCa5S8yldt+y110zlkgEA/DUAIOySaA56p9QoLhG22seZVXTJ+CLPckzd+KRvmhdLW2KIQERSyspPOxVnD6WdZ/srwjWYRZsBWdgYfFlMxkWwiIGpaiMP89+mBQSigBAoGAVBag2xByS1l+VwGhh2oorTJ4DdYIHj8M13Ti736IA1zSni659DqbmGenUANjdG7INy2iH9OFoouS2T3ubuXyJh8NOs13QgwLz9QaeRdMB4e/v2nqDL+CApjXxgQA3wjgYLIhGOkX2GkMkqnJNV0xoWs3icMq+8cCbKko7SJ3nMY=";
        // 请求网关地址
        public final static String URL = "https://openapi.alipay.com/gateway.do";
        // 编码
        public final static String CHARSET = "UTF-8";
        // 返回格式
        public final static String FORMAT = "json";
        //支付宝公钥 正式
        public final static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh+8ldfDSoW8MA0DmkjMWLPxz9h80m3/w9OHyjfKuk+e4Y9bXzR14tLW1dpLbh9MRMB0tdm9OK+g1MDfY60m8lZxVuDGX5lOfdGb7YTuEU0b8bpbhLTXtuynrgxyIMvwJCs5YaMPS1WOPnuL8mlZYMMnOC7qn2J7+MM2ROBGAaUJizxqj13HsFrvqg5ETmyCJF0Dv+ooOHWWTOCZHZAE+mXZresKUpBPHAPscqMx6W2K59kMztjscKyNiftL36vK0COm8mBY9zAT/DSZ+yjLjPmyqlcYfIsukpFCNPqp1Cdkth5WH8OMakwETFb2iq5oUuuCclA42z+KUxBMpaGC07wIDAQAB";
        // RSA2
        public final static String SIGNTYPE = "RSA2";
        // 超时时间 可空
        public final static String TIMEOUT_EXPRESS = "2m";
        // 销售产品码 必填
        public static final String PRODUCT_CODE = "QUICK_MSECURITY_PAY";
    }

    //ali
    public static class AliMap {

        //授权链接
        public final static String AUTHORIZE_URL = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=%s&scope=%s&redirect_uri=%s&state=%s";

        /**
         * 授权访问令牌的授权类型
         */
        public static final String GRANT_TYPE = "authorization_code";

        // 授权方式
        public final static String AUTH_BASE = "auth_base";

        // 授权方式 可以获取用户信息
        public final static String AUTH_USER = "auth_user";

        // 商户appid
        public final static String APPID = "2018091761435216";
        // 私钥
        public final static String RSA_PRIVATE_KEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDQKuMS+2zhkzx2o9VLDrt4SPmDmBomUFUeg47hRZoFFeBNLHf0XU3OLmyze9nL/FPZGtAwuewR+/XoG4fkuafCzKm+7zUWChw7gW+KENvi2NiUq/THMZeG/Nv+npV2AcRwVBlNwkEUHmcbtwpE5ZBoPmBzjO10dbclnd6RuQraQuAE5TX+ftZMIkCowMNhXNBjWzvDNY3ukgVed/tdytbrb3bATB6FrpSvkjggOT82+Wmot8EA38wwFO0Fzf8T5UUUswrE++MgyLY10omD4h0fSz2QFEPw7r06anyQkwKI1wxUfURoFe6GDJsOz0QuKDryt3fcbBQVPl6vQzwMe0fZAgMBAAECggEBAIPOI+ko+U/7AhW8tpjHwzvuOM5xyFzqEHs5oYZRo6wEpfk3Ztx7F4CtNHaQvN7D7fNRXvD7R9Y7SwCkLe9M/kQBgpk6rMo/rJCg7BbnhdKkqr24A0z8wYzfHgyiLNCDXfYhycPKc5phhbeutfnZwQ3AMSH44YET59sRkpOCLrTy7qqT0tN4tG8CYGiYux05miLT9sBynBkfsvx9n9P0vXxlDrjJvTBmgFTQvnpvT3rhhCyrKOmHX8k+DfUWX+d8CfIufL77Z6NqgF13YGJW8w2+lQwNpVh78B3z5hsVpsRKAP/cKRCjYruakuurkC8InlAPtW3ZlExaqWoWSKzKfAECgYEA7FBjU+fvpzMNsLWwrfQfUeIZKxW2YlodC0hJtKSiCQwzfpyTli3zKzhYvzpl5Vuc6r/Qnpd/HOHTTBzO1uKy5tYroPJgZP5waUmWUBg1PBZUE1GhgG4ELCgpb6E/ze/4+O1qkOn+O6PqEapTDfjL1CSwS2BwLjEA4/MjrJ8ZFgkCgYEA4YI/3QJNZT3B/xOhKXokU9Xpg5ipMSJHALk+PYidtxzcpa6Zv4HoOJWrXH/foN0uZ6A1U4oZA7KOrOJCAEcikOSBGkeGxcEaokGembMxjyClOEsqQPsvNvBfVDVifmtCbfS8q9AkHX6arH6BEvQcHAdwaYzrJ1kRXt6sTSAsl1ECgYBt87VTi/4g4ItFJjSPq5+K/NVTC6YxutX6subAaz2NUiT/iyDMZpLXED+SsjxI7EhlnVwW/kxBhv1GU70VlKNC7nGcUEnzk8N/6aJLoAfc7lKuLnQjiozccs9cxj0mt7ozMFw+EvGclFbLY2GDhbho+60fVT6870YHydBWeCw26QKBgQDecyWcVhEStYEQpcRsrdf3+yOGdKkxlqaDyDfla0Zow0fFCB8SEouIhdusO1fVz1lfT2JLLgbO5uxnDiodLA6k70OLMNN5b6teM2zIIUPZ9FIG3d6oix9gCm1/G8o0+olk/e6/bWqyzZi2X3PjxnmUDKKO/DtbbTj9ZJqC2iIigQKBgQCICttpGUosYufZEqBgnVYbNsCBLeg6mkSBfYtocmBZcg0PHVu8npiBsy4GH7ReFTBJNbymV911xIkhbV+BpZ56shSUAIfCfATXyohXNyY7rUYbWnMz5hjcNJvqhOYofDPunhjo0RsjViMm7EFPx6HmkcfRT9rCLoog1Hj36dSNVg==";
        // 请求网关地址
        public final static String URL = "https://openapi.alipay.com/gateway.do";
        // 编码
        public final static String CHARSET = "UTF-8";
        // 返回格式
        public final static String FORMAT = "json";
        //支付宝公钥 正式
        public final static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh+8ldfDSoW8MA0DmkjMWLPxz9h80m3/w9OHyjfKuk+e4Y9bXzR14tLW1dpLbh9MRMB0tdm9OK+g1MDfY60m8lZxVuDGX5lOfdGb7YTuEU0b8bpbhLTXtuynrgxyIMvwJCs5YaMPS1WOPnuL8mlZYMMnOC7qn2J7+MM2ROBGAaUJizxqj13HsFrvqg5ETmyCJF0Dv+ooOHWWTOCZHZAE+mXZresKUpBPHAPscqMx6W2K59kMztjscKyNiftL36vK0COm8mBY9zAT/DSZ+yjLjPmyqlcYfIsukpFCNPqp1Cdkth5WH8OMakwETFb2iq5oUuuCclA42z+KUxBMpaGC07wIDAQAB";
        // RSA2
        public final static String SIGNTYPE = "RSA2";
    }

    //短信
    public static class SMS {

        //产品名称:云通信短信API产品,开发者无需替换
        public static final String product = "Dysmsapi";
        //产品域名,开发者无需替换
        public static final String domain = "dysmsapi.aliyuncs.com";

        public static final String accessKeyId = "LTAI59XCMq8Tl7x9";

        public static final String accessKeySecret = "E86lDQpupoSkrTXQNr9JsWMxhxOq4q";

        public static final String FREESIGNNAME = "山屿海医生";

        //用户退款成功成功通知-用户 山屿海医生】感谢您使用山屿海医生，您有一笔退款money元，已经退回至您的 paytype账户
        public static final String refund_tempid = "SMS_149416300";

        //用户下单图文问诊订单-医生 【山屿海医生】尊敬的doctor医生，您有新的图文问诊订单，请到山屿海医生医生版APP中及时处理
        public static final String answer_order_success_tempid = "SMS_149416302";

        //用户向您预约了${time}的视频咨询，请提前安排好时间，打开山屿海医生医生版APP查看详情。
        public static final String video_order_success_tempid = "SMS_152544796";

        //用户向您预约了${time}的电话咨询服务，请保持通信畅通，打开山屿海医生医生版APP查看详情
        public static final String phone_order_success_tempid = "SMS_152544812";

//        //用户问诊成功-医生 【山屿海医生】尊敬的doctor医生，您有一笔问诊订单积分money到账，已存入您的积分账户，进入山屿海医生医生版APP查看详情
//        public static final String tempidThree = "SMS_149421184";

        //尊敬的${doctorname}，您有一笔问诊订单${money}元到账，已存入您的钱包账户，进入山屿海医生医生版APP查看详情
        public static final String extract_success_tempid= "SMS_152210454";


        //登录短信模板
        public static final String Login_sms_template = "SMS_149422869";

        //提现模板，您正在使用山屿海医生医生版提现，验证码为：${code}，5分钟内有效！请勿泄漏
        public static final String putforward_sms_template = "SMS_150860775";

        //医生审核失败  尊敬的doctor医⽣，很抱歉，您的认证审核未通过，请更新资料后重新认证。客服电话：4000668880
        public static final String doctor_examine_fail = "SMS_150183827";

        //redis有效时间
        public static final long timeout = 900;
    }

    //七陌相关参数
    public static class QIMO {
        public static final String ACCOUNTID = "N00000032760";
        public static final String APISECRET = "0a8b3cb0-b0b0-11e8-ac83-613efed083c8";
        public static final String SERVICENO = "01025270191";
    }

    //声网相关参数
    public static class AGORA {
        public static final String APPID = "d7a1141a4ea3469c98a1997f3d3b110d";
        public static final String ACCOUNT = "wei.jiang@syhdoctor.com";
        public static final String CERTIFICATE = "035dab8f65e24ba0b30b9d39ad04e3a0";//项目中开启

        public static final long EXPIREDTSINMILLISECOND = 0;  //声网token提前创建时间毫秒
    }
}
