package com.syhdoctor.webserver.mapper.hospital;

import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import com.syhdoctor.webserver.utils.JiebaUtils;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2018/11/16
 */
public abstract class HospitalBaseMapper extends BaseMapper {

    public List<Map<String, Object>> findHospitalList(String name, long begintime, long endtime, int pageIndex, int pageSize) {
        String sql = " select hosp.id,hospital_name value,hospital_code code,hosp.create_time createtime,bs.name hospitaltype,ba.name hospitallevel from hospital hosp " +
                " LEFT JOIN basics bs on bs.customid=hosp.hospitaltype and ifnull(bs.delflag,0)=0 and bs.type=8 " +
                " LEFT JOIN basics ba on ba.customid=hosp.hospitallevel and ifnull(ba.delflag,0)=0 and ba.type=9 " +
                " where ifnull(hosp.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and (hospital_name like ? or hospital_name_pinyin like ?) ";
            params.add(String.format("%%%S%%", JiebaUtils.getName(name)));
            params.add(String.format("%S%%", name.toUpperCase()));
        }
        if (begintime != 0) {
            sql += " and hosp.create_time > ? ";
            params.add(begintime);
        }
        if (begintime != 0) {
            sql += " and hosp.create_time < ? ";
            params.add(endtime);
        }
        return query(pageSql(sql, " order by hosp.id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            return map;
        });
    }

    public long getHospitalCount(String name, long begintime, long endtime) {
        String sql = "select count(id) count from hospital where ifnull(delflag, 0) = 0";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and hospital_name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (begintime != 0) {
            sql += " and create_time >  ? ";
            params.add(begintime);
        }

        if (endtime != 0) {
            sql += " and create_time < ? ";
            params.add(endtime);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);

    }

    /**
     * 新增医院
     *
     * @param name
     * @param pid
     * @param cid
     * @param did
     * @param address
     * @param hospitaltype
     * @param hospitallevel
     * @param hospitalnamepinyin
     * @return
     */
    public boolean insertHospital(String name, long pid, long cid, long did, String address, int hospitaltype, int hospitallevel, String hospitalnamepinyin) {
        String sql = "insert into hospital (hospital_name,hospital_code,create_time,delflag,pid,cid,did,address,hospitaltype,hospitallevel,hospital_name_pinyin) values (?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(getId("hospital"));
        params.add(UnixUtil.getNowTimeStamp());
        params.add(0);
        params.add(pid);
        params.add(cid);
        params.add(did);
        params.add(address);
        params.add(hospitaltype);
        params.add(hospitallevel);
        params.add(hospitalnamepinyin);
        return insert(sql, params) > 0;
    }

    /**
     * 医院类型名字
     *
     * @return
     */
    public List<Map<String, Object>> getHospitalType() {
        String sql = " select customid id,name from basics where ifnull(delflag,0)=0 and type=8 ";
        return queryForList(sql);
    }

    public Map<String, Object> findHospitalByName(String name) {
        String sql = "select id from hospital where hospital_name=? and ifnull(delflag,0)=0";
        return queryForMap(sql, name);
    }

    /**
     * 医院详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> findHospitalById(long id) {
        String sql = "select id,hospital_name name,hospital_code code,address,hospitaltype,hospitallevel from hospital where id=?";
        return queryForMap(sql, id);
    }

    /**
     * 详情医院类型
     *
     * @param customid
     * @return
     */
    public Map<String, Object> getHospitalTypeId(long customid) {
        String sql = " select customid id,name from basics where ifnull(delflag,0)=0 and type=8 and customid=? ";
        return queryForMap(sql, customid);
    }

    /**
     * 详情医院等级
     *
     * @param customid
     * @return
     */
    public Map<String, Object> getHospitalLevelId(long customid) {
        String sql = " select customid id,name from basics where ifnull(delflag,0)=0 and type=9 and customid=? ";
        return queryForMap(sql, customid);
    }

    /**
     * 详情省市区
     *
     * @param id
     * @return
     */
    public Map<String, Object> getHospitalPcdId(long id) {
        String sql = " Select pid,cid,did from hospital where ifnull(delflag,0)=0 and id=? ";
        return queryForMap(sql, id);
    }


    /**
     * 修改医院
     *
     * @param id
     * @param name
     * @param pid
     * @param cid
     * @param did
     * @param address
     * @param hospitaltype
     * @param hospitallevel
     * @param hospitalnamepinyin
     * @return
     */
    public boolean updateHospital(long id, String name, long pid, long cid, long did, String address, int hospitaltype, int hospitallevel, String hospitalnamepinyin) {
        String sql = "update hospital set hospital_name=?,pid=?,cid=?,did=?,address=?,hospitaltype=?,hospitallevel=?,hospital_name_pinyin=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(pid);
        params.add(cid);
        params.add(did);
        params.add(address);
        params.add(hospitaltype);
        params.add(hospitallevel);
        params.add(hospitalnamepinyin);
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean deleteHospital(long id) {
        String sql = "update hospital set delflag=1 where id=?";
        return update(sql, id) > 0;
    }
}
