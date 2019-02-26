package com.syhdoctor.webtask.vipcard.service;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webtask.base.service.BaseService;
import com.syhdoctor.webtask.vipcard.mapper.VipCardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2018/11/12
 */
@Service
public class VipCardService extends BaseService {

    @Autowired
    private VipCardMapper vipCardMapper;

    public boolean updateList(){
        List<Map<String,Object>> mapList=vipCardMapper.getList();
        try {
            for (Map<String, Object> map : mapList) {
                long expireTime = ModelUtil.getLong(map, "vip_expiry_time");
                long id = ModelUtil.getLong(map, "id");
                long nowTime = UnixUtil.getNowTimeStamp();
                if (nowTime > expireTime || nowTime == expireTime) {//过期
                    vipCardMapper.updateListUser(id, 0);
                }else {
                    continue;
                }
            }
        }catch (Exception e){
        }
        return true;
    }
}
