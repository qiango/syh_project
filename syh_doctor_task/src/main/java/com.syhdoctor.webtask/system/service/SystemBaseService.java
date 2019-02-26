package com.syhdoctor.webtask.system.service;


import com.syhdoctor.webtask.base.service.BaseService;
import com.syhdoctor.webtask.system.mapper.SystemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class SystemBaseService extends BaseService {

    @Autowired
    private SystemMapper systemMapper;


    /**
     * 添加消息
     *
     * @param url            医生、用户头像
     * @param name           展示标题
     * @param type           1 用户 2 医生
     * @param typeName       根据不同类型填写不同数据
     * @param messageType    消息类型
     * @param sendId         发送给谁
     * @param messageText    消息文本
     * @param messageSubtext 消息副文本
     * @return
     */
    public boolean addMessage(String url, String name, int type, String typeName, int messageType, long sendId, String messageText, String messageSubtext) {
        return systemMapper.addMessage(url, name, type, typeName, messageType, sendId, messageText, messageSubtext);
    }


}
