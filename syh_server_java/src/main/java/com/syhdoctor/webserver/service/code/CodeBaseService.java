package com.syhdoctor.webserver.service.code;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.Pinyin4jUtil;
import com.syhdoctor.common.utils.StrUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.code.CodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.syhdoctor.common.utils.ExcelUtil.readExcel;

@Service
public abstract class CodeBaseService extends BaseService {


    @Autowired
    private CodeMapper codeMapper;

    /**
     * 疾病编码gb-95字典
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getListDiseaseCodeGB95(int pageIndex, int pageSize) {
        return codeMapper.getListDiseaseCodeGB95(pageIndex, pageSize);
    }


    /**
     * 性别字典
     *
     * @return
     */
    public Map<String, Object> getGender(String code) {
        return codeMapper.getGender(code);
    }

    /**
     * 保险类别
     *
     * @return
     */
    public Map<String, Object> getInsuranceCategory(String code) {
        return codeMapper.getInsuranceCategory(code);
    }

    /**
     * 科室字典（只取了一级）
     *
     * @return
     */
    public List<Map<String, Object>> getDepartmentList(String name, int pageIndex, int pageSize) {
        return codeMapper.getDepartmentList(name, pageIndex, pageSize);
    }

    /**
     * 科室字典
     *
     * @return
     */
    public Map<String, Object> getDepartment(long id) {
        return codeMapper.getDepartment(id);
    }

    /**
     * 科室字典
     *
     * @return
     */
    public Map<String, Object> getTitle(long id) {
        return codeMapper.getTitle(id);
    }

    /**
     * 科室字典（全部）
     *
     * @return
     */
    public List<Map<String, Object>> getAllDepartmentList(String name) {
        List<Map<String, Object>> allDepartmentList = codeMapper.getAllDepartmentList(name);
        Map<Long, List<Map<String, Object>>> tempMap = new HashMap<>();
        for (Map<String, Object> temp : allDepartmentList) {
            Long pid = ModelUtil.getLong(temp, "pid", 0);
            departmentTree(temp, pid, tempMap);
        }
        for (Map<String, Object> temp : allDepartmentList) {
            Long pid = ModelUtil.getLong(temp, "id", 0);
            temp.put("child", tempMap.get(pid));
        }
        return tempMap.get(0L);
    }

    public List<Map<String, Object>> getAllDepartmentLists() {
        return codeMapper.getAllDepartmentLists(null);
    }

    public List<Map<String, Object>> getTypeTree(long departId) {
        List<Map<String, Object>> typeAll = codeMapper.getTypeAll();
        List<Map<String, Object>> typeList = codeMapper.getTypeList(departId);
        for (Map<String, Object> map : typeAll) {
            long id = ModelUtil.getLong(map, "id");//总类型id
            for (Map<String, Object> mapType : typeList) {
                long typeId = ModelUtil.getLong(mapType, "typeid");//科室下类型
                if (id == typeId) {
                    map.put("checked", 1);
                    break;
                } else {
                    map.put("checked", 0);
                }
            }
        }
        return typeAll;
    }


    private void departmentTree(Map<String, Object> temp, long pid, Map<Long, List<Map<String, Object>>> tempMap) {
        if (tempMap.containsKey(pid)) {
            tempMap.get(pid).add(temp);
        } else {
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(temp);
            tempMap.put(pid, list);
        }
    }


    /**
     * 录地区数据
     *
     * @return
     */
    public boolean adddiqu() {
        List<Map<String, Object>> maps = readExcel("/home/qwq/tools/aaa.xlsx", 0, 1, 3, 2);
        for (Map<String, Object> map : maps) {
            String str = ModelUtil.getStr(map, "0");
            String str1 = ModelUtil.getStr(map, "2");
            if (str.lastIndexOf(".0") > 0) {
                str = str.substring(0, str.length() - 2);
            }
            if (str1.lastIndexOf(".0") > 0) {
                str1 = str1.substring(0, str1.length() - 2);
            }
            codeMapper.adddiqu(Integer.parseInt(str), ModelUtil.getStr(map, "1"), Integer.parseInt(str1));
        }
        return true;
    }

