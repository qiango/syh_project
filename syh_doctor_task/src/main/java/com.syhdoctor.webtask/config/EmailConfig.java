package com.syhdoctor.webtask.config;

import com.syhdoctor.common.utils.FileUtil;
import com.syhdoctor.common.utils.email.EmailConfigStorage;
import com.syhdoctor.common.utils.email.EmailService;
import com.syhdoctor.common.utils.email.impl.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class EmailConfig {


    @Value("${spring.mail.username}")
    private String emailfrom;

    @Bean
    public EmailService emailService() {
        EmailService emailService = new EmailServiceImpl();
        EmailConfigStorage emailConfigStorage = new EmailConfigStorage();
        emailConfigStorage.setEmailfrom(emailfrom);
        emailConfigStorage.setEmailpath(ConfigModel.BASEFILEPATH + FileUtil.getTempPath(FileUtil.FILE_EMAIL_PATH));
        emailService.setEmailConfigStorage(emailConfigStorage);
        return emailService;
    }
}
