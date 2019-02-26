package com.syhdoctor.common.utils.email.impl;

import com.syhdoctor.common.utils.email.EmailConfigStorage;
import com.syhdoctor.common.utils.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;

public class EmailServiceImpl implements EmailService {


    @Autowired
    private JavaMailSender mailSender;

    static {
        System.setProperty("mail.mime.splitlongparameters", "false");
    }

    private EmailConfigStorage emailConfigStorage;

    @Override
    public void setEmailConfigStorage(EmailConfigStorage emailConfigStorage) {
        this.emailConfigStorage = emailConfigStorage;
    }

    @Override
    public void sendSimpleMail(String sendTo, String titel, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(emailConfigStorage.getEmailfrom());
            helper.setTo(sendTo);
            helper.setSubject(titel);
            helper.setText(content, false);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendAttachmentsMail(String sendTo, String titel, String content, String path) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(emailConfigStorage.getEmailfrom());
            helper.setTo(sendTo.split(","));
            helper.setSubject(titel);
            helper.setText(content);
            helper.addAttachment(path, new FileSystemResource(emailConfigStorage.getEmailpath() + path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mailSender.send(mimeMessage);
    }


}
