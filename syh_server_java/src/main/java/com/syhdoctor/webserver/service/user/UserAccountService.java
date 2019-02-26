package com.syhdoctor.webserver.service.user;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.mapper.user.UserAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserAccountService extends UserBaseService {

    @Autowired
    private UserAccountMapper userAccountMapper;

    /**
     * 用户账户管理查询
     *
     * @param userno
     * @param name
     * @param phone
     * @param begintime
     * @param endtime
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getUserAccountList(String userno, String name, String phone, long begintime, long endtime, int pageIndex, int pageSize) {
        return userAccountMapper.getUserAccountList(userno, name, phone, begintime, endtime, pageIndex, pageSize);
    }

    /**
     * 行数
     *
     * @param userno
     * @param name
     * @param phone
     * @param begintime
     * @param endtime
     * @return
     */
    public long getUserAccountCount(String userno, String name, String phone, long begintime, long endtime) {
        return userAccountMapper.getUserAccountCount(userno, name, phone, begintime, endtime);
    }

    public Map<String, Object> getUserAccountId(long id) {
        return userAccountMapper.getUserAccountId(id);
    }


    public List<Map<String, Object>> getUserAccountListId(long userid, int moneyflag, int pageIndex, int pageSize) {
        return userAccountMapper.getUserAccountListId(userid, moneyflag, pageIndex, pageSize);
    }

    public long getUserAccountListCount(long id, int moneyflag) {
        return userAccountMapper.getUserAccountListCount(id, moneyflag);
    }

    /**
     * 导出
     *
     * @param userno
     * @param name
     * @param phone
     * @param begintime
     * @param endtime
     * @return
     */
    public List<Map<String, Object>> getUserAccountexportListAll(String userno, String name, String phone, long begintime, long endtime) {
        List<Map<String, Object>> list = userAccountMapper.getUserAccountexportListAll(userno, name, phone, begintime, endtime);
        Map<String, Object> map = new HashMap<>();
        map.put("id", "编号");
        map.put("userno", "会员号");
        map.put("name", "姓名");
        map.put("phone", "手机号");
        map.put("createtime", "注册时间");
        map.put("walletbalance", "钱包余额");
        map.put("integral", "积分");
        list.add(0, map);
        return list;
    }
}
