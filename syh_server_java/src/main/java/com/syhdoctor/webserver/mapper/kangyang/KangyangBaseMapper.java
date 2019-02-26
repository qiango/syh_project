package com.syhdoctor.webserver.mapper.kangyang;

import com.syhdoctor.common.utils.EnumUtils.RegisterChannelEnum;
import com.syhdoctor.common.utils.Pinyin4jUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class KangyangBaseMapper extends BaseMapper {

    public long importKangyangUser(String name, String headpic, String vip, long birlong, int age, int gender, String phone, String cardno, String areas, String address, String id) {
        String sql = " insert into user_account (id," +
                "                          account, " +
                "                          headpic, " +
                "                          name_pinyin, " +
                "                          phone, " +
                "                          create_time, " +
                "                          register_channel, " +
                "                          gender, " +
                "                          birthday, " +
                "                          areas, " +
                "                          name, " +
                "                          userno, " +
                "                          cardno, " +
                "                          address, " +
                "                          kangyang_vip_grade, " +
                "                          age, " +
                "                          kangyang_userid) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        long userId = getId("user_account");
        params.add(userId);
        params.add(phone);
        params.add(headpic);
        params.add(Pinyin4jUtil.getInstance().getFirstSpell(name));
        params.add(phone);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(RegisterChannelEnum.Kangyang.getCode());
        params.add(gender);
        params.add(birlong);
        params.add(areas);
        params.add(name);
        params.add(UnixUtil.addZeroForNum(userId, 1));
        params.add(cardno);
        params.add(address);
        params.add(vip);
        params.add(age);
        params.add(id);
        insert(sql, params);
        return userId;
    }

    public boolean updateKangyangUser(String name, String headpic, String vip, long birlong, int age, int gender, String cardno, String areas, String address, long id) {
        String sql = " update user_account set " +
                "                          headpic=?, " +
                "                          name_pinyin=?, " +
                "                          gender=?, " +
                "                          birthday=?, " +
                "                          areas=?, " +
                "                          name=?, " +
                "                          cardno=?, " +
                "                          address=?, " +
                "                          kangyang_vip_grade=?, " +
                "                          age=? " +
                " where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(headpic);
        params.add(Pinyin4jUtil.getInstance().getFirstSpell(name));
        params.add(gender);
        params.add(birlong);
        params.add(areas);
        params.add(name);
        params.add(cardno);
        params.add(address);
        params.add(vip);
        params.add(age);
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean importHospital(String name, String pid, String cid, String did, String address, int type, int level) {
        String sql = " insert into hospital(id, hospital_name, hospital_code, create_time, delflag, pid, " +
                "                     cid,did, address, hospitaltype, hospitallevel) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        long id = getId("hospital");
        params.add(id);
        params.add(name);
        params.add(id);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(0);
        params.add(pid);
        params.add(cid);
        params.add(did);
        params.add(address);
        params.add(type);
        params.add(level);
        return insert(sql, params) > 0;
    }

    public List<Map<String, Object>> getHospitalList() {
        String sql = " select id,hospital_name from hospital  ";
        return queryForList(sql);
    }

    public boolean updateHospital(String name, long id) {
        String sql = " update hospital set hospital_name_pinyin=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(id);
        return update(sql, params) > 0;
    }

    public Map<String, Object> getUser(String phone) {
        String sql = " select id,phone from user_account where phone=? and ifnull(delflag,0)=0 ";
        return queryForMap(sql, phone);
    }
}
