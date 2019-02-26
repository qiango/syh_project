package com.syhdoctor.webtask.doctorduty.service;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webtask.base.service.BaseService;
import com.syhdoctor.webtask.doctorduty.mapper.DoctorDutyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DoctorDutyService extends BaseService {

    @Autowired
    private DoctorDutyMapper doctorDutyMapper;

    public void getDoctorDuty() {
        List<Map<String, Object>> mpList = doctorDutyMapper.getDoctorDuty();
        for (Map<String, Object> map : mpList) {
            long doctorId = ModelUtil.getLong(map, "doctorid", 0);
            String startTime = ModelUtil.getStr(map, "starttime", "starttime");
            String endTime = ModelUtil.getStr(map, "endtime", "endtime");
            Map<Integer, Map<String, Long>> mapMap = UnixUtil.weekDays(new Date(), startTime, endTime);
            for (int i = 0; i < 7; i++) {
                boolean value = ModelUtil.getBoolean(map, getWeekName(i), false);
                if (value) {
                    Map<String, Long> mapLong = mapMap.get(i);
                    long visitingStartTime = ModelUtil.getLong(mapLong, "starttime");
                    long visitingEndTime = ModelUtil.getLong(mapLong, "endtime");
                    doctorDutyMapper.addDoctorInquiry(doctorId, visitingStartTime, visitingEndTime);
                }
            }
        }
    }

    /**
     * @return
     */
    public void isOnDuty() {
        long count = doctorDutyMapper.isOnDuty();
        if (count == 0) {
            try {
                //emailService.sendSimpleMail("heng.tao@syhdoctor.com", "78888", "12323213");
            } catch (Exception e) {

            }
        }

    }

    private String getWeekName(int i) {
        switch (i) {
            case 0:
                return "sunday";
            case 1:
                return "monday";
            case 2:
                return "tuesday";
            case 3:
                return "wednesday";
            case 4:
                return "thursday";
            case 5:
                return "friday";
            case 6:
                return "saturday";
            default:
                return "";
        }

    }


}
