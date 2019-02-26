package com.syhdoctor.webserver;

import com.syhdoctor.common.utils.FileUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
@ServletComponentScan
public class WebserviceApplication {


    /**
     * 文件上传临时路径
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        if (FileUtil.validateFile("/data/tomcat/tmp/")) {
            factory.setLocation("/data/tomcat/tmp");
        } else {
            FileUtil.createFile("file/tomcat/tmp/");
            factory.setLocation(FileUtil.getFilePath("file/tomcat/tmp"));
        }
        return factory.createMultipartConfig();
    }

    public static void main(String[] args) {
        SpringApplication.run(WebserviceApplication.class, args);
    }

}
