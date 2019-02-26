package com.syhdoctor.webserver.mapper.wechat;

import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class AccessTokenMapper extends BaseMapper {


    public Map<String, Object> getAccesstoken() {
        String sql = "SELECT " +
                "  access_token, " +
                "  create_time createtime, " +
                "  expires_in " +
                "FROM accesstoken " +
                "WHERE 1=1 " +
                "ORDER BY create_time DESC";
        List<Object> params = new ArrayList<>();
        return queryForMap(sql, params);

    }

    public void updateAccesstoken(String access_token) {
        String sql = " UPDATE accesstoken SET access_token=?,create_time=?,expires_in=? ";
        List<Object> params = new ArrayList<>();
        params.add(access_token);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(7200);
        update(sql, params);
    }


    public void setAccesstoken(String access_token) {
        String sql = "INSERT INTO accesstoken(access_token, expires_in, create_time) VALUES (?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(access_token);
        params.add(7200);
        params.add(UnixUtil.getNowTimeStamp());
        update(sql, params);
    }
}
