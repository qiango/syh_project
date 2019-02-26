package com.syhdoctor.webserver.mapper.reflect;

import com.syhdoctor.common.utils.EnumUtils.ExtractOrderStateEnum;
import com.syhdoctor.common.utils.EnumUtils.PayTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class DoctorExtractOrderBaseMapper extends BaseMapper {

    /**
     * 提现记录查询
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
    public List<Map<String, Object>> getDoctorExtractOrderList(String dootel,long doctorid, String docname, int status, long begintime, long endtime, int pageIndex, int pageSize) {
        String sql = "select deo.id id, " +
                "       deo.orderno orderno, " +
                "       di.in_doc_code indoccode, " +
                "       di.doc_name docname, " +
                "       di.doo_tel dootel, " +
                "       deo.create_time examinetime, " +
                "       deo.amountmoney amountmoney, " +
                "       deo.status status, " +
                "       deo.examine_time paytime," +
                "       dbc.number, " +
                "       dbc.bankname," +
                "       deo.failreason " +
                " from doctor_extract_order deo " +
                "       left join doctor_info di on deo.doctorid = di.doctorid and ifnull(di.delflag, 0) = 0 " +
                "       left join doctor_bank_card dbc on dbc.id = deo.cardid and ifnull(dbc.delflag, 0) = 0 " +
                " where ifnull(deo.delflag, 0) = 0 and status in (1,2,3) ";
        List<Object> params = new ArrayList<>();
        if (doctorid != 0) {
            sql += " and deo.in_doc_code like ? ";
            params.add(String.format("%%%S%%", doctorid));
        }
        if (!StrUtil.isEmpty(docname)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%S%%", docname));
        }
        if(!StrUtil.isEmpty(dootel)){
            sql += " and di.doo_tel like ? ";
            params.add(String.format("%%%S%%",dootel));
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
                //map.put("status", ExtractOrderStateEnum.getValue(ModelUtil.getInt(map, "status")).getMessage());
                map.put("amountmoney", PriceUtil.findPrice(ModelUtil.getLong(map, "amountmoney")));
                //map.put("paytimename", UnixUtil.getDate(ModelUtil.getLong(map, "paytime"), "yyyy-MM-dd HH:mm:ss"));
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
    public long getDoctorExtractOrderCount(long doctorid, String docname, int status, long begintime, long endtime) {
        String sql = " select count(deo.id) count " +
                " from doctor_extract_order deo " +
                "       left join doctor_info di on deo.doctorid = di.doctorid and ifnull(di.delflag, 0) = 0 " +
                " where ifnull(deo.delflag, 0) = 0 and status in (1,2,3) ";
        List<Object> params = new ArrayList<>();
        if (doctorid != 0) {
            sql += " and di.doctorid = ? ";
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
     * 审核成功 日志
     *
     * @param id
     * @param status
     * @param createuser
     * @return
     */
    public boolean addExamine(long id, int status, long createuser) {
        String sql = " insert into audit_record (orderid, status, create_time, create_user) " +
                " values (?, ?, ?, ?) ";
        List<Object> params = new ArrayList<>();
        params.add(id);
        params.add(status);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(createuser);
        return insert(sql, params) > 0;
    }

    /**
     * 修改状态
     *
     * @param id
     * @param status
     * @return
     */
    public boolean updateStatus(long id, int status, String failreason) {
        String sql = " UPDATE doctor_extract_order set failreason=?,status=? where id=? and status = ? ";
        List<Object> params = new ArrayList<>();
        params.add(failreason);
        params.add(status);
        params.add(id);
        params.add(1);
        return update(sql, params) > 0;
    }

    /**
     * 修改审核时间
     *
     * @param id
     * @return
     */
    public boolean updateExamineTime(long id) {
        String sql = " UPDATE doctor_extract_order set examine_time=? where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(UnixUtil.getNowTimeStamp());
        params.add(id);
        return update(sql, params) > 0;
    }

//    /**
//     * 修改打款时间
//     *
//     * @param id
//     * @return
//     */
//    public boolean updatePayTime(long id) {
//        String sql = " UPDATE doctor_extract_order set pay_time=? where  id = ? ";
//        List<Object> params = new ArrayList<>();
//        params.add(UnixUtil.getNowTimeStamp());
//        params.add(id);
//        return update(sql, params) > 0;
//    }


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
    public List<Map<String, Object>> getDoctorExtractOrderExportListAll(long doctorid, String docname, int status, long begintime, long endtime) {
        String sql = "select deo.id id, " +
                "       deo.orderno orderno, " +
                "       di.in_doc_code indoccode, " +
                "       di.doc_name docname, " +
                "       di.doo_tel dootel, " +
                "       deo.examine_time examinetime, " +
                "       deo.amountmoney amountmoney, " +
                "       deo.status status, " +
                "       deo.pay_time paytime, " +
                "       dbc.number, " +
                "       dbc.bankname, " +
                "       deo.failreason " +
                " from doctor_extract_order deo " +
                "       left join doctor_info di on deo.doctorid = di.doctorid and ifnull(di.delflag, 0) = 0 " +
                "       left join doctor_bank_card dbc on dbc.id = deo.cardid and ifnull(dbc.delflag, 0) = 0 " +
                " where ifnull(deo.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (doctorid != 0) {
            sql += " and deo.in_doc_code like ? ";
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
