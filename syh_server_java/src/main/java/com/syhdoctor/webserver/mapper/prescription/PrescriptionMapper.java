package com.syhdoctor.webserver.mapper.prescription;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PrescriptionMapper extends PrescriptionBaseMapper {


    /**
     * 一级
     *
     * @return
     */
    public List<Map<String, Object>> getDrugsPackageList() {
        String sql = " SELECT id value,name label FROM drugs_package where ifnull(delflag,0)=0 ";
        return queryForList(sql);
    }

    /**
     * 二级
     *
     * @param pidlist
     * @return
     */
    public Map<Long, List<Map<String, Object>>> getMiddleDrugsPackageList(List<Long> pidlist) {
        if (pidlist == null) {
            return null;
        }
        String sql = " select mdp.drugsid value, cd.value label,dp.id pid " +
                " from middle_drugs_package mdp " +
                "       left join drugs_package dp on dp.id = mdp.packageid and ifnull(dp.delflag, 0) = 0 " +
                "       left join code_drugs cd on cd.id = mdp.drugsid and ifnull(cd.delflag, 0) = 0 " +
                " where mdp.packageid in (:pid) ";
        Map<String, Object> params = new HashMap<>();
        params.put("pid", pidlist);
        Map<Long, List<Map<String, Object>>> mapfinal = new HashMap<>();
        List<Map<String, Object>> list = queryForList(sql, params);
        for (Map<String, Object> maps : list) {
            long pid = ModelUtil.getLong(maps, "pid");
            if (mapfinal.containsKey(pid)) {
                mapfinal.get(pid).add(maps);
            } else {
                List<Map<String, Object>> list1 = new ArrayList<>();
                list1.add(maps);
                mapfinal.put(pid, list1);
            }
        }
        return mapfinal;
    }

    /**
     * 子级药
     *
     * @param name
     * @param pid
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getMiddleDrugsList(String name, long pid, int pageIndex, int pageSize) {
        String sql = " select mdp.drugsid value, cdb.value label,cdb.standard_desc standarddesc,cdb.drug_form drugform " +
                " from middle_drugs_package mdp " +
                "       left join drugs_package dp on dp.id = mdp.packageid and ifnull(dp.delflag, 0) = 0 " +
                "       left join code_drugs_bak cdb on cdb.id = mdp.drugsid and ifnull(cdb.delflag, 0) = 0 " +
                " where 1=1 ";
        List<Object> params = new ArrayList<>();
        if (pid != 0) {
            sql += " and mdp.packageid =? ";
            params.add(pid);
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and  cdb.value like ? ";
            params.add(String.format("%%%S%%", name));
        }
        return queryForList(pageSql(sql, " order by mdp.drugsid desc "), pageParams(params, pageIndex, pageSize));
    }

    public long getMiddleDrugsListCount(String name, long pid) {
        String sql = " select count(mdp.drugsid) count " +
                " from middle_drugs_package mdp " +
                "       left join drugs_package dp on dp.id = mdp.packageid and ifnull(dp.delflag, 0) = 0 " +
                "       left join code_drugs cd on cd.id = mdp.drugsid and ifnull(cd.delflag, 0) = 0 " +
                " where 1=1";
        List<Object> params = new ArrayList<>();
        if (pid != 0) {
            sql += " and mdp.packageid =? ";
            params.add(pid);
        }
        if (!StrUtil.isEmpty(name)) {
            sql += " and  cd.value like ? ";
            params.add(String.format("%%%S%%", name));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }


}
