package com.syhdoctor.common.utils.encryption;

public interface IEncrypt {

    /**
     * 加密
     *
     * @param value 密文
     */
    String encrypt(String value);

    /**
     * 解密
     *
     * @param value
     */
    String decrypt(String value);

}
