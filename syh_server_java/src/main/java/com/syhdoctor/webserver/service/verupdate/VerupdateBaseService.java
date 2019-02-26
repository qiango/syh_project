package com.syhdoctor.webserver.service.verupdate;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.verupdate.VerupdateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public abstract class VerupdateBaseService extends BaseService {

    @Autowired
    private VerupdateMapper verupdateMapper;

    /**
     * 检查更新
     *
     * @param type   1: android 2:ios
     * @param system 1: 用户端 2:医生端
     * @return
     */
    public Map<String, Object> checkUpdate(int type, int system) {
        return verupdateMapper.checkUpdate(type, system);
    }

    public List<Map<String, Object>> verupdateList() {
        return verupdateMapper.verupdateList((Map<String, Object> value) -> {
            value.put("type", ModelUtil.getInt(value, "type") == 1 ? "Android" : "Ios");
            value.put("system", ModelUtil.getInt(value, "system") == 1 ? "用户端" : "医生端");
            value.put("identification", ModelUtil.getInt(value, "identification") == 1 ? "是" : "否");
            return value;
        });
    }

    public boolean updateVerupdate(long id, String url, String vernumber,int identification, long agentid) {
        return verupdateMapper.updateVerupdate(id, url, vernumber, identification,agentid);
    }

    public Map<String,Object> getVerupdate(long id){
        Map<String, Object> verupdate = verupdateMapper.getVerupdate(id);
        verupdate.put("type", ModelUtil.getInt(verupdate, "type") == 1 ? "Android" : "Ios");
        verupdate.put("system", ModelUtil.getInt(verupdate, "system") == 1 ? "用户端" : "医生端");
        return verupdate;
    }
}
