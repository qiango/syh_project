package com.syhdoctor.webserver.api.request;

import com.alibaba.fastjson.annotation.JSONField;

public interface IRequest<T> {


    @JSONField(serialize = false)
    String getApiName();

    @JSONField(serialize = false)
    Class<T> getResponseClass();

    /**
     * 客户端参数检查，减少服务端无效调用。
     */
    void Validate();
}
