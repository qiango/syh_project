package com.syhdoctor.webserver.mapper.doctor;

import com.syhdoctor.common.utils.EnumUtils.PhoneOrderStateEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.UnixUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class DoctorPhoneMapper extends DoctorBaseMapper {


    /**
     * 订单详情
     *
     * @param orderId 订单ID
     * @return
     */
    public Map<String, Object> getPhoneOrderById(long orderId) {

        String sql = "select dpo.id,dpo.doctorid, order_no as orderno,user_phone userphone,dpo.paytype,dpo.visitcategory,dpo.originalprice,dpo.actualmoney, dpo.create_time as createtime, status,b.name as statename,dpo.diagnosis,dpo.result_time as resulttime  " +
                "from doctor_phone_order dpo  " +
                "       left join basics b on dpo.status=b.customid and type=5 where dpo.id=?";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        Map<String, Object> data = queryForMap(sql, params);
        if (data != null) {
            data.put("actualmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "actualmoney")));
            data.put("originalprice", PriceUtil.findPrice(ModelUtil.getLong(data, "originalprice")));
        }
        return data;
    }

    public Map<String, Object> getPhoneOrderDetail(long id) {
        String sql = "select dpo.id, " +
                "       dpo.order_no       orderno, " +
                "       dpo.paytype, " +
                "       dpo.status, " +
                "       dpo.actualmoney, " +
                "       dpo.diagnosis, " +
                "       dpo.subscribe_time subscribetime, " +
                "       dpo.userid, " +
                "       dpo.create_time    createtime, " +
                "       b.name diseasetime, " +
                "       dpo.gohospital, " +
                "       dpo.issuredis, " +
                "       dpo.dis_describe disdescribe " +
                "from doctor_phone_order dpo left join basics b on dpo.disease_time=b.customid and b.type=25 " +
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


    public Map<String, Object> getUserInfo(long orderid) {
        String sql = "select ua.id,ua.name,ua.gender,ua.age,ua.headpic from doctor_phone_order dp  left join user_account ua on dp.userid=ua.id where dp.id=?";
        Map<String, Object> map = queryForMap(sql, orderid);
        if (map != null) {
            map.put("gender", ModelUtil.getInt(map, "gender") == 1 ? "男" : "女");
        }
        return map;
    }

    /**
     * 病症
     *
     * @param orderId 订单ID
     * @return
     */
    public List<Map<String, Object>> getPhoneDisease(long orderId) {
        String sql = "select id, diseasename value " +
                "from middle_phone_disease " +
                "where orderid = ? ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        return queryForList(sql, params);
    }

    /**
     * 保存诊疗结果
     *
     * @param orderId
     * @param diagnosis
     * @return
     */
    public boolean updatePhoneOrder(long orderId, String diagnosis) {
        String sql = "update doctor_phone_order set diagnosis=?,result_time=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(diagnosis);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(orderId);
        return update(sql, params) > 0;
    }

    /**
     * 急诊状态和录音文件
     *
     * @param orderId
     * @return
     */
    public boolean updatePhoneOrder(long orderId, int status, String filePath) {
        String sql = "update doctor_phone_order set status=?,record_url=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(status);
        params.add(filePath);
        params.add(orderId);
        return update(sql, params) > 0;
    }


    //修改急诊订单状态
    public boolean updatePhoneOrderStatus(long orderId, int status) {
        String sql = "update doctor_phone_order set status=?,modify_time=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(status);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(orderId);
        return update(sql, params) > 0;
    }

    //修改急诊订单电话状态
    public boolean updatePhoneOrderPhoneStatus(long orderId, int state) {
        String sql = "update doctor_phone_order set phonestatus=?,modify_time=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(state);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(orderId);
        return update(sql, params) > 0;
    }

    //添加急诊记录
    public boolean addPhoneOrderRecord(long orderId, int type, String message, String filePath) {
        String sql = " insert into doctor_phone_order_record (orderid, type, message, filepath,delflag, create_time) " +
                "values (?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        params.add(type);
        params.add(message);
        params.add(filePath);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params) > 0;
    }
}
