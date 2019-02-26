package com.syhdoctor.webtask.vipcard.mapper;

import com.syhdoctor.webtask.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2018/11/12
 */
@Repository
public class VipCardMapper extends BaseMapper {

    public List<Map<String, Object>> getList() {
        String sql = "select id,vip_expiry_time from user_member where ifnull(delflag,0)=0 and is_expire=1 and is_enabled=1";
        return queryForList(sql);
    }

    public boolean updateListUser(long id,int expire){
        String sql="update user_member set is_expire=? where id=?";
        return update(sql,expire,id)>0;

    }

}
