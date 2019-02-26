package com.syhdoctor.webserver.mapper.user;

import com.syhdoctor.common.utils.EnumUtils.MoneyTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.PayTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.TransactionTypeStateEnum;
import com.syhdoctor.common.utils.EnumUtils.VisitCategoryEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class UserAccountMapper extends UserBaseMapper {

    /**
     * 用户账户管理查询
     *
     * @param userno
     * @param name
     * @param phone
     * @param begintime
     * @param endtime
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getUserAccountList(String userno, String name, String phone, long begintime, long endtime, int pageIndex, int pageSize) {
        String sql = " select ua.id id, ua.userno userno, ua.name name, ua.phone phone, ua.create_time createtime, ua.walletbalance walletbalance, ua.integral integral " +
                " from user_account ua " +
                " where ifnull(ua.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(userno)) {
            sql += " and ua.userno like ? ";
            params.add(String.format("%%%S%%", userno));
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and ua.phone like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        if (begintime != 0) {
            sql += " and ua.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and ua.create_time < ? ";
            params.add(endtime);
        }

        return query(pageSql(sql, " order by ua.id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("paytypename", PayTypeEnum.getValue(ModelUtil.getInt(map, "paytype")).getMessage());
                map.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(map, "walletbalance")));
            }
            return map;
        });

    }

    /**
     * 行数
     *
     * @param userno
     * @param name
     * @param phone
     * @param begintime
     * @param endtime
     * @return
     */
    public long getUserAccountCount(String userno, String name, String phone, long begintime, long endtime) {
        String sql = " select count(id) count  " +
                " from user_account ua  " +
                " where ifnull(ua.delflag, 0) = 0  ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(userno)) {
            sql += " and userno = ? ";
            params.add(userno);
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and name = ? ";
            params.add(name);
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and phone = ? ";
            params.add(phone);
        }
        if (begintime != 0) {
            sql += " and create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and create_time < ? ";
            params.add(endtime);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);

    }


    /**
     * 用户账户详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getUserAccountId(long id) {
        String sql = " Select userno, create_time createtime, name, phone, walletbalance, integral " +
                " from user_account ua " +
                " where id = ? " +
                "  and ifnull(ua.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        Map<String, Object> map = queryForMap(sql, params);
        if (map != null) {
            map.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(map, "walletbalance")));
        }
        return map;
    }

    /**
     * 用户账户详情列表
     *
     * @param userid
     * @param moneyflag
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getUserAccountListId(long userid, int moneyflag, int pageIndex, int pageSize) {
        String sql = " select utr.id, utr.moneyflag, utr.transactionmoney, utr.transactiontype, utr.create_time createtime " +
                " from user_transaction_record  utr " +
                " where ifnull(utr.delflag, 0) = 0 and utr.userid = ?  ";
        List<Object> params = new ArrayList<>();
        //if (userid != 0) {
        //sql += " and utr.userid = ? ";
        params.add(userid);
        //}
        if (moneyflag != 0) {
            sql += " and utr.moneyflag = ? ";
            params.add(moneyflag);
        }
        return query(pageSql(sql, " order by utr.id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("transactiontype", TransactionTypeStateEnum.getValue(ModelUtil.getInt(map, "transactiontype")).getMessage());
                map.put("moneyflag", MoneyTypeEnum.getValue(ModelUtil.getInt(map, "moneyflag")).getMessage());
                map.put("transactionmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "transactionmoney")));
            }
            return map;
        });


    }

    public long getUserAccountListCount(long id, int moneyflag) {
        String sql = " Select COUNT(utr.id) count  " +
                " from user_transaction_record utr  " +
                " where ifnull(utr.delflag, 0) = 0  ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and utr.id = ? ";
            params.add(id);
        }
        if (moneyflag >= 0) {
            sql += " and utr.moneyflag = ?  ";
            params.add(moneyflag);
        }

        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);

    }


    /**
     * 导出
     *
     * @param userno
     * @param name
     * @param phone
     * @param begintime
     * @param endtime
     * @return
     */
    public List<Map<String, Object>> getUserAccountexportListAll(String userno, String name, String phone, long begintime, long endtime) {
        String sql = " select ua.id, ua.userno, ua.name, ua.phone, ua.create_time createtime, ua.walletbalance, ua.integral " +
                " from user_account ua " +
                " where ifnull(ua.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(userno)) {
            sql += " and ua.userno like ? ";
            params.add(String.format("%%%S%%", userno));
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and ua.phone like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        if (begintime != 0) {
            sql += " and ua.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and ua.create_time < ? ";
            params.add(endtime);
        }

        List<Map<String, Object>> list = queryForList(sql, params);
        for (Map<String, Object> map : list) {
            map.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(map, "walletbalance")));
            map.put("createtime", UnixUtil.getDate(ModelUtil.getLong(map, "createtime"), "yyyy-MM-dd HH:mm:ss"));
        }
        return list;

    }
}
