package com.syhdoctor.webserver.service.user;

import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.user.UserEvaluateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public abstract class UserEvaluateBaseService extends BaseService {

    @Autowired
    private UserEvaluateMapper userEvaluateMapper;

    public List<Map<String, Object>> getUserEvaluateList(long id, String username, String doctorname, long begintime, long endtime, int pageIndex, int pageSize) {
        return userEvaluateMapper.getUserEvaluateList(id, username, doctorname, begintime, endtime, pageIndex, pageSize);
    }

    public long getUserEvaluateListCount(long id, String username, String doctorname, long begintime, long endtime) {
        return userEvaluateMapper.getUserEvaluateListCount(id, username, doctorname, begintime, endtime);
    }

    public boolean delUserEvaluateList(long id) {
        return userEvaluateMapper.delUserEvaluateList(id);
    }

    public boolean delUpdateReason(long id, String delreason) {
        boolean boo = delUserEvaluateList(id) && delReason(id, delreason);
        return boo;
    }

    public boolean delReason(long id, String delreason) {
        return userEvaluateMapper.delReason(id, delreason);
    }


}
