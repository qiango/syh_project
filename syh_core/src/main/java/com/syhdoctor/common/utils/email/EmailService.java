package com.syhdoctor.common.utils.email;

public interface EmailService {


    void setEmailConfigStorage(EmailConfigStorage emailConfigStorage);
    /**
     * 发送简单邮件
     *
     * @param sendTo  收件人地址
     * @param titel   邮件标题
     * @param content 邮件内容
     */
    void sendSimpleMail(String sendTo, String titel, String content) throws Exception;

    /**
     * 发送简单邮件
     *
     * @param sendTo       收件人地址
     * @param titel        邮件标题
     * @param content      邮件内容
     * @param path<文件名，附件> 附件列表
     */
    void sendAttachmentsMail(String sendTo, String titel, String content, String path) throws Exception;

}
