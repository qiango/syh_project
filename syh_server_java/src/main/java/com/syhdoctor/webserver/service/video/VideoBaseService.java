package com.syhdoctor.webserver.service.video;

import com.syhdoctor.common.utils.EnumUtils.MessageTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.OrderTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.TypeNameAppPushEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.TextFixed;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.mapper.video.DoctorVideoMapper;
import com.syhdoctor.webserver.service.prescription.PrescriptionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

public abstract class VideoBaseService extends BaseService {


    @Autowired
    private DoctorVideoMapper doctorVideoMapper;

    @Autowired
    private PrescriptionService prescriptionService;

    public List<Map<String, Object>> orderVideoDiseaseList(List<Long> ids) {
        return doctorVideoMapper.orderVideoDiseaseList(ids);
    }

    //医生预约时间列表
    public List<Map<String, Object>> findSchedulingList(long doctorid) {
        return doctorVideoMapper.findSchedulingList(doctorid);
    }

    //取消预约
    public boolean cancelScheduling(long doctorid, List<?> visiting_start_time) {
        long startTime = 0;
        for (Object o : visiting_start_time) {
            startTime = Long.parseLong(o.toString());
            boolean scheduling = doctorVideoMapper.findScheduling(doctorid, startTime);
            if (scheduling) {
                throw new ServiceException("时间段" + startTime + "已有用户预约");
            }
            return doctorVideoMapper.cancelScheduling(doctorid, startTime);
        }
        return true;
    }

