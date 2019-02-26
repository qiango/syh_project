package com.syhdoctor.webserver.mapper.wallet;

import com.syhdoctor.common.utils.EnumUtils.PayTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.TransactionTypeStateEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import com.syhdoctor.webserver.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public abstract class RechargeableOrderBaseMapper extends BaseMapper {

    /**
     * 充值记录查询
     *
     * @param id
     * @param amountmoney
     * @param lotnumber
     * @param name
     * @param paytype
     * @param begintime
     * @param endtime
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getRechargeableOrderList(long id, BigDecimal amountmoney, String lotnumber, String name, int paytype, long begintime, long endtime, int pageIndex, int pageSize) {
        String sql = "select o.id id," +
                "       o.paytype paytype," +
                "       o.amountmoney amountmoney, " +
                "       o.lotnumber lotnumber, " +
                "       c.redeemcode redeemcode, " +
                "       o.rechargeable_time begintime, " +
                "       o.userid userid, " +
                "       case ifnull(o.role_type,1)   " +
                "             when 1 then a.userno   " +
                "             else d.in_doc_code end as userno, " +
                "       case ifnull(o.role_type,1)    " +
                "             when 1 then a.name   " +
                "             else d.doc_name end as name " +
                "from rechargeable_order o " +
                "       left join rechargeable_card c on o.cardid = c.id and ifnull(c.delflag, 0) = 0 " +
                "       left join user_account a on o.userid = a.id and ifnull(a.delflag, 0) = 0 " +
                "       left join doctor_info d on o.userid = d.doctorid and ifnull(a.delflag, 0) = 0 " +
                " where ifnull(o.delflag, 0) = 0 and o.status=2 and paystatus=1 ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and o.id = ? ";
            params.add(id);
        }
        if (amountmoney.compareTo(BigDecimal.ZERO) > 0) {
            sql += " and o.amountmoney = ? ";
            params.add(PriceUtil.addPrice(amountmoney));
        }
        if (!StrUtil.isEmpty(lotnumber)) {
            sql += " and o.lotnumber like ? ";
            params.add(String.format("%%%S%%", lotnumber));
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and a.name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (paytype > 0) {
            sql += " and o.paytype = ? ";
            params.add(paytype);
        }

        if (begintime != 0) {
            sql += " and o.create_time >  ? ";
            params.add(begintime);
        }

        if (begintime != 0) {
            sql += " and o.create_time < ? ";
            params.add(endtime);
        }
        return query(pageSql(sql, " order by o.id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("paytype", PayTypeEnum.getValue(ModelUtil.getInt(map, "paytype")).getMessage());
                map.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "amountmoney")));
            }
            return map;
        });
    }

    /**
     * 行数
     *
     * @param id
     * @param amountmoney
     * @param lotnumber
     * @param name
     * @param paytype
     * @param begintime
     * @param endtime
     * @return
     */
    public long getRechargeableOrderCount(long id, BigDecimal amountmoney, String lotnumber, String name, int paytype, long begintime, long endtime) {
        String sql = "  select  count(o.id) count  " +
                "    from rechargeable_order o  " +
                "                      left join rechargeable_card c on o.cardid = c.id and ifnull(c.delflag, 0) = 0 " +
                "                       left join user_account a on o.userid = a.id and ifnull(a.delflag, 0) = 0 " +
                "                 where ifnull(o.delflag, 0) = 0  ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and o.id = ?";
            params.add(id);
        }
        if (amountmoney.compareTo(BigDecimal.ZERO) > 0) {
            sql += " and o.amountmoney = ?";
            params.add(PriceUtil.addPrice(amountmoney));
        }
        if (!StrUtil.isEmpty(lotnumber)) {
            sql += " and o.lotnumber like ? ";
            params.add(String.format("%%%S%%", lotnumber));
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and a.name like ?";
            params.add(String.format("%%%S%%", name));
        }
        if (paytype > 0) {
            sql += " and o.paytype = ?";
            params.add(paytype);
        }

        if (begintime != 0) {
            sql += " and o.create_time >  ? ";
            params.add(begintime);
        }

        if (begintime != 0) {
            sql += " and o.create_time < ? ";
            params.add(endtime);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 充值记录导出
     *
     * @param id
     * @param amountmoney
     * @param lotnumber
     * @param name
     * @param paytype
     * @param begintime
     * @param endtime
     * @return
     */
    public List<Map<String, Object>> getRechargeableOrderExportListAll(long id, BigDecimal amountmoney, String lotnumber, String name, int paytype, long begintime, long endtime) {
        String sql = "select o.id id, " +
                "       o.paytype paytype, " +
                "       o.amountmoney amountmoney, " +
                "       o.lotnumber lotnumber, " +
                "       c.redeemcode redeemcode, " +
                "       o.begintime begintime, " +
                "       o.userid userid, " +
                "       a.name name " +
                " from rechargeable_order o " +
                "       left join rechargeable_card c on o.cardid = c.id and ifnull(c.delflag, 0) = 0 " +
                "       left join user_account a on o.userid = a.id and ifnull(a.delflag, 0) = 0 " +
                " where ifnull(o.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and o.id = ? ";
            params.add(id);
        }
        if (amountmoney.compareTo(BigDecimal.ZERO) > 0) {
            sql += " and o.amountmoney = ? ";
            params.add(PriceUtil.addPrice(amountmoney));
        }
        if (!StrUtil.isEmpty(lotnumber)) {
            sql += " and o.lotnumber like ? ";
            params.add(String.format("%%%S%%", lotnumber));
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and a.name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (paytype > 0) {
            sql += " and o.paytype = ? ";
            params.add(paytype);
        }

        if (begintime != 0) {
            sql += " and o.create_time >  ? ";
            params.add(begintime);
        }

        if (begintime != 0) {
            sql += " and o.create_time < ? ";
            params.add(endtime);
        }

        List<Map<String, Object>> list = queryForList(sql, params);
        for (Map<String, Object> map : list) {
            map.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "amountmoney")));
            map.put("begintime", UnixUtil.getDate(ModelUtil.getLong(map, "begintime"), "yyyy-MM-dd HH:mm:ss"));
            map.put("paytype", PayTypeEnum.getValue(ModelUtil.getInt(map, "paytype")).getMessage());
        }
        return list;
    }

    public List<Map<String, Object>> getRechargeableOrderExportLot(BigDecimal amountmoney, String lotnumber, long begintime, long endtime) {
        String sql = "select id," +
                "       amountmoney, " +
                "       lotnumber, " +
                "       effectivetype, " +
                "       total, " +
                "       begintime, " +
                "       endtime, " +
                "       principal, " +
                "       createcardstatus, " +
                "       notusedcount, " +
                "       create_time " +
                "from rechargeable_card_lotnumber " +
                " where ifnull(delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (amountmoney.compareTo(BigDecimal.ZERO) > 0) {
            sql += " and amountmoney= ? ";
            params.add(PriceUtil.addPrice(amountmoney));
        }
        if (!StrUtil.isEmpty(lotnumber)) {
            sql += " and lotnumber like ? ";
            params.add(String.format("%%%S%%", lotnumber));
        }
        if (begintime != 0) {
            sql += " and create_time >  ? ";
            params.add(begintime);
        }

        if (begintime != 0) {
            sql += " and create_time < ? ";
            params.add(endtime);
        }
        return query(sql + " order by id desc", params, (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "amountmoney")));
                map.put("effectivetype", ModelUtil.getInt(map, "effectivetype") == 1 ? "永久有效" : "近效期");
                map.put("begintime", UnixUtil.getDate(ModelUtil.getLong(map, "begintime"), "yyyy-MM-dd HH:mm:ss"));
                map.put("endtime", UnixUtil.getDate(ModelUtil.getLong(map, "endtime"), "yyyy-MM-dd HH:mm:ss"));
                map.put("create_time", UnixUtil.getDate(ModelUtil.getLong(map, "create_time"), "yyyy-MM-dd HH:mm:ss"));
                map.put("createcardstatus", ModelUtil.getInt(map, "createcardstatus") == 0 ? "未完成" : "已完成");
            }
            return map;
        });


    }

    public long addRechargeablecardlot(BigDecimal amountmoney, int effectivetype, long begintime, long endtime, long total, String principal, String channel_prefix, long createuser) {
        String sql = " insert into rechargeable_card_lotnumber (amountmoney, " +
                "                                  lotnumber, " +
                "                                  effectivetype, " +
                "                                  begintime, " +
                "                                  endtime, " +
                "                                  total, " +
                "                                  notusedcount, " +
                "                                  principal, " +
                "                                  create_time, " +
                "                                  channel_prefix, " +
                "                                  create_user) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";
        long id = getId("rechargeable_card_lotnumber");
        List<Object> params = new ArrayList<>();
        params.add(PriceUtil.addPrice(amountmoney));
        params.add(id);
        params.add(effectivetype);
        params.add(begintime);
        params.add(endtime);
        params.add(total);
        params.add(total);
        params.add(principal);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(channel_prefix);
        params.add(createuser);
        insert(sql, params);
        return id;
    }

    public Map<String, Object> getResult(long id) {
        String sqlLot = "select createcardstatus from rechargeable_card_lotnumber where id=?";
        Map<String, Object> list = queryForMap(sqlLot, id);
        return list;
    }

    public List<Map<String, Object>> getRechargeableOrderList(BigDecimal amountmoney, String lotnumber, long begintime, long endtime, int pageIndex, int pageSize) {
        String sql = "select id," +
                "       amountmoney, " +
                "       lotnumber, " +
                "       total, " +
                "       notusedcount, " +
                "       create_time " +
                "from rechargeable_card_lotnumber " +
                " where ifnull(delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (amountmoney.compareTo(BigDecimal.ZERO) > 0) {
            sql += " and amountmoney= ? ";
            params.add(PriceUtil.addPrice(amountmoney));
        }
        if (!StrUtil.isEmpty(lotnumber)) {
            sql += " and lotnumber like ? ";
            params.add(String.format("%%%S%%", lotnumber));
        }
        if (begintime != 0) {
            sql += " and create_time >  ? ";
            params.add(begintime);
        }

        if (begintime != 0) {
            sql += " and create_time < ? ";
            params.add(endtime);
        }
