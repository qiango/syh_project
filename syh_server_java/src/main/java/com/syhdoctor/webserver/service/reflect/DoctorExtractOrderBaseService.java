package com.syhdoctor.webserver.service.reflect;

import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.reflect.DoctorExtractOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public abstract class DoctorExtractOrderBaseService extends BaseService {

    @Autowired
    private DoctorExtractOrderMapper doctorExtractOrderMapper;

    /**
     * 提现记录查询
     *
     * @param doctorid
     * @param docname
     * @param status
     * @param begintime
     * @param endtime
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getDoctorExtractOrderList(String dootel,long doctorid, String docname, int status, long begintime, long endtime, int pageIndex, int pageSize) {
        return doctorExtractOrderMapper.getDoctorExtractOrderList( dootel,doctorid, docname, status, begintime, endtime, pageIndex, pageSize);
    }

    /**
     * 行数
     *
     * @param doctorid
     * @param docname
     * @param status
     * @param begintime
     * @param endtime
     * @return
     */
    public long getDoctorExtractOrderCount(long doctorid, String docname, int status, long begintime, long endtime) {
        return doctorExtractOrderMapper.getDoctorExtractOrderCount(doctorid, docname, status, begintime, endtime);
    }


    /**
     * 通过审核
     *
     * @param id
     * @param status
     * @param createuser
     * @return
     */
    public boolean addExamine(long id, int status, long createuser, String failreason) {
        return doctorExtractOrderMapper.addExamine(id, status, createuser) && updateStatus(id, status, failreason);
    }

    /**
     * 修改审核状态、修改审核时间
     *
     * @param id
     * @param status
     * @return
     */
    public boolean updateStatus(long id, int status, String failreason) {
        return doctorExtractOrderMapper.updateStatus(id, status, failreason) && updateExamineTime(id);
    }

    /**
     * 修改审核时间
     *
     * @param id
     * @return
     */
    public boolean updateExamineTime(long id) {
        return doctorExtractOrderMapper.updateExamineTime(id);
    }

//    /**
//     * 修改打款时间
//     *
//     * @param id
//     * @return
//     */
//    public boolean updatePayTime(long id) {
//        return doctorExtractOrderMapper.updatePayTime(id);
//    }

    /**
     * 导出
     *
     * @param doctorid
     * @param docname
     * @param status
     * @param begintime
     * @param endtime
     * @return
     */
    public List<Map<String, Object>> getDoctorExtractOrderExportListAll(long doctorid, String docname, int status, long begintime, long endtime) {
        List<Map<String, Object>> list = doctorExtractOrderMapper.getDoctorExtractOrderExportListAll(doctorid, docname, status, begintime, endtime);
        Map<String, Object> map = new HashMap<>();
        map.put("id", "编号");
        map.put("orderno", "交易流水号");
        map.put("indoccode", "医生编号");
        map.put("number", "银行卡号");
        map.put("bankname", "开户银行");
        map.put("docname", "医生姓名");
        map.put("dootel", "医生手机号");
        map.put("examinetime", "提交时间");
        map.put("amountmoney", "提现金额");
        map.put("status", "状态");
        map.put("failreason", "备注");
        map.put("paytime", "完成时间");
        list.add(0, map);
        return list;
    }


}
