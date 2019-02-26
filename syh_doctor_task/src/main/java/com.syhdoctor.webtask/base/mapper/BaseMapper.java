package com.syhdoctor.webtask.base.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.*;

public class BaseMapper {


    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected NamedParameterJdbcTemplate namedJdbcTemplate;

    public interface IMapperResult {
        Map<String, Object> result(Map<String, Object> value);
    }

    /**
     * 插入数据
     *
     * @param sql    sql语句
     * @param params sql执行参数
     * @return long 执行条数
     */
    protected long insert(String sql, List<Object> params) {
        log.info("mapper insert sql > " + sql);
        if (params.size() > 0) {
            log.info("mapper insert params > " + params.toString());
        }
        int number = jdbcTemplate.update(sql, params.toArray());
        log.info(" mapper insert number >" + number);
        return number;
    }

    /**
     * @param sql    sql语句
     * @param params sql执行参数
     * @param idName id列名
     * @return long 插入数据的id
     */
    protected long insert(String sql, List<Object> params, String idName) {
        log.info("mapper insert sql > " + sql);
        if (params.size() > 0) {
            log.info("mapper insert params > " + params.toString());
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((Connection con) -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{idName});
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            return ps;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        log.info(" mapper insert id >" + id);
        return id;
    }

    /**
     * 查询结果集转Map
     *
     * @param rs 源数据
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    protected Map<String, Object> resultToMap(ResultSet rs) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            String key = rsmd.getColumnLabel(i + 1);
            result.put(key, rs.getObject(key));
        }
        return result;
    }

    /**
     * 获取增长id
     *
     * @param tableName 表明
     * @return long
     */
    protected long getId(String tableName) {
        Long id = jdbcTemplate.queryForObject("SELECT sys_fun_seq(?)", Long.class, tableName);
        log.info("mapper getId tableName = " + tableName + " | returnValue = " + id);
        return id == null ? 0 : id;
    }


    /**
     * 给sql语句拼接分页
     *
     * @param sql     sql语句
     * @param orderby 排序语句
     * @return java.lang.String
     */
    protected String pageSql(String sql, String orderby) {
        return sql + " " + orderby + " LIMIT ?,?";
    }

    /**
     * 给sql参数拼接分页参数
     *
     * @param params 原始参数
     * @param index  页码
     * @param size   数据长度
     * @return java.com.syhdoctor.webserver.api.util.List
     */
    protected List<Object> pageParams(List<Object> params, int index, int size) {
        int startIndex = (index - 1) * size;
        params.add(startIndex);
        params.add(size);
        return params;
    }


    protected Map<String, Object> pageParams(Map<String, Object> value, int index, int size) {
        int startIndex = index * size;
        value.put("startindex", startIndex);
        value.put("size", startIndex + size);
        return value;
    }

    /**
     * 执行插入或者更新sql语句
     *
     * @param sql  SQL语句
     * @param args sql参数
     * @return long
     */
    protected long update(String sql, Object... args) {
        if (args != null && args.length > 0) {
            return update(sql, Arrays.asList(args));
        } else {
            return update(sql, new ArrayList<>());
        }
    }

    /**
     * 执行插入或者更新sql语句
     *
     * @param sql    SQL语句
     * @param params sql参数
     * @return long
     */
    protected long update(String sql, List<Object> params) {
        log.info("mapper update sql > " + sql);
        if (params == null) {
            int number = jdbcTemplate.update(sql);
            log.info(" mapper insert number >" + number);
            return number;
        } else {
            log.info("mapper update params > " + params.toString());
            int number = jdbcTemplate.update(sql, params.toArray());
            log.info(" mapper insert number >" + number);
            return number;
        }
    }


//    protected int[] batchUpdate(String sql, List<Object> params) {
//        return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                ps.setObject(i + 1, params.get(i));
//            }
//
//            @Override
//            public int getBatchSize() {
//                return params.size();
//            }
//        });
//    }

    /**
     * 查询数据列表
     *
     * @param sql SQL语句
     * @return java.com.syhdoctor.webserver.api.util.List
     */
    protected List<Map<String, Object>> queryForList(String sql) {
        return queryForList(sql, new ArrayList<>());
    }

    /**
     * 查询数据列表
     *
     * @param sql  SQL语句
     * @param args SQL参数
     * @return java.com.syhdoctor.webserver.api.util.List
     */
    protected List<Map<String, Object>> queryForList(String sql, Object... args) {
        if (args != null && args.length > 0) {
            return queryForList(sql, Arrays.asList(args));
        } else {
            return queryForList(sql, new ArrayList<>());
        }
    }

    /**
     * 查询数据列表
     *
     * @param sql    SQL语句
     * @param params SQL参数
     * @return java.com.syhdoctor.webserver.api.util.List
     */
    protected List<Map<String, Object>> queryForList(String sql, List<Object> params) {
        log.info("mapper query sql > " + sql);
        if (params == null) {
            List<Map<String, Object>> datas = jdbcTemplate.queryForList(sql);
            if (datas.size() > 0) {
                log.info("mapper query result > " + datas.toString());
            }
            return datas;
        } else {
            List<Map<String, Object>> datas = jdbcTemplate.queryForList(sql, params.toArray());
            if (params.size() > 0) {
                log.info("mapper query params > " + params.toString());
            }
            if (datas.size() > 0) {
                log.info("mapper query result > " + datas.toString());
            }
            return datas;
        }
    }

    /**
     * 查询数据列表
     *
     * @param sql    SQL语句
     * @param params SQL参数
     * @return java.com.syhdoctor.webserver.api.util.List
     */
    protected List<Map<String, Object>> queryForList(String sql, Map<String, Object> params) {
        log.info("mapper query sql > " + sql);
        if (params.size() > 0) {
            log.info("mapper query params > " + params.toString());
        }
        List<Map<String, Object>> datas = namedJdbcTemplate.queryForList(sql, params);
        if (datas.size() > 0) {
            log.info("mapper query result > " + params.toString());
        }
        return datas;
    }

    /**
     * 查询单行数据
     *
     * @param sql  SQL语句
     * @param args SQL参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    protected Map<String, Object> queryForMap(String sql, Object... args) {
        if (args != null && args.length > 0) {
            return queryForMap(sql, Arrays.asList(args));
        } else {
            return queryForMap(sql, new ArrayList<>());
        }
    }

    /**
     * 查询单行数据
     *
     * @param sql    SQL语句
     * @param params SQL参数
     * @return java.com.syhdoctor.webserver.api.util.Map
     */
    protected Map<String, Object> queryForMap(String sql, List<Object> params) {
        List<Map<String, Object>> temp = queryForList(sql, params);
        if (temp != null && temp.size() > 0) {
            return temp.get(0);
        } else {
            return null;
        }
    }

    /**
     * 查询数据列表（自行处理每行数据）
     *
     * @param sql       SQL语句
     * @param params    SQL参数
     * @param rowMapper 行数据回掉对象
     * @return java.com.syhdoctor.webserver.api.util.List
     */
    protected List<Map<String, Object>> query(String sql, List<Object> params, RowMapper<Map<String, Object>> rowMapper) {
        if (params == null) {
            params = new ArrayList<>();
        }
        log.info("mapper query sql > " + sql);
        if (params.size() > 0) {
            log.info("mapper query params > " + params.toString());
        }
        List<Map<String, Object>> datas = jdbcTemplate.query(sql, params.toArray(), rowMapper);
        if (datas.size() > 0) {
            log.info("mapper query result > " + datas.toString());
        }
        return datas;
    }
}
