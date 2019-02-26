package com.syhdoctor.webserver.mapper.video;

import com.syhdoctor.common.config.ConfigModel;
import com.syhdoctor.common.utils.EnumUtils.OrderTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.VideoOrderStateEnum;
import com.syhdoctor.common.utils.*;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.sql.ResultSet;
import java.util.*;

public abstract class VideoBaseMapper extends BaseMapper {


    //该医生下订单列表
    public List<Map<String, Object>> findOrderListAll(String patientname, String phonenumber, String dcotorname, int status, int visitcategory, long begintime, long endtime, int pageSize, int pageIndex) {
        String sql = "select di.doc_name       docname, " +
                "       dv.id, " +
                "       up.name   patientname, " +
                "       up.gender gender, " +
                "       up.age    age, " +
                "       up.phone  patientphone, " +
                "       dv.orderno, " +
                "       dv.actualmoney, " +
                "       dv.create_time    createtime, " +
                "       dv.status, " +
                "       dv.visitcategory " +
                " from doctor_video_order dv " +
                "       left join user_account up on dv.userid = up.id " +
                "       left join doctor_scheduling ds on dv.schedulingid = ds.id " +
                "       left join doctor_info di on dv.doctorid = di.doctorid " +
                " where ifnull(dv.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(patientname)) {
            sql += " and up.name like ? ";
            params.add(String.format("%%%S%%", patientname));
        }
        if (!StrUtil.isEmpty(phonenumber)) {
            sql += " and up.phone like ? ";
            params.add(String.format("%%%S%%", phonenumber));
        }
        if (!StrUtil.isEmpty(dcotorname)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%S%%", dcotorname));
        }
        if (status != 0) {
            sql += " and dv.status = ? ";
            params.add(status);
        }
        if (visitcategory != 0) {
            sql += " and dv.visitcategory= ? ";
            params.add(visitcategory);
        }
        if (begintime != 0) {
            sql += " and dv.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and dv.create_time < ? ";
            params.add(endtime);
        }
        return query(pageSql(sql, " order by dv.id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "actualmoney")));
                //todo订单状态，订单类型转换
                map.put("status", VideoOrderStateEnum.getValue(ModelUtil.getInt(map, "status")).getMessage());
                map.put("visitcategory", "视频订单");
                map.put("gender", ModelUtil.getInt(map, "gender") == 1 ? "男" : "女");
            }
            return map;
        });
    }

    public long findOrderListCountAll(String patientname, String phonenumber, String dcotorname, int status, int visitcategory, long begintime, long endtime) {
        String sql = "select count(dv.id)count from doctor_video_order dv left join user_patient up on dv.patientid=up.id "
                + " left join doctor_scheduling ds on dv.schedulingid=ds.id left join doctor_info di on dv.doctorid=di.doctorid where ifnull(dv.delflag,0)=0";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(patientname)) {
            sql += " and up.patient_name like ? ";
            params.add(String.format("%%%S%%", patientname));
        }
        if (!StrUtil.isEmpty(phonenumber)) {
            sql += " and up.patient_phone like ? ";
            params.add(String.format("%%%S%%", phonenumber));
        }
        if (!StrUtil.isEmpty(dcotorname)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%S%%", dcotorname));
        }
        if (status != 0) {
            sql += " and dv.status = ? ";
            params.add(status);
        }
        if (visitcategory != 0) {
            sql += " and dv.visitcategory= ? ";
            params.add(visitcategory);
        }
        if (begintime != 0) {
            sql += " and dv.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and dv.create_time < ? ";
            params.add(endtime);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public List<Map<String, Object>> findDoctorSchdue(long time,String docname,String phone,String number) {
        String sql = "select di.doc_name name,visiting_start_time visitingstarttime,issubscribe from doctor_scheduling ds left join doctor_info di on ds.doctorid=di.doctorid where ifnull(ds.delflag,0)=0 and  visiting_start_time between ? and ?  ";
        List<Object> params = new ArrayList<>();
        params.add(time);//当天凌晨0点00
        params.add(UnixUtil.getBeginDayOfTomorrow(new Date(time)));
        if (!StrUtil.isEmpty(docname)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%s%%", docname));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and di.doo_tel like ? ";
            params.add(String.format("%%%s%%", phone));
        }
        if (!StrUtil.isEmpty(number)) {
            sql += " and di.in_doc_code like ? ";
            params.add(String.format("%%%s%%", number));
        }
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> findDoctorSchdueDate(long time) {
        String sql = "select di.doc_name name,from_unixtime(visiting_start_time / 1000, '%Y-%m-%d %H:%i') visitingstarttime,issubscribe from doctor_scheduling ds left join doctor_info di on ds.doctorid=di.doctorid where ifnull(ds.delflag,0)=0 and  visiting_start_time between ? and ?  ";
        List<Object> params = new ArrayList<>();
        params.add(time);//当天凌晨0点00
        params.add(UnixUtil.getBeginDayOfTomorrow(new Date(time)));
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> findDoctorSchdueDateNew(long starttime, long endtime) {
        String sql = "select di.doc_name                                                                         name, " +
                "       concat(from_unixtime(visiting_start_time / 1000, '%Y-%m-%d'), ifnull(doc_name, '')) dayname, " +
                "       from_unixtime(visiting_start_time / 1000, '%Y-%m-%d')                               day, " +
                "       from_unixtime(visiting_start_time / 1000, '%H:%i')                         visitingstarttime, " +
                "       from_unixtime(visiting_end_time / 1000, '%H:%i')                         visitingendtime, " +
                "       visiting_end_time-visiting_start_time time, " +
                "       issubscribe " +
                "from doctor_scheduling ds " +
                "            inner join doctor_info di on ds.doctorid = di.doctorid and ifnull(di.delflag, 0) = 0 " +
                "where ifnull(ds.delflag, 0) = 0 and ds.examine_state=2 ";
        List<Object> params = new ArrayList<>();
        if (starttime != 0) {
            sql += " and visiting_start_time >= ? ";
            params.add(starttime);//当天凌晨0点00
        }
        if (endtime != 0) {
            sql += " and visiting_start_time <= ? ";
            params.add(UnixUtil.getEndTime(endtime));
        }
        sql += " order by day desc ";
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> findDoctorSchduePhone(long time,String docname,String phone,String number) {
        String sql = "select di.doc_name name,visiting_start_time visitingstarttime,issubscribe from doctor_onduty   ds left join doctor_info di on ds.doctorid=di.doctorid where ifnull(ds.delflag,0)=0 and visiting_start_time between ? and ?  ";
        List<Object> params = new ArrayList<>();
        params.add(time);//当天凌晨0点00
        params.add(UnixUtil.getBeginDayOfTomorrow(new Date(time)));
        if (!StrUtil.isEmpty(docname)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%s%%", docname));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and di.doo_tel like ? ";
            params.add(String.format("%%%s%%", phone));
        }
        if (!StrUtil.isEmpty(number)) {
            sql += " and di.in_doc_code like ? ";
            params.add(String.format("%%%s%%", number));
        }
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> findDoctorSchduePhoneNew(long starttime, long endtime) {
        String sql = "select di.doc_name                                                                         name, " +
                "       concat(from_unixtime(visiting_start_time / 1000, '%Y-%m-%d'), ifnull(doc_name, '')) dayname, " +
                "       from_unixtime(visiting_start_time / 1000, '%Y-%m-%d')                               day, " +
                "       from_unixtime(visiting_start_time / 1000, '%H:%i')                         visitingstarttime, " +
                "       from_unixtime(visiting_end_time / 1000, '%H:%i')                         visitingendtime, " +
                "       visiting_end_time-visiting_start_time time, " +
                "       issubscribe " +
                "from doctor_onduty ds " +
                "            inner join doctor_info di on ds.doctorid = di.doctorid and ifnull(di.delflag, 0) = 0 " +
                "where ifnull(ds.delflag, 0) = 0 and ds.examine_state=2 ";
        List<Object> params = new ArrayList<>();
        if (starttime != 0) {
            sql += " and visiting_start_time >= ? ";
            params.add(starttime);//当天凌晨0点00
        }
        if (endtime != 0) {
            sql += " and visiting_start_time <= ? ";
            params.add(UnixUtil.getEndTime(endtime));
        }
        sql += " order by day desc ";
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> findDoctorSchduePhoneDate(long time) {
        String sql = "select di.doc_name name,from_unixtime(visiting_start_time / 1000, '%Y-%m-%d %H:%i') visitingstarttime,issubscribe from doctor_onduty   ds left join doctor_info di on ds.doctorid=di.doctorid where ifnull(ds.delflag,0)=0 and visiting_start_time between ? and ?  ";
        List<Object> params = new ArrayList<>();
        params.add(time);//当天凌晨0点00
        params.add(UnixUtil.getBeginDayOfTomorrow(new Date(time)));
        return queryForList(sql, params);
    }


    //后台订单详情
    public Map<String, Object> findOrderDetailAdmin(long orderid) {
        String sql = "select dv.id, " +
                "       dv.visitcategory, " +
                "       dv.status, " +
                "       ds.visiting_start_time visitingstarttime, " +
                "       ds.visiting_end_time   visitingendtime, " +
                "       up.name        patientname, " +
                "       up.gender      gender, " +
                "       up.age         age, " +
                "       dv.dis_describe introduction, " +
                "       dv.orderno, " +
                "       dv.actualmoney, " +
                "       dv.create_time         createtime, " +
                "       up.phone       patientphone, " +
                "       di.doc_name            docname, " +
                "       dv.record_url            recordurl " +
                " from doctor_video_order dv " +
                "       left join user_account up on dv.userid = up.id " +
                "       left join doctor_scheduling ds on dv.schedulingid = ds.id " +
                "       left join doctor_info di on dv.doctorid = di.doctorid " +
                " where dv.id = ?";
        Map<String, Object> map = queryForMap(sql, orderid);
        if (null != map) {
            map.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "actualmoney")));
            map.put("status", VideoOrderStateEnum.getValue(ModelUtil.getInt(map, "status")).getMessage());
            map.put("visitcategory", "视频订单");
            map.put("gender", ModelUtil.getInt(map, "gender") == 1 ? "男" : "女");
        }
        return map;
    }

    public boolean cancelScheduling(long doctorid, long visiting_start_time) {
        String sql = "update doctor_scheduling set delflag=1,modify_time=? where ifnull(delflag,0)=0 and doctorid=? and visiting_start_time=?";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorid);
        params.add(visiting_start_time);
        return update(sql, params) > 0;
    }

    //查询该时间段是否被预约
    public boolean findScheduling(long doctorid, long visiting_start_time) {
        String sql = "select id,issubscribe from doctor_scheduling where ifnull(delflag,0)=0 and doctorid=? and visiting_start_time=?";
        List<Object> list = new ArrayList<>();
        list.add(doctorid);
        list.add(visiting_start_time);
        Map<String, Object> map = queryForMap(sql, list);
        return ModelUtil.getInt(map, "issubscribe") == 1 ? true : false;//true为被预约
    }

    //查询该医生预约情况
    public List<Map<String, Object>> findSchedulingList(long doctorid) {
        String sql = "select id,visiting_start_time visitingstarttime,issubscribe from doctor_scheduling where ifnull(delflag,0)=0 and doctorid=?";
        List<Object> list = new ArrayList<>();
        list.add(doctorid);
        return queryForList(sql, list);
    }


//    //修改订单状态
//    public boolean updateOrderStatus(long orderid, String guidance) {
//        String sql = "update doctor_video_order set guidance=? where id=?";
//        List<Object> param = new ArrayList<>();
//        param.add(guidance);
//        param.add(orderid);
//        return update(sql, param) > 0;
//    }

    //查看诊后指导
    public Map<String, Object> findGuidance(long orderid) {
        String sql = "select guidance from doctor_video_order where id=?";
        return queryForMap(sql, orderid);
    }


    //订单详情
    public Map<String, Object> findOrderDetail(long orderid) {
        String sql = "select dv.guidance diagnosis,dv.status, ds.visiting_start_time visitingstarttime,ds.visiting_end_time visitingendtime,up.id,up.name name, up.gender gender,up.age age,up.birthday,up.headpic,  " +
                "dv.visitcategory,b.name diseasetime,dv.gohospital,dv.issuredis,dv.dis_describe disdescribe,dv.orderno,dv.actualmoney,dv.subscribe_time createtime from doctor_video_order dv left join user_account up on dv.userid=up.id "
                + " left join doctor_scheduling ds on dv.schedulingid=ds.id and ifnull(ds.delflag,0)=0 left join basics b on dv.disease_time=b.customid and b.type=25 where dv.id=?";
        Map<String, Object> map = queryForMap(sql, orderid);
        if (null != map) {
            map.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "actualmoney")));
            int status = ModelUtil.getInt(map, "status");
            map.put("statusname", VideoOrderStateEnum.getValue(status).getMessage());
            map.put("gender", ModelUtil.getInt(map, "gender") == 1 ? "男" : "女");
            long visitingstarttime = ModelUtil.getLong(map, "visitingstarttime");
            String diagnosis = ModelUtil.getStr(map, "diagnosis");
            long nowTime = UnixUtil.getNowTimeStamp() + ConfigModel.AGORA.EXPIREDTSINMILLISECOND;
            if (status == 2 || status == 3) {
                if (nowTime > visitingstarttime || nowTime == visitingstarttime) {
                    map.put("isopen", 1);//到点了
                } else {
                    map.put("isopen", 0);
                }
            } else {
                map.put("isopen", 0);
            }
            //是否填写诊后指导
            String tips = "";
            map.put("guidance", 0);
            if (status == VideoOrderStateEnum.OrderSuccess.getCode()) {
                if (StrUtil.isEmpty(diagnosis)) {
                    map.put("guidance", 1);
                    tips = TextFixed.doctorVideoGuidanceFailTips;
                } else {
                    map.put("guidance", 2);
                    tips = TextFixed.doctorVideoGuidanceSuccessTips;
                }
                map.put("statusname", "已完成");
            } else if (status == VideoOrderStateEnum.WaitRefund.getCode() || status == VideoOrderStateEnum.OrderFail.getCode()) {
                map.put("status", VideoOrderStateEnum.OrderFail.getCode());
                map.put("statusname", "交易失败");
                tips = TextFixed.doctorVideoFailTips;
            } else if (status == VideoOrderStateEnum.Paid.getCode()) {
                tips = String.format(TextFixed.doctorVideoPaidTips, UnixUtil.getDate(ModelUtil.getLong(map, "visitingstarttime"), "yyyy-MM-dd HH:mm"));
                map.put("statusname", "待接诊");
            } else if (status == VideoOrderStateEnum.InCall.getCode()) {
                long start = ModelUtil.getLong(map, "visitingstarttime");
                long end = ModelUtil.getLong(map, "visitingendtime");
                long ff = end - start;
                long finas = ff / 60000;
                tips = String.format(TextFixed.doctorVideoInCallTips, finas);
                map.put("statusname", "进行中");
            }
            map.put("tips", tips);
        }
        return map;
    }

    public Map<String, Object> getVideoOrder(long orderId) {
        String sql = "select dvo.id, " +
                "       dvo.orderno, " +
                "       dvo.userid, " +
                "       dvo.doctorid, " +
                "       dvo.status, " +
                "       dvo.paystatus, " +
                "       dvo.paytype, " +
                "       dvo.actualmoney, " +
                "       dvo.marketprice, " +
                "       dvo.originalprice, " +
                "       dvo.discount, " +
                "       dvo.token usertoken, " +
                "       dvo.doctor_token doctortoken, " +
                "       dvo.useruid, " +
                "       dvo.doctoruid, " +
                "       dvo.user_device_code userdevicecode, " +
                "       dvo.doctor_device_code doctordevicecode, " +
                "       dvo.guidance, " +
                "       dvo.userinto, " +
                "       dvo.doctorinto, " +
                "       dvo.dis_describe introduction, " +
                "       dvo.schedulingid, " +
                "       dvo.subscribe_time     subscribetime, " +
                "       dvo.subscribe_end_time subscribeendtime, " +
                "       dvo.visitcategory, " +
                "       dvo.create_time        createtime, " +
                "       dvo.userid, " +
                "       dvo.doctorid, " +
                "       ua.userno, " +
                "       ua.name username, " +
                "       ua.headpic userheadpic, " +
                "       cdt.value doctortitle, " +
                "       di.doc_photo_url doctorheadpic, " +
                "       di.doc_name doctorname, " +
                "       di.in_doc_code doctorno " +
                "from doctor_video_order dvo " +
                "       left join user_account ua on dvo.userid = ua.id and ifnull(ua.delflag,0)=0 " +
                "       left join doctor_info di on dvo.doctorid = di.doctorid and ifnull(di.delflag,0)=0 " +
                "       left join code_doctor_title cdt on di.title_id= cdt.id and ifnull(cdt.delflag,0)=0 " +
                "where dvo.id = ? ";
        Map<String, Object> map = queryForMap(sql, orderId);
        if (map != null) {
            map.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "actualmoney")));
            map.put("marketprice", PriceUtil.findPrice(ModelUtil.getLong(map, "marketprice")));
            map.put("originalprice", PriceUtil.findPrice(ModelUtil.getLong(map, "originalprice")));
        }
        return map;
    }

    public boolean findSchTime(long scheduid) {
        String sql = "select id from doctor_scheduling where id=? and issubscribe=0 and ifnull(delflag,0)=0";
        if (queryForMap(sql, scheduid) == null) {//被预约了，无该可用时间
            return true;
        } else {
            return false;
        }
    }

    public Map<String, Object> getVideoOrderDetailed(long orderId) {
        String sql = "select dvo.id, " +
                "       dvo.orderno, " +
                "       dvo.doctorid, " +
                "       dvo.status, " +
                "       dvo.paytype, " +
                "       dvo.visitcategory," +
                "       dvo.subscribe_time     subscribetime, " +
                "       dvo.actualmoney, " +
                "       dvo.guidance diagnosis, " +
                "       b.name diseasetime, " +
                "       dvo.gohospital, " +
                "       dvo.issuredis, " +
                "       dvo.dis_describe disdescribe, " +
                "       dvo.create_time        createtime " +
                "from doctor_video_order dvo " +
                " left join basics b on dvo.disease_time=b.customid and b.type=25 " +
                "where dvo.id = ? ";
        Map<String, Object> map = queryForMap(sql, orderId);
        if (map != null) {
            map.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "actualmoney")));
            int status = ModelUtil.getInt(map, "status");
            map.put("statusname", VideoOrderStateEnum.getValue(status).getMessage());
        }
        return map;
    }

    public Map<String, Object> getVideoOrderSimple(long orderId) {
        String sql = "select id,userid,doctorid from doctor_video_order where id = ? ";
        return queryForMap(sql, orderId);
    }

    public Map<String, Object> getPhoneOrderSimple(long orderId) {
        String sql = "select id,userid,doctorid from doctor_phone_order where id = ? ";
        return queryForMap(sql, orderId);
    }

    public Map<String, Object> getAnswerOrderSimple(long orderId) {
        String sql = "select id,userid,doctorid from doctor_problem_order where id = ? ";
        return queryForMap(sql, orderId);
    }

    //详情照片
    public List<Map<String, Object>> findOrderPhoto(long orderid, int type) {
        String sql = "select disease_picture value from middle_order_picture where orderid=? and ifnull(delflag,0)=0 and order_type=?";
        List<Object> list = new ArrayList<>();
        list.add(orderid);
        list.add(type);
        return queryForList(sql, list);
    }

    //详情症状
    public List<Map<String, Object>> findOrderDiseaseds(long orderid) {
        String sql = "select id,diseasename value from  middle_video_disease where orderid=? ";
        return queryForList(sql, orderid);
    }

    //详情症状
    public List<Map<String, Object>> findOrderDiseased(long orderid) {
        String sql = "select cd.name ,cd.id from middle_video_disease mv left join common_disease_symptoms cd on mv.diseaseid=cd.id where mv.orderid=? and ifnull(mv.delflag,0)=0";
        return queryForList(sql, orderid);
    }

    //详情症状问题
    public List<Map<String, Object>> findOrderAnswer(long orderid) {
        String sql = "select id,problem_name problemname, answer_name answername from user_video_template_answer where orderid=? and ifnull(delflag,0)=0";
        return queryForList(sql, orderid);
    }

    //首页查询当天预约
    public List<Map<String, Object>> getSubscribeList(long doctorid) {
        String sql = "select visiting_start_time visitingstarttime,'视频问诊' visitcategory from doctor_scheduling where issubscribe=1 and visiting_start_time between ? and ? and doctorid=?";
        List<Object> list = new ArrayList<>();
        list.add(UnixUtil.getStart());
        list.add(UnixUtil.getEndTime());
        list.add(doctorid);
        return queryForList(sql, list);
    }

    //家庭成员管理
    public boolean insertFamily(long userid, String name, int age, int gender, String phone, int isdefault, int ismaster) {
        String sql = "insert into user_patient (userid,patient_name,patient_age,patient_phone,patient_gender,is_default,is_master,create_time,delflag) values(?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(name);
        params.add(age);
        params.add(phone);
        params.add(gender);
        params.add(isdefault);
        params.add(ismaster);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(0);
        return insert(sql, params) > 0;
    }

    //家庭成员管理
    public boolean updateFamily(long id, long userid, String name, int age, int gender, String phone, int isdefault, int ismaster) {
        String sql = "update user_patient set userid=?,patient_name=?,patient_age=?,patient_phone=?,patient_gender=?,is_default=?,is_master=?,modify_time=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(name);
        params.add(age);
        params.add(phone);
        params.add(gender);
        params.add(isdefault);
        params.add(ismaster);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean cancelDefault(long userid) {
        String sql = "update user_patient set is_default=0 where userid=? and ifnull(delflag,0) and is_default=1";
        return update(sql, userid) > 0;
    }

    public boolean deleteFamily(long id) {
        String sql = "update user_patient set delflag=1 where id=?";
        return update(sql, id) > 0;
    }

    public List<Map<String, Object>> findFamilyList(long userid) {
        String sql = "select id,patient_name patientname,patient_gender patientgender,patient_age patientage,patient_phone patientphone,is_default isdefault from user_patient where userid=? and ifnull(delflag,0)=0";
        List<Map<String, Object>> list = queryForList(sql, userid);
        for (Map<String, Object> map : list) {
            map.put("patientgender", ModelUtil.getInt(map, "patientgender") == 1 ? "男" : "女");
        }
        return list;
    }

    public Map<String, Object> getMasterFamily(long userid) {
        String sql = "select id,patient_name patientname,patient_gender patientgender,patient_age patientage,patient_phone patientphone,is_default isdefault from user_patient where userid=? and is_master=1";
        Map<String, Object> map = queryForMap(sql, userid);
        if (map != null) {
            map.put("patientgender", ModelUtil.getInt(map, "patientgender") == 1 ? "男" : "女");
        }
        return map;
    }

    public Map<String, Object> findFamilyLists(long userid) {
        String sql = "select ua.name,up.id,patient_name patientname,patient_gender patientgender,patient_age patientage,patient_phone patientphone,is_default isdefault " +
                " from user_patient up left join user_account ua on up.userid=ua.id where up.id=? ";
        Map<String, Object> list = queryForMap(sql, userid);
        if (list != null) {
            list.put("patientgender", ModelUtil.getInt(list, "patientgender") == 1 ? "男" : "女");
        }
        return list;
    }

    public List<Map<String, Object>> findFamilyListAdmin(String username, long begintime, long endtime, int pageSize, int pageIndex) {
        String sql = "select up.create_time,ua.name,up.id,patient_name patientname,patient_gender patientgender,patient_age patientage,patient_phone patientphone,is_default isdefault from user_patient up " +
                " left join user_account ua on up.userid=ua.id where ifnull(up.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(username)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%S%%", username));
        }
        if (begintime != 0) {
            sql += " and up.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and up.create_time < ? ";
            params.add(endtime);
        }
        return query(pageSql(sql, " order by up.id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("patientgender", ModelUtil.getInt(map, "patientgender") == 1 ? "男" : "女");
            }
            return map;
        });
    }

    public long findFamilyListAdminCount(String username, long begintime, long endtime) {
        String sql = "select count(up.id)count from user_patient up " +
                " left join user_account ua on up.userid=ua.id where ifnull(up.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(username)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%S%%", username));
        }
        if (begintime != 0) {
            sql += " and up.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and up.create_time < ? ";
            params.add(endtime);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 订单用户选择的常见病症
     *
     * @param ids
     * @return
     */
    public List<Map<String, Object>> orderVideoDiseaseList(List<Long> ids) {
        String sql = " select orderid, diseasename value  " +
                "from middle_video_disease " +
                "where orderid in (:ids)  " +
                "  and ifnull(delflag, 0) = 0 ";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        return queryForList(sql, params);
    }

    public Map<String, Object> getSetingTime(int ordertype) {
        String sql = "select start_time,end_time,interval_time from video_time where ifnull(delflag,0)=0 and ordertype=? limit 1 ";
        return queryForMap(sql, ordertype);
    }

    public List<Map<String, Object>> findResults(long doctorid) {
        String sql = "select visiting_start_time,issubscribe from doctor_scheduling where ifnull(delflag,0)=0 and visiting_start_time >= ? and doctorid=?";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(doctorid);
        return queryForList(sql, params);
    }

    public Map<String, Object> getUserInfo(long userid) {
        String sql = "select ua.id,ua.name,ua.gender,ua.age,ua.headpic from user_account ua where ua.id=?";
        Map<String, Object> map = queryForMap(sql, userid);
        if (map != null) {
            map.put("gender", ModelUtil.getInt(map, "gender") == 1 ? "男" : "女");
        }
        return map;
    }

    public List<Map<String, Object>> getAnswerDiseaseList(long orderId) {
        String sql = "select id, diseasename value " +
                "from middle_answer_disease " +
                "where orderid = ? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        return queryForList(sql, params);
    }

    /**
     * 病症
     *
     * @param orderId 订单ID
     * @return
     */
    public List<Map<String, Object>> getPhoneDiseaseList(long orderId) {
        String sql = "select id, diseasename value " +
                "from middle_phone_disease " +
                "where orderid = ? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        return queryForList(sql, params);
    }

    /**
     * 病症
     *
     * @param orderId 订单ID
     * @return
     */
    public List<Map<String, Object>> getVideoDiseaseList(long orderId) {
        String sql = "select id, diseasename value " +
                "from middle_video_disease " +
                "where orderid = ? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        return queryForList(sql, params);
    }

    public Map<String, Object> getVideoOrderGuidance(long orderid) {
        String sql = " select id,doctorid,userid,guidance diagnosis,diagnostic_results diagnosticresults,dis_describe disdescribe from doctor_video_order where id = ?";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        return queryForMap(sql, params);
    }

    public Map<String, Object> getPhoneGuidance(long orderid) {
        String sql = " select id,doctorid,userid,diagnosis,diagnostic_results diagnosticresults ,dis_describe disdescribe from doctor_phone_order where id = ?";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        return queryForMap(sql, params);
    }

    public Map<String, Object> getAnswerGuidance(long orderid) {
        String sql = " select id,doctorid,userid,diagnosis,diagnostic_results diagnosticresults,dis_describe disdescribe from doctor_problem_order where id = ?";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        return queryForMap(sql, params);
    }

    public Map<String, Object> getOrderEvaluate(long orderid, int ordertype) {
        String sql = " select order_number orderno,ue.userid,ue.doctorid,ue.evaluate,ue.content,ue.isanonymous " +
                "from user_evaluate ue " +
                "where ifnull(ue.delflag, 0) = 0 " +
                "  and ue.order_number = ? " +
                "  and ue.ordertype = ? ";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        params.add(ordertype);
        return queryForMap(sql, params);
    }

    public boolean addOrderEvaluate(long orderid, int ordertype, long userid, long doctorid, int isanonymous, int evaluate, String content) {
        String sql = " insert into user_evaluate(order_number, userid, ordertype, evaluate, content, doctorid, delflag, create_time, isanonymous) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(orderid);
        params.add(userid);
        params.add(ordertype);
        params.add(evaluate);
        params.add(content);
        params.add(doctorid);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(isanonymous);
        return insert(sql, params) > 0;
    }



    public Map<String, Object> getDoctorPrescription(long orderid, int ordertype) {
        String sql = " select prescriptionid,pres_photo_url presphotourl,examine  from doc_prescription where orderid =? and examine=2 and ifnull(order_type,1)=? order by create_time desc limit 1 ";
        List<Object> list = new ArrayList<>();
        list.add(orderid);
        list.add(ordertype);
        return queryForMap(sql, list);
    }

    public Map<String, Object> getUserPrescription(long orderid, int ordertype) {
        String sql = " select prescriptionid,pres_photo_url presphotourl,examine  from doc_prescription where orderid =? and ifnull(order_type,1)=? and examine=2 order by create_time desc limit 1 ";
        List<Object> list = new ArrayList<>();
        list.add(orderid);
        list.add(ordertype);
        return queryForMap(sql, list);
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
     * 保存诊疗结果
     *
     * @param orderId
     * @param diagnosis
     * @return
     */
    public boolean updateProblemOrderDiagnosis(long orderId, String diagnosis, String diagnosticresults) {
        String sql = "update doctor_problem_order set diagnosis=?,result_time=?,diagnostic_results=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(diagnosis);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(diagnosticresults);
        params.add(orderId);
        return update(sql, params) > 0;
    }

    public boolean getPrescription(long orderid){
        String sql="select pres_photo_url from doc_prescription where ifnull(delflag,0)=0 and ifnull(examine,2)=2 and orderid=? and ifnull(order_type,1)=1";
        return queryForMap(sql,orderid)!=null?false:true;
    }

    public Map<String, Object> answerOrderId(long orderid) {
        String sql = " SELECT " +
                "       dpo.id as orderid, " +
                "       dpo.userid, " +
                "       dpo.doctorid, " +
                "       dpo.create_time AS createtime, " +
                "       dpoe.uplatform AS uplatform, " +
                "       dpoe.utoken AS utoken, " +
                "       dpoe.dplatform AS dplatform, " +
                "       dpoe.dtoken AS dtoken " +
                " FROM doctor_problem_order dpo " +
                "       LEFT JOIN doctor_problem_order_extend dpoe ON dpoe.orderid = dpo.id " +
                " WHERE dpo.id = ? ";
        return queryForMap(sql, orderid);
    }


    /**
     * 保存电话诊疗结果
     *
     * @param orderId
     * @param diagnosis
     * @return
     */
    public boolean updatePhoneOrderDiagnosis(long orderId, String diagnosis, String diagnosticresults) {
        String sql = "update doctor_phone_order set diagnosis=?,result_time=?,diagnostic_results=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(diagnosis);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(diagnosticresults);
        params.add(orderId);
        return update(sql, params) > 0;
    }

    public Map<String, Object> phoneOrderId(long orderid) {
        String sql = " SELECT " +
                "       dpo.id as orderid, " +
                "       dpo.userid, " +
                "       dpo.doctorid, " +
                "       dpo.create_time AS createtime, " +
                "       dpoe.uplatform AS uplatform, " +
                "       dpoe.utoken AS utoken, " +
                "       dpoe.dplatform AS dplatform, " +
                "       dpoe.dtoken AS dtoken " +
                " FROM doctor_phone_order dpo " +
                "            LEFT JOIN doctor_phone_order_extend dpoe ON dpoe.orderid = dpo.id " +
                " WHERE dpo.id = ? ";
        return queryForMap(sql, orderid);
    }

    /**
     * 添加push消息表
     *
     * @return
     */
    public boolean addPushApp(String title, String content, int type, String typeName, int receiveId, int receiveType, int platform,
                              String xgtoken) {
        String sql = "insert into push_app(title, content, type, typename, receiveid, receivetype, delflag, create_time,ispush,platform,xgtoken)" +
                "values (?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(title);
        params.add(content);
        params.add(type);
        params.add(typeName);
        params.add(receiveId);
        params.add(receiveType);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(0);
        params.add(platform);
        params.add(xgtoken);
        return insert(sql, params) > 0;
    }


    /**
     * 添加消息
     *
     * @param url            医生、用户头像
     * @param name           展示标题
     * @param type           1 用户 2 医生
     * @param typeName       根据不同类型填写不同数据
     * @param messageType    消息类型
     * @param sendId         发送给谁
     * @param messageText    消息文本
     * @param messageSubtext 消息副文本
     * @return
     */
    public boolean addMessage(String url, String name, int type, String typeName, int messageType, long sendId, String messageText, String messageSubtext) {
        String sql = "insert into message(url,name, type, type_name, message_type, sendid, message_text, message_subtext, delflag, create_time) " +
                "values (?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(url);
        params.add(name);
        params.add(type);
        params.add(typeName);
        params.add(messageType);
        params.add(sendId);
        params.add(messageText);
        params.add(messageSubtext);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }

    public Map<String, Object> docName(long doctorid) {
        String sql = " select doc_name name from doctor_info where ifnull(delflag,0)=0 and doctorid = ? ";
        return queryForMap(sql, doctorid);
    }


    //编辑诊后指导
    public boolean updateVideoOrderDiagnosis(long orderid, String guidance, String diagnosticresults) {
        String sql = "update doctor_video_order set guidance=?,result_time=?,diagnostic_results=? where id=?";
        List<Object> param = new ArrayList<>();
        param.add(guidance);
        param.add(UnixUtil.getNowTimeStamp());
        param.add(diagnosticresults);
        param.add(orderid);
        return update(sql, param) > 0;
    }

    public Map<String, Object> videoOrderId(long orderid) {
        String sql = " SELECT " +
                "       dpo.id as orderid, " +
                "       dpo.userid, " +
                "       dpo.doctorid, " +
                "       dpo.create_time AS createtime, " +
                "       dpoe.uplatform AS uplatform, " +
                "       dpoe.utoken AS utoken, " +
                "       dpoe.dplatform AS dplatform, " +
                "       dpoe.dtoken AS dtoken " +
                " FROM doctor_video_order dpo " +
                "       LEFT JOIN doctor_video_order_extend dpoe ON dpoe.orderid = dpo.id " +
                " WHERE dpo.id = ? ";
        return queryForMap(sql, orderid);
    }


    public List<Map<String, Object>> getPictureList(long orderId) {
        String sql = " select disease_picture value from middle_order_picture where orderid=? and ifnull(delflag,0)=0 and order_type=? ";
        return queryForList(sql, orderId, OrderTypeEnum.Video.getCode());
    }
}
