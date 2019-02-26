package com.syhdoctor.webserver.service.doctor;

import com.syhdoctor.webserver.mapper.doctor.DoctorManageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DoctorManageService extends DoctorBaseService {

    @Autowired
    private DoctorManageMapper doctorManageMapper;

    /**
     * 医生账户管理查询
     *
     * @param indoccode
     * @param docname
     * @param dootel
     * @param begintime
     * @param endtime
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getDoctorManageList(String indoccode, String docname, String dootel, long begintime, long endtime, int pageIndex, int pageSize) {
        return doctorManageMapper.getDoctorManageList(indoccode, docname, dootel, begintime, endtime, pageIndex, pageSize);
    }


    /**
     * 行数
     *
     * @param indoccode
     * @param docname
     * @param dootel
     * @param begintime
     * @param endtime
     * @return
     */
    public long getDoctorManageCount(String indoccode, String docname, String dootel, long begintime, long endtime) {
        return doctorManageMapper.getDoctorManageCount(indoccode, docname, dootel, begintime, endtime);
    }


    public Map<String, Object> getDoctorinfoId(long doctorid) {
        return doctorManageMapper.getDoctorinfoId(doctorid);
    }


    public List<Map<String, Object>> getDoctorinfoListId(long doctorid, int moneyflag, int pageIndex, int pageSize) {
        return doctorManageMapper.getDoctorinfoListId(doctorid, moneyflag, pageIndex, pageSize);
    }

    public long getDoctorinfoListCount(long doctorid, int moneyflag) {

        return doctorManageMapper.getDoctorinfoListCount(doctorid, moneyflag);
    }


    /**
     * 导出
     *
     * @param indoccode
     * @param docname
     * @param dootel
     * @param begintime
     * @param endtime
     * @return
     */
    public List<Map<String, Object>> getDoctorExportListAll(String indoccode, String docname, String dootel, long begintime, long endtime) {
        List<Map<String, Object>> list = doctorManageMapper.getDoctorExportListAll(indoccode, docname, dootel, begintime, endtime);
        Map<String, Object> map = new HashMap<>();
        map.put("doctorid", "编号");
        map.put("indoccode", "会员号");
        map.put("docname", "姓名");
        map.put("dootel", "手机号");
        map.put("createtime", "注册时间");
        map.put("walletbalance", "积分");
        map.put("integral", "积分");
        list.add(0, map);
        return list;
    }

}
