package com.syhdoctor.webserver.mapper.salesperson;

import com.syhdoctor.common.utils.EnumUtils.DoctorExamineEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SalespersonBaseMapper extends BaseMapper {


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
        String sql = " select id,invitation_code invitationcode,name,phone,salesman_code salesmancode,create_time createtime from salesperson where ifnull(delflag,0)=0  ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(invitationcode)) {
            sql += " and invitation_code like ? ";
            params.add(String.format("%%%S%%", invitationcode));
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and phone like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        return queryForList(pageSql(sql, " order by id desc "), pageParams(params, pageindex, pagesize));
    }

    public long getSalespersonListCount(String invitationcode, String name, String phone) {
        String sql = " select count(id) count from salesperson where ifnull(delflag,0)=0  ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(invitationcode)) {
            sql += " and invitation_code like ? ";
            params.add(String.format("%%%S%%", invitationcode));
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and phone like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 销售员详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getSalespersonId(long id) {
        String sql = " select id,invitation_code invitationcode,name,phone,salesman_code salesmancode,create_time createtime from salesperson where ifnull(delflag,0)=0 and id =? ";
        return queryForMap(sql, id);
    }

    /**
     * 删除销售员
     *
     * @param id
     * @return
     */
    public boolean delSalesperson(long id) {
        String sql = " update salesperson set delflag=1 where id =? ";
        return update(sql, id) > 0;
    }

    /**
     * 修改销售员
     *
     * @param id
     * @param name
     * @return
     */
    public boolean updateSalesperson(long id, String name, String phone, String salesmancode) {
        String sql = " update salesperson set name =?,phone=?,salesman_code=? where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(phone);
        params.add(salesmancode);
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 新增销售员
     *
     * @param invitationcode
     * @param name
     * @param phone
     * @param salesmancode
     * @return
     */
    public boolean insertSalesperson(String invitationcode, String name, String phone, String salesmancode) {
        String sql = " insert into salesperson(invitation_code,name,phone,salesman_code,create_time) values(?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(invitationcode);
        params.add(name);
        params.add(phone);
        params.add(salesmancode);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    /**
     * 邀请码是否存在
     *
     * @param invitationcode
     * @return
     */
    public long invitationCode(String invitationcode) {
        String sql = " select count(id) count from salesperson where ifnull(delflag,0)=0 and invitation_code =? ";
        List<Object> params = new ArrayList<>();
        params.add(invitationcode);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 销售员手机号是否存在
     *
     * @param phone
     * @return
     */
    public long phoneYesNo(String phone) {
        String sql = " select count(id) count from salesperson where ifnull(delflag,0)=0 and phone =? ";
        List<Object> params = new ArrayList<>();
        params.add(phone);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    /**
     * 邀请医生列表
     *
     * @param invitationcode   邀请码
     * @param salespersonname  销售员姓名
     * @param salespersonphone 销售员电话
     * @param doctorname       医生姓名
     * @param doctortel        医生电话
     * @param begintime
     * @param endtime
     * @param pageindex
     * @param pagesize
     * @return
     */
    public List<Map<String, Object>> getSalespersonDoctorList(String doccode, String invitationcode, String salespersonname, String salespersonphone, String doctorname, String doctortel, long begintime, long endtime, int pageindex, int pagesize) {
        String sql = " select mds.id, " +
                "       mds.invitation_code invitationcode, " +
                "       sal.name salespersonname, " +
                "       sal.phone salespersonphone, " +
                "       salespersonid, " +
                "       mds.doctorid, " +
                "       di.examine, " +
                "       di.in_doc_code doccode, " +
                "       di.doc_name doctorname, " +
                "       di.doo_tel doctortel, " +
                "       mds.create_time createtime" +
                " from middle_salesperson_doctor mds " +
                "       left join salesperson sal on sal.id = mds.salespersonid and ifnull(sal.delflag, 0) = 0 " +
                "       left join doctor_info di on di.doctorid = mds.doctorid and ifnull(di.delflag, 0) = 0 " +
                " where ifnull(mds.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(invitationcode)) {
            sql += " and mds.invitation_code like ? ";
            params.add(String.format("%%%S%%", invitationcode));
        }
        if (!StrUtil.isEmpty(doccode)) {
            sql += " and di.in_doc_code like ? ";
            params.add(String.format("%%%S%%", doccode));
        }
        if (!StrUtil.isEmpty(salespersonname)) {
            sql += " and sal.name like ? ";
            params.add(String.format("%%%S%%", salespersonname));
        }
        if (!StrUtil.isEmpty(salespersonphone)) {
            sql += " and sal.phone like ? ";
            params.add(String.format("%%%S%%", salespersonphone));
        }
        if (!StrUtil.isEmpty(doctorname)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%S%%", doctorname));
        }
        if (!StrUtil.isEmpty(doctortel)) {
            sql += " and di.doo_tel like ? ";
            params.add(String.format("%%%S%%", doctortel));
        }
        if (begintime != 0) {
            sql += " and mds.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and mds.create_time < ? ";
            params.add(endtime);
        }
        List<Map<String, Object>> list= queryForList(pageSql(sql, " order by mds.id desc "), pageParams(params, pageindex, pagesize));
        list.forEach(map -> {
            map.put("examine", DoctorExamineEnum.getValue(ModelUtil.getInt(map,"examine")).getMessage());
        });
        return list;
    }

    public long getSalespersonDoctorListCount(String doccode, String invitationcode, String salespersonname, String salespersonphone, String doctorname, String doctortel, long begintime, long endtime) {
        String sql = " select count(mds.id) count " +
                " from middle_salesperson_doctor mds " +
                "       left join salesperson sal on sal.id = mds.salespersonid and ifnull(sal.delflag, 0) = 0 " +
                "       left join doctor_info di on di.doctorid = mds.doctorid and ifnull(di.delflag, 0) = 0 " +
                " where ifnull(mds.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(invitationcode)) {
            sql += " and mds.invitation_code like ? ";
            params.add(String.format("%%%S%%", invitationcode));
        }
        if (!StrUtil.isEmpty(doccode)) {
            sql += " and di.in_doc_code like ? ";
            params.add(String.format("%%%S%%", doccode));
        }
        if (!StrUtil.isEmpty(salespersonname)) {
            sql += " and sal.name like ? ";
            params.add(String.format("%%%S%%", salespersonname));
        }
        if (!StrUtil.isEmpty(salespersonphone)) {
            sql += " and sal.phone like ? ";
            params.add(String.format("%%%S%%", salespersonphone));
        }
        if (!StrUtil.isEmpty(doctorname)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%S%%", doctorname));
        }
        if (!StrUtil.isEmpty(doctortel)) {
            sql += " and di.doo_tel like ? ";
            params.add(String.format("%%%S%%", doctortel));
        }
        if (begintime != 0) {
            sql += " and mds.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and mds.create_time < ? ";
            params.add(endtime);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    /**
     * 导出
     *
     * @param invitationcode   邀请码
     * @param salespersonname  销售员姓名
     * @param salespersonphone 销售员电话
     * @param doctorname       医生姓名
     * @param doctortel        医生电话
     * @param begintime
     * @param endtime
     * @return
     */
    public List<Map<String, Object>> getSalespersonDoctorListAll(String doccode, String invitationcode, String salespersonname, String salespersonphone, String doctorname, String doctortel, long begintime, long endtime) {
        String sql = " select mds.id, " +
                "       mds.invitation_code invitationcode, " +
                "       sal.name salespersonname, " +
                "       sal.phone salespersonphone, " +
//                "       salespersonid, " +
                "       di.in_doc_code doccode, " +
                "       di.doc_name doctorname, " +
                "       di.doo_tel doctortel, " +
                "       mds.create_time createtime" +
                " from middle_salesperson_doctor mds " +
                "       left join salesperson sal on sal.id = mds.salespersonid and ifnull(sal.delflag, 0) = 0 " +
                "       left join doctor_info di on di.doctorid = mds.doctorid and ifnull(di.delflag, 0) = 0 " +
                " where ifnull(mds.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(invitationcode)) {
            sql += " and mds.invitation_code like ? ";
            params.add(String.format("%%%S%%", invitationcode));
        }
        if (!StrUtil.isEmpty(doccode)) {
            sql += " and di.in_doc_code like ? ";
            params.add(String.format("%%%S%%", doccode));
        }
        if (!StrUtil.isEmpty(salespersonname)) {
            sql += " and sal.name like ? ";
            params.add(String.format("%%%S%%", salespersonname));
        }
        if (!StrUtil.isEmpty(salespersonphone)) {
            sql += " and sal.phone like ? ";
            params.add(String.format("%%%S%%", salespersonphone));
        }
        if (!StrUtil.isEmpty(doctorname)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%S%%", doctorname));
        }
        if (!StrUtil.isEmpty(doctortel)) {
            sql += " and di.doo_tel like ? ";
            params.add(String.format("%%%S%%", doctortel));
        }
        if (begintime != 0) {
            sql += " and mds.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and mds.create_time < ? ";
            params.add(endtime);
        }
        sql += " order by mds.id desc ";
        List<Map<String, Object>> list = queryForList(sql, params);
        for (Map<String, Object> map : list) {
            map.put("createtime", UnixUtil.getDate(ModelUtil.getLong(map, "createtime"), ""));
        }
        return list;
    }


}