//        queryForList(pageSql(sql, " order by d.id desc"), pageParams(params, pageIndex, pageSize))
        return query(pageSql(sql, " order by id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "amountmoney")));
            }
            return map;
        });
    }

    /**
     * 行数
     *
     * @param amountmoney
     * @param lotnumber
     * @param begintime
     * @param endtime
     * @return
     */
    public long getRechargeableOrderCount(BigDecimal amountmoney, String lotnumber, long begintime, long endtime) {
        String sql = "select count(id) count from rechargeable_card_lotnumber o where ifnull(o.delflag,0)=0";
        List<Object> params = new ArrayList<>();
        if (amountmoney.compareTo(BigDecimal.ZERO) > 0) {
            sql += " and amountmoney = ?";
            params.add(amountmoney);
        }
        if (!StrUtil.isEmpty(lotnumber)) {
            sql += " and lotnumber like ? ";
            params.add(String.format("%%%S%%", lotnumber));
        }
        if (begintime != 0) {
            sql += " and create_time >  ? ";
            params.add(begintime);
        }

        if (endtime != 0) {
            sql += " and create_time < ? ";
            params.add(endtime);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public List<Map<String, Object>> getDetailList(long id, int pageIndex, int pageSize) {
        String sql = "select id," +
                "    redeemcode,  " +
                "    useflag  " +
                "from rechargeable_card " +
                " where ifnull(delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        sql += " and lotnumberid = ? ";
        params.add(id);
        return query(pageSql(sql, " order by id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("useflag", ModelUtil.getInt(map, "useflag") == 1 ? "已使用" : "未使用");
            }
            return map;
        });
    }

    public List<Map<String, Object>> getDetailList(long id) {
        String sql = "select rc.id, " +
                "       rc.redeemcode, " +
                "       rc.useflag, " +
                "       rel.amountmoney, " +
                "       rel.lotnumber, " +
                "       rel.effectivetype, " +
                "       begintime, " +
                "       endtime, " +
                "       principal " +
                "from rechargeable_card rc " +
                "            left join rechargeable_card_lotnumber rel on rc.lotnumberid = rel.id and ifnull(rel.delflag, 0) = 0 " +
                "where ifnull(rc.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        sql += " and lotnumberid = ? order by id desc ";
        params.add(id);
        return query(sql, params, (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "amountmoney")));
                map.put("effectivetype", ModelUtil.getInt(map, "effectivetype") == 1 ? "永久有效" : "近效期");
                map.put("begintime", UnixUtil.getDate(ModelUtil.getLong(map, "begintime"), "yyyy-MM-dd HH:mm:ss"));
                map.put("endtime", UnixUtil.getDate(ModelUtil.getLong(map, "endtime"), "yyyy-MM-dd HH:mm:ss"));
                map.put("useflag", ModelUtil.getInt(map, "useflag") == 0 ? "未使用" : "已使用");
            }
            return map;
        });
    }

    public Map<String, Object> getHead(long id) {
        String sql = "select lotnumber,amountmoney,channel_prefix,total,effectivetype,createcardstatus,begintime,endtime,principal " +
                "  from rechargeable_card_lotnumber where ifnull(delflag, 0) = 0 and id=?";
        Map<String, Object> data = queryForMap(sql, id);
        if (data != null) {
            data.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(data, "amountmoney")));
            data.put("effectivetype", (ModelUtil.getInt(data, "effectivetype") == 1) ? "永久有效" : "近效期");
        }
        return data;
    }


    public long getDetailListCount(long id) {
        String sql = "select count(id) count from rechargeable_card o where ifnull(o.delflag,0)=0";
        List<Object> params = new ArrayList<>();
        sql += " and lotnumberid = ? ";
        params.add(id);
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 用户充值
     *
     * @param *              @param roleType
     * @param userId
     * @param payType
     * @param applicableType
     * @param amount
     * @param cardCode
     * @param remark
     * @param createuserid
     * @param operateMode
     * @return void
     * @author qian.wang
     * @date 2018/10/23
     */
    public Map<String, Object> insertRecharOrderByUser(long userId, int payType, int applicableType, BigDecimal amount, String cardCode, String remark, long createuserid, int operateMode, String orderNo) {
        String sql = " insert into rechargeable_order (orderno, " +
                "                                  paytype, " +
                "                                  cardid, " +
                "                                  amountmoney, " +
                "                                  userid, " +
                "                                  lotnumber, " +
                "                                  effectivetype, " +
                "                                  begintime, " +
                "                                  endtime, " +
                "                                  principal, " +
                "                                  create_time, " +
                "                                  rechargeable_time, " +
                "                                  role_type, " +
                "                                  operate_mode, " +
                "                                  remark, " +
                "                                  applicable_type, " +
                "                                  create_user) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?,?,?, ?, ?, ?, ?, ?,?, ?)";

        List<Object> params = new ArrayList<>();
        params.add(orderNo);
        params.add(payType);
        Map<String, Object> key = new HashMap<>();
        if (!StrUtil.isEmpty(cardCode)) {
            String sqlCard = "select id,lotnumberid " +
                    "from rechargeable_card " +
                    " where ifnull(delflag, 0) = 0 and redeemcode=?";
            List<Object> param = new ArrayList<>();
            param.add(cardCode);
            Map<String, Object> map = queryForMap(sqlCard, param);
            if (null == map) {
                throw new ServiceException("该充值卡不存在");
            }
            long cardid = ModelUtil.getLong(map, "id");
            key.put("cardid", cardid);
            long lotnumberid = ModelUtil.getLong(map, "lotnumberid");
            String sqllot = "select id," +
                    "       amountmoney, " +
                    "       lotnumber, " +
                    "       notusedcount, " +
                    "       effectivetype, " +
                    "       begintime, " +
                    "       principal, " +
                    "       endtime " +
                    "from rechargeable_card_lotnumber " +
                    " where ifnull(delflag, 0) = 0 and id=?";
            List<Object> paramlot = new ArrayList<>();
            paramlot.add(lotnumberid);
            Map<String, Object> maplot = queryForMap(sqllot, paramlot);
            if (maplot == null) {
                throw new ServiceException("该充值卡所属批号不存在或已删除，请检查");
            }
            BigDecimal amountmoney = ModelUtil.getDec(maplot, "amountmoney", BigDecimal.ZERO);
            key.put("amountmoney", PriceUtil.findPrice(amountmoney));
            long id = ModelUtil.getLong(maplot, "id");
            int effectivetype = ModelUtil.getInt(maplot, "effectivetype");
            long begintime = ModelUtil.getLong(maplot, "begintime");
            long endtime = ModelUtil.getLong(maplot, "endtime");
            String lotnumber = ModelUtil.getStr(maplot, "lotnumber");
            String principal = ModelUtil.getStr(maplot, "principal");
            long notusedcount = ModelUtil.getLong(maplot, "notusedcount");
            if (effectivetype != 1) {
                Map<String, Object> overdue = isOverdue(lotnumberid);
                if (overdue == null) {
                    throw new ServiceException("该充值卡已过期");
                }
            }
            params.add(cardid);
            params.add(amountmoney);
            params.add(userId);
            params.add(lotnumber);
            params.add(effectivetype);
            params.add(begintime);
            params.add(endtime);
            params.add(principal);
            //更新充值卡批号使用数量
            String sqllots = "update rechargeable_card_lotnumber set notusedcount=? where id=?";
            List<Object> paramnew = new ArrayList<>();
            paramnew.add(notusedcount - 1);
            paramnew.add(id);
            update(sqllots, paramnew);
        } else {
            params.add(null);
            params.add(PriceUtil.addPrice(amount));
            key.put("amountmoney", amount);
            params.add(userId);
            params.add(null);
            params.add(null);
            params.add(null);
            params.add(null);
            params.add(null);
        }
        params.add(UnixUtil.getNowTimeStamp());
        params.add(UnixUtil.getNowTimeStamp());
        params.add(1);
        params.add(operateMode);
        params.add(remark);
        params.add(applicableType);
        params.add(createuserid);
        insert(sql, params);
        return key;
    }

    /**
     * 医生充值
     *
     * @param *              @param roleType
     * @param userId
     * @param applicableType
     * @param createuserid
     * @param operateMode
     * @return void
     * @author qian.wang
     * @date 2018/10/23
     */
    public BigDecimal insertRecharOrderByDoctor(long userId, String orderNo, BigDecimal amountmoney, int applicableType, String remark, long createuserid, int operateMode) {

        String sql = " insert into rechargeable_order (orderno, " +
                "                                  userid, " +
                "                                  create_time, " +
                "                                  amountmoney, " +
                "                                  rechargeable_time, " +
                "                                  applicable_type, " +
                "                                  role_type, " +
                "                                  operate_mode, " +
                "                                  remark, " +
                "                                  create_user) " +
                "values (?, ?, ?, ?, ?, ?,?, ?,?,?)";

        List<Object> params = new ArrayList<>();
        params.add(orderNo);
        params.add(userId);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(PriceUtil.addPrice(amountmoney));
        params.add(UnixUtil.getNowTimeStamp());
        params.add(applicableType);
        params.add(2);
        params.add(operateMode);
        params.add(remark);
        params.add(createuserid);
        insert(sql, params);
        return amountmoney;
    }

    /**
     * 查找用户
     *
     * @param * @param userno 用户编码
     * @return java.util.Map<java.lang.String                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                >
     * @author qian.wang
     * @date 2018/10/23
     */
    public Map<String, Object> getUser(String userno) {
        String sql = "select id,name,phone from user_account where userno=? ";
        Map<String, Object> data = queryForMap(sql, userno);
//        if (data != null) {
//            data.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(data, "walletbalance")));
//        }
        return data;
    }

    //查找医生
    public Map<String, Object> getDoctor(String doctorno) {
        String sql = "select f.doctorid, f.doc_name, f.doo_tel, e.integral " +
                " from doctor_info f " +
                "       left join doctor_extends e on f.doctorid = e.doctorid " +
                " where in_doc_code =? ";
        Map<String, Object> data = queryForMap(sql, doctorno);
        return data;
    }

    public List<Map<String, Object>> getDoctorList(String name) {
        String sql = "select doctorid id,CONCAT_WS('   ',in_doc_code ,doc_name) name,in_doc_code doctorno from doctor_info where examine=7 and IFNULL(delflag,0)=0 ";
        List<Object> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(name)) {
            sql += " and (doc_name like ? or in_doc_code like ?) ";
            list.add(String.format("%%%s%%", name));
            list.add(String.format("%%%s%%", name));
        }
        sql += " order by create_time desc";
        return queryForList(sql, list);
    }


    public Map<String, Object> isOverdue(long lotid) {
        String sql = " select id " +
                "from rechargeable_card_lotnumber " +
                "where UNIX_TIMESTAMP() * 1000 > begintime && unix_timestamp() * 1000 < endtime " +
                "  and id = ? ";
        return queryForMap(sql, lotid);
    }

    public void deleteLot(long id) {
        String sql = "update rechargeable_card_lotnumber set delflag=1 where id=?";
        update(sql, id);
    }

    public List<Map<String, Object>> findValue() {
        String sql = "select value id,value from face_value";
        return query(sql, new ArrayList<>(), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            map.put("id", PriceUtil.findPrice(ModelUtil.getLong(map, "id")));
            map.put("value", PriceUtil.findPrice(ModelUtil.getLong(map, "value")));
            return map;
        });
    }

    public void createDetail(long id) {
        String sqlDetail = " insert into rechargeable_card (lotnumberid, " +
                "                                  redeemcode, " +
                "                                  useflag, " +
                "                                  create_time) " +
                "values (?, ?, ?, ?) ";
        String sqlLot = "select total,channel_prefix from rechargeable_card_lotnumber where id=?";
        Map<String, Object> list = queryForMap(sqlLot, id);
        long sum = ModelUtil.getLong(list, "total");
        String channel = ModelUtil.getStr(list, "channel_prefix");
        for (int i = 0; i < sum; i++) {
            List<Object> paramsDetails = new ArrayList<>();
            paramsDetails.add(id);
            String value = UnixUtil.generateString(6);
            paramsDetails.add(((channel == null ? "" : channel) + value).toLowerCase());
            paramsDetails.add(0);
            paramsDetails.add(UnixUtil.getNowTimeStamp());
            insert(sqlDetail, paramsDetails);
        }
        String updateSql = "update rechargeable_card_lotnumber set createcardstatus=1 where id=?";
        List<Object> params = new ArrayList<>();
        params.add(id);
        update(updateSql, params);
    }

    public Map<String, Object> getRechargeableOrder(long orderId) {
        String sql = " select paystatus from rechargeable_order where id =? ";
        return queryForMap(sql, orderId);
    }

    /**
     * 医生充值记录列表
     *
     * @param name
     * @param phone
     * @param applicabletype
     * @param begintime
     * @param endtime
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> doctorRechargeableOrderList(String name, String phone, int applicabletype, long begintime, long endtime, int pageIndex, int pageSize) {
        String sql = " select o.id                        id, " +
                "       d.doo_tel                   docphone, " +
                "       rechargeable_time           rechargeabletime, " +
                "       remark, " +
                "       applicable_type             applicabletype, " +
//                "       o.paytype                   paytype, " +
                "       o.amountmoney               amountmoney, " +
//                "       o.lotnumber                 lotnumber, " +
//                "       c.redeemcode                redeemcode, " +
//                "       o.rechargeable_time         begintime, " +
//                "       o.userid                    userid, " +
//                "       d.in_doc_code  as userno, " +
                "       d.doc_name     as name " +
                " from rechargeable_order o " +
                "       left join rechargeable_card c on o.cardid = c.id and ifnull(c.delflag, 0) = 0 " +
                "       left join user_account a on o.userid = a.id and ifnull(a.delflag, 0) = 0 " +
                "       left join doctor_info d on o.userid = d.doctorid and ifnull(a.delflag, 0) = 0 " +
                " where ifnull(o.delflag, 0) = 0 " +
                "  and o.status = 2 " +
                "  and paystatus = 1 " +
                "  and o.role_type = 2 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and d.doc_name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and d.doo_tel like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        if (applicabletype != 0) {
            sql += " and applicable_type = ? ";
            params.add(applicabletype);
        }
        if (begintime != 0) {
            sql += " and rechargeable_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and rechargeable_time < ? ";
            params.add(endtime);
        }
        return query(pageSql(sql, " order by rechargeable_time desc "), pageParams(params, pageIndex, pageSize), (ResultSet rs, int num) -> {
            Map<String, Object> map = resultToMap(rs);
            if (map != null) {
                int a = ModelUtil.getInt(map, "applicabletype");
                map.put("applicabletype", a == 0 ? null : TransactionTypeStateEnum.getValue(a).getMessage());
                map.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "amountmoney")));
            }
            return map;
        });
    }

    /**
     * 医生充值记录列表导出
     *
     * @param name
     * @param phone
     * @param applicabletype
     * @param begintime
     * @param endtime
     * @return
     */
    public List<Map<String, Object>> doctorRechargeableOrderListExport(String name, String phone, int applicabletype, long begintime, long endtime) {
        String sql = " select o.id                        id, " +
                "       d.doo_tel                   docphone, " +
                "       rechargeable_time           rechargeabletime, " +
                "       remark, " +
                "       applicable_type             applicabletype, " +
                "       o.amountmoney               amountmoney, " +
                "       d.doc_name     as name " +
                " from rechargeable_order o " +
                "       left join rechargeable_card c on o.cardid = c.id and ifnull(c.delflag, 0) = 0 " +
                "       left join user_account a on o.userid = a.id and ifnull(a.delflag, 0) = 0 " +
                "       left join doctor_info d on o.userid = d.doctorid and ifnull(a.delflag, 0) = 0 " +
                " where ifnull(o.delflag, 0) = 0 " +
                "  and o.status = 2 " +
                "  and paystatus = 1 " +
                "  and o.role_type = 2 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and d.doc_name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and d.doo_tel like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        if (applicabletype != 0) {
            sql += " and applicable_type = ? ";
            params.add(applicabletype);
        }
        if (begintime != 0) {
            sql += " and rechargeable_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and rechargeable_time < ? ";
            params.add(endtime);
        }
        List<Map<String, Object>> list = queryForList(sql + " order by rechargeable_time desc ", params);
        for (Map<String, Object> map : list) {
            int a = ModelUtil.getInt(map, "applicabletype");
            map.put("applicabletype", a == 0 ? null : TransactionTypeStateEnum.getValue(a).getMessage());
            map.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "amountmoney")));
            map.put("rechargeabletime", UnixUtil.getDate(ModelUtil.getLong(map, "rechargeabletime"), "yyyy-MM-dd HH:mm:ss"));
        }
        return list;
    }

    public long doctorRechargeableOrderListCount(String name, String phone, int applicabletype, long begintime, long endtime) {
        String sql = " select count(o.id) count " +
                " from rechargeable_order o " +
                "       left join rechargeable_card c on o.cardid = c.id and ifnull(c.delflag, 0) = 0 " +
                "       left join user_account a on o.userid = a.id and ifnull(a.delflag, 0) = 0 " +
                "       left join doctor_info d on o.userid = d.doctorid and ifnull(a.delflag, 0) = 0 " +
                " where ifnull(o.delflag, 0) = 0 " +
                "  and o.status = 2 " +
                "  and paystatus = 1 " +
                "  and o.role_type = 2 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and d.doc_name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and d.doo_tel like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        if (applicabletype != 0) {
            sql += " and applicable_type = ? ";
            params.add(applicabletype);
        }
        if (begintime != 0) {
            sql += " and rechargeable_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and rechargeable_time < ? ";
            params.add(endtime);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


}
