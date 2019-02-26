package com.syhdoctor.webserver.service.hospital;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.Pinyin4jUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.mapper.hospital.HospitalMapper;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2018/11/16
 */
public abstract class HospitalBaseService extends BaseService {

    @Autowired
    private HospitalMapper hospitalMapper;

    public List<Map<String, Object>> findHospitalList(String name, long begintime, long endtime, int pageIndex, int pageSize) {
        return hospitalMapper.findHospitalList(name, begintime, endtime, pageIndex, pageSize);
    }

    public long hospitalCount(String name, long begintime, long endtime) {
        return hospitalMapper.getHospitalCount(name, begintime, endtime);
    }

    public boolean insertHospital(String name, long id, List<?> pcdids, String address, int hospitaltype, int hospitallevel) {
        List<Long> pcdid = new ArrayList<>();
        if (pcdids.size() > 0) {
            for (Object ob : pcdids) {
                pcdid.add(Long.valueOf(String.valueOf(ob)));
            }
        }
        long pid = pcdid.get(0);
        long cid = pcdid.get(1);
        long did = pcdid.get(2);
        Pinyin4jUtil instance = Pinyin4jUtil.getInstance();
        String hospitalnamepinyin = instance.getFirstSpell(name).toUpperCase(); //获取首字母转为大写
        if (id != 0) {
            return updateHospital(id, name, pid, cid, did, address, hospitaltype, hospitallevel, hospitalnamepinyin);
        } else {
            if (null == findHosByname(name)) {
                return hospitalMapper.insertHospital(name, pid, cid, did, address, hospitaltype, hospitallevel, hospitalnamepinyin);
            } else {
                throw new ServiceException("该医院已存在，请检查");
            }
        }
    }

    /**
     * 医院类型
     *
     * @return
     */
    public List<Map<String, Object>> getHospitalType() {
        return hospitalMapper.getHospitalType();
    }


    public boolean updateHospital(long id, String name, long pid, long cid, long did, String address, int hospitaltype, int hospitallevel, String hospitalnamepinyin) {
        Map<String, Object> a = findHosByname(name);
        if (a == null || (a != null && ModelUtil.getLong(a, "id") == id)) {
            return hospitalMapper.updateHospital(id, name, pid, cid, did, address, hospitaltype, hospitallevel, hospitalnamepinyin);
        } else {
            throw new ServiceException("该医院已存在，请检查");
        }

    }

    public Map<String, Object> findHosByname(String name) {
        return hospitalMapper.findHospitalByName(name);
    }

    /**
     * 医院详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> findHospitalById(long id) {
        Map<String, Object> map = hospitalMapper.findHospitalById(id);
        int type = ModelUtil.getInt(map, "hospitaltype");
        int level = ModelUtil.getInt(map, "hospitallevel");
        Map<String, Object> pcdmap = hospitalMapper.getHospitalPcdId(id);
        List<String> pcdlist = new ArrayList<>();
        pcdlist.add(String.valueOf(ModelUtil.getInt(pcdmap, "pid")));
        pcdlist.add(String.valueOf(ModelUtil.getInt(pcdmap, "cid")));
        pcdlist.add(String.valueOf(ModelUtil.getInt(pcdmap, "did")));
        map.put("pcd", pcdlist);
        map.put("hospitaltype", hospitalMapper.getHospitalTypeId(type));
        map.put("hospitallevel", hospitalMapper.getHospitalLevelId(level));
        return map;
    }

    public boolean deleteHos(long id) {
        return hospitalMapper.deleteHospital(id);
    }

}
