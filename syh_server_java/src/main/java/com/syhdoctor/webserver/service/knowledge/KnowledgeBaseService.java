package com.syhdoctor.webserver.service.knowledge;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.Pinyin4jUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.knowledge.KnowledgeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public abstract class KnowledgeBaseService extends BaseService {
    @Autowired
    private KnowledgeMapper knowledgeMapper;

    /**
     * 常见疾病列表
     *
     * @param name
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getAdminDiseaseList(String name, int pageIndex, int pageSize) {
        return knowledgeMapper.getAdminDiseaseList(name, pageIndex, pageSize);
    }

    /**
     * 常见疾病数量
     *
     * @param name
     * @return
     */
    public long getAdminDiseaseCount(String name) {
        return knowledgeMapper.getAdminDiseaseCount(name);
    }

    /**
     * 常见疾病详细
     *
     * @param id
     * @return
     */
    public Map<String, Object> getAdminDisease(long id) {
        return knowledgeMapper.getDisease(id);
    }

    /**
     * @param id             id
     * @param name           疾病名
     * @param summary      概要
     * @param information    基本信息
     * @param knowledge      疾病知识
     * @param classification 分类
     * @param clinical       临床表现
     * @param inspect        检查
     * @param diagnosis      诊断
     * @param treatment      治疗方案
     * @param prevention     预防
     * @param createUser
     * @return
     */
    public boolean addUpdateDisease(long id, String name, String summary, String information, String knowledge, String classification,
                                    String clinical, String inspect, String diagnosis, String treatment, String prevention, long createUser) {
        String name_pinyin = Pinyin4jUtil.getInstance().getFirstSpell(name);
        if (id == 0) {
            return knowledgeMapper.addDisease(name, name_pinyin, summary, information, knowledge, classification, clinical, inspect, diagnosis, treatment, prevention, createUser);
        } else {
            return knowledgeMapper.updateDisease(id, name, name_pinyin, summary, information, knowledge, classification, clinical, inspect, diagnosis, treatment, prevention, createUser);
        }
    }

    /**
     * 删除常见疾病
     *
     * @param id
     * @param createUser
     * @return
     */
    public boolean delDisease(long id, long createUser) {
        return knowledgeMapper.delDisease(id, createUser);
    }


    public Map<String, Object> getAppDiseaseList(String name, int pageIndex, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> hotDiseaseList = knowledgeMapper.getHotDiseaseList();
        List<Map<String, Object>> list = knowledgeMapper.getAppDiseaseList(name, pageIndex, pageSize);
        //字母集合
        List<Map<String, Object>> letterList = new ArrayList<>();

        //用户集合
        List<Map<String, Object>> diseaseList = new ArrayList<>();

        if (list.size() > 0) {
            //用户对象
            String key = null;
            for (Map map : list) {
                Map<String, Object> diseaseMap = new HashMap<>();
                String initials = ModelUtil.getStr(map, "initials");
                if (!initials.equals(key)) {
                    key = initials;
                    //字母对象
                    Map<String, Object> letterMap = new HashMap<>();
                    diseaseList = new ArrayList<>();
                    letterMap.put("key", key);
                    letterMap.put("diseaselist", diseaseList);
                    diseaseMap.put("id", ModelUtil.getLong(map, "id"));
                    diseaseMap.put("value", ModelUtil.getStr(map, "name"));
                    diseaseList.add(diseaseMap);
                    letterList.add(letterMap);
                } else {
                    diseaseMap.put("id", ModelUtil.getLong(map, "id"));
                    diseaseMap.put("value", ModelUtil.getStr(map, "name"));
                    diseaseList.add(diseaseMap);
                }
            }
        }
        result.put("hotdiseaselist", hotDiseaseList);
        result.put("diseaselist", letterList);
        return result;
    }

    public List<Map<String, Object>> getAppDiseaseList() {
        return knowledgeMapper.getHotDiseaseList();
    }

    public Map<String, Object> getAppDisease(long diseaseid) {
        knowledgeMapper.updateDiseasePageviews(diseaseid);
        return knowledgeMapper.getDisease(diseaseid);
    }

    public List<Map<String, Object>> searchDiseaseList(String name) {
        return knowledgeMapper.getAppDiseaseList(name);
    }
}
