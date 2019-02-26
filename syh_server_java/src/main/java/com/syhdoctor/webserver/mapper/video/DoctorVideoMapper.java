package com.syhdoctor.webserver.mapper.video;

import com.syhdoctor.common.utils.EnumUtils.VideoOrderStateEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.StrUtil;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class DoctorVideoMapper extends VideoBaseMapper {

    public List<Map<String, Object>> appDoctorVideoOrderList(long doctorid, int status, int intotype, int pageindex, int pagesize) {
        String sql = " select  dvo.id, " +
                "       dvo.orderno, " +
                "       dvo.userid, " +
                "       dvo.doctorid, " +
                "       dvo.status states, " +
                "       dvo.paystatus, " +
                "       dvo.paytype, " +
                "       dvo.actualmoney price, " +
                "       dvo.marketprice, " +
                "       dvo.originalprice, " +
                "       dvo.discount, " +
                "       dvo.dis_describe disdescribe, " +
                "       dvo.guidance diagnosis, " +
                "       dvo.create_time createtime, " +
                "       dvo.visitcategory, " +
                "       ua.headpic, " +
                "       ua.name " +
                "from doctor_video_order dvo" +
                " left join user_account ua on dvo.userid=ua.id and ifnull(ua.delflag,0)=0 " +
                "where dvo.doctorid = ? and ifnull(dvo.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        if (status > 0) {
            sql += " and dvo.status =?  ";
            params.add(status);
        } else if (intotype == 2) {
            sql += " and dvo.status in (2,3) ";
        } else {
            sql += "  and dvo.status in (2,3,4,5,8) ";
        }
        return query(pageSql(sql, " order by dvo.id desc "), pageParams(params, pageindex, pagesize), (ResultSet res, int num) -> {
            Map<String, Object> map = resultToMap(res);
            if (map != null) {
                BigDecimal price = PriceUtil.findPrice(ModelUtil.getLong(map, "price"));
                map.put("price", price);
                map.put("marketprice", PriceUtil.findPrice(ModelUtil.getLong(map, "marketprice")));
                map.put("originalprice", PriceUtil.findPrice(ModelUtil.getLong(map, "originalprice")));
                map.put("statesname", VideoOrderStateEnum.getValue(ModelUtil.getInt(map, "states")).getMessage());

                String guidance = ModelUtil.getStr(map, "diagnosis");
                int states = ModelUtil.getInt(map, "states");
                map.put("statesname", VideoOrderStateEnum.getValue(states).getMessage());

                //用户订单列表新加字段:
                //guidance:0:不显示,1:未诊断,2:已诊断
                //record:0:不显示,1:显示问诊记录按钮(针对图文,其他默认0)
                //contact:0:不显示,1:联系患者按钮(针对图文,其他默认0)
                map.put("guidance", 0);
                map.put("record", 0);
                map.put("contact", 0);
                map.put("refund", 0);
                map.put("isopen", 0);
                if (states == VideoOrderStateEnum.OrderSuccess.getCode()) {
                    if (StrUtil.isEmpty(guidance)) {
                        map.put("guidance", 1);
                    } else {
                        map.put("guidance", 2);
                    }
                } else if (states == VideoOrderStateEnum.WaitRefund.getCode()) {
                    map.put("states", VideoOrderStateEnum.OrderFail.getCode());
                    map.put("statesname", VideoOrderStateEnum.OrderFail.getMessage());
                } else if (states == VideoOrderStateEnum.InCall.getCode()) {
                    map.put("isopen", 1);
                }
            }
            return map;
        });
    }


    public boolean updateVideoDoctorinto(long orderId, String doctordevicecode) {
        String sql = " update doctor_video_order set doctorinto=1,doctor_device_code=? where id =? ";
        return update(sql, doctordevicecode, orderId) > 0;
    }

    public Map<String, Object> getBeingVideoOrder(long doctorid) {
        String sql = " select id,orderno,status,subscribe_time subscribetime,create_time createtime " +
                "from doctor_video_order " +
                "where status in (2,3) " +
                "  and paystatus = 1 " +
                "  and doctorid = ? " +
                "  order by subscribe_time " +
                "limit 1 ";
        return queryForMap(sql, doctorid);
    }
}
