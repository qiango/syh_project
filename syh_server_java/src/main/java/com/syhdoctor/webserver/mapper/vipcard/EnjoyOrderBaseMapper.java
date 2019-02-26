package com.syhdoctor.webserver.mapper.vipcard;

import com.syhdoctor.common.utils.EnumUtils.PayTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class EnjoyOrderBaseMapper extends BaseMapper {

    public List<Map<String, Object>> getEnjoyOrder(String name, String phone, long begintime, long endtime, int pageIndex, int pageSize) {
        String sql = " SELECT vo.id, vo.orderno, ua.name, ua.phone, vo.price, vo.pay_type paytype, vo.create_time createtime,vo.operate_mode operatemode " +
                " FROM vip_order vo " +
                "       left join user_account ua on ua.id = vo.userid and IFNULL(ua.delflag, 0) = 0 " +
                " where IFNULL(vo.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();

        if (!StrUtil.isEmpty(name)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and ua.phone like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        if (begintime != 0) {
            sql += " and vo.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and vo.create_time < ? ";
            params.add(endtime);
        }
        return query(pageSql(sql, " order by vo.id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("paytype", PayTypeEnum.getValue(ModelUtil.getInt(map, "paytype")).getMessage());
                map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
                map.put("operatemode",ModelUtil.getInt(map,"operatemode")==1?"后台充值":"前台充值");
            }
            return map;
        });
    }


    public long getEnjoyOrderCount(String name, String phone, long begintime, long endtime) {
        String sql = " SELECT count(vo.id) count " +
                "  FROM vip_order vo  " +
                "  left join user_account ua on ua.id = vo.id and IFNULL(ua.delflag, 0) = 0  " +
                "  where IFNULL(vo.delflag, 0) = 0  ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and ua.phone like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        if (begintime != 0) {
            sql += " and vo.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and vo.create_time < ? ";
            params.add(endtime);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public Map<String, Object> getEnjoyOrderId(long id) {
        String sql = " SELECT vo.orderno, vo.create_time createtime, ua.name, ua.gender, ua.phone, vo.price, vo.pay_type paytype " +
                "   FROM vip_order vo " +
                "   left join user_account ua on ua.id = vo.id and IFNULL(ua.delflag, 0) = 0 " +
                "   where IFNULL(vo.delflag, 0) = 0 " +
                "   and vo.id = ?  ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        Map<String, Object> map = queryForMap(sql, params);
        if (map != null) {
            map.put("paytype", PayTypeEnum.getValue(ModelUtil.getInt(map, "patype")).getMessage());
            map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
        }
        return map;
    }


    /*
    尊享列表
     */
    public List<Map<String, Object>> getEnjoyList(String name, String phone, int level, long begintime, long endtime, int pageIndex, int pageSize) {
        String sql = " select um.id, ua.name, ua.userno, ua.phone, um.level, um.current_integral currentintegral, um.vip_expiry_time vipexpirytime,um.is_expire isexpire " +
                " from user_member um " +
                "       left join user_account ua on ua.id = um.userid and IFNULL(ua.delflag, 0) = 0 " +
                " where ifnull(um.delflag, 0) = 0 and um.is_enabled = ? ";
        List<Object> params = new ArrayList<>();
        params.add(1);
        if (!StrUtil.isEmpty(name)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and ua.phone like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        if (level != 0) {
            sql += " and um.level = ? ";
            params.add(level);
        }
        if (begintime != 0) {
            sql += " and um.vip_expiry_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and um.vip_expiry_time < ? ";
            params.add(endtime);
        }
        return query(pageSql(sql, " order by um.id asc "), pageParams(params, pageIndex, pageSize),(ResultSet re, int num) ->{
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("isexpire",ModelUtil.getInt(map,"isexpire")==1?"未到期":"已到期");
            }
            return map;
        });
    }


    public long getEnjoyListCount(String name, String phone, int level, long begintime, long endtime) {
        String sql = " select count(um.id) count " +
                " from user_member um " +
                "       left join user_account ua on ua.id = um.userid and IFNULL(ua.delflag, 0) = 0 " +
                " where ifnull(um.delflag, 0) = 0 and um.is_enabled = ? ";
        List<Object> params = new ArrayList<>();
        params.add(1);
        if (!StrUtil.isEmpty(name)) {
            sql += " and ua.name like ? ";
            params.add(String.format("%%%S%%", name));
        }
        if (!StrUtil.isEmpty(phone)) {
            sql += " and ua.phone like ? ";
            params.add(String.format("%%%S%%", phone));
        }
        if (level != 0) {
            sql += " and um.level = ? ";
            params.add(level);
        }
        if (begintime != 0) {
            sql += " and um.vip_expiry_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and um.vip_expiry_time < ? ";
            params.add(endtime);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    public Map<String, Object> getEnjoyListId(long id) {
        String sql = " select um.id, " +
                "    ua.name, " +
                "    ua.userno, " +
                "    ua.phone, " +
                "    um.level, " +
                "    um.current_integral           currentintegral, " +
                "    um.vip_expiry_time            vipexpirytime, " +
                "    ua.gender, " +
                "    um.medical_green_num          medicalgreennum, " +
                "    um.discount, " +
                "    um.ceefax, " +
                "    um.video, " +
                "    um.health_consultant_ceefax   healthconsultantceefax, " +
                "    um.health_consultant_phone    healthconsultantphone, " +
                "    um.medical_expert_ceefax      medicalexpertceefax, " +
                "    um.medical_expert_phone       medicalexpertphone, " +
                "    um.medical_expert_video       medicalexpertvideo, " +
                "    um.health_consultant_discount healthconsultantdiscount, " +
                "    um.medical_expert_discount    medicalexpertdiscount " +
                "    from user_member um " +
                "    left join user_account ua on ua.id = um.userid and IFNULL(ua.delflag, 0) = 0 " +
                "    where ifnull(um.delflag, 0) = 0 " +
                "    and um.id = ?  ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        Map<String, Object> map = queryForMap(sql, params);
        if(map != null){
            map.put("gender",ModelUtil.getInt(map,"gender")==1?"男":"女");
        }
        return map;
    }


    public boolean updateEnjoyList(int ceefax, int video, long id) {
        String sql = " update user_member set ceefax= ? ,video=? where id =  ? and IFNULL(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        params.add(ceefax);
        params.add(video);
        params.add(id);
        return update(sql, params) > 0;
    }


}
