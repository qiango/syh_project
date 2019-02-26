package com.syhdoctor.webserver.service.answer;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.mapper.answer.AnswerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PhoneService extends AnswerBaseService{

    @Autowired
    private AnswerMapper answerMapper;


    public List<Map<String, Object>> getDoctorPhoneSchedulingList(long doctorId) {
        List<Map<String, Object>> doctorSchedulingList = answerMapper.getDoctorPhoneSchedulingList(doctorId);
        List<Map<String, Object>> tempList = new ArrayList<>();
        List<Map<String, Object>> resultList = new ArrayList<>();
        String key = "";
        for (Map<String, Object> map : doctorSchedulingList) {
            String daytime = ModelUtil.getStr(map, "daytime");
            if (!key.equals(daytime)) {
                key = daytime;
                Map<String, Object> dayObj = new HashMap<>();
                dayObj.put("daytime", daytime);
                tempList = new ArrayList<>();
                Map<String, Object> contentObj = new HashMap<>();
                contentObj.put("id", ModelUtil.getLong(map, "id"));
                contentObj.put("startendtime", ModelUtil.getStr(map, "starttime"));
                contentObj.put("issubscribe", ModelUtil.getInt(map, "issubscribe"));
                tempList.add(contentObj);
                dayObj.put("timelist", tempList);
                resultList.add(dayObj);
            } else {
                Map<String, Object> contentObj = new HashMap<>();
                contentObj.put("id", ModelUtil.getLong(map, "id"));
                contentObj.put("startendtime", ModelUtil.getStr(map, "starttime"));
                contentObj.put("issubscribe", ModelUtil.getInt(map, "issubscribe"));
                tempList.add(contentObj);
            }
        }
        return resultList;
    }
}