    /**
     * 录药品数据
     *
     * @return
     */
    public boolean adddrugs() {
        List<Map<String, Object>> maps = readExcel("/home/qwq/tools/aaa.xlsx", 0, 1, 12, 2);
        for (Map<String, Object> map : maps) {
            String str = ModelUtil.getStr(map, "0");
            String str1 = ModelUtil.getStr(map, "1");
            String firstSpell = Pinyin4jUtil.getInstance().getFirstSpell(str1);
            String str2 = ModelUtil.getStr(map, "2");
            String str3 = ModelUtil.getStr(map, "3");
            String str4 = ModelUtil.getStr(map, "4");
            String str5 = ModelUtil.getStr(map, "5");
            String str6 = ModelUtil.getStr(map, "6");
            String str7 = ModelUtil.getStr(map, "7");
            String str10 = ModelUtil.getStr(map, "10");
            String str11 = ModelUtil.getStr(map, "11");

            //codeMapper.adddrug(str, str1, str2, str3, firstSpell, str4, str5, str6, str7, str10, str11);
        }
        return true;
    }


    /**
     * 根据pid查找地区
     *
     * @param code
     * @return
     */
    public List<Map<String, Object>> getAreaByParentId(int code) {
        return codeMapper.getAreaByParentId(code);
    }

    /**
     * 根据code查找地区
     *
     * @param code
     * @return
     */
    public Map<String, Object> getArea(int code) {
        return codeMapper.getArea(code);
    }

    /**
     * 根据value查找地区
     *
     * @param value
     * @return
     */
    public Map<String, Object> getAreaByValue(String value) {
        return codeMapper.getAreaByValue(value);
    }

    /**
     * 根据codes查找地区
     *
     * @param codes
     * @return
     */
    public String getArea(String codes) {
        if (!StrUtil.isEmpty(codes) && codes.length() == 20) {
            return ModelUtil.getStr(codeMapper.getArea(codes), "areas");
        } else {
            return null;
        }
    }

    /**
     * 药品字典列表
     *
     * @param name
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getDrugsList(String name, int pageIndex, int pageSize) {
        return codeMapper.getDrugsList(name, pageIndex, pageSize);
    }

    public Map<String, Object> getDrugsListId(long id) {
        Map<String, Object> map = codeMapper.getDrugsListId(id);
        String drugform = ModelUtil.getStr(map, "drugform");
        int drugformid = ModelUtil.getInt(map, "drugformid");
        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", drugformid);
        map1.put("name", drugform);
        map.put("drugformmap", map1);
        return map;
    }

    public List<Map<String, Object>> codeDrugsDosageList() {
        return codeMapper.codeDrugsDosageList();
    }


    public boolean updateDrugs(long id, int drugformid, int drugcode, String durgname) {
        Map<String, Object> map = codeMapper.codeDrugsDosageListId(drugformid);
        String drugform = ModelUtil.getStr(map, "value");
        return codeMapper.updateDrugs(id, drugformid, drugform, drugcode, durgname);
    }


    /**
     * 药品字典数量
     *
     * @param name
     * @return
     */
    public long getDrugsCount(String name) {
        return codeMapper.getDrugsCount(name);
    }

    /**
     * 用药频率字典列表
     *
     * @return
     */
    public List<Map<String, Object>> getFrequencyList() {
        return codeMapper.getFrequencyList();
    }

    /**
     * 药品字典
     *
     * @param id
     * @return
     */
    public Map<String, Object> getDrugs(long id) {
        return codeMapper.getDrugs(id);
    }

    /**
     * 用药频率
     *
     * @param id
     * @return
     */
    public Map<String, Object> getFrequency(long id) {
        return codeMapper.getFrequency(id);
    }

    /**
     * 疾病类型
     *
     * @param id
     * @return
     */
    public Map<String, Object> getDiseasesType(long id) {
        return codeMapper.getDiseasesType(id);
    }

    /**
     * 药品字典添加首字母
     *
     * @param name
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public boolean updateDrugs(String name, int pageIndex, int pageSize) {
        List<Map<String, Object>> departmentList = codeMapper.getDrugsList(name, pageIndex, pageSize);
        for (Map<String, Object> map : departmentList) {
            long id = ModelUtil.getLong(map, "id");
            String value = ModelUtil.getStr(map, "value");
            String firstSpell = Pinyin4jUtil.getInstance().getFirstSpell(value);
            codeMapper.updateDrugs(id, firstSpell);
        }
        return true;
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
        return codeMapper.getTitleList(name, pageIndex, pageSize);
    }

    /**
     * 职称数量
     *
     * @param name
     * @return
     */
    public long getTitleCount(String name) {
        return codeMapper.getTitleCount(name);
    }

    public boolean updateDrugs(long id, String drug_form, String standard_desc, String totaldosage_unit, String unit, String manufacturing_enterprise, String bidding_enterprise, String catalog_category, String procurement_category, long userId) {
        return codeMapper.updateDrugs(id, drug_form, standard_desc, totaldosage_unit, unit, manufacturing_enterprise, bidding_enterprise, catalog_category, procurement_category, userId);
    }
}
