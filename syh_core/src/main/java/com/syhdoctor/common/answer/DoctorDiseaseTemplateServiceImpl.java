package com.syhdoctor.common.answer;

import com.syhdoctor.common.utils.EnumUtils.QAContentTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorDiseaseTemplateServiceImpl implements IProblem {

    private static volatile DoctorDiseaseTemplateServiceImpl instance = null;

    private DoctorDiseaseTemplateServiceImpl() {
    }

    public static DoctorDiseaseTemplateServiceImpl getInstance() {
        synchronized (DoctorDiseaseTemplateServiceImpl.class) {
            if (instance == null) {
                instance = new DoctorDiseaseTemplateServiceImpl();
            }
        }
        return instance;
    }

    @Override
    public List<Map<String, Object>> getContent(List<Map<String, Object>> answerList, List<Map<String, Object>> answerDiseaseTemplateList) {
        List<Map<String, Object>> tempList = new ArrayList<>();
        Map<Long, Object> tempProblem = new HashMap<>();
        long tempId = 0;

        for (Map<String, Object> obj : answerDiseaseTemplateList) {
            Long id = ModelUtil.getLong(obj, "id");
            if (id > 0) {
                if (id != tempId) {
                    tempId = id;
                    Map<String, Object> problemObj = new HashMap<>();
                    problemObj.put("id", id);
                    problemObj.put("templateid", ModelUtil.getLong(obj, "templateid"));
                    problemObj.put("doctortitle", ModelUtil.getStr(obj, "doctortitle"));
                    problemObj.put("checkbox", ModelUtil.getBoolean(obj, "checkbox", false));

                    tempList = new ArrayList<>();
                    Map<String, Object> contentObj = new HashMap<>();
                    if (ModelUtil.getLong(obj, "answerid") > 0) {
                        contentObj.put("anscontent", ModelUtil.getStr(obj, "content"));
                        contentObj.put("useranswerid", ModelUtil.getLong(obj, "answerid"));
                        tempList.add(contentObj);
                    }
                    problemObj.put("diseaselist", tempList);
                    tempProblem.put(id, problemObj);
                } else {
                    Map<String, Object> contentObj = new HashMap<>();
                    if (ModelUtil.getLong(obj, "answerid") > 0) {
                        contentObj.put("anscontent", ModelUtil.getStr(obj, "content"));
                        contentObj.put("useranswerid", ModelUtil.getLong(obj, "answerid"));
                        tempList.add(contentObj);
                    }
                }
            }
        }
        for (Map<String, Object> map : answerList) {
            int contenttype = ModelUtil.getInt(map, "contenttype");
            if (QAContentTypeEnum.DiseaseDoctor.getCode() == contenttype) {
                map.put("content", tempProblem.get(ModelUtil.getLong(map, "id")));
            }
        }

        return answerList;
    }
}
