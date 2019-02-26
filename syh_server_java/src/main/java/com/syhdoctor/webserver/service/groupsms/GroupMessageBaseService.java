package com.syhdoctor.webserver.service.groupsms;

import com.syhdoctor.common.utils.EnumUtils.TypeNameAppPushEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.TextFixed;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.groupsms.GroupMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public abstract class GroupMessageBaseService extends BaseService {

    @Autowired
    private GroupMessageMapper groupMessageMapper;


    public boolean addGroupMessageAll(int type, String messagetext,String typename) {
        if (type == 2) {
            groupMessageMapper.addGroupMessageDoctorAll(type, TextFixed.systemNotification, TypeNameAppPushEnum.SystemMessage.getCode(), messagetext,typename);
        } else {
            groupMessageMapper.addGroupMessageUserAll(type,TextFixed.systemNotification, TypeNameAppPushEnum.SystemMessage.getCode(), messagetext,typename);
        }
        return true;
    }


    public boolean addGroupMessageId(int type, String messagetext, List<?> id,String typename) {
        int idsize = id.size();
        if (idsize > 0) {
            for (Object key : id) {
                if (type == 2) {
                    groupMessageMapper.addGroupMessageDoctorId(type,TextFixed.systemNotification, TypeNameAppPushEnum.SystemMessage.getCode(), messagetext, ModelUtil.strToLong(String.valueOf(key), 0),typename);
                } else if (type == 1) {
                    groupMessageMapper.addGroupMessageUserId(type,TextFixed.systemNotification, TypeNameAppPushEnum.SystemMessage.getCode(), messagetext, ModelUtil.strToLong(String.valueOf(key), 0),typename);
                }
            }
        }
        return true;
    }


    /**
     * 下拉框名称
     *
     * @return
     */
    public List<Map<String, Object>> doctorName(long id,String name, int pageIndex, int pageSize) {
        return groupMessageMapper.doctorName(id,name,pageIndex,pageSize);
    }
    public long doctorNameCount(long id, String name) {
        return groupMessageMapper.doctorNameCount(id,name);
    }


    public List<Map<String, Object>> userName(long id,String name, int pageIndex, int pageSize) {
        return groupMessageMapper.userName(id,name,pageIndex,pageSize);
    }

    public long userNameCount(long id, String name) {
        return groupMessageMapper.userNameCount(id,name);
    }

    public List<Map<String, Object>> nameAll(int type,long id,String name, int pageIndex, int pageSize) {
        List<Map<String, Object>> names = null;
        if(type == 1){
            names = userName(id,name,pageIndex,pageSize);
        }else{
            names = doctorName(id,name,pageIndex,pageSize);
        }
        return names;
    }


}
