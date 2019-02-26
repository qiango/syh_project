package com.syhdoctor.webtask.system.mapper;

import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webtask.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public abstract class SystemBaseMapper extends BaseMapper {

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
        String sql = "insert into message(url,name, type, type_name, message_type, sendid, message_text, message_subtext, delflag, create_time) " +
                "values (?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(url);
        params.add(name);
        params.add(type);
        params.add(typeName);
        params.add(messageType);
        params.add(sendId);
        params.add(messageText);
        params.add(messageSubtext);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }


}
