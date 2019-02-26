package com.syhdoctor.webtask.video.Mapper;

import com.syhdoctor.common.utils.EnumUtils.PayStateEnum;
import com.syhdoctor.common.utils.EnumUtils.VideoOrderStateEnum;
import com.syhdoctor.webtask.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class RecordingVideoMapper extends BaseMapper {

    public List<Map<String, Object>> getPaidVideoOrderList() {
        String sql = "  select dvo.id, " +
                "       dvo.orderno, " +
                "       dvo.subscribe_time     subscribetime, " +
                "       dvo.subscribe_end_time/1000 subscribeendtime, " +
                "       ua.userno, " +
                "       di.in_doc_code         doctorno " +
                "from doctor_video_order dvo " +
                "       left join user_account ua on dvo.userid = ua.id and ifnull(ua.delflag, 0) = 0 " +
                "       left join doctor_info di on dvo.doctorid = di.doctorid and ifnull(di.delflag, 0) = 0 " +
                "where dvo.subscribe_time <= unix_timestamp() * 1000 " +
                "  and dvo.status = ? " +
                "  and dvo.paystatus = ? ";
        List<Object> params = new ArrayList<>();
        params.add(VideoOrderStateEnum.Paid.getCode());
        params.add(PayStateEnum.Paid.getCode());
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> startRecordingOrderList() {
        String sql = "  select id,nativeHandle,userinto,doctorinto " +
                "from doctor_video_order " +
                "where status = ? " +
                "  and ifnull(nativeHandle, 0) != 0 " +
                "  and recording_status = 0  ";
        return queryForList(sql, VideoOrderStateEnum.InCall.getCode());
    }

    public List<Map<String, Object>> stopRecordingOrderList() {
        String sql = "  select id,nativeHandle,userinto,doctorinto " +
                "from doctor_video_order " +
                "where status != ? " +
                "  and ifnull(nativeHandle, 0) != 0 " +
                "  and recording_status = 1  ";
        return queryForList(sql, VideoOrderStateEnum.InCall.getCode());
    }

    public List<Map<String, Object>> getProperties() {
        String sql = "  select id,nativeHandle,userinto,doctorinto " +
                "from doctor_video_order " +
                "where status = ? " +
                "  and ispull=0 " +
                "  and ( userinto > 0 or doctorinto >0 ) " +
                "  and ifnull(nativeHandle, 0) != 0 " +
                "  and recording_status = 1  ";
        return queryForList(sql, VideoOrderStateEnum.InCall.getCode());
    }

    public void updateOrderStatus(long id) {
        String sql = " update doctor_video_order set status=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(VideoOrderStateEnum.InCall.getCode());
        params.add(id);
        update(sql, params);
    }

    public void updateOrderUserToken(long id, String token, int useruid) {
        String sql = " update doctor_video_order set token=?,useruid=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(token);
        params.add(useruid);
        params.add(id);
        update(sql, params);
    }

    public void updateOrderDoctorToken(long id, String token, int doctoruid) {
        String sql = " update doctor_video_order set doctor_token=?,doctoruid=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(token);
        params.add(doctoruid);
        params.add(id);
        update(sql, params);
    }

    public void updateOrdernNativeHandle(long nativeHandle, long orderId) {
        String sql = " update doctor_video_order set nativeHandle=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(nativeHandle);
        params.add(orderId);
        update(sql, params);
    }

    public Map<String, Object> getOrderNativeHandle(long orderId) {
        String sql = " select nativeHandle from doctor_video_order  where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        return queryForMap(sql, params);
    }

    public boolean updateOrderRecordingStatus(long orderId, int status) {
        String sql = " update doctor_video_order set recording_status=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(status);
        params.add(orderId);
        return update(sql, params) > 0;
    }

    public boolean updateOrderPullStatus(long orderId, String url) {
        String sql = " update doctor_video_order set ispull=?,record_url=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(1);
        params.add(url);
        params.add(orderId);
        return update(sql, params) > 0;
    }
}
