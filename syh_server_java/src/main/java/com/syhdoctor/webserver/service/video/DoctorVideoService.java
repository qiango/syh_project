package com.syhdoctor.webserver.service.video;

import com.syhdoctor.common.utils.EnumUtils.VideoOrderStateEnum;
import com.syhdoctor.common.utils.EnumUtils.VideoTipTypeEnum;
import com.syhdoctor.common.utils.JsonUtil;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.common.utils.http.HttpParamModel;
import com.syhdoctor.common.utils.http.HttpUtil;
import com.syhdoctor.webserver.config.ConfigModel;
import com.syhdoctor.webserver.exception.ServiceException;
import com.syhdoctor.webserver.mapper.video.DoctorVideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DoctorVideoService extends VideoBaseService {

    @Autowired
    private DoctorVideoMapper doctorVideoMapper;

    public List<Map<String, Object>> appDoctorVideoOrderList(long doctorid, int status, int intotype, int pageindex, int pagesize) {
        return doctorVideoMapper.appDoctorVideoOrderList(doctorid, status, intotype, pageindex, pagesize);
    }

    public boolean closeOrder(long orderid) {
        Map<String, Object> map = new HashMap<>();
        map.put("orderid", orderid);
        map.put("tiptype", VideoTipTypeEnum.DoctorClose.getCode());
        map.put("tipmessage", VideoTipTypeEnum.DoctorClose.getMessage());
        Map<String, Object> videoOrder = doctorVideoMapper.getVideoOrder(orderid);
        String json = String.format("%s|%s", JsonUtil.getInstance().toJson(map), ModelUtil.getStr(videoOrder, "useruid"));
        HttpParamModel httpParamModel = new HttpParamModel();
        httpParamModel.add("json", json);
        HttpUtil.getInstance().post(ConfigModel.WEBSOCKETLINKURL + "websocket/videoPushData", httpParamModel);
        return true;
    }

    public Map<String, Object> doctorIntoVideo(long orderId, String doctordevicecode) {
        Map<String, Object> videoOrder = doctorVideoMapper.getVideoOrder(orderId);
        int status = ModelUtil.getInt(videoOrder, "status");
        if (status != VideoOrderStateEnum.InCall.getCode()) {
            throw new ServiceException("该状态不能进入直播");
        }

        if (UnixUtil.getNowTimeStamp() > ModelUtil.getLong(videoOrder, "subscribeendtime")) {
            throw new ServiceException("直播已经结束");
        }

        String doctordevicecode1 = ModelUtil.getStr(videoOrder, "doctordevicecode");
        if (StrUtil.isEmpty(doctordevicecode1)) {
            doctorVideoMapper.updateVideoDoctorinto(orderId, doctordevicecode);
        } else if (!doctordevicecode.equals(doctordevicecode1)) {
            throw new ServiceException("已有设备进入");
        }

        if (videoOrder != null) {
            videoOrder.put("currenttime", UnixUtil.getNowTimeStamp());
        }

        return videoOrder;
    }
}