    //订单详情
    public Map<String, Object> findOrderDetail(long orderid) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> orderDetail = doctorVideoMapper.findOrderDetail(orderid);
        map.put("title", orderDetail);//订单详情
        map.put("picturelist", doctorVideoMapper.findOrderPhoto(orderid, OrderTypeEnum.Video.getCode()));//详情照片
        Map<String, Object> userinfo = new HashMap<>();
        userinfo.put("id", ModelUtil.getLong(orderDetail, "id"));
        userinfo.put("name", ModelUtil.getStr(orderDetail, "name"));
        userinfo.put("age", ModelUtil.getLong(orderDetail, "age"));
        userinfo.put("gender", ModelUtil.getStr(orderDetail, "gender"));
        userinfo.put("birthday", ModelUtil.getLong(orderDetail, "birthday"));
        userinfo.put("headpic", ModelUtil.getStr(orderDetail, "headpic"));
        map.put("userinfo", userinfo);
//        map.put("diseaselist", doctorVideoMapper.findOrderDiseaseds(orderid));//详情症状
//        map.put("answer", doctorVideoMapper.findOrderAnswer(orderid));//详情症状问答
        return map;
    }

    //后台订单详情
    public Map<String, Object> findOrderDetailAdmin(long orderid) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", doctorVideoMapper.findOrderDetailAdmin(orderid));//订单详情
        map.put("photo", doctorVideoMapper.findOrderPhoto(orderid, OrderTypeEnum.Video.getCode()));//详情照片
        map.put("disease", doctorVideoMapper.findOrderDiseased(orderid));//详情症状
        map.put("answer", doctorVideoMapper.findOrderAnswer(orderid));//详情症状问答
        return map;
    }

    //首页查询当天预约
    public Map<String, Object> getSubscribeList(long doctorid) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> getsubscribe = doctorVideoMapper.getSubscribeList(doctorid);
        map.put("time", getsubscribe);
        map.put("count", getsubscribe.size());
        return map;
    }

    public List<Map<String, Object>> findOrderListAll(String patientname, String phonenumber, String dcotorname, int status, int visitcategory, long begintime, long endtime, int pageSize, int pageIndex) {
        return doctorVideoMapper.findOrderListAll(patientname, phonenumber, dcotorname, status, visitcategory, begintime, endtime, pageSize, pageIndex);
    }

    public long findOrderListCountAll(String patientname, String phonenumber, String dcotorname, int status, int visitcategory, long begintime, long endtime) {
        return doctorVideoMapper.findOrderListCountAll(patientname, phonenumber, dcotorname, status, visitcategory, begintime, endtime);
    }

    //家庭成员管理
    public boolean insertFamily(long id, long userid, String name, int age, int gender, String phone, int isdefault, int ismaster) {
        if (id != 0) {
            return doctorVideoMapper.updateFamily(id, userid, name, age, gender, phone, isdefault, ismaster);
        } else {
            int size = doctorVideoMapper.findFamilyList(userid).size();
            if (size > 6) {
                throw new ServiceException("您已添加了6人,无法继续添加");
            }
            return doctorVideoMapper.insertFamily(userid, name, age, gender, phone, isdefault, ismaster);
        }
    }

    public boolean cancleDefault(long userid, int isdefault) {
        if (isdefault == 1) {
            return doctorVideoMapper.cancelDefault(userid);
        }
        return true;
    }

    public boolean deleteFamily(long id) {
        return doctorVideoMapper.deleteFamily(id);
    }

    public List<Map<String, Object>> findFamilyList(long userid) {
        return doctorVideoMapper.findFamilyList(userid);
    }

    public Map<String, Object> getMasterFamily(long userid) {
        return doctorVideoMapper.getMasterFamily(userid);
    }

    public List<Map<String, Object>> findFamilyListAdmin(String username, long begintime, long endtime, int pageSize, int pageIndex) {
        return doctorVideoMapper.findFamilyListAdmin(username, begintime, endtime, pageSize, pageIndex);
    }

    public long findFamilyListAdminCount(String username, long begintime, long endtime) {
        return doctorVideoMapper.findFamilyListAdminCount(username, begintime, endtime);
    }

    public Map<String, Object> findFamilyListAdminDetail(long id) {
        return doctorVideoMapper.findFamilyLists(id);
    }

    public Map<String, Object> findScheudList(long doctorid) {
        Map<String, Object> settTime = doctorVideoMapper.getSetingTime(OrderTypeEnum.Video.getCode());
        int start = ModelUtil.getInt(settTime, "start_time");
        int end = ModelUtil.getInt(settTime, "end_time");
        int interval_time = ModelUtil.getInt(settTime, "interval_time");//间隔时间
        int fina = 0;
        try {
            int interval = (end - start) * 60;
            fina = interval / interval_time;
            if (fina < 0 || fina == 0) {
                throw new ServiceException("配的开始时间结束时间不合法,请检查");
            }
        } catch (Exception e) {
            throw new ServiceException("配的开始时间结束时间不合法,请检查");
        }
        Map<String, Object> map = new HashMap<>();
        long currentTimes = UnixUtil.getNowTimeStamp();
        List<Map<String, Object>> result = doctorVideoMapper.findResults(doctorid);
        Map<Long, Object> mapResult = new HashMap<>();
        for (Map<String, Object> m : result) {
            long time = ModelUtil.getLong(m, "visiting_start_time");
            mapResult.put(time, ModelUtil.getInt(m, "issubscribe"));
        }

        List<Map<String, Object>> doctorLdleTimeR = new ArrayList<>();
        long currentTime = UnixUtil.getStart();//当天开始时间
        Date time = UnixUtil.getStartDate();
        for (int i = 0; i < 14; i++) {
            Map<String, Object> map2 = new HashMap<>();
            long tomorrow;
            if (i == 0) {
                tomorrow = currentTime;
            } else {
                tomorrow = UnixUtil.getBeginDayOfTomorrow(time);//每天的日期
            }
            time = new Date(tomorrow);
            List<Map<String, Object>> doctorLdleR = new ArrayList<>();
            long startTimeCurrent = tomorrow + start * 60 * 60 * 1000 - interval_time * 60 * 1000;//每天每段开始时间
            int a = 0;
            for (int j = 0; j < fina + 1; j++) {
                startTimeCurrent += interval_time * 60 * 1000;
                if (i == 0) {
                    if (currentTimes > startTimeCurrent) {
                        continue;
                    }
                }
                Map<String, Object> map1 = new HashMap<>();
                map1.put("ldlestime", startTimeCurrent);
                if (mapResult.keySet().contains(startTimeCurrent)) {
                    map1.put("isopen", 1);//是否开放
                    map1.put("issubscribe", mapResult.get(startTimeCurrent));//未被预约
                    a = 1;
                } else {
                    map1.put("isopen", 0);//是否开放
                    map1.put("issubscribe", 0);
                }
                doctorLdleR.add(map1);
            }
            map2.put("doctorldler", doctorLdleR);
            map2.put("date", tomorrow);
            map2.put("isselect", a);
            doctorLdleTimeR.add(map2);
        }
        map.put("currenttime", currentTime);
        map.put("doctorldletimer", doctorLdleTimeR);
        return map;
    }

    public Map<String, Object> findDoctorSchdue(long time, int pageSize, int pageIndex, String docname, String phone, String number) {
        Map<String, Object> settTime = doctorVideoMapper.getSetingTime(OrderTypeEnum.Video.getCode());
        int interval_time = ModelUtil.getInt(settTime, "interval_time");//间隔时间
        Map<String, Object> mapResult = new HashMap<>();
        if (time == 0) {//不传时间则默认当天时间预约列表
            time = UnixUtil.getStart();
        }
        List<Map<String, Object>> doctorSchdue = doctorVideoMapper.findDoctorSchdue(time, docname, phone, number);//当天所有预约情况
        Map<String, List<Map<String, Object>>> mapSchdue = new HashMap<>();
        for (Map<String, Object> map1 : doctorSchdue) {
            String name = ModelUtil.getStr(map1, "name");
            Map<String, Object> info = new HashMap<>();
            info.put("visitingstarttime", ModelUtil.getLong(map1, "visitingstarttime"));
            info.put("issubscribe", ModelUtil.getInt(map1, "issubscribe"));
            if (mapSchdue.containsKey(name)) {
                mapSchdue.get(name).add(info);
            } else {
                List<Map<String, Object>> list1 = new ArrayList<>();
                list1.add(info);
                mapSchdue.put(name, list1);
            }
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (String name : mapSchdue.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            List<Map<String, Object>> list1 = mapSchdue.get(name);//该用户下所有预约的时间和开通时间
            long size = list1.size();
            long fina = size * interval_time;
            float m = (float) fina / 60;
            BigDecimal b = new BigDecimal(m);
            map.put("timeList", list1);
            map.put("alltime", b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
            list.add(map);
        }
        int offset = (pageIndex - 1) * pageSize;
        int total = list.size();
        int limit = total - offset;

        if (list != null && total > 0) {
            //查询当页无数据，需要显示最后一页的内容
            if (limit <= 0) {
                offset = total % pageSize == 0 ? (total / pageSize) - 1 * pageSize : total / pageSize * pageSize;
                mapResult.put("doctorTime", list.subList(offset, total));
                //正常查询，查询当页有数据
            } else {
                limit = pageSize > limit ? limit : pageSize;
                List<Map<String, Object>> ss = list.subList(offset, offset + limit);
                mapResult.put("doctorTime", ss);
            }
            mapResult.put("count", total);
        }
        return mapResult;
    }

    public Map<String, Object> findDoctorSchdues(long time) {
        Map<String, Object> settTime = doctorVideoMapper.getSetingTime(OrderTypeEnum.Video.getCode());
        int interval_time = ModelUtil.getInt(settTime, "interval_time");//间隔时间
        Map<String, Object> mapResult = new HashMap<>();
        if (time == 0) {//不传时间则默认当天时间预约列表
            time = UnixUtil.getStart();
        }
        List<Map<String, Object>> doctorSchdue = doctorVideoMapper.findDoctorSchdueDate(time);//当天所有预约情况
        Map<String, List<Map<String, Object>>> mapSchdue = new HashMap<>();
        for (Map<String, Object> map1 : doctorSchdue) {
            String name = ModelUtil.getStr(map1, "name");
            Map<String, Object> info = new HashMap<>();
            info.put("visitingstarttime", ModelUtil.getStr(map1, "visitingstarttime"));
            info.put("issubscribe", ModelUtil.getInt(map1, "issubscribe"));
            if (mapSchdue.containsKey(name)) {
                mapSchdue.get(name).add(info);
            } else {
                List<Map<String, Object>> list1 = new ArrayList<>();
                list1.add(info);
                mapSchdue.put(name, list1);
            }
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (String name : mapSchdue.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            List<Map<String, Object>> list1 = mapSchdue.get(name);//该用户下所有预约的时间和开通时间
            long size = list1.size();
            long fina = size * interval_time;
            float m = (float) fina / 60;
            BigDecimal b = new BigDecimal(m);
            map.put("timeList", list1);
            map.put("alltime", b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
            list.add(map);
        }
        int total = list.size();
        mapResult.put("count", total);
        mapResult.put("doctorTime", list);
        return mapResult;
    }

    public List<Map<String, Object>> findDoctorSchduesNew(long starttime, long endtime) {
        List<Map<String, Object>> doctorSchdue = doctorVideoMapper.findDoctorSchdueDateNew(starttime, endtime);//当天所有预约情况
        Map<String, List<Map<String, Object>>> mapSchdue = new LinkedHashMap<>();
        for (Map<String, Object> map1 : doctorSchdue) {
            String dayname = ModelUtil.getStr(map1, "dayname");
            Map<String, Object> info = new HashMap<>();
            info.put("visitingstarttime", ModelUtil.getStr(map1, "visitingstarttime"));
            info.put("visitingendtime", ModelUtil.getStr(map1, "visitingendtime"));
            info.put("time", ModelUtil.getStr(map1, "time"));
            info.put("name", ModelUtil.getStr(map1, "name"));
            info.put("day", ModelUtil.getStr(map1, "day"));
            info.put("issubscribe", ModelUtil.getInt(map1, "issubscribe"));
            if (mapSchdue.containsKey(dayname)) {
                mapSchdue.get(dayname).add(info);
            } else {
                List<Map<String, Object>> list1 = new ArrayList<>();
                list1.add(info);
                mapSchdue.put(dayname, list1);
            }
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (String name : mapSchdue.keySet()) {
            Map<String, Object> map = new HashMap<>();
            List<Map<String, Object>> list1 = mapSchdue.get(name);
            map.put("timeList", list1);
            list.add(map);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("docname", "医生姓名");
        map.put("alltime", "总时长");
        map.put("date", "日期");
        list.add(0, map);
        return list;
    }

    public List<Map<String, Object>> findDoctorSchdueList(long time) {
        List<Map<String, Object>> list = (List<Map<String, Object>>) findDoctorSchdues(time).get("doctorTime");

        return list;
    }

    public Map<String, Object> findTimeList(long times) {
        Map<String, Object> map = new HashMap<>();
        if (times == 0) {//不传时间则默认当天时间预约列表
            times = UnixUtil.getStart();
        }
        Map<String, Object> settTime = doctorVideoMapper.getSetingTime(OrderTypeEnum.Video.getCode());
        int start = ModelUtil.getInt(settTime, "start_time");
        int end = ModelUtil.getInt(settTime, "end_time");
        int interval_time = ModelUtil.getInt(settTime, "interval_time");//间隔时间
        int fina = 0;
        try {
            int interval = (end - start) * 60;
            fina = interval / interval_time;
            if (fina < 0 || fina == 0) {
                throw new ServiceException("配的开始时间结束时间不合法,请检查");
            }
        } catch (Exception e) {
            throw new ServiceException("配的开始时间结束时间不合法,请检查");
        }
        long tomorrow = times;//传入时间
        long startTimeCurrent = tomorrow + start * 60 * 60 * 1000 - interval_time * 60 * 1000;//每天每段开始时间
        List<Long> timeList = new ArrayList<>();
        for (int j = 0; j < fina + 1; j++) {
            startTimeCurrent += interval_time * 60 * 1000;
            timeList.add(startTimeCurrent);
        }
        map.put("timeList", timeList);
        map.put("intervaltime", interval_time);
        return map;
    }

    public Map<String, Object> findDoctorSchduePhone(long time, int pageSize, int pageIndex, String docname, String phone, String number) {
        Map<String, Object> settTime = doctorVideoMapper.getSetingTime(OrderTypeEnum.Phone.getCode());
        int interval_time = ModelUtil.getInt(settTime, "interval_time");//间隔时间
        Map<String, Object> mapResult = new HashMap<>();
        if (time == 0) {//不传时间则默认当天时间预约列表
            time = UnixUtil.getStart();
        }
        List<Map<String, Object>> doctorSchdue = doctorVideoMapper.findDoctorSchduePhone(time, docname, phone, number);//当天所有预约情况
        Map<String, List<Map<String, Object>>> mapSchdue = new HashMap<>();
        for (Map<String, Object> map1 : doctorSchdue) {
            String name = ModelUtil.getStr(map1, "name");
            Map<String, Object> info = new HashMap<>();
            info.put("visitingstarttime", ModelUtil.getLong(map1, "visitingstarttime"));

            info.put("issubscribe", ModelUtil.getInt(map1, "issubscribe"));
            if (mapSchdue.containsKey(name)) {
                mapSchdue.get(name).add(info);
            } else {
                List<Map<String, Object>> list1 = new ArrayList<>();
                list1.add(info);
                mapSchdue.put(name, list1);
            }
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (String name : mapSchdue.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            List<Map<String, Object>> list1 = mapSchdue.get(name);
            map.put("timeList", list1);
            int size = list1.size();
            long fina = size * interval_time;
            float m = (float) fina / 60;
            BigDecimal b = new BigDecimal(m);
            map.put("timeList", list1);
            map.put("alltime", b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
            list.add(map);
        }
        int offset = (pageIndex - 1) * pageSize;
        int total = list.size();
        int limit = total - offset;

        if (list != null && total > 0) {
            //查询当页无数据，需要显示最后一页的内容
            if (limit <= 0) {
                offset = total % pageSize == 0 ? (total / pageSize) - 1 * pageSize : total / pageSize * pageSize;
//                list.subList(offset, total);
                mapResult.put("doctorTime", list.subList(offset, total));
                //正常查询，查询当页有数据
            } else {
                limit = pageSize > limit ? limit : pageSize;
                List<Map<String, Object>> ss = list.subList(offset, offset + limit);
                mapResult.put("doctorTime", ss);
            }
            mapResult.put("count", total);
        }
        return mapResult;
    }

    public Map<String, Object> findDoctorSchduePhones(long time) {
        Map<String, Object> settTime = doctorVideoMapper.getSetingTime(OrderTypeEnum.Phone.getCode());
        int interval_time = ModelUtil.getInt(settTime, "interval_time");//间隔时间
        Map<String, Object> mapResult = new HashMap<>();
        if (time == 0) {//不传时间则默认当天时间预约列表
            time = UnixUtil.getStart();
        }
        List<Map<String, Object>> doctorSchdue = doctorVideoMapper.findDoctorSchduePhoneDate(time);//当天所有预约情况
        Map<String, List<Map<String, Object>>> mapSchdue = new HashMap<>();
        for (Map<String, Object> map1 : doctorSchdue) {
            String name = ModelUtil.getStr(map1, "name");
            Map<String, Object> info = new HashMap<>();
            info.put("visitingstarttime", ModelUtil.getStr(map1, "visitingstarttime"));
            info.put("issubscribe", ModelUtil.getInt(map1, "issubscribe"));
            if (mapSchdue.containsKey(name)) {
                mapSchdue.get(name).add(info);
            } else {
                List<Map<String, Object>> list1 = new ArrayList<>();
                list1.add(info);
                mapSchdue.put(name, list1);
            }
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (String name : mapSchdue.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            List<Map<String, Object>> list1 = mapSchdue.get(name);
            map.put("timeList", list1);
            int size = list1.size();
            long fina = size * interval_time;
            float m = (float) fina / 60;
            BigDecimal b = new BigDecimal(m);
            map.put("timeList", list1);
            map.put("alltime", b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
            list.add(map);
        }
        int total = list.size();
        mapResult.put("count", total);
        mapResult.put("doctorTime", list);
        return mapResult;
    }

    public List<Map<String, Object>> findDoctorSchduePhonesNew(long starttime, long endtime) {
        List<Map<String, Object>> doctorSchdue = doctorVideoMapper.findDoctorSchduePhoneNew(starttime, endtime);//当天所有预约情况
        Map<String, List<Map<String, Object>>> mapSchdue = new LinkedHashMap<>();
        for (Map<String, Object> map1 : doctorSchdue) {
            String dayname = ModelUtil.getStr(map1, "dayname");
            Map<String, Object> info = new HashMap<>();
            info.put("visitingstarttime", ModelUtil.getStr(map1, "visitingstarttime"));
            info.put("visitingendtime", ModelUtil.getStr(map1, "visitingendtime"));
            info.put("time", ModelUtil.getStr(map1, "time"));
            info.put("name", ModelUtil.getStr(map1, "name"));
            info.put("day", ModelUtil.getStr(map1, "day"));
            info.put("issubscribe", ModelUtil.getInt(map1, "issubscribe"));
            if (mapSchdue.containsKey(dayname)) {
                mapSchdue.get(dayname).add(info);
            } else {
                List<Map<String, Object>> list1 = new ArrayList<>();
                list1.add(info);
                mapSchdue.put(dayname, list1);
            }
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (String name : mapSchdue.keySet()) {
            Map<String, Object> map = new HashMap<>();
            List<Map<String, Object>> list1 = mapSchdue.get(name);
            map.put("timeList", list1);
            list.add(map);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("docname", "医生姓名");
        map.put("alltime", "总时长");
        map.put("date", "日期");
        list.add(0, map);
        return list;
    }

    public List<Map<String, Object>> findDoctorSchduePhoneList(long time) {
        return (List<Map<String, Object>>) findDoctorSchduePhones(time).get("doctorTime");
    }

    public Map<String, Object> findTimeListPhone(long times) {
        Map<String, Object> map = new HashMap<>();
        if (times == 0) {//不传时间则默认当天时间预约列表
            times = UnixUtil.getStart();
        }
        Map<String, Object> settTime = doctorVideoMapper.getSetingTime(OrderTypeEnum.Phone.getCode());
        int start = ModelUtil.getInt(settTime, "start_time");
        int end = ModelUtil.getInt(settTime, "end_time");
        int interval_time = ModelUtil.getInt(settTime, "interval_time");//间隔时间
        int fina = 0;
        try {
            int interval = (end - start) * 60;
            fina = interval / interval_time;
            if (fina < 0 || fina == 0) {
                throw new ServiceException("配的开始时间结束时间不合法,请检查");
            }
        } catch (Exception e) {
            throw new ServiceException("配的开始时间结束时间不合法,请检查");
        }
        long tomorrow = times;//传入时间
        long startTimeCurrent = tomorrow + start * 60 * 60 * 1000 - interval_time * 60 * 1000;//每天每段开始时间
        List<Long> timeList = new ArrayList<>();
        for (int j = 0; j < fina + 1; j++) {
            startTimeCurrent += interval_time * 60 * 1000;
            timeList.add(startTimeCurrent);
        }
        map.put("timeList", timeList);
        map.put("intervaltime", interval_time);
        return map;
    }

    public Map<String, Object> getOrderGuidance(long orderid, int orderType) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> order = null;
        long doctorId;
        List<Map<String, Object>> list = new ArrayList<>();
        switch (OrderTypeEnum.getValue(orderType)) {
            case Answer:
                order = doctorVideoMapper.getAnswerGuidance(orderid);
                list = doctorVideoMapper.getAnswerDiseaseList(orderid);
                break;
            case Phone:
                order = doctorVideoMapper.getPhoneGuidance(orderid);
                list = doctorVideoMapper.getPhoneDiseaseList(orderid);
                break;
            case Video:
                order = doctorVideoMapper.getVideoOrderGuidance(orderid);
                list = doctorVideoMapper.getVideoDiseaseList(orderid);
                break;
            default:
                break;
        }
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        Map<String, Object> prescription = doctorVideoMapper.getUserPrescription(orderid, orderType);
        order.put("presphotourl", ModelUtil.getStr(prescription, "presphotourl"));
        doctorId = ModelUtil.getLong(order, "doctorid");
        if (list.size() > 0) {
            result.put("diseaselist", list);
        } else {
            result.put("diseaselist", new ArrayList<>());
        }
        result.put("doctor", doctorVideoMapper.getDoctor(doctorId));//医生详情
        result.put("order", order);
        result.put("picturelist", doctorVideoMapper.findOrderPhoto(orderid, orderType));
        return result;
    }

    public Map<String, Object> getOrderGuidanceDoctor(long orderid, int orderType) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> order = null;
        long userid;
        List<Map<String, Object>> list = new ArrayList<>();
        switch (OrderTypeEnum.getValue(orderType)) {
            case Answer:
                order = doctorVideoMapper.getAnswerGuidance(orderid);
                list = doctorVideoMapper.getAnswerDiseaseList(orderid);
//                Map<String, Object> prescription = doctorVideoMapper.getPrescription(orderid);
//                order.put("presphotourl", ModelUtil.getStr(prescription, "presphotourl"));
                break;
            case Phone:
                order = doctorVideoMapper.getPhoneGuidance(orderid);
                list = doctorVideoMapper.getPhoneDiseaseList(orderid);
                break;
            case Video:
                order = doctorVideoMapper.getVideoOrderGuidance(orderid);
                list = doctorVideoMapper.getVideoDiseaseList(orderid);
                break;
            default:
                break;
        }
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        Map<String, Object> prescription = doctorVideoMapper.getDoctorPrescription(orderid, orderType);
        order.put("presphotourl", ModelUtil.getStr(prescription, "presphotourl"));
        order.put("examine", ModelUtil.getInt(prescription, "examine"));
        order.put("prescriptionid", ModelUtil.getLong(prescription, "prescriptionid"));
        userid = ModelUtil.getLong(order, "userid");
        result.put("userinfo", doctorVideoMapper.getUserInfo(userid));//用户详情
        if (list.size() > 0) {
            result.put("diseaselist", list);
        } else {
            result.put("diseaselist", new ArrayList<>());
        }
        result.put("order", order);
        result.put("picturelist", doctorVideoMapper.findOrderPhoto(orderid, orderType));
        return result;
    }


    public boolean addOrderGuidance(long orderid, int orderType, String diagnosis, String diagnosisfordec, String diagnosticresults, String presNo, long oftenPrescriptionId, List<?> druglist, long doctorid) {
        if (StringUtils.isNotEmpty(diagnosticresults)) {
            //发送处方
            if (oftenPrescriptionId != 0) {
                prescriptionService.addPrescriptionByOften(oftenPrescriptionId, presNo, orderid, orderType);
            } else if (druglist != null && druglist.size() > 0) {
                prescriptionService.sendPrescriptionExamine(orderid, doctorid, diagnosisfordec, druglist, orderType);
            }
        }
        switch (OrderTypeEnum.getValue(orderType)) {
            case Answer:
                doctorVideoMapper.updateProblemOrderDiagnosis(orderid, diagnosis, diagnosticresults);
                answerOrderGuidancePush(orderid);//图文诊后指导给用户推送
                break;
            case Phone:
                doctorVideoMapper.updatePhoneOrderDiagnosis(orderid, diagnosis, diagnosticresults);
                phoneOrderGuidancePush(orderid);//电话诊后指导给用户推送
                break;
            case Video:
                doctorVideoMapper.updateVideoOrderDiagnosis(orderid, diagnosis, diagnosticresults);
                videoOrderGuidancePush(orderid);//视频诊后指导给用户推送
                break;
            default:
                break;
        }
        return true;
    }


    /**
     * 图文订单医生完成诊后指导给用户推送
     *
     * @param orderid
     */
    public void answerOrderGuidancePush(long orderid) {
        Map<String, Object> map = doctorVideoMapper.answerOrderId(orderid);
        long orderId = ModelUtil.getInt(map, "orderid");
        int doctorId = ModelUtil.getInt(map, "doctorid");
        int dplatform = ModelUtil.getInt(map, "dplatform");
        String dToken = ModelUtil.getStr(map, "dtoken");
        long userid = ModelUtil.getLong(map, "userid");

        Map<String, Object> docnamemap = doctorVideoMapper.docName(doctorId);
        String docname = ModelUtil.getStr(docnamemap, "name");

        doctorVideoMapper.addPushApp(TextFixed.messageServiceTitle,
                String.format(TextFixed.answerDoctorSummaryText, docname),
                TypeNameAppPushEnum.AnswerDiagnosisGuidance.getCode(), String.valueOf(orderId), (int) userid, MessageTypeEnum.user.getCode(), dplatform, dToken); //app用户push消息

        doctorVideoMapper.addMessage("", TextFixed.messageServiceTitle,
                MessageTypeEnum.user.getCode(), String.valueOf(orderId),
                TypeNameAppPushEnum.AnswerDiagnosisGuidance.getCode(), (int) userid, String.format(TextFixed.answerDoctorSummarySystemText, docname), "");//app 用户 内推送
    }


    /**
     * 电话订单医生完成诊后指导给用户推送
     *
     * @param orderid
     */
    public void phoneOrderGuidancePush(long orderid) {
        Map<String, Object> map = doctorVideoMapper.phoneOrderId(orderid);
        long orderId = ModelUtil.getInt(map, "orderid");
        int doctorId = ModelUtil.getInt(map, "doctorid");
        int dplatform = ModelUtil.getInt(map, "dplatform");
        String dToken = ModelUtil.getStr(map, "dtoken");
        long userid = ModelUtil.getLong(map, "userid");

        Map<String, Object> docnamemap = doctorVideoMapper.docName(doctorId);
        String docname = ModelUtil.getStr(docnamemap, "name");

        doctorVideoMapper.addPushApp(TextFixed.messageServiceTitle,
                String.format(TextFixed.phoneDoctorSummaryText, docname),
                TypeNameAppPushEnum.PhoneDiagnosisGuidance.getCode(), String.valueOf(orderId), (int) userid, MessageTypeEnum.user.getCode(), dplatform, dToken); //app用户push消息

        doctorVideoMapper.addMessage("", TextFixed.messageServiceTitle,
                MessageTypeEnum.user.getCode(), String.valueOf(orderId),
                TypeNameAppPushEnum.PhoneDiagnosisGuidance.getCode(), (int) userid, String.format(TextFixed.phoneDoctorSummarySystemText, docname), "");//app 用户 内推送
    }

    /**
     * 视频订单医生完成诊后指导给用户推送
     *
     * @param orderid
     */
    public void videoOrderGuidancePush(long orderid) {
        Map<String, Object> map = doctorVideoMapper.videoOrderId(orderid);
        long orderId = ModelUtil.getInt(map, "orderid");
        int doctorId = ModelUtil.getInt(map, "doctorid");
        int dplatform = ModelUtil.getInt(map, "dplatform");
        String dToken = ModelUtil.getStr(map, "dtoken");
        long userid = ModelUtil.getLong(map, "userid");

        Map<String, Object> docnamemap = doctorVideoMapper.docName(doctorId);
        String docname = ModelUtil.getStr(docnamemap, "name");

        doctorVideoMapper.addPushApp(TextFixed.messageServiceTitle,
                String.format(TextFixed.videoDoctorSummaryText, docname),
                TypeNameAppPushEnum.VideoDiagnosisGuidance.getCode(), String.valueOf(orderId), (int) userid, MessageTypeEnum.user.getCode(), dplatform, dToken); //app用户push消息

        doctorVideoMapper.addMessage("", TextFixed.messageServiceTitle,
                MessageTypeEnum.user.getCode(), String.valueOf(orderId),
                TypeNameAppPushEnum.VideoDiagnosisGuidance.getCode(), (int) userid, String.format(TextFixed.videoDoctorSummarySystemText, docname), "");//app 用户 内推送
    }

}


