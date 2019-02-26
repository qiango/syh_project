package com.syhdoctor.webserver.mapper.answer;

import com.syhdoctor.common.utils.EnumUtils.*;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AnswerBaseMapper extends BaseMapper {

    public List<Map<String, Object>> getOnDuctDoctorList(String doctorName) {
        String sql = "select di.doctorid, concat_ws('_',di.doc_name,di.doo_tel) as value " +
                "from doctor_info di " +
                "       left join doctor_extends de on di.doctorid = de.doctorid " +
                " join doc_med_price_list dmpl on di.doctorid=dmpl.doctorid and dmpl.med_class_id=3 " +
                "where di.examine = 2 and ifnull(di.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(doctorName)) {
            sql += " and di.doc_name like ?";
            params.add(String.format("%%%s%%", doctorName));
        }
        return queryForList(sql, params);
    }

    /**
     * 查询用户信息
     *
     * @param userId
     * @return
     */
    public Map<String, Object> getUserById(long userId) {
        String sql = "select id,name as username,phone as userphone from user_account where id=?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        return queryForMap(sql, params);
    }

    /**
     * 修改订单支付状态
     *
     * @param id          订单ID
     * @param payType     支付类型
     * @param payStatus   支付状态
     * @param price       支付价格
     * @param orderRemark 订单备注
     * @param agenId      登录人ID
     * @return
     */
    public boolean updateDoctorPhoneOrder(int id, int payType, int payStatus, BigDecimal price, String orderRemark, long agenId) {
        String sql = "update doctor_phone_order set paytype=?,paystatus=?,actualmoney=?,order_remark=?,modify_user=?,modify_time=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(payType);
        params.add(payStatus);
        params.add(PriceUtil.addPrice(price));
        params.add(orderRemark);
        params.add(agenId);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 查询急诊订单
     *
     * @param id
     * @return
     */
    public Map<String, Object> getDoctorPhoneOrderById(long id) {
        String sql = "select dpo.id, " +
                "                        order_no as orderno,   " +
                "                        ua.name as username,   " +
                "                        ua.kangyang_userid as kangyanguserid,   " +
                "                        ua.openid,   " +
                "                        ua.walletbalance,   " +
                "                        ua.id userid,   " +
                "                        dpo.doctorid, " +
                "                        dpo.userid, " +
                "                        dpo.schedulingid, " +
                "                        dpo.visitcategory, " +
                "                        di.doc_name as doctorname,   " +
                "                        doctor_phone as doctorphone,   " +
                "                        user_phone as userphone,   " +
                "                        actualmoney,   " +
                "                        paystatus,   " +
                "                        status,   " +
                "                        diagnosis,   " +
                "                        dpo.create_time createtime, " +
                "                        dpo.create_user createuser, " +
                "       dpo.result_time resulttime, " +
                "                        phonestatus,   " +
                "                        paytype,   " +
                "                        order_remark orderremark," +
                "                        dpo.record_url as recordurl" +
                "                        from doctor_phone_order dpo   " +
                "                        left join user_account ua on dpo.userid=ua.id   " +
                "                        left join doctor_info di on di.doctorid=dpo.doctorid" +
                "  where dpo.id= ?";
        List<Object> params = new ArrayList<>();
        params.add(id);
        Map<String, Object> data = queryForMap(sql, params);
        if (data != null) {
            data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
            data.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(data, "walletbalance")));
        }
        return data;
    }

    public boolean findSchTime(long scheduid) {
        String sql = "select id from doctor_onduty where id=? and issubscribe=0 and ifnull(delflag,0)=0";
        if (queryForMap(sql, scheduid) == null) {//被预约了，无该可用时间
            return true;
        } else {
            return false;
        }
    }

    public List<Map<String, Object>> getMiddlePhoneDiseaseList(int id) {
        String sql = "select id,diseasename value from middle_phone_disease where orderid=?";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return queryForList(sql, params);
    }

    /**
     * 后台急诊订单添加
     *
     * @param orderNo     订单号
     * @param doctorId    医生ID
     * @param userId      用户Id
     * @param doctorPhone 医生手机号
     * @param price       价格
     * @param payStatus   支付状态
     * @param status      订单状态
     * @param payType     支付方式
     * @param orderRemark 订单备注
     * @param agenId      登录人ID
     * @return
     */
    public long addAdminDoctorPhoneOrder(String orderNo, long doctorId, long userId, String doctorPhone, String userPhone, BigDecimal price,
                                         int payStatus, long createTime, int status, int payType, String orderRemark, long agenId) {
        String sql = "insert into doctor_phone_order (" +
                "                                order_no, " +
                "                                doctorid, " +
                "                                userid, " +
                "                                doctor_phone, " +
                "                                user_phone, " +
                "                                actualmoney, " +
                "                                paystatus, " +
                "                                status, " +
                "                                create_time, " +
                "                                create_user, " +
                "                                delflag, " +
                "                                paytype,order_remark,create_type) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,1) ";
        List<Object> params = new ArrayList<>();
        params.add(orderNo);
        params.add(doctorId);
        params.add(userId);
        params.add(doctorPhone);
        params.add(userPhone);
        params.add(PriceUtil.addPrice(price));
        params.add(payStatus);
        params.add(status);
        params.add(createTime);
        params.add(agenId);
        params.add(0);
        params.add(payType);
        params.add(orderRemark);
        return insert(sql, params, "id");
    }


    /**
     * @param orderNo     订单号
     * @param userId      用户id
     * @param doctorId    医生id
     * @param actualMoney 实际支付价格
     * @param marketPrice 应付价格
     */
    public long addAnswerOrder(String orderNo, long userId, long doctorId, BigDecimal actualMoney, BigDecimal marketPrice, BigDecimal originalprice, Double discount, int visitCategory, String disdescribe, long diseasetimeid, int gohospital, int issuredis) {
        String sql = " INSERT INTO doctor_problem_order (orderno, userid, doctorid, states, paystatus, create_time, delflag, actualmoney, marketprice,originalprice,discount,visitcategory,dis_describe,disease_time,gohospital,issuredis) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(orderNo);
        params.add(userId);
        params.add(doctorId);
        params.add(AnswerOrderStateEnum.UnPaid.getCode());
        params.add(PayStateEnum.UnPaid.getCode());
        params.add(UnixUtil.getNowTimeStamp());
        params.add(0);
        params.add(PriceUtil.addPrice(actualMoney));
        params.add(PriceUtil.addPrice(marketPrice));
        params.add(PriceUtil.addPrice(originalprice));
        params.add(discount);
        params.add(visitCategory);
        params.add(disdescribe);
        params.add(diseasetimeid);
        params.add(gohospital);
        params.add(issuredis);
        return insert(sql, params, "id");
    }

    public Map<String, Object> getUserAccountByUserId(long userId) {
        String sql = "select xg_token as xgtoken,platform,walletbalance from user_account where id=?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        Map<String, Object> data = queryForMap(sql, params);
        if (data != null) {
            data.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(data, "walletbalance")));
        }
        return data;
    }

    public Map<String, Object> getDoctorExtendsByDoctorId(long doctorId) {
        String sql = "select xg_token as xgtoken,platform from doctor_extends where doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        return queryForMap(sql, params);
    }

    public void addAnswerOrderExtend(long orderId, int dPlatform, String dToken, int uPlatform, String uToken) {
        String sql = "insert into doctor_problem_order_extend(orderid, dplatform, uplatform, utoken, dtoken, delflag, create_time) " +
                " values (?,?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(dPlatform);
        params.add(uPlatform);
        params.add(uToken);
        params.add(dToken);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        update(sql, params);
    }

    public void addPhoneOrderExtend(long orderId, int dPlatform, String dToken, int uPlatform, String uToken) {
        String sql = "insert into doctor_phone_order_extend(orderid, dplatform, uplatform, utoken, dtoken, delflag, create_time) " +
                " values (?,?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(dPlatform);
        params.add(uPlatform);
        params.add(uToken);
        params.add(dToken);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        update(sql, params);
    }

    public void addVideoOrderExtend(long orderId, int dPlatform, String dToken, int uPlatform, String uToken) {
        String sql = "insert into doctor_video_order_extend(orderid, dplatform, uplatform, utoken, dtoken, delflag, create_time) " +
                " values (?,?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(dPlatform);
        params.add(uPlatform);
        params.add(uToken);
        params.add(dToken);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        update(sql, params);
    }

    //首页查询当天预约
    public Map<String, Object> getPhoneSubscribe(long id, long doctorId) {
        String sql = "select id,visiting_start_time visitingstarttime,visiting_end_time visitingendtime,issubscribe from doctor_onduty where id=? and doctorid=? ";
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(doctorId);
        return queryForMap(sql, list);
    }

    //首页查询当天预约
    public Map<String, Object> getVideoSubscribe(long id, long doctorId) {
        String sql = "select id,visiting_start_time visitingstarttime,visiting_end_time visitingendtime,issubscribe from doctor_scheduling where id=? and doctorid=? ";
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(doctorId);
        return queryForMap(sql, list);
    }

    public boolean updateSubscribe(long schedulingid) {
        String sql = " update doctor_onduty set issubscribe=1 where id=? ";
        return update(sql, schedulingid) > 0;
    }


    //最近预约
    public Map<String, Object> getLatelySubscribe(long doctorId) {
        String sql = "select id,visiting_start_time visitingstarttime,visiting_end_time visitingendtime,issubscribe " +
                "from doctor_scheduling " +
                "where ifnull(issubscribe, 0) = 0 " +
                "  and doctorid = ? " +
                "  and visiting_start_time > UNIX_TIMESTAMP() " +
                "order by visiting_start_time " +
                "limit 1 ";
        List<Object> list = new ArrayList<>();
        list.add(doctorId);
        return queryForMap(sql, list);
    }

    /**
     * @param orderNo     订单号
     * @param userId      用户id
     * @param doctorId    医生id
     * @param actualMoney 实际支付价格
     * @param marketPrice 应付价格
     */
    public long addVideoOrder(String orderNo, long userId, long doctorId, BigDecimal actualMoney, BigDecimal marketPrice, BigDecimal originalprice, Double discount, int visitCategory,
                              long schedulingid, long subscribeTime, long subscribeEndTime, String disdescribe, long diseasetimeid, int gohospital, int issuredis) {
        String sql = "insert into doctor_video_order(orderno, " +
                "                               userid, " +
                "                               doctorid, " +
                "                               status, " +
                "                               paystatus, " +
                "                               actualmoney, " +
                "                               marketprice, " +
                "                               originalprice, " +
                "                               discount, " +
                "                               visitcategory, " +
                "                               schedulingid, " +
                "                               subscribe_time, " +
                "                               subscribe_end_time, " +
                "                               dis_describe, " +
                "                               disease_time, " +
                "                               gohospital, " +
                "                               issuredis, " +
                "                               delflag, " +
                "                               create_time) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(orderNo);
        params.add(userId);
        params.add(doctorId);
        params.add(AnswerOrderStateEnum.UnPaid.getCode());
        params.add(PayStateEnum.UnPaid.getCode());
        params.add(PriceUtil.addPrice(actualMoney));
        params.add(PriceUtil.addPrice(marketPrice));
        params.add(PriceUtil.addPrice(originalprice));
        params.add(discount);
        params.add(visitCategory);
        params.add(schedulingid);
        params.add(subscribeTime);
        params.add(subscribeEndTime);
        params.add(disdescribe);
        params.add(diseasetimeid);
        params.add(gohospital);
        params.add(issuredis);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params, "id");
    }


    /**
     * @param orderNo     订单号
     * @param userId      用户id
     * @param doctorId    医生id
     * @param actualMoney 实际支付价格
     */
    public long addPhoneOrder(String orderNo, long userId, long doctorId, String userPhone, String doctorPhone, BigDecimal actualMoney, BigDecimal marketprice,
                              BigDecimal originalprice, Double discount, int visitCategory, long schedulingid, long startTime, long endTime, String disdescribe, long diseasetimeid, int gohospital, int issuredis) {
        String sql = " insert into doctor_phone_order (order_no," +
                "                                doctorid," +
                "                                userid," +
                "                                doctor_phone," +
                "                                user_phone," +
                "                                actualmoney," +
                "                                marketprice," +
                "                                originalprice," +
                "                                discount," +
                "                                paystatus," +
                "                                status," +
                "                                create_time," +
                "                                delflag," +
                "                                callnum," +
                "                                visitCategory," +
                "                                phonestatus," +
                "                                schedulingid," +
                "                                subscribe_time," +
                "                                subscribe_end_time," +
                "                                dis_describe," +
                "                                disease_time," +
                "                                gohospital," +
                "                                issuredis," +
                "                                ispull)" +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        long nowTimeStamp = UnixUtil.getNowTimeStamp();
        params.add(orderNo);
        params.add(doctorId);
        params.add(userId);
        params.add(doctorPhone);
        params.add(userPhone);
        params.add(PriceUtil.addPrice(actualMoney));
        params.add(PriceUtil.addPrice(marketprice));
        params.add(PriceUtil.addPrice(originalprice));
        params.add(discount);
        params.add(PayStateEnum.UnPaid.getCode());
        params.add(PhoneOrderStateEnum.UnPaid.getCode());
        params.add(nowTimeStamp);
        params.add(0);
        params.add(0);
        params.add(visitCategory);
        params.add(0);
        params.add(schedulingid);
        params.add(visitCategory == VisitCategoryEnum.department.getCode() ? nowTimeStamp : startTime);
        params.add(endTime);
        params.add(disdescribe);
        params.add(diseasetimeid);
        params.add(gohospital);
        params.add(issuredis);
        params.add(0);
        return insert(sql, params, "id");
    }

    /**
     * 添加订单选择的病症
     *
     * @param orderId
     * @param name
     * @return
     */
    public boolean addAnswerDiseaseid(long orderId, long diseaseId, String name) {
        String sql = " insert into middle_answer_disease(orderid,diseaseid,diseasename,delflag,create_time)values(?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(diseaseId);
        params.add(name);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    /**
     * 添加订单图片
     *
     * @param orderId
     * @param url
     * @param orderType
     * @return
     */
    public boolean addOrderPicture(long orderId, String url, int orderType) {
        String sql = " insert into middle_order_picture(orderid,disease_picture,delflag,create_time,order_type)values(?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(url);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(orderType);
        return insert(sql, params) > 0;
    }


    /**
     * 添加订单选择的病症
     *
     * @param orderId
     * @param name
     * @return
     */
    public boolean addPhoneDiseaseid(long orderId, long diseaseId, String name) {
        String sql = " insert into middle_phone_disease(orderid,diseaseid,diseasename,delflag,create_time)values(?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(diseaseId);
        params.add(name);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    /**
     * 添加订单选择的病症
     *
     * @param orderId
     * @param name
     * @return
     */
    public boolean addVideoDiseaseid(long orderId, long diseaseId, String name) {
        String sql = " insert into middle_video_disease(orderid,diseaseid,diseasename,delflag,create_time)values(?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(diseaseId);
        params.add(name);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }


    /**
     * 订单状态列表
     *
     * @return
     */
    public List<Map<String, Object>> basicsList() {
        String sql = " select customid id,name from basics where Type=4 and ifnull(delflag,0)=0 order by sort desc ";
        return queryForList(sql);
    }

    /**
     * 常见病症列表
     *
     * @return
     */
    public List<Map<String, Object>> getSymptomsList(long typeId) {
        String sql = " select id,name value from common_disease_symptoms where ifnull(delflag,0)=0 and typeid=? and name!='其他' ORDER BY rand() LIMIT 7 ";
        return queryForList(sql, typeId);
    }

    /**
     * 常见病症列表
     *
     * @return
     */
    public List<Map<String, Object>> getSymptomsOrderBySortList(long typeId) {
        String sql = " select id,name value from common_disease_symptoms where ifnull(delflag,0)=0 and typeid=? and name !='其他' ORDER BY sort desc limit 7 ";
        return queryForList(sql, typeId);
    }

    public Map<String, Object> getSpeci(long typeid) {
        String sql = "select id,backgroundpicture from special_specialties where symptomtypeid=? and ifnull(delflag,0)=0";
        return queryForMap(sql, typeid);
    }

    /**
     * 常见病症列表
     *
     * @return
     */
    public Map<String, Object> getOtherSymptoms(long typeId) {
        String sql = " select id,name value from common_disease_symptoms where ifnull(delflag,0)=0 and typeid=? and name='其他' limit 1 ";
        return queryForMap(sql, typeId);
    }

    /**
     * 专病咨询 常见病症类型+症状列表
     *
     * @return
     */
    public List<Map<String, Object>> getCounselingSymptomsTypeList(long scid) {
        String sql = " select cd.id typeid, cd.name typevalue, cds.id id, cds.name value " +
                "from symptom_type st " +
                "       left join common_disease_symptoms_type cd on st.symptomtypeid = cd.id and ifnull(cd.delflag, 0) = 0 " +
                "       left join common_disease_symptoms cds on cd.id = cds.typeid and ifnull(cds.delflag, 0) = 0 " +
                "where st.scid = ? " +
                "  and ifnull(st.delflag, 0) = 0 " +
                "order by cd.id, cds.id ";
        return queryForList(sql, scid);
    }

    /**
     * 常见病症类型+症状列表
     *
     * @return
     */
    public List<Map<String, Object>> getSymptomsTypeList() {
        String sql = " select mdst.id typeid, mdst.name typevalue, mds.id id, mds.name value " +
                "from common_disease_symptoms_type mdst " +
                "       left join common_disease_symptoms mds on mdst.id = mds.typeid and ifnull(mds.delflag, 0) = 0 " +
                "where ifnull(mdst.delflag, 0) = 0 " +
                "order by mdst.id, mds.id ";
        return queryForList(sql);
    }

    /**
     * 常见病症类型+症状列表
     *
     * @return
     */
    public List<Map<String, Object>> getSymptomsTypeList(long typeid) {
        String sql = " select mdst.id typeid, mdst.name typevalue, mds.id id, mds.name value " +
                "from common_disease_symptoms_type mdst " +
                "       left join common_disease_symptoms mds on mdst.id = mds.typeid and ifnull(mds.delflag, 0) = 0 " +
                "where ifnull(mdst.delflag, 0) = 0 and mdst.id=? " +
                "order by mdst.id, mds.id ";
        return queryForList(sql, typeid);
    }

    /**
     * 常见病症类型+症状列表
     *
     * @return
     */
    public List<Map<String, Object>> getSymptomsTypeListByDoctorId(long doctorId) {
        String sql = " select cdst.id typeid, cdst.name typevalue, cds.id id, cds.name value " +
                "from middle_department_symptoms_type md " +
                "       left join common_disease_symptoms_type cdst on md.typeid = cdst.id and ifnull(cdst.delflag, 0) = 0 " +
                "       left join common_disease_symptoms cds on cdst.id = cds.typeid and ifnull(cds.delflag, 0) = 0 " +
                "       left join doctor_info di on md.departmentid = di.department_id and ifnull(di.delflag, 0) = 0 " +
                "where doctorid = ? and ifnull(md.delflag, 0) = 0 order by cdst.id,cds.id ";
        return queryForList(sql, doctorId);
    }

    /**
     * 常见病症类型+症状列表
     *
     * @return
     */
    public List<Map<String, Object>> getSymptomsTypeListByOther() {
        String sql = " select cdst.id typeid, cdst.name typevalue, cds.id id, cds.name value " +
                "from common_disease_symptoms_type cdst " +
                "       left join common_disease_symptoms cds on cdst.id = cds.typeid and ifnull(cds.delflag, 0) = 0 " +
                "where cdst.name = '其他' and ifnull(cdst.delflag,0)=0 ";
        return queryForList(sql);
    }

    /**
     * 常见病症
     *
     * @return
     */
    public Map<String, Object> getSymptoms(long id) {
        String sql = " select id,name value from common_disease_symptoms where ifnull(delflag,0)=0 and id=? ";
        return queryForMap(sql, id);
    }

    /**
     * 用户常见病症
     *
     * @param userId
     * @return
     */
    public List<Map<String, Object>> getAdminSymptomsList(long userId) {
        String sql = "select distinct m.diseasename name " +
                "from (select distinct mad.diseasename " +
                "      from middle_answer_disease mad " +
                "             left join doctor_problem_order dpo on dpo.id = mad.id " +
                "      where dpo.userid = ? " +
                "      union all " +
                "      select distinct mpd.diseasename " +
                "      from middle_phone_disease mpd " +
                "             left join doctor_phone_order dpo on dpo.id = mpd.id " +
                "      where dpo.userid = ?) m ";
        return queryForList(sql, userId, userId);
    }

    /**
     * 症状列表
     *
     * @param userid
     * @return
     */
    public List<Map<String, Object>> getDiseaseName(long userid) {
        String sql = " SELECT " +
                " cds.name,cdst.name typename " +
                " FROM " +
                " middle_answer_disease mad " +
                " left join doctor_problem_order dpo on mad.orderid=dpo.id  and IFNULL(dpo.delflag,0) = 0 " +
                " inner JOIN common_disease_symptoms cds ON mad.diseaseid = cds.id and IFNULL(cds.delflag,0) = 0 " +
                " inner JOIN common_disease_symptoms_type cdst ON cds.typeid = cdst.id and IFNULL(cdst.delflag,0) = 0 " +
                " WHERE " +
                " dpo.userid = ? " +
                " GROUP BY cds.id  ";
        return queryForList(sql, userid);
    }


    /**
     * 订单常见病症
     *
     * @param orderId
     * @return
     */
    public List<Map<String, Object>> getOrderDiseaseList(long orderId) {
        String sql = " select diseasename name from middle_answer_disease where orderid=? ";
        return queryForList(sql, orderId);
    }

    /**
     * 医生用户问诊列表
     *
     * @param doctorId
     * @return
     */
    public List<Map<String, Object>> doctorUserOrderList(long doctorId, long userId) {
        String sql = " select * from( " +
                "select sp.id, sp.create_time as createtime, states, 1 ordertype,'问诊' ordertypestr " +
                "from doctor_problem_order sp " +
                "where ifnull(sp.delflag, 0) = 0 " +
                "  and sp.doctorid = ? " +
                "  and userid = ? " +
                "  and states in (2, 4, 6,9) " +
                "union all " +
                "select dpo.id, dpo.create_time createtime, status states, 2 ordertype,'电话' ordertypestr " +
                "from doctor_phone_order dpo " +
                "where ifnull(dpo.delflag, 0) = 0 " +
                "  and dpo.doctorid = ? " +
                "  and dpo.userid = ? " +
                "  and dpo.status in (2,3, 4, 6) " +
                " union all " +
                "select dpo.id, dpo.create_time createtime, status states, 3 ordertype,'视频' ordertypestr " +
                "from doctor_video_order dpo " +
                "where ifnull(dpo.delflag, 0) = 0 " +
                "  and dpo.doctorid = ? " +
                "  and dpo.userid = ? " +
                "  and dpo.status in (2,3, 4, 9) " +
                "   ) o order by createtime desc ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(userId);
        params.add(doctorId);
        params.add(userId);
        params.add(doctorId);
        params.add(userId);
        return queryForList(sql, params);
    }

    /**
     * 用户问诊列表
     *
     * @return
     */
    public List<Map<String, Object>> userOrderList(long userId, int pageIndex, int pageSize) {
        String sql = " select * from ( " +
                "select dpo.id, " +
                "       dpo.orderno, " +
                "       ua.name         username, " +
                "       di.doc_name     doctorname, " +
                "       dpo.states, " +
                "       b.name          statesname, " +
                "       dpo.actualmoney, " +
                "       dpo.create_time createtime, " +
                "       1               ordertype, " +
                "       '图文'            ordertypestr " +
                "from doctor_problem_order dpo " +
                "       left join user_account ua on dpo.userid = ua.id " +
                "       left join doctor_info di on dpo.doctorid = di.doctorid " +
                "       left join basics b on dpo.states = b.customid and b.type = 4 " +
                "where ifnull(dpo.delflag, 0) = 0 " +
                "  and dpo.userid = ? " +
                "union all " +
                "select dpo.id, " +
                "       dpo.order_no    orderno, " +
                "       ua.name         username, " +
                "       di.doc_name     doctorname, " +
                "       dpo.status      states, " +
                "       b.name          statesname, " +
                "       dpo.actualmoney, " +
                "       dpo.create_time createtime, " +
                "       2               ordertype, " +
                "       '电话'            ordertypestr " +
                "from doctor_phone_order dpo " +
                "       left join user_account ua on dpo.userid = ua.id " +
                "       left join doctor_info di on dpo.doctorid = di.doctorid " +
                "       left join basics b on dpo.status = b.customid and b.type = 5 " +
                "where ifnull(dpo.delflag, 0) = 0 " +
                "  and dpo.userid = ? " +
                "              ) o ";

        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(userId);
        return query(pageSql(sql, " ORDER BY o.createtime DESC "), pageParams(params, pageIndex, pageSize), (ResultSet rs, int rowNum) -> {
            Map<String, Object> data = resultToMap(rs);
            if (data != null) {
                data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
            }
            return data;
        });
    }

    /**
     * 用户问诊数量
     *
     * @return
     */
    public long userOrderCount(long userId) {
        String sql = " select count(dpo.id) count " +
                "from doctor_problem_order dpo " +
                "       left join user_account ua on dpo.userid = ua.id " +
                "       left join doctor_info di on dpo.doctorid = di.doctorid " +
                "where ifnull(dpo.delflag,0)=0 and dpo.userid=? ";

        List<Object> params = new ArrayList<>();
        params.add(userId);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    /**
     * 医生问诊列表
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> adminAnswerOrderList(long departid, long departTypeid, String userName, String doctorName, String doctorPhone, String userPhone, long startTime, long endTime, int state, int pageIndex, int pageSize) {
        String sql = " select dpo.id, " +
                "       dpo.orderno, " +
                "       dpo.visitcategory, " +
                "       ua.name         username, " +
                "       di.doc_name doctorname, " +
                "       b.name statesname, " +
                "       dpo.states, " +
                "       dpo.actualmoney, " +
                "       dpo.create_time createtime " +
                "from doctor_problem_order dpo " +
                "       left join user_account ua on dpo.userid = ua.id " +
//                "       left join middle_answer_disease ma ON dpo.id = ma.orderid " +
//                "       LEFT JOIN common_disease_symptoms cds ON ma.diseaseid=cds.id " +
//                "       LEFT JOIN common_disease_symptoms_type cdt ON cds.typeid=cdt.id " +
                "       left join doctor_info di on dpo.doctorid = di.doctorid " +
                "       left join basics b on dpo.states=b.customid and b.type=4 " +
                "where ifnull(dpo.delflag,0)=0 ";

        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(userName)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%s%%", userName));
        }
        if (!StrUtil.isEmpty(doctorName)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%s%%", doctorName));
        }
        if (!StrUtil.isEmpty(doctorPhone)) {
            sql += "  and di.doo_tel  like ?";
            params.add(String.format("%%%s%%", doctorPhone));
        }
        if (!StrUtil.isEmpty(userPhone)) {
            sql += "  and ua.phone like ?";
            params.add(String.format("%%%s%%", userPhone));
        }
        if (startTime > 0 && endTime > 0) {
            sql += "  and dpo.create_time between ? and ?";
            params.add(String.format("%s", startTime));
            params.add(String.format("%s", endTime));
        }
        if (state > 0) {
            sql += " and dpo.states= ? ";
            params.add(state);
        }
        if (departid > 0) {
//            sql += " and ma.diseaseid= ? ";
            sql += " and dpo.id in(select orderid from middle_answer_disease where diseaseid= ?) ";
            params.add(departid);
        }
        if (departTypeid > 0) {
//            sql += " and  cdt.id= ? ";
            sql += " and dpo.id in(select orderid from middle_answer_disease where diseaseid in(select id from common_disease_symptoms where typeid=? )) ";
            params.add(departTypeid);
        }
        return query(pageSql(sql, " ORDER BY dpo.create_time DESC "), pageParams(params, pageIndex, pageSize), (ResultSet rs, int rowNum) -> {
            Map<String, Object> data = resultToMap(rs);
            if (data != null) {
                data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
                data.put("visitcategory", VisitCategoryEnum.getValue(ModelUtil.getInt(data, "visitcategory")).getMessage());
            }
            return data;
        });
    }

    /**
     * 医生问诊数量
     *
     * @return
     */
    public long adminAnswerOrderCount(long departid, long departTypeid, String userName, String doctorName, String doctorPhone, String userPhone, long startTime, long endTime, int state) {
        String sql = " select count(dpo.id) count " +
                "from doctor_problem_order dpo " +
                "       left join user_account ua on dpo.userid = ua.id " +
//                "       left join middle_answer_disease ma ON dpo.id = ma.orderid " +
//                "       LEFT JOIN common_disease_symptoms cds ON ma.diseaseid=cds.id " +
//                "       LEFT JOIN common_disease_symptoms_type cdt ON cds.typeid=cdt.id " +
                "       left join doctor_info di on dpo.doctorid = di.doctorid " +
                "       left join basics b on dpo.states=b.customid and b.type=4 " +
                "where ifnull(dpo.delflag,0)=0  ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(userName)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%s%%", userName));
        }
        if (!StrUtil.isEmpty(doctorName)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%s%%", doctorName));
        }
        if (!StrUtil.isEmpty(doctorPhone)) {
            sql += "  and di.doo_tel  like ?";
            params.add(String.format("%%%s%%", doctorPhone));
        }
        if (!StrUtil.isEmpty(userPhone)) {
            sql += "  and ua.phone like ?";
            params.add(String.format("%%%s%%", userPhone));
        }
        if (startTime > 0 && endTime > 0) {
            sql += " and dpo.create_time between ? and ?";
            params.add(String.format("%s", startTime));
            params.add(String.format("%s", endTime));
        }
        if (state > 0) {
            sql += " and dpo.states= ? ";
            params.add(state);
        }
        if (departid > 0) {
//            sql += " and ma.diseaseid= ? ";
            sql += " and dpo.id in(select orderid from middle_answer_disease where diseaseid= ?) ";
            params.add(departid);
        }
        if (departTypeid > 0) {
//            sql += " and  cdt.id= ? ";
            sql += " and dpo.id in(select orderid from middle_answer_disease where diseaseid in(select id from common_disease_symptoms where typeid=? )) ";
            params.add(departTypeid);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 医生急诊列表
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> adminPhoneOrderLists(long departid, long departTypeid, String userName, String doctorName, String doctorPhone, String userPhone, long startTime, long endTime, int state, int pageIndex, int pageSize) {
        String sql = " select dpo.id,  dpo.order_no as orderno, " +
                "          dpo.visitcategory, " +
                "          ua.name username, " +
                "          di.doc_name doctorname, " +
                "          b.name statesname, " +
                "          dpo.status as states, " +
                "          dpo.actualmoney,   " +
                "          doctor_phone as doctorphone," +
                "          user_phone as userphone," +
                "          dpo.create_time createtime, " +
                "          dpo.record_url as recordurl, " +
                "          dpo.phonestatus, " +
                "          bc.name as phonestatusname " +
                "                 from doctor_phone_order dpo " +
                "                        left join user_account ua on dpo.userid = ua.id   " +
//                "       left join middle_phone_disease ma ON dpo.id = ma.orderid " +
//                "       LEFT JOIN common_disease_symptoms cds ON ma.diseaseid=cds.id " +
//                "       LEFT JOIN common_disease_symptoms_type cdt ON cds.typeid=cdt.id " +
                "                        left join doctor_info di on dpo.doctorid = di.doctorid   " +
                "                        left join basics b on dpo.status=b.customid and b.type=5 " +
                "                        left join basics bc on dpo.phonestatus=bc.customid and bc.type=6 " +
                "                 where ifnull(dpo.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(userName)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%s%%", userName));
        }
        if (!StrUtil.isEmpty(doctorName)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%s%%", doctorName));
        }
        if (!StrUtil.isEmpty(doctorPhone)) {
            sql += " and dpo.doctor_phone like ? ";
            params.add(String.format("%%%s%%", doctorPhone));
        }
        if (!StrUtil.isEmpty(userPhone)) {
            sql += " and dpo.user_phone like ? ";
            params.add(String.format("%%%s%%", userPhone));
        }
        if (startTime > 0 && endTime > 0) {
            sql += " and dpo.create_time between ? and ? ";
            params.add(String.format("%s", startTime));
            params.add(String.format("%s", endTime));
        }
        if (state > 0) {
            sql += " and dpo.status= ? ";
            params.add(state);
        }
        if (departid > 0) {
//            sql += " and ma.diseaseid= ? ";
            sql += " and dpo.id in(select orderid from middle_phone_disease where diseaseid= ?) ";
            params.add(departid);
        }
        if (departTypeid > 0) {
//            sql += " and  cdt.id= ? ";
            sql += " and dpo.id in(select orderid from middle_phone_disease where diseaseid in(select id from common_disease_symptoms where typeid=? )) ";
            params.add(departTypeid);
        }
        return query(pageSql(sql, " ORDER BY dpo.create_time DESC "), pageParams(params, pageIndex, pageSize), (ResultSet rs, int rowNum) -> {
            Map<String, Object> data = resultToMap(rs);
            if (data != null) {
                data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
                data.put("visitcategory", VisitCategoryEnum.getValue(ModelUtil.getInt(data, "visitcategory")).getMessage());
            }
            return data;
        });
    }

    public List<Map<String, Object>> adminPhoneOrderList(String userName, String doctorName, String doctorPhone, String userPhone, long startTime, long endTime, int state, int pageIndex, int pageSize) {
        String sql = " select dpo.id,  dpo.order_no as orderno, " +
                "          dpo.visitcategory, " +
                "          ua.name username, " +
                "          di.doc_name doctorname, " +
                "          b.name statesname, " +
                "          dpo.status as states, " +
                "          dpo.actualmoney,   " +
                "          doctor_phone as doctorphone," +
                "          user_phone as userphone," +
                "          dpo.create_time createtime, " +
                "          dpo.record_url as recordurl, " +
                "          dpo.phonestatus, " +
                "          bc.name as phonestatusname " +
                "                 from doctor_phone_order dpo " +
                "                        left join user_account ua on dpo.userid = ua.id   " +
                "                        left join doctor_info di on dpo.doctorid = di.doctorid   " +
                "                        left join basics b on dpo.status=b.customid and b.type=5 " +
                "                        left join basics bc on dpo.phonestatus=bc.customid and bc.type=6 " +
                "                 where ifnull(dpo.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(userName)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%s%%", userName));
        }
        if (!StrUtil.isEmpty(doctorName)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%s%%", doctorName));
        }
        if (!StrUtil.isEmpty(doctorPhone)) {
            sql += " and dpo.doctor_phone like ? ";
            params.add(String.format("%%%s%%", doctorPhone));
        }
        if (!StrUtil.isEmpty(userPhone)) {
            sql += " and dpo.user_phone like ? ";
            params.add(String.format("%%%s%%", userPhone));
        }
        if (startTime > 0 && endTime > 0) {
            sql += " and dpo.create_time between ? and ? ";
            params.add(String.format("%s", startTime));
            params.add(String.format("%s", endTime));
        }
        if (state > 0) {
            sql += " and dpo.status= ? ";
            params.add(state);
        }
        return query(pageSql(sql, " ORDER BY dpo.create_time DESC "), pageParams(params, pageIndex, pageSize), (ResultSet rs, int rowNum) -> {
            Map<String, Object> data = resultToMap(rs);
            if (data != null) {
                data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
                data.put("visitcategory", VisitCategoryEnum.getValue(ModelUtil.getInt(data, "visitcategory")).getMessage());
            }
            return data;
        });
    }

    /**
     * 医生急诊列表
     *
     * @return
     */
    public long adminPhoneOrderCount(long departid, long departTypeid, String userName, String doctorName, String doctorPhone, String userPhone, long startTime, long endTime, int state) {
        String sql = "  select count(dpo.id) as count " +
                "                 from doctor_phone_order dpo " +
                "                        left join user_account ua on dpo.userid = ua.id   " +
                "       left join middle_phone_disease ma ON dpo.id = ma.orderid " +
                "       LEFT JOIN common_disease_symptoms cds ON ma.diseaseid=cds.id " +
                "       LEFT JOIN common_disease_symptoms_type cdt ON cds.typeid=cdt.id " +
                "                        left join doctor_info di on dpo.doctorid = di.doctorid   " +
                "                        left join basics b on dpo.status=b.customid and b.type=5 " +
                "                 where ifnull(dpo.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(userName)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%s%%", userName));
        }
        if (!StrUtil.isEmpty(doctorName)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%s%%", doctorName));
        }
        if (!StrUtil.isEmpty(doctorPhone)) {
            sql += " and dpo.doctor_phone like ? ";
            params.add(String.format("%%%s%%", doctorPhone));
        }
        if (!StrUtil.isEmpty(userPhone)) {
            sql += " and dpo.user_phone like ? ";
            params.add(String.format("%%%s%%", userPhone));
        }
        if (startTime > 0 && endTime > 0) {
            sql += " and dpo.create_time between ? and ? ";
            params.add(String.format("%s", startTime));
            params.add(String.format("%s", endTime));
        }
        if (state > 0) {
            sql += " and dpo.status= ? ";
            params.add(state);
        }
        if (departid > 0) {
            sql += " and ma.diseaseid= ? ";
            params.add(departid);
        }
        if (departTypeid > 0) {
            sql += " and  cdt.id= ? ";
            params.add(departTypeid);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 医生问诊列表
     *
     * @param doctorid
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> appDoctorAnswerOrderList(long doctorid, int status, int intotype, int pageIndex, int pageSize) {
        String sql = " select " +
                "  sp.id, " +
                "  sp.dis_describe disdescribe, " +
                "  sp.diagnosis, " +
                "  ua.headpic, " +
                "  ua.name, " +
                "  ua.id as userid," +
                "  sp.create_time as createtime, " +
                "  sp.actualmoney price, " +
                "  sp.states " +
                " from doctor_problem_order sp " +
                "  left join user_account ua on sp.userid = ua.id " +
                " where ifnull(sp.delflag, 0) = 0 and sp.doctorid = ? ";

        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        if (status > 0) {
            sql += " and sp.states = ? ";
            params.add(status);
        } else if (intotype == 2) {
            sql += " and sp.states in (2,6) ";
        } else {
            sql += "  and sp.states in (2,4,5,6,8) ";
        }
        return query(pageSql(sql, " ORDER BY createtime DESC "), pageParams(params, pageIndex, pageSize), (ResultSet rs, int rowNum) -> {
            Map<String, Object> map = resultToMap(rs);
            if (map != null) {
                BigDecimal price = PriceUtil.findPrice(ModelUtil.getLong(map, "price"));
                map.put("price", price);

                String guidance = ModelUtil.getStr(map, "diagnosis");
                int states = ModelUtil.getInt(map, "states");
                map.put("statesname", AnswerOrderStateEnum.getValue(states).getMessage());

                //用户订单列表新加字段:
                //guidance:0:不显示,1:未诊断,2:已诊断
                //record:0:不显示,1:显示问诊记录按钮(针对图文,其他默认0)
                //contact:0:不显示,1:联系患者按钮(针对图文,其他默认0)
                map.put("guidance", 0);
                map.put("record", 0);
                map.put("contact", 0);
                map.put("refund", 0);
                map.put("isopen", 0);
                if (states == AnswerOrderStateEnum.OrderSuccess.getCode()) {
                    map.put("record", 1);
                    if (StrUtil.isEmpty(guidance)) {
                        map.put("guidance", 1);
                    } else {
                        map.put("guidance", 2);
                    }
                } else if (states == AnswerOrderStateEnum.Paid.getCode() || states == AnswerOrderStateEnum.WaitReply.getCode()) {
                    map.put("contact", 1);
                } else if (states == AnswerOrderStateEnum.WaitRefund.getCode()) {
                    map.put("states", AnswerOrderStateEnum.OrderFail.getCode());
                    map.put("statesname", AnswerOrderStateEnum.OrderFail.getMessage());
                }
            }
            return map;
        });
    }

    /**
     * 医生急诊列表
     *
     * @param doctorid
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> appDoctorPhoneOrderList(long doctorid, int status, int intotype, int pageIndex, int pageSize) {
        String sql = " select dpo.id," +
                "          dpo.diagnosis," +
                "          dpo.dis_describe disdescribe," +
                "          ua.headpic, " +
                "          ua.name, " +
                "          ua.id as userid," +
                "          dpo.status as states, " +
                "          dpo.actualmoney price,   " +
                "          dpo.create_time createtime " +
                "                 from doctor_phone_order dpo " +
                "                        left join user_account ua on dpo.userid = ua.id   " +
                "                        left join doctor_info di on dpo.doctorid = di.doctorid   " +
                "                 where ifnull(dpo.delflag,0)=0 and dpo.doctorid= ? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        if (status > 0) {
            sql += " and dpo.status =? ";
            params.add(status);
        } else if (intotype == 2) {
            sql += " and dpo.status in (2,3) ";
        } else {
            sql += "  and dpo.status in (2,3,4,5,8) ";
        }
        return query(pageSql(sql, " ORDER BY createtime DESC "), pageParams(params, pageIndex, pageSize), (ResultSet rs, int rowNum) -> {
            Map<String, Object> map = resultToMap(rs);
            if (map != null) {
                BigDecimal price = PriceUtil.findPrice(ModelUtil.getLong(map, "price"));
                map.put("price", price);
                map.put("actualmoney", price);

                String guidance = ModelUtil.getStr(map, "diagnosis");
                int states = ModelUtil.getInt(map, "states");
                map.put("statesname", PhoneOrderStateEnum.getValue(states).getMessage());

                //用户订单列表新加字段:
                //guidance:0:不显示,1:未诊断,2:已诊断
                //record:0:不显示,1:显示问诊记录按钮(针对图文,其他默认0)
                //contact:0:不显示,1:联系患者按钮(针对图文,其他默认0)
                map.put("guidance", 0);
                map.put("record", 0);
                map.put("contact", 0);
                map.put("refund", 0);
                map.put("isopen", 0);
                if (states == PhoneOrderStateEnum.OrderSuccess.getCode()) {
                    map.put("states", PhoneOrderStateEnum.OrderSuccess.getCode());
                    map.put("statesname", PhoneOrderStateEnum.OrderSuccess.getMessage());
                    if (StrUtil.isEmpty(guidance)) {
                        map.put("guidance", 1);
                    } else {
                        map.put("guidance", 2);
                    }
                } else if (states == PhoneOrderStateEnum.WaitRefund.getCode()) {
                    map.put("states", PhoneOrderStateEnum.OrderFail.getCode());
                    map.put("statesname", PhoneOrderStateEnum.OrderFail.getMessage());
                }
            }
            return map;
        });
    }


    /**
     * app用户端问诊列表
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> appUserAnswerOrderList(long userId, int pageIndex, int pageSize) {
        String sql = " select sp.id, " +
                "       sp.diagnosis, " +
                "       sp.dis_describe disdescribe, " +
                "       sp.create_time as createtime, " +
                "       sp.actualmoney    price, " +
                "       ue.id             evaluateid, " +
                "       sp.states, " +
                "       dp.prescriptionid " +
                "from doctor_problem_order sp " +
                "            left join user_account ua on sp.userid = ua.id " +
                "            left join user_evaluate ue on sp.id = ue.order_number and ue.ordertype = 1 " +
                "            left join (select orderid,max(prescriptionid) prescriptionid " +
                "                       from doc_prescription " +
                "                       where ifnull(delflag, 0) = 0 and order_type=1 " +
                "                       group by orderid) dp on sp.id = dp.orderid " +
                "where ifnull(sp.delflag, 0) = 0 " +
                "  and sp.userid = ? " +
                "  and sp.states in (2, 4, 5, 6, 8) ";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        return query(pageSql(sql, " ORDER BY createtime DESC "), pageParams(params, pageIndex, pageSize), (ResultSet rs, int rowNum) -> {
            Map<String, Object> map = resultToMap(rs);
            if (map != null) {
                BigDecimal price = PriceUtil.findPrice(ModelUtil.getLong(map, "price"));
                map.put("price", price);
                String guidance = ModelUtil.getStr(map, "diagnosis");
                long evaluateid = ModelUtil.getLong(map, "evaluateid");
                long prescriptionid = ModelUtil.getLong(map, "prescriptionid");
                int states = ModelUtil.getInt(map, "states");
                map.put("statesname", AnswerOrderStateEnum.getValue(states).getMessage());
                answerOrderResult(map, states, guidance, evaluateid, price, prescriptionid);
            }
            return map;
        });
    }

    public void answerOrderResult(Map<String, Object> map, int status, String guidance, long evaluateid, BigDecimal price, long prescriptionid) {
        //用户订单列表新加字段:
        //guidance:0:不显示,1:未诊断,2:已诊断
        //evaluate:0:不显示,1:未评论,2:以评论
        //record:0:不显示,1:显示问诊记录按钮(针对图文,其他默认0)
        //contact:0:不显示,1:显示医生按钮(针对图文,其他默认0)
        //refund:0:不显示,1:退款中,2:退款成功
        map.put("guidance", 0);
        map.put("evaluate", 0);
        map.put("record", 0);
        map.put("contact", 0);
        map.put("refund", 0);
        map.put("isopen", 0);
        if (status == AnswerOrderStateEnum.OrderSuccess.getCode()) {
            map.put("states", AnswerOrderStateEnum.OrderSuccess.getCode());
            map.put("statesname", AnswerOrderStateEnum.OrderSuccess.getMessage());
            map.put("record", 1);
            if (StrUtil.isEmpty(guidance) && prescriptionid == 0) {
                map.put("guidance", 1);
            } else {
                map.put("guidance", 2);
            }

            if (evaluateid > 0) {
                map.put("evaluate", 2);
            } else {
                map.put("evaluate", 1);
            }
        } else if (status == AnswerOrderStateEnum.Paid.getCode() || status == AnswerOrderStateEnum.WaitReply.getCode()) {
            map.put("contact", 1);
        } else if (status == AnswerOrderStateEnum.WaitRefund.getCode()) {
            map.put("states", AnswerOrderStateEnum.OrderFail.getCode());
            map.put("statesname", AnswerOrderStateEnum.OrderFail.getMessage());
            map.put("refund", 1);
        } else if (status == AnswerOrderStateEnum.OrderFail.getCode()) {
            if (price.compareTo(BigDecimal.ZERO) == 0) {
                map.put("refund", 0);
            } else {
                map.put("refund", 2);
            }
        }
    }

    /**
     * app用户端急诊列表
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> appUserPhoneOrderList(long userId, int pageIndex, int pageSize) {
        String sql = " select dpo.id," +
                "          dpo.diagnosis," +
                "          dpo.dis_describe disdescribe, " +
                "          ua.headpic, " +
                "          ua.name, " +
                "          ua.id as userid," +
                "          ue.id as evaluateid," +
                "          dpo.status as states, " +
                "          dpo.actualmoney price,   " +
                "          dp.prescriptionid,   " +
                "          dpo.create_time createtime " +
                "                 from doctor_phone_order dpo " +
                "                        left join user_account ua on dpo.userid = ua.id   " +
                "                        left join doctor_info di on dpo.doctorid = di.doctorid   " +
                "                        left join user_evaluate ue on dpo.id=ue.order_number and ue.ordertype=2 " +
                "            left join (select orderid,max(prescriptionid) prescriptionid " +
                "                       from doc_prescription " +
                "                       where ifnull(delflag, 0) = 0 and order_type=2 " +
                "                       group by orderid) dp on dpo.id = dp.orderid " +
                "                 where ifnull(dpo.delflag,0)=0 and dpo.userid= ? and dpo.status in (2,3,4,5,6,8) ";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        return query(pageSql(sql, " ORDER BY createtime DESC "), pageParams(params, pageIndex, pageSize), (ResultSet rs, int rowNum) -> {
            Map<String, Object> map = resultToMap(rs);
            if (map != null) {
                BigDecimal price = PriceUtil.findPrice(ModelUtil.getLong(map, "price"));
                map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
                String diagnosis = ModelUtil.getStr(map, "diagnosis");
                long evaluateid = ModelUtil.getLong(map, "evaluateid");
                int states = ModelUtil.getInt(map, "states");
                long prescriptionid = ModelUtil.getLong(map, "prescriptionid");
                map.put("statesname", PhoneOrderStateEnum.getValue(states).getMessage());
                phoneOrderResult(map, states, diagnosis, evaluateid, price, prescriptionid);
            }
            return map;
        });
    }

    public void phoneOrderResult(Map<String, Object> map, int status, String guidance, long evaluateid, BigDecimal price, long prescriptionid) {
        //用户订单列表新加字段:
        //guidance:0:不显示,1:未诊断,2:已诊断
        //evaluate:0:不显示,1:未评论,2:以评论
        //record:0:不显示,1:显示问诊记录按钮(针对图文,其他默认0)
        //contact:0:不显示,1:显示医生按钮(针对图文,其他默认0)
        //refund:0:不显示,1:退款中,2:退款成功
        map.put("guidance", 0);
        map.put("evaluate", 0);
        map.put("contact", 0);
        map.put("record", 0);
        map.put("refund", 0);
        map.put("isopen", 0);
        if (status == PhoneOrderStateEnum.OrderSuccess.getCode()) {
            map.put("states", PhoneOrderStateEnum.OrderSuccess.getCode());
            map.put("statesname", PhoneOrderStateEnum.OrderSuccess.getMessage());
            if (StrUtil.isEmpty(guidance) && prescriptionid == 0) {
                map.put("guidance", 1);
            } else {
                map.put("guidance", 2);
            }

            if (evaluateid > 0) {
                map.put("evaluate", 2);
            } else {
                map.put("evaluate", 1);
            }
        } else if (status == PhoneOrderStateEnum.WaitRefund.getCode()) {
            map.put("states", PhoneOrderStateEnum.OrderFail.getCode());
            map.put("statesname", PhoneOrderStateEnum.OrderFail.getMessage());
            map.put("refund", 1);
        } else if (status == PhoneOrderStateEnum.OrderFail.getCode()) {
            if (price.compareTo(BigDecimal.ZERO) == 0) {
                map.put("refund", 0);
            } else {
                map.put("refund", 2);
            }
        }
    }

    /**
     * 订单用户选择的常见病症
     *
     * @param ids
     * @return
     */
    public List<Map<String, Object>> orderAnswerDiseaseList(List<Long> ids) {
        String sql = " select orderid,diseasename value " +
                "from middle_answer_disease " +
                "where orderid in (:ids) and ifnull(delflag,0)=0";
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
        String sql = " select orderid, diseasename value  " +
                "from middle_phone_disease " +
                "where orderid in (:ids)  " +
                "  and ifnull(delflag, 0) = 0 ";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        return queryForList(sql, params);
    }


    /**
     * 订单详细
     *
     * @param orderId
     * @return
     */
    public Map<String, Object> getProblem(long orderId) {
        String sql = "select dpo.userid, " +
                "       dpo.doctorid, " +
                "       dpo.orderno, " +
                "       dpo.states, " +
                "       dpo.issubmit, " +
                "       dpo.is_app_user_online useronline, " +
                "       dpo.is_app_doctor_online doctoronline, " +
                "       ua.name          as username, " +
                "       ua.userno, " +
                "       di.doc_name      as docname, " +
                "       di.in_doc_code      as doctorno, " +
                "       di.work_inst_name   workinstname, " +
                "       ua.headpic, " +
                "       di.doc_photo_url as docphotourl, " +
                "       ua.platform     as uplatform, " +
                "       ua.xg_token     as utoken, " +
                "       de.xg_token     as dtoken, " +
                "       de.platform     as dplatform " +
                "from doctor_problem_order dpo " +
                "       left join user_account ua on dpo.userid = ua.id " +
                "       left join doctor_info di on dpo.doctorid = di.doctorid " +
                "       left join doctor_extends de on dpo.doctorid = de.doctorid " +
                "where dpo.id = ? ";
        return queryForMap(sql, orderId);
    }

    public Map<String, Object> getVideoOrderDetail(long orderid) {
        String sql = "select userid,orderno,doctorid from doctor_video_order where id=? ";
        return queryForMap(sql, orderid);
    }

    public Map<String, Object> getPhoneOrderDetail(long orderid) {
        String sql = "select userid,orderno,doctorid from doctor_phone_order where id=? ";
        return queryForMap(sql, orderid);
    }

    public Map<String, Object> getProblemOrderDetail(long id) {
        String sql = "select dpo.id, " +
                "       dpo.orderno       orderno, " +
                "       dpo.paytype, " +
                "       dpo.states status, " +
                "       dpo.visitcategory," +
                "       dpo.actualmoney, " +
                "       dpo.diagnosis, " +
                "       dpo.userid, " +
                "       dpo.create_time    createtime, " +
                "       b.name diseasetime, " +
                "       dpo.gohospital, " +
                "       dpo.issuredis, " +
                "       dpo.dis_describe disdescribe " +
                "from doctor_problem_order dpo left join basics b on dpo.disease_time=b.customid and b.type=25  " +
                "where ifnull(dpo.delflag, 0) = 0 " +
                "  and dpo.id = ? ";
        Map<String, Object> data = queryForMap(sql, id);
        if (data != null) {
            data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
            int status = ModelUtil.getInt(data, "status");
            data.put("statusname", AnswerOrderStateEnum.getValue(status).getMessage());
        }
        return data;
    }

    public boolean updateAnswers(long orderid) {
        String sql = "update doctor_answer set is_answer=1 where orderid=? and contenttype=? and ifnull(delflag,0)=0";
        List<Object> list = new ArrayList<>();
        list.add(orderid);
        list.add(QAContentTypeEnum.DoctorClose.getCode());
        return update(sql, list) > 0;
    }

    public Map<String, Object> getAnswerNew(long orderid) {
        String sql = "select id,create_time from doctor_answer where ifnull(delflag,0)=0 and orderid=? and contenttype=? order by create_time limit 1";
        List<Object> list = new ArrayList<>();
        list.add(orderid);
        list.add(QAContentTypeEnum.DoctorClose.getCode());
        return queryForMap(sql, list);
    }

    public Map<String, Object> getUserInfo(long orderid) {
        String sql = "select ua.id,ua.name,ua.gender,ua.age,ua.headpic from doctor_problem_order dp  left join user_account ua on dp.userid=ua.id where dp.id=?";
        Map<String, Object> map = queryForMap(sql, orderid);
        if (map != null) {
            map.put("gender", ModelUtil.getInt(map, "gender") == 1 ? "男" : "女");
        }
        return map;
    }

    public List<Map<String, Object>> getAnswerDisease(long orderId) {
        String sql = "select id, diseasename value " +
                "from middle_answer_disease " +
                "where orderid = ? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> getPhoneDisease(long orderId) {
        String sql = "select id, diseasename value " +
                "from middle_phone_disease " +
                "where orderid = ? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        return queryForList(sql, params);
    }

    public Map<String, Object> getDoctorOrderState(long orderid) {
        String sql = "select states from doctor_problem_order where id=?";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        return queryForMap(sql, params);
    }

    public List<Map<String, Object>> getOrderState(long orderid) {
        String sql = "select states,diagnosis from doctor_problem_order where id=?";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        return queryForList(sql, params);
    }

    /**
     * 医生聊天详细列表
     *
     * @return
     */
    public List<Map<String, Object>> getDoctorCurrentAnswerList(long orderid, int pageindex, int pagesize) {
        String sql = " select ua.headpic     userheadpic, " +
                "       ua.name, " +
                "       da.id, " +
                "       da.create_time createtime, " +
                "       da.content, " +
                "       da.contenttime, " +
                "       da.contenttype, " +
                "       da.questionanswertype, " +
                "       di.doc_name    doctorname, " +
                "       di.doc_photo_url   doctorheadpic " +
                "from doctor_answer da " +
                "       left join user_account ua on da.userid = ua.id " +
                "       left join doctor_info di on di.doctorid = da.doctorid " +
                "       left join doctor_problem_order dpo on da.orderid=dpo.id " +
                "where ifnull(da.delflag, 0) = 0  and da.orderid= ? and dpo.states in(2,4,5,6) and da.contenttype not in(5,9,12,14,16) ";
        List<Object> params = new ArrayList<>();
        params.add(orderid);

        return queryForList(pageSql(sql, " order by da.create_time desc,da.id desc "), pageParams(params, pageindex, pagesize));
    }

    /**
     * 医生聊天详细列表
     *
     * @return
     */
    public List<Map<String, Object>> getUserCurrentAnswerList(long orderid, int pageindex, int pagesize) {
        String sql = " select ua.headpic     userheadpic, " +
                "       ua.name, " +
                "       da.id, " +
                "       da.create_time createtime, " +
                "       da.content, " +
                "       da.is_answer isanswer,   " +
                "       da.contenttime, " +
                "       da.contenttype, " +
                "       da.questionanswertype, " +
                "       di.doc_name    doctorname, " +
                "       di.doc_photo_url   doctorheadpic " +
                "from doctor_answer da " +
                "       left join user_account ua on da.userid = ua.id " +
                "       left join doctor_info di on di.doctorid = da.doctorid " +
                "       left join doctor_problem_order dpo on da.orderid=dpo.id " +
                "where ifnull(da.delflag, 0) = 0  and da.orderid= ? and dpo.states in(2,4,5,6,8) and da.contenttype not in(6,7,10,13,15,17,18)";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        return queryForList(pageSql(sql, " order by da.create_time desc,da.id desc "), pageParams(params, pageindex, pagesize));
    }

    /**
     * 订单答案列表(包含历史)
     *
     * @return
     */
    public List<Map<String, Object>> getDoctorHistoryAnswerList(long doctorId, long userId, int pageindex, int pagesize) {
        String sql = " select ua.headpic     userheadpic,   " +
                "            ua.name,   " +
                "            da.id,   " +
                "            da.create_time createtime,   " +
                "            da.content,   " +
                "            da.contenttime,   " +
                "            da.contenttype,   " +
                "            da.questionanswertype,   " +
                "            di.doc_name    doctorname,   " +
                "            di.doc_photo_url   doctorheadpic   " +
                "     from doctor_answer da   " +
                "            left join user_account ua on da.userid = ua.id   " +
                "            left join doctor_info di on di.doctorid = da.doctorid   " +
                "            left join doctor_problem_order dpo on dpo.id= da.orderid " +
                "     where ifnull(da.delflag, 0) = 0  and da.doctorid= ? and da.contenttype not in(5,9,12,14,16) and da.userid=? and dpo.states in(2,4,6) ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(userId);
        return queryForList(pageSql(sql, " order by da.create_time desc,da.id desc "), pageParams(params, pageindex, pagesize));
    }

    public boolean updateNum(long orderid) {
        String sql = "update doctor_problem_order set noread_message=0 where id=?";
        return update(sql, orderid) > 0;
    }


    /**
     * 追加的消息
     *
     * @return
     */
    public List<Map<String, Object>> getAppendDoctorAnswerList(long orderid, long id) {
        String sql = " select ua.headpic     userheadpic, " +
                "       ua.name, " +
                "       da.id, " +
                "       da.create_time createtime, " +
                "       da.content, " +
                "       da.contenttime, " +
                "       da.contenttype, " +
                "       da.questionanswertype, " +
                "       di.doc_name    doctorname, " +
                "       di.doc_photo_url   doctorheadpic " +
                "from doctor_answer da " +
                "       left join user_account ua on da.userid = ua.id " +
                "       left join doctor_info di on di.doctorid = da.doctorid " +
                "       left join doctor_problem_order dpo on da.orderid=dpo.id " +
                "where ifnull(da.delflag, 0) = 0  and da.orderid= ? and dpo.states in(2,4,6) and da.contenttype not in(5,9) and da.id > ? order by da.create_time desc,da.id desc ";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        params.add(id);
        return queryForList(sql, params);
    }

    /**
     * 追加的消息
     *
     * @return
     */
    public List<Map<String, Object>> getDoctorAnswerList(long answerid) {
        String sql = " select ua.headpic     userheadpic, " +
                "       ua.name, " +
                "       da.id, " +
                "       da.create_time createtime, " +
                "       da.content, " +
                "       da.contenttime, " +
                "       da.contenttype, " +
                "       da.questionanswertype, " +
                "       di.doc_name    doctorname, " +
                "       di.doc_photo_url   doctorheadpic " +
                "from doctor_answer da " +
                "       left join user_account ua on da.userid = ua.id " +
                "       left join doctor_info di on di.doctorid = da.doctorid " +
                "       left join doctor_problem_order dpo on da.orderid=dpo.id " +
                "where da.id=?  ";
        List<Object> params = new ArrayList<>();
        params.add(answerid);
        return queryForList(sql, params);
    }

    public boolean updateDoctorAnswer(long pid, int contenttype, long id) {
        String sql = " update doctor_answer set content=?, contenttype=? where id=? ";
        return update(sql, pid, contenttype, id) > 0;
    }

    public boolean updatePresion(long preid) {
        String sql = " update doc_prescription set havehandle=1 where prescriptionid=? ";
        return update(sql,preid) > 0;
    }

    public Map<String, Object> findDoctorAnswer(long preid) {
        String sql = " select id from doctor_answer where content=? and contenttype=18 and ifnull(delflag,0)=0 ";
        return queryForMap(sql,preid);
    }

    /**
     * 追加的消息
     *
     * @return
     */
    public List<Map<String, Object>> getAppendDoctorSocketAnswerList(long orderid, long id) {
        String sql = " select ua.headpic     userheadpic, " +
                "       ua.name, " +
                "       da.id, " +
                "       da.create_time createtime, " +
                "       da.content, " +
                "       da.contenttime, " +
                "       da.contenttype, " +
                "       da.questionanswertype, " +
                "       di.doc_name    doctorname, " +
                "       di.doc_photo_url   doctorheadpic " +
                "from doctor_answer da " +
                "       left join user_account ua on da.userid = ua.id " +
                "       left join doctor_info di on di.doctorid = da.doctorid " +
                "       left join doctor_problem_order dpo on da.orderid=dpo.id " +
                "where ifnull(da.delflag, 0) = 0  and da.orderid= ? and dpo.states in(2,4,6) and da.contenttype not in(5,9,12,14,16) and da.id = ? order by da.create_time desc,da.id desc ";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        params.add(id);
        return queryForList(sql, params);
    }

    /**
     * 订单答案列表(包含历史)
     *
     * @return
     */
    public List<Map<String, Object>> getUserHistoryAnswerList(long doctorId, long userId, int pageindex, int pagesize) {
        String sql = " select ua.headpic     userheadpic,   " +
                "            ua.name,   " +
                "            da.id,   " +
                "            da.create_time createtime,   " +
                "            da.content,   " +
                "            da.is_answer isanswer,   " +
                "            da.contenttime,   " +
                "            da.contenttype,   " +
                "            da.questionanswertype,   " +
                "            di.doc_name    doctorname,   " +
                "            di.doc_photo_url   doctorheadpic   " +
                "     from doctor_answer da   " +
                "            left join user_account ua on da.userid = ua.id   " +
                "            left join doctor_info di on di.doctorid = da.doctorid   " +
                "            left join doctor_problem_order dpo on dpo.id= da.orderid " +
                "     where ifnull(da.delflag, 0) = 0  and da.doctorid= ? and da.contenttype not in(6,7,10,13,15,17,18) and da.userid=? and dpo.states in(2,4,5,6,8) ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(userId);
        return queryForList(pageSql(sql, " order by da.create_time desc,da.id desc "), pageParams(params, pageindex, pagesize));
    }

    /**
     * 订单推荐回复列表(包含历史)
     *
     * @return
     */
    public List<Map<String, Object>> getAppendUserAnswerList(long doctorId, long userId, long id) {
        String sql = " select ua.headpic     userheadpic,   " +
                "            ua.name,   " +
                "            da.id,   " +
                "            da.create_time createtime,   " +
                "            da.content,   " +
                "            da.contenttime,   " +
                "            da.contenttype,   " +
                "            da.questionanswertype,   " +
                "            di.doc_name    doctorname,   " +
                "            di.doc_photo_url   doctorheadpic   " +
                "     from doctor_answer da   " +
                "            left join user_account ua on da.userid = ua.id   " +
                "            left join doctor_info di on di.doctorid = da.doctorid   " +
                "            left join doctor_problem_order dpo on dpo.id= da.orderid " +
                "     where ifnull(da.delflag, 0) = 0  and da.doctorid= ? and da.contenttype not in(6,7,10) and da.userid=? and dpo.states in(2,4,6) and da.id > ? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(userId);
        params.add(id);
        if (id == 0) {
            sql += " and da.create_time > ? ";
            params.add(UnixUtil.getNowTimeStamp());
        }
        sql += " order by da.create_time desc,da.id desc  ";
        return queryForList(sql, params);
    }

    /**
     * 订单推荐回复列表(单个订单)
     *
     * @return
     */
    public List<Map<String, Object>> getAppendUserAnswerList(long orderId, long id) {
        String sql = " select ua.headpic     userheadpic,   " +
                "            ua.name,   " +
                "            da.id,   " +
                "            da.create_time createtime,   " +
                "            da.content,   " +
                "            da.contenttime,   " +
                "            da.contenttype,   " +
                "            da.questionanswertype,   " +
                "            di.doc_name    doctorname,   " +
                "            di.doc_photo_url   doctorheadpic   " +
                "     from doctor_answer da   " +
                "            left join user_account ua on da.userid = ua.id   " +
                "            left join doctor_info di on di.doctorid = da.doctorid   " +
                "            left join doctor_problem_order dpo on dpo.id= da.orderid " +
                "     where ifnull(da.delflag, 0) = 0  and da.orderid=? and da.contenttype not in(6,7,10) and dpo.states in(2,4,6) and da.id > ? ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(id);
        if (id == 0) {
            sql += " and da.create_time > ? ";
            params.add(UnixUtil.getNowTimeStamp());
        }
        sql += " order by da.create_time desc,da.id desc  ";
        return queryForList(sql, params);
    }

    /**
     * 订单推荐回复列表(单个订单)
     *
     * @return
     */
    public List<Map<String, Object>> getAppendUserSocketAnswerList(long orderId, long id) {
        String sql = " select ua.headpic     userheadpic,   " +
                "            ua.name,   " +
                "            da.id,   " +
                "            da.create_time createtime,   " +
                "            da.content,   " +
                "            da.contenttime,   " +
                "            da.contenttype,   " +
                "            da.questionanswertype,   " +
                "            di.doc_name    doctorname,   " +
                "            di.doc_photo_url   doctorheadpic   " +
                "     from doctor_answer da   " +
                "            left join user_account ua on da.userid = ua.id   " +
                "            left join doctor_info di on di.doctorid = da.doctorid   " +
                "            left join doctor_problem_order dpo on dpo.id= da.orderid " +
                "     where ifnull(da.delflag, 0) = 0  and da.orderid=? and da.contenttype not in(6,7,10,13,15,17,18) and dpo.states in(2,4,6) and da.id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(id);
        return queryForList(sql, params);
    }

    /**
     * 用户显示模板列表
     *
     * @return
     */
    public List<Map<String, Object>> userInfoList(List<Long> ids) {
        String sql = " select da.id,ua.name username,ua.gender,cg.value gendername,ua.birthday,diseasename,dpo.dis_describe disdescribe from doctor_answer da left join middle_answer_disease mad on da.orderid=mad.orderid " +
                "left join user_account ua on da.content=ua.id " +
                "left join doctor_problem_order dpo on da.orderid=dpo.id " +
                "left join code_gender cg on ua.gender=cg.code " +
                "where da.id in (:ids)  ";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        return queryForList(sql, params);
    }

    /**
     * 用户显示模板列表
     *
     * @return
     */
    public List<Map<String, Object>> userAnswerDiseaseTemplateList(List<Long> ids) {
        String sql = " select da.id, da.orderid, dpt.id templateid, dpt.usertitle, dpt.doctortitle, dpt.checkbox,dpt.choiceflag,dpta.id answerid,dpta.content,ua.useranswerid,ua.content anscontent " +
                "from doctor_answer da " +
                "       left join doctor_problem_template dpt on da.content = dpt.id and ifnull(dpt.delflag, 0) = 0 " +
                "       left join doctor_problem_template_answer dpta on dpt.id = dpta.templateid " +
                "       left join (select dpta.id,upta.answerid useranswerid,dpta.content " +
                "                  from user_problem_template_answer upta " +
                "                         left join doctor_problem_template_answer dpta " +
                "                           on upta.templateid = dpta.templateid and upta.answerid = dpta.id and ifnull(upta.delflag,0)=0 ) ua on dpta.id=ua.id " +
                "where da.id in (:ids) " +
                "  and ifnull(da.delflag, 0) = 0 order by da.id,dpt.id,dpta.id ";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        return queryForList(sql, params);
    }

    /**
     * 医生显示模板列表
     *
     * @return
     */
    public List<Map<String, Object>> doctorAnswerDiseaseTemplateList(List<Long> ids) {
        String sql = " select da.id, da.orderid, dpt.id templateid, dpt.usertitle, dpt.doctortitle, dpt.checkbox,upta.id answerid,dpta.content " +
                "from doctor_answer da " +
                "       left join doctor_problem_template dpt on da.content = dpt.id and ifnull(dpt.delflag, 0) = 0 " +
                "       left join user_problem_template_answer upta on dpt.id=upta.templateid and ifnull(upta.delflag,0)=0 " +
                "    left join doctor_problem_template_answer dpta on upta.answerid=dpta.id " +
                "where da.id in (:ids) and ifnull(da.delflag, 0) = 0 order by da.id  ";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        return queryForList(sql, params);
    }

    /**
     * 医生显示模板列表
     *
     * @return
     */
    public List<Map<String, Object>> doctorAnswerDiseaseTemplateList(long orderId) {
        String sql = " select da.id, da.orderid, dpt.id templateid, dpt.usertitle, dpt.doctortitle, dpt.checkbox,upta.id answerid,dpta.content " +
                "from doctor_answer da " +
                "       left join doctor_problem_template dpt on da.content = dpt.id and ifnull(dpt.delflag, 0) = 0 " +
                "       left join user_problem_template_answer upta on dpt.id=upta.templateid and ifnull(upta.delflag,0)=0 " +
                "    left join doctor_problem_template_answer dpta on upta.answerid=dpta.id " +
                "where da.orderid =? and da.contenttype=6 and ifnull(da.delflag, 0) = 0 order by da.id  ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        return queryForList(sql, params);
    }

    /**
     * 订单处方类型处方列表
     *
     * @return
     */
    public List<Map<String, Object>> prescriptionList(List<Long> ids) {
        String sql = " select da.id,prescriptionid,pres_photo_url presphotourl,dp.modify_time modifytime from doctor_answer da left join doc_prescription dp on da.content=dp.prescriptionid where da.id in (:ids) and ifnull(da.delflag, 0) = 0  ";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        return queryForList(sql, params);
    }

    /**
     * 订单处方类型处方列表
     *
     * @return
     */
    public Map<String, Object> getPrescription(long prescriptionid) {
        String sql = " select prescriptionid,pres_photo_url presphotourl from  doc_prescription  where prescriptionid=? ";
        return queryForMap(sql, prescriptionid);
    }

    /**
     * 添加语音回答
     *
     * @return
     */
    public long getAnswerCount(long orderId) {
        String sql = "   select count(id) count from  doctor_answer where contenttype=10 and ifnull(delflag,0)=0 and orderid=? ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    /**
     * 添加语音回答
     * params questionaAnswerType 0:用户 1：医生 2：系统
     *
     * @return
     */
    public long addAnswer(long userId, long doctorId, long orderId, String content, long contenttime, int contentType, int questionaAnswerType) {
        String sql = "   insert into doctor_answer (content,contenttime, userid, doctorid, orderid, delflag, create_time, status, contenttype,questionanswertype) " +
                " values (   " +
                "   ?, ?, ?, ?, ?, ?, ?, ?, ? , ?" +
                " )  ";
        List<Object> params = new ArrayList<>();
        params.add(content);
        params.add(contenttime);
        params.add(userId);
        params.add(doctorId);
        params.add(orderId);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(1);
        params.add(contentType);
        params.add(questionaAnswerType);
        return insert(sql, params, "id");
    }

    public void updateOrderTemplateChoiceflag(long orderId) {
        String sql = " update doctor_problem_template set choiceflag=1 where orderid=? ";
        update(sql, orderId);
    }

    /**
     * 添加语音回答
     *
     * @return
     */
    public long addAnswer(long userId, long doctorId, long orderId, String content, int contentType, int questionaAnswerType) {
        return addAnswer(userId, doctorId, orderId, content, 0, contentType, questionaAnswerType);
    }

    /**
     * 多选时删除上条回答
     *
     * @return
     */
    public long delAnswer(long orderId, String content) {
        String sql = "   update doctor_answer set delflag=1, modify_time=? where orderid=? and content=? and contenttype=6 ";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(orderId);
        params.add(content);
        return update(sql, params);
    }

    /**
     * 添加医生语音回答
     *
     * @return
     */
    public boolean updateAnswer(String content, long answerId) {
        String sql = " update doctor_answer set content=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(content);
        params.add(answerId);
        return update(sql, params) > 0;
    }

    /**
     * 将订单状态改为待回复
     *
     * @return
     */
    public boolean updateAnswer(long id) {
        String sql = " update doctor_problem_order set states=6 where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean updateAnswerread(long id) {
        String sql = " update doctor_problem_order set noread_message=noread_message+1 where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 急诊
     *
     * @param doctorid 医生id
     * @return
     */
    public Map<String, Object> getDoctorPrices(long doctorid) {
        String sql = "";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return queryForMap(sql, params);
    }


    /**
     * 添加医生价格
     *
     * @param doctorid 医生id
     * @param type     1：问诊，3：一键求助
     * @param price    价格
     * @return
     */
    public boolean addDoctorPrice(long doctorid, int type, BigDecimal price) {
//        String sql = " INSERT INTO doc_med_price_list(med_class_id,med_class_name,price,doctorid,open,delflag,create_time)values (?,?,?,?,?,?,?) ";
//        List<Object> params = new ArrayList<>();
//        params.add(type);
//        params.add(type == 1 ? "语音问诊" : "一键求助");
//        params.add(price);
//        params.add(doctorid);
//        params.add(1);
//        params.add(0);
//        params.add(UnixUtil.getNowTimeStamp());
        //return insert(sql, params) > 0;
        return false;
    }

    /**
     * 修改医生价格
     *
     * @param doctorid 医生id
     * @param type     1：问诊，3：一键求助
     * @param price    价格
     * @return
     */
    public boolean updateDoctorPrice(long doctorid, int type, BigDecimal price) {
        String sql = " UPDATE doc_med_price_list SET price=?,whetheropen=?,modify_time=? where doctorId=? and med_class_id=?  ";
        List<Object> params = new ArrayList<>();
        params.add(PriceUtil.addPrice(price));
        params.add(1);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorid);
        params.add(type);
        return insert(sql, params) > 0;
    }

    public void addWXNewsSend(long userId, String sourceId, String appId, String appSecret,
                              String templateId, String url, String first,
                              String keyword1, String keyword2, String keyword3, String remark,
                              String msgid, String keyword4, String keyword5, int typekeyid) {
        String sql = "INSERT INTO YR_WX_NewsSend (openid, userid, usertype, newstype, sourceid, appid, appsecret,  " +
                "                            templateid, url, first,  " +
                "                            keyword1, keyword2, keyword3, remark, msgid, sendtime,  " +
                "                            CreateTime, keyword4, keyword5, typekeyid,status)  " +
                "  SELECT  " +
                "    openid,  " +
                "    userid,  " +
                "    1,  " +
                "    6,  " +
                "    ?,  " +
                "    ?,  " +
                "    ?,  " +
                "    ?,  " +
                "    ?,  " +
                "    ?,  " +
                "    ?,  " +
                "    ?,  " +
                "    ?,  " +
                "    ?,  " +
                "    ?,  " +
                "    ?,  " +
                "    ?,  " +
                "    ?,  " +
                "    ?,  " +
                "    ?,   " +
                "    10  " +
                "  FROM User_Account  " +
                "  WHERE userid = ?";
        List<Object> params = new ArrayList<>();
        params.add(sourceId);
        params.add(appId);
        params.add(appSecret);
        params.add(templateId);
        params.add(url);
        params.add(first);
        params.add(keyword1);
        params.add(keyword2);
        params.add(keyword3);
        params.add(remark);
        params.add(msgid);
        params.add(UnixUtil.timeStampDate(UnixUtil.getNowTimeStamp(), "yyyy-MM-dd HH:mm:ss"));
        params.add(UnixUtil.timeStampDate(UnixUtil.getNowTimeStamp(), "yyyy-MM-dd HH:mm:ss"));
        params.add(keyword4);
        params.add(keyword5);
        params.add(typekeyid);
        params.add(userId);
        update(sql, params);
    }

    /**
     * 系统模板列表
     *
     * @return
     */
    public List<Map<String, Object>> getTemplateList() {
        String sql = " select id,usertitle,doctortitle,checkbox from disease_template where ifnull(delflag,0)=0 order by sort desc ";
        return queryForList(sql);
    }

    /**
     * 系统模板对应的答案列表
     *
     * @param id
     * @return
     */
    public List<Map<String, Object>> getTemplateAnswerList(long id) {
        String sql = " select id,content from disease_template_answer where ifnull(delflag,0)=0 and templateid=? order by id ";
        return queryForList(sql, id);
    }

    public long deleteAnwser(long id) {
        String sql = "update disease_template_answer set delflag=1 where id=?";
        return update(sql, id);
    }


    /**
     * 订单模板详细
     *
     * @param id
     * @return
     */
    public Map<String, Object> getProblemTemplate(long id) {
        String sql = " select id,usertitle,doctortitle,checkbox from doctor_problem_template where ifnull(delflag,0)=0 and id=? ";
        return queryForMap(sql, id);
    }

    /**
     * 订单模板详细
     *
     * @param id
     * @return
     */
    public Map<String, Object> getProblemTemplateOrder(long id) {
        String sql = " select dpt.id,dpo.states,dpt.usertitle,dpt.doctortitle,dpt.checkbox from doctor_problem_template dpt left join doctor_problem_order dpo on dpt.orderid=dpo.id where ifnull(dpt.delflag,0)=0 and dpt.id=? ";
        return queryForMap(sql, id);
    }

    /**
     * 添加订单模板问题
     *
     * @param orderId
     * @param usertitle
     * @param doctortitle
     * @param checkbox
     * @return
     */
    public long addProblemTemplate(long orderId, String usertitle, String doctortitle, int checkbox) {
        String sql = " insert into doctor_problem_template (orderid,usertitle,doctortitle,checkbox,delflag,create_time)VALUES (?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(usertitle);
        params.add(doctortitle);
        params.add(checkbox);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params, "id");
    }

    /**
     * 添加模板问题答案
     *
     * @param templateId
     * @param content
     */
    public void addProblemTemplateAnswer(long templateId, String content) {
        String sql = " insert into doctor_problem_template_answer (templateid,content,delflag,create_time)VALUES (?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(templateId);
        params.add(content);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        insert(sql, params);
    }

    /**
     * 相同模板和问题回答的次数
     *
     * @param templateId
     * @param answerId
     * @return
     */
    public long getUserAnserCount(long templateId, long answerId) {
        String sql = " select count(id) count from user_problem_template_answer where ifnull(delflag,0)=0 and templateid=? and answerid=? ";
        List<Object> params = new ArrayList<>();
        params.add(templateId);
        params.add(answerId);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 模板回答的次数
     *
     * @param templateId
     * @return
     */
    public long getUserAnserCount(long templateId) {
        String sql = " select count(id) count from user_problem_template_answer where ifnull(delflag,0)=0 and templateid=? ";
        List<Object> params = new ArrayList<>();
        params.add(templateId);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 添加用户回答模板对应的问题
     *
     * @param templateId
     * @param answerId
     * @return
     */
    public boolean addUserAnser(long templateId, long answerId) {
        String sql = " insert into user_problem_template_answer (templateid, answerid, delflag, create_time) VALUES (?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(templateId);
        params.add(answerId);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getUserAnswerDetailed(long id) {
        String sql = "select dpo.id, " +
                "       dpo.orderno, " +
                "       dpo.paytype, " +
                "       dpo.visitcategory," +
                "       dpo.states status, " +
                "       dpo.actualmoney, " +
                "       dpo.diagnosis, " +
                "       dpo.doctorid, " +
                "       b.name diseasetime, " +
                "       dpo.gohospital, " +
                "       dpo.issuredis, " +
                "       dpo.dis_describe disdescribe, " +
                "       dpo.create_time     createtime " +
                " from doctor_problem_order dpo " +
                " left join basics b on dpo.disease_time=b.customid and b.type=25 " +
                "where ifnull(dpo.delflag, 0) = 0 " +
                "  and dpo.id = ?";
        Map<String, Object> data = queryForMap(sql, id);
        if (data != null) {
            data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
            int status = ModelUtil.getInt(data, "status");
            data.put("statusname", AnswerOrderStateEnum.getValue(status).getMessage());
        }
        return data;
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getUserPhoneDetailed(long id) {
        String sql = "select dpo.id, " +
                "       dpo.order_no       orderno, " +
                "       dpo.paytype, " +
                "       dpo.user_phone userphone," +
                "       dpo.visitcategory," +
                "       dpo.status, " +
                "       dpo.actualmoney, " +
                "       dpo.diagnosis, " +
                "       dpo.subscribe_time subscribetime, " +
                "       dpo.doctorid, " +
                "       b.name diseasetime, " +
                "       dpo.gohospital, " +
                "       dpo.issuredis, " +
                "       dpo.dis_describe disdescribe, " +
                "       dpo.create_time    createtime " +
                "from doctor_phone_order dpo " +
                " left join basics b on dpo.disease_time=b.customid and b.type=25 " +
                "where ifnull(dpo.delflag, 0) = 0 " +
                "  and dpo.id = ? ";
        Map<String, Object> data = queryForMap(sql, id);
        if (data != null) {
            data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
            int status = ModelUtil.getInt(data, "status");
            data.put("statusname", PhoneOrderStateEnum.getValue(status).getMessage());
            if (ModelUtil.getLong(data, "subscribetime") == 0) {
                data.put("subscribetime", ModelUtil.getLong(data, "createtime"));
            }
        }
        return data;
    }

    public Map<String, Object> getDoctor(long doctorId) {
        String sql = " select  di.doctorid id, " +
                "       di.doc_name       name, " +
                "       doc_photo_url     headpic, " +
                "       di.work_inst_name hospital, " +
                "       cd.value          department, " +
                "       cdt.value         title " +
                "from doctor_info di " +
                "       left join code_department cd on di.department_id = cd.id " +
                "       left join code_doctor_title cdt on di.title_id = cdt.id " +
                "where doctorid= ? ";
        return queryForMap(sql, doctorId);
    }


    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getAnswerOrder(long id) {
        String sql = "select dpo.id, " +
                "       dpo.orderno, " +
                "       dpo.paytype, " +
                "       dpo.paystatus, " +
                "       dpo.issubmit, " +
                "       dpo.diagnosis, " +
                "       dpo.states, " +
                "       b.name              statename, " +
                "       dpo.actualmoney, " +
                "       dpo.marketprice, " +
                "       dpo.originalprice, " +
                "       dpo.discount, " +
                "       dpo.visitcategory, " +
                "       0                as favorableprice, " +
                "       dpo.userid, " +
                "       ua.openid, " +
                "       dpo.doctorid, " +
                "       dpo.create_time     createtime, " +
                "       di.doc_name      as doctorname, " +
                "       di.in_doc_code      as doccode, " +
                "       di.examine, " +
                "       ua.name          as username, " +
                "       ua.userno          as userno, " +
                "       di.doc_photo_url as  docphotourl, " +
                "       di.in_doc_code as  doccode, " +
                "       ua.walletbalance, " +
                "       ua.kangyang_userid as kangyanguserid,   " +
                "       ua.headpic       as headpic, " +
                "       ua.platform      as uplatform, " +
                "       ua.xg_token      as utoken, " +
                "       de.platform      as dplatform, " +
                "       de.xg_token      as dtoken " +
                " from doctor_problem_order dpo " +
                "       left join user_account ua on dpo.userid = ua.id " +
                "       left join basics b on dpo.states = b.customid and b.type = 4 " +
                "       left join doctor_info di on dpo.doctorid = di.doctorid " +
                "       left join doctor_extends de on de.doctorid = di.doctorid " +
                "where ifnull(dpo.delflag, 0) = 0 " +
                "  and dpo.id = ?";
        Map<String, Object> data = queryForMap(sql, id);
        if (data != null) {
            data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
            data.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(data, "walletbalance")));
            data.put("marketprice", PriceUtil.findPrice(ModelUtil.getLong(data, "marketprice")));
            data.put("originalprice", PriceUtil.findPrice(ModelUtil.getLong(data, "originalprice")));
        }
        return data;
    }

    /**
     * 订单处方列表
     *
     * @param orderId
     * @return
     */
    public List<Map<String, Object>> getPrescriptionList(long orderId) {
        String sql = " select dp.prescriptionid,dp.diagnosis,dp.create_time createtime from doctor_answer da left join doc_prescription dp on da.content=dp.prescriptionid " +
                "where da.orderid=? and da.contenttype=4 ";
        return queryForList(sql, orderId);
    }

    /**
     * 订单处方列表
     *
     * @param orderId
     * @return
     */
    public Map<String, Object> getPrescriptionByOrderId(long orderId) {
        String sql = " select prescriptionid from doc_prescription where orderid=? order by prescriptionid desc limit 1 ";
        return queryForMap(sql, orderId);
    }

    public Map<String, Object> getAnswerPrice(long doctorId) {
        String sql = " select doctorid,price,whetheropen from doc_med_price_list where doctorid=? and med_class_id=1 and ifnull(delflag,0)=0 ";
        Map<String, Object> data = queryForMap(sql, doctorId);
        if (data != null) {
            data.put("price", PriceUtil.findPrice(ModelUtil.getLong(data, "price", 0)));
        }
        return data;
    }

    /**
     * 电话
     *
     * @param doctorId 医生ID
     * @param
     * @return
     */
    public Map<String, Object> getPhonePrice(long doctorId) {
        String sql = "select dmpl.id, " +
                "       dmpl.doctorid, " +
                "       dmpl.price, " +
                "       dmpl.whetheropen " +
                "from doc_med_price_list dmpl " +
                "       inner join doctor_onduty do " +
                "         on dmpl.doctorid = do.doctorid and ifnull(do.delflag, 0) = 0 and ifnull(examine_state, 0) = 2 " +
                "              and do.visiting_start_time > UNIX_TIMESTAMP() * 1000 " +
                "where dmpl.doctorid = ? " +
                "  and dmpl.med_class_id = 3 and ifnull(dmpl.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        Map<String, Object> data = queryForMap(sql, params);
        if (data != null) {
            data.put("price", PriceUtil.findPrice(ModelUtil.getLong(data, "price", 0)));
        }
        return data;
    }

    /**
     * 电话
     *
     * @return
     */
    public Map<String, Object> getVideoPrice(long doctorid) {
        String sql = " select distinct dmpl.doctorid,dmpl.price,dmpl.whetheropen " +
                "from doc_med_price_list dmpl " +
                "       inner join doctor_scheduling ds on dmpl.doctorid = ds.doctorid and ifnull(ds.delflag, 0) = 0 " +
                "where dmpl.doctorid = ? " +
                "  and dmpl.med_class_id = 4 " +
                "  and ifnull(dmpl.delflag, 0) = 0 " +
                "  and ds.visiting_start_time > unix_timestamp() * 1000 ";
        Map<String, Object> data = queryForMap(sql, doctorid);
        if (data != null) {
            data.put("price", PriceUtil.findPrice(ModelUtil.getLong(data, "price", 0)));
        }
        return data;
    }

    /**
     * 急诊
     *
     * @return
     */
    public Map<String, Object> getDepartmentPrice() {
        String sql = " select emergency_price price from doctor_emergency_clinic_price where ifnull(delflag,0)=0 ";
        Map<String, Object> data = queryForMap(sql);
        if (data != null) {
            data.put("price", PriceUtil.findPrice(ModelUtil.getLong(data, "price", 0)));
        }
        return data;
    }

    /**
     * 急诊
     *
     * @return
     */
    public Map<String, Object> getOutpatientPrice() {
        String sql = " select outpatient_price price from doctor_emergency_clinic_price where ifnull(delflag,0)=0 ";
        Map<String, Object> data = queryForMap(sql);
        if (data != null) {
            data.put("price", PriceUtil.findPrice(ModelUtil.getLong(data, "price", 0)));
        }
        return data;
    }


    public Map<String, Object> getPrice() {
        String sql = " select emergency_price price from doctor_emergency_clinic_price where ifnull(delflag,0)=0 ";
        Map<String, Object> data = queryForMap(sql);
        if (data != null) {
            data.put("price", PriceUtil.findPrice(ModelUtil.getLong(data, "price", 0)));
        }
        return data;
    }

    /**
     * 用户取消订单
     *
     * @param orderId 订单id
     */
    public boolean closeOrderUserAnswer(long orderId) {
        String sql = "UPDATE doctor_problem_order " +
                "SET states = 3,ModifyTime=? " +
                "WHERE Id = ?";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(orderId);
        return update(sql, params) > 0;
    }

    /**
     * 模板列表
     *
     * @param usertitle
     * @param doctortitle
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getDiseaseTemplateList(String usertitle, String doctortitle, int pageIndex, int pageSize) {
        String sql = " select * " +
                "from (select dt.id, " +
                "             dt.usertitle, " +
                "             dt.doctortitle, " +
                "             sort, " +
                "             dt.delflag, " +
                "             dt.create_time            createtime, " +
                "             ifnull(dt.checkbox, 0)    checkbox, " +
                "             group_concat(dta.content) content " +
                "      from disease_template dt " +
                "             left join disease_template_answer dta on dt.id = dta.templateid and ifnull(dta.delflag, 0) = 0 " +
                "      group by dt.id)mid " +
                "where ifnull(delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(usertitle)) {
            sql += " and usertitle like ? ";
            params.add(String.format("%%%s%%", usertitle));
        }
        if (!StrUtil.isEmpty(doctortitle)) {
            sql += " and doctortitle like ? ";
            params.add(String.format("%%%s%%", doctortitle));
        }
        return queryForList(pageSql(sql, " order by sort desc,createtime desc "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 模板数量
     *
     * @param usertitle
     * @param doctortitle
     * @return
     */
    public long getDiseaseTemplateCount(String usertitle, String doctortitle) {
        String sql = " select count(id) count from disease_template where ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(usertitle)) {
            sql += " and usertitle like ? ";
            params.add(String.format("%%%s%%", usertitle));
        }
        if (!StrUtil.isEmpty(doctortitle)) {
            sql += " and doctortitle like ? ";
            params.add(String.format("%%%s%%", doctortitle));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public Map<String, Object> getDiseaseTemplate(long id) {
        String sql = " select id,usertitle,doctortitle,sort,create_time createtime,checkbox from disease_template where ifnull(delflag,0)=0 and id=? ";
        return queryForMap(sql, id);
    }

    /**
     * 添加模板
     *
     * @param usertitle
     * @param doctortitle
     * @param sort
     * @param checkbox
     * @param createUser
     * @return
     */
    public long addDiseaseTemplate(String usertitle, String doctortitle, int sort, int checkbox, long createUser) {
        String sql = " insert into disease_template(usertitle,doctortitle,sort,delflag,create_time,checkbox,create_user)values(?,?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(usertitle);
        params.add(doctortitle);
        params.add(sort);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(checkbox);
        params.add(createUser);
        return insert(sql, params, "id");
    }

    /**
     * 修改模板
     *
     * @param id
     * @param usertitle
     * @param doctortitle
     * @param sort
     * @param checkbox
     * @param createUser
     * @return
     */
    public boolean updateDiseaseTemplate(long id, String usertitle, String doctortitle, int sort, int checkbox, long createUser) {
        String sql = " update disease_template set usertitle=?,doctortitle=?,sort=?,modify_time=?,checkbox=?,modify_user=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(usertitle);
        params.add(doctortitle);
        params.add(sort);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(checkbox);
        params.add(createUser);
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 删除模板
     *
     * @param id
     * @param createUser
     * @return
     */
    public boolean delDiseaseTemplate(long id, long createUser) {
        String sql = " update disease_template set modify_time=?,delflag=?,modify_user=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(1);
        params.add(createUser);
        params.add(id);
        return update(sql, params) > 0;
    }


    /**
     * 添加模板答案
     *
     * @param templateid
     * @param content
     * @param createUser
     * @return
     */
    public boolean addTemplateAnswer(long templateid, String content, long createUser) {
        String sql = " insert into disease_template_answer(templateid,content,delflag,create_time,create_user)values(?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(templateid);
        params.add(content);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(createUser);
        return insert(sql, params) > 0;
    }


    /**
     * 删除模板答案
     *
     * @param templateid
     * @param createUser
     * @return
     */
    public boolean delTemplateAnswer(long templateid, long createUser) {
        String sql = " update disease_template_answer set modify_time=?,delflag=?,modify_user=? where templateid=? ";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(1);
        params.add(createUser);
        params.add(templateid);
        return update(sql, params) > 0;
    }

    public Map<String, Object> getDoctorAnswer(long id) {
        String sql = " select ua.headpic     userheadpic,   " +
                "            ua.name,   " +
                "            ua.id,   " +
                "            ua.userno,   " +
                "            da.id,   " +
                "            da.orderid,   " +
                "            da.create_time createtime,   " +
                "            da.content,   " +
                "            da.contenttime,   " +
                "            da.contenttype,   " +
                "            da.questionanswertype,   " +
                "            di.doc_name    doctorname,   " +
                "            di.in_doc_code    doctorno,   " +
                "            di.doc_photo_url   doctorheadpic   " +
                "     from doctor_answer da   " +
                "            left join user_account ua on da.userid = ua.id   " +
                "            left join doctor_info di on di.doctorid = da.doctorid   " +
                "     where ifnull(da.delflag, 0) = 0  and da.id= ?";
        return queryForMap(sql, id);
    }

    public Map<String, Object> getUserDoctorAnswerOrder(long userId, long doctorid) {
        String sql = " select id,orderno,userid,doctorid from doctor_problem_order where ifnull(delflag,0)=0 and states in(2,6) and userid=? and doctorid=? and visitcategory =? ";
        List<Object> list = new ArrayList<>();
        list.add(userId);
        list.add(doctorid);
        list.add(VisitCategoryEnum.graphic.getCode());
        return queryForMap(sql, list);
    }

    public Map<String, Object> getUserAnswerOrder(long userId) {
        String sql = " select id,orderno,userid,doctorid from doctor_problem_order where ifnull(delflag,0)=0 and states in(2,6) and userid=? and visitcategory =? ";
        List<Object> list = new ArrayList<>();
        list.add(userId);
        list.add(VisitCategoryEnum.Outpatient.getCode());
        return queryForMap(sql, list);
    }

    public Map<String, Object> getUserDoctorPhoneOrder(long userId, long doctorId) {
        String sql = " select id,order_no orderno,userid,doctorid from doctor_phone_order where userid=? and doctorid=? and status in(2,3) and visitcategory=? and ifnull(delflag,0)=0 ";
        return queryForMap(sql, userId, doctorId, VisitCategoryEnum.phone.getCode());
    }

    public Map<String, Object> getUserPhoneOrder(long userId) {
        String sql = " select id,order_no orderno,userid,doctorid from doctor_phone_order where userid=? and status in(2,3) and visitcategory=? and ifnull(delflag,0)=0 ";
        return queryForMap(sql, userId, VisitCategoryEnum.department.getCode());
    }

    public Map<String, Object> getUserVideoOrder(long userId) {
        String sql = " select id,orderno,userid,doctorid from doctor_video_order where userid=? and status in(2,3) and ifnull(delflag,0)=0 ";
        return queryForMap(sql, userId);
    }


    public Map<String, Object> getAnswerOrderByOrderNo(String orderNo) {
        String sql = "select dpo.id, " +
                "       dpo.userid, " +
                "       dpo.doctorid, " +
                "       dpo.paystatus, " +
                "       dpo.create_time as createtime, " +
                "       ua.platform     as uplatform, " +
                "       ua.xg_token     as utoken, " +
                "       ua.name, " +
                "       de.xg_token     as dtoken, " +
                "       de.platform     as dplatform " +
                "from doctor_problem_order dpo " +
                "       left join user_account ua on dpo.userid =ua.id " +
                "       left join doctor_extends de on dpo.doctorid = de.doctorid " +
                "where dpo.orderno = ? ";
        List<Object> params = new ArrayList<>();
        params.add(orderNo);
        return queryForMap(sql, params);
    }

    /**
     * 更改问诊订单状态
     *
     * @param out_trade_no
     */
    public void updateAnswerStatusSuccess(String out_trade_no, String trade_no, int payType) {
        String sql = " update doctor_problem_order set paystatus=?,paytime=?,paytype=?,states=?,paytransactionid=? where orderno=? ";
        List<Object> params = new ArrayList<>();
        params.add(PayStateEnum.Paid.getCode());
        params.add(UnixUtil.getNowTimeStamp());
        params.add(payType);
        params.add(AnswerOrderStateEnum.Paid.getCode());
        params.add(trade_no);
        params.add(out_trade_no);
        update(sql, params);
    }

    public Map<String, Object> findById(long doctorid) {
        String sql = "select doo_tel,doc_name from doctor_info where doctorid=?";
        return queryForMap(sql, doctorid);
    }

    public Map<String, Object> getDoctorPhoneOrderByOrderNo(String orderNo) {
        String sql = "select dpo.id, " +
                "       dpo.userid, " +
                "       ua.name username, " +
                "       dpo.doctorid, " +
                "       dpo.paystatus, " +
                "       dpo.callnum, " +
                "       dpo.actualmoney, " +
                "       dpo.visitcategory, " +
                "       dpo.paytype, " +
                "       dpo.create_time as createtime, " +
                "       dpo.subscribe_time as subscribetime, " +
                "       ua.platform     as uplatform, " +
                "       ua.xg_token     as utoken, " +
                "       de.xg_token     as dtoken, " +
                "       de.platform     as dplatform " +
                "from doctor_phone_order dpo " +
                "       left join user_account ua on dpo.userid =ua.id " +
                "       left join doctor_extends de on dpo.doctorid = de.doctorid " +
                "where dpo.order_no = ?";
        List<Object> params = new ArrayList<>();
        params.add(orderNo);
        Map<String, Object> data = queryForMap(sql, params);
        if (data != null) {
            data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
        }
        return data;
    }

    /**
     * 更改订单状态
     *
     * @param out_trade_no
     */
    public void updatePhoneStatusSuccess(String out_trade_no, String trade_no, int payType) {
        String sql = " update doctor_phone_order set paystatus=?,paytime=?,paytype=?,status=?,paytransactionid=? where order_no=? ";
        List<Object> params = new ArrayList<>();
        params.add(PayStateEnum.Paid.getCode());
        params.add(UnixUtil.getNowTimeStamp());
        params.add(payType);
        params.add(PhoneOrderStateEnum.Paid.getCode());
        params.add(trade_no);
        params.add(out_trade_no);
        update(sql, params);
    }

    /**
     * 更改订单状态
     *
     * @param out_trade_no
     */
    public void updateAnswerStatusFail(String out_trade_no, String trade_no, String remark, int payType) {
        String sql = " update doctor_problem_order set paystatus=?,paytime=?,paytype=?,states=?,paytransactionid=?,remark=? where orderno=? ";
        List<Object> params = new ArrayList<>();
        params.add(PayStateEnum.UnPaid.getCode());
        params.add(UnixUtil.getNowTimeStamp());
        params.add(payType);
        params.add(AnswerOrderStateEnum.UnPaid.getCode());
        params.add(out_trade_no);
        params.add(remark);
        params.add(trade_no);
        update(sql, params);
    }

    /**
     * 更改订单状态
     *
     * @param out_trade_no
     */
    public void updatePhoneStatusFail(String out_trade_no, String trade_no, String remark, int payType) {
        String sql = " update doctor_phone_order set paystatus=?,paytime=?,paytype=?,status=?,paytransactionid=?,remark=? where order_no=? ";
        List<Object> params = new ArrayList<>();
        params.add(PayStateEnum.UnPaid.getCode());
        params.add(UnixUtil.getNowTimeStamp());
        params.add(payType);
        params.add(PhoneOrderStateEnum.UnPaid);
        params.add(out_trade_no);
        params.add(remark);
        params.add(trade_no);
        update(sql, params);
    }

    public Map<String, Object> getProblemOrder(String out_trade_no) {
        String sql = " select id,orderno,actualmoney,userid,doctorid,states,visitcategory from doctor_problem_order where orderno=? ";
        Map<String, Object> data = queryForMap(sql, out_trade_no);
        if (data != null) {
            data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
        }
        return data;
    }

    public Map<String, Object> getPhoneOrder(String out_trade_no) {
        String sql = " select id,order_no orderno,status,actualmoney,userid,schedulingid,doctorid,visitcategory,subscribe_time subscribetime from doctor_phone_order where order_no=? ";
        Map<String, Object> data = queryForMap(sql, out_trade_no);
        if (data != null) {
            data.put("actualmoney", ModelUtil.getLong(data, "actualmoney"));
        }
        return data;
    }

    public boolean updateProblemTemplate(long templateId) {
        String sql = " update doctor_problem_template set choiceflag=1 where id=? ";
        return update(sql, templateId) > 0;
    }

    public boolean updateProblemOrder(long orderId) {
        String sql = " update doctor_problem_order set issubmit=1 where id=? ";
        return update(sql, orderId) > 0;
    }

    public Map<String, Object> getPhoneOrder(long orderId) {
        String sql = " select dpo.id,dpo.actualmoney,dpo.visitcategory,dpo.marketprice,dpo.originalprice,ua.walletbalance,ua.kangyang_userid as kangyanguserid,dpo.userid from doctor_phone_order dpo left join user_account ua on dpo.userid=ua.id where dpo.id=? ";
        Map<String, Object> data = queryForMap(sql, orderId);
        if (data != null) {
            data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
            data.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(data, "walletbalance")));
            data.put("marketprice", PriceUtil.findPrice(ModelUtil.getLong(data, "marketprice")));
            data.put("originalprice", PriceUtil.findPrice(ModelUtil.getLong(data, "originalprice")));
        }
        return data;
    }

    public Map<String, Object> getVideoOrder(long orderId) {
        String sql = " select dpo.id,dpo.actualmoney,dpo.visitcategory,dpo.marketprice,dpo.originalprice,ua.walletbalance,ua.kangyang_userid as kangyanguserid,dpo.userid from doctor_video_order dpo left join user_account ua on dpo.userid=ua.id where dpo.id=? ";
        Map<String, Object> data = queryForMap(sql, orderId);
        if (data != null) {
            data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
            data.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(data, "walletbalance")));
            data.put("marketprice", PriceUtil.findPrice(ModelUtil.getLong(data, "marketprice")));
            data.put("originalprice", PriceUtil.findPrice(ModelUtil.getLong(data, "originalprice")));
        }
        return data;
    }

    public boolean delUserAnser(long templateId) {
        String sql = " update user_problem_template_answer set delflag=1 where templateid=? ";
        return update(sql, templateId) > 0;
    }

    /**
     * 自动关闭问诊订单
     */
    public boolean autoCloseProblemOrder(long id, int states, String remark) {
        String sql = "update doctor_problem_order set states=?,remark=? where id=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(states);
        params.add(remark);
        params.add(id);
        return update(sql, params) > 0;
    }

    public List<Map<String, Object>> findTypeOne() {
        String sql = "select id ,name value from common_disease_symptoms_type where ifnull(delflag,0)=0";
        return queryForList(sql);
    }

    public List<Map<String, Object>> findTypeTwo(long typeid) {
        String sql = " select id ,name value from common_disease_symptoms where ifnull(delflag,0)=0 and typeid=? ";
        return queryForList(sql, typeid);
    }

    //问诊针状列表
    public List<Map<String, Object>> findDepartType(long orderid) {
        String sql = "SELECT " +
                " cd.id, " +
                " cd.name realname," +
                " ma.diseasename diseasename," +
                "  cds.name typename  " +
                "FROM " +
                " common_disease_symptoms cd " +
                " LEFT JOIN common_disease_symptoms_type cds ON cd.typeid = cds.id  " +
                " LEFT JOIN middle_answer_disease ma ON cd.id=ma.diseaseid " +
                "WHERE " +
                " ma.orderid=? and ifnull(ma.delflag,0)=0 and ifnull(cds.delflag,0)=0";
        return queryForList(sql, orderid);
    }

    //急诊针状列表
    public List<Map<String, Object>> findDepartTypePhone(long orderid) {
        String sql = "SELECT " +
                " cd.id, " +
                " cd.name realname," +
                " ma.diseasename diseasename," +
                "  cds.name typename  " +
                "FROM " +
                " common_disease_symptoms cd " +
                " LEFT JOIN common_disease_symptoms_type cds ON cd.typeid = cds.id  " +
                " LEFT JOIN middle_phone_disease ma ON cd.id=ma.diseaseid " +
                "WHERE " +
                " ma.orderid=? and ifnull(ma.delflag,0)=0 and ifnull(cds.delflag,0)=0";
        return queryForList(sql, orderid);
    }

    /**
     * 问诊值班表导出
     *
     * @return
     */
    public List<Map<String, Object>> getDoctorInquiryListExport(long begintime, long endtime, String name, String phone, String number) {
        String sql = " select din.doctorid, " +
                "       doc_name                                                        docname, " +
                "       in_doc_code                                                     docno, " +
                "       doo_tel                                                         phone, " +
                "       from_unixtime(din.visiting_start_time / 1000, '%Y-%m') starttime1, " +
                "       from_unixtime(din.visiting_start_time / 1000, '%Y-%m-%d %H:%i') starttime, " +
                "       from_unixtime(din.visiting_end_time / 1000, '%Y-%m-%d %H:%i')   endtime " +
                " from doctor_inquiry din " +
                "            inner join doctor_info di on din.doctorid = di.doctorid and ifnull(di.delflag, 0) = 0 " +
                " where ifnull(din.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
//        if (visitingstarttime != 0) {
//            sql += " and TO_DAYS(from_unixtime(din.visiting_start_time/1000,'%Y-%m-%d %H-%i-%s')) = TO_DAYS(from_unixtime(?/1000,'%Y-%m-%d %H-%i-%s')) ";
//            params.add(visitingstarttime);
//        }
        if (begintime != 0) {
            sql += " and din.visiting_start_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and din.visiting_start_time < ? ";
            params.add(endtime);
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and doc_name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and doo_tel like ? ";
            params.add(String.format("%%%s%%", phone));
        }
        if (!StrUtil.isEmpty(number)) {
            sql += " and in_doc_code like ? ";
            params.add(String.format("%%%s%%", number));
        }
        return queryForList(sql + " order by din.doctorid,din.visiting_start_time desc ", params);
    }

    /**
     * 问诊值班表
     *
     * @return
     */
    public List<Map<String, Object>> getDoctorInquiryList(long begintime, long endtime, int pageIndex, int pageSize, String name, String phone, String number) {
        String sql = " select din.doctorid, " +
                "       doc_name                                                        docname, " +
                "       in_doc_code                                                     docno, " +
                "       doo_tel                                                         phone, " +
                "       din.visiting_start_time                                         visitingstarttime, " +
                "       din.visiting_end_time                                           visitingendtime, " +
                "       from_unixtime(din.visiting_start_time / 1000, '%Y-%m') starttime1, " +
                "       from_unixtime(din.visiting_start_time / 1000, '%Y-%m-%d %H:%i') starttime, " +
                "       from_unixtime(din.visiting_end_time / 1000, '%Y-%m-%d %H:%i')   endtime " +
                " from doctor_inquiry din " +
                "            inner join doctor_info di on din.doctorid = di.doctorid and ifnull(di.delflag, 0) = 0 " +
                " where ifnull(din.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
//        if (visitingstarttime != 0) {
//            sql += " and TO_DAYS(from_unixtime(din.visiting_start_time/1000,'%Y-%m-%d %H-%i-%s')) = TO_DAYS(from_unixtime(?/1000,'%Y-%m-%d %H-%i-%s')) ";
//            params.add(visitingstarttime);
//        }
        if (begintime != 0) {
            sql += " and din.visiting_start_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and din.visiting_start_time < ? ";
            params.add(endtime);
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and doc_name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and doo_tel like ? ";
            params.add(String.format("%%%s%%", phone));
        }
        if (!StrUtil.isEmpty(number)) {
            sql += " and in_doc_code like ? ";
            params.add(String.format("%%%s%%", number));
        }
        List<Map<String, Object>> list = queryForList(pageSql(sql, " order by din.doctorid,din.visiting_start_time desc "), pageParams(params, pageIndex, pageSize));
        list.forEach(map -> {
            long starttime = ModelUtil.getLong(map, "visitingstarttime");
            long endtimes = ModelUtil.getLong(map, "visitingendtime");
            float alltime = (float) (endtimes - starttime) / 3600000;
            BigDecimal b = new BigDecimal(alltime);
            map.put("alltime", b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
        });
        return list;
    }

    public long getDoctorInquiryListCount(long begintime, long endtime, String name, String phone, String number) {
        String sql = " select count(din.doctorid) count " +
                " from doctor_inquiry din " +
                "            inner join doctor_info di on din.doctorid = di.doctorid and ifnull(di.delflag, 0) = 0 " +
                " where ifnull(din.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
//        if (visitingstarttime != 0) {
//            sql += " and TO_DAYS(from_unixtime(din.visiting_start_time/1000,'%Y-%m-%d %H-%i-%s')) = TO_DAYS(from_unixtime(?/1000,'%Y-%m-%d %H-%i-%s')) ";
//            params.add(visitingstarttime);
//        }
        if (begintime != 0) {
            sql += " and din.visiting_start_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and din.visiting_start_time < ? ";
            params.add(endtime);
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and doc_name like ? ";
            params.add(String.format("%%%s%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and doo_tel like ? ";
            params.add(String.format("%%%s%%", phone));
        }
        if (!StrUtil.isEmpty(number)) {
            sql += " and in_doc_code like ? ";
            params.add(String.format("%%%s%%", number));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    public List<Map<String, Object>> getPictureList(long orderId, int orderType) {
        String sql = " select disease_picture value from middle_order_picture where orderid=? and ifnull(delflag,0)=0 and order_type=? ";
        return queryForList(sql, orderId, orderType);
    }

    public List<Map<String, Object>> getDoctorPhoneSchedulingList(long doctorId) {
        String sql = " select id, " +
                "       from_unixtime(visiting_start_time / 1000, '%m月%d日') daytime, " +
                "       from_unixtime(visiting_start_time / 1000, '%H:%i') starttime," +
                "       from_unixtime(visiting_end_time / 1000, '%H:%i') endtime," +
                "       issubscribe " +
                "from doctor_onduty " +
                "where ifnull(delflag, 0) = 0 " +
                "  and doctorid = ? and visiting_start_time > unix_timestamp() * 1000 " +
                "order by visiting_start_time ";
        return queryForList(sql, doctorId);
    }

    public List<Map<String, Object>> getDoctorVideoSchedulingList(long doctorId) {
        String sql = " select id, " +
                "       from_unixtime(visiting_start_time / 1000, '%m月%d日') daytime, " +
                "       from_unixtime(visiting_start_time / 1000, '%H:%i') starttime," +
                "       from_unixtime(visiting_end_time / 1000, '%H:%i') endtime," +
                "       issubscribe " +
                "from doctor_scheduling " +
                "where ifnull(delflag, 0) = 0 " +
                "  and doctorid = ? and visiting_start_time > unix_timestamp() * 1000 " +
                "order by visiting_start_time ";
        return queryForList(sql, doctorId);
    }

    public List<Map<String, Object>> getSickTimeList() {
        String sql = " SELECT customid id,name value FROM basics where ifnull(delflag,0)=0 and type = 25  ";
        return queryForList(sql);
    }
}
