package com.syhdoctor.webserver.mapper.finance;

import com.syhdoctor.common.utils.EnumUtils.ExtractOrderStateEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class FinanceOrderBaseMapper extends BaseMapper {
    /**
     * 打款记录查询
     *
     * @param doctorid
     * @param docname
     * @param status
     * @param begintime
     * @param endtime
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getFinanceOrderList(long doctorid, String docname, int status, long begintime, long endtime, int pageIndex, int pageSize) {
        String sql = "select deo.id id, " +
                "       deo.orderno orderno, " +
                "       di.in_doc_code indoccode, " +
                "       di.doc_name docname, " +
                "       di.doo_tel dootel, " +
                "       deo.examine_time examinetime, " +
                "       deo.amountmoney amountmoney, " +
                "       deo.status, " +
                "       deo.failreason, " +
                "       deo.pay_time paytime, " +
                "       dbc.number, " +
                "       dbc.bankname " +
                " from doctor_extract_order deo " +
                "       left join doctor_info di on deo.doctorid = di.doctorid and ifnull(di.delflag, 0) = 0 " +
                "       left join doctor_bank_card dbc on dbc.id = deo.cardid and ifnull(dbc.delflag, 0) = 0 " +
                " where ifnull(deo.delflag, 0) = 0 and deo.status in (3,4,5) ";
        List<Object> params = new ArrayList<>();
        //params.add(3);
        if (doctorid != 0) {
            sql += " and di.in_doc_code like ? ";
            params.add(String.format("%%%S%%", doctorid));
        }
        if (!StrUtil.isEmpty(docname)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%S%%", docname));
        }
        if (status != 0) {
            sql += " and deo.status = ? ";
            params.add(status);
        }
        if (begintime != 0) {
            sql += " and deo.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and deo.create_time < ? ";
            params.add(endtime);
        }

        return query(pageSql(sql, " order by deo.status "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
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
     * @param doctorid
     * @param docname
     * @param status
     * @param begintime
     * @param endtime
     * @return
     */
    public long getFinanceOrderListCount(long doctorid, String docname, int status, long begintime, long endtime) {
        String sql = " select count(deo.id) count " +
                " from doctor_extract_order deo " +
                "       left join doctor_info di on deo.doctorid = di.doctorid and ifnull(di.delflag, 0) = 0 " +
                " where ifnull(deo.delflag, 0) = 0 and deo.status = ?  ";
        List<Object> params = new ArrayList<>();
        params.add(1);
        if (doctorid != 0) {
            sql += " and di.in_doc_code = ? ";
            params.add(doctorid);
        }
        if (!StrUtil.isEmpty(docname)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%S%%", docname));
        }
        if (status != 0) {
            sql += " and deo.status = ? ";
            params.add(status);
        }
        if (begintime != 0) {
            sql += " and deo.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and deo.create_time < ? ";
            params.add(endtime);
        }

        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    /**
     * 打款成功   日志
     *
     * @param id
     * @param status
     * @param createuser
     * @return
     */
    public boolean addRemittanceLog(long id, int status, long createuser) {
        String sql = " insert into audit_record (orderid, create_time, create_user ,status) " +
                " values (?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(createuser);
        params.add(status);
        return insert(sql, params) > 0;
    }

    /**
     * 修改打款状态
     *
     * @param id
     * @param status
     * @return
     */
    public boolean updateFundtype(long id, int status, String failreason) {
        String sql = " UPDATE doctor_extract_order set failreason=?,status=? where id=? and status=?  ";
        List<Object> params = new ArrayList<>();
        params.add(failreason);
        params.add(status);
        params.add(id);
        params.add(3);
        return update(sql, params) > 0;
    }


    /**
     * 修改打款时间
     *
     * @param id
     * @return
     */
    public boolean updatePayTime(long id) {
        String sql = " UPDATE doctor_extract_order set pay_time=? where  id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(id);
        return update(sql, params) > 0;
    }


    /**
     * 导出
     *
     * @param doctorid
     * @param docname
     * @param status
     * @param begintime
     * @param endtime
     * @return
     */
    public List<Map<String, Object>> getFinanceOrderListAll(long doctorid, String docname, int status, long begintime, long endtime) {
        String sql = "select deo.id id, " +
                "       deo.orderno orderno, " +
                "       di.in_doc_code indoccode, " +
                "       di.doc_name docname, " +
                "       di.doo_tel dootel, " +
                "       deo.examine_time examinetime, " +
                "       deo.amountmoney amountmoney, " +
                "       deo.status, " +
                "       deo.failreason, " +
                "       deo.pay_time paytime, " +
                "       dbc.number, " +
                "       dbc.bankname " +
                " from doctor_extract_order deo " +
                "       left join doctor_info di on deo.doctorid = di.doctorid and ifnull(di.delflag, 0) = 0 " +
                "       left join doctor_bank_card dbc on dbc.id = deo.cardid and ifnull(dbc.delflag, 0) = 0 " +
                " where ifnull(deo.delflag, 0) = 0 and deo.status in (3,4,5) ";
        List<Object> params = new ArrayList<>();
        if (doctorid != 0) {
            sql += " and di.in_doc_code like ? ";
            params.add(String.format("%%%S%%", doctorid));
        }
        if (!StrUtil.isEmpty(docname)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%S%%", docname));
        }
        if (status != 0) {
            sql += " and deo.status = ? ";
            params.add(status);
        }
        if (begintime != 0) {
            sql += " and deo.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and deo.create_time < ? ";
            params.add(endtime);
        }

        List<Map<String, Object>> list = queryForList(sql, params);
        for (Map<String, Object> map : list) {
            if (map != null) {
                map.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "amountmoney")));
                map.put("status", ExtractOrderStateEnum.getValue(ModelUtil.getInt(map, "status")).getMessage());
                map.put("examinetime", UnixUtil.getDate(ModelUtil.getLong(map, "examinetime"), "yyyy-MM-dd HH:mm:ss"));
                map.put("paytime", UnixUtil.getDate(ModelUtil.getLong(map, "paytime"), "yyyy-MM-dd HH:mm:ss"));
            }
        }

        return list;
    }

}
