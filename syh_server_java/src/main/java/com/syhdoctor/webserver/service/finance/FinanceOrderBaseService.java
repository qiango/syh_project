package com.syhdoctor.webserver.service.finance;

import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.finance.FinanceOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public abstract class FinanceOrderBaseService extends BaseService {

    @Autowired
    private FinanceOrderMapper financeOrderMapper;

    public List<Map<String, Object>> getFinanceOrderList(long doctorid, String docname, int status, long begintime, long endtime, int pageIndex, int pageSize) {
        return financeOrderMapper.getFinanceOrderList(doctorid, docname, status, begintime, endtime, pageIndex, pageSize);
    }

    public long getFinanceOrderListCount(long doctorid, String docname, int status, long begintime, long endtime) {
        return financeOrderMapper.getFinanceOrderListCount(doctorid, docname, status, begintime, endtime);
    }


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
    public List<Map<String, Object>> getFinanceOrderListAll(long doctorid, String docname, int status, long begintime, long endtime) {
        List<Map<String, Object>> list = financeOrderMapper.getFinanceOrderListAll(doctorid, docname, status, begintime, endtime);
        Map<String, Object> map = new HashMap<>();
        map.put("id", "编号");
        map.put("orderno", "交易流水号");
        map.put("doctorid", "医生编号");
        map.put("docname", "医生姓名");
        map.put("number", "银行卡号");
        map.put("bankname", "开户银行");
        map.put("dootel", "医生手机号");
        map.put("examinetime", "提交时间");
        map.put("amountmoney", "提现金额");
        map.put("status", "打款状态");
        map.put("failreason", "备注");
        map.put("paytime", "完成时间");
        list.add(0, map);
        return list;
    }
//    /**
//     * 修改打款时间
//     *
//     * @param id
//     * @return
//     */
//    public boolean updatePayTime(long id) {
//        return ;
//    }

    /**
     * 修改打款状态、修改打款时间
     *
     * @param id
     * @param status
     * @return
     */
    public boolean updateFundtype(long id, int status, String failreason) {
        return financeOrderMapper.updateFundtype(id, status, failreason) && financeOrderMapper.updatePayTime(id);
    }

    /**
     * 打款成功 日志
     *
     * @param id
     * @param status
     * @param createuser
     * @return
     */
    public boolean addRemittanceLog(long id, int status, long createuser, String failreason) {
        return financeOrderMapper.addRemittanceLog(id, status, createuser) && updateFundtype(id, status, failreason);
    }


}
