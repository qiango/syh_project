package com.syhdoctor.webserver.mapper.doctor;

import com.syhdoctor.common.utils.EnumUtils.DoctorExamineEnum;
import com.syhdoctor.common.utils.EnumUtils.DoctorTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.VisitCategoryEnum;
import com.syhdoctor.common.utils.*;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.*;

public abstract class DoctorBaseMapper extends BaseMapper {


    /**
     * 急诊，门诊价格管理
     *
     * @return
     */
    public List<Map<String, Object>> getEmergencyClinicPriceList() {
        String sql = "select id,emergency_price as emergencyprice,outpatient_price as outpatientprice,create_time from doctor_emergency_clinic_price where ifnull(delflag,0)=0";
        List<Map<String, Object>> map = queryForList(sql);
        for (Map<String, Object> maps : map) {
            maps.put("emergencyprice", PriceUtil.findPrice(ModelUtil.getLong(maps, "emergencyprice")));
            maps.put("outpatientprice", PriceUtil.findPrice(ModelUtil.getLong(maps, "outpatientprice")));
        }
        return map;

    }

    /**
     * 急诊，门诊价格管理
     *
     * @return
     */
    public boolean updateEmergencyClinicPrice(long id, BigDecimal emergencyprice, BigDecimal outpatientprice, long userid) {
        String sql = "update doctor_emergency_clinic_price set emergency_price=?,outpatient_price=?,modify_time=?,modify_user=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(PriceUtil.addPrice(emergencyprice));
        params.add(PriceUtil.addPrice(outpatientprice));
        params.add(UnixUtil.getNowTimeStamp());
        params.add(userid);
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 医生排班审核
     *
     * @param examineState 0 待排班  1 审核中 2 确认值班 3 审核失败
     * @param id
     * @return
     */
    public boolean examineState(int examineState, int id) {
        String sql = "update doctor_onduty set examine_state=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(examineState);
        params.add(id);
        return update(sql, params) > 0;
    }


    /**
     * 医生排班
     *
     * @return
     */
    public List<Map<String, Object>> getSchedulingCalendar() {
        String sql = "select UNIX_TIMESTAMP(FROM_UNIXTIME(visiting_start_time/1000,'%Y-%m-%d')) as calendar " +
                "from doctor_onduty where ifnull(delflag,0)=0  group by UNIX_TIMESTAMP(FROM_UNIXTIME(visiting_start_time/1000,'%Y-%m-%d'))";
        return queryForList(sql);
    }

//    public List<Map<String, Object>> getSchedulingCalendar() {
//        String sql = "select UNIX_TIMESTAMP(FROM_UNIXTIME(visiting_start_time/1000,'%Y-%m-%d')) as calendar,doctorid,count(doctorid) as count " +
//                "from doctor_onduty where ifnull(delflag,0)=0  group by UNIX_TIMESTAMP(FROM_UNIXTIME(visiting_start_time/1000,'%Y-%m-%d')),doctorid";
//        return queryForList(sql);
//    }

    /**
     * 坐班医生排班
     *
     * @return
     */
    public List<Map<String, Object>> getSchedulingCalendars() {
        String sql = "select UNIX_TIMESTAMP(FROM_UNIXTIME(start_time/1000,'%Y-%m-%d')) as calendar " +
                "from doctor_sitting where ifnull(delflag,0)=0  group by UNIX_TIMESTAMP(FROM_UNIXTIME(start_time/1000,'%Y-%m-%d'))";
        return queryForList(sql);
    }

    /**
     * 医生值班信息
     *
     * @return
     */
    public List<Map<String, Object>> getDoctorSchedulingInfo(long time) {
        String sql = "select id,do.doctorid,d.doc_name as docname,visiting_start_time as starttime,visiting_end_time as endtime, " +
                "UNIX_TIMESTAMP(FROM_UNIXTIME(visiting_start_time/1000,'%Y-%m-%d')) as time,do.shift,do.examine_state as examinestate " +
                "from doctor_onduty do " +
                " left join doctor_info d on do.doctorid=d.doctorid " +
                "where ifnull(do.delflag,0)=0 and ifnull(d.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (time != 0) {
            sql += " and UNIX_TIMESTAMP(FROM_UNIXTIME(visiting_start_time/1000,'%Y-%m-%d'))=?";
            params.add(time);
        }
        return queryForList(sql, params);
    }

