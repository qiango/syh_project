package com.syhdoctor.webserver.mapper.doctor;

import com.syhdoctor.common.utils.EnumUtils.MoneyTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.PayTypeEnum;
import com.syhdoctor.common.utils.EnumUtils.TransactionTypeStateEnum;
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
public class DoctorManageMapper extends DoctorBaseMapper {

    /**
     * 医生账户管理查询
     *
     * @param indoccode
     * @param docname
     * @param dootel
     * @param begintime
     * @param endtime
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getDoctorManageList(String indoccode, String docname, String dootel, long begintime, long endtime, int pageIndex, int pageSize) {
        String sql = " select di.doctorid doctorid, in_doc_code indoccode, doc_name docname, doo_tel dootel, de.create_time createtime, de.walletbalance walletbalance, de.integral integral " +
                " from doctor_info di " +
                "       left join doctor_extends de on di.doctorid = de.doctorid and ifnull(de.delflag, 0) = 0 " +
                " where ifnull(di.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(indoccode)) {
            sql += " and di.in_doc_code like ? ";
            params.add(String.format("%%%S%%", indoccode));
        }
        if (!StrUtil.isEmpty(docname)) {
            sql += " and di.doc_name like ? ";
            params.add(String.format("%%%S%%",docname));
        }
        if (!StrUtil.isEmpty(dootel)) {
            sql += " and di.doo_tel like ? ";
            params.add(String.format("%%%S%%",dootel));
        }
        if (begintime != 0) {
            sql += " and di.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and di.create_time < ? ";
            params.add(endtime);
        }

        return query(pageSql(sql, " order by di.doctorid desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("paytypename", PayTypeEnum.getValue(ModelUtil.getInt(map, "paytype")).getMessage());
                map.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(map, "walletbalance")));
            }
            return map;
        });

    }


    /**
     * 导出
     * @param indoccode
     * @param docname
     * @param dootel
     * @param begintime
     * @param endtime
     * @return
     */
    public List<Map<String, Object>> getDoctorExportListAll(String indoccode, String docname, String dootel, long begintime, long endtime) {
        String sql = " select di.doctorid doctorid, di.in_doc_code indoccode, di.doc_name docname, di.doo_tel dootel, de.create_time createtime, de.walletbalance walletbalance , de.integral integral " +
                " from doctor_info di " +
                "       left join doctor_extends de on di.doctorid = de.doctorid and ifnull(de.delflag, 0) = 0 " +
                " where ifnull(di.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(indoccode)) {
            sql += " and id.in_doc_code = ? ";
            params.add(indoccode);
        }
        if (!StrUtil.isEmpty(docname)) {
            sql += " and id.doc_name like ? ";
            params.add(String.format("%%%S%%",docname));
        }
        if (!StrUtil.isEmpty(dootel)) {
            sql += " and id.doo_tel like ? ";
            params.add(String.format("%%%S%%",dootel));
        }
        if (begintime != 0) {
            sql += " and di.create_time > ? ";
            params.add(begintime);
        }
        if (endtime != 0) {
            sql += " and di.create_time < ? ";
            params.add(endtime);
        }
        List<Map<String, Object>> list = queryForList(sql, params);
        for (Map<String, Object> map : list) {
            if (map != null) {
                map.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(map, ".walletbalance")));
                map.put("createtime", UnixUtil.getDate(ModelUtil.getLong(map, "createtime"), "yyyy-MM-dd HH:mm:ss"));
            }
        }
        return list;


    }


    /**
     * 行数
     *
     * @param indoccode
     * @param docname
     * @param dootel
     * @param begintime
     * @param endtime
     * @return
     */
    public long getDoctorManageCount(String indoccode, String docname, String dootel, long begintime, long endtime) {
        String sql = " select count(doctorid) count from doctor_info di where ifnull(di.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(indoccode)) {
            sql += " and in_doc_code = ? ";
            params.add(indoccode);
        }
        if (!StrUtil.isEmpty(docname)) {
            sql += " and doc_name like ? ";
            params.add(String.format("%%%S%%",docname));
        }
        if (!StrUtil.isEmpty(dootel)) {
            sql += " and doo_tel like ? ";
            params.add(String.format("%%%S%%",dootel));
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
     * 医生账户详情
     *
     * @param doctorid
     * @return
     */
    public Map<String, Object> getDoctorinfoId(long doctorid) {
        String sql = " select di.in_doc_code indoccode , di.create_time createtime, di.doc_name docname, di.doo_tel dootel, de.walletbalance , de.integral   " +
                "         from doctor_info di  " +
                "         left join doctor_extends de on di.doctorid = de.doctorid and ifnull(de.delflag, 0) = 0 " +
                "         where di.doctorid = ? " +
                "         and ifnull(di.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        Map<String, Object> map = queryForMap(sql, params);
        if (map != null) {
            map.put("walletbalance", PriceUtil.findPrice(ModelUtil.getLong(map, "walletbalance")));
        }
        return map;
    }

    /**
     * 医生账户详情列表
     *
     * @param doctorid
     * @param moneyflag
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getDoctorinfoListId(long doctorid, int moneyflag, int pageIndex, int pageSize) {
        String sql = " select id, moneyflag, transactionmoney, transactiontype, create_time  createtime " +
                "    from doctor_transaction_record  " +
                "    where ifnull(delflag, 0) = 0 and doctorid = ?  ";
        List<Object> params = new ArrayList<>();
        params.add(doctorid);
        if (moneyflag != 0) {
            sql += " and moneyflag = ? ";
            params.add(moneyflag);
        }
        return query(pageSql(sql, " order by id desc "), pageParams(params, pageIndex, pageSize), (ResultSet re, int num) -> {
            Map<String, Object> map = resultToMap(re);
            if (map != null) {
                map.put("moneyflag", MoneyTypeEnum.getValue(ModelUtil.getInt(map, "moneyflag")).getMessage());
                map.put("transactionmoney",PriceUtil.findPrice(ModelUtil.getLong(map,"transactionmoney")));
                map.put("transactiontype", TransactionTypeStateEnum.getValue(ModelUtil.getInt(map, "transactiontype")).getMessage());
            }
            return map;
        });
    }

    public long getDoctorinfoListCount(long doctorid, int moneyflag) {
        String sql = " select  count(dtr.id)  from  doctor_transaction_record dtr  where ifnull(dtr.delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (doctorid != 0) {
            sql += " and dtr.doctorid = ? ";
            params.add(doctorid);
        }
        if (moneyflag >= 0) {
            sql += " and dtr.moneyflag = ?  ";
            params.add(moneyflag);
        }

        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);

    }


}
