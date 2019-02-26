package com.syhdoctor.webserver.mapper.wallet;

import com.syhdoctor.common.utils.EnumUtils.ExtractOrderStateEnum;
import com.syhdoctor.common.utils.EnumUtils.MoneyTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.TransactionTypeStateEnum;
import com.syhdoctor.common.utils.EnumUtils.VisitCategoryEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DoctorWalletBaseMapper extends BaseMapper {

    public boolean updateDoctorWallet(long doctorId, BigDecimal walletbalance) {
        String sql = " update doctor_extends set walletbalance=? where doctorid=? ";
        List<Object> params = new ArrayList<>();
        params.add(PriceUtil.addPrice(walletbalance));
        params.add(doctorId);
        return update(sql, params) > 0;
    }

    public Map<String, Object> getDoctorWallet(long doctorId) {
        String sql = " select di.doctorid, di.doo_tel phone, de.walletbalance " +
                "from doctor_info di " +
                "       left join doctor_extends de on di.doctorid = de.doctorid and ifnull(de.delflag, 0) = 0 " +
                "where di.doctorid = ? ";
        Map<String, Object> data = queryForMap(sql, doctorId);
        if (data != null) {
            data.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(data, "walletbalance")));
        }
        return data;
    }

    public boolean addDoctorTransactionRecord(String orderno, TransactionTypeStateEnum transactionTypeStateEnum, MoneyTypeEnum moneyTypeEnum, long doctorid, String account, BigDecimal transactionmoney, String cardnumber, BigDecimal walletbalance) {
        String sql = " insert into doctor_transaction_record (orderno, " +
                "                                transactiontype, " +
                "                                moneyflag, " +
                "                                doctorid, " +
                "                                account, " +
                "                                transactionmoney, " +
                "                                cardnumber, " +
                "                                delflag, " +
                "                                walletbalance, " +
                "                                create_time) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(orderno);
        params.add(transactionTypeStateEnum.getCode());
        params.add(moneyTypeEnum.getCode());
        params.add(doctorid);
        params.add(account);
        params.add(PriceUtil.addPrice(transactionmoney));
        params.add(cardnumber);
        params.add(0);
        params.add(PriceUtil.addPrice(walletbalance));
        params.add(UnixUtil.getNowTimeStamp());
        return update(sql, params) > 0;
    }

    public long getAnswerOrderCount(long doctorId, VisitCategoryEnum visitCategoryEnum) {
        String sql = " select count(visitCategory) count " +
                "from doctor_problem_order " +
                "where ifnull(delflag, 0) = 0 " +
                "  and paystatus = 1 " +
                "  and states in (2, 4, 6) " +
                "  and doctorid = ? " +
                "  and from_unixtime(create_time / 1000, '%Y-%m') = date_format(now(), '%Y-%m') " +
                "  and visitCategory = ? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(visitCategoryEnum.getCode());
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public long getAnswerOrderCount(long doctorId, VisitCategoryEnum visitCategoryEnum, long begintime, long endtime) {
        String sql = " select count(visitCategory) count " +
                "from doctor_problem_order " +
                "where ifnull(delflag, 0) = 0 " +
                "  and paystatus = 1 " +
                "  and states = 4" + // in (2, 4, 6)
                "  and doctorid = ? " +
                "  and visitCategory = ? " +
                "  and create_time >= ? " +
                "  and create_time <= ? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(visitCategoryEnum.getCode());
        params.add(begintime);
        params.add(endtime);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public long getPhoneOrderCount(long doctorId, VisitCategoryEnum visitCategoryEnum) {
        String sql = " select count(visitCategory) count " +
                "from doctor_phone_order " +
                "where ifnull(delflag, 0) = 0 " +
                "  and paystatus = 1 " +
                "  and status in (2, 3, 4, 6) " +
                "  and doctorid = ? " +
                "  and from_unixtime(create_time / 1000, '%Y-%m') = date_format(now(), '%Y-%m') " +
                "  and visitCategory = ? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(visitCategoryEnum.getCode());
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public long getPhoneOrderCount(long doctorId, VisitCategoryEnum visitCategoryEnum, long begintime, long endtime) {
        String sql = " select count(visitCategory) count " +
                "from doctor_phone_order " +
                "where ifnull(delflag, 0) = 0 " +
                "  and paystatus = 1 " +
                "  and status =4 " + //in (2, 3, 4, 6)
                "  and doctorid = ? " +
                "  and visitCategory = ? " +
                "  and create_time >= ? " +
                "  and create_time <= ? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(visitCategoryEnum.getCode());
        params.add(begintime);
        params.add(endtime);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public long getVideoOrderCount(long doctorId, VisitCategoryEnum visitCategoryEnum, long begintime, long endtime) {
        String sql = " select count(visitCategory) count " +
                "from doctor_video_order " +
                "where ifnull(delflag, 0) = 0 " +
                "  and paystatus = 1 " +
                "  and status =4 " + //in (2, 3, 4)
                "  and doctorid = ? " +
                "  and visitCategory = ? " +
                "  and create_time >= ? " +
                "  and create_time <= ? ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        params.add(visitCategoryEnum.getCode());
        params.add(begintime);
        params.add(endtime);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 获取银行卡列表
     *
     * @param doctorid id
     * @return
     */
    public long getBankCardCount(long doctorid) {
        String sql = " SELECT count(id) count FROM doctor_bank_card WHERE doctorid=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    public List<Map<String, Object>> transactionRecordList(long doctorId, int pageIndex, int pageSize) {
        return transactionRecordList(doctorId, null, 0, 0, pageIndex, pageSize);
    }

    public List<Map<String, Object>> transactionRecordList(long doctorId, MoneyTypeEnum value, long begintime, long endtime, int pageIndex, int pageSize) {
        String sql = " select dt.id,dt.transactiontype,right(dt.cardnumber, 4) num,dt.transactionmoney,dt.moneyflag,dt.create_time createtime,dt.cardnumber,bp.picture from doctor_transaction_record dt left join doctor_extract_order de on dt.orderno=de.orderno  left join bank_pic bp on de.bankcode = bp.bankcode and ifnull(bp.delflag, 0) = 0   where dt.doctorid=? " +
                "and ifnull(dt.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        if (value != null && !value.equals(MoneyTypeEnum.All)) {
            sql += " and dt.moneyflag=? ";
            params.add(value.getCode());
        }
        if (begintime > 0) {
            sql += " and dt.create_time >= ?";
            params.add(begintime);
        }
        if (endtime > 0) {
            sql += " and dt.create_time <= ?";
            params.add(endtime);
        }
        return query(pageSql(sql, " order by dt.id desc "), pageParams(params, pageIndex, pageSize), (ResultSet rs, int num) -> {
            Map<String, Object> data = resultToMap(rs);
            if (data != null) {
                int type = ModelUtil.getInt(data, "transactiontype");
                data.put("transactionmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "transactionmoney")));
                data.put("transactiontypename", TransactionTypeStateEnum.getValue(ModelUtil.getInt(data, "transactiontype")).getMessage());
                if (type == 10) {
                    String n = ModelUtil.getStr(data, "num");
                    String fa = n == null ? "" : n;
                    data.put("transactiontypename", TransactionTypeStateEnum.getValue(ModelUtil.getInt(data, "transactiontype")).getMessage() + "(尾号" + fa + ")");
                }

            }
            return data;
        });
    }

    public Map<String, Object> getTransactionRecord(long id) {
        String sql = " select id,orderno,account,transactiontype,transactionmoney,moneyflag,create_time createtime from doctor_transaction_record where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        Map<String, Object> data = queryForMap(sql, params);
        if (data != null) {
            data.put("transactionmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "transactionmoney")));
            data.put("transactiontypename", TransactionTypeStateEnum.getValue(ModelUtil.getInt(data, "transactiontype")).getMessage());
        }
        return data;
    }

    public Map<String, Object> getBankCardByDoctorId(long doctorId) {
        String sql = " select dbc.id cardid, dbc.bankname, bp.picture, dbc.number " +
                "from doctor_bank_card dbc " +
                "       left join bank_pic bp on dbc.bankcode = bp.bankcode " +
                "where ifnull(dbc.delflag, 0) = 0 " +
                "  and ifnull(bp.delflag, 0) = 0 " +
                "  and doctorid = ? ";
        return queryForMap(sql, doctorId);
    }

    public Map<String, Object> getBankCardById(long id) {
        String sql = " select id cardid,bankname,number,bankcode " +
                "from doctor_bank_card where ifnull(delflag, 0) = 0 " +
                "  and id = ? ";
        return queryForMap(sql, id);
    }

    public long addExtractOrder(String orderno, long cardId, String cardnumber, String bankname, String bankcode, BigDecimal amountmoney, long doctorId, String account) {
        String sql = " insert into doctor_extract_order (orderno, " +
                "                                  cardid, " +
                "                                  cardnumber, " +
                "                                  bankname, " +
                "                                  bankcode, " +
                "                                  amountmoney, " +
                "                                  doctorid, " +
                "                                  account, " +
                "                                  status, " +
                "                                  delflag, " +
                "                                  create_time) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(orderno);
        params.add(cardId);
        params.add(cardnumber);
        params.add(bankname);
        params.add(bankcode);
        params.add(PriceUtil.addPrice(amountmoney));
        params.add(doctorId);
        params.add(account);
        params.add(ExtractOrderStateEnum.Submit.getCode());
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params, "id");
    }


    public Map<String, Object> getPhoneOrderUser(String orderno) {
        String sql = " select dpo.id orderid, ua.id userid, ua.name, ua.headpic, ua.phone, ua.delflag " +
                "from doctor_phone_order dpo " +
                "       left join user_account ua on dpo.userid = ua.id " +
                "where dpo.order_no = ? ";
        return queryForMap(sql, orderno);
    }

    public Map<String, Object> getAnswerOrderUser(String orderno) {
        String sql = " select dpo.id orderid, ua.id userid, ua.name, ua.headpic, ua.phone, ua.delflag " +
                "from doctor_problem_order dpo " +
                "       left join user_account ua on dpo.userid = ua.id " +
                "where dpo.orderno = ? ";
        return queryForMap(sql, orderno);
    }

    public Map<String, Object> getExtractOrder(String orderno) {
        String sql = " select id,amountmoney,failreason reason,status,create_time createtime,examine_time examinetime,pay_time paytime,orderno,account,cardnumber from doctor_extract_order where orderno=? ";
        Map<String, Object> map = queryForMap(sql, orderno);
        if (null != map) {
            map.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "amountmoney")));
        }
        return map;
    }

    public Map<String, Object> getExtractOrder(long orderid) {
        String sql = " select id,amountmoney,failreason reason,status,create_time createtime,examine_time examinetime,pay_time paytime,orderno,account,cardnumber from doctor_extract_order where id=? ";
        Map<String, Object> map = queryForMap(sql, orderid);
        if (null != map) {
            map.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "amountmoney")));
        }
        return map;
    }

    /**
     * 常见病症
     *
     * @return
     */
    public List<Map<String, Object>> getAnswerDiseaseList(long orderId) {
        String sql = " select id,diseasename value from middle_answer_disease where orderid=? and ifnull(delflag,0)=0 ";
        return queryForList(sql, orderId);
    }

    /**
     * 常见病症
     *
     * @return
     */
    public List<Map<String, Object>> getPhoneDiseaseList(long orderId) {
        String sql = " select id,diseasename value from middle_phone_disease where orderid=? and ifnull(delflag,0)=0 ";
        return queryForList(sql, orderId);
    }

    public BigDecimal getTransactionRecordSum(long doctorId, MoneyTypeEnum moneyTypeEnum, long begintime, long endtime) {
        String sql = " select sum(transactionmoney) price from doctor_transaction_record WHERE doctorid=? and ifnull(delflag,0)=0";
        List<Object> params = new ArrayList<>();
        params.add(doctorId);
        if (moneyTypeEnum != null && !moneyTypeEnum.equals(MoneyTypeEnum.All)) {
            sql += " and moneyflag=? ";
            params.add(moneyTypeEnum.getCode());
        }
        if (begintime != 0) {
            sql += " and create_time >= ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and create_time <= ? ";
            params.add(endtime);
        }
        Long price = jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
        return PriceUtil.findPrice(price);
    }
}
