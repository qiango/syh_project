package com.syhdoctor.webserver.config.ali;

import com.syhdoctor.common.pay.AliAppPayServiceImpl;
import com.syhdoctor.common.pay.AliWebPayServiceImpl;
import com.syhdoctor.common.pay.IPayService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AliPayConfig {

    @Bean(name = "aliWebPayImpl")
    public IPayService aliWebPayImpl() {
        return new AliWebPayServiceImpl();
    }

    @Bean(name = "aliAppPayImpl")
    public IPayService aliAppPayImpl() {
        return new AliAppPayServiceImpl();
    }

}
