package com.syhdoctor.webserver.mapper.code;

import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.common.utils.UnixUtil;
import com.syhdoctor.webserver.base.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class CodeBaseMapper extends BaseMapper {
    /**
     * 疾病编码GB-95
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getListDiseaseCodeGB95(int pageIndex, int pageSize) {
        String sql = " select id,code,value from code_disease_code_ICD10 where ifnull(parentid,0)=0 and ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        return queryForList(pageSql(sql, " ORDER BY RAND() "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 性别
     *
     * @return
     */
    public List<Map<String, Object>> getGenderList() {
        String sql = " select id,value as name from code_gender where ifnull(delflag,0)=0 ";
        return queryForList(sql);
    }

    /**
     * 性别
     *
     * @return
     */
    public Map<String, Object> getGender(String code) {
        String sql = " select id,value as name from code_gender where ifnull(delflag,0)=0 and code=? ";
        return queryForMap(sql, code);
    }

    /**
     * 保险类别
     *
     * @return
     */
    public Map<String, Object> getInsuranceCategory(String code) {
        String sql = " select id,value as name from code_insurance_category where ifnull(delflag,0)=0 and code=? ";
        return queryForMap(sql, code);
    }

    /**
     * 科室
     *
     * @return
     */
    public Map<String, Object> getDepartment(long id) {
        String sql = " select id ,value as name from code_department where  ifnull(delflag,0)=0 and id = ? ";
        return queryForMap(sql, id);
    }

    /**
     * 科室
     *
     * @return
     */
    public List<Map<String, Object>> getDepartmentList(String name, int pageIndex, int pageSize) {
        String sql = " select id ,value as name from code_department where  ifnull(delflag,0)=0 and parentid = 0";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and value like ? ";
            params.add(String.format("%%%s%%", name));
        }
        return queryForList(pageSql(sql, " order by create_time "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 科室
     *
     * @return
     */
    public List<Map<String, Object>> getAllDepartmentList(String name) {
        String sql = " select cd1.id, cd1.value title,cd1.parentid pid, group_concat(cd2.value) name,cd1.delflag " +
                "from code_department cd1 " +
                "       left join code_department cd2 on cd2.code like concat(cd1.code, '%') " +
                "group by cd1.id having ifnull(cd1.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and name like ?  ";
            params.add(String.format("%%%s%%", name));
        }
        sql += " order by cd1.id ";
        return queryForList(sql, params);
    }

    public List<Map<String, Object>> getAllDepartmentLists(String name) {
        String sql = " select cd1.id, cd1.value ,cd1.parentid pid, group_concat(cd2.value) name,cd1.delflag " +
                "from code_department cd1 " +
                "       left join code_department cd2 on cd2.code like concat(cd1.code, '%') " +
                "group by cd1.id having ifnull(cd1.delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and name like ?  ";
            params.add(String.format("%%%s%%", name));
        }
        sql += " order by cd1.id ";
        return queryForList(sql, params);
    }


    //科室类型
    public List<Map<String, Object>> getTypeList(long departId) {
        String sql = "SELECT  " +
                " cd.id,  " +
                " cd.code,  " +
                " cd.value,  " +
                " cs.name,  " +
                " cs.id typeid " +
                "FROM  " +
                " middle_department_symptoms_type md  " +
                " LEFT JOIN common_disease_symptoms_type cs ON md.typeid = cs.id  " +
                " LEFT JOIN code_department cd ON md.departmentid = cd.id   " +
                "WHERE  " +
                " IFNULL(md.delflag, 0 ) = 0 and  md.departmentid=? ";
        return queryForList(sql, departId);
    }

    public List<Map<String, Object>> getDepartChild(long departId) {
        String sql = "select id,value from code_department where parentid=? and ifnull(delflag,0)=0";
        return queryForList(sql, departId);
    }

    //所有类型
    public List<Map<String, Object>> getTypeAll() {
        String sql = "select id,name from common_disease_symptoms_type where ifnull(delflag,0)=0 order by sort desc";
        return queryForList(sql);
    }

    public List<Map<String, Object>> getDrugsList(String name, int pageIndex, int pageSize) {
        String sql = " select id, " +
                "       code, " +
                "       value             name, " +
                "       standard_desc     standarddesc, " +
                "       total_dosage_unit totaldosageunit, " +
                "       unit, " +
                "       drug_form         drugform, " +
                "       drug_code         drugcode, " +
                "       durg_name         durgname " +
                " from code_drugs_bak " +
                " where ifnull(delflag, 0) = 0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and ( value like ? or value_pinyin like ? ) ";
            params.add(String.format("%%%s%%", name));
            params.add(String.format("%%%s%%", name));
        }
        return queryForList(pageSql(sql, " order by create_time "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 药品详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getDrugsListId(long id) {
        String sql = " select id, " +
                "       code, " +
                "       value             name, " +
                "       standard_desc     standarddesc, " +
                "       total_dosage_unit totaldosageunit, " +
                "       unit, " +
                "       drug_form         drugform, " +
                "       drug_form_id         drugformid, " +
                "       drug_code         drugcode, " +
                "       durg_name         durgname " +
                " from code_drugs_bak " +
                " where ifnull(delflag, 0) = 0 and id =? ";
        return queryForMap(sql, id);
    }

    public Map<String, Object> codeDrugsDosageListId(int code) {
        String sql = " select code,value from code_drugs_dosage where ifnull(delflag, 0) = 0 and code=?  ";
        return queryForMap(sql, code);
    }

    public List<Map<String, Object>> codeDrugsDosageList() {
        String sql = " select code id,value name from code_drugs_dosage where ifnull(delflag, 0) = 0  ";
        return queryForList(sql);
    }


    /**
     * 修改药品
     *
     * @param id
     * @param drugformid
     * @param drugform
     * @param drugcode
     * @param durgname
     * @return
     */
    public boolean updateDrugs(long id, int drugformid, String drugform, int drugcode, String durgname) {
        String sql = " update code_drugs_bak " +
                " set drug_form_id = ?, " +
                "    drug_form    = ?, " +
                "    drug_code    = ?, " +
                "    durg_name    = ? " +
                " where id = ? ";
        List<Object> params = new ArrayList<>();
        params.add(drugformid);
        params.add(drugform);
        params.add(drugcode);
        params.add(durgname);
        params.add(id);
        return update(sql, params) > 0;
    }


    public long getDrugsCount(String name) {
        String sql = " select count(id) count from code_drugs_bak where ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and ( value like ? or value_pinyin like ? ) ";
            params.add(String.format("%%%s%%", name));
            params.add(String.format("%%%s%%", name));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public List<Map<String, Object>> getFrequencyList() {
        String sql = " select id,value from code_drug_frequency  order by id ";
        return queryForList(sql);
    }

    public Map<String, Object> getDrugs(long id) {
        String sql = " select id,code,value appdrugname,drug_form,standard_desc standarddesc,total_dosage_unit totaldosageunit,unit,manufacturing_enterprise,bidding_enterprise,catalog_category,procurement_category from code_drugs_bak where ifnull(delflag,0)=0 and id=? ";
        return queryForMap(sql, id);
    }

    public Map<String, Object> getFrequency(long id) {
        String sql = " select id,code,value name from code_drug_frequency where ifnull(delflag,0)=0 and id=? ";
        return queryForMap(sql, id);
    }

    public Map<String, Object> getDiseasesType(long id) {
        String sql = " select id,code,value name from code_disease_type where ifnull(delflag,0)=0 and id=? ";
        return queryForMap(sql, id);
    }

    /**
     * 录地区数据
     *
     * @param anInt
     * @param str
     * @param anInt1
     */
    public void adddiqu(int anInt, String str, int anInt1) {
        String sql = " insert into code_area(code,value,parentid,delflag,create_time)values(?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(anInt);
        params.add(str);
        params.add(anInt1);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        insert(sql, params);
    }

    public void adddrug(String code, String value, String drug_form, String dosage, String value_pinyin, String specifications, String unit, String manufacturing_enterprise, String bidding_enterprise, String catalog_category, String procurement_category) {
        String sql = "insert into code_drugs_bak (code, " +
                "                            value, " +
                "                            drug_form, " +
                "                            standard_desc, " +
                "                            value_pinyin, " +
                "                            total_dosage_unit, " +
                "                            unit, " +
                "                            manufacturing_enterprise, " +
                "                            bidding_enterprise, " +
                "                            catalog_category, " +
                "                            procurement_category, " +
                "                            delflag, " +
                "                            create_time) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(code);
        params.add(value);
        params.add(drug_form);
        params.add(dosage);
        params.add(value_pinyin);
        params.add(specifications);
        params.add(unit);
        params.add(manufacturing_enterprise);
        params.add(bidding_enterprise);
        params.add(catalog_category);
        params.add(procurement_category);
        params.add(0);
        params.add(UnixUtil.getNowTimeStamp());
        insert(sql, params);
    }

    /**
     * 根据pid查找地区
     *
     * @param code
     * @return
     */
    public List<Map<String, Object>> getAreaByParentId(int code) {
        String sql = " select code id,value from code_area where ifnull(delflag,0)=0 and parentid=? ";
        return queryForList(sql, code);
    }

    /**
     * 根据code查找地区
     *
     * @param code
     * @return
     */
    public Map<String, Object> getArea(int code) {
        String sql = " select code id,value from code_area where ifnull(delflag,0)=0 and code=? ";
        return queryForMap(sql, code);
    }

    /**
     * 根据codes查找地区
     *
     * @param codes
     * @return
     */
    public Map<String, Object> getArea(String codes) {
        String sql = " select CONCAT(pr.value, ci.value, di.value) areas " +
                "from code_area pr " +
                "       left join code_area ci on ci.code = substring_index(substring_index(?, ',', 2), ',', -1) " +
                "       left join code_area di on di.code = substring_index(?, ',', -1) " +
                "where pr.code = substring_index(?, ',', 1) ";
        List<Object> params = new ArrayList<>();
        params.add(codes);
        params.add(codes);
        params.add(codes);
        return queryForMap(sql, params);
    }

    /**
     * 根据codes查找地区
     *
     * @param value
     * @return
     */
    public Map<String, Object> getAreaByValue(String value) {
        String sql = " select code id,code,value from code_area where value=? ";
        List<Object> params = new ArrayList<>();
        params.add(value);
        return queryForMap(sql, params);
    }

    public boolean updateDrugs(long id, String pinyin) {
        String sql = " update code_drugs_bak set value_pinyin=? where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(pinyin);
        params.add(id);
        return update(sql, params) > 0;
    }

    /**
     * 职称列表
     *
     * @param name
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getTitleList(String name, int pageIndex, int pageSize) {
        String sql = " select id,code,value name from code_doctor_title where ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and value like ? ";
            params.add(String.format("%%%s%%", name));
        }
        return queryForList(pageSql(sql, " order by sort desc "), pageParams(params, pageIndex, pageSize));
    }

    /**
     * 职称数量
     *
     * @param name
     * @return
     */
    public long getTitleCount(String name) {
        String sql = " select count(id) count from code_doctor_title where ifnull(delflag,0)=0 ";
        List<Object> params = new ArrayList<>();
        if (!StrUtil.isEmpty(name)) {
            sql += " and value like ? ";
            params.add(String.format("%%%s%%", name));
        }
        return jdbcTemplate.queryForObject(sql, params.toArray(), long.class);
    }

    public boolean updateDrugs(long id, String drug_form, String standard_desc, String totaldosage_unit, String unit, String manufacturing_enterprise, String bidding_enterprise, String catalog_category, String procurement_category, long userId) {
        String sql = " update code_drugs_bak set  " +
                "                            standard_desc=?, " +
                "                            total_dosage_unit=?, " +
                "                            unit=?, " +
                "                            manufacturing_enterprise=?, " +
                "                            bidding_enterprise=?, " +
                "                            catalog_category=?, " +
                "                            procurement_category=?, " +
                "                            modify_time=?, " +
                "                            modify_user=?, " +
                "                            drug_form=? " +
                "where id=? ";
        List<Object> params = new ArrayList<>();
        params.add(standard_desc);
        params.add(totaldosage_unit);
        params.add(unit);
        params.add(manufacturing_enterprise);
        params.add(bidding_enterprise);
        params.add(catalog_category);
        params.add(procurement_category);
        params.add(UnixUtil.getNowTimeStamp());
        params.add(userId);
        params.add(drug_form);
        params.add(id);
        return update(sql, params) > 0;
    }

    public Map<String, Object> getTitle(long id) {
        String sql = " select id,value name from code_doctor_title where ifnull(delflag,0)=0 and id=? ";
        return queryForMap(sql, id);
    }
}
