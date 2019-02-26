package com.syhdoctor.webserver.service.doctor;

import com.syhdoctor.common.utils.EnumUtils.OrderTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.VisitCategoryEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.controller.webapp.appapi.user.answer.util.DoctorPrice;
import com.syhdoctor.webserver.mapper.doctor.DoctorInfoMapper;
import com.syhdoctor.webserver.service.answer.AnswerService;
import com.syhdoctor.webserver.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DoctorInfoService extends DoctorBaseService {

    @Autowired
    private DoctorInfoMapper doctorInfoMapper;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;

    @Autowired
    private DoctorPrice doctorPrice;

    /**
     * 查询医生列表
     *
     * @param departmentId 科室ID
     * @param recommend    是否推荐
     * @param pageIndex    分页
     * @param pageSize     分页
     * @return
     */
    public List<Map<String, Object>> getDoctorOnDutyList(int departmentId, int recommend, int pageIndex, int pageSize) {
        return doctorInfoMapper.getDoctorOnDutyList(departmentId, recommend, pageIndex, pageSize);
    }

    public List<Map<String, Object>> getDoctorEvaluateList(long doctorId, int pageIndex, int pageSize) {
        List<Map<String, Object>> doctorEvaluateList = doctorInfoMapper.getDoctorEvaluateList(doctorId, pageIndex, pageSize);
        doctorEvaluateList(doctorEvaluateList);
        return doctorEvaluateList;
    }

    /**
     * 医生主页
     *
     * @param doctorId
     * @return
     */
    public Map<String, Object> getDoctorHomePageNew(long doctorId, long userId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> value = doctorInfoMapper.getDoctorHomePage(doctorId);
        if (value != null) {
            Map<String, Object> vip = userService.isVip(userId);
            Map<String, Object> graphic = this.doctorPrice.setPriceType(VisitCategoryEnum.graphic)
                    .setUserId(userId)
                    .setDoctorId(doctorId)
                    .show().result();

            Map<String, Object> phone = this.doctorPrice.setPriceType(VisitCategoryEnum.phone)
                    .setUserId(userId)
                    .setDoctorId(doctorId)
                    .show().result();

            Map<String, Object> video = this.doctorPrice.setPriceType(VisitCategoryEnum.video)
                    .setUserId(userId)
                    .setDoctorId(doctorId)
                    .show().result();
            value.put("graphic", graphic);
            value.put("phone", phone);
            value.put("video", video);
            value.put("uservip", vip);
        }
        result.put("doctor", value);
        boolean flag = userService.verifyUser(userId);//信息是否完善
        result.put("isinformation", flag ? 1 : 0);
        List<Map<String, Object>> doctorEvaluateList = doctorInfoMapper.getDoctorEvaluateList(doctorId);
        doctorEvaluateList(doctorEvaluateList);
        result.put("doctorEvaluateList", doctorEvaluateList);
        return result;
    }

    private void doctorEvaluateList(List<Map<String, Object>> doctorEvaluateList) {
        List<Long> answerIds = new ArrayList<>();
        List<Long> phoneIds = new ArrayList<>();
        List<Long> videoIds = new ArrayList<>();
        for (Map<String, Object> map : doctorEvaluateList) {
            long orderid = ModelUtil.getLong(map, "orderid");
            switch (OrderTypeEnum.getValue(ModelUtil.getInt(map, "ordertype"))) {
                case Answer:
                    answerIds.add(orderid);
                    break;
                case Phone:
                    phoneIds.add(orderid);
                    break;
                case Video:
                    videoIds.add(orderid);
                    break;
                default:
                    break;
            }
        }

        if (answerIds.size() > 0) {
            List<Map<String, Object>> orderDiseaseList = doctorInfoMapper.orderAnswerDiseaseList(answerIds);
            initList(doctorEvaluateList, orderDiseaseList, OrderTypeEnum.Answer.getCode());
        }

        if (phoneIds.size() > 0) {
            List<Map<String, Object>> orderDiseaseList = doctorInfoMapper.orderPhoneDiseaseList(phoneIds);
            initList(doctorEvaluateList, orderDiseaseList, OrderTypeEnum.Phone.getCode());
        }

        if (videoIds.size() > 0) {
            List<Map<String, Object>> orderDiseaseList = doctorInfoMapper.orderVideoDiseaseList(videoIds);
            initList(doctorEvaluateList, orderDiseaseList, OrderTypeEnum.Video.getCode());
        }
    }


    private void initList(List<Map<String, Object>> orderList, List<Map<String, Object>> diseaseList, int ordertype) {
        List<Map<String, Object>> tempList = new ArrayList<>();
        Map<Long, Object> tempProblem = new HashMap<>();
        long tempId = 0;

        for (Map<String, Object> obj : diseaseList) {
            Long orderid = ModelUtil.getLong(obj, "orderid");
            if (orderid > 0) {
                if (orderid != tempId) {
                    tempId = orderid;
                    tempList = new ArrayList<>();
                    Map<String, Object> contentObj = new HashMap<>();
                    contentObj.put("value", ModelUtil.getStr(obj, "value"));
                    tempList.add(contentObj);

                    tempProblem.put(orderid, tempList);
                } else {
                    Map<String, Object> contentObj = new HashMap<>();
                    contentObj.put("value", ModelUtil.getStr(obj, "value"));
                    tempList.add(contentObj);
                }
            }
        }

        for (Map<String, Object> map : orderList) {
            if (ModelUtil.getInt(map, "ordertype") == ordertype) {
                map.put("diseaselist", tempProblem.get(ModelUtil.getLong(map, "orderid")));
            }
        }
    }


    /**
     * ordertype 1：图文。2：电话，3：视频
     */

    public Map<String, Object> getDoctorSymptomslist(long userId, long doctorId, int ordertype) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        boolean flag = userService.verifyUser(userId);//信息是否完善
        if (flag) {
            Map<String, Object> userOrder = null;
            if (ordertype == OrderTypeEnum.Answer.getCode()) {
                userOrder = answerService.getUserAnswerOrder(userId, doctorId);
            } else if (ordertype == OrderTypeEnum.Phone.getCode()) {
                userOrder = answerService.getUserDoctorPhoneOrder(userId, doctorId);
            }/* else if (ordertype == OrderTypeEnum.Video.getCode()) {
                userOrder = answerService.getUserVideoOrder(userId);
            }*/
            //是否存在未结束订单
            if (userOrder != null) {
                map.put("status", 1);
                map.put("orderid", ModelUtil.getLong(userOrder, "id"));
                map.put("userid", ModelUtil.getLong(userOrder, "userid"));
                map.put("doctorid", ModelUtil.getLong(userOrder, "doctorid"));
                map.put("orderno", ModelUtil.getStr(userOrder, "orderno"));
            } else {
                map.put("status", 0);
            }

        }
        map.put("isinformation", flag ? 1 : 0);
        result.put("symptomstypelist", answerService.getAppSymptomsTypeListByDoctorId(doctorId));
        result.put("answerbean", map);
        return result;
    }
}