    /**
     * 坐班医生值班信息
     *
     * @return
     */
    public List<Map<String, Object>> getDoctorSchedulingInfos(long time) {
        String sql = "select id,do.doctorid,d.doc_name as docname,d.in_doc_code indoccode,start_time as starttime,end_time as endtime, do.create_time as createtime,d.doo_tel dootel," +
                "UNIX_TIMESTAMP(FROM_UNIXTIME(start_time/1000,'%Y-%m-%d')) as time,do.shift " +
                "from doctor_sitting do " +
                " left join doctor_info d on do.doctorid=d.doctorid " +
                "where ifnull(do.delflag,0)=0 and ifnull(d.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (time != 0) {
            sql += " and UNIX_TIMESTAMP(FROM_UNIXTIME(start_time/1000,'%Y-%m-%d'))=?";
            params.add(time);
        }
        return queryForList(sql, params);
    }


    /**
     * 添加医生价格
     *
     * @param doctorId
     * @param whetheropen
     * @param price
     * @param medclassId
     * @param medclassName
     */
    public void addDocMedPrice(long doctorId, int whetheropen, BigDecimal price, int medclassId, String medclassName) {
        String sql = "insert into doc_med_price_list(med_class_id, med_class_name, price, doctorid,whetheropen,create_time) " +
                " values (?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(medclassId);
        params.add(medclassName);
        params.add(PriceUtil.addPrice(price));
        params.add(doctorId);
        params.add(whetheropen);
        params.add(UnixUtil.getNowTimeStamp());
        update(sql, params);
    }

    /**
     * 修改医生价格
     *
     * @param id
     * @param open
     * @param price
     */
    public void UpdateDocMedPrice(int id, int open, BigDecimal price) {
        String sql = "update doc_med_price_list set price=?,whetheropen=?,modify_time=? where id= ? ";
        List<Object> params = new ArrayList<>();
        params.add(PriceUtil.addPrice(price));
        params.add(open);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(id);
        update(sql, params);
    }

    /**
     * 查询医生价格
     *
     * @param doctorId
     * @return
     */
    public List<Map<String, Object>> getMedPriceList(long doctorId) {
        String sql = "select id,med_class_id as medclassid, med_class_name as medclassname, price,whetheropen " +
                "from doc_med_price_list " +
                "where doctorid = ? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);

        return query(sql, params, (ResultSet rs, int rowNum) -> {
            Map<String, Object> data = resultToMap(rs);
            if (data != null) {
                data.put("price", PriceUtil.findPrice(ModelUtil.getLong(data, "price", 0)));
            }
            return data;
        });
    }

    /**
     * @param id
     * @return
     */
    public Map<String, Object> getDocMedPriceListById(int id) {
        String sql = "select id,med_class_id as medclassid, med_class_name as medclassname, price,ifnull(whetheropen,0) whetheropen" +
                "                from doc_med_price_list " +
                "                where id= ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        Map<String, Object> data = queryForMap(sql, params);
        if (data != null) {
            data.put("price", PriceUtil.findPrice(ModelUtil.getLong(data, "price", 0)));
        }
        return data;
    }

    /**
     * 医生多点执业备案
     *
     * @param doctorId
     * @return
     */
    public List<Map<String, Object>> getDocMultiSitedLicRecord(long doctorId) {
        String sql = "select id,doc_multi_sited_lic_record_url as url  from doc_multi_sited_lic_record_list where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForList(sql, params);
    }

    /**
     * 多点执业备案表
     *
     * @param docMultiSitedLicRecord    上传图片
     * @param docMultiSitedLicRecordUrl 本地存储地址
     * @param doctorId                  医生ID
     * @return
     */
    public boolean addMultiSitedLicRecord(String docMultiSitedLicRecord, String docMultiSitedLicRecordUrl, long doctorId) {
        String sql = "insert into doc_multi_sited_lic_record_list(doc_multi_sited_lic_record,doc_multi_sited_lic_record_url, doctorid,delflag,create_time) " +
                "values (?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(docMultiSitedLicRecord);
        params.add(docMultiSitedLicRecordUrl);
        params.add(doctorId);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return update(sql, params) > 0;
    }

    public boolean deleteMultiSitedLicRecord(long doctorId) {
        String sql = "delete from doc_multi_sited_lic_record_list where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return update(sql, params) > 0;
    }


    /**
     * 医生审核
     *
     * @param examine
     * @return
     */
    public boolean examineDoctor(long doctorId, int examine, long agentId, String reason, int doctype) {
        String sql = "update doctor_info set examine=?,reason=?,modify_time=?,authentication_time=?,modify_user=?,doc_type=? where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(examine);
        params.add(reason);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentId);
        params.add(doctype);
        params.add(doctorId);
        return update(sql, params) > 0;
    }

    /**
     * 删除医生
     *
     * @param doctorId
     * @param agentId
     * @return
     */
    public boolean deleteDoctor(long doctorId, long agentId) {
        String sql = " update doctor_info set delflag=1,modify_user=?,modify_time=? where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(agentId);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorId);
        return update(sql, params) > 0;
    }

    /**
     * 审方成功的行数
     *
     * @return
     */
    public long trialPartyOk() {
        String sql = " select count(doctorid) as docnum from doctor_info  " +
                " where IFNULL(delflag,0) = 0 and examine = 7 and doc_type = 1 ";
        return jdbcTemplate.queryForObject(sql, long.class);
    }


    /**
     * 审核成功的行数
     *
     * @return
     */
    public long examineOk() {
        String sql = " select count(doctorid) as docnum from doctor_info  " +
                " where IFNULL(delflag,0) = 0 and examine = 7 and doc_type = 5 ";
        return jdbcTemplate.queryForObject(sql, long.class);
    }


    /**
     * 查询医生列表
     *
     * @param docName
     * @param dooTel
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getDoctorList(String docName, String dooTel, String workInstName, String titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus, int pageIndex, int pageSize) {
        String sql = " select d.doctorid, " +
                "       doc_name                     as docname, " +
                "       reason,                                  " +
                "       doc_photo_url                as docphotourl, " +
                "       doc_type                     as doctype, " +
                "       title_id                     as titleid, " +
                "       cdt.value                    as titlename, " +
                "       work_inst_name               as workinstname, " +
                "       work_inst_code               as workinstcode, " +
                "       doo_tel                      as dootel, " +
                "       id_card                      as idcard, " +
                "       prac_no                      as pracno, " +
                "       prac_rec_date                as pracrecdate, " +
                "       prac_rec_date                as pracrecdate, " +
                "       cert_no                      as certno, " +
                "       cert_rec_date                as certrecdate, " +
                "       title_no                     as titleno, " +
                "       title_rec_date               as titlerecdate, " +
                "       prac_type                    as practype, " +
                "       qualify_or_not               as qualifyornot, " +
                "       professional, " +
                "       in_doc_code                     indoccode, " +
                "       sign_time                    as signtime, " +
                "       d.create_time                as createtime, " +
                "       sign_life                    as signlife, " +
                "       employ_file_url              as employfileurl, " +
                "       credit_level                 as creditlevel, " +
                "       occu_level                   as occulevel, " +
                "       digital_sign_url             as digitalsignurl, " +
                "       doc_penalty_points           as docpenaltypoints, " +
                "       yc_record_flag               as ycrecordflag, " +
                "       hos_confirm_flag             as hosconfirmflag, " +
                "       yc_pres_record_flag          as ycpresrecordflag, " +
                "       prac_scope                   as pracscope, " +
                "       prac_scope_approval          as pracscopeapproval, " +
                "       agree_terms                     agreeterms, " +
                "       doc_multi_sited_date_start   as docmultisiteddatestart, " +
                "       doc_multi_sited_date_end     as docmultisiteddateend, " +
                "       hos_opinion                  as hosopinion, " +
                "       hos_digital_sign             as hosdigitalsign, " +
                "       hos_opinion_date             as hosopiniondate, " +
                "       doc_multi_sited_date_promise as docmultisiteddatepromise, " +
                "       introduction, " +
                "       ifnull(gender, 0)               gender, " +
                "       department_id                as departmentid, " +
                "       cdp.value                    as departmentname, " +
                "       ifnull(d.examine, 0)         as examine, " +
                "       ifnull(dmplqa.whetheropen,0) as qaopen, " +
                "       ifnull(dmplphone.whetheropen,0) as phoneopen " +
                "from doctor_info d " +
                "       left join code_department cdp on d.department_id = cdp.id " +
                "       left join code_doctor_title cdt on d.title_id = cdt.id " +
                "       left join  doc_med_price_list dmplqa on dmplqa.doctorid=d.doctorid and dmplqa.med_class_id=2 " +
                "       left join  doc_med_price_list dmplphone on dmplphone.doctorid=d.doctorid and dmplphone.med_class_id=3 " +
                "where ifnull(d.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(docName)) {
            sql += " and d.doc_name like ?";
            params.add(String.format("%%%s%%", docName));
        }
        if (!StrUtil.isEmpty(dooTel)) {
            sql += " and d.doo_tel like ?";
            params.add(String.format("%%%s%%", dooTel));
        }
        if (!StrUtil.isEmpty(workInstName)) {
            sql += " and d.work_inst_name like ?";
            params.add(String.format("%%%s%%", workInstName));
        }
        if (!StrUtil.isEmpty(titleId)) {
            sql += " and  cdt.id = ?";
            params.add(String.format("%s", titleId));
        }
        if (!StrUtil.isEmpty(departmentId)) {
            sql += " and cdp.id = ?";
            params.add(String.format("%s", departmentId));
        }
        if (doctorStart != 0 && doctorEnd != 0) {
            sql += " and sign_time between ? and ?";
            params.add(String.format("%s", doctorStart));
            params.add(String.format("%s", doctorEnd));
        }
        if (examine != -1) {
            sql += " and d.examine=?";
            params.add(String.format("%s", examine));
        }
        if (graphicStatus != -1) {
            sql += " and ifnull(dmplqa.whetheropen,0) = ? ";
            params.add(String.format("%s", graphicStatus));
        }
//        return queryForList(pageSql(sql, " order by d.create_time desc "), pageParams(params, pageIndex, pageSize));
        return query(pageSql(sql, " order by d.create_time desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (null != map) {
                map.put("doctype", DoctorTypeEnum.getValue(ModelUtil.getInt(map, "doctype")).getMessage());
            }
            return map;
        });
    }

    public long getDoctorTotal(String docName, String dooTel, String workInstName, String titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus) {
        String sql = "select count(d.doctorid) as count " +
                "from doctor_info d " +
                "       left join code_department cdp on d.department_id = cdp.id " +
                "       left join code_doctor_title cdt on d.title_id = cdt.id " +
                "       left join  doc_med_price_list dmplqa on dmplqa.doctorid=d.doctorid and dmplqa.med_class_id=2 " +
                "       left join  doc_med_price_list dmplphone on dmplphone.doctorid=d.doctorid and dmplphone.med_class_id=3 " +
                "where ifnull(d.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(docName)) {
            sql += " and d.doc_name like ?";
            params.add(String.format("%%%s%%", docName));
        }
        if (!StrUtil.isEmpty(dooTel)) {
            sql += " and d.doo_tel like ?";
            params.add(String.format("%%%s%%", dooTel));
        }
        if (!StrUtil.isEmpty(workInstName)) {
            sql += " and d.work_inst_name like ?";
            params.add(String.format("%%%s%%", workInstName));
        }
        if (!StrUtil.isEmpty(titleId)) {
            sql += " and  cdt.id = ?";
            params.add(String.format("%%%s%%", titleId));
        }
        if (!StrUtil.isEmpty(departmentId)) {
            sql += " and cdp.id = ?";
            params.add(String.format("%s", departmentId));
        }
        if (doctorStart != 0 && doctorEnd != 0) {
            sql += " and sign_time between ? and ?";
            params.add(String.format("%s", doctorStart));
            params.add(String.format("%s", doctorEnd));
        }
        if (examine != -1) {
            sql += " and d.examine=?";
            params.add(String.format("%s", examine));
        }
        if (graphicStatus != -1) {
            sql += " and ifnull(dmplqa.whetheropen,0) = ? ";
            params.add(String.format("%s", graphicStatus));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), Long.class);
    }

    /**
     * 查询医生详细不要流
     *
     * @param doctorId
     * @return
     */
    public Map<String, Object> getDoctorNotBlobById(long doctorId) {
        String sql = "select d.doctorid, " +
                "       doc_name                     as docname, " +
                "       doc_photo_url                as docphotourl, " +
                "       doc_type                     as doctype, " +
                "       title_id                     as titleid, " +
                "       cdt.value                    as titlename, " +
                "       cdt.code                     as titlecode, " +
                "       work_inst_name               as workinstname, " +
                "       work_inst_code               as workinstcode, " +
                "       hospitalid, " +
                "       doo_tel                      as dootel, " +
                "       id_card                      as idcard, " +
                "       prac_no                      as pracno, " +
                "       prac_rec_date                as pracrecdate, " +
                //"       prac_rec_date                as pracrecdate, " +
                "       cert_no                      as certno, " +
                "       cert_rec_date                as certrecdate, " +
                "       title_no                     as titleno, " +
                "       title_rec_date               as titlerecdate, " +
                "       prac_type                    as practype, " +
                "       qualify_or_not               as qualifyornot, " +
                "       professional, " +
                "       in_doc_code                     indoccode, " +
                "       sign_time                    as signtime, " +
                "       sign_life                    as signlife, " +
                "       employ_file_url              as employfileurl, " +
                "       credit_level                 as creditlevel, " +
                "       occu_level                   as occulevel, " +
                "       digital_sign_url             as digitalsignurl, " +
                "       doc_penalty_points           as docpenaltypoints, " +
                "       yc_record_flag               as ycrecordflag, " +
                "       hos_confirm_flag             as hosconfirmflag, " +
                "       yc_pres_record_flag          as ycpresrecordflag, " +
                "       prac_scope                   as pracscope, " +
                "       prac_scope_approval          as pracscopeapproval, " +
                "       agree_terms                     agreeterms, " +
                "       doc_multi_sited_date_start   as docmultisiteddatestart, " +
                "       doc_multi_sited_date_end     as docmultisiteddateend, " +
                "       hos_opinion                  as hosopinion, " +
                "       hos_digital_sign             as hosdigitalsign, " +
                "       hos_opinion_date             as hosopiniondate, " +
                "       doc_multi_sited_date_promise as docmultisiteddatepromise, " +
                "       introduction, " +
                "       gender, " +
                "       department_id                as departmentid, " +
                "       examine_time                 as examinetime, " +
                "       examine, " +
                "       cdp.value                    as departmentname," +
                "       msd.invitation_code          as invitationcode  " +
                "from doctor_info d " +
                "       left join code_department cdp on d.department_id = cdp.id " +
                "       left join code_doctor_title cdt on d.title_id = cdt.id " +
                "       left join middle_salesperson_doctor msd on ifnull(msd.delflag,0)=0 and msd.doctorid=d.doctorid " +
                "where ifnull(d.delflag, 0) = 0 " +
                "  and d.doctorid = ? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForMap(sql, params);
    }


    public Map<String, Object> getCodeDepartmentfu(long id) {
        String sql = " SELECT value FROM code_department where id in (select parentid from code_department where id=? ) ";
        return queryForMap(sql, id);
    }

    /**
     * 查询专家医生详细
     *
     * @param doctorId
     * @return
     */

    public Map<String, Object> getDoctorexpertId(long doctorId) {
        String sql = "select d.doctorid, " +
//                "       doc_name                     as docname, " +
//                "       doc_photo_url                as docphotourl, " +
                "       doc_type                     as doctype, " +
//                "       title_id                     as titleid, " +
                "       cdt.value                    as titlename, " +
                "       cdt.code                     as titlecode, " +
                "       work_inst_name               as workinstname, " +
                "       work_inst_code               as workinstcode, " +
//                "       hospitalid, " +
//                "       doo_tel                      as dootel, " +
//                "       id_card                      as idcard, " +
//                "       prac_no                      as pracno, " +
                "       prac_rec_date                as pracrecdate, " +
                //"       prac_rec_date                as pracrecdate, " +
                "       cert_no                      as certno, " +
                "       cert_rec_date                as certrecdate, " +
                "       title_no                     as titleno, " +
                "       title_rec_date               as titlerecdate, " +
                "       prac_type                    as practype, " +
                "       qualify_or_not               as qualifyornot, " +
//                "       professional, " +
                "       in_doc_code                     indoccode, " +
                "       sign_time                    as signtime, " +
                "       sign_life                    as signlife, " +
                "       employ_file_url              as employfileurl, " +
                "       credit_level                 as creditlevel, " +
                "       occu_level                   as occulevel, " +
                "       digital_sign_url             as digitalsignurl, " +
                "       doc_penalty_points           as docpenaltypoints, " +
                "       yc_record_flag               as ycrecordflag, " +
                "       hos_confirm_flag             as hosconfirmflag, " +
                "       yc_pres_record_flag          as ycpresrecordflag, " +
                "       prac_scope                   as pracscope, " +
                "       prac_scope_approval          as pracscopeapproval, " +
                "       agree_terms                     agreeterms, " +
                "       doc_multi_sited_date_start   as docmultisiteddatestart, " +
                "       doc_multi_sited_date_end     as docmultisiteddateend, " +
                "       hos_opinion                  as hosopinion, " +
                "       hos_digital_sign             as hosdigitalsign, " +
                "       hos_opinion_date             as hosopiniondate, " +
                "       doc_multi_sited_date_promise as docmultisiteddatepromise, " +
                "       examine_time                 as examinetime, " +
                "       cdp.value                    as departmentname " +
                "from doctor_info d " +
                "       left join code_department cdp on d.department_id = cdp.id " +
                "       left join code_doctor_title cdt on d.title_id = cdt.id " +
                "where ifnull(d.delflag, 0) = 0 " +
                "  and d.doctorid = ? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForMap(sql, params);
    }


    public Map<String, Object> getDocDepartment(long titlleId) {
        String sql = "select id,value from code_department where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(titlleId);
        return queryForMap(sql, params);
    }

    public List<Long> getDocDepartmentTree(long departId) {
        List<Long> longs = new ArrayList<>();
        String sql = "select id,parentid from code_department where id=? and ifnull(delflag,0)=0";
        Map<String, Object> map = queryForMap(sql, departId);
        if (map != null) {
            longs.add(ModelUtil.getLong(map, "id"));
            Map<String, Object> maps = queryForMap(sql, ModelUtil.getLong(map, "parentid"));
            if (maps != null) {
                longs.add(ModelUtil.getLong(maps, "id"));
                Map<String, Object> mapss = queryForMap(sql, ModelUtil.getLong(maps, "parentid"));
                if (mapss != null) {
                    longs.add(ModelUtil.getLong(mapss, "id"));
                }
            }
        }
        return longs;
    }


    public Map<String, Object> getCodeDoctorTitle(long titlleId) {
        String sql = "select id,value from code_doctor_title where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(titlleId);
        return queryForMap(sql, params);
    }

    /**
     * @param doctorType 2:审核医生 3：审方医生
     * @return
     */
    public Map<String, Object> getReviewTrialDoctor(int doctorType) {
        String sql = " select doctorid,in_doc_code indoccode,doc_name docname,digital_sign_url digitalsignurl from doctor_info where doc_type=? and ifnull(delflag,0)=0 and examine=7 order by RAND() LIMIT 1 ";
        return queryForMap(sql, doctorType);
    }

    /**
     * 获取医生签名
     *
     * @param doctorId
     * @return
     */
    public Map<String, Object> getDoctordSignUrl(long doctorId) {
        String sql = " select digital_sign_url digitalsignurl from doctor_info where doctorid=? ";
        return queryForMap(sql, doctorId);
    }

    /**
     * 查询医生详细
     *
     * @param doctorId
     * @return
     */
    public Map<String, Object> getDoctorById(long doctorId) {
        String sql = "select doctorid,  " +
                "       doc_name                     as docname," +
                "       doc_photo                    as docphoto,  " +
                "       doc_type                     as doctype,  " +
                "       title_id                     as titleid,  " +
                "       cdt.value                    as titlename,  " +
                "       cdt.code                     as titlecode,  " +
                "       work_inst_name               as workinstname,  " +
                "       work_inst_code               as workinstcode,  " +
                "       doo_tel                      as dootel,  " +
                "       id_card                      as idcard,  " +
                "       prac_no                      as pracno,  " +
                "       prac_rec_date                as pracrecdate,  " +
                "       prac_rec_date                as pracrecdate,  " +
                "       cert_no                      as certno,  " +
                "       cert_rec_date                as certrecdate,  " +
                "       title_no                     as titleno,  " +
                "       title_rec_date               as titlerecdate,  " +
                "       prac_type                    as practype,  " +
                "       qualify_or_not               as qualifyornot,  " +
                "       professional,  " +
                "       in_doc_code                     indoccode,  " +
                "       sign_time                    as signtime,  " +
                "       sign_life                    as signlife,  " +
                "       employ_file                  as employfile,  " +
                "       credit_level                 as creditlevel,  " +
                "       occu_level                   as occulevel,  " +
                "       digital_sign                 as digitalsign,  " +
                "       doc_penalty_points           as docpenaltypoints,  " +
                "       yc_record_flag               as ycrecordflag,  " +
                "       hos_confirm_flag             as hosconfirmflag,  " +
                "       yc_pres_record_flag          as ycpresrecordflag,  " +
                "       prac_scope                   as pracscope,  " +
                "       prac_scope_approval          as pracscopeapproval,  " +
                "       agree_terms                     agreeterms,  " +
                "       doc_multi_sited_date_start   as docmultisiteddatestart,  " +
                "       doc_multi_sited_date_end     as docmultisiteddateend,  " +
                "       hos_opinion                  as hosopinion,  " +
                "       hos_digital_sign             as hosdigitalsign,  " +
                "       hos_opinion_date             as hosopiniondate,  " +
                "       doc_multi_sited_date_promise as docmultisiteddatepromise,  " +
                "       introduction,  " +
                "       gender,  " +
                "       department_id                as department_id,  " +
                "       cdp.value                    as departmentname,  " +
                "       cdp.code                    as departmentcode,  " +
                "       digital_sign_url   as digitalsignurl  " +
                "from doctor_info d  " +
                "      left join code_department cdp on d.department_id = cdp.id  " +
                "      left  join code_doctor_title cdt on d.title_id = cdt.id  " +
                "where ifnull(d.delflag, 0) = 0  " +
                "  and d.doctorid = ? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForMap(sql, params);
    }

    public Map<String, Object> getDoctorTypes(long doctorId) {
        String sql = "select doctorid,  " +
                "       doc_type                     as doctype  " +
                "from doctor_info  " +
                "where ifnull(delflag, 0) = 0  " +
                "  and doctorid = ? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForMap(sql, params);
    }

    public Map<String, Object> getAnswerOrderDoctorId(long orderid) {
        String sql = "select doctorid from doctor_problem_order where ifnull(delflag,0)=0 and id=? ";
        return queryForMap(sql, orderid);
    }

    public Map<String, Object> getPhoneOrderDoctorId(long orderid) {
        String sql = "select doctorid from doctor_phone_order where ifnull(delflag,0)=0 and id=? ";
        return queryForMap(sql, orderid);
    }

    public Map<String, Object> getVideoOrderDoctorId(long orderid) {
        String sql = "select doctorid from doctor_video_order where ifnull(delflag,0)=0 and id=? ";
        return queryForMap(sql, orderid);
    }


    public Map<String, Object> getPid(long doctorid) {
        String sql = "select pid from doctor_extends where doctorid=? and ifnull(delflag,0)=0";
        return queryForMap(sql, doctorid);
    }

    public List<Map<String, Object>> getNoBlobDocIdCardList(long doctorId) {
        String sql = "select id,url from doc_id_card_list where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> getNoBlobDocCertDocPracList(long doctorId) {
        String sql = "select id,url  from doc_cert_doc_prac_list where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> getNoBlobDocCertList(long doctorId) {
        String sql = "select id,url from  doc_cert_list where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> getNoBlobDocTitleCertList(long doctorId) {
        String sql = "select id,url from  doc_title_cert_list where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForList(sql, params);
    }

    /**
     * 多文件
     *
     * @param doctorId 医生ID
     * @return
     */
    public List<Map<String, Object>> getNoBlobMultiSitedLicRecordList(long doctorId) {
        String sql = "select id,doc_multi_sited_lic_record_url as  url from doc_multi_sited_lic_record_list where doctorid= ? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForList(sql, params);
    }

    public boolean updateDoctorImageBackup(long doctorId, String digitalSign, String employfile, String docPhoto, String digitalsignurlthumbnail) {
        String sql = "update doctor_info set digital_sign=?,employ_file=?,doc_photo=?,digital_sign_url_thumbnail=? where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(digitalSign);
        params.add(employfile);
        params.add(docPhoto);
        params.add(digitalsignurlthumbnail);
        params.add(doctorId);
        return update(sql, params) > 0;

    }

    public boolean updateDoctorCard(int id, String idCard) {
        String sql = "update doc_id_card_list set id_card=? where id= ? ";
        List<Object> params = new ArrayList<>();
        params.add(idCard);
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean updateDocIdCard(int id, String certDocPrac) {
        String sql = "update doc_cert_doc_prac_list set cert_doc_prac=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(certDocPrac);
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean updateTitleCert(int id, String titleCert) {
        String sql = " update doc_title_cert_list set title_cert=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(titleCert);
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean updateDocCert(int id, String docCert) {
        String sql = " update doc_cert_list set doc_cert=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(docCert);
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean updateDocMultiSitedLicRecord(int id, String docMultiSitedLicRecord) {
        String sql = " update doc_multi_sited_lic_record_list set doc_multi_sited_lic_record=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(docMultiSitedLicRecord);
        params.add(id);
        return update(sql, params) > 0;
    }


    public List<Map<String, Object>> getDocIdCardList(long doctorId) {
        String sql = "select id,url from doc_id_card_list where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> getDocCertDocPracList(long doctorId) {
        String sql = "select id,url from doc_cert_doc_prac_list where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForList(sql, params);
    }


    public List<Map<String, Object>> getDocCertList(long doctorId) {
        String sql = "select id,url from  doc_cert_list where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> getDocTitleCertList(long doctorId) {
        String sql = "select id,url from  doc_title_cert_list where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForList(sql, params);
    }


    public void deleteDocCard(Long doctorId) {
        String sql = "delete from doc_id_card_list where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        update(sql, params);
    }

    public void deleteDocCertList(long doctorId) {
        String sql = "delete from doc_cert_list where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        update(sql, params);
    }

    public void deleteCertDocPracList(long doctorId) {
        String sql = "delete from doc_cert_doc_prac_list where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        update(sql, params);
    }

    public void deleteDocTitleCertList(long doctorId) {
        String sql = "delete from doc_title_cert_list where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        update(sql, params);
    }

    /**
     * 保存身份证文件
     *
     * @param cardId
     * @param doctorid
     * @param agentId
     */
    public void addDocCardList(String cardId, String url, long doctorid, long agentId) {
        String sql = "insert into doc_id_card_list(id_card,url,doctorid, delflag, create_time, create_user)  " +
                "values (?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(cardId);
        params.add(url);
        params.add(doctorid);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentId);
        update(sql, params);
    }


    /**
     * 保存资格证文件
     *
     * @param docCert
     * @param doctorid
     * @param agentId
     */
    public void addDocCertList(String docCert, String url, long doctorid, long agentId) {
        String sql = "insert into doc_cert_list(doc_cert,url, doctorid, create_time, create_user)  " +
                "values (?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(docCert);
        params.add(url);
        params.add(doctorid);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentId);
        update(sql, params);
    }

    /**
     * 保存职称文件
     *
     * @param titleCert
     * @param doctorid
     * @param agentId
     */
    public void addTitleCertList(String titleCert, String url, long doctorid, long agentId) {
        String sql = "insert into doc_title_cert_list(title_cert,url, doctorid, create_time, create_user)  " +
                "values (?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(titleCert);
        params.add(url);
        params.add(doctorid);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentId);
        update(sql, params);
    }

    /**
     * 保存执业证文件
     *
     * @param certDocPrac
     * @param doctorid
     * @param agentId
     */
    public void addCertDocPracList(String certDocPrac, String url, long doctorid, long agentId) {
        String sql = "insert into doc_cert_doc_prac_list (cert_doc_prac,url, doctorid, delflag, create_time, create_user)  " +
                "values (?, ?, ?, ?, ?, ?)";
        List<Object> params = new ArrayList<>();
        params.add(certDocPrac);
        params.add(url);
        params.add(doctorid);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentId);
        update(sql, params);
    }


    /**
     * 查询科室
     *
     * @return
     */
    public List<Map<String, Object>> getDepartment(String value) {
        String sql = "select id,value from code_department where delflag=0";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(value)) {
            sql += "  and value like ?";
            params.add(String.format("%%%s%%", value));
        }
        return queryForList(sql, params);
    }

    /**
     * 查询科室最后一级
     *
     * @return
     */
    public List<Map<String, Object>> getLastDepartment(String value) {
        String sql = "select * " +
                "from (select case when ifnull(cd1.id, 0) = 0 then cd.id else cd1.id end       id, " +
                "             case when ifnull(cd1.id, 0) = 0 then cd.value else cd1.value end value " +
                "      from code_department cd " +
                "             left join code_department cd1 on cd.id = cd1.parentid and ifnull(cd1.delflag, 0) = 0 " +
                "      where ifnull(cd.delflag, 0) = 0 and ifnull(cd.parentid,0)=0 " +
                "      order by cd.id) m " +
                "where 1=1  ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(value)) {
            sql += "  and value like ?";
            params.add(String.format("%%%s%%", value));
        }
        sql += "order by id ";
        return queryForList(sql, params);
    }

    //父级
    public List<Map<String, Object>> getDepartmentOne() {
        String sql = "select id value,value label from code_department where ifnull(delflag, 0) = 0 and parentid=0";
        return queryForList(sql);
    }

    //子级
    public List<Map<String, Object>> getDepartmentTwo(long parentid) {
        String sql = "select id value,value label from code_department where ifnull(delflag, 0) = 0 and parentid=?";
        return queryForList(sql, parentid);
    }

    public List<Map<String, Object>> getAdminDepartmen(String name) {
        String sql = "select id value,value label from code_department where ifnull(delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += "  and value like ?";
            params.add(String.format("%%%s%%", name));
        }
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> getAppDepartmen(String name) {
        String sql = "select id ,value from code_department where ifnull(delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += "  and value like ?";
            params.add(String.format("%%%s%%", name));
        }
        return queryForList(sql, params);
    }


    public Map<String, Object> getDepartmens(long id) {
        String sql = "select id value,value label from code_department where ifnull(delflag, 0) = 0 and id=?";
        return queryForMap(sql, id);
    }

    /**
     * 查询科室最后一级
     *
     * @return
     */
    public List<Map<String, Object>> getLastDepartments(String value) {
        String sql = "select id,value as name " +
                "from (select case when ifnull(cd1.id, 0) = 0 then cd.id else cd1.id end       id, " +
                "             case when ifnull(cd1.id, 0) = 0 then cd.value else cd1.value end value " +
                "      from code_department cd " +
                "             left join code_department cd1 on cd.id = cd1.parentid and ifnull(cd1.delflag, 0) = 0 " +
                "      where ifnull(cd.delflag, 0) = 0 and ifnull(cd.parentid,0)=0 " +
                "      order by cd.id) m " +
                "where 1=1  ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(value)) {
            sql += "  and value like ?";
            params.add(String.format("%%%s%%", value));
        }
        sql += "order by id ";
        return queryForList(sql, params);
    }

    /**
     * 查询职称
     *
     * @return
     */
    public List<Map<String, Object>> getDoctorTitle(String value) {
        String sql = "select id,value from code_doctor_title where delflag=0";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(value)) {
            sql += "  and value like ?";
            params.add(String.format("%%%s%%", value));
        }
        sql += " order by sort desc ";
        return queryForList(sql, params);
    }

    /**
     * 获取医生code
     *
     * @return
     */
    public Map<String, Object> getDoctorCode() {
        Map<String, Object> data = new HashMap<>();
        long doctorId = getId("doctor_info");
        String inDocCode = UnixUtil.addZeroForNum(doctorId, 2);
        data.put("doctorId", doctorId);
        data.put("inDocCode", inDocCode);
        return data;
    }


    /**
     * @param docName                  医生姓名
     * @param docPhoto                 医生头像
     * @param docType                  医生类型
     * @param titleId                  职称中文名
     * @param workInstCode             线下医院code
     * @param workInstName             线下医院名称
     * @param idCard                   医生身份证号
     * @param pracNo                   医师执业号
     * @param pracRecDate              执业证取得时间（YYYY-MM-DD）
     * @param certNo                   医师资格证号
     * @param certRecDate              资格证取得时间
     * @param titleNo                  医师职称号
     * @param titleRecDate             职称证取得时间
     * @param pracType                 医师执业类别
     * @param qualifyOrNot             考核是否合格 是 | 否
     * @param professional             医师擅长专业
     * @param signTime                 签约时间
     * @param signLife                 签约年限
     * @param employFile               聘任合同
     * @param creditLevel              信用评级
     * @param occuLevel                职业评级
     * @param digitalSign              数字签名留样
     * @param docPenaltyPoints         医师评分
     *                                 //     * @param ycRecordFlag             银川是否备案
     *                                 //     * @param hosConfirmFlag           医院是否备案
     *                                 //     * @param ycPresRecordFlag         是否有开处方的权限
     * @param pracScope                医师执业范围
     * @param pracScopeApproval        审批局规定的医师执业范围
     * @param agreeTerms               医师是否同意多点执业备案信息表上的条款，同意填“是”，不同意填“否” 0 是 1 否
     * @param docMultiSitedDateStart   医师多点执业起始时间
     * @param docMultiSitedDateEnd     医师多点执业起始时间
     * @param hosOpinion               申请拟执业医疗机构意见
     *                                 //     * @param hosDigitalSign           申请拟执业医疗机构-电子章
     * @param hosOpinionDate           申请拟执业医疗机构意见时间
     * @param docMultiSitedDatePromise 医师申请多点执业承诺时间
     * @param inDocCode                医师CODE
     * @param tempDoctorId             医师ID
     * @return
     */
    public long addDoctorInfo(String docName, String docPhoto, String docPhoneUrl, int docType, int titleId, String workInstCode,
                              String workInstName, long hospitalid, String dooTel, String idCard, String pracNo, long pracRecDate, String certNo, long certRecDate, String titleNo,
                              long titleRecDate, String pracType, String qualifyOrNot, String professional,
                              long signTime, String signLife, String employFile, String employFileUrl, String creditLevel, String occuLevel,
                              String digitalSign, String digitalSignUrl, String docPenaltyPoints, String pracScope, String pracScopeApproval, int agreeTerms,
                              long docMultiSitedDateStart, long docMultiSitedDateEnd, String hosOpinion,
                              long hosOpinionDate, long docMultiSitedDatePromise, long agentid,
                              String introduction, int gender, int departmentId, String inDocCode, long tempDoctorId) {

        String sql = "insert into doctor_info (doctorid,  " +
                "                         doc_name,  " +
                "                         doc_photo,  " +
                "                         doc_photo_url, " +
                "                         doc_type,  " +
                "                         title_id,  " +
                "                         work_inst_code,  " +
                "                         work_inst_name,  " +
                "                         hospitalid,  " +
                "                         doo_tel,  " +
                "                         id_card,  " +

                "                         prac_no,  " +
                "                         prac_rec_date,  " +
                "                         cert_no,  " +
                "                         cert_rec_date,  " +
                "                         title_no,  " +
                "                         title_rec_date,  " +
                "                         prac_type,  " +
                "                         qualify_or_not,  " +
                "                         professional,  " +
                "                         in_doc_code,  " +

                "                         sign_time,  " +
                "                         sign_life,  " +
                "                         employ_file,  " +
                "                         employ_file_url,  " +
                "                         credit_level,  " +
                "                         occu_level,  " +
                "                         digital_sign,  " +
                "                         digital_sign_url,  " +
                "                         doc_penalty_points,  " +
//                "                         yc_record_flag,  " +
//
//                "                         hos_confirm_flag,  " +
//                "                         yc_pres_record_flag,  " +
                "                         prac_scope,  " +
                "                         prac_scope_approval,  " +
                "                         agree_terms,  " +
                "                         doc_multi_sited_date_start,  " +
                "                         doc_multi_sited_date_end,  " +
                "                         hos_opinion,  " +
//                "                         hos_digital_sign,  " +
                "                         hos_opinion_date,  " +

                "                         doc_multi_sited_date_promise,  " +
                "                         delflag,  " +
                "                         create_time,  " +
                "                         create_user,  " +
                "                         introduction,  " +
                "                         gender,  " +
                "                         department_id," +
                "                         examine )  " +
                "values (?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?, ?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(tempDoctorId);
        params.add(docName);
        params.add(docPhoto);
        params.add(docPhoneUrl);
        params.add(3);
        params.add(titleId);
        params.add(workInstCode);
        params.add(workInstName);
        params.add(hospitalid);
        params.add(dooTel);
        params.add(idCard);

        params.add(pracNo);
        params.add(pracRecDate);
        params.add(certNo);
        params.add(certRecDate);
        params.add(titleNo);
        params.add(titleRecDate);
        params.add(pracType);
        params.add(qualifyOrNot);
        params.add(professional);
        params.add(inDocCode);

        params.add(signTime);
        params.add(signLife);
        params.add(employFile);
        params.add(employFileUrl);
        params.add(creditLevel);
        params.add(occuLevel);
        params.add(digitalSign);
        params.add(digitalSignUrl);
        params.add(docPenaltyPoints);
//        params.add(ycRecordFlag);
//
//        params.add(hosConfirmFlag);
//        params.add(ycPresRecordFlag);
        params.add(pracScope);
        params.add(pracScopeApproval);
        params.add(agreeTerms);
        params.add(docMultiSitedDateStart);
        params.add(docMultiSitedDateEnd);
        params.add(hosOpinion);
//        params.add(hosDigitalSign);
        params.add(hosOpinionDate);

        params.add(docMultiSitedDatePromise);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentid);
        params.add(introduction);
        params.add(gender);
        params.add(departmentId);
        insert(sql, params);
        return tempDoctorId;
    }


    /**
     * 完善医生信息
     *
     * @param doctorId 医生ID
     * @param docPhoto 医生头像
     * @return
     */
    public boolean updateDoctorInfoNew(long doctorId, String docPhoto) {
        String sql = "update doctor_info  set" +
                "    doc_photo       = ?,  " +
                "    credit_level=?, " +
                "    occu_level=?, " +
                "    hos_opinion=?, " +
                "    qualify_or_not=?, " +
                "    doc_penalty_points=? ," +
                "    hos_digital_sign=?, " +
                "    modify_time=?  " +
                " where doctorid = ?";
        List<Object> params = new ArrayList<>();
        params.add(docPhoto);
        params.add("优秀");
        params.add("1级");
        params.add("同意");
        params.add("合格");
        params.add("100");
        params.add("file-static-hos_digital_sign.png");
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorId);
        return update(sql, params) > 0;
    }

    public boolean updateDoctorInfoNewSign(long doctorId, String digitalSignUrl, String digital_sign) {
        String sql = "update doctor_info  set" +
                "    digital_sign    = ?,  " +
                "    digital_sign_url= ?, " +
                "    examine    =?, " +
                "    modify_time=?  " +
                " where doctorid = ?";
        List<Object> params = new ArrayList<>();
        params.add(digital_sign);
        params.add(digitalSignUrl);
        params.add(DoctorExamineEnum.Certification.getCode());
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorId);
        return update(sql, params) > 0;
    }

    public boolean updateDoctorInfo(long doctorId, String docName, String docPhoto, String docPhotoUrl, String idCard, int gender,
                                    String workInstName, int departmentId, int titleId,
                                    String pracNo, String digitalSign, String digitalSignUrl, String professional, String introduction, int agreeTerms) {
        String sql = "update doctor_info  " +
                "set doc_name        = ?,  " +
                "    doc_photo       = ?,  " +
                "    doc_photo_url   = ?,   " +
                "    id_card         = ?,  " +
                "    gender          = ?,  " +
                "    work_inst_name  = ?,  " +
                "    department_id = ?,  " +
                "    title_id      = ?,  " +
                "    prac_no         = ?,  " +
                "    digital_sign    = ?,  " +
                "    digital_sign_url= ?, " +
                "    professional    = ?,  " +
                "    introduction    = ?,  " +
                "    agree_terms    = ?,  " +
                "    examine    = 1, " +
                "    credit_level=?, " +
                "    occu_level=?, " +
                "    doc_penalty_points=? ," +
                "    doc_type=?, " +
                "    hos_digital_sign=? " +
                " where doctorid = ?";
        List<Object> params = new ArrayList<>();
        params.add(docName);
        params.add(docPhoto);
        params.add(docPhotoUrl);
        params.add(idCard);
        params.add(gender);
        params.add(workInstName);
        params.add(departmentId);
        params.add(titleId);
        params.add(pracNo);
        params.add(digitalSign);
        params.add(digitalSignUrl);
        params.add(professional);
        params.add(introduction);
        params.add(agreeTerms);
        params.add("优秀");
        params.add("1级");
        params.add("0");
        params.add(2);
        params.add("file-static-hos_digital_sign.png");
        params.add(doctorId);
        return update(sql, params) > 0;
    }


    /**
     * 完善医生认证信息
     *
     * @param docPhoto                 医生头像
     * @param pracRecDate              执业证取得时间（YYYY-MM-DD）
     * @param certNo                   医师资格证号
     * @param certRecDate              资格证取得时间
     * @param titleNo                  医师职称号
     * @param titleRecDate             职称证取得时间
     * @param pracType                 医师执业类别
     * @param signTime                 签约时间
     * @param signLife                 签约年限
     * @param employFile               聘任合同
     * @param digitalSign              数字签名留样
     *                                 //     * @param ycRecordFlag             银川是否备案
     *                                 //     * @param hosConfirmFlag           医院是否备案
     *                                 //     * @param ycPresRecordFlag         是否有开处方的权限
     * @param pracScope                医师执业范围
     * @param pracScopeApproval        审批局规定的医师执业范围
     * @param docMultiSitedDateStart   医师多点执业起始时间
     * @param docMultiSitedDateEnd     医师多点执业起始时间
     *                                 //     * @param hosDigitalSign           申请拟执业医疗机构-电子章
     * @param docMultiSitedDatePromise 医师申请多点执业承诺时间
     * @param doctorId
     * @return
     */
    public boolean updateDoctorInfos(String pracNo, int examine, String docPhoto, int docTypeid, long hospitailid,
                                     long pracRecDate, String certNo,
                                     long certRecDate, String titleNo,
                                     long titleRecDate, String pracType,
                                     long signTime, String signLife, String employFile, String employFileUrl,
                                     String digitalSign, String digitalSignUrl, String pracScope, String pracScopeApproval,
                                     long docMultiSitedDateStart, long docMultiSitedDateEnd,
                                     long docMultiSitedDatePromise, long agentid,
                                     long doctorId) {

        String sql = "update doctor_info set " +
                "    doc_photo                    = ?,  " +
                "    doc_type                     = ?,  " +
                "    work_inst_code               = ?,  " +
                "    work_inst_name               = ?,  " +
                "    prac_no               = ?,  " +

                "    prac_rec_date                = ?,  " +
                "    cert_no                      = ?,  " +
                "    cert_rec_date                = ?,  " +
                "    title_no                     = ?,  " +
                "    title_rec_date               = ?,  " +
                "    prac_type                    = ?,  " +
                "    qualify_or_not               = ?,  " +
                "    sign_time                    = ?,  " +

                "    sign_life                    = ?,  " +
                "    employ_file                  = ?,  " +
                "    employ_file_url              = ?,   " +
                "    credit_level                 = ?,  " +
                "    occu_level                   = ?,  " +
                "    digital_sign                 = ?,  " +
                "    digital_sign_url             = ?,  " +
                "    doc_penalty_points           = ?,  " +
                "    prac_scope                   = ?,  " +
                "    prac_scope_approval          = ?,  " +
                "    agree_terms                  = ?,  " +
                "    doc_multi_sited_date_start   = ?,  " +
                "    doc_multi_sited_date_end     = ?,  " +
                "    hos_opinion                  = ?,  " +
//                "    hos_digital_sign             = ?,  " +
                "    hos_opinion_date             = ?,  " +
                "    doc_multi_sited_date_promise = ?,  " +

                "    delflag                      = ?,  " +
                "    modify_time                  = ?,  " +
                "    examine                  = ?,  " +
                "    modify_user                  = ?,  " +
                "   hos_digital_sign=?" +
                "where doctorid = ?";

        long hosOpinionDate = docMultiSitedDateStart;
        Map<String, Object> map = findHospital(hospitailid);

        List<Object> params = new ArrayList<>();
        params.add(docPhoto);
        params.add(docTypeid);
        params.add(ModelUtil.getStr(map, "hospital_code"));
        params.add(ModelUtil.getStr(map, "hospital_name"));
        params.add(pracNo);

        params.add(pracRecDate);
        params.add(certNo);
        params.add(certRecDate);
        params.add(titleNo);
        params.add(titleRecDate);
        params.add(pracType);
        params.add("1");
        params.add(signTime);

        params.add(signLife);
        params.add(employFile);
        params.add(employFileUrl);
        params.add("优秀");
        params.add("1级");
        params.add(digitalSign);
        params.add(digitalSignUrl);
        params.add("100");
        params.add(pracScope);
        params.add(pracScopeApproval);
        params.add(1);
        params.add(docMultiSitedDateStart);
        params.add(docMultiSitedDateEnd);
        params.add("同意");
//        params.add(hosDigitalSign);
        params.add(hosOpinionDate);
        params.add(docMultiSitedDatePromise);

        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(examine);
        params.add(agentid);
        params.add("file-static-hos_digital_sign.png");
        params.add(doctorId);
        return update(sql, params) > 0;

    }


    /**
     * 完善医生信息(全部)
     *
     * @param docName                  医生姓名
     * @param docPhoto                 医生头像
     * @param titleId                  职称中文名
     * @param idCard                   医生身份证号
     * @param pracNo                   医师执业号
     * @param pracRecDate              执业证取得时间（YYYY-MM-DD）
     * @param certNo                   医师资格证号
     * @param certRecDate              资格证取得时间
     * @param titleNo                  医师职称号
     * @param titleRecDate             职称证取得时间
     * @param pracType                 医师执业类别
     * @param qualifyOrNot             考核是否合格 是 | 否
     * @param professional             医师擅长专业
     * @param signTime                 签约时间
     * @param signLife                 签约年限
     * @param employFile               聘任合同
     * @param creditLevel              信用评级
     * @param occuLevel                职业评级
     * @param digitalSign              数字签名留样
     * @param docPenaltyPoints         医师评分
     *                                 //     * @param ycRecordFlag             银川是否备案
     *                                 //     * @param hosConfirmFlag           医院是否备案
     *                                 //     * @param ycPresRecordFlag         是否有开处方的权限
     * @param pracScope                医师执业范围
     * @param pracScopeApproval        审批局规定的医师执业范围
     * @param agreeTerms               医师是否同意多点执业备案信息表上的条款，同意填“是”，不同意填“否” 0 是 1 否
     * @param docMultiSitedDateStart   医师多点执业起始时间
     * @param docMultiSitedDateEnd     医师多点执业起始时间
     * @param hosOpinion               申请拟执业医疗机构意见
     * @param hosDigitalSign           申请拟执业医疗机构-电子章
     * @param hosOpinionDate           申请拟执业医疗机构意见时间
     * @param docMultiSitedDatePromise 医师申请多点执业承诺时间
     * @param doctorId
     * @return
     */
    public boolean updateDoctorInfo(String docName, String docPhoto, String docPhotoUrl, int docTypeid, int titleId, long hospitailid,
                                    String dooTel, String idCard, String pracNo, long pracRecDate, String certNo,
                                    long certRecDate, String titleNo,
                                    long titleRecDate, String pracType, String qualifyOrNot, String professional,
                                    long signTime, String signLife, String employFile, String employFileUrl, String creditLevel, String occuLevel,
                                    String digitalSign, String digitalSignUrl, String docPenaltyPoints, String pracScope, String pracScopeApproval, int agreeTerms,
                                    long docMultiSitedDateStart, long docMultiSitedDateEnd, String hosOpinion,
                                    String hosDigitalSign, long hosOpinionDate, long docMultiSitedDatePromise, long agentid,
                                    String introduction, int gender, int departmentId,
                                    long doctorId) {

        String sql = "update doctor_info  " +
                " set doc_name                     = ?,  " +
                "    doc_photo                    = ?,  " +
                "    doc_photo_url                = ?,  " +
                "    doc_type                     = ?,  " +
                "    title_id                     = ?,  " +
                "    work_inst_code               = ?,  " +
                "    work_inst_name               = ?,  " +
                "    hospitalid               = ?,  " +
                "    doo_tel                      = ?,  " +
                "    id_card                      = ?,  " +
                "    prac_no                      = ?,  " +

                "    prac_rec_date                = ?,  " +
                "    cert_no                      = ?,  " +
                "    cert_rec_date                = ?,  " +
                "    title_no                     = ?,  " +
                "    title_rec_date               = ?,  " +
                "    prac_type                    = ?,  " +
                "    qualify_or_not               = ?,  " +
                "    professional                 = ?,  " +
                "    sign_time                    = ?,  " +

                "    sign_life                    = ?,  " +
                "    employ_file                  = ?,  " +
                "    employ_file_url              = ?,   " +
                "    credit_level                 = ?,  " +
                "    occu_level                   = ?,  " +
                "    digital_sign                 = ?,  " +
                "    digital_sign_url             = ?,  " +
                "    doc_penalty_points           = ?,  " +
//                "    yc_record_flag               = ?,  " +
//                "    hos_confirm_flag             = ?,  " +

//                "    yc_pres_record_flag          = ?,  " +
                "    prac_scope                   = ?,  " +
                "    prac_scope_approval          = ?,  " +
                "    agree_terms                  = ?,  " +
                "    doc_multi_sited_date_start   = ?,  " +
                "    doc_multi_sited_date_end     = ?,  " +
                "    hos_opinion                  = ?,  " +
                "    hos_digital_sign             = ?,  " +
                "    hos_opinion_date             = ?,  " +
                "    doc_multi_sited_date_promise = ?,  " +

                "    delflag                      = ?,  " +
                "    modify_time                  = ?,  " +
                "    modify_user                  = ?,  " +
                "    introduction                 = ?,  " +
                "    gender                       = ?,  " +
                "    department_id                = ?   " +
                "where doctorid = ?";
        Map<String, Object> map = findHospital(hospitailid);
        List<Object> params = new ArrayList<>();
        params.add(docName);
        params.add(docPhoto);
        params.add(docPhotoUrl);
        params.add(docTypeid);
        params.add(titleId);
        params.add(ModelUtil.getStr(map, "hospital_code"));
        params.add(ModelUtil.getStr(map, "hospital_name"));
        params.add(hospitailid);
        params.add(dooTel);
        params.add(idCard);
        params.add(pracNo);

        params.add(pracRecDate);
        params.add(certNo);
        params.add(certRecDate);
        params.add(titleNo);
        params.add(titleRecDate);
        params.add(pracType);
        params.add(qualifyOrNot);
        params.add(professional);
        params.add(signTime);

        params.add(signLife);
        params.add(employFile);
        params.add(employFileUrl);
        params.add(creditLevel);
        params.add(occuLevel);
        params.add(digitalSign);
        params.add(digitalSignUrl);
        params.add(docPenaltyPoints);
//        params.add(ycRecordFlag);
//        params.add(hosConfirmFlag);
//
//        params.add(ycPresRecordFlag);
        params.add(pracScope);
        params.add(pracScopeApproval);
        params.add(agreeTerms);
        params.add(docMultiSitedDateStart);
        params.add(docMultiSitedDateEnd);
        params.add(hosOpinion);
        params.add(hosDigitalSign);
        params.add(hosOpinionDate);
        params.add(docMultiSitedDatePromise);

        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentid);
        params.add(introduction);
        params.add(gender);
        params.add(departmentId);
        params.add(doctorId);
        return update(sql, params) > 0;

    }

    public Map<String, Object> findHospital(long id) {
        String sql = "select hospital_name,hospital_code from hospital where id=?";
        return queryForMap(sql, id);
    }

    public Map<String, Object> findHospitals(long id) {
        String sql = "select id,hospital_name value,hospital_code code from hospital where id=?";
        return queryForMap(sql, id);
    }

    /**
     * 查询医生咨询订单
     *
     * @param docName   医生姓名
     * @param userName  用户姓名
     * @param dooTel    医生手机号
     * @param states    订单状态
     * @param paystatus 支付状态
     * @param orderNo   订单号
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> getDoctorProblemOrder(String docName, String userName, String dooTel, int states, int paystatus, String orderNo, int pageIndex, int pageSize) {
        String sql = "select dpo.id, " +
                "       dpo.doctorid, " +
                "       d.doc_name      as docname, " +
                "       dpo.userid, " +
                "       u.name          as username, " +
                "       dpo.states, " +
                "       dpo.paystatus, " +
                "       dpo.create_time as createtime " +
                "from doctor_problem_order dpo " +
                "       left join doctor_info d on dpo.doctorid = d.doctorid " +
                "       left join user_account u on dpo.userid = u.id " +
                "where ifnull(dpo.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(docName)) {
            sql += " and d.doc_name like ? ";
            params.add(String.format("%%%s%%", docName));
        }
        if (!StrUtil.isEmpty(userName)) {
            sql += " and u.name like ? ";
            params.add(String.format("%%%s%%", userName));
        }
        if (!StrUtil.isEmpty(dooTel)) {
            sql += " and d.doo_tel like ? ";
            params.add(String.format("%%%s%%", dooTel));
        }
        if (states != -1) {
            sql += " and dpo.states = ? ";
            params.add(String.format("%s", states));
        }
        if (paystatus != -1) {
            sql += " and dpo.paystatus = ? ";
            params.add(String.format("%s", paystatus));
        }
        if (!StrUtil.isEmpty(orderNo)) {
            sql += " and dpo.orderno like ? ";
            params.add(String.format("%%%s%%", orderNo));
        }
        return queryForList(pageSql(sql, " dpo.create_time desc "), pageParams(params, pageIndex, pageSize));
    }

    public long getDoctorProblemOrderTotal(String docName, String userName, String dooTel, int states, int paystatus, String orderNo) {
        String sql = "";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(docName)) {
            sql += " and d.doc_name like ? ";
            params.add(String.format("%%%s%%", docName));
        }
        if (!StrUtil.isEmpty(userName)) {
            sql += " and u.name like ? ";
            params.add(String.format("%%%s%%", userName));
        }
        if (!StrUtil.isEmpty(dooTel)) {
            sql += " and d.doo_tel like ? ";
            params.add(String.format("%%%s%%", dooTel));
        }
        if (states != -1) {
            sql += " and dpo.states = ? ";
            params.add(String.format("%s", states));
        }
        if (paystatus != -1) {
            sql += " and dpo.paystatus = ? ";
            params.add(String.format("%s", paystatus));
        }
        if (!StrUtil.isEmpty(orderNo)) {
            sql += " and dpo.orderno like ? ";
            params.add(String.format("%%%s%%", orderNo));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), Long.class);
    }


    /**
     * 医生手机号注册
     *
     * @param phone
     * @return
     */

    public long addDoctorRegisterNew(String phone, int agreePlatform) {
        long doctorId = getId("doctor_info");
        String inDocCode = UnixUtil.addZeroForNum(doctorId, 2);
        String sql = "insert  into doctor_info(examine,doctorid,doo_tel,in_doc_code,agree_platform,create_time,create_user,delflag,doc_type" +
                ") values (?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(DoctorExamineEnum.inCertified.getCode());
        params.add(doctorId);
        params.add(phone);
        params.add(inDocCode);
        params.add(agreePlatform);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorId);
        params.add(0);
        params.add(2);
        insert(sql, params);
        return doctorId;
    }


    /**
     * 医生注册后修改
     *
     * @param
     * @return
     */
    public boolean addDoctorRegisterNewUpdates(String docPhotoUrl, String hoscode, String hosname, String idcard, long hospitalid, long doctorid, int gender, String name, int departmentid, int titleId) {
        String sql = "update doctor_info set gender=?,doc_name=?,department_id=?,work_inst_code=?,work_inst_name=?,doc_photo_url=?," +
                " title_id=?,id_card=?,hospitalid=? ,examine=? where doctorid=? and ifnull(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(gender);
        params.add(name);
        params.add(departmentid);
        params.add(hoscode);
        params.add(hosname);
        params.add(docPhotoUrl);
        params.add(titleId);
        params.add(idcard);
        params.add(hospitalid);
        params.add(4);
        params.add(doctorid);
        return update(sql, params) > 0;
    }

    /**
     * 医生注册后修改
     *
     * @param
     * @return
     */
    public boolean addDoctorRegisterNewUpdate(String docPhotoUrl, String pra_no, String hoscode, String hosname, String idcard, long hospitalid, long doctorid, int gender, String name, int departmentid, int titleId) {
        String sql = "update doctor_info set prac_no=?, gender=?,doc_name=?,department_id=?,work_inst_code=?,work_inst_name=?,doc_photo_url=?," +
                " title_id=?,id_card=?,hospitalid=? ,examine=? where doctorid=? and ifnull(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(pra_no);
        params.add(gender);
        params.add(name);
        params.add(departmentid);
        params.add(hoscode);
        params.add(hosname);
        params.add(docPhotoUrl);
        params.add(titleId);
        params.add(idcard);
        params.add(hospitalid);
        params.add(4);
        params.add(doctorid);
        return update(sql, params) > 0;
    }


    //邀请人是否存在
    public Map<String, Object> findSalesPerson(String code) {
        String sql = "select id from salesperson where invitation_code=? and ifnull(delflag,0)=0";
        return queryForMap(sql, code);
    }

    //修改邀请码
    public boolean updateSalesPerson(long doctorid, String code) {
        String sql = " update middle_salesperson_doctor set invitation_code=? where doctorid=?  ";
        List<Object> params = new ArrayList<>();
        params.add(code);
        params.add(doctorid);
        return update(sql, params) > 0;
    }

    //删除邀请码中间表
    public boolean delSalesPerson(long doctorid) {
        String sql = " delete from middle_salesperson_doctor where doctorid=?  ";
        return update(sql, doctorid) > 0;
    }


    //新增邀请码中间表
    public boolean insertSalesPerson(long id, String code, long doctorid) {
        String sql = "insert into middle_salesperson_doctor (invitation_code,salespersonid,doctorid,create_time) values (?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(code);
        params.add(id);
        params.add(doctorid);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    public List<Map<String, Object>> salespersonAll() {
        String sql = " select invitation_code id,concat(invitation_code,name) value from salesperson ";
        return queryForList(sql);
    }

    public boolean addDoctorRegisterNewUpdateThree(long doctorid, String professional, String introduction) {
        String sql = "update doctor_info set professional=?,introduction=?,examine=? where doctorid=? and ifnull(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(professional);
        params.add(introduction);
        params.add(DoctorExamineEnum.auditSuccess.getCode());//信息审核成功
        params.add(doctorid);
        return update(sql, params) > 0;
    }

    /**
     * 医生手机号注册
     *
     * @param phone
     * @return
     */
    public long addDoctorRegister(String phone, int agreePlatform) {
        long doctorId = getId("doctor_info");
        String inDocCode = UnixUtil.addZeroForNum(doctorId, 2);
        String sql = "insert  into doctor_info(doctorid,doo_tel,in_doc_code,agree_platform,create_time,create_user,delflag) values (?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(phone);
        params.add(inDocCode);
        params.add(agreePlatform);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorId);
        params.add(0);
        update(sql, params);
        return doctorId;
    }

    public long addDoctorRegister(String phone, String name, int agreePlatform) {
        long doctorId = getId("doctor_info");
        String inDocCode = UnixUtil.addZeroForNum(doctorId, 2);
        String sql = "insert  into doctor_info(doctorid,doo_tel,in_doc_code,agree_platform,create_time,create_user,delflag,doc_name,doc_type) values (?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(phone);
        params.add(inDocCode);
        params.add(agreePlatform);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorId);
        params.add(0);
        params.add(name);
        params.add(DoctorTypeEnum.DctorDiagnosis.getCode());
        update(sql, params);
        return doctorId;
    }


    /**
     * 医生扩展表
     *
     * @param doctorId
     */
    public void addDoctorExpand(long doctorId) {
        String sql = "insert into doctor_extends(doctorid,integral,create_time, create_user)" +
                "values (?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorId);
        update(sql, params);
    }

    /**
     * 医生详细
     *
     * @param doctorid
     * @return
     */
    public Map<String, Object> getSimpleDoctor(long doctorid) {
        String sql = "select reason,doctorid,doo_tel phone,doc_name name,doc_photo_url headpic,work_inst_name hospital,cd.value department,cdt.value title,ifnull(examine ,0) examine,professional,introduction from doctor_info di " +
                "left join  code_department cd on di.department_id=cd.id " +
                "left join code_doctor_title cdt on di.title_id=cdt.id where doctorid=? and ifnull(di.delflag,0)=0 ";
        return queryForMap(sql, doctorid);
    }

    /**
     * @param doctorId
     * @return
     */
    public long getDoctorMessageCount(long doctorId) {
        String sql = "select count(messagetype) count " +
                "from (select message_type messagetype " +
                "      from message " +
                "      where type = 2 " +
                "        and sendid = ? " +
                "        and ifnull(`read`, 0) = 0 " +
                "      group by message_type)m";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 医生是否开启服务
     *
     * @param doctorid
     * @return
     */
    public int isOpen(long doctorid) {
        String sql = "select count(id) count from doc_med_price_list where doctorid=? and whetheropen=1 and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class) > 0 ? 1 : 0;
    }

    /**
     * 修改医生擅长和简介
     *
     * @param doctorid     医生id
     * @param professional 擅长
     * @param introduction 简介
     * @return
     */
    public boolean updateDoctorProfessionalAndIntroduction(long doctorid, String professional, String introduction) {
        String sql = " update doctor_info set professional=? , introduction=? where doctorid=? ";
        List<Object> params = new ArrayList<>();
        params.add(professional);
        params.add(introduction);
        params.add(doctorid);
        return update(sql, params) > 0;
    }

    /**
     * 医生常用药品列表
     *
     * @param doctorid  医生id
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> getDoctorDrugsList(long doctorid, int pageIndex, int pageSize) {
        String sql = " select cd.id,cd.code,cd.value from middle_doctor_drugs mdd left join code_drugs_bak cd on mdd.drugsid=cd.id where doctorid=? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return queryForList(pageSql(sql, " order by mdd.num desc,mdd.id desc "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 药品包列表
     *
     * @return
     */
    public List<Map<String, Object>> getDrugsPackageList() {
        String sql = " select id,name,img from drugs_package where ifnull(delflag,0)=0 order by sort desc ";
        return queryForList(sql);
    }


    /**
     * 药品包下药品字典列表判断医生是否已经选择
     *
     * @param doctorId  医生id
     * @param packageid 药品分类id
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> selectDrugsList(long doctorId, long packageid, int pageIndex, int pageSize) {
        String sql = " select cd.id, cd.value, case when ifnull(mdd.id, 0) = 0 then 0 else 1 end exist " +
                "from middle_drugs_package mdp " +
                "       left join code_drugs_bak cd on mdp.drugsid = cd.id " +
                "       left join middle_doctor_drugs mdd on cd.id = mdd.drugsid and mdd.doctorid = ? " +
                "where ifnull(cd.delflag, 0) = 0 " +
                "  and mdp.packageid = ?  ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(packageid);
        return queryForList(pageSql(sql, " order by cd.id "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 药品包下药品字典列表判断医生是否已经选择
     *
     * @param doctorId  医生id
     * @param name      药品名或者首字母
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> searchDrugsList(long doctorId, String name, int pageIndex, int pageSize) {
        String sql = " select cd.id, cd.value, case when ifnull(mdd.id, 0) = 0 then 0 else 1 end exist " +
                "from code_drugs_bak cd " +
                "       left join middle_doctor_drugs mdd on cd.id = mdd.drugsid and mdd.doctorid = ? " +
                "where ifnull(cd.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        if (!StrUtil.isEmpty(name)) {
            sql += " and ( cd.value like ? or cd.value_pinyin like ? )";
            params.add(String.format("%%%s%%", name));
            params.add(String.format("%%%s%%", name));
        }
        return queryForList(pageSql(sql, " order by cd.id "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 添加常用药品
     *
     * @param doctorid 医生id
     * @param drugsid  药品id
     * @return
     */
    public boolean addOftenDrugs(long doctorid, long drugsid) {
        String sql = " INSERT INTO middle_doctor_drugs(doctorid, drugsid) VALUES (?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(drugsid);
        return insert(sql, params) > 0;
    }

    /**
     * 删除常用药品
     *
     * @param doctorid 医生id
     * @param drugsid  药品id
     * @return
     */
    public boolean delOftenDrugs(long doctorid, long drugsid) {
        String sql = " DELETE FROM middle_doctor_drugs WHERE doctorid=? and drugsid=? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(drugsid);
        return update(sql, params) > 0;
    }


    /**
     * 根据医生手机号是否注册
     *
     * @param phone
     * @return
     */
    public long getDoctorIsRegister(String phone) {
        String sql = "select count(doctorid) as count from doctor_info where doo_tel=? and ifnull(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(phone);
        return jdbcTemplate.queryForObject(sql, params.toArray(), Long.class);
    }

    /**
     * 登录成功查询医生信息
     *
     * @param phone
     * @return
     */
    public Map<String, Object> getDoctorLogin(String phone) {
        String sql = "select examine,doctorid,doc_name as docname,doc_photo as docphoto,work_inst_name as workinstname,cdt.value as titlename,ifnull(examine,0) as examine,in_doc_code as indoccode from doctor_info  " +
                "d left join code_doctor_title cdt on d.title_id=cdt.id  " +
                "where doo_tel=? and ifnull(d.delflag,0)=0 order by d.create_time desc limit 1";
        List<Object> params = new ArrayList<>();
        params.add(phone);
        return queryForMap(sql, params);
    }


    public Map<String, Object> getDoctorLogins(long doctorid) {
        String sql = "select doctorid,prac_no pracno,doc_name as docname,doc_photo_url as docphoto,in_doc_code as indoccode,gender,introduction,professional,id_card idcard from doctor_info  " +
                "where doctorid=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return queryForMap(sql, params);
    }

    public Map<String, Object> getCode(long doctorid) {
        String sql = "select invitation_code code from middle_salesperson_doctor  " +
                "where doctorid=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return queryForMap(sql, params);
    }

    //职称
    public Map<String, Object> getDoctorTitles(long doctorid) {
        String sql = "select cdt.id,cdt.value from doctor_info  " +
                "d inner join code_doctor_title cdt on d.title_id=cdt.id  " +
                "where doctorid=? and ifnull(d.delflag,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        Map<String, Object> map = queryForMap(sql, params);
        return map;
    }

    //医院
    public Map<String, Object> getDoctorHospital(long doctorid) {
        String sql = "select h.id,h.hospital_name value from doctor_info  " +
                "d inner join hospital h on d.hospitalid=h.id  " +
                "where doctorid=? and ifnull(d.delflag,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return queryForMap(sql, params);
    }

    //科室
    public Map<String, Object> getDoctorType(long doctorid) {
        String sql = "select cd.id,cd.value from doctor_info  " +
                "d inner join code_department cd on d.department_id=cd.id  " +
                "where doctorid=? and ifnull(d.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return queryForMap(sql, params);
    }

    //身份证
    public List<Map<String, Object>> getSigns(long doctorid) {
        String sql = "select url from doc_id_card_list " +
                "where doctorid=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return queryForList(sql, params);
    }

    //五证
    public List<Map<String, Object>> getSignt(long doctorid) {
        String sql = "select url from doc_cert_list " +
                "where doctorid=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return queryForList(sql, params);
    }

    //五证
    public List<Map<String, Object>> getSignw(long doctorid) {
        String sql = "select url from doc_title_cert_list " +
                "where doctorid=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return queryForList(sql, params);
    }

    //五证
    public Map<String, Object> getSigno(long doctorid) {
        String sql = "select url from doc_cert_doc_prac_list " +
                "where doctorid=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return queryForMap(sql, params);
    }

    /**
     * 添加银行卡
     *
     * @param doctorid 医生id
     * @param name     开户行
     * @param number   卡号
     * @return
     */
    public boolean addBankCard(long doctorid, String address, String name, String bankName, String number, String issInsId) {
        String sql = " INSERT INTO doctor_bank_card(doctorid,address,name,bankname,number,bankcode,delflag,create_time)VALUES (?,?,?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(address);
        params.add(name);
        params.add(bankName);
        params.add(number);
        params.add(issInsId);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    /**
     * 获取银行卡列表
     *
     * @param doctorid id
     * @return
     */
    public List<Map<String, Object>> getBankCardList(long doctorid) {
        String sql = " SELECT dbc.id, " +
                "       name, " +
                "       dbc.address, " +
                "       dbc.bankname, " +
                "       concat('尾号（', right(number, 4), '）') lastnumber, " +
                "       bp.picture, " +
                "       dbc.bankcode " +
                "FROM doctor_bank_card dbc " +
                "       left join bank_pic bp on dbc.bankcode = bp.bankcode and ifnull(bp.delflag, 0) = 0 " +
                "WHERE doctorid = ? " +
                "  and ifnull(dbc.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return queryForList(sql, params);
    }

    /**
     * 获取银行卡列表
     *
     * @param doctorid id
     * @return
     */
    public long getBankCardCount(long doctorid) {
        String sql = " SELECT count(id) count FROM doctor_bank_card WHERE doctorid=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 获取银行卡详情
     *
     * @param id id
     * @return
     */
    public Map<String, Object> getBankCard(long id) {
        String sql = " SELECT name,address,bankname,number FROM doctor_bank_card WHERE id=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return queryForMap(sql, params);
    }

    /**
     * 解除银行卡绑定
     *
     * @param id id
     * @return
     */
    public boolean delBankCard(long id) {
        String sql = " update doctor_bank_card set delflag=1 WHERE id= ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 是否签到
     *
     * @param doctorid 医生id
     * @return
     */
    public long signFlag(long doctorid) {
        String sql = " select count(id) count from doctor_sign_in where from_unixtime(sign_time/1000,'%Y-%m-%d')=date_format(now(), '%Y-%m-%d') and doctorid=? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 签到
     *
     * @param doctorid 医生id
     * @return
     */
    public boolean signIn(long doctorid) {
        String sql = " INSERT INTO doctor_sign_in(doctorid,sign_time,delflag,create_time) VALUES (?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    public long getOftenDrugsCount(long doctorid, long drugsid) {
        String sql = " select count(id) count from middle_doctor_drugs where doctorid=? and drugsid=? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(drugsid);

        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 患者列表
     *
     * @param doctorId
     * @return
     */
    public List<Map<String, Object>> doctorUserList(long doctorId) {
        String sql = "  select case " +
                "         when UPPER(left(ua.name_pinyin, 1)) = '' or UPPER(left(ua.name_pinyin, 1)) is null then '#' " +
                "         else UPPER(left(ua.name_pinyin, 1)) end as initials, ua.id, ua.name, ua.headpic " +
                "from uiexchange ui " +
                "       left join user_account ua on ui.userid = ua.id " +
                "where ui.doctorid = ? " +
                "  and ifnull(ua.delflag, 0) = 0 " +
                "order by field(initials,'#') ASC,initials ASC  ";
        return queryForList(sql, doctorId);
    }

    /**
     * 患者列表查询
     *
     * @param doctorid
     * @param name
     * @return
     */
    public List<Map<String, Object>> fingDoctorUser(long doctorid, String name) {
        String sql = " select ua.id,ua.name,ua.headpic from uiexchange ui left join user_account ua on ui.userid=ua.id where doctorid=? and ua.delflag=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        if (!StrUtil.isEmpty(name)) {
            sql += " and (ua.name like ? or ua.name_pinyin like ? )";
            params.add(String.format("%%%s%%", name));
            params.add(String.format("%%%s%%", name));
        }
        return queryForList(sql, params);
    }

    /**
     * 修改积分
     *
     * @param doctorid
     * @param integral
     * @return
     */
    public boolean updateIntegral(long doctorid, int integral) {
        String sql = " update doctor_extends set integral=integral+? where doctorid=? ";
        List<Object> params = new ArrayList<>();
        params.add(integral);
        params.add(doctorid);
        return update(sql, params) > 0;
    }

    /**
     * 添加积分详细
     *
     * @param doctorid
     * @param type
     * @param integral
     * @return
     */
    public boolean addIntegralDetailed(long doctorid, int type, int integral) {
        String sql = " insert into doctor_integral_detailed(doctorid, integral, type, create_time) VALUES (?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(integral);
        params.add(type);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    /**
     * 积分详细数量
     *
     * @param doctorId
     * @param type
     * @return
     */
    public long getIntegralDetailed(long doctorId, int type) {
        String sql = " select count(id) count from doctor_integral_detailed where doctorid=? and type=? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(type);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 今日问诊数量
     *
     * @param doctorId
     * @return
     */
    public long getTodayProblemCount(long doctorId) {
        String sql = "select sum(m.count) count " +
                "from (SELECT count(id) count " +
                "      FROM doctor_problem_order " +
                "      WHERE doctorid = ? " +
                "        AND from_unixtime(create_time / 1000, '%Y-%m-%d') = date_format(now(), '%Y-%m-%d') " +
                "        and ifnull(delflag, 0) = 0 " +
                "        and paystatus = 1 " +
                "      union all " +
                "      select count(id) count " +
                "      from doctor_phone_order " +
                "      where doctorid = ? " +
                "        and from_unixtime(create_time / 1000, '%Y-%m-%d') = date_format(now(), '%Y-%m-%d') " +
                "        and ifnull(delflag, 0) = 0 " +
                "        and paystatus = 1 " +
                "      union all " +
                "      select count(id) count " +
                "      from doctor_video_order " +
                "      where doctorid = ? " +
                "        and from_unixtime(create_time / 1000, '%Y-%m-%d') = date_format(now(), '%Y-%m-%d') " +
                "        and ifnull(delflag, 0) = 0 " +
                "        and paystatus = 1 )m ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(doctorId);
        params.add(doctorId);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 历史问诊数量
     *
     * @param doctorId
     * @return
     */
    public long getHistoryProblemcount(long doctorId) {
        String sql = "select sum(m.count) count " +
                "from (SELECT count(id) count " +
                "      FROM doctor_problem_order " +
                "      WHERE doctorid = ? " +
                "        and ifnull(delflag, 0) = 0 " +
                "        and paystatus = 1 " +
                "      union all " +
                "      select count(id) count " +
                "      from doctor_phone_order " +
                "      where doctorid = ? " +
                "        and ifnull(delflag, 0) = 0 " +
                "        and paystatus = 1 " +
                "      union all " +
                "      select count(id) count " +
                "      from doctor_video_order " +
                "      where doctorid = ? " +
                "        and ifnull(delflag, 0) = 0 " +
                "        and paystatus = 1 )m ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(doctorId);
        params.add(doctorId);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    /**
     * 等待接诊数量
     *
     * @param doctorId
     * @return
     */
    public long getWaitVisitCount(long doctorId) {
        String sql = "select sum(m.count) count " +
                "from (SELECT count(id) count " +
                "      FROM doctor_problem_order " +
                "      WHERE doctorid = ? " +
                "        and ifnull(delflag, 0) = 0 " +
                "        and states =2  " +
                "        and paystatus = 1 " +
                "      union all " +
                "      select count(id) count " +
                "      from doctor_phone_order " +
                "      where doctorid = ? " +
                "        and ifnull(delflag, 0) = 0 " +
                "        and status =2" +
                "        and paystatus = 1 " +
                "      union all " +
                "      select count(id) count " +
                "      from doctor_video_order " +
                "      where doctorid = ? " +
                "        and status = 2 " +
                "        and ifnull(delflag, 0) = 0 " +
                "        and status =2" +
                "        and paystatus = 1 )m ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(doctorId);
        params.add(doctorId);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public Map<String, Object> getAnswerCount(long doctorId) {
        String sql = " select count(id) count,case when UNIX_TIMESTAMP() * 1000 - ifnull(max(create_time), 0) > ? then 0 else 1 end new " +
                "from doctor_problem_order " +
                "where ifnull(delflag, 0) = 0 " +
                "  and states in (2, 6) " +
                "  and paystatus = 1 " +
                "  and doctorid = ? ";
        return queryForMap(sql, TextFixed.doctor_homepage_new_order, doctorId);
    }

    public Map<String, Object> getPhoneCount(long doctorId) {
        String sql = " select count(id) count,case when UNIX_TIMESTAMP() * 1000 - ifnull(max(create_time), 0) > ? then 0 else 1 end new " +
                "from doctor_phone_order " +
                "where ifnull(delflag, 0) = 0 " +
                "  and status in (2, 3) " +
                "  and paystatus = 1 " +
                "  and doctorid = ? ";
        return queryForMap(sql, TextFixed.doctor_homepage_new_order, doctorId);
    }


    public Map<String, Object> getPresionFail(long doctorid) {
        String sql = "select prescriptionid,orderid from doc_prescription where ifnull(delflag,0)=0 and examine=3 and doctorid=? and ifnull(havehandle,0)=0 order by create_time asc limit 1";
        return queryForMap(sql, doctorid);
    }

    public List<Map<String, Object>> getChat(long doctorid) {
        String sql = "SELECT " +
                "noread_message havenoread, " +
                "dpo.id," +
                "ua.`name`," +
                "ua.headpic " +
                "FROM " +
                "doctor_problem_order dpo " +
                "LEFT JOIN user_account ua ON dpo.userid = ua.id " +
                "WHERE " +
                "ifnull( dpo.delflag, 0 ) = 0 " +
                "AND noread_message <>0 and dpo.doctorid=? ";
        return queryForList(sql, doctorid);
    }

    public Map<String, Object> getAnswer(long doctroid, long orderid) {
        String sql = "select contenttype,content,create_time createtime from doctor_answer where doctorid=? and orderid=? and ifnull(delflag,0)=0 order by create_time desc limit 1";
        List<Object> list = new ArrayList<>();
        list.add(doctroid);
        list.add(orderid);
        return queryForMap(sql, list);
    }

    public Map<String, Object> getVideoCount(long doctorId) {
        String sql = " select count(id) count,case when UNIX_TIMESTAMP() * 1000 - ifnull(max(create_time), 0) > ? then 0 else 1 end new " +
                "from doctor_video_order " +
                "where ifnull(delflag, 0) = 0 " +
                "  and status in (2, 3) " +
                "  and paystatus = 1 " +
                "  and doctorid = ? ";
        return queryForMap(sql, TextFixed.doctor_homepage_new_order, doctorId);
    }

    /**
     * 等待回复数量
     *
     * @param doctorId
     * @return
     */
    public long getWaitReplyCount(long doctorId) {
        String sql = "select sum(m.count) count " +
                "from (SELECT count(id) count " +
                "      FROM doctor_problem_order " +
                "      WHERE doctorid = ? " +
                "        and ifnull(delflag, 0) = 0 " +
                "        and states =6  " +
                "        and paystatus = 1 " +
                "      union all " +
                "      select count(id) count " +
                "      from doctor_phone_order " +
                "      where doctorid = ? " +
                "        and ifnull(delflag, 0) = 0 " +
                "        and status =3" +
                "        and paystatus = 1  " +
                "      union all " +
                "      select count(id) count " +
                "      from doctor_video_order " +
                "      where doctorid = ? " +
                "        and ifnull(delflag, 0) = 0 " +
                "        and status =3" +
                "        and paystatus = 1 )m ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(doctorId);
        params.add(doctorId);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    /**
     * 查询医生值班状态
     *
     * @param doctorId
     * @return
     */
    public Map<String, Object> getOnDutyStatus(long doctorId, int meadClassId) {
        String sql = "select id,ifnull(whetheropen,0) whetheropen from doc_med_price_list where doctorid=? and med_class_id=? and ifnull(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(meadClassId);
        return queryForMap(sql, params);
    }

    public Map<String, Object> getDepartmentStatus(long doctorId) {
        String sql = "select id,ifnull(whetheropen,0) whetheropendepartment from doc_med_price_list where doctorid=? and med_class_id=3 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForMap(sql, params);
    }

    /**
     * 查询医生值班状态（视频）
     *
     * @param doctorId
     * @return
     */
    public Map<String, Object> getVideoStatus(long doctorId) {
        String sql = "select id,ifnull(whetheropen,0) whetheropenvideo from doc_med_price_list where doctorid=? and med_class_id=4 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForMap(sql, params);
    }

    /**
     * 添加诊所
     *
     * @param medclassid
     * @param medclassname
     * @param price
     * @param doctorid
     * @param whetheropen
     * @return
     */
    public boolean insertDoctorClinic(long medclassid, String medclassname, BigDecimal price, long doctorid,
                                      int whetheropen) {
        String sql = " insert into doc_med_price_list(med_class_id,med_class_name,price,doctorid,create_time,whetheropen) values(?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(medclassid);
        params.add(medclassname);
        params.add(PriceUtil.addPrice(price));
        params.add(doctorid);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(whetheropen);
        return insert(sql, params) > 0;
    }

    public Map<String, Object> findDoctorClinic(long doctorid) {
        String sql = " select id,price,whetheropen from doc_med_price_list where doctorid=? and med_class_id=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(4);
        Map<String, Object> map = queryForMap(sql, params);
        if (map == null) {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("price", "");
            map1.put("whetheropen", 0);
            return map1;
        } else {
            map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
        }
        return map;
    }

    public Map<String, Object> findDoctorClinicDuty(long doctorid) {
        String sql = " select id,price,whetheropen from doc_med_price_list where doctorid=? and med_class_id=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(3);
        Map<String, Object> map = queryForMap(sql, params);
        if (map == null) {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("price", "");
            map1.put("whetheropen", 0);
            return map1;
        } else {
            map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
        }
        return map;
    }

    public Map<String, Object> findDoctorClinics(long doctorid) {
        String sql = " select id from doc_med_price_list where doctorid=? and med_class_id=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(4);
        return queryForMap(sql, params);
    }

    public List<Map<String, Object>> findSchedu(long doctorid) {
        String sql = "select id from doctor_scheduling where doctorid=? and ifnull(delflag,0)=0";
        return queryForList(sql, doctorid);
    }


    /**
     * 修改诊所
     *
     * @param
     * @param price
     * @param whetheropen
     * @return
     */
    public boolean updateDoctorClinic(BigDecimal price, int whetheropen, long doctorid) {
        String sql = " update doc_med_price_list set price=?,whetheropen=? where doctorid=? and med_class_id=4 and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(PriceUtil.addPrice(price));
        params.add(whetheropen);
        params.add(doctorid);
        return update(sql, params) > 0;
    }

    public boolean insertScheduling(long doctorid, long visiting_start_time, long visiting_end_time) {
        String sql = "insert into doctor_scheduling (doctorid,visiting_start_time,visiting_end_time,examine_state,issubscribe,create_time) " +
                " values (?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(visiting_start_time);
        params.add(visiting_end_time);
        params.add(2);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    public List<Map<String, Object>> getHaveSelectTime(long doctorid) {
        String sql = "select id,visiting_start_time from doctor_scheduling where ifnull(delflag,0)=0 and doctorid=? and visiting_start_time>? order by visiting_start_time asc ";
        List<Object> list = new ArrayList<>();
        list.add(doctorid);
        list.add(UnixUtil.getNowTimeStamp());
        return queryForList(sql, list);
    }

    public List<Map<String, Object>> getHaveSelectTimePhone(long doctorid) {
        String sql = "select id,visiting_start_time from doctor_onduty where ifnull(delflag,0)=0 and doctorid=? and visiting_start_time>? order by visiting_start_time asc ";
        List<Object> list = new ArrayList<>();
        list.add(doctorid);
        list.add(UnixUtil.getNowTimeStamp());
        return queryForList(sql, list);
    }


    public boolean insertDuty(long doctorid, long visiting_start_time, long visiting_end_time) {
        String sql = "insert into doctor_onduty (doctorid,visiting_start_time,visiting_end_time,examine_state,issubscribe,create_time) " +
                " values (?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(visiting_start_time);
        params.add(visiting_end_time);
        params.add(2);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }


    /**
     * 开关电话状态
     *
     * @param doctorId    医生id
     * @param whetherOpen 是否开关
     * @return
     */
    public boolean updateWhetherOpen(long doctorId, int whetherOpen, int meadClassId) {
        String sql = "update doc_med_price_list set whetheropen=? where doctorid=? and med_class_id=?";
        List<Object> params = new ArrayList<>();
        params.add(whetherOpen);
        params.add(doctorId);
        params.add(meadClassId);
        return update(sql, params) > 0;
    }

    /**
     * 是否开通急诊
     *
     * @param doctorId
     * @param whetherOpen
     * @return
     */
    public boolean updateDepartmentWhetherOpen(long doctorId, int whetherOpen) {
        String sql = "update doc_med_price_list set whetheropen=? where doctorid=? and med_class_id =3 ";
        List<Object> params = new ArrayList<>();
        params.add(whetherOpen);
        params.add(doctorId);
        return update(sql, params) > 0;
    }


    public boolean deleteDoctorDutyRule(long doctorId) {
        String sql = "delete from doctor_duty_rule  where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return update(sql, params) > 0;
    }

    /**
     * 查询电话价格
     *
     * @param doctorId
     * @param meadClassId
     * @return
     */
    public Map<String, Object> getPhonePrice(long doctorId, int meadClassId) {
        String sql = "select id,price from doc_med_price_list where doctorid=? and med_class_id=? and ifnull(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(meadClassId);
        Map<String, Object> data = queryForMap(sql, params);
        if (data != null) {
            data.put("price", PriceUtil.findPrice(ModelUtil.getLong(data, "price", 0)));
        }
        return data;
    }


    public Map<String, Object> getAnswerTime(long doctorId) {
        String sql = "select id,visiting_start_time as starttime,visiting_end_time as endtime from doctor_inquiry where doctorid=? and ifnull(delflag,0)=0 order by id desc limit 1 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForMap(sql, params);
    }

    public Map<String, Object> getWeeks(long doctorId) {
        String sql = "select sunday,monday, tuesday, wednesday, thursday, friday, saturday from doctor_duty_rule where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForMap(sql, params);
    }


    public boolean addDoctorDutyRule(long doctorId, boolean monday, boolean tuesday, boolean wednesday,
                                     boolean thursday, boolean friday, boolean saturday, boolean sunday, String startTime, String endTime) {
        String sql = "insert into doctor_duty_rule(doctorid, monday, tuesday, wednesday, thursday, friday, saturday,sunday,starttime,endtime,delflag, create_time, create_user) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(monday);
        params.add(tuesday);
        params.add(wednesday);
        params.add(thursday);
        params.add(friday);
        params.add(saturday);
        params.add(sunday);
        params.add(startTime);
        params.add(endTime);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorId);
        return update(sql, params) > 0;
    }

    /**
     * 添加问诊值班
     *
     * @param doctorId          医生id
     * @param visitingStartTime 值班开始时间
     * @param visitingEndTime   值班结束时间
     * @return
     */
    public boolean addDoctorInquiry(long doctorId, long visitingStartTime, long visitingEndTime) {
        String sql = "insert into doctor_inquiry(doctorid, visiting_start_time, visiting_end_time, delflag, create_time, create_user)" +
                "values (?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(visitingStartTime);
        params.add(visitingEndTime);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorId);
        return update(sql, params) > 0;
    }

    /**
     * 保存医生价格
     *
     * @param meadClassId   咨询类型id
     * @param meadClassName 咨询类型中文名称
     * @param price         咨询价格
     * @param doctorId      医生id
     * @param whetherOpen   是否开启
     * @return
     */
    public boolean addDoctorPrice(int meadClassId, String meadClassName, BigDecimal price, long doctorId,
                                  long whetherOpen) {
        String sql = "insert into doc_med_price_list(med_class_id, med_class_name, price, doctorid, delflag, create_time, create_user, whetheropen) " +
                "values (?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(meadClassId);
        params.add(meadClassName);
        params.add(PriceUtil.addPrice(price));
        params.add(doctorId);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorId);
        params.add(whetherOpen);
        return update(sql, params) > 0;
    }

    /**
     * 更新医生价格信息
     *
     * @param meadClassId
     * @param price
     * @param doctorId
     * @param whetherOpen
     * @return
     */
    public boolean updateDoctorPirce(int meadClassId, BigDecimal price, long doctorId, long whetherOpen) {
        String sql = "update doc_med_price_list set whetheropen=?,price=? where doctorid=? and med_class_id=?";
        List<Object> params = new ArrayList<>();
        params.add(whetherOpen);
        params.add(PriceUtil.addPrice(price));
        params.add(doctorId);
        params.add(meadClassId);
        return update(sql, params) > 0;
    }


    /**
     * 查询是否开启过咨询
     *
     * @param meadClassId
     * @param doctorId
     * @return
     */
    public long getDoctorPrice(int meadClassId, long doctorId) {
        String sql = "select count(id) from doc_med_price_list where doctorid=? and med_class_id=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(meadClassId);
        return jdbcTemplate.queryForObject(sql, params.toArray(), Long.class);
    }


    /**
     * 医生当前星期排班删除
     *
     * @param doctorId 医生id
     * @return
     */
    public boolean deleteDoctorInquiry(long doctorId) {
        String sql = " update " +
                "       doctor_inquiry " +
                "set delflag=1 " +
                "where visiting_start_time >= unix_timestamp(curdate()) * 1000 " +
                "  and visiting_start_time < " +
                "      UNIX_TIMESTAMP(subdate(curdate(), date_format(curdate(), '%w') - 8)) * 1000 " +
                "  and doctorid = ? ";
        List<Object> patams = new ArrayList<>();
        patams.add(doctorId);
        return update(sql, patams) > 0;
    }


    /**
     * 查询医生排班信息
     *
     * @param doctorId
     * @return
     */
    public List<Map<String, Object>> getOndutyList(long doctorId) {
        String sql = "select id,examine_state as examinestate,visiting_start_time as visitingstarttime, " +
                "       visiting_end_time as visitingendtime,shift from doctor_onduty where doctorid = ? " +
                "  and UNIX_TIMESTAMP(now())< visiting_end_time / 1000";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForList(sql, params);
    }

    /**
     * 查询可以排班医生的医生
     *
     * @return
     */
    public List<Map<String, Object>> getWaitSchedulingDoctorList() {
        String sql = "select d.doctorid,concat_ws(' ',doc_name,doo_tel) as value from doctor_info d " +
                " left join doc_med_price_list dmpl on dmpl.doctorid=d.doctorid " +
                "where ifnull(d.delflag,0)=0 and d.examine=? and doc_type=? and dmpl.med_class_id=3 and ifnull(dmpl.whetheropen,0)=1";
        List<Object> params = new ArrayList<>();
        params.add(DoctorExamineEnum.certificationSuccess.getCode());
        params.add(DoctorTypeEnum.DoctorExpert.getCode());
        return queryForList(sql, params);
    }

    /**
     * 查询可以排班医生的医生
     *
     * @return
     */
    public List<Map<String, Object>> getWaitSittingDoctorList() {
        String sql = " select d.doctorid id, concat_ws(' ', doc_name, doo_tel) as name " +
                "from doctor_info d " +
                "       left join doc_med_price_list dmpl1 on dmpl1.doctorid = d.doctorid and dmpl1.med_class_id = 1 " +
                "       left join doc_med_price_list dmpl3 on dmpl3.doctorid = d.doctorid and dmpl3.med_class_id = 3 " +
                "where ifnull(d.delflag, 0) = 0 " +
                "  and d.examine in(2,7) " +
                "  and d.doc_type in(3,4) ";
        return queryForList(sql);
    }

    /**
     * 医生排班 添加
     *
     * @param doctorId          医生ID
     * @param visitingStartTime 排班开始时间
     * @param visitingEndTime   排班结束时间
     */
    public boolean addOnduty(long doctorId, long visitingStartTime, long visitingEndTime, int shift) {
        String sql = "insert into doctor_onduty(doctorid, visiting_start_time, visiting_end_time, examine_state, delflag, create_time, create_user,shift)" +
                "values (?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(visitingStartTime);
        params.add(visitingEndTime);
        params.add(0);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorId);
        params.add(shift);
        return update(sql, params) > 0;
    }

    public boolean findByDoctor(long doctorid, long visitingStartTime, long visitingEndTime, int shift) {
        String sql = "select id from doctor_onduty where doctorid=? and ifnull(delflag,0)=0 and visiting_start_time=? and visiting_end_time=? and shift=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(visitingStartTime);
        params.add(visitingEndTime);
        params.add(shift);
        Map<String, Object> map = queryForMap(sql, params);
        //该医生排班已经存在
        if (null != map) {
            return true;
        }
        return false;
    }

    /**
     * 医生排班 修改
     *
     * @param doctorId          医生ID
     * @param visitingStartTime 排班开始时间
     * @param visitingEndTime   排班结束时间
     */
    public boolean updateDoctorOnduty(int id, long doctorId, long visitingStartTime, long visitingEndTime,
                                      int shift) {
        String sql = "update doctor_onduty set visiting_start_time=?,visiting_end_time=?,modify_time=?,modify_user= ?,shift= ? where id= ? ";
        List<Object> params = new ArrayList<>();
        params.add(visitingStartTime);
        params.add(visitingEndTime);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorId);
        params.add(shift);
        params.add(id);
        return update(sql, params) > 0;
    }


    public List<Map<String, Object>> orderProfitList(long doctorid, int pageIndex, int pageSize) {
        String sql = " select * " +
                "from (select sp.id, " +
                "             ua.headpic, " +
                "             sp.create_time as createtime, " +
                "             sp.actualmoney    price, " +
                "             1                 ordertype, " +
                "             concat(ua.name,'_问诊')              name " +
                "      from doctor_problem_order sp " +
                "             left join user_account ua on sp.userid = ua.id " +
                "      where ifnull(sp.delflag, 0) = 0 " +
                "        and sp.doctorid = ? " +
                "        and sp.states = 4 " +
                "      union all " +
                "      select sp.id, " +
                "             ua.headpic, " +
                "             sp.create_time as createtime, " +
                "             sp.actualmoney    price, " +
                "             2                 ordertype, " +
                "             concat(ua.name,'_电话')              name " +
                "      from doctor_phone_order sp " +
                "             left join user_account ua on sp.userid = ua.id " +
                "      where ifnull(sp.delflag, 0) = 0 " +
                "        and sp.doctorid = ? " +
                "        and sp.status = 4 ) o ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(doctorid);
        return query(pageSql(sql, " order by createtime desc "), pageParams(params, pageIndex, pageSize), (ResultSet rs, int rowNum) -> {
            Map<String, Object> data = resultToMap(rs);
            if (data != null) {
                data.put("price", PriceUtil.findPrice(ModelUtil.getLong(data, "price")));
            }
            return data;
        });
    }

    /**
     * 积分详细列表
     *
     * @param doctorid
     * @param callback
     * @return
     */
    public List<Map<String, Object>> doctorIntegralList(long doctorid, int pageIndex, int pageSize, IMapperResult
            callback) {
        String sql = " select integral,type,create_time createtime from doctor_integral_detailed where doctorid=? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return query(pageSql(sql, " order by id desc "), pageParams(params, pageIndex, pageSize), (ResultSet res, int row) -> {
            Map<String, Object> result = resultToMap(res);
            if (callback != null) {
                callback.result(result);
            }
            return result;
        });
    }

    /**
     * 当月积分
     *
     * @param doctorid
     * @return
     */
    public long thisMonthDoctorIntegralCount(long doctorid) {
        String sql = " select count(integral) count from doctor_integral_detailed where doctorid=? and from_unixtime(create_time/1000,'%Y-%m')=date_format(now(),'%Y-%m') ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 总积分
     *
     * @param doctorid
     * @return
     */
    public long totalDoctorIntegralCount(long doctorid) {
        String sql = " select count(integral) count from doctor_integral_detailed where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public List<Map<String, Object>> newUserList(long doctorid, int pageIndex, int pageSize) {
        String sql = "  select ua.id,ua.name,ua.headpic,u.create_time createtime  " +
                "from uiexchange u  " +
                "       left join user_account ua on u.userid = ua.id  " +
                "where ifnull(u.delflag, 0) = 0  " +
                "  and ifnull(ua.delflag, 0) = 0  " +
                "  and UNIX_TIMESTAMP() * 1000 - u.create_time > ? " +
                "  and doctorid = ?  ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(TextFixed.new_user_time);
        return queryForList(pageSql(sql, " order by u.create_user desc "), pageParams(params, pageIndex, pageSize));
    }

    public long newUserCount(long doctorid) {
        String sql = " select count(ua.id) count " +
                "from uiexchange u " +
                "       left join user_account ua on u.userid = ua.id " +
                "where ifnull(u.delflag, 0) = 0 " +
                "  and ifnull(ua.delflag, 0) = 0 " +
                "  and ifnull(newflag, 0) = 0 " +
                "  and doctorid = ? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public long getDoctorUserCount(long doctorid, long userid) {
        String sql = " select count(id) count from uiexchange where doctorid=? and userid=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(userid);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public boolean addDoctorUser(long doctorid, long userid) {
        String sql = " insert into uiexchange (doctorid, userid, newflag,delflag, create_time) " +
                "values (?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(userid);
        params.add(0);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    public boolean updateDoctorUser(long doctorid, long userid) {
        String sql = " update uiexchange set newflag=?, modify_time=? where doctorid=? and userid=? ";
        List<Object> params = new ArrayList<>();
        params.add(1);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorid);
        params.add(userid);
        return insert(sql, params) > 0;
    }

    /**
     * 获取电话医生
     *
     * @return
     */
    public Map<String, Object> getPhoneDoctor() {
        String sql = "select di.doctorid " +
                "from doctor_info di " +
                "       left join doctor_extends de on di.doctorid = de.doctorid " +
                "       left join doctor_onduty do on di.doctorid = do.doctorid " +
                "       left join doc_med_price_list dm on di.doctorid=dm.doctorid " +
                "where ifnull(di.delflag, 0) = 0 " +
                "  and di.examine = 2 " +
                "  and ifnull(dm.whetheropen,1)=1 " +
                "  and do.visiting_start_time / 1000 < UNIX_TIMESTAMP(now()) " +
                "  and do.visiting_end_time / 1000 > UNIX_TIMESTAMP(now()) " +
                "order by de.phonecount, di.doctorid limit 1 ";
        return queryForMap(sql);
    }

    /**
     * 获取问诊医生
     *
     * @return
     */
    public Map<String, Object> getAnserDoctor() {
        String sql = " select di.doctorid " +
                "from doctor_info di " +
                "       left join doctor_extends de on di.doctorid = de.doctorid " +
                "       left join doc_med_price_list dm on di.doctorid = dm.doctorid " +
                "where di.examine = 2 " +
                "  and ifnull(dm.whetheropen, 0) = 1 " +
                "  and dm.med_class_id = 1 " +
                "  and ifnull(di.delflag, 0) = 0 " +
                "order by de.answercount, di.doctorid " +
                "limit 1 ";
        return queryForMap(sql);
    }


    /**
     * 查询消息列表
     *
     * @param doctorId
     * @return
     */
    public List<Map<String, Object>> getDoctorMessageList(long doctorId, int pageIndex, int pageSize) {
        String sql = " select id, " +
                "       url, " +
                "       name, " +
                "       message_type    as messagetype, " +
                "       type_name       as typename, " +
                "       message_text    as messagetext, " +
                "       message_subtext as messagesubtext, " +
                "       `read`          as isread, " +
                "       create_time     as createtime " +
                "from message " +
                "where id in(select max(id) " +
                "            from message " +
                "            where type = 2 " +
                "              and sendid = ? " +
                "              and ifnull(delflag, 0) = 0 " +
                "            group by message_type) ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForList(pageSql(sql, " order by id desc "), pageParams(params, pageIndex, pageSize));
    }


    public Map<String, Object> getMessageType(long id) {
        String sql = " select message_type messagetype from message where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return queryForMap(sql, params);
    }

    /**
     * 查询消息列表
     *
     * @param type
     * @return
     */
    public boolean updateMessageReadStatu(long type) {
        String sql = " update message set `read`=1 where message_type=? and type=2 ";
        List<Object> params = new ArrayList<>();
        params.add(type);
        return update(sql, params) > 0;
    }


    public boolean setSittingDoctor(long doctorid, long agentid, long startTime, long endTime) {
        String sql = " insert into doctor_sitting(doctorid, delflag, create_time, create_user,start_time,end_time)values (?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentid);
        params.add(startTime);
        params.add(endTime);
        return insert(sql, params) > 0;
    }

    public boolean findByDoctor(long id, long startTime, long endTime) {
//        String sql = "select id from doctor_sitting where doctorid=? and ((start_time between ? and ?) or (end_time between ? and ?)) and ifnull(delflag,0)=0";
        String sql = "select id from doctor_sitting where doctorid=? and ((start_time > ? and start_time<?) or (end_time> ? and end_time<?)) and ifnull(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(id);
        params.add(startTime);
        params.add(endTime);
        params.add(startTime);
        params.add(endTime);
        List<Map<String, Object>> list = queryForList(sql, params);
        return list.size() > 0 ? true : false;
    }

    public Map<String, Object> findDoctorName(long id) {
        String sql = "select doc_name from doctor_info where doctorid=?";
        return queryForMap(sql, id);
    }

    public boolean updateDoctorExtendAnswerCount(long doctorId) {
        String sql = " update doctor_extends set answercount=answercount+1 where doctorid=? and ifnull(delflag,0)=0 ";
        return update(sql, doctorId) > 0;
    }

    public boolean updateDoctorExtendPhoneCount(long doctorId) {
        String sql = "update doctor_extends set phonecount=phonecount+1 where doctorid=? and ifnull(delflag,0)=0; ";
        return update(sql, doctorId) > 0;
    }

    public boolean updateDoctorExtendVideoCount(long doctorId) {
        String sql = "update doctor_extends set videocount=videocount+1 where doctorid=? and ifnull(delflag,0)=0; ";
        return update(sql, doctorId) > 0;
    }

    //所有坐班医生和顾问
    public Map<String, Object> getSittingDoctor() {
        String sql = " select ds.doctorid " +
                "from doctor_sitting ds " +
                "       left join doctor_extends de on ds.doctorid = de.doctorid " +
                "       left join doctor_info di on de.doctorid = di.doctorid " +
                "where ifnull(ds.delflag, 0) = 0 " +
                "  and ifnull(di.delflag, 0) = 0 " +
                "  and di.examine in (2,7)" +
                "  and di.doc_type in (3,4)" +
                "  and ds.start_time<= ? " +
                "  and ds.end_time > ? " +
                "ORDER BY RAND() " +
                "LIMIT 1 ";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(UnixUtil.getNowTimeStamp());
        return queryForMap(sql, params);
    }

    //所有坐班顾问
    public Map<String, Object> getHealthyDoctor() {
        String sql = " select ds.doctorid " +
                "from doctor_sitting ds " +
                "       left join doctor_extends de on ds.doctorid = de.doctorid " +
                "       left join doctor_info di on de.doctorid = di.doctorid " +
                "where ifnull(ds.delflag, 0) = 0 " +
                "  and ifnull(di.delflag, 0) = 0 " +
                "  and di.examine = 2 " +
                "  and di.doc_type =4 " +
                "  and ds.start_time<= ? " +
                "  and ds.end_time > ? " +
                "ORDER BY RAND() " +
                "LIMIT 1 ";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(UnixUtil.getNowTimeStamp());
        return queryForMap(sql, params);
    }

    //没有正在进行中订单的坐班医生和顾问
    public Map<String, Object> getPhoneSittingDoctor() {
        String sql = " select ds.doctorid " +
                "from doctor_sitting ds " +
                "       left join doctor_extends de on ds.doctorid = de.doctorid " +
                "       left join doctor_info di on de.doctorid = di.doctorid " +
                "left join doctor_phone_order dpo on dpo.doctorid=ds.doctorid " +
                "where ifnull(ds.delflag, 0) = 0 " +
                "  and ifnull(di.delflag, 0) = 0 " +
                "  and di.examine in (2,7)" +
                "  and di.doc_type in (3,4)" +
                "  and ds.start_time<= ? " +
                "  and ds.end_time > ? " +
                "and dpo.status not in(2,3) " +
                "  group by de.doctorid " +
                "ORDER BY RAND() LIMIT 1 ";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(UnixUtil.getNowTimeStamp());
        return queryForMap(sql, params);
    }

    public Map<String, Object> getAnswerSittingDoctor() {
        String sql = " select ds.doctorid " +
                "from doctor_sitting ds " +
                "       left join doctor_extends de on ds.doctorid = de.doctorid " +
                "       left join doctor_info di on de.doctorid = di.doctorid " +
                "       left join doctor_problem_order dpo on dpo.doctorid=ds.doctorid " +
                "where ifnull(ds.delflag, 0) = 0 " +
                "  and ifnull(di.delflag, 0) = 0 " +
                "  and di.examine in (2,7)" +
                "  and di.doc_type in (3,4)" +
                "  and dpo.states not in(2,6) " +
                "  and ds.start_time<= ? " +
                "  and ds.end_time > ? " +
                "  group by de.doctorid " +
                "ORDER BY RAND() LIMIT 1 ";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(UnixUtil.getNowTimeStamp());
        return queryForMap(sql, params);
    }

    public boolean delSittingDoctor(long id) {
        String sql = " update doctor_sitting set delflag=1 where id=? ";
        return update(sql, id) > 0;
    }

    public List<Map<String, Object>> getSittingDoctorList(String name, String phone, int pageIndex, int pageSize) {
        String sql = " select d.id, di.doc_name name, di.doo_tel phone, di.doc_photo_url headpic, d.create_time createtime,d.start_time,d.end_time " +
                "from doctor_sitting d " +
                "       left join doctor_info di on d.doctorid = di.doctorid " +
                "where ifnull(d.delflag, 0) = 0 " +
                "  and ifnull(di.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and di.doo_tel like ? ";
            params.add(String.format("%%%s%%", phone));
        }
        List<Map<String, Object>> map = queryForList(pageSql(sql, " order by d.id desc"), pageParams(params, pageIndex, pageSize));
        return map;
    }

    public long getSittingDoctorCount(String name, String phone) {
        String sql = " select count(d.id) coun from doctor_sitting d " +
                "       left join doctor_info di on d.doctorid = di.doctorid " +
                "where ifnull(d.delflag, 0) = 0 " +
                "  and ifnull(di.delflag, 0) = 0";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and di.doo_tel like ? ";
            params.add(String.format("%%%s%%", phone));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    public Map<String, Object> findById() {
        String sql = "select id,emergency_price as emergencyprice,outpatient_price as outpatientprice,video_price videoprice from doctor_emergency_clinic_price where ifnull(delflag, 0) = 0 ";
        Map<String, Object> map = queryForMap(sql);
        if (null != map) {
//            BigDecimal a = PriceUtil.findPrice(ModelUtil.getLong(map, "emergencyprice"));
//            System.out.println(a);
            map.put("emergencyprice", PriceUtil.findPrice(ModelUtil.getLong(map, "emergencyprice")));
            map.put("outpatientprice", PriceUtil.findPrice(ModelUtil.getLong(map, "outpatientprice")));
            map.put("videoprice", PriceUtil.findPrice(ModelUtil.getLong(map, "videoprice")));
        }
        return map;
    }

    /*
    查询折扣
     */
    public Map<String, Object> discount() {
        String sql = " select health_consultant_discount from vip_card where IFNULL(delflag,0) = 0 order by sort desc limit 1 ";
        return queryForMap(sql);
    }

    /*
   图文
    */
    public boolean updateOutpatientprice(BigDecimal outpatientprice) {
        String sql = " update doctor_emergency_clinic_price set outpatient_price = ? where id = 1 ";
        List<Object> params = new ArrayList<>();
        params.add(PriceUtil.addPrice(outpatientprice));
        return update(sql, params) > 0;
    }

    /*
    电话
     */
    public boolean updateEmergencyprice(BigDecimal emergencyprice) {
        String sql = " update doctor_emergency_clinic_price set emergency_price = ? where id = 1 ";
        List<Object> params = new ArrayList<>();
        params.add(PriceUtil.addPrice(emergencyprice));
        return update(sql, params) > 0;
    }

    /*
    视频
     */
    public boolean updateVideoprice(BigDecimal videoprice) {
        String sql = " update doctor_emergency_clinic_price set video_price = ? where id = 1 ";
        return update(sql, PriceUtil.addPrice(videoprice)) > 0;
    }


    public long getDoctorCount(long doctorId, String dooTel) {
        String sql = " select count(doctorid) count from doctor_info where doctorid!=? and doo_tel=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(dooTel);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public long getDoctorCount(String dooTel) {
        String sql = " select count(doctorid) count from doctor_info where doo_tel=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(dooTel);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    /**
     * 诊疗医师
     *
     * @param docName
     * @param dooTel
     * @param workInstName
     * @param titleId
     * @param departmentId
     * @param doctorStart
     * @param doctorEnd
     * @param examine
     * @param graphicStatus
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getClinicsList(String docName, String dooTel, String workInstName, String
            titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus, int pageIndex,
                                                    int pageSize) {
        String sql = " select d.doctorid, " +
                "       doc_name                         as docname, " +
                "       d.in_doc_code                         as indoccode, " +
                "       reason, " +
                "       doo_tel                          as dootel, " +
                "       d.create_time                    as createtime, " +
                "       ifnull(d.examine, 0)             as examine " +
                " from doctor_info d " +
                "       left join code_department cdp on d.department_id = cdp.id " +
                "       left join code_doctor_title cdt on d.title_id = cdt.id " +
                "       left join doc_med_price_list dmplqa on dmplqa.doctorid = d.doctorid and dmplqa.med_class_id = 2 " +
                "       left join doc_med_price_list dmplphone on dmplphone.doctorid = d.doctorid and dmplphone.med_class_id = 3 " +
                " where ifnull(d.delflag, 0) = 0 and doc_type = ? and examine in (?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(DoctorTypeEnum.DctorDiagnosis.getCode());
        params.add(DoctorExamineEnum.successfulCertified.getCode());
        params.add(DoctorExamineEnum.failCertified.getCode());
        params.add(DoctorExamineEnum.inCertified.getCode());
        if (!StrUtil.isEmpty(docName)) {
            sql += " and d.doc_name like ?";
            params.add(String.format("%%%s%%", docName));
        }
        if (!StrUtil.isEmpty(dooTel)) {
            sql += " and d.doo_tel like ?";
            params.add(String.format("%%%s%%", dooTel));
        }
        if (!StrUtil.isEmpty(workInstName)) {
            sql += " and d.work_inst_name like ?";
            params.add(String.format("%%%s%%", workInstName));
        }
        if (!StrUtil.isEmpty(titleId)) {
            sql += " and  cdt.id = ?";
            params.add(String.format("%s", titleId));
        }
        if (!StrUtil.isEmpty(departmentId)) {
            sql += " and cdp.id = ?";
            params.add(String.format("%s", departmentId));
        }
        if (doctorStart != 0 && doctorEnd != 0) {
            sql += " and sign_time between ? and ?";
            params.add(String.format("%s", doctorStart));
            params.add(String.format("%s", doctorEnd));
        }
        if (examine != 0) {
            sql += " and d.examine=?";
            params.add(String.format("%s", examine));
        }
        if (graphicStatus != -1) {
            sql += " and ifnull(dmplqa.whetheropen,0) = ? ";
            params.add(String.format("%s", graphicStatus));
        }
        return query(pageSql(sql, " order by FIELD(d.examine, 1,4,3),d.create_time desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (null != map) {
                map.put("doctype", DoctorTypeEnum.getValue(ModelUtil.getInt(map, "doctype")).getMessage());
            }
            return map;
        });


    }

    public long getClinicsListCount(String docName, String dooTel, String workInstName, String titleId, String
            departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus) {
        String sql = " select count(d.doctorid) count " +
                " from doctor_info d " +
                "       left join code_department cdp on d.department_id = cdp.id " +
                "       left join code_doctor_title cdt on d.title_id = cdt.id " +
                "       left join doc_med_price_list dmplqa on dmplqa.doctorid = d.doctorid and dmplqa.med_class_id = 2 " +
                "       left join doc_med_price_list dmplphone on dmplphone.doctorid = d.doctorid and dmplphone.med_class_id = 3 " +
                " where ifnull(d.delflag, 0) = 0 and doc_type = 2 and examine in (1,3,4) ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(docName)) {
            sql += " and d.doc_name like ?";
            params.add(String.format("%%%s%%", docName));
        }
        if (!StrUtil.isEmpty(dooTel)) {
            sql += " and d.doo_tel like ?";
            params.add(String.format("%%%s%%", dooTel));
        }
        if (!StrUtil.isEmpty(workInstName)) {
            sql += " and d.work_inst_name like ?";
            params.add(String.format("%%%s%%", workInstName));
        }
        if (!StrUtil.isEmpty(titleId)) {
            sql += " and  cdt.id = ?";
            params.add(String.format("%s", titleId));
        }
        if (!StrUtil.isEmpty(departmentId)) {
            sql += " and cdp.id = ?";
            params.add(String.format("%s", departmentId));
        }
        if (doctorStart != 0 && doctorEnd != 0) {
            sql += " and sign_time between ? and ?";
            params.add(String.format("%s", doctorStart));
            params.add(String.format("%s", doctorEnd));
        }
        if (examine != 0) {
            sql += " and d.examine=?";
            params.add(String.format("%s", examine));
        }
        if (graphicStatus != -1) {
            sql += " and ifnull(dmplqa.whetheropen,0) = ? ";
            params.add(String.format("%s", graphicStatus));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);


    }


    /**
     * 绑定专家下拉渲染值
     *
     * @param doctorid
     * @return
     */
    public Map<String, Object> bindingExpert(long doctorid) {
        String sql = " select di.doctorid id,di.doc_name name from doctor_info di where di.doctorid = (select de.pid from doctor_extends de where de.doctorid = ?) ";
        return queryForMap(sql, doctorid);
    }

    public Map<String, Object> getSalesId(long doctorId) {
        String sql = " select invitation_code id,invitation_code value from middle_salesperson_doctor  " +
                " where ifnull(delflag,0)=0 and doctorid=?";
        return queryForMap(sql, doctorId);
    }


    /**
     * 诊疗医师详情
     *
     * @param doctorId
     * @return
     */
    public Map<String, Object> getClinicsById(long doctorId) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT " +
                " di.doctorid, " +
                " di.in_doc_code AS indoccode, " +
                " di.doc_name AS docname, " +
                " di.doc_photo_url AS docphotourl, " +
                " di.title_id AS titleid, " +
                " di.hospitalid, " +
                " di.doo_tel AS dootel, " +
                " di.id_card AS idcard, " +
                " di.prac_no AS pracno, " +
                " di.professional, " +
                " di.introduction, " +
                " di.gender, " +
                " di.examine, " +
                " di.department_id AS departmentid, " +
                " msd.invitation_code as invitationcode " +
//                " de.pid expertid " +
                " FROM " +
                " doctor_info di " +
                " left join middle_salesperson_doctor msd on ifnull(msd.delflag,0)=0 and msd.doctorid=di.doctorid " +
//                " left join doctor_extends de on di.doctorid = de.doctorid and IFNULL(de.delflag,0)=0 " +
                " WHERE " +
                " ifnull( di.delflag, 0 ) = 0  " +
                " AND di.doctorid = ? ";
        params.add(doctorId);
        return queryForMap(sql, params);
    }

    /**
     * 修改审核状态
     *
     * @param doctorid
     * @param examine
     * @return
     */
    public boolean updataExamine(long doctorid, int examine) {
        String sqltime = " update doctor_info set examine = ?,examine_time = ? where doctorid = ? ";
        String sql = " update doctor_info set examine = ? where doctorid = ? ";
        long sq = 0;
        if (examine == DoctorExamineEnum.auditSuccess.getCode()) {
            List<Object> params = new ArrayList<>();
            params.add(examine);
            params.add(UnixUtil.getNowTimeStamp());
            params.add(doctorid);
            sq = update(sqltime, params);
        } else {
            List<Object> params = new ArrayList<>();
            params.add(examine);
            params.add(doctorid);
            sq = update(sql, params);
        }
        return sq > 0;
    }


    /**
     * 添加信息审核失败原因
     *
     * @param doctorid
     * @param reason
     * @return
     */
    public boolean updateReason(long doctorid, String reason) {
        String sql = " update doctor_info set reason = ? where doctorid = ? ";
        List<Object> params = new ArrayList<>();
        params.add(reason);
        params.add(doctorid);
        return update(sql, params) > 0;
    }


    /**
     * 修改诊疗医师
     *
     * @param docName
     * @param docPhotoUrl
     * @param titleId
     * @param hospitailid
     * @param dooTel
     * @param idCard
     * @param professional
     * @param introduction
     * @param gender
     * @param departmentId
     * @param doctorId
     * @return
     */
    public boolean updateClinics(String docName, String docPhotoUrl, int titleId, long hospitailid,
                                 String dooTel, String idCard, String professional,
                                 String introduction, int gender, int departmentId, long doctorId) {
        String sql = "update doctor_info set  " +
                "    doc_name                     = ?,  " +
                "    doc_photo_url                = ?,  " +
                "    title_id                     = ?,  " +
                "    hospitalid               = ?,  " +
                "    doo_tel                      = ?,  " +
                "    id_card                      = ?,  " +
//                "    prac_no                      = ?,  " +
                "    professional                 = ?,  " +
                "    introduction                 = ?,  " +
                "    gender                       = ?,  " +
                "    department_id                = ?  " +
                "where doctorid = ?";
        List<Object> params = new ArrayList<>();
        params.add(docName);
        params.add(docPhotoUrl);
        params.add(titleId);
        params.add(hospitailid);
        params.add(dooTel);
        params.add(idCard);
//        params.add(pracNo);
        params.add(professional);
        params.add(introduction);
        params.add(gender);
        params.add(departmentId);
        params.add(doctorId);
        return update(sql, params) > 0;

    }

    /**
     * 修改顾问医师
     *
     * @param docName
     * @param docPhotoUrl
     * @param titleId
     * @param hospitailid
     * @param dooTel
     * @param idCard
     * @param
     * @param professional
     * @param introduction
     * @param gender
     * @param departmentId
     * @param doctorId
     * @return
     */
    public boolean updateAdviser(int examine, String docName, String docPhotoUrl, int titleId, long hospitailid,
                                 String dooTel, String idCard, String professional,
                                 String introduction, int gender, int departmentId, long doctorId) {
        String sql = "update doctor_info set  " +
                "    doc_name                     = ?,  " +
                "    doc_photo_url                = ?,  " +
                "    title_id                     = ?,  " +
                "    hospitalid               = ?,  " +
                "    doo_tel                      = ?,  " +
                "    id_card                      = ?,  " +
//                "    prac_no                      = ?,  " +
                "    professional                 = ?,  " +
                "    introduction                 = ?,  " +
                "    gender                       = ?,  " +
                "    examine                       = ?,  " +
                "    department_id                = ?  " +

                "where doctorid = ?";
        List<Object> params = new ArrayList<>();
        params.add(docName);
        params.add(docPhotoUrl);
        params.add(titleId);
        params.add(hospitailid);
        params.add(dooTel);
        params.add(idCard);
//        params.add(pracNo);
        params.add(professional);
        params.add(introduction);
        params.add(gender);
        params.add(examine);
        params.add(departmentId);

        params.add(doctorId);
        return update(sql, params) > 0;

    }


    /**
     * 修改审方医师基本信息
     *
     * @param docName
     * @param docPhotoUrl
     * @param titleId
     * @param hospitailid
     * @param dooTel
     * @param idCard
     * @param professional
     * @param introduction
     * @param gender
     * @param departmentId
     * @param doctorId
     * @return
     */
    public boolean updateTrialParty(int examine, int doctype, String docName, String docPhotoUrl, int titleId,
                                    long hospitailid,
                                    String dooTel, String idCard, String professional,
                                    String introduction, int gender, int departmentId, long doctorId) {
//select doctorid,doo_tel,doc_photo_url,doc_name,id_card,gender,hospitalid,department_id,title_id,prac_no,professional,introduction  from doctor_info
//where IFNULL(delflag,0) = 0
        String sql = "update doctor_info set  " +
                "    doc_name                     = ?,  " +
                "    doc_photo_url                = ?,  " +
                "    title_id                     = ?,  " +
                "    hospitalid               = ?,  " +
                "    doo_tel                      = ?,  " +
                "    id_card                      = ?,  " +
//                "    prac_no                      = ?,  " +
                "    professional                 = ?,  " +
                "    introduction                 = ?,  " +
                "    gender                       = ?,  " +
                "    doc_type                       = ?,  " +
                "    examine                       = ?,  " +
                "    department_id                = ?  " +

                "where doctorid = ?";
        List<Object> params = new ArrayList<>();
        params.add(docName);
        params.add(docPhotoUrl);
        params.add(titleId);
        params.add(hospitailid);
        params.add(dooTel);
        params.add(idCard);
//        params.add(pracNo);
        params.add(professional);
        params.add(introduction);
        params.add(gender);
        params.add(doctype);
        params.add(examine);
        params.add(departmentId);

        params.add(doctorId);
        return update(sql, params) > 0;

    }

    /**
     * 新增诊疗医师
     *
     * @param docName      医生姓名
     * @param titleId      职称中文名
     * @param idCard       医生身份证号
     * @param professional 医师擅长专业
     *                     //* @param tempDoctorId 医师ID
     * @return
     */
    public long addClinics(String docName, String docPhotoUrl, int titleId, long hospitalid, String dooTel, String
            idCard,
                           String professional, String introduction, int gender, int departmentId) {
        long doctorId = getId("doctor_info");
        String inDocCode = UnixUtil.addZeroForNum(doctorId, 2);
        String sql = "insert into doctor_info (" +
                "                         doctorid,  " +
                "                         doc_name,  " +
                "                         doc_photo_url, " +
                "                         title_id,  " +
                "                         hospitalid,  " +
                "                         doo_tel,  " +
                "                         id_card,  " +
//                "                         prac_no,  " +
                "                         professional,  " +
                "                         introduction,  " +
                "                         gender,  " +
                "                         department_id ," +
                "                         examine,    " +
                "                         doc_type," +
                "                         in_doc_code," +
                "                         create_time)" +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(docName);
        params.add(docPhotoUrl);
        params.add(titleId);
        params.add(hospitalid);
        params.add(dooTel);
        params.add(idCard);
//        params.add(pracNo);
        params.add(professional);
        params.add(introduction);
        params.add(gender);
        params.add(departmentId);
        params.add(DoctorExamineEnum.successfulCertified.getCode());
        params.add(DoctorTypeEnum.DctorDiagnosis.getCode());
        params.add(inDocCode);
        params.add(UnixUtil.getNowTimeStamp());
        insert(sql, params);
        return doctorId;
    }

    /**
     * 新增专家医师基本信息
     *
     * @param docName      医生姓名
     * @param titleId      职称中文名
     * @param idCard       医生身份证号
     * @param professional 医师擅长专业
     *                     //* @param tempDoctorId 医师ID
     * @return
     */
    public long addExpertPhysician(String docName, String docPhotoUrl, int titleId, long hospitalid, String
            dooTel, String idCard,
                                   String professional, String introduction, int gender, int departmentId) {
        long doctorId = getId("doctor_info");
        String inDocCode = UnixUtil.addZeroForNum(doctorId, 2);
        String sql = "insert into doctor_info (" +
                "                         doctorid,  " +
                "                         doc_name,  " +
                "                         doc_photo_url, " +
                "                         title_id,  " +
                "                         hospitalid,  " +
                "                         doo_tel,  " +
                "                         id_card,  " +
//                "                         prac_no,  " +
                "                         professional,  " +
                "                         introduction,  " +
                "                         gender,  " +
                "                         department_id ," +
                "                         examine,    " +
                "                         doc_type," +
                "                         in_doc_code," +
                "                         create_time   )  " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(docName);
        params.add(docPhotoUrl);
        params.add(titleId);
        params.add(hospitalid);
        params.add(dooTel);
        params.add(idCard);
//        params.add(pracNo);
        params.add(professional);
        params.add(introduction);
        params.add(gender);
        params.add(departmentId);
        params.add(DoctorExamineEnum.Certification.getCode());
        params.add(DoctorTypeEnum.DoctorExpert.getCode());
        params.add(inDocCode);
        params.add(UnixUtil.getNowTimeStamp());
        update(sql, params);
        return doctorId;
    }

    /**
     * 新增审方医师基本信息
     *
     * @param docName      医生姓名
     * @param titleId      职称中文名
     * @param idCard       医生身份证号
     *                     //     * @param pracNo       医师执业号
     * @param professional 医师擅长专业
     *                     //* @param tempDoctorId 医师ID
     * @return
     */
    public long addTrialPartys(int doctype, String docName, String docPhotoUrl, int titleId,
                               long hospitalid, String dooTel, String idCard,
                               String professional, String introduction, int gender, int departmentId) {
        long doctorId = getId("doctor_info");
        String inDocCode = UnixUtil.addZeroForNum(doctorId, 2);
        String sql = "insert into doctor_info (" +
                "                         doctorid,  " +
                "                         doc_name,  " +
                "                         doc_photo_url, " +
                "                         title_id,  " +
                "                         hospitalid,  " +
                "                         doo_tel,  " +
                "                         id_card,  " +
//                "                         prac_no,  " +
                "                         professional,  " +
                "                         introduction,  " +
                "                         gender,  " +
                "                         department_id ," +
                "                         examine,    " +
                "                         doc_type," +
                "                         in_doc_code," +
                "                         create_time   )  " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(docName);
        params.add(docPhotoUrl);
        params.add(titleId);
        params.add(hospitalid);
        params.add(dooTel);
        params.add(idCard);
//        params.add(pracNo);
        params.add(professional);
        params.add(introduction);
        params.add(gender);
        params.add(departmentId);
        params.add(DoctorExamineEnum.Certification.getCode());
        params.add(doctype);
        params.add(inDocCode);
        params.add(UnixUtil.getNowTimeStamp());
        update(sql, params);
        return doctorId;
    }


    /**
     * 专家医师
     *
     * @param docName
     * @param dooTel
     * @param workInstName
     * @param titleId
     * @param departmentId
     * @param doctorStart
     * @param doctorEnd
     * @param examine
     * @param graphicStatus
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getExpertPhysicianList(String docName, String dooTel, String
            workInstName, String titleId, String departmentId, long doctorStart, long doctorEnd, int examine,
                                                            int graphicStatus, int pageIndex, int pageSize) {
        String sql = " select d.doctorid, " +
                "       doc_name                         as docname, " +
                "       reason, " +
                "       doc_photo_url                    as docphotourl, " +
                "       authentication_time              as authenticationtime, " +
                "       doc_type                         as doctype, " +
                "       title_id                         as titleid, " +
                "       cdt.value                        as titlename, " +
                "       work_inst_name                   as workinstname, " +
                "       work_inst_code                   as workinstcode, " +
                "       doo_tel                          as dootel, " +
                "       id_card                          as idcard, " +
                "       prac_no                          as pracno, " +
                "       prac_rec_date                    as pracrecdate, " +
                "       prac_rec_date                    as pracrecdate, " +
                "       cert_no                          as certno, " +
                "       cert_rec_date                    as certrecdate, " +
                "       title_no                         as titleno, " +
                "       title_rec_date                   as titlerecdate, " +
                "       prac_type                        as practype, " +
                "       qualify_or_not                   as qualifyornot, " +
                "       professional, " +
                "       in_doc_code                         indoccode, " +
                "       sign_time                        as signtime, " +
                "       d.create_time                    as createtime, " +
                "       sign_life                        as signlife, " +
                "       employ_file_url                  as employfileurl, " +
                "       credit_level                     as creditlevel, " +
                "       occu_level                       as occulevel, " +
                "       digital_sign_url                 as digitalsignurl, " +
                "       doc_penalty_points               as docpenaltypoints, " +
                "       yc_record_flag                   as ycrecordflag, " +
                "       hos_confirm_flag                 as hosconfirmflag, " +
                "       yc_pres_record_flag              as ycpresrecordflag, " +
                "       prac_scope                       as pracscope, " +
                "       prac_scope_approval              as pracscopeapproval, " +
                "       agree_terms                         agreeterms, " +
                "       doc_multi_sited_date_start       as docmultisiteddatestart, " +
                "       doc_multi_sited_date_end         as docmultisiteddateend, " +
                "       hos_opinion                      as hosopinion, " +
                "       hos_digital_sign                 as hosdigitalsign, " +
                "       hos_opinion_date                 as hosopiniondate, " +
                "       doc_multi_sited_date_promise     as docmultisiteddatepromise, " +
                "       introduction, " +
                "       ifnull(gender, 0)                   gender, " +
                "       department_id                    as departmentid, " +
                "       cdp.value                        as departmentname, " +
                "       ifnull(d.examine, 0)             as examine, " +
                "       ifnull(dmplqa.whetheropen, 0)    as qaopen, " +
                "       ifnull(dmplphone.whetheropen, 0) as phoneopen " +
                " from doctor_info d " +
                "       left join code_department cdp on d.department_id = cdp.id " +
                "       left join code_doctor_title cdt on d.title_id = cdt.id " +
                "       left join doc_med_price_list dmplqa on dmplqa.doctorid = d.doctorid and dmplqa.med_class_id = 2 " +
                "       left join doc_med_price_list dmplphone on dmplphone.doctorid = d.doctorid and dmplphone.med_class_id = 3 " +
                " where ifnull(d.delflag, 0) = 0 and ((doc_type = ? and examine = ?) or (doc_type = ? and examine in (?,?,?))) ";
        List<Object> params = new ArrayList<>();
        params.add(DoctorTypeEnum.DoctorExpert.getCode());
        params.add(DoctorExamineEnum.certificationSuccess.getCode());
        params.add(DoctorTypeEnum.DctorDiagnosis.getCode());
        params.add(DoctorExamineEnum.Certification.getCode());
        params.add(DoctorExamineEnum.authenticationFailed.getCode());
        params.add(DoctorExamineEnum.auditSuccess.getCode());
        if (!StrUtil.isEmpty(docName)) {
            sql += " and d.doc_name like ?";
            params.add(String.format("%%%s%%", docName));
        }
        if (!StrUtil.isEmpty(dooTel)) {
            sql += " and d.doo_tel like ?";
            params.add(String.format("%%%s%%", dooTel));
        }
        if (!StrUtil.isEmpty(workInstName)) {
            sql += " and d.work_inst_name like ?";
            params.add(String.format("%%%s%%", workInstName));
        }
        if (!StrUtil.isEmpty(titleId)) {
            sql += " and  cdt.id = ?";
            params.add(String.format("%s", titleId));
        }
        if (!StrUtil.isEmpty(departmentId)) {
            sql += " and cdp.id = ?";
            params.add(String.format("%s", departmentId));
        }
        if (doctorStart != 0 && doctorEnd != 0) {
            sql += " and sign_time between ? and ?";
            params.add(String.format("%s", doctorStart));
            params.add(String.format("%s", doctorEnd));
        }
        if (examine != 0) {
            sql += " and d.examine=?";
            params.add(String.format("%s", examine));
        }
        if (graphicStatus != -1) {
            sql += " and ifnull(dmplqa.whetheropen,0) = ? ";
            params.add(String.format("%s", graphicStatus));
        }
        return query(pageSql(sql, " order by FIELD(d.examine, 5, 2, 7, 6),d.create_time desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (null != map) {
//                map.put("doctype", DoctorTypeEnum.getValue(ModelUtil.getInt(map, "doctype")).getMessage());
            }
            return map;
        });


    }


    public long getExpertPhysicianListCount(String docName, String dooTel, String workInstName, String
            titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus) {
        String sql = " select count(d.doctorid) count " +
                " from doctor_info d " +
                "       left join code_department cdp on d.department_id = cdp.id " +
                "       left join code_doctor_title cdt on d.title_id = cdt.id " +
                "       left join doc_med_price_list dmplqa on dmplqa.doctorid = d.doctorid and dmplqa.med_class_id = 2 " +
                "       left join doc_med_price_list dmplphone on dmplphone.doctorid = d.doctorid and dmplphone.med_class_id = 3 " +
                " where ifnull(d.delflag, 0) = 0 and ((doc_type = ? and examine = ?) or (doc_type = ? and examine in (?,?,?))) ";
        List<Object> params = new ArrayList<>();
        params.add(DoctorTypeEnum.DoctorExpert.getCode());
        params.add(DoctorExamineEnum.certificationSuccess.getCode());
        params.add(DoctorTypeEnum.DctorDiagnosis.getCode());
        params.add(DoctorExamineEnum.Certification.getCode());
        params.add(DoctorExamineEnum.authenticationFailed.getCode());
        params.add(DoctorExamineEnum.auditSuccess.getCode());
        if (!StrUtil.isEmpty(docName)) {
            sql += " and d.doc_name like ?";
            params.add(String.format("%%%s%%", docName));
        }
        if (!StrUtil.isEmpty(dooTel)) {
            sql += " and d.doo_tel like ?";
            params.add(String.format("%%%s%%", dooTel));
        }
        if (!StrUtil.isEmpty(workInstName)) {
            sql += " and d.work_inst_name like ?";
            params.add(String.format("%%%s%%", workInstName));
        }
        if (!StrUtil.isEmpty(titleId)) {
            sql += " and  cdt.id = ?";
            params.add(String.format("%s", titleId));
        }
        if (!StrUtil.isEmpty(departmentId)) {
            sql += " and cdp.id = ?";
            params.add(String.format("%s", departmentId));
        }
        if (doctorStart != 0 && doctorEnd != 0) {
            sql += " and sign_time between ? and ?";
            params.add(String.format("%s", doctorStart));
            params.add(String.format("%s", doctorEnd));
        }
        if (examine != 0) {
            sql += " and d.examine=?";
            params.add(String.format("%s", examine));
        }
        if (graphicStatus != -1) {
            sql += " and ifnull(dmplqa.whetheropen,0) = ? ";
            params.add(String.format("%s", graphicStatus));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);


    }


    /**
     * 顾问列表
     *
     * @param docName
     * @param dooTel
     * @param workInstName
     * @param titleId
     * @param departmentId
     * @param doctorStart
     * @param doctorEnd
     * @param examine
     * @param graphicStatus
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getAdviserList(String docName, String dooTel, String workInstName, String
            titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus, int pageIndex,
                                                    int pageSize) {
        String sql = " select d.doctorid, " +
                "       doc_name                         as docname, " +
                "       d.in_doc_code                         as indoccode, " +
                "       reason, " +
                "       doo_tel                          as dootel, " +
                "       d.create_time                    as createtime, " +
                "       ifnull(d.examine, 0)             as examine " +
                " from doctor_info d " +
                "       left join code_department cdp on d.department_id = cdp.id " +
                "       left join code_doctor_title cdt on d.title_id = cdt.id " +
                "       left join doc_med_price_list dmplqa on dmplqa.doctorid = d.doctorid and dmplqa.med_class_id = 2 " +
                "       left join doc_med_price_list dmplphone on dmplphone.doctorid = d.doctorid and dmplphone.med_class_id = 3 " +
                " where ifnull(d.delflag, 0) = 0 and doc_type = 4 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(docName)) {
            sql += " and d.doc_name like ?";
            params.add(String.format("%%%s%%", docName));
        }
        if (!StrUtil.isEmpty(dooTel)) {
            sql += " and d.doo_tel like ?";
            params.add(String.format("%%%s%%", dooTel));
        }
        if (!StrUtil.isEmpty(workInstName)) {
            sql += " and d.work_inst_name like ?";
            params.add(String.format("%%%s%%", workInstName));
        }
        if (!StrUtil.isEmpty(titleId)) {
            sql += " and  cdt.id = ?";
            params.add(String.format("%s", titleId));
        }
        if (!StrUtil.isEmpty(departmentId)) {
            sql += " and cdp.id = ?";
            params.add(String.format("%s", departmentId));
        }
        if (doctorStart != 0 && doctorEnd != 0) {
            sql += " and sign_time between ? and ?";
            params.add(String.format("%s", doctorStart));
            params.add(String.format("%s", doctorEnd));
        }
        if (examine != 0) {
            sql += " and d.examine=?";
            params.add(String.format("%s", examine));
        }
        if (graphicStatus != -1) {
            sql += " and ifnull(dmplqa.whetheropen,0) = ? ";
            params.add(String.format("%s", graphicStatus));
        }
        return query(pageSql(sql, " order by FIELD(d.examine, 1, 2, 3),d.create_time desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (null != map) {
                map.put("doctype", DoctorTypeEnum.getValue(ModelUtil.getInt(map, "doctype")).getMessage());
            }
            return map;
        });


    }

    public Map<String, Object> getQuestionDoc() {
        String sql = "select doctorid from doctor_info where ifnull(delflag,0)=0 and  doc_type = 4 and  examine=2 order by rand() limit 1";
        return queryForMap(sql);
    }


    public long getAdviserListCount(String docName, String dooTel, String workInstName, String titleId, String
            departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus) {
        String sql = " select count(d.doctorid) count " +
                " from doctor_info d " +
                "       left join code_department cdp on d.department_id = cdp.id " +
                "       left join code_doctor_title cdt on d.title_id = cdt.id " +
                "       left join doc_med_price_list dmplqa on dmplqa.doctorid = d.doctorid and dmplqa.med_class_id = 2 " +
                "       left join doc_med_price_list dmplphone on dmplphone.doctorid = d.doctorid and dmplphone.med_class_id = 3 " +
                " where ifnull(d.delflag, 0) = 0 and doc_type = 4 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(docName)) {
            sql += " and d.doc_name like ?";
            params.add(String.format("%%%s%%", docName));
        }
        if (!StrUtil.isEmpty(dooTel)) {
            sql += " and d.doo_tel like ?";
            params.add(String.format("%%%s%%", dooTel));
        }
        if (!StrUtil.isEmpty(workInstName)) {
            sql += " and d.work_inst_name like ?";
            params.add(String.format("%%%s%%", workInstName));
        }
        if (!StrUtil.isEmpty(titleId)) {
            sql += " and  cdt.id = ?";
            params.add(String.format("%s", titleId));
        }
        if (!StrUtil.isEmpty(departmentId)) {
            sql += " and cdp.id = ?";
            params.add(String.format("%s", departmentId));
        }
        if (doctorStart != 0 && doctorEnd != 0) {
            sql += " and sign_time between ? and ?";
            params.add(String.format("%s", doctorStart));
            params.add(String.format("%s", doctorEnd));
        }
        if (examine != 0) {
            sql += " and d.examine=?";
            params.add(String.format("%s", examine));
        }
        if (graphicStatus != -1) {
            sql += " and ifnull(dmplqa.whetheropen,0) = ? ";
            params.add(String.format("%s", graphicStatus));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);


    }


    /**
     * 新增顾问
     *
     * @param docName      医生姓名
     * @param titleId      职称中文名
     * @param idCard       医生身份证号
     *                     //     * @param pracNo       医师执业号
     * @param professional 医师擅长专业
     *                     //* @param tempDoctorId 医师ID
     * @return
     */
    public long addAdviser(String docName, String docPhotoUrl, int titleId, long hospitalid, String dooTel, String
            idCard,
                           String professional, String introduction, int gender, int departmentId) {
        long doctorId = getId("doctor_info");
        String inDocCode = UnixUtil.addZeroForNum(doctorId, 2);
        String sql = "insert into doctor_info (" +
                "                         doctorid,  " +
                "                         doc_name,  " +
                "                         doc_photo_url, " +
                "                         title_id,  " +
                "                         hospitalid,  " +
                "                         doo_tel,  " +
                "                         id_card,  " +
//                "                         prac_no,  " +
                "                         professional,  " +
                "                         introduction,  " +
                "                         gender,  " +
                "                         department_id ," +
                "                         examine,    " +
                "                         doc_type," +
                "                         in_doc_code," +
                "                         create_time)" +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(docName);
        params.add(docPhotoUrl);
        params.add(titleId);
        params.add(hospitalid);
        params.add(dooTel);
        params.add(idCard);
//        params.add(pracNo);
        params.add(professional);
        params.add(introduction);
        params.add(gender);
        params.add(departmentId);
        params.add(DoctorExamineEnum.successfulCertified.getCode());
        params.add(DoctorTypeEnum.DoctorAdviser.getCode());
        params.add(inDocCode);
        params.add(UnixUtil.getNowTimeStamp());
        insert(sql, params);
        return doctorId;
    }


    /**
     * 审方医师
     *
     * @param docName
     * @param dooTel
     * @param workInstName
     * @param titleId
     * @param departmentId
     * @param doctorStart
     * @param doctorEnd
     * @param examine
     * @param graphicStatus
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getTrialPartyList(String docName, String dooTel, String workInstName, String
            titleId, String departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus, int pageIndex,
                                                       int pageSize) {
        String sql = " select d.doctorid, " +
                "       doc_name                         as docname, " +
                "       reason, " +
                "       doc_photo_url                    as docphotourl, " +
                "       doc_type                         as doctype, " +
                "       title_id                         as titleid, " +
                "       cdt.value                        as titlename, " +
                "       work_inst_name                   as workinstname, " +
                "       work_inst_code                   as workinstcode, " +
                "       doo_tel                          as dootel, " +
                "       id_card                          as idcard, " +
                "       prac_no                          as pracno, " +
                "       prac_rec_date                    as pracrecdate, " +
                "       prac_rec_date                    as pracrecdate, " +
                "       cert_no                          as certno, " +
                "       cert_rec_date                    as certrecdate, " +
                "       title_no                         as titleno, " +
                "       title_rec_date                   as titlerecdate, " +
                "       prac_type                        as practype, " +
                "       qualify_or_not                   as qualifyornot, " +
                "       professional, " +
                "       in_doc_code                         indoccode, " +
                "       sign_time                        as signtime, " +
                "       d.create_time                    as createtime, " +
                "       sign_life                        as signlife, " +
                "       employ_file_url                  as employfileurl, " +
                "       credit_level                     as creditlevel, " +
                "       occu_level                       as occulevel, " +
                "       digital_sign_url                 as digitalsignurl, " +
                "       doc_penalty_points               as docpenaltypoints, " +
                "       yc_record_flag                   as ycrecordflag, " +
                "       hos_confirm_flag                 as hosconfirmflag, " +
                "       yc_pres_record_flag              as ycpresrecordflag, " +
                "       prac_scope                       as pracscope, " +
                "       prac_scope_approval              as pracscopeapproval, " +
                "       agree_terms                         agreeterms, " +
                "       doc_multi_sited_date_start       as docmultisiteddatestart, " +
                "       doc_multi_sited_date_end         as docmultisiteddateend, " +
                "       hos_opinion                      as hosopinion, " +
                "       hos_digital_sign                 as hosdigitalsign, " +
                "       hos_opinion_date                 as hosopiniondate, " +
                "       doc_multi_sited_date_promise     as docmultisiteddatepromise, " +
                "       introduction, " +
                "       ifnull(gender, 0)                   gender, " +
                "       department_id                    as departmentid, " +
                "       cdp.value                        as departmentname, " +
                "       ifnull(d.examine, 0)             as examine, " +
                "       ifnull(dmplqa.whetheropen, 0)    as qaopen, " +
                "       ifnull(dmplphone.whetheropen, 0) as phoneopen " +
                " from doctor_info d " +
                "       left join code_department cdp on d.department_id = cdp.id " +
                "       left join code_doctor_title cdt on d.title_id = cdt.id " +
                "       left join doc_med_price_list dmplqa on dmplqa.doctorid = d.doctorid and dmplqa.med_class_id = 2 " +
                "       left join doc_med_price_list dmplphone on dmplphone.doctorid = d.doctorid and dmplphone.med_class_id = 3 " +
                " where ifnull(d.delflag, 0) = 0 and doc_type in (1,5) ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(docName)) {
            sql += " and d.doc_name like ?";
            params.add(String.format("%%%s%%", docName));
        }
        if (!StrUtil.isEmpty(dooTel)) {
            sql += " and d.doo_tel like ?";
            params.add(String.format("%%%s%%", dooTel));
        }
        if (!StrUtil.isEmpty(workInstName)) {
            sql += " and d.work_inst_name like ?";
            params.add(String.format("%%%s%%", workInstName));
        }
        if (!StrUtil.isEmpty(titleId)) {
            sql += " and  cdt.id = ?";
            params.add(String.format("%s", titleId));
        }
        if (!StrUtil.isEmpty(departmentId)) {
            sql += " and cdp.id = ?";
            params.add(String.format("%s", departmentId));
        }
        if (doctorStart != 0 && doctorEnd != 0) {
            sql += " and sign_time between ? and ?";
            params.add(String.format("%s", doctorStart));
            params.add(String.format("%s", doctorEnd));
        }
        if (examine != 0) {
            sql += " and d.examine=?";
            params.add(String.format("%s", examine));
        }
        if (graphicStatus != -1) {
            sql += " and ifnull(dmplqa.whetheropen,0) = ? ";
            params.add(String.format("%s", graphicStatus));
        }
        return query(pageSql(sql, " order by FIELD(d.examine, 5, 7, 6),d.create_time desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (null != map) {
//                map.put("doctype", DoctorTypeEnum.getValue(ModelUtil.getInt(map, "doctype")).getMessage());
            }
            return map;
        });


    }

    public long getTrialPartyListCount(String docName, String dooTel, String workInstName, String titleId, String
            departmentId, long doctorStart, long doctorEnd, int examine, int graphicStatus) {
        String sql = " select count(d.doctorid) count " +
                " from doctor_info d " +
                "       left join code_department cdp on d.department_id = cdp.id " +
                "       left join code_doctor_title cdt on d.title_id = cdt.id " +
                "       left join doc_med_price_list dmplqa on dmplqa.doctorid = d.doctorid and dmplqa.med_class_id = 2 " +
                "       left join doc_med_price_list dmplphone on dmplphone.doctorid = d.doctorid and dmplphone.med_class_id = 3 " +
                " where ifnull(d.delflag, 0) = 0 and doc_type = 1 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(docName)) {
            sql += " and d.doc_name like ?";
            params.add(String.format("%%%s%%", docName));
        }
        if (!StrUtil.isEmpty(dooTel)) {
            sql += " and d.doo_tel like ?";
            params.add(String.format("%%%s%%", dooTel));
        }
        if (!StrUtil.isEmpty(workInstName)) {
            sql += " and d.work_inst_name like ?";
            params.add(String.format("%%%s%%", workInstName));
        }
        if (!StrUtil.isEmpty(titleId)) {
            sql += " and  cdt.id = ?";
            params.add(String.format("%s", titleId));
        }
        if (!StrUtil.isEmpty(departmentId)) {
            sql += " and cdp.id = ?";
            params.add(String.format("%s", departmentId));
        }
        if (doctorStart != 0 && doctorEnd != 0) {
            sql += " and sign_time between ? and ?";
            params.add(String.format("%s", doctorStart));
            params.add(String.format("%s", doctorEnd));
        }
        if (examine != -1) {
            sql += " and d.examine=?";
            params.add(String.format("%s", examine));
        }
        if (graphicStatus != -1) {
            sql += " and ifnull(dmplqa.whetheropen,0) = ? ";
            params.add(String.format("%s", graphicStatus));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);


    }


    /**
     * 审方
     *
     * @param docName                  医生姓名
     * @param docPhoto                 医生头像
     * @param docType                  医生类型
     * @param titleId                  职称中文名
     * @param workInstCode             线下医院code
     * @param workInstName             线下医院名称
     * @param idCard                   医生身份证号
     * @param pracNo                   医师执业号
     * @param pracRecDate              执业证取得时间（YYYY-MM-DD）
     * @param certNo                   医师资格证号
     * @param certRecDate              资格证取得时间
     * @param titleNo                  医师职称号
     * @param titleRecDate             职称证取得时间
     * @param pracType                 医师执业类别
     * @param qualifyOrNot             考核是否合格 是 | 否
     * @param professional             医师擅长专业
     * @param signTime                 签约时间
     * @param signLife                 签约年限
     * @param employFile               聘任合同
     * @param creditLevel              信用评级
     * @param occuLevel                职业评级
     * @param digitalSign              数字签名留样
     * @param docPenaltyPoints         医师评分
     *                                 //     * @param ycRecordFlag             银川是否备案
     *                                 //     * @param hosConfirmFlag           医院是否备案
     *                                 //     * @param ycPresRecordFlag         是否有开处方的权限
     * @param pracScope                医师执业范围
     * @param pracScopeApproval        审批局规定的医师执业范围
     * @param agreeTerms               医师是否同意多点执业备案信息表上的条款，同意填“是”，不同意填“否” 0 是 1 否
     * @param docMultiSitedDateStart   医师多点执业起始时间
     * @param docMultiSitedDateEnd     医师多点执业起始时间
     * @param hosOpinion               申请拟执业医疗机构意见
     *                                 //     * @param hosDigitalSign           申请拟执业医疗机构-电子章
     * @param hosOpinionDate           申请拟执业医疗机构意见时间
     * @param docMultiSitedDatePromise 医师申请多点执业承诺时间
     * @param inDocCode                医师CODE
     * @param tempDoctorId             医师ID
     * @return
     */
    public long addTrialParty(String docName, String docPhoto, String docPhoneUrl, int docType, int titleId, String
            workInstCode,
                              String workInstName, long hospitalid, String dooTel, String idCard, String pracNo,
                              long pracRecDate, String certNo, long certRecDate, String titleNo,
                              long titleRecDate, String pracType, String qualifyOrNot, String professional,
                              long signTime, String signLife, String employFile, String employFileUrl, String creditLevel, String occuLevel,
                              String digitalSign, String digitalSignUrl, String docPenaltyPoints, String pracScope, String
                                      pracScopeApproval, int agreeTerms,
                              long docMultiSitedDateStart, long docMultiSitedDateEnd, String hosOpinion,
                              long hosOpinionDate, long docMultiSitedDatePromise, long agentid,
                              String introduction, int gender, int departmentId, String inDocCode, long tempDoctorId) {
        String sql = "insert into doctor_info (doctorid,  " +
                "                         doc_name,  " +
                "                         doc_photo,  " +
                "                         doc_photo_url, " +
                "                         doc_type,  " +
                "                         title_id,  " +
                "                         work_inst_code,  " +
                "                         work_inst_name,  " +
                "                         hospitalid,  " +
                "                         doo_tel,  " +
                "                         id_card,  " +

                "                         prac_no,  " +
                "                         prac_rec_date,  " +
                "                         cert_no,  " +
                "                         cert_rec_date,  " +
                "                         title_no,  " +
                "                         title_rec_date,  " +
                "                         prac_type,  " +
                "                         qualify_or_not,  " +
                "                         professional,  " +
                "                         in_doc_code,  " +

                "                         sign_time,  " +
                "                         sign_life,  " +
                "                         employ_file,  " +
                "                         employ_file_url,  " +
                "                         credit_level,  " +
                "                         occu_level,  " +
                "                         digital_sign,  " +
                "                         digital_sign_url,  " +
                "                         doc_penalty_points,  " +
//                "                         yc_record_flag,  " +
//
//                "                         hos_confirm_flag,  " +
//                "                         yc_pres_record_flag,  " +
                "                         prac_scope,  " +
                "                         prac_scope_approval,  " +
                "                         agree_terms,  " +
                "                         doc_multi_sited_date_start,  " +
                "                         doc_multi_sited_date_end,  " +
                "                         hos_opinion,  " +
//                "                         hos_digital_sign,  " +
                "                         hos_opinion_date,  " +

                "                         doc_multi_sited_date_promise,  " +
                "                         delflag,  " +
                "                         create_time,  " +
                "                         create_user,  " +
                "                         introduction,  " +
                "                         gender,  " +
                "                         department_id," +
                "                         examine )  " +
                "values (?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?, ?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(tempDoctorId);
        params.add(docName);
        params.add(docPhoto);
        params.add(docPhoneUrl);
        params.add(docType);
        params.add(titleId);
        params.add(workInstCode);
        params.add(workInstName);
        params.add(hospitalid);
        params.add(dooTel);
        params.add(idCard);

        params.add(pracNo);
        params.add(pracRecDate);
        params.add(certNo);
        params.add(certRecDate);
        params.add(titleNo);
        params.add(titleRecDate);
        params.add(pracType);
        params.add(qualifyOrNot);
        params.add(professional);
        params.add(inDocCode);

        params.add(signTime);
        params.add(signLife);
        params.add(employFile);
        params.add(employFileUrl);
        params.add(creditLevel);
        params.add(occuLevel);
        params.add(digitalSign);
        params.add(digitalSignUrl);
        params.add(docPenaltyPoints);
//        params.add(ycRecordFlag);
//
//        params.add(hosConfirmFlag);
//        params.add(ycPresRecordFlag);
        params.add(pracScope);
        params.add(pracScopeApproval);
        params.add(agreeTerms);
        params.add(docMultiSitedDateStart);
        params.add(docMultiSitedDateEnd);
        params.add(hosOpinion);
//        params.add(hosDigitalSign);
        params.add(hosOpinionDate);

        params.add(docMultiSitedDatePromise);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(agentid);
        params.add(introduction);
        params.add(gender);
        params.add(departmentId);
        params.add(5);
        update(sql, params);
        return tempDoctorId;
    }


    /**
     * 顾问详情的医生下拉框（专家认证成功的）
     *
     * @return
     */
    public List<Map<String, Object>> doctorSelect(String phoneName) {
        String sql = " select doctorid id,concat_ws(' ',doo_tel,doc_name) name from  doctor_info " +
                " where IFNULL(delflag,0) = 0 and doc_type = ? and examine = ? ";
        List<Object> params = new ArrayList<>();
        params.add(DoctorTypeEnum.DoctorExpert.getCode());
        params.add(DoctorExamineEnum.certificationSuccess.getCode());
        if (!StrUtil.isEmpty(phoneName)) {
            sql += " and (doo_tel like ? or doc_name like ?) ";
            params.add(String.format("%%%S%%", phoneName));
            params.add(String.format("%%%S%%", phoneName));
        }
        return queryForList(sql, params);
    }


    /**
     * 新增顾问医生拓展表，绑定认证成功的专家医师
     *
     * @param doctorid
     * @param expertid
     * @return
     */
    public boolean insertAdviserDoctorExtends(long doctorid, long expertid) {
        String sql = " insert into doctor_extends(doctorid,integral,create_time,platform,pid) values(?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(0);
        params.add(expertid);
        return insert(sql, params) > 0;
    }

    /**
     * 修改顾问医生拓展表，绑定认证成功的专家医师
     *
     * @param doctorid
     * @param expertid
     * @return
     */
    public boolean updateAdviserDoctorExtends(long doctorid, long expertid) {
        String sql = " update doctor_extends set pid = ? where doctorid = ? ";
        List<Object> params = new ArrayList<>();
        params.add(expertid);
        params.add(doctorid);
        return update(sql, params) > 0;
    }


//    /**
//     * 新增医生拓展表
//     *
//     * @param doctorid
//     * @return
//     */
//    public boolean insertDoctorExtends(long doctorid) {
//        String sql = " insert into doctor_extends(doctorid,integral,create_time,platform) values(?,?,?,?) ";
//        List<Object> params = new ArrayList<>();
//        params.add(doctorid);
//        params.add(0);
//        params.add(UnixUtil.getNowTimeStamp());
//        params.add(0);
//        return insert(sql, params) > 0;
//    }

    /**
     * 删除医生拓展表
     *
     * @param doctorid
     * @return
     */
    public boolean delDoctorExtends(long doctorid, long agentId) {
        String sql = " update doctor_extends set delflag = 1,modify_user=?,modify_time=? where doctorid = ? ";
        List<Object> params = new ArrayList<>();
        params.add(agentId);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorid);
        return update(sql, params) > 0;
    }


    /**
     * 查询医院
     *
     * @param id
     * @return
     */
    public Map<String, Object> getHospitalById(long id) {
        String sql = " select hospital_code hospitalcode,hospital_name hospitalname from hospital where id = ? ";
        return queryForMap(sql, id);
    }

    /**
     * 修改医生，医院code医院name
     *
     * @param doctorid
     * @param hospitalcode
     * @param hospitalname
     * @return
     */
    public boolean updateDoctorCodeName(long doctorid, String hospitalcode, String hospitalname) {
        String sql = " update doctor_info set work_inst_code=?,work_inst_name=? where doctorid = ? ";
        List<Object> params = new ArrayList<>();
        params.add(hospitalcode);
        params.add(hospitalname);
        params.add(doctorid);
        return update(sql, params) > 0;
    }


    public Map<String, Object> getDoctorExamine(long doctorid) {
        String sql = " select examine,reason from doctor_info where doctorid=? ";
        return queryForMap(sql, doctorid);
    }

    public boolean delOtherScheduling(long doctorid) {
        String sql = " update doctor_scheduling " +
                "set delflag=1 " +
                "where doctorid = ? " +
                "  and ifnull(delflag, 0) = 0 " +
                "  and ifnull(issubscribe, 0) = 0 " +
                "  and visiting_start_time > unix_timestamp() * 1000 ";
        return update(sql, doctorid) > 0;
    }


    public boolean getOtherScheduling(long doctorid) {
        String sql = " select id from doctor_scheduling " +
                " where doctorid = ? " +
                "  and ifnull(delflag, 0) = 0 " +
                "  and issubscribe = 1 " +
                "  and visiting_start_time > unix_timestamp() * 1000 ";
        return queryForList(sql, doctorid).size() > 0;
    }

    public boolean delOtherDuty(long doctorid) {
        String sql = " update doctor_onduty " +
                "set delflag=1 " +
                "where doctorid = ? " +
                "  and ifnull(delflag, 0) = 0 " +
                "  and ifnull(issubscribe, 0) = 0 " +
                "  and visiting_start_time > unix_timestamp() * 1000 ";
        return update(sql, doctorid) > 0;
    }

    public List<Map<String, Object>> findResults(long doctorid) {
        String sql = "select visiting_start_time,issubscribe from doctor_onduty where ifnull(delflag,0)=0 and visiting_start_time >= ? and doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorid);
        return queryForList(sql, params);
    }

    public boolean udate() {
        String sql = "select id,doctorid,visiting_start_time,visiting_end_time from doctor_onduty where issubscribe is null and ifnull(delflag,0)=0 and visiting_end_time>unix_timestamp( ) * 1000 and isupdate is null ";
        List<Map<String, Object>> maps = queryForList(sql);
        Map<String, Object> settTime = getSetingTime();
        int interval_time = ModelUtil.getInt(settTime, "interval_time");//间隔时间
//        int interval_time=5*60;
        String sqlNew = "insert into doctor_onduty (visiting_start_time,visiting_end_time,create_time,examine_state,doctorid,isupdate) values (?,?,?,?,?,?)";
        String sqlEn = "update doctor_onduty set delflag=1 where id=?";
        List<Object> params = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            long startTime = ModelUtil.getLong(map, "visiting_start_time");
            long doctorid = ModelUtil.getLong(map, "doctorid");
            long endTime = ModelUtil.getLong(map, "visiting_end_time");
            long id = ModelUtil.getLong(map, "id");
            while (true) {
                long realEndTime = startTime + interval_time * 60 * 1000;
                if (realEndTime > endTime) {
                    break;
                }
                params.clear();
                params.add(startTime);
                params.add(realEndTime);
                params.add(UnixUtil.getNowTimeStamp());
                params.add(2);
                params.add(doctorid);
                params.add(1);
                insert(sqlNew, params);
                startTime = realEndTime;
            }
            update(sqlEn, id);
        }


        return true;
    }

    public Map<String, Object> getSetingTime() {
        String sql = "select start_time,end_time,interval_time from video_time where ifnull(delflag,0)=0 and ordertype=2 limit 1";
        return queryForMap(sql);
    }

    public List<Map<String, Object>> get() {
        String sql = "select id,cardno from user_account where ifnull(delflag,0)=0";
        List<Map<String, Object>> list = queryForList(sql);
        return list;
    }

    public boolean updateSql(long id, int age) {
        String sql = "update user_account set age=? where id=?";
        List<Object> list = new ArrayList<>();
        list.add(age);
        list.add(id);
        return update(sql, list) > 0;
    }

    public List<Map<String, Object>> getUserOrderList(long userid, long doctorid, int pageIndex, int pageSize) {
        String sql = "select m.*,dp.prescriptionid " +
                "from ( " +
                "       select dpo.id, " +
                "              dpo.diagnosis, " +
                "              dpo.dis_describe   disdescribe, " +
                "              dpo.create_time as createtime, " +
                "              dpo.actualmoney    price, " +
                "              ue.id              evaluateid, " +
                "              1                  ordertype " +
                "       from doctor_problem_order dpo " +
                "              left join user_evaluate ue on dpo.id = ue.order_number and ue.ordertype = 1 " +
                "       where ifnull(dpo.delflag, 0) = 0 " +
                "         and dpo.userid = ? " +
                "         and dpo.doctorid = ? " +
                "         and dpo.states = 4 " +
                "       union all " +
                "       select dpo.id, " +
                "              dpo.diagnosis, " +
                "              dpo.dis_describe disdescribe, " +
                "              dpo.create_time  createtime, " +
                "              dpo.actualmoney  price, " +
                "              ue.id as         evaluateid, " +
                "              2                ordertype " +
                "       from doctor_phone_order dpo " +
                "              left join user_account ua on dpo.userid = ua.id " +
                "              left join user_evaluate ue on dpo.id = ue.order_number and ue.ordertype = 2 " +
                "       where ifnull(dpo.delflag, 0) = 0 " +
                "         and dpo.userid = ? " +
                "         and dpo.doctorid = ? " +
                "         and dpo.status = 4 " +
                "       union all " +
                "       select dvo.id, " +
                "              dvo.guidance     diagnosis, " +
                "              dvo.dis_describe disdescribe, " +
                "              dvo.create_time  createtime, " +
                "              dvo.actualmoney  price, " +
                "              ue.id            evaluateid, " +
                "              3                ordertype " +
                "       from doctor_video_order dvo " +
                "              left join user_evaluate ue on dvo.id = ue.order_number and ue.ordertype = 3 " +
                "       where dvo.userid = ? " +
                "         and dvo.doctorid = ? " +
                "         and ifnull(dvo.delflag, 0) = 0 " +
                "         and dvo.status = 4 " +
                "     ) m " +
                "       left join (select orderid,order_type ordertype,max(prescriptionid) prescriptionid " +
                "                  from doc_prescription " +
                "                  where ifnull(delflag, 0) = 0 " +
                "                  group by orderid,order_type) dp on m.id = dp.orderid and m.ordertype = dp.ordertype ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(doctorid);
        params.add(userid);
        params.add(doctorid);
        params.add(userid);
        params.add(doctorid);
        return query(pageSql(sql, " order by createtime desc "), pageParams(params, pageIndex, pageSize), (ResultSet res, int num) -> {
            Map<String, Object> map = resultToMap(res);
            if (map != null) {
                //用户订单列表新加字段:
                //guidance:0:不显示,1:未诊断,2:已诊断
                //evaluate:0:不显示,1:未评论,2:以评论
                //record:0:不显示,1:显示问诊记录按钮(针对图文,其他默认0)
                map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
                long evaluateid = ModelUtil.getLong(map, "evaluateid");
                String diagnosis = ModelUtil.getStr(map, "diagnosis");
                int visitcategory = ModelUtil.getInt(map, "visitcategory");
                long prescriptionid = ModelUtil.getLong(map, "prescriptionid");
                map.put("guidance", 0);
                map.put("evaluate", 0);
                map.put("record", 0);
                if (StrUtil.isEmpty(diagnosis) && prescriptionid == 0) {
                    map.put("guidance", 1);
                } else {
                    map.put("guidance", 2);
                }

                if (evaluateid > 0) {
                    map.put("evaluate", 2);
                } else {
                    map.put("evaluate", 1);
                }
                if (visitcategory == VisitCategoryEnum.Outpatient.getCode() || visitcategory == VisitCategoryEnum.graphic.getCode()) {
                    map.put("record", 1);
                }
            }
            return map;
        });
    }

    /**
     * 医生推荐价格
     *
     * @return
     */
    public List<Map<String, Object>> doctorRecommendPriceList(int type) {
        String sql = " select id,sort,price from doctor_recommend_price where ifnull(delflag,0)=0 and type = ? " +
                " order by sort ";
        List<Map<String, Object>> list = queryForList(sql, type);
        for (Map<String, Object> map : list) {
            if (map != null) {
                map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
            }
        }
        return list;
    }

    /**
     * 新增医生推荐价格
     *
     * @param sort
     * @param price
     * @param type
     * @return
     */
    public boolean insertDoctorRecommendPrice(int sort, BigDecimal price, int type) {
        String sql = " insert into doctor_recommend_price(sort,price,type,delflag,create_time) values(?,?,?,0,?) ";
        List<Object> params = new ArrayList<>();
        params.add(sort);
        params.add(PriceUtil.addPrice(price));
        params.add(type);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    /**
     * 删除医生推荐价格
     *
     * @return
     */
    public boolean delDoctorRecommendPrice() {
        String sql = " update doctor_recommend_price set delflag=1";
        return update(sql) > 0;
    }


    /**
     * 查询推荐价格（1图文，2电话，3视频）
     *
     * @return
     */
    public List<Map<String, Object>> getRecommendPrice(int type) {
        String sql = "select id,price from doctor_recommend_price where ifnull(delflag,0)=0 and type=? order by price";
        List<Map<String, Object>> list = queryForList(sql, type);
        for (Map<String, Object> map : list) {
            if (map != null) {
                map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
            }
        }
        return list;
    }

    public boolean updatecheck(int id, int onecheck, int twocheck) {
        String sqls = "";
        List<Object> list = new ArrayList<>();
        list.add(onecheck);
        list.add(twocheck);
        list.add(UnixUtil.getNowTimeStamp());
        if (id != 0) {
            sqls = "update auto_check set onecheck=?,twocheck=?,create_time=? where id=?";
            list.add(id);
            return update(sqls, list) > 0;
        } else {
            sqls = "insert into auto_check (onecheck,twocheck,create_time) values (?,?,?)";
            return insert(sqls, list) > 0;
        }

    }

    public List<Map<String, Object>> getCheck() {
        String sql = "select id,onecheck,twocheck,create_time createtime from auto_check where ifnull(delflag,0)=0";
        List<Map<String,Object>> list=queryForList(sql);
        if(list.size()==0||list==null){
            list=new ArrayList<>();
            Map<String,Object> map=new HashMap<>();
            map.put("onecheck",0);
            map.put("twocheck",0);
            list.add(map);
        }
        return list;
    }

    public Map<String, Object> getCheckDetail(long id) {
        String sql = "select id,onecheck,twocheck,create_time createtime from auto_check where ifnull(delflag,0)=0 and id=?";
        return queryForMap(sql, id);
    }


}
