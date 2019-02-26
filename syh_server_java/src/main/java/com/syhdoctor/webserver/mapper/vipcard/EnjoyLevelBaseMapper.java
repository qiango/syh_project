package com.syhdoctor.webserver.mapper.vipcard;


import com.syhdoctor.common.utils.EnumUtils.EnjoyTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import com.syhdoctor.webserver.exception.ServiceException;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class EnjoyLevelBaseMapper extends BaseMapper {


/*
等级
 */

    public List<Map<String, Object>> getLevelList(long id, int level, int pageIndex, int pageSize) {
        String sql = " select id,current_integral currentintegral,level from enjoy_level where ifnull(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and id = ? ";
            params.add(id);
        }
        if (level != 0) {
            sql += " and level = ? ";
            params.add(level);
        }
        return queryForList(pageSql(sql, " order by id desc "), pageParams(params, pageIndex, pageSize));
    }

    public long getLevelListCount(long id, int level) {
        String sql = " select count(id)count from enjoy_level where ifnull(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and id = ? ";
            params.add(id);
        }
        if (level != 0) {
            sql += " and level = ? ";
            params.add(level);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public Map<String, Object> getLevelListId(long id) {
        String sql = " select id,current_integral currentintegral,level from enjoy_level where ifnull(delflag,0) = 0 and id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return queryForMap(sql, params);
    }

    public boolean updateLevel(long id, int level, int currentintegral) {
        String sql = " update enjoy_level set current_integral = ?,level=? where id = ?  and IFNULL(delflag,0) = 0  ";
        String sqllevel = " select level from enjoy_level where ifnull(delflag,0)=0 and level=? ";
        List<Object> params = new ArrayList<>();
        params.add(currentintegral);
        params.add(level);
        params.add(id);
        Map<String, Object> dj = queryForMap(sqllevel, level);
        if (null != dj) {
            throw new ServiceException("该等级已配置，请检查！");
        }
        return update(sql, params) > 0;
    }

    public boolean insertLevel(int level, int currentintegral) {
        String sql = " insert into enjoy_level(level,current_integral) values (?,?) ";
        String sqllevel = " select level from enjoy_level where  ifnull(delflag,0)=0 and level=? ";
        List<Object> params = new ArrayList<>();
        params.add(level);
        params.add(currentintegral);
        Map<String, Object> map = queryForMap(sqllevel, level);
        if (map != null) {
            throw new ServiceException("该等级已配置，请检查");
        }
        return insert(sql, params) > 0;
    }

    public boolean delLevel(long id) {
        String sql = " update enjoy_level set delflag = 1 where IFNULL(delflag,0) = 0 and id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }


    /*
    尊享值
     */
    public List<Map<String, Object>> getEnjoyValueList(long id, int pageIndex, int pageSize) {
        String sql = " select id,current_integral currentintegral,price from enjoy_value where ifnull(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and id = ? ";
            params.add(id);
        }
        return query(pageSql(sql, " order by id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
            }
            return map;
        });
    }

    public long getEnjoyValueListCount(long id) {
        String sql = " select count(id) count from enjoy_value where ifnull(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (id != 0) {
            sql += " and id = ? ";
            params.add(id);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public Map<String, Object> getEnjoyValueListId(long id) {
        String sql = " select id,current_integral currentintegral,price from enjoy_value where ifnull(delflag,0) = 0 and id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        Map<String, Object> map = queryForMap(sql, params);
        if (map != null) {
            map.put("price", PriceUtil.findPrice(ModelUtil.getLong(map, "price")));
        }
        return map;
    }


    public boolean updateEnjoyValue(long id, int currentintegral, BigDecimal price) {
        String sql = " update enjoy_value set current_integral = ?,price = ? where ifnull(delflag,0) = 0 and id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(currentintegral);
        params.add(PriceUtil.addPrice(price));
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean insertEnjoyValue(int currentintegral, BigDecimal price) {
        String sql = " insert into enjoy_value(current_integral,price) values(?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(currentintegral);
        params.add(PriceUtil.addPrice(price));
        return insert(sql, params) > 0;
    }

    public boolean delEnjoyValue(long id) {
        String sql = " update enjoy_value set delflag = 1 where ifnull(delflag,0) = 0 and id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }



    /*
    尊享类别
     */

    public List<Map<String, Object>> getEnjoyTypeList(int type, int pageIndex, int pageSize) {
        String sql = " select id,type,type_describe typedescribe from enjoy_type where ifnull(delflag,0) = 0  ";
        List<Object> params = new ArrayList<>();
        if (type != 0) {
            sql += " and type = ? ";
            params.add(type);
        }
        return query(pageSql(sql, " order by id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("type", EnjoyTypeEnum.getValue(ModelUtil.getInt(map, "type")).getMessage());
            }
            return map;
        });

    }

    public long getEnjoyTypeListCount(int type) {
        String sql = " select count(id) count from enjoy_type where ifnull(delflag,0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (type != 0) {
            sql += " and type = ? ";
            params.add(type);
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


    public Map<String, Object> getEnjoyTypeListId(long id) {
        String sql = " select id,type,type_describe typedescribe from enjoy_type where ifnull(delflag,0) = 0 and id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        Map<String, Object> map = queryForMap(sql, params);
        if (map != null) {
            map.put("type", EnjoyTypeEnum.getValue(ModelUtil.getInt(map, "type")).getMessage());
        }
        return map;
    }


    public boolean updateEnjoyType(long id, String typedescribe) {
        String sql = " update enjoy_type set type_describe = ? where ifnull(delflag,0) = 0 and id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(typedescribe);
        params.add(id);
        return update(sql, params) > 0;
    }

    public boolean insertEnjoyType(int type, String typedescribe) {
        String sql = " insert into enjoy_type(type,type_describe) values(?,?) ";
        List<Object> params = new ArrayList<>();
        params.add(type);
        params.add(typedescribe);
        return insert(sql, params) > 0;
    }

    public boolean delEnjoyType(long id) {
        String sql = " update enjoy_type set delflag = 1 where ifnull(delflag,0) = 0 and id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        return update(sql, params) > 0;
    }


}
