package com.syhdoctor.webserver.service.wechat;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.wechat.AccessTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AccessTokenService extends BaseService {
    @Autowired
    private AccessTokenMapper accessTokenMapper;

    public Map<String, Object> getAccessToken() {
        return accessTokenMapper.getAccesstoken();
    }

    @Transactional
    public void setAccessToken(String access_token) {
        Map<String, Object> result= accessTokenMapper.getAccesstoken();
        if (result != null) {
            if (!access_token.equals(ModelUtil.getStr(result, "access_token"))) {
                accessTokenMapper.updateAccesstoken(access_token);
            }
        } else {
            accessTokenMapper.setAccesstoken(access_token);
        }
    }

}
