package com.syhdoctor.webserver.mapper.video;

import com.syhdoctor.common.utils.EnumUtils.*;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserVideoMapper extends VideoBaseMapper {

    public Map<String, Object> getDefaultPatient(long userid) {
        String sql = " select id,patient_name name,patient_gender gender,patient_age age,patient_phone phone " +
                "from user_patient " +
                "where userid = ? " +
                "  and ifnull(delflag, 0) = 0 " +
                "order by is_default desc,modify_time desc,create_time desc " +
                "limit 1 ";
        return queryForMap(sql, userid);
    }

    public Map<String, Object> getDefaultScheduling() {
        String sql = " select id,visiting_start_time,visiting_end_time " +
                "from doctor_scheduling " +
                "where visiting_start_time > unix_timestamp() " +
                "  and ifnull(delflag, 0) = 0 " +
                "  and ifnull(issubscribe, 0) = 0 " +
                "order by visiting_start_time " +
                "limit 1 ";
        return queryForMap(sql);
    }

    /**
     * 系统模板列表
     *
     * @return
     */
    public List<Map<String, Object>> getTemplateList() {
        String sql = " select dt.id,dt.usertitle,dt.doctortitle,dt.checkbox,dta.content " +
                "from disease_template dt " +
                "       left join disease_template_answer dta on dt.id = dta.templateid and ifnull(dta.delflag, 0) = 0 " +
                "where ifnull(dt.delflag, 0) = 0 " +
                "order by dt.sort desc,dta.id ";
        return queryForList(sql);
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
     * 更改问诊订单状态
     *
     * @param out_trade_no
     */
    public void updateVideoStatusSuccess(String out_trade_no, String trade_no, int payType) {
        String sql = " update doctor_video_order set paystatus=?,paytime=?,paytype=?,status=?,paytransactionid=? where orderno=? ";
        List<Object> params = new ArrayList<>();
        params.add(PayStateEnum.Paid.getCode());
        params.add(UnixUtil.getNowTimeStamp());
        params.add(payType);
        params.add(AnswerOrderStateEnum.Paid.getCode());
        params.add(trade_no);
        params.add(out_trade_no);
        update(sql, params);
    }

    /**
     * 修改医生排班预约状态
     *
     * @param id
     */
    public void updateDoctorScheduling(long id) {
        String sql = " update doctor_scheduling set issubscribe=1 where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        update(sql, params);
    }

    public Map<String, Object> findById(long doctorid) {
        String sql = "select doo_tel,doc_name from doctor_info where doctorid=?";
        return queryForMap(sql, doctorid);
    }

    public Map<String, Object> getVideoOrderByOrderNo(String orderNo) {
        String sql = "select dpo.id, " +
                "       dpo.userid, " +
                "       dpo.doctorid, " +
                "       dpo.paystatus, " +
                "       dpo.status, " +
                "       dpo.actualmoney, " +
                "       dpo.schedulingid, " +
                "       dpo.subscribe_time as subscribetime, " +
                "       dpo.create_time as createtime, " +
                "       ua.platform     as uplatform, " +
                "       ua.xg_token     as utoken, " +
                "       ua.name, " +
                "       de.xg_token     as dtoken, " +
                "       de.platform     as dplatform " +
                "from doctor_video_order dpo " +
                "       left join user_account ua on dpo.userid =ua.id " +
                "       left join doctor_extends de on dpo.doctorid = de.doctorid " +
                "where dpo.orderno = ? ";
        List<Object> params = new ArrayList<>();
        params.add(orderNo);
        Map<String, Object> map = queryForMap(sql, params);
        if (map != null) {
            map.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "actualmoney")));
        }
        return map;
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

    //首页查询当天预约
    public Map<String, Object> getSubscribe(long id, long doctorId) {
        String sql = "select id,visiting_start_time visitingstarttime,visiting_end_time visitingendtime,issubscribe from doctor_scheduling where id=? and doctorid=? ";
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(doctorId);
        return queryForMap(sql, list);
    }

    public List<Map<String, Object>> getDoctorSchedulingList(long doctorId) {
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

    public List<Map<String, Object>> appUserVideoOrderList(long userId, int pageindex, int pagesize) {
        String sql = " select dvo.id, " +
                "       ue.id evaluateid, " +
                "       dvo.orderno, " +
                "       dvo.userid, " +
                "       dvo.doctorid, " +
                "       dvo.status states, " +
                "       dvo.paystatus, " +
                "       dvo.paytype, " +
                "       dvo.actualmoney price, " +
                "       dvo.marketprice, " +
                "       dvo.originalprice, " +
                "       dvo.discount," +
                "       dvo.guidance diagnosis, " +
                "       dvo.dis_describe disdescribe, " +
                "       dvo.create_time createtime, " +
                "       dvo.subscribe_time subscribetime," +
                "       dp.prescriptionid, " +
                "       dvo.visitcategory " +
                "from doctor_video_order dvo " +
                "                        left join user_evaluate ue on dvo.id=ue.order_number and ue.ordertype=3 " +
                "            left join (select orderid,max(prescriptionid) prescriptionid " +
                "                       from doc_prescription " +
                "                       where ifnull(delflag, 0) = 0 and order_type=3 " +
                "                       group by orderid) dp on dvo.id = dp.orderid " +
                "where dvo.userid = ? and ifnull(dvo.delflag,0)=0 and dvo.status in (2,3,4,5,6,8) ";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        return query(pageSql(sql, " order by createtime desc "), pageParams(params, pageindex, pagesize), (ResultSet res, int num) -> {
            Map<String, Object> map = resultToMap(res);
            if (map != null) {
                BigDecimal price = PriceUtil.findPrice(ModelUtil.getLong(map, "price"));
                map.put("price", price);
                map.put("marketprice", PriceUtil.findPrice(ModelUtil.getLong(map, "marketprice")));
                map.put("originalprice", PriceUtil.findPrice(ModelUtil.getLong(map, "originalprice")));
                String diagnosis = ModelUtil.getStr(map, "diagnosis");
                int states = ModelUtil.getInt(map, "states");
                map.put("statesname", VideoOrderStateEnum.getValue(states).getMessage());
                long evaluateid = ModelUtil.getLong(map, "evaluateid");
                long prescriptionid = ModelUtil.getLong(map, "prescriptionid");
                videoOrderResult(map, states, diagnosis, evaluateid, price, prescriptionid);
            }
            return map;
        });
    }

    public void videoOrderResult(Map<String, Object> map, int status, String guidance, long evaluateid, BigDecimal price, long prescriptionid) {
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
        if (status == VideoOrderStateEnum.OrderSuccess.getCode()) {
            map.put("states", VideoOrderStateEnum.OrderSuccess.getCode());
            map.put("statesname", VideoOrderStateEnum.OrderSuccess.getMessage());
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
        } else if (status == VideoOrderStateEnum.WaitRefund.getCode()) {
            map.put("states", VideoOrderStateEnum.OrderFail.getCode());
            map.put("statesname", VideoOrderStateEnum.OrderFail.getMessage());
            map.put("refund", 1);
        } else if (status == VideoOrderStateEnum.OrderFail.getCode()) {
            if (price.compareTo(BigDecimal.ZERO) == 0) {
                map.put("refund", 0);
            } else {
                map.put("refund", 2);
            }
        } else if (status == VideoOrderStateEnum.InCall.getCode()) {
            map.put("isopen", 1);
        }
    }

    public List<Map<String, Object>> getUserSubscribeList(long userid) {
        String sql = " select id,status,visitcategory,subscribe_time subscribetime from doctor_video_order where userid=? and ifnull(delflag,0)=0  and status in(2,3) and subscribe_time between ? and ? and paystatus=1 order by subscribe_time ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(UnixUtil.getStarttime(UnixUtil.getNowTimeStamp()));
        params.add(UnixUtil.getEndtime(UnixUtil.getNowTimeStamp()));
        return query(sql, params, (ResultSet res, int num) -> {
            Map<String, Object> map = resultToMap(res);
            if (map != null) {
                map.put("visitcategory", VisitCategoryEnum.getValue(ModelUtil.getInt(map, "visitcategory")).getMessage());
            }
            return map;
        });
    }

    public long getUserSubscribeCount(long userid) {
        String sql = " select count(id) count from doctor_video_order where userid=? and ifnull(delflag,0)=0  and status in(2,3) and subscribe_time between ? and ? and paystatus=1 order by subscribe_time ";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(UnixUtil.getStarttime(UnixUtil.getNowTimeStamp()));
        params.add(UnixUtil.getEndtime(UnixUtil.getNowTimeStamp()));
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    //绿通医院列表
    public List<Map<String, Object>> hospitalList(List<Long> categoryids, List<Long> departmentid, int pageIndex, int pageSize) {
        String sql = "SELECT " +
                " hg.id, " +
                " hg.hospital_name name, " +
                " hg.hospital_picture_small picture,  " +
                " hg.hospital_level level  " +
                "FROM " +
                " hospital_green hg  " +
                "WHERE ifnull(hg.delflag,0)=0 ";
        Map<String, Object> map = new HashMap<>();
        if (categoryids.size() > 0) {
            sql += " and hg.id in ( " +
                    "                 SELECT  " +
                    "                  DISTINCT mc.hospitalid   " +
                    "                 FROM  " +
                    "                  middle_hospital_category mc  " +
                    "                  inner JOIN middle_hospital_department md ON mc.hospitalid = md.hospitalid and ifnull(md.delflag, 0) = 0 " +
                    "                 WHERE ifnull(mc.delflag, 0) = 0 and    " +
                    "                  mc.categoryid in(:categoryids)    ";
            map.put("categoryids", categoryids);
            if (departmentid.size() > 0) {
                sql += " and md.departmentid in(:depaid) ) ";
                map.put("depaid", departmentid);
            } else {
                sql += " )";
            }
        } else {
            if (departmentid.size() > 0) {
                sql += " and hg.id in ( " +
                        "                 SELECT  " +
                        "                  DISTINCT mc.hospitalid   " +
                        "                 FROM  " +
                        "                  middle_hospital_category mc  " +
                        "                  inner JOIN middle_hospital_department md ON mc.hospitalid = md.hospitalid and ifnull(md.delflag, 0) = 0 " +
                        "                 WHERE ifnull(mc.delflag, 0) = 0 and  md.departmentid in (:depaid)) ";
                map.put("depaid", departmentid);
            }
        }
        map.put("start", (pageIndex - 1) * pageSize);
        map.put("end", (pageIndex - 1) * pageSize + pageSize);
        sql += " order by hg.id desc limit :start,:end";
        List<Map<String, Object>> list = queryForList(sql, map);
        List<Long> hospital = new ArrayList<>();
        for (Map<String, Object> map1 : list) {
            long hospitalid = ModelUtil.getLong(map1, "id");
            hospital.add(hospitalid);
        }
        Map<Long, List<Map<String, Object>>> category = findCategory(hospital);
        Map<Long, List<Map<String, Object>>> department = findDepartment(hospital);
        for (Map<String, Object> map2 : list) {
            long hospitalid = ModelUtil.getLong(map2, "id");
            map2.put("department", department.get(hospitalid));
            map2.put("category", category.get(hospitalid));
        }
        return list;
    }

    public Map<Long, List<Map<String, Object>>> findCategory(List<Long> hospitalid) {
        String sql = "select hc.category_name name,mh.categoryid id, mh.hospitalid from middle_hospital_category mh left join hospital_category hc on mh.categoryid=hc.id where mh.hospitalid in(:ids)";
        if (hospitalid.size() == 0) {
            return null;
        }
        Map<String, Object> map = new HashedMap();
        map.put("ids", hospitalid);
        List<Map<String, Object>> listCategory = queryForList(sql, map);
        Map<Long, List<Map<String, Object>>> map1 = new HashedMap();
        for (Map<String, Object> map2 : listCategory) {
            long hospitalids = ModelUtil.getLong(map2, "hospitalid");
            if (map1.containsKey(hospitalids)) {
                map1.get(hospitalids).add(map2);
            } else {
                List<Map<String, Object>> list1 = new ArrayList<>();
                list1.add(map2);
                map1.put(hospitalids, list1);
            }
        }
        return map1;
    }

    public Map<Long, List<Map<String, Object>>> findDepartment(List<Long> hospitalid) {
        if (hospitalid.size() == 0) {
            return null;
        }
        Map<String, Object> map = new HashedMap();
        map.put("ids", hospitalid);
        String sqls = "select hc.department_name name,mh.departmentid id , mh.hospitalid from middle_hospital_department mh left join department_green hc on mh.departmentid=hc.id where mh.hospitalid in(:ids)";
        List<Map<String, Object>> listDepartment = queryForList(sqls, map);
        Map<Long, List<Map<String, Object>>> map1 = new HashedMap();
        for (Map<String, Object> map2 : listDepartment) {
            long hospitalids = ModelUtil.getLong(map2, "hospitalid");
            if (map1.containsKey(hospitalids)) {
                map1.get(hospitalids).add(map2);
            } else {
                List<Map<String, Object>> list1 = new ArrayList<>();
                list1.add(map2);
                map1.put(hospitalids, list1);
            }
        }
        return map1;
    }

    public Map<String, Object> findDetail(long hospitalid) {
        String sql = "select id,hospital_name name,hospital_address adress,hospital_phone phone,hospital_introduce introduce,hospital_picture_big picture from hospital_green where id=? and ifnull(delflag,0)=0";
        return queryForMap(sql, hospitalid);
    }

    public List<Map<String, Object>> findDepartment(long hospital) {
        String sql = "select hc.department_name name,mh.departmentid id from middle_hospital_department mh left join department_green hc on mh.departmentid=hc.id where mh.hospitalid=? and ifnull(mh.delflag,0)=0";
        return queryForList(sql, hospital);
    }

    //树形科室
    public List<Map<String, Object>> findDeparmentListTree() {
        String sql = "select id,department_name value, pid " +
                " from department_green  " +
                " where ifnull(delflag,0)=0 ";
        return queryForList(sql);
    }

    public List<Map<String, Object>> findDeparmentList() {
        String sql = "select id,department_name value " +
                " from department_green  " +
                " where ifnull(delflag,0)=0 ";
        return queryForList(sql);
    }


    public List<Map<String, Object>> findCategory() {
        String sql = "select category_name value,id from hospital_category where ifnull(delflag,0)=0";
        return queryForList(sql);
    }


    /*
     *功能描述 用户下单绿通
     * @author qian.wang
     * @date 2018/12/5
     * @param  hospitalid 医院id
     * @param departmentid  科室id
     * @param appointmentType 预约类型
     * @param userPatient  患者id
     * @param serviceContent 挂号
     * @param isUrgent 是否加急
     * @return boolean
     */
    public long insertOrderGreen(long userid, long doctorid, long hospitalid, long departmentid, int appointmentType, int isOrdinary, int isExpert, int isUrgent) {
        String sql = "insert into green_order (orderno,userid,doctorid,status,paystatus,paytype,create_time,hospital_greenid,green_departmentid,appointment_type,is_urgent,is_ordinary,is_expert)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(IdGenerator.INSTANCE.nextId());
        params.add(userid);
        params.add(doctorid);
        params.add(6);//预约中
        params.add(1);//已支付
        params.add(4);//零元支付
        params.add(UnixUtil.getNowTimeStamp());
        params.add(hospitalid);
        params.add(departmentid);
        params.add(appointmentType);
        params.add(isUrgent);
        params.add(isOrdinary);
        params.add(isExpert);
        return insert(sql, params, "id");
    }

    /**
     * 订单答案列表(包含历史)
     *
     * @return
     */
    public List<Map<String, Object>> getUserHistoryGreenList(long doctorId, long userId, int pageindex, int pagesize) {
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
                "     from green_order_chat da   " +
                "            left join user_account ua on da.userid = ua.id   " +
                "            left join doctor_info di on di.doctorid = da.doctorid   " +
                "            left join green_order dpo on dpo.id= da.orderid " +
                "     where ifnull(da.delflag, 0) = 0  and da.doctorid= ? and da.userid=? and dpo.status in(2,4,3,6) ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(userId);
        return queryForList(pageSql(sql, " order by da.create_time desc,da.id desc "), pageParams(params, pageindex, pagesize));
    }

    /**
     * 医生聊天详细列表
     *
     * @return
     */
    public List<Map<String, Object>> getUserCurrentGreenList(long orderid, int pageindex, int pagesize) {
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
                "from green_order_chat da " +
                "       left join user_account ua on da.userid = ua.id " +
                "       left join doctor_info di on di.doctorid = da.doctorid " +
                "       left join green_order dpo on da.orderid=dpo.id " +
                "where ifnull(da.delflag, 0) = 0  and da.orderid= ? and dpo.status in(2,4,3,6)";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        return queryForList(pageSql(sql, " order by da.create_time desc,da.id desc "), pageParams(params, pageindex, pagesize));
    }

    public boolean addGreenDiseaseid(long orderId, long diseaseId, String name) {
        String sql = " insert into middle_green_disease(orderid,diseaseid,diseasename,delflag,create_time)values(?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(diseaseId);
        params.add(name);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    public List<Map<String, Object>> greenOrderList(long userid, int pageSize, int pageIndex) {
        String sql = "select gor.id,hg.hospital_name name,gor.create_time createtime, gor.status,gor.paystatus,gor.actualmoney from green_order gor left join hospital_green hg on gor.hospital_greenid=hg.id where ifnull(gor.delflag,0)=0 and gor.userid=?";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        return query(pageSql(sql, " order by gor.id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                //TODO订单状态,支付状态
//                map.put("status", GreenOrderStateEnum.getValue(ModelUtil.getInt(map, "status")).getMessage());
                map.put("paystatus", ModelUtil.getInt(map, "paystatus") == 0 ? "未支付" : "已支付");
                map.put("actualmoney", "￥" + PriceUtil.findPrice(ModelUtil.getLong(map, "actualmoney")));
            }
            return map;
        });
    }

    /*
     *功能描述 订单详情（后续再改动）
     * @author qian.wang
     * @date 2018/12/5
     * @param  * @param orderid
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    public Map<String, Object> getGreenDetail(long orderid) {
        String sql = "select dg.department_name departmentname ,gor.orderno,gor.paytype,gor.appointment_type type,gor.is_urgent isurgent,gor.id,hg.hospital_name name,gor.subscribe_time subscribetime, "
                + " gor.create_time createtime ,gor.status,gor.paystatus,gor.actualmoney,gor.green_contact greencontact,gor.green_phone greenphone,gor.green_address greenaddress " +
                "from green_order gor left join hospital_green hg on gor.hospital_greenid=hg.id left join department_green dg on gor.green_departmentid=dg.id where ifnull(gor.delflag,0)=0 and gor.id=?";
        Map<String, Object> map = queryForMap(sql, orderid);
        if (null != map) {
            map.put("type", ModelUtil.getInt(map, "type") == 0 ? "门诊" : "住院");
//            map.put("status", GreenOrderStateEnum.getValue(ModelUtil.getInt(map, "status")).getMessage());
            map.put("paytype", PayTypeEnum.getValue(ModelUtil.getInt(map, "paytype")).getMessage());
            map.put("paystatus", ModelUtil.getInt(map, "paystatus") == 0 ? "未支付" : "已支付");
            map.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "actualmoney")));
        }
        return map;
    }

    public List<Map<String, Object>> findGreenDisease(long orderid) {
        String sql = "select id,diseasename from middle_green_disease where orderid=? and ifnull(delflag,0)=0";
        return queryForList(sql, orderid);
    }

    //更新钱包的钱
    public boolean updateWallet(long id, BigDecimal finalamount) {
        String sql = "update user_account set walletbalance=?,modify_time=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(finalamount);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(id);
        return update(sql, params) > 0;
    }

    //更新订单状态
    public boolean updateStatus(long orderid, int payType) {
        String sql = "update green_order set  paystatus=1,paytype=?,status=2,modify_time=? where id=? and ifnull(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(payType);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(orderid);
        return update(sql) > 0;
    }

    public Map<String, Object> findOrderByorderId(long orderid) {
        String sql = "select ua.phone,go.orderno,go.actualmoney,ua.walletbalance from green_order go left join user_account ua on go.userid=ua.id where go.id=?";
        Map<String, Object> map = queryForMap(sql, orderid);
        if (null != map) {
            map.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "actualmoney")));
        }
        return map;
    }

    /**
     * 添加语音回答
     * params questionaAnswerType 0:用户 1：医生 2：系统
     *
     * @return
     */
    public long addAnswer(long userId, long doctorId, long orderId, String content, long contenttime, int contentType, int questionaAnswerType) {
        String sql = "   insert into green_order_chat (content,contenttime, userid, doctorid, orderid, delflag, create_time, status, contenttype,questionanswertype) " +
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

    public Map<String, Object> getDoctorAnswer(long id) {
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
                "     from green_order_chat da   " +
                "            left join user_account ua on da.userid = ua.id   " +
                "            left join doctor_info di on di.doctorid = da.doctorid   " +
                "     where ifnull(da.delflag, 0) = 0  and da.id= ?";
        return queryForMap(sql, id);
    }

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
                "     from green_order_chat da   " +
                "            left join user_account ua on da.userid = ua.id   " +
                "            left join doctor_info di on di.doctorid = da.doctorid   " +
                "            left join green_order dpo on dpo.id= da.orderid " +
                "     where ifnull(da.delflag, 0) = 0  and da.orderid=? and da.contenttype not in(6,7,10) and dpo.status in(2,3,4,6) and da.id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(id);
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
                "       dpo.status, " +
                "       ua.name          as username, " +
                "       di.doc_name      as docname, " +
                "       ua.userno          as userno, " +
                "       di.in_doc_code      as doctorno, " +
                "       di.work_inst_name   workinstname, " +
                "       ua.headpic, " +
                "       di.doc_photo_url as docphotourl, " +
                "       ua.platform     as uplatform, " +
                "       ua.xg_token     as utoken, " +
                "       de.xg_token     as dtoken, " +
                "       de.platform     as dplatform " +
                "from green_order dpo " +
                "       left join user_account ua on dpo.userid = ua.id " +
                "       left join doctor_info di on dpo.doctorid = di.doctorid " +
                "       left join doctor_extends de on dpo.doctorid = de.doctorid " +
                "where dpo.id = ? ";
        return queryForMap(sql, orderId);
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getGreenOrder(long id) {
        String sql = "select dpo.id, " +
                "       dpo.orderno, " +
                "       dpo.paytype, " +
                "       dpo.paystatus, " +
                "       dpo.status, " +
                "       dpo.actualmoney, " +
                "       dpo.marketprice, " +
                "       dpo.subscribe_time subscribetime, " +
                "       dpo.green_contact greencontact, " +
                "       dpo.green_phone greenphone, " +
                "       dpo.green_address greenaddress, " +
                "       hg.hospital_name hospitalname, " +
                "       dpo.discount, " +
                "       dpo.visitcategory, " +
                "       dpo.userid, " +
                "       ua.openid, " +
                "       di.in_doc_code doccode, " +
                "       ua.userno , " +
                "       dpo.doctorid, " +
                "       dpo.create_time     createtime, " +
                "       di.doc_name      as doctorname, " +
                "       di.examine, " +
                "       ua.name          as username, " +
                "       di.doc_photo_url as docphotourl, " +
                "       ua.walletbalance, " +
                "       ua.headpic       as headpic, " +
                "       ua.platform      as uplatform, " +
                "       ua.xg_token      as utoken, " +
                "       de.platform      as dplatform, " +
                "       de.xg_token      as dtoken " +
                " from green_order dpo " +
                "       left join user_account ua on dpo.userid = ua.id " +
                "       left join basics b on dpo.status = b.customid and b.type = 4 " +
                "       left join doctor_info di on dpo.doctorid = di.doctorid " +
                "       left join doctor_extends de on de.doctorid = di.doctorid " +
                "       left join hospital_green hg on dpo.hospital_greenid=hg.id " +
                "where ifnull(dpo.delflag, 0) = 0 " +
                "  and dpo.id = ?";
        Map<String, Object> data = queryForMap(sql, id);
        if (data != null) {
            data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
            data.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(data, "walletbalance")));
            data.put("marketprice", PriceUtil.findPrice(ModelUtil.getLong(data, "marketprice")));
        }
        return data;
    }

    /**
     * 订单答案列表(包含历史)
     *
     * @return
     */
    public List<Map<String, Object>> getDoctorHistoryGreenList(long orderid, long doctorId, long userId, int pageindex, int pagesize) {
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
                "     from green_order_chat da   " +
                "            left join user_account ua on da.userid = ua.id   " +
                "            left join doctor_info di on di.doctorid = da.doctorid   " +
                "            left join green_order dpo on dpo.id= da.orderid " +
                "     where ifnull(da.delflag, 0) = 0  and da.orderid= ? and dpo.status in(2,3,4,6) ";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        return queryForList(pageSql(sql, " order by da.create_time desc,da.id desc "), pageParams(params, pageindex, pagesize));
    }

    /**
     * 医生聊天详细列表
     *
     * @return
     */
    public List<Map<String, Object>> getDoctorCurrentGreenList(long orderid, int pageindex, int pagesize) {
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
                "from green_order_chat da " +
                "       left join user_account ua on da.userid = ua.id " +
                "       left join doctor_info di on di.doctorid = da.doctorid " +
                "       left join green_order dpo on da.orderid=dpo.id " +
                "where ifnull(da.delflag, 0) = 0  and da.orderid= ? and dpo.status in(2,3,4,6) ";
        List<Object> params = new ArrayList<>();
        params.add(orderid);

        return queryForList(pageSql(sql, " order by da.create_time desc,da.id desc "), pageParams(params, pageindex, pagesize));
    }

    /**
     * 用户显示模板列表
     *
     * @return
     */
    public List<Map<String, Object>> userInfoList(List<Long> ids) {
        String sql = " select da.id,ua.name username,ua.gender,cg.value gendername,ua.birthday,diseasename from green_order_chat da left join middle_answer_disease mad on da.orderid=mad.orderid " +
                "left join user_account ua on da.content=ua.id " +
                "left join code_gender cg on ua.gender=cg.code " +
                "where da.id in (:ids)  ";
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
                "from green_order_chat da " +
                "       left join doctor_problem_template dpt on da.content = dpt.id and ifnull(dpt.delflag, 0) = 0 " +
                "       left join user_problem_template_answer upta on dpt.id=upta.templateid and ifnull(upta.delflag,0)=0 " +
                "    left join doctor_problem_template_answer dpta on upta.answerid=dpta.id " +
                "where da.orderid =? and da.contenttype=6 and ifnull(da.delflag, 0) = 0 order by da.id  ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> prescriptionList(List<Long> ids) {
        String sql = " select da.id,prescriptionid,pres_photo_url presphotourl from green_order_chat da left join doc_prescription dp on da.content=dp.prescriptionid where da.id in (:ids) and ifnull(da.delflag, 0) = 0  ";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        return queryForList(sql, params);
    }

    public Map<String, Object> getDoctorOrderState(long orderid) {
        String sql = "select status from green_order where id=?";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        return queryForMap(sql, params);
    }


    public long insertOrderGreenSimple(long doctorid, long userid, long hospitalid, long departmentid) {
        String sql = "insert into green_order (orderno,userid,doctorid,status,paystatus,paytype,create_time,hospital_greenid,green_departmentid)" +
                " values(?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(IdGenerator.INSTANCE.nextId());
        params.add(userid);
        params.add(doctorid);
        params.add(6);//预约中
        params.add(1);//已支付
        params.add(4);//零元支付
        params.add(UnixUtil.getNowTimeStamp());
        params.add(hospitalid);
        params.add(departmentid);
        return insert(sql, params, "id");

    }

    public boolean findGreeOrder(long userid, long hospitalid, long departmentid) {
        String sql = "select id from green_order where hospital_greenid=? and green_departmentid=? and ifnull(delflag,0)=0 and userid=? and status in(1,2,3,6)";
        List<Object> params = new ArrayList<>();
        params.add(hospitalid);
        params.add(departmentid);
        params.add(userid);
        return queryForMap(sql, params) == null ? false : true;
    }

    public boolean updateOrderStatus(long orderid) {
        String sql = " update doctor_video_order set status=? where id =? ";
        List<Object> params = new ArrayList<>();
        params.add(VideoOrderStateEnum.OrderSuccess.getCode());
        params.add(orderid);
        return update(sql, params) > 0;
    }

    public boolean updateVideoUserinto(long orderId, String userdevicecode) {
        String sql = " update doctor_video_order set userinto=1,user_device_code=? where id =? ";
        return update(sql, userdevicecode, orderId) > 0;
    }


}
