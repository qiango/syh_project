package com.syhdoctor.webserver.service.salesperson;

import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.mapper.salesperson.SalespersonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalespersonBaseService extends BaseService {

    @Autowired
    private SalespersonMapper salespersonMapper;

    /**
     * 销售员列表
     *
     * @param invitationcode
     * @param name
     * @param phone
     * @param pageindex
     * @param pagesize
     * @return
     */
    public List<Map<String, Object>> getSalespersonList(String invitationcode, String name, String phone, int pageindex, int pagesize) {
        return salespersonMapper.getSalespersonList(invitationcode, name, phone, pageindex, pagesize);
    }

    public long getSalespersonListCount(String invitationcode, String name, String phone) {
        return salespersonMapper.getSalespersonListCount(invitationcode, name, phone);
    }

    /**
     * 销售员详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getSalespersonId(long id) {
        return salespersonMapper.getSalespersonId(id);
    }

    /**
     * 删除销售员
     *
     * @param id
     * @return
     */
    public boolean delSalesperson(long id) {
        return salespersonMapper.delSalesperson(id);
    }


    /**
     * 新增销售员
     *
     * @param id
     * @param name
     * @param phone
     * @param salesmancode
     * @return
     */
    public boolean updateAddSalesperson(long id, String name, String phone, String salesmancode) {
        boolean s = true;
        String invitationcode = "";
        if (id == 0) {
            long phonenum = salespersonMapper.phoneYesNo(phone);
            if (phonenum > 0) {
                throw new ServiceException("手机号已存在，请重新输入！");
            }
            long num = 0;
            do {
                invitationcode = UnixUtil.generateNumber(4);
                num = salespersonMapper.invitationCode(invitationcode);   //邀请码是否存在
            } while (num > 0);
            s = salespersonMapper.insertSalesperson(invitationcode, name, phone, salesmancode);
        } else {
            s = salespersonMapper.updateSalesperson(id, name, phone, salesmancode);
        }
        return s;
    }


    public List<Map<String, Object>> getSalespersonDoctorList(String doccode, String invitationcode, String salespersonname, String salespersonphone, String doctorname, String doctortel, long begintime, long endtime, int pageindex, int pagesize) {
        return salespersonMapper.getSalespersonDoctorList(doccode, invitationcode, salespersonname, salespersonphone, doctorname, doctortel, begintime, endtime, pageindex, pagesize);
    }

    public long getSalespersonDoctorListCount(String doccode, String invitationcode, String salespersonname, String salespersonphone, String doctorname, String doctortel, long begintime, long endtime) {
        return salespersonMapper.getSalespersonDoctorListCount(doccode, invitationcode, salespersonname, salespersonphone, doctorname, doctortel, begintime, endtime);
    }


    public List<Map<String, Object>> getSalespersonDoctorListAll(String doccode, String invitationcode, String salespersonname, String salespersonphone, String doctorname, String doctortel, long begintime, long endtime) {
        List<Map<String, Object>> list = salespersonMapper.getSalespersonDoctorListAll(doccode, invitationcode, salespersonname, salespersonphone, doctorname, doctortel, begintime, endtime);
        Map<String, Object> map = new HashMap<>();
        map.put("id", "编号");
        map.put("invitationcode", "邀请码");
        map.put("doccode", "医生编号");
        map.put("doctorname", "医生姓名");
        map.put("doctortel", "医生电话");
        map.put("salespersonname", "销售员姓名");
        map.put("salespersonphone", "销售员手机号");
        map.put("createtime", "邀请时间");
        list.add(0, map);
        return list;
    }


}
