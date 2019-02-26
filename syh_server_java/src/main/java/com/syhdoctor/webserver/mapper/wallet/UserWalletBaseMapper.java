package com.syhdoctor.webserver.mapper.wallet;

import com.syhdoctor.common.utils.EnumUtils.*;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class UserWalletBaseMapper extends BaseMapper {

    public Map<String, Object> getUserWallet(long userId) {
        String sql = " select  id, name, userno, phone, walletbalance from user_account where id=? ";
        Map<String, Object> data = queryForMap(sql, userId);
        if (data != null) {
            data.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(data, "walletbalance")));
        }
        return data;
    }

    public Map<String, Object> getUserOpenId(long userId, OpenTypeEnum openTypeEnum) {
        String sql = " select  id,userid,openid from user_openid where opentype=? and userid=? ";
        Map<String, Object> data = queryForMap(sql, openTypeEnum.getCode(), userId);
        if (data != null) {
            data.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(data, "walletbalance")));
        }
        return data;
    }

    public Map<String, Object> getRechargeableCard(String redeemcode) {
        String sql = " select rc.id, rc.useflag,rc.cardname,rcl.id lotid,rcl.amountmoney, rcl.lotnumber, effectivetype, begintime, endtime, principal " +
                "from rechargeable_card rc " +
                "       left join rechargeable_card_lotnumber rcl on rc.lotnumberid = rcl.id " +
                "where rc.redeemcode = ? " +
                "  and ifnull(rc.delflag, 0) = 0 " +
                "  and ifnull(rcl.delflag, 0) = 0 ";
        Map<String, Object> data = queryForMap(sql, redeemcode);
        if (data != null) {
            data.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "amountmoney")));
        }
        return data;
    }

    public boolean updateRechargeableCard(long id) {
        String sql = " update rechargeable_card set useflag=1 where id=? ";
        return update(sql, id) > 0;
    }

    public boolean updateNotusedcount(long id) {
        String sql = " update rechargeable_card_lotnumber set notusedcount=notusedcount-1 where id=? ";
        return update(sql, id) > 0;
    }

    public Map<String, Object> isOverdue(long lotid) {
        String sql = " select id " +
                "from rechargeable_card_lotnumber " +
                "where UNIX_TIMESTAMP() * 1000 > begintime && unix_timestamp() * 1000 < endtime " +
                "  and id = ? ";
        return queryForMap(sql, lotid);
    }

    public long addOrder(String orderno, BigDecimal amountmoney, long userId) {
        return addOrder(orderno, 0, amountmoney, userId, null, 0, 0, 0, null);
    }

    public long addOrder(String orderno, long cardId, BigDecimal amountmoney, long userId, String lotnumber, int effectivetype, long begintime, long endtime, String principal) {
        String sql = " insert into rechargeable_order (orderno, " +
                "                                cardid, " +
                "                                amountmoney, " +
                "                                userid, " +
                "                                status, " +
                "                                lotnumber, " +
                "                                effectivetype, " +
                "                                begintime, " +
                "                                endtime, " +
                "                                principal, " +
                "                                rechargeable_time, " +
                "                                role_type, " +
                "                                operate_mode, " +
                "                                delflag, " +
                "                                create_time) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(orderno);
        params.add(cardId);
        params.add(PriceUtil.addPrice(amountmoney));
        params.add(userId);
        params.add(RechargeableOrderStateEnum.UnPaid.getCode());
        params.add(lotnumber);
        params.add(effectivetype);
        params.add(begintime);
        params.add(endtime);
        params.add(principal);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(1);
        params.add(2);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        return insert(sql, params, "id");
    }

    public boolean updateOrderStatusSuccess(PayTypeEnum payTypeEnum, String orderNo) {
        String sql = " update rechargeable_order set paytype=?,status=?,paystatus=? where orderno=? ";
        List<Object> params = new ArrayList<>();
        params.add(payTypeEnum.getCode());
        params.add(RechargeableOrderStateEnum.Paid.getCode());
        params.add(PayStateEnum.Paid.getCode());
        params.add(orderNo);
        return update(sql, params) > 0;
    }

    public boolean updateUserWallet(long userId, BigDecimal walletbalance) {
        String sql = " update user_account set walletbalance=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(PriceUtil.addPrice(walletbalance));
        params.add(userId);
        return update(sql, params) > 0;
    }


    public boolean addUserTransactionRecord(String orderno, TransactionTypeStateEnum transactionTypeStateEnum, MoneyTypeEnum moneyTypeEnum, long userid, String account, BigDecimal transactionmoney, BigDecimal walletbalance) {
        String sql = " insert into user_transaction_record (orderno, " +
                "                                transactiontype, " +
                "                                moneyflag, " +
                "                                userid, " +
                "                                account, " +
                "                                transactionmoney, " +
                "                                refundflag, " +
                "                                delflag, " +
                "                                walletbalance, " +
                "                                create_time) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(orderno);
        params.add(transactionTypeStateEnum.getCode());
        params.add(moneyTypeEnum.getCode());
        params.add(userid);
        params.add(account);
        params.add(PriceUtil.addPrice(transactionmoney));
        params.add(0);
        params.add(0);
        params.add(PriceUtil.addPrice(walletbalance));
        params.add(UnixUtil.getNowTimeStamp());
        return update(sql, params) > 0;
    }

    public Map<String, Object> getRechargeableOrder(String orderno) {
        String sql = " select ro.id orderid,ro.orderno,ro.amountmoney,ro.status,ua.id userid, ua.walletbalance  " +
                "from rechargeable_order ro  " +
                "       left join user_account ua on ro.userid = ua.id  " +
                "where ro.orderno = ?  " +
                "  and ifnull(ro.delflag, 0) = 0  " +
                "  and ifnull(ua.delflag, 0) = 0 ";
        Map<String, Object> data = queryForMap(sql, orderno);
        if (data != null) {
            data.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "amountmoney")));
            data.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(data, "walletbalance")));
        }
        return data;
    }

    public Map<String, Object> getRechargeableOrder(long orderId) {
        String sql = " select paytype, amountmoney, status, paystatus, orderno, ua.phone, rc.redeemcode,ro.create_time createtime " +
                "from rechargeable_order ro " +
                "       left join user_account ua on ro.userid = ua.id " +
                "       left join rechargeable_card rc on ro.cardid = rc.id " +
                "where ro.id = ? ";
        Map<String, Object> data = queryForMap(sql, orderId);
        if (data != null) {
            data.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "amountmoney")));
        }
        return data;
    }

    public List<Map<String, Object>> transactionRecordList(MoneyTypeEnum moneyTypeEnum, long userId, int pageIndex, int pageSize) {
        String sql = " select id,transactiontype,transactionmoney,moneyflag,create_time createtime,refundflag from user_transaction_record where userid=? and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        if (!moneyTypeEnum.equals(MoneyTypeEnum.All)) {
            sql += " and moneyflag=? ";
            params.add(moneyTypeEnum.getCode());
        }
        return query(pageSql(sql, " order by id desc "), pageParams(params, pageIndex, pageSize), (ResultSet rs, int num) -> {
            Map<String, Object> data = resultToMap(rs);
            if (data != null) {
                data.put("transactionmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "transactionmoney")));
                data.put("transactiontypename", TransactionTypeStateEnum.getValue(ModelUtil.getInt(data, "transactiontype")).getMessage());
            }
            return data;
        });
    }

    public Map<String, Object> getTransactionRecord(long id) {
        String sql = " select id,orderno,account,transactiontype,transactionmoney,moneyflag,create_time createtime,refundflag from user_transaction_record where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        Map<String, Object> data = queryForMap(sql, params);
        if (data != null) {
            data.put("transactionmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "transactionmoney")));
            data.put("transactiontypename", TransactionTypeStateEnum.getValue(ModelUtil.getInt(data, "transactiontype")).getMessage());
        }
        return data;
    }

    public Map<String, Object> getPhoneOrderDoctor(String orderno) {
        String sql = " select  di.doctorid id, " +
                "       di.doc_name       name, " +
                "       doc_photo_url     headpic, " +
                "       di.work_inst_name hospital, " +
                "       cd.value          department, " +
                "       cdt.value         title " +
                "from doctor_phone_order dpo " +
                "       left join doctor_info di on dpo.doctorid = di.doctorid " +
                "       left join code_department cd on di.department_id = cd.id " +
                "       left join code_doctor_title cdt on di.title_id = cdt.id " +
                "       left join basics b on dpo.status = b.customid and type = 4 " +
                "where dpo.order_no = ? ";
        return queryForMap(sql, orderno);
    }

    public Map<String, Object> getAnswerOrderDoctor(String orderno) {
        String sql = " select  di.doctorid id, " +
                "       di.doc_name       name, " +
                "       doc_photo_url     headpic, " +
                "       di.work_inst_name hospital, " +
                "       cd.value          department, " +
                "       cdt.value         title " +
                "from doctor_problem_order dpo " +
                "       left join doctor_info di on dpo.doctorid = di.doctorid " +
                "       left join code_department cd on di.department_id = cd.id " +
                "       left join code_doctor_title cdt on di.title_id = cdt.id " +
                "       left join basics b on dpo.states = b.customid and type = 4 " +
                "where dpo.orderno = ? ";
        return queryForMap(sql, orderno);
    }

    public Map<String, Object> getVideoOrderDoctor(String orderno) {
        String sql = " select  di.doctorid id, " +
                "       di.doc_name       name, " +
                "       doc_photo_url     headpic, " +
                "       di.work_inst_name hospital, " +
                "       cd.value          department, " +
                "       cdt.value         title " +
                "from doctor_video_order dvo " +
                "       left join doctor_info di on dvo.doctorid = di.doctorid " +
                "       left join code_department cd on di.department_id = cd.id " +
                "       left join code_doctor_title cdt on di.title_id = cdt.id " +
                "where dvo.orderno = ? ";
        return queryForMap(sql, orderno);
    }
}
