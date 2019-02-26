package com.syhdoctor.webtask.rechargeable.mapper;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webtask.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class RechargeableMapper extends BaseMapper {


    /**
     *生成充值卡
     * @author qian.wang
     * @date 2018/10/23
     * @param  * @param
     * @return void
     */
    public void addRechargeableDetail() {
        String sqlLot = "select id,total,channel_prefix from rechargeable_card_lotnumber where createcardstatus=0 and create_time<?";
        long time=UnixUtil.getNowTimeStamp()-6*50000;
        List<Map<String, Object>> list = queryForList(sqlLot,time);

        for (Map<String, Object> map : list) {
            long id =ModelUtil.getLong(map,"id");
            long total =ModelUtil.getLong(map,"total");
            String channel = ModelUtil.getStr(map,"channel_prefix");
            String sqls = "select count(id) count from rechargeable_card where ";
            List<Object> paramsDetail = new ArrayList<>();
            sqls += " lotnumberid = ? ";
            paramsDetail.add(id);
            long count = jdbcTemplate.queryForObject(sqls, paramsDetail.toArray(), long.class);
            long realNumber = total - count;
            if (realNumber != 0) {
                String sqlDetail = " insert into rechargeable_card (lotnumberid, " +
                        "                                  redeemcode, " +
                        "                                  useflag, " +
                        "                                  create_time) " +
                        "values (?, ?, ?, ?) ";
                for (int i = 0; i < realNumber; i++) {
                    List<Object> paramsDetails = new ArrayList<>();
                    paramsDetails.add(id);
                    String value = UnixUtil.generateString(6);
                    paramsDetails.add(((channel==null?"":channel) + value).toLowerCase());
                    paramsDetails.add(0);
                    paramsDetails.add(UnixUtil.getNowTimeStamp());
                    insert(sqlDetail, paramsDetails);
                }
            }
            String updateSql = "update rechargeable_card_lotnumber set createcardstatus=1 where id=?";
            List<Object> params = new ArrayList<>();
            params.add(id);
            update(updateSql, params);
        }
    }

}
