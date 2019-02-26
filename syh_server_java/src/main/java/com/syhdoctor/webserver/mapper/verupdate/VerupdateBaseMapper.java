package com.syhdoctor.webserver.mapper.verupdate;

import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class VerupdateBaseMapper extends BaseMapper {

    /**
     * 检查更新
     *
     * @param type   1: android 2:ios
     * @param system 1: 用户端 2:医生端
     * @return
     */
    public Map<String, Object> checkUpdate(int type, int system) {
        String sql = " select vernumber,url,identification from verupdate where type=? and system=? ";
        return queryForMap(sql, type, system);
    }

    public List<Map<String, Object>> verupdateList(IMapperResult callback) {
        String sql = " select id,vernumber,url,type,system,identification from verupdate ";
        return query(sql, new ArrayList<>(), (ResultSet res, int num) -> {
            Map<String, Object> map = resultToMap(res);
            if (callback != null) {
                callback.result(map);
            }
            return map;
        });
    }

    public boolean updateVerupdate(long id, String url, String vernumber, int identification, long agentid) {
        String sql = " update verupdate set vernumber=? ,url=?,modify_time=?,modify_user=?,identification=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(vernumber);
        params.add(url);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentid);
        params.add(identification);
        params.add(id);
        return update(sql, params) > 0;
    }

    public Map<String, Object> getVerupdate(long id) {
        String sql = " select id,vernumber,url,type,system,identification from verupdate where id=? ";
        return queryForMap(sql, id);
    }
}
