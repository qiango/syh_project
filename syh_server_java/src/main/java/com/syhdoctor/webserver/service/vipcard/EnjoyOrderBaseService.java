package com.syhdoctor.webserver.service.vipcard;

import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.vipcard.EnjoyOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public abstract class EnjoyOrderBaseService extends BaseService {

    @Autowired
    private EnjoyOrderMapper enjoyOrderMapper;

    public List<Map<String, Object>> getEnjoyOrder(String name, String phone, long begintime, long endtime, int pageIndex, int pageSize) {

        return enjoyOrderMapper.getEnjoyOrder(name, phone, begintime, endtime, pageIndex, pageSize);
    }

    public long getEnjoyOrderCount(String name, String phone, long begintime, long endtime) {
        return enjoyOrderMapper.getEnjoyOrderCount(name, phone, begintime, endtime);
    }

    public Map<String, Object> getEnjoyOrderId(long id) {
        return enjoyOrderMapper.getEnjoyOrderId(id);
    }


/*
尊享列表
 */
    public List<Map<String, Object>> getEnjoyList(String name, String phone, int level, long begintime, long endtime, int pageIndex, int pageSize) {

        return enjoyOrderMapper.getEnjoyList(name, phone, level, begintime, endtime, pageIndex, pageSize);
    }

    public long getEnjoyListCount(String name, String phone, int level, long begintime, long endtime) {
        return enjoyOrderMapper.getEnjoyListCount(name, phone, level, begintime, endtime);
    }


    public Map<String, Object> getEnjoyListId(long id) {
        return enjoyOrderMapper.getEnjoyListId(id);
    }

    public boolean updateEnjoyList(int ceefax, int video, long id) {
        return enjoyOrderMapper.updateEnjoyList(ceefax, video, id);
    }









    /*
    尊享值
     */


}
