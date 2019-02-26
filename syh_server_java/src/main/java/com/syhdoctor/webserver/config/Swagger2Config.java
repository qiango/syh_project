package com.syhdoctor.webserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.AbstractPathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration //标记配置类
@EnableSwagger2 //开启在线接口文档
public class Swagger2Config {
    private static final String SWAGGER_SCAN_BASE_PACKAGE = "com.syhdoctor.webserver";
    private static final String VERSION = "1.0.0";

    @Value("${swagger.base.apiPath}")
    private String swaggerPath;

//    /**
//     * 定义api组，
//     */
//    @Bean
//    public Docket innerApi() {
//
//        return new Docket(DocumentationType.SWAGGER_2)
//                .groupName("innerApi")
//                .genericModelSubstitutes(DeferredResult.class)
////                .genericModelSubstitutes(ResponseEntity.class)
//                .useDefaultResponseMessages(false)
//                .forCodeGeneration(true)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.zybros.jinlh"))
//                .paths(PathSelectors.any())
//                .build()
//                .apiInfo(innerApiInfo());
//    }
//
//    private ApiInfo innerApiInfo() {
//        return new ApiInfoBuilder()
//                .title("JinLH inner Platform API")//大标题
//                .description("内部api")//详细描述
//                .version("1.0")//版本
//                .termsOfServiceUrl("NO terms of service")
//                .contact(new Contact("stone", "https://www.jinlh.com", "787591269@qq.com"))//作者
//                .license("The Apache License, Version 2.0")
//                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
//                .build();
//    }
//
//    @Bean
//    public Docket openApi() {
//
//
//        Predicate<RequestHandler> predicate = new Predicate<RequestHandler>() {
//            @Override
//            public boolean apply(RequestHandler input) {
////                Class<?> declaringClass = input.declaringClass();
////                if (declaringClass == BasicErrorController.class)// 排除
////                    return false;
////                if(declaringClass.isAnnotationPresent(ApiOperation.class)) // 被注解的类
////                    return true;
////                if(input.isAnnotatedWith(ResponseBody.class)) // 被注解的方法
////                    return true;
//                if (input.isAnnotatedWith(ApiOperation.class))//只有添加了ApiOperation注解的method才在API中显示
//                    return true;
//                return false;
//            }
//        };
//
//
//        return new Docket(DocumentationType.SWAGGER_2)
//                .groupName("openApi")
//                .genericModelSubstitutes(DeferredResult.class)
////              .genericModelSubstitutes(ResponseEntity.class)
//                .useDefaultResponseMessages(false)
//                .forCodeGeneration(false)
//                .select()
//                .apis(predicate)
//                .paths(PathSelectors.any())//过滤的接口
//                .build()
//                .apiInfo(openApiInfo());
//    }
//
//    private ApiInfo openApiInfo() {
//        return new ApiInfoBuilder()
//                .title("JinLH Platform API")//大标题
//                .description("金老虎提供的OpenAPI")//详细描述
//                .version("1.0")//版本
//                .termsOfServiceUrl("NO terms of service")
//                .contact(new Contact("泽佑","www.zybros.com", "787591269@qq.com"))//作者
//                .license("The Apache License, Version 2.0")
//                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
//                .build();
//    }
    @Bean
    public Docket createRestApi() {
        if ("1".equals(ConfigModel.ISONLINE)) {
            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfoOnline())
                    .select()
                    .paths(PathSelectors.none())//如果是线上环境，添加路径过滤，设置为全部都不符合
                    .build();
        } else {
            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo())
                    .pathProvider(new AbstractPathProvider() {
                        @Override
                        protected String applicationPath() {
                            return swaggerPath;
                        }

                        @Override
                        protected String getDocumentationPath() {
                            return swaggerPath;
                        }
                    })
                    .select()
                    .apis(RequestHandlerSelectors.basePackage(SWAGGER_SCAN_BASE_PACKAGE))//api接口包扫描路径
                    .paths(PathSelectors.any())//可以根据url路径设置哪些请求加入文档，忽略哪些请求
                    .build();
        }
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("山屿海互联网医院接口文档")//设置文档的标题
                .description("用于管理所有功能模块...")//设置文档的描述->1.Overview
                .version(VERSION)//设置文档的版本信息-> 1.1 Version information
                .contact(new Contact("syh", "https://www.syhdoctor.com", ""))//设置文档的联系方式->1.2 Contact information
                .termsOfServiceUrl("https://www.syhdoctor.com")//设置文档的License信息->1.3 License information
                .build();
    }

    private ApiInfo apiInfoOnline() {
        return new ApiInfoBuilder()
                .title("")
                .description("")
                .license("")
                .licenseUrl("")
                .termsOfServiceUrl("")
                .version("")
                .contact(new Contact("", "", ""))
                .build();
    }
}
