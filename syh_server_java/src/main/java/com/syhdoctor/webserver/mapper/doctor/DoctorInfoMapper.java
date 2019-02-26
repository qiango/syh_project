package com.syhdoctor.webserver.mapper.doctor;

import com.syhdoctor.common.utils.EnumUtils.DoctorExamineEnum;
import com.syhdoctor.common.utils.EnumUtils.DoctorTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DoctorInfoMapper extends DoctorBaseMapper {

    public List<Map<String, Object>> getDoctorOnDutyList(int departmentId, int recommend, int pageIndex, int pageSize) {
        String sql = " select doctorid, " +
                "       doctorname, " +
                "       docphoto, " +
                "       title, " +
                "       workinstname, " +
                "       phoneopen, " +
                "       online, " +
                "       answeropen, " +
                "       videoopen, " +
                "       modify_time, " +
                "       create_time, " +
                "       department " +
                "from ( " +
                "       select di.doctorid, " +
                "              di.doc_name      as                                                             doctorname, " +
                "              di.doc_photo_url as                                                             docphoto, " +
                "              cd.value         as                                                             department, " +
                "              cdt.value        as                                                             title, " +
                "              work_inst_name   as                                                             workinstname, " +
                "              di.modify_time, " +
                "              di.create_time, " +
                "              case when ifnull(do.id, 0) > 0 and ifnull(phone.id, 0) > 0 then 1 else 0 end    online, " +
                "              case when ifnull(do.id, 0) > 0 and ifnull(phone.id, 0) > 0 then 1 else 0 end    phoneopen, " +
                "              case when ifnull(answer.id, 0) > 0 then 1 else 0 end                            answeropen, " +
                "              case when ifnull(ds.id, 0) > 0 and ifnull(video.id, 0) > 0 then 1 else 0 end videoopen " +
                "       from doctor_info di " +
                "              left join doctor_extends de on di.doctorid = de.doctorid and ifnull(de.delflag, 0) = 0 " +
                "              left join code_department cd on di.department_id = cd.id and ifnull(cd.delflag, 0) = 0 " +
                "              left join code_doctor_title cdt on di.title_id = cdt.id and ifnull(cdt.delflag, 0) = 0 " +
                "              left join doc_med_price_list answer " +
                "                        on di.doctorid = answer.doctorid and answer.med_class_id = 1 and answer.whetheropen=1 and ifnull(answer.delflag, 0) = 0 " +
                "              left join doc_med_price_list video " +
                "                        on di.doctorid = video.doctorid and video.med_class_id = 4 and video.whetheropen=1 and ifnull(video.delflag, 0) = 0 " +
                "              left join doctor_scheduling ds " +
                "                        on di.doctorid = ds.doctorid and ds.visiting_start_time > unix_timestamp() * 1000 and " +
                "                           ifnull(ds.delflag, 0) = 0 " +
                "              left join doc_med_price_list phone " +
                "                        on di.doctorid = phone.doctorid and phone.med_class_id = 3 and phone.whetheropen=1 and ifnull(phone.delflag, 0) = 0 " +
                "              left join doctor_onduty do " +
                "                        on di.doctorid = do.doctorid and do.examine_state = 2 and ifnull(do.delflag, 0) = 0 and " +
                "                           do.visiting_start_time > UNIX_TIMESTAMP() * 1000 " +
                "       where ifnull(di.delflag, 0) = 0 " +
                "         and di.examine = ? " +
                "         and di.doc_type = ? ";
        List<Object> params = new ArrayList<>();
        params.add(DoctorExamineEnum.certificationSuccess.getCode());
        params.add(DoctorTypeEnum.DoctorExpert.getCode());
        if (departmentId > 0) {
            sql += " and di.department_id=? ";
            params.add(String.format("%s", departmentId));
        }
        if (recommend > 0) {
            sql += " and de.recommend=? ";
            params.add(String.format("%s", recommend));
        }
        return queryForList(pageSql(sql, ") m GROUP BY doctorid,online,phoneopen,answeropen,videoopen order by modify_time desc,online desc,create_time asc "), pageParams(params, pageIndex, pageSize));
    }


    /**
     * 医生主页信息
     *
     * @param doctorId
     * @return
     */
    public Map<String, Object> getDoctorHomePage(long doctorId) {
        String sql = "select di.doctorid,di.doc_name as doctorname,di.doc_photo_url as docphoto,cd.value as department,cdt.value as title,   " +
                "                       work_inst_name as workinstname, " +
                "                di.professional " +
                "                from doctor_info di   " +
                "                       left join doctor_extends de on di.doctorid = de.doctorid   " +
                "                       left join code_department cd on di.department_id = cd.id   " +
                "                       left join code_doctor_title cdt on di.title_id = cdt.id " +
                "                where ifnull(di.delflag,0)=0 and di.examine=? and di.doctorid= ?";
        List<Object> params = new ArrayList<>();
        params.add(DoctorExamineEnum.certificationSuccess.getCode());
        params.add(doctorId);
        return queryForMap(sql, params);
    }

    public List<Map<String, Object>> getDoctorEvaluateList(long doctorid) {
        String sql = "select case when isanonymous = 1 then concat(left(ua.name, 1), '**') else ua.name end username, " +
                "       order_number                                                                   orderid, " +
                "       ordertype, " +
                "       userid, " +
                "       evaluate, " +
                "       content, " +
                "       isanonymous, " +
                "       ue.create_time                                                                 createtime " +
                "from user_evaluate ue " +
                "            left join user_account ua on ue.userid = ua.id and ifnull(ua.delflag, 0) = 0 " +
                "where ifnull(ue.delflag, 0) = 0 " +
                "  and ue.doctorid = ? order by ue.id desc limit 3";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> getDoctorEvaluateList(long doctorid, int pageIndex, int pageSize) {
        String sql = " select case when isanonymous = 1 then concat(left(ua.name, 1), '**') else ua.name end username, " +
                "       order_number                                                                   orderid, " +
                "       ordertype, " +
                "       userid, " +
                "       evaluate, " +
                "       content, " +
                "       isanonymous, " +
                "       ue.create_time                                                                 createtime " +
                "from user_evaluate ue " +
                "            left join user_account ua on ue.userid = ua.id and ifnull(ua.delflag, 0) = 0 " +
                "where ifnull(ue.delflag, 0) = 0 " +
                "  and ue.doctorid = ? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return queryForList(pageSql(sql, " order by ue.id desc "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 订单用户选择的常见病症
     *
     * @param ids
     * @return
     */
    public List<Map<String, Object>> orderAnswerDiseaseList(List<Long> ids) {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        String sql = " select orderid,diseasename value " +
                "from middle_answer_disease " +
                "where orderid in (:ids) and ifnull(delflag,0)=0 ";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        return queryForList(sql, params);
    }


    /**
     * 订单用户选择的常见病症
     *
     * @param ids
     * @return
     */
    public List<Map<String, Object>> orderPhoneDiseaseList(List<Long> ids) {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        String sql = " select orderid, diseasename value  " +
                "from middle_phone_disease " +
                "where orderid in (:ids)  " +
                "  and ifnull(delflag, 0) = 0  ";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        return queryForList(sql, params);
    }

    /**
     * 订单用户选择的常见病症
     *
     * @param ids
     * @return
     */
    public List<Map<String, Object>> orderVideoDiseaseList(List<Long> ids) {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        String sql = " select orderid, diseasename value  " +
                "from middle_video_disease " +
                "where orderid in (:ids)  " +
                "  and ifnull(delflag, 0) = 0  ";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        return queryForList(sql, params);
    }

    /**
     * 查询医生展示价格
     *
     * @param doctorId 医生ID
     * @param
     * @return
     */
    public Map<String, Object> getDoctorPrice(long doctorId, int medclassId) {
        String sql = "select id,med_class_id as medclassid,med_class_name as medclassname,price as price,whetheropen from doc_med_price_list where doctorid=? and med_class_id=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(medclassId);
        Map<String, Object> data = queryForMap(sql, params);
        if (data != null) {
            data.put("price", PriceUtil.findPrice(ModelUtil.getLong(data, "price", 0)));
        }
        return data;
    }

    /**
     * 查询医生展示价格
     *
     * @param doctorId 医生ID
     * @param
     * @return
     */
    public Map<String, Object> getDoctorPrice(long doctorId) {
        String sql = "  " +
                "select dmpl.id, " +
                "       dmpl.med_class_id   as medclassid, " +
                "       dmpl.med_class_name as medclassname, " +
                "       dmpl.price          as price, " +
                "       dmpl.whetheropen " +
                "from doc_med_price_list dmpl " +
                "       inner join doctor_onduty do " +
                "         on dmpl.doctorid = do.doctorid and ifnull(do.delflag, 0) = 0 and ifnull(examine_state, 0) = 2 " +
                "              and do.visiting_start_time / 1000 < UNIX_TIMESTAMP(now()) and " +
                "            do.visiting_end_time / 1000 > UNIX_TIMESTAMP(now()) " +
                "where dmpl.doctorid = ? " +
                "  and dmpl.med_class_id = 3 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        Map<String, Object> data = queryForMap(sql, params);
        if (data != null) {
            data.put("price", PriceUtil.findPrice(ModelUtil.getLong(data, "price", 0)));
        }
        return data;
    }


    public Map<String, Object> getGraphicOrderId(long doctorId, long userId) {
        String sql = "select id from doctor_problem_order where  doctorid=? and userid=? and  ifnull(delflag,0)=0 and states in (2,6)";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(userId);
        return queryForMap(sql, params);
    }

    public Map<String, Object> getPhoneOrderId(long doctorId, long userId) {
        String sql = "select id from doctor_phone_order where doctorid=? and userid=? and ifnull(delflag,0)=0 and status in (2,6)";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(userId);
        return queryForMap(sql, params);
    }
}
