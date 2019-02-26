package com.syhdoctor.webserver.mapper.vipcard;


import com.syhdoctor.common.utils.EnumUtils.IdGenerator;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import com.syhdoctor.webserver.exception.ServiceException;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.*;

public abstract class VipCardBaseMapper extends BaseMapper {

    /**
     * 会员卡列表
     *
     * @param id
     * @param begintime
     * @param endtime
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> vipCardList(long id, String vipcardno, String vipcardname, long begintime, long endtime, int pageIndex, int pageSize) {
        String sql = " SELECT " +
                "     id,   " +
                "     vipcardno, " +
                "     vipcardname, " +
                "     price, " +
                "     renewal_fee, " +
                "     original_price, " +
                "     health_consultant healthconsultant, " +
                "     medical_expert medicalexpert, " +
                "     medical_green medicalgreen, " +
                "     health_consultant_ceefax healthconsultantceefax, " +
                "     health_consultant_phone healthconsultantphone, " +
                "     health_consultant_discount healthconsultantdiscount," +
                "     medical_expert_discount medicalexpertdiscount," +
                "     medical_expert_ceefax medicalexpertceefax, " +
                "     medical_expert_phone medicalexpertphone, " +
                "     medical_expert_video medicalexpertvideo, " +
                "     video, " +
                "     ceefax, " +
                "     discount, " +
                "     effective_time effectivetime, " +
                "     create_time createtime," +
                "     sort" +
                "    FROM vip_card where ifnull(delflag,0)=0  ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and id = ? ";
            params.add(id);
        }
        if (!StrUtil.isEmpty(vipcardno)) {
            sql += " and vipcardno like ? ";
            params.add(String.format("%%%S%%", vipcardno));
        }
        if (!StrUtil.isEmpty(vipcardname)) {
            sql += " and vipcardname like ? ";
            params.add(String.format("%%%S%%", vipcardname));
        }
        if (begintime != 0) {
            sql += " and create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and create_time < ? ";
            params.add(endtime);
        }
//        return queryForList(pageSql(sql, " order by id desc "), pageParams(params, pageIndex, pageSize));
        return query(pageSql(sql, " order by sort desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("discount", PriceUtil.findPrice(ModelUtil.getLong(map, "discount")));
                map.put("renewal_fee", PriceUtil.findPrice(ModelUtil.getLong(map, "renewal_fee")));
                map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
                map.put("original_price", PriceUtil.findPrice(ModelUtil.getLong(map, "original_price")));
            }
            return map;
        });
    }


    public Map<String, Object> vipCardDetails(long id) {
        String sql = " SELECT " +
                "     id,   " +
                "     vipcardno, " +
                "     vipcardname, " +
                "     price, " +
                "     renewal_fee, " +
                "     original_price, " +
                "     health_consultant healthconsultant, " +
                "     medical_expert medicalexpert, " +
                "     medical_green medicalgreen, " +
                "     health_consultant_ceefax healthconsultantceefax, " +
                "     health_consultant_phone healthconsultantphone, " +
                "     health_consultant_discount healthconsultantdiscount," +
                "     medical_expert_discount medicalexpertdiscount," +
                "     medical_expert_ceefax medicalexpertceefax, " +
                "     medical_expert_phone medicalexpertphone, " +
                "     medical_expert_video medicalexpertvideo, " +
                "     video, " +
                "     ceefax, " +
                "     discount, " +
                "     sort," +
                "     effective_time effectivetime, " +
                "     create_time createtime" +
                "    FROM vip_card where ifnull(delflag,0)=0 and id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        Map<String, Object> map = queryForMap(sql, params);
        if (null != map) {
            map.put("discount", PriceUtil.findPrice(ModelUtil.getLong(map, "discount")));
            map.put("renewal_fee", PriceUtil.findPrice(ModelUtil.getLong(map, "renewal_fee")));
            map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
            map.put("original_price", PriceUtil.findPrice(ModelUtil.getLong(map, "original_price")));
        }
        return map;
    }

    public Map<String, Object> findVipCard(long userid) {
        String sqls = "select id as vipcardid,price,renewal_fee as renewalfee,vipcardno,vipcardname,effective_time as effectivetime,original_price as originalprice from vip_card where ifnull(delflag,0)=0 order by sort desc limit 1";
        Map<String, Object> mapss = queryForMap(sqls);
        mapss.put("price", PriceUtil.findPrice(ModelUtil.getLong(mapss, "price")));
        mapss.put("renewalfee", PriceUtil.findPrice(ModelUtil.getLong(mapss, "renewalfee")));
        mapss.put("originalprice", PriceUtil.findPrice(ModelUtil.getLong(mapss, "originalprice")));
        mapss.put("isvip", 0);
        String sqlUserVip = "select vc.renewal_fee as renewalfee,um.vipcardid,um.id,um.is_enabled as isenabled,um.level,um.upgrade_integral as upgradeintegral,um.current_integral as currentintegral,um.is_expire as isexpire,um.vip_expiry_time as vipexpirytime," +
                "ua.headpic,ua.name from user_member um left join user_account ua on um.userid=ua.id " +
                " left join vip_card vc on um.vipcardid=vc.id where ifnull(um.delflag,0)=0 and um.userid=?";
        Map<String, Object> map = queryForMap(sqlUserVip, userid);
        if (map != null) {
            if (ModelUtil.getInt(map, "isenabled") == 1) {
                int ex = ModelUtil.getInt(map, "isexpire");
                long exTime = ModelUtil.getLong(map, "vipexpirytime");//到期时间
                long nowTime = UnixUtil.getNowTimeStamp();//当前时间
                long all = getTimeMonth();
                if (all > exTime || all == exTime) {//要续费
                    map.put("isrenew", 1);
                } else {
                    map.put("isrenew", 0);
                }
                if (1 == ex) {//未过期
                    if (nowTime > exTime || nowTime == exTime) {//过期
                        map.put("is_expire", 0);//到没到期
                        map.put("isvip", "0");//不是vip
                        String sql = "update user_member set is_expire=1 where id=?";
                        //更新过期
                        update(sql, ModelUtil.getLong(map, "id"));
                    } else {
                        map.put("isvip", "1");//是vip
                    }
                }
                map.put("renewalfee", PriceUtil.findPrice(ModelUtil.getLong(map, "renewalfee")));
                return map;
            } else {
                return mapss;
            }
        }
        return mapss;
    }

    /**
     * 会员卡导出
     *
     * @param id
     * @param begintime
     * @param endtime
     * @return
     */
    public List<Map<String, Object>> vipCardExportList(long id, String vipcardno, String vipcardname, long begintime, long endtime) {
        String sql = " SELECT " +
                "     id,   " +
                "     vipcardno, " +
                "     vipcardname, " +
                "     price, " +
                "     health_consultant healthconsultant, " +
                "     medical_expert medicalexpert, " +
                "     medical_green medicalgreen, " +
                "     health_consultant_ceefax healthconsultantceefax, " +
                "     health_consultant_phone healthconsultantphone, " +
                "     medical_expert_ceefax medicalexpertceefax, " +
                "     medical_expert_phone medicalexpertphone, " +
                "     medical_expert_video medicalexpertvideo, " +
                "     health_consultant_discount healthconsultantdiscount," +
                "     medical_expert_discount medicalexpertdiscount," +
                "     medical_green_num medicalgreennum, " +
                "     health_consultant_discount healthconsultantdiscount, " +
                "     medical_expert_discount medicalexpertdiscount, " +
                "     effective_time effectivetime, " +
                "     create_time createtime " +
                "    FROM vip_card where ifnull(delflag,0)=0  ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and id = ? ";
            params.add(id);
        }
        if (!StrUtil.isEmpty(vipcardno)) {
            sql += " and vipcardno = ? ";
            params.add(vipcardno);
        }
        if (!StrUtil.isEmpty(vipcardname)) {
            sql += " and vipcardname = ? ";
            params.add(vipcardname);
        }
        if (begintime != 0) {
            sql += " and create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and create_time < ? ";
            params.add(endtime);
        }
        return queryForList(sql, params);
    }


    /**
     * 会员卡行数
     *
     * @param id
     * @param begintime
     * @param endtime
     * @return
     */
    public long vipCardListCount(long id, String vipcardno, String vipcardname, long begintime, long endtime) {
        String sql = " select count(id) count from vip_card where ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and id = ? ";
            params.add(id);
        }
        if (!StrUtil.isEmpty(vipcardno)) {
            sql += " and vipcardno = ? ";
            params.add(vipcardno);
        }
        if (!StrUtil.isEmpty(vipcardname)) {
            sql += " and vipcardname = ? ";
            params.add(vipcardname);
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

    //首冲生成订单的同时把信息同步给用户,未生效状态
    public boolean syscoreUser(long userid, long vipcardId) {
        //先查，若未删除，则删除重新生成
        String sqlUserVip = "select id,is_enabled from user_member where ifnull(delflag,0)=0 and userid=?";
        Map<String, Object> map = queryForMap(sqlUserVip, userid);
        if (map != null) {
            if (ModelUtil.getInt(map, "is_enabled") == 1) {
                throw new ServiceException("您已购买该会员卡，不可重复购买");
            }
            String sqlUpdate = "update user_member set delflag=1 where id=?";
            update(sqlUpdate, ModelUtil.getLong(map, "id"));
        }
        String sqlCard = "select vipcardno,vipcardname,price,effective_time,health_consultant, " +
                "       medical_expert, " +
                "       medical_green, " +
                "       health_consultant_ceefax, " +
                "       health_consultant_phone, " +
                "       medical_expert_ceefax, " +
                "       medical_expert_phone, " +
                "       medical_expert_video, " +
                "       medical_green_num, " +
                "       health_consultant_discount, " +
                "       discount, " +
                "       medical_expert_discount, " +
                "       effective_time from vip_card where id=?";
        Map<String, Object> vipmap = queryForMap(sqlCard, vipcardId);
        long expiryTime = getTime();
        String sql = "insert into user_member(userid," +
                "       vipcardno, " +
                "       vipcardname, " +
                "       vipcardId, " +
                "       vip_expiry_time, " +
                "       create_time, " +
                "       health_consultant, " +
                "       medical_expert, " +
                "       medical_green, " +
                "       is_expire, " +
                "       upgrade_integral, " +
                "       level, " +
                "       current_integral, " +
                "       health_consultant_ceefax, " +
                "       health_consultant_phone, " +
                "       medical_expert_ceefax, " +
                "       medical_expert_phone, " +
                "       medical_expert_video, " +
                "       medical_green_num, " +
                "       health_consultant_discount, " +
                "       medical_expert_discount, " +
                "       discount, " +
                "       is_enabled) " +
                "       VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(userid);
        params.add(ModelUtil.getStr(vipmap, "vipcardno"));
        params.add(ModelUtil.getStr(vipmap, "vipcardname"));
        params.add(vipcardId);
        params.add(expiryTime);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(ModelUtil.getInt(vipmap, "health_consultant"));
        params.add(ModelUtil.getStr(vipmap, "medical_expert"));
        params.add(ModelUtil.getStr(vipmap, "medical_green"));
        params.add(1);
        params.add(1000);//升级所需积分
        params.add(1);
        params.add(0);
        params.add(ModelUtil.getInt(vipmap, "health_consultant_ceefax"));
        params.add(ModelUtil.getInt(vipmap, "health_consultant_phone"));
        params.add(ModelUtil.getInt(vipmap, "medical_expert_ceefax"));
        params.add(ModelUtil.getInt(vipmap, "medical_expert_phone"));
        params.add(ModelUtil.getInt(vipmap, "medical_expert_video"));
        params.add(ModelUtil.getInt(vipmap, "medical_green_num"));
        params.add(ModelUtil.getDouble(vipmap, "health_consultant_discount", 0));
        params.add(ModelUtil.getDouble(vipmap, "medical_expert_discount", 0));
        params.add(ModelUtil.getDouble(vipmap, "discount", 0));
        params.add(0);
        return insert(sql, params) > 0;
    }

    public long findEnjoyLevel(int level) {
        String sql = "select current_integral from enjoy_level where level=? and ifnull(delflag,0)=0";
        return ModelUtil.getLong(queryForMap(sql, level), "current_integral");
    }

    public Map<String, Object> findNum() {
        String sql = "select current_integral,price from enjoy_value where ifnull(delflag,0)=0 order by id desc limit 1";
        Map<String, Object> map = queryForMap(sql);
        if (null != map) {
            map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
        }
        return map;
    }


    public boolean vipSort(int sort) {
        String sql = " update vip_card set sort = 0 where sort = ? ";
        return update(sql, sort) > 0;
    }


    /**
     * 新增会员卡
     *
     * @param vipcardname
     * @param price
     * @param healthconsultant
     * @param medicalexpert
     * @param medicalgreen
     * @param effectivetime
     * @return
     */
    public boolean addVipCard(Double healthconsultantdiscount, Double medicalexpertdiscount, String vipcardname, BigDecimal price, BigDecimal renewal_fee, BigDecimal original_price, String healthconsultant, String medicalexpert, String medicalgreen, int ceefax, int video, Double discount, long effectivetime, int sort, long health_consultant_ceefax, long health_consultant_phone, long medical_expert_ceefax, long medical_expert_phone, long medical_expert_video) {
        String sql = " insert into vip_card (vipcardno, " +
                "                      vipcardname, " +
                "                      price, " +
                "                      renewal_fee, " +
                "                      original_price, " +
                "                      sort, " +
                "                      health_consultant, " +
                "                      medical_expert, " +
                "                      medical_green, " +
                "       health_consultant_ceefax, " +
                "       health_consultant_phone, " +
                "       medical_expert_ceefax, " +
                "       medical_expert_phone, " +
                "       medical_expert_video, " +
                "                      video, " +
                "                      ceefax, " +
                "                      discount, " +
                "                      effective_time, " +
                "                      create_time," +
                "     health_consultant_discount , " +
                "            medical_expert_discount ) " +

                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(IdGenerator.INSTANCE.nextId());
        params.add(vipcardname);
        params.add(PriceUtil.addPrice(price));
        params.add(PriceUtil.addPrice(renewal_fee));
        params.add(PriceUtil.addPrice(original_price));
        params.add(sort);
        params.add(healthconsultant);
        params.add(medicalexpert);
        params.add(medicalgreen);
        params.add(health_consultant_ceefax);
        params.add(health_consultant_phone);
        params.add(medical_expert_ceefax);
        params.add(medical_expert_phone);
        params.add(medical_expert_video);
        params.add(video);
        params.add(ceefax);
        params.add(discount);
        params.add(effectivetime);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(healthconsultantdiscount);
        params.add(medicalexpertdiscount);
        return insert(sql, params) > 0;
    }

    //首冲vip生成订单
    public Map<String, Object> insertOrder(long userid, long vipcardid) {
        String sqlDelete = "select id from vip_order where userid=? and ifnull(delflag, 0)=0 and order_type=0";
        Map<String, Object> map = queryForMap(sqlDelete, userid);
        if (null != map) {
            String sqls = "update vip_order set delflag=1 where id=?";
            update(sqls, ModelUtil.getLong(map, "id"));
        }
        String sqlCard = "select vipcardno,vipcardname,price,renewal_fee,effective_time from vip_card where id=?";
        Map<String, Object> vipmap = queryForMap(sqlCard, vipcardid);
        String sql = " insert into vip_order (" +
                "     vipcardno, " +
                "     vipcardname, " +
                "     price, " +
                "     renewal_fee, " +
                "     vip_cardid,   " +
                "     userid, " +
                "     status, " +
                "     order_type, " +
                "     effective_time, " +
                "     delflag,   " +
                "     orderno,   " +
                "     create_time) " +
                "     VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        String orderNo = IdGenerator.INSTANCE.nextId();
        List<Object> params = new ArrayList<>();
        params.add(ModelUtil.getStr(vipmap, "vipcardno"));
        params.add(ModelUtil.getStr(vipmap, "vipcardname"));
        params.add(ModelUtil.getLong(vipmap, "price"));
        params.add(ModelUtil.getLong(vipmap, "renewal_fee"));
        params.add(vipcardid);
        params.add(userid);
        params.add(1);
        params.add(0);
        params.add(ModelUtil.getInt(vipmap, "effective_time"));
        params.add(0);
        params.add(orderNo);
        params.add(UnixUtil.getNowTimeStamp());
        long id = insert(sql, params, "id");
        Map<String, Object> res = new HashMap<>();
        res.put("orderid", id);
        res.put("orderno", orderNo);
        return res;
    }

    //续费vip生成订单
    public Map<String, Object> renewalOrder(long userid, long vipcardid) {
        String sqlDelete = "select id from vip_order where userid=? and ifnull(delflag, 0)=0 and order_type=1 ";
        Map<String, Object> map = queryForMap(sqlDelete, userid);
        if (null != map) {
            String sqls = "update vip_order set delflag=1 where id=?";
            update(sqls, ModelUtil.getLong(map, "id"));
        }
        String sqlCard = "select vipcardno,vipcardname,renewal_fee,effective_time,health_consultant_ceefax,health_consultant_phone,medical_expert_ceefax,medical_expert_phone,medical_expert_video,health_consultant_discount,medical_expert_discount from vip_card where id=?";
        Map<String, Object> vipmap = queryForMap(sqlCard, vipcardid);
        String sql = " insert into vip_order (" +
                "     vipcardno, " +
                "     vipcardname, " +
                "     renewal_fee, " +
                "       health_consultant_ceefax, " +
                "       health_consultant_phone, " +
                "       medical_expert_ceefax, " +
                "       medical_expert_phone, " +
                "       medical_expert_video, " +
                "       health_consultant_discount, " +
                "       medical_expert_discount, " +
                "       discount, " +
                "     vip_cardid,   " +
                "     userid, " +
                "     status, " +
                "     order_type, " +
                "     effective_time, " +
                "     delflag,   " +
                "     orderno,   " +
                "     create_time) " +
                "     VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String orderNo = IdGenerator.INSTANCE.nextId();
        List<Object> params = new ArrayList<>();
        params.add(ModelUtil.getStr(vipmap, "vipcardno"));
        params.add(ModelUtil.getStr(vipmap, "vipcardname"));
        params.add(ModelUtil.getLong(vipmap, "renewal_fee"));
        params.add(ModelUtil.getInt(vipmap, "health_consultant_ceefax"));
        params.add(ModelUtil.getInt(vipmap, "health_consultant_phone"));
        params.add(ModelUtil.getInt(vipmap, "medical_expert_ceefax"));
        params.add(ModelUtil.getInt(vipmap, "medical_expert_phone"));
        params.add(ModelUtil.getInt(vipmap, "medical_expert_video"));
        params.add(ModelUtil.getDouble(vipmap, "health_consultant_discount", 0));
        params.add(ModelUtil.getDouble(vipmap, "medical_expert_discount", 0));
        params.add(ModelUtil.getDouble(vipmap, "discount", 0));
        params.add(vipcardid);
        params.add(userid);
        params.add(1);
        params.add(1);
        params.add(ModelUtil.getInt(vipmap, "effective_time"));
        params.add(0);
        params.add(orderNo);
        params.add(UnixUtil.getNowTimeStamp());
        long id = insert(sql, params, "id");
        Map<String, Object> res = new HashMap<>();
        res.put("orderid", id);
        res.put("orderno", orderNo);
        return res;
    }

    public Map<String, Object> getAmount(long orderid) {
        String sql = "select ua.walletbalance,ua.phone,ua.kangyang_userid as kangyanguserid,vo.order_type as orderType,vo.price,vo.renewal_fee as renewalfee from vip_order vo" +
                " left join user_account ua on vo.userid=ua.id where vo.id=?";
        Map<String, Object> map = queryForMap(sql, orderid);
        if (map != null) {
            map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
            map.put("renewalfee", PriceUtil.findPrice(ModelUtil.getLong(map, "renewalfee")));
            map.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(map, "walletbalance")));
        }
        return map;
    }

    public Map<String, Object> findUserMemById(long userid) {
        String sqlUserVip = "select id,level,upgrade_integral,current_integral,vip_expiry_time,health_consultant_ceefax,health_consultant_phone, " +
                "medical_expert_ceefax,medical_expert_phone,medical_expert_video from user_member where ifnull(delflag,0)=0 and userid=?";
        return queryForMap(sqlUserVip, userid);
    }

    public Map<String, Object> findOrderByNo(String orderNo) {
        String sqlDelete = "select id,vip_cardid,userid,price,renewal_fee,orderno,order_type,status,health_consultant_ceefax,health_consultant_phone, " +
                "  medical_expert_ceefax,medical_expert_phone,medical_expert_video,discount,health_consultant_discount,medical_expert_discount from vip_order where orderno=? and ifnull(delflag, 0)=0";
        Map<String, Object> map = queryForMap(sqlDelete, orderNo);
        if (null != map) {
            map.put("price", PriceUtil.findPrice(ModelUtil.getDec(map, "price", BigDecimal.ZERO)));
            map.put("renewal_fee", PriceUtil.findPrice(ModelUtil.getDec(map, "renewal_fee", BigDecimal.ZERO)));
        }
        return map;
    }

    public Map<String, Object> findOrderByorderId(long orderid) {
        String sqlDelete = "select id,userid,price,renewal_fee,orderno,order_type,status,pay_status paystatus,ceefax,video,vip_cardid from vip_order where id=? and ifnull(delflag, 0)=0";
        Map<String, Object> map = queryForMap(sqlDelete, orderid);
        if (null != map) {
            map.put("price", PriceUtil.findPrice(ModelUtil.getDec(map, "price", BigDecimal.ZERO)));
            map.put("renewal_fee", PriceUtil.findPrice(ModelUtil.getDec(map, "renewal_fee", BigDecimal.ZERO)));
        }
        return map;
    }

    //支付成功更新订单状态
    public boolean updateStatus(long orderid, int payType, int operateMode) {
        String sql = "update vip_order set status=4,pay_type=?,pay_status=1,modify_time=?,operate_mode=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(payType);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(operateMode);
        params.add(orderid);
        return update(sql, params) > 0;
    }

    //更新用户会员卡生效,尊享值
    public boolean updateUserRene(long userid, int level, long curr, long upgrade_integral, long expirTime, long allmedicalceefax, long allmedicalphone, long allmedicalvideo, long allhealthphone, long allhealthceefax, Double healthdisconut, Double medicaldisconut) {
        String sql = "update user_member set is_enabled=1,is_expire=1,level=?,current_integral=?,upgrade_integral=?,vip_expiry_time=?,health_consultant_ceefax=?,health_consultant_phone=?,medical_expert_ceefax=?,medical_expert_phone=?,medical_expert_video=?,health_consultant_discount=?,medical_expert_discount=? where ifnull(delflag,0)=0 and userid=?";
        List<Object> params = new ArrayList<>();
        params.add(level);
        params.add(curr);
        params.add(upgrade_integral);
        params.add(expirTime);
        params.add(allhealthceefax);
        params.add(allhealthphone);
        params.add(allmedicalceefax);
        params.add(allmedicalphone);
        params.add(allmedicalvideo);
        params.add(healthdisconut);
        params.add(medicaldisconut);
        params.add(userid);
        return update(sql, params) > 0;
    }

    //更新续费用户会员卡,尊享值
    public boolean updateUserVip(long userid, int level, long curr, long upgrade_integral) {
        String sql = "update user_member set is_enabled=1,level=?,current_integral=?,upgrade_integral=?  where ifnull(delflag,0)=0 and userid=?";
        List<Object> params = new ArrayList<>();
        params.add(level);
        params.add(curr);
        params.add(upgrade_integral);
        params.add(userid);
        return update(sql, params) > 0;
    }


    //更新钱包的钱
    public boolean updateWallet(long id, BigDecimal finalamount) {
        String sql = "update user_account set walletbalance=?,modify_time=? where id=?";
        List<Object> params = new ArrayList<>();
        params.add(finalamount);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 删除会员卡
     *
     * @param id
     * @return
     */
    public boolean delVipCard(long id) {
        String sql = " update vip_card vc set vc.delflag = 1 where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }


    /**
     * 修改会员
     *
     * @param id
     * @param vipcardno
     * @param vipcardname
     * @param price
     * @param healthconsultant
     * @param medicalexpert
     * @param medicalgreen
     * @param effectivetime
     * @return
     */
    public boolean updateVipCard(Double healthconsultantdiscount, Double medicalexpertdiscount, long id, String vipcardno, String vipcardname, BigDecimal price, BigDecimal renewal_fee, BigDecimal original_price, String healthconsultant, String medicalexpert, String medicalgreen, int ceefax, int video, Double discount, long effectivetime, int sort, long health_consultant_ceefax, long health_consultant_phone, long medical_expert_ceefax, long medical_expert_phone, long medical_expert_video) {
        String sql = " update vip_card " +
                "    set vipcardno              = ?, " +
                "    vipcardname                = ?, " +
                "    price                      = ?, " +
                "    renewal_fee                = ?, " +
                "    original_price                = ?, " +
                "    sort                       = ?, " +
                "    health_consultant          = ?, " +
                "    medical_expert             = ?, " +
                "    medical_green              = ?, " +
                "       health_consultant_ceefax=?, " +
                "       health_consultant_phone=?, " +
                "       medical_expert_ceefax=?, " +
                "       medical_expert_phone=?, " +
                "       medical_expert_video=?, " +
                "    video       = ?, " +
                "    ceefax          = ?, " +
                "    discount = ?, " +
                "    effective_time             = ?, " +
                "    health_consultant_discount = ?, " +
                "    medical_expert_discount = ? " +
                "where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(vipcardno);
        params.add(vipcardname);
        params.add(PriceUtil.addPrice(price));
        params.add(PriceUtil.addPrice(renewal_fee));
        params.add(PriceUtil.addPrice(original_price));
        params.add(sort);
        params.add(healthconsultant);
        params.add(medicalexpert);
        params.add(medicalgreen);
        params.add(health_consultant_ceefax);
        params.add(health_consultant_phone);
        params.add(medical_expert_ceefax);
        params.add(medical_expert_phone);
        params.add(medical_expert_video);
        params.add(video);
        params.add(ceefax);
        params.add(discount);
        params.add(effectivetime);
        params.add(healthconsultantdiscount);
        params.add(medicalexpertdiscount);
        params.add(id);
        return update(sql, params) > 0;
    }

    public Map<String, Object> getAnswerOrder(long orderid) {
        String sql = "select status from vip_order where id=?";
        return queryForMap(sql, orderid);
    }

    public Map<String, Object> getUserMember(long userId) {
        String sql = "select is_expire isexpire,level from user_member where ifnull(delflag,0)=0 and userid=? and is_enabled=1 ";
        return queryForMap(sql, userId);
    }

    public long getTime() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//设置起时间
        cal.add(Calendar.YEAR, 1);//增加一年
        return cal.getTimeInMillis();
    }

    public long getTimeMonth() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//设置起时间
        cal.add(Calendar.MONTH, 2);//增加2个月
        return cal.getTimeInMillis();
    }

    public long getTi(long date, int year) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);//设置起始时间
        cal.add(Calendar.YEAR, year);//增加year年
        return cal.getTimeInMillis();
    }

    public Map<String, Object> userVipDiscount(long userId) {
        String sql = " select id,health_consultant_ceefax   healthconsultantceefax, " +
                "       health_consultant_phone    healthconsultantphone, " +
                "       health_consultant_discount healthconsultantdiscount, " +
                "       medical_expert_ceefax      medicalexpertceefax, " +
                "       medical_expert_phone       medicalexpertphone, " +
                "       medical_expert_video       medicalexpertvideo, " +
                "       medical_expert_discount    medicalexpertdiscount, " +
                "       medical_green_num          medicalgreennum " +
                "from user_member " +
                "where ifnull(delflag, 0) = 0 " +
                "  and userid = ? ";
        return queryForMap(sql, userId);
    }

    public Map<String, Object> vipDiscount() {
        String sql = " select id,health_consultant_ceefax   healthconsultantceefax, " +
                "       health_consultant_phone    healthconsultantphone, " +
                "       health_consultant_discount healthconsultantdiscount, " +
                "       medical_expert_ceefax      medicalexpertceefax, " +
                "       medical_expert_phone       medicalexpertphone, " +
                "       medical_expert_video       medicalexpertvideo, " +
                "       medical_expert_discount    medicalexpertdiscount, " +
                "       medical_green_num          medicalgreennum " +
                "from vip_card " +
                "where ifnull(delflag, 0) = 0 " +
                "  order by sort desc limit 1 ";
        return queryForMap(sql);
    }

    //顾问图文次数
    public boolean updateHealthConsultantCeefax(long vipid) {
        String sql = " UPDATE user_member set health_consultant_ceefax=health_consultant_ceefax-1 where id=? ";
        return update(sql, vipid) > 0;
    }

    //专家图文次数
    public boolean updateMedicalExpertCeefax(long vipid) {
        String sql = " UPDATE user_member set medical_expert_ceefax=medical_expert_ceefax-1 where id=? ";
        return update(sql, vipid) > 0;
    }

    //顾问电话次数
    public boolean updateHealthConsultantPhone(long vipid) {
        String sql = " UPDATE user_member set health_consultant_phone=health_consultant_phone-1 where id=? ";
        return update(sql, vipid) > 0;
    }

    //专家电话次数
    public boolean updateMedicalExpertPhone(long vipid) {
        String sql = " UPDATE user_member set medical_expert_phone=medical_expert_phone-1 where id=? ";
        return update(sql, vipid) > 0;
    }

    //专家电话次数
    public boolean updateMedicalExpertVideo(long vipid) {
        String sql = " UPDATE user_member set medical_expert_video=medical_expert_video-1 where id=? ";
        return update(sql, vipid) > 0;
    }


    /**
     * 未首值的用户
     *
     * @return
     */
    public List<Map<String, Object>> userlistDropdownBox() {
        String sql = " select ua.id,ua.name  from user_account ua  left join user_member um on um.userid = ua.id and ifnull(um.delflag,0) = 0 " +
                "                 where (um.is_enabled = 0 or IFNULL(um.id,0) = 0) and ifnull(ua.delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();

        return queryForList(sql, params);
    }


    public List<Map<String, Object>> viplistDropdownBox() {
        String sql = "select id ,vipcardname name from vip_card where ifnull(delflag,0)=0 order by sort desc ";

        return queryForList(sql);
    }

    public Map<String, Object> getUserMembers(long id) {//s
        String sql = "select um.userid id,ua.name ,um.vipcardid,vc.vipcardname from user_member um left join user_account ua on ua.id=um.userid left join vip_card vc on um.vipcardid=vc.id where um.id=?";
        return queryForMap(sql, id);
    }

    public Map<String, Object> findCardById(long cardid) {
        String sql = "select effective_time from vip_card where id=?";
        return queryForMap(sql, cardid);
    }

}
