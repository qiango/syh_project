package com.syhdoctor.common.answer;

import com.syhdoctor.common.utils.EnumUtils.QAContentTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDiseaseTemplateServiceImpl implements IProblem {

    private static volatile UserDiseaseTemplateServiceImpl instance = null;

    private UserDiseaseTemplateServiceImpl() {
    }

    public static UserDiseaseTemplateServiceImpl getInstance() {
        synchronized (UserDiseaseTemplateServiceImpl.class) {
            if (instance == null) {
                instance = new UserDiseaseTemplateServiceImpl();
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
                    problemObj.put("usertitle", ModelUtil.getStr(obj, "usertitle"));
                    problemObj.put("choiceflag", ModelUtil.getInt(obj, "choiceflag", 0));
                    problemObj.put("checkbox", ModelUtil.getInt(obj, "checkbox", 0));

                    tempList = new ArrayList<>();
                    Map<String, Object> contentObj = new HashMap<>();
                    contentObj.put("answerid", ModelUtil.getLong(obj, "answerid"));
                    contentObj.put("value", ModelUtil.getStr(obj, "content"));
                    contentObj.put("useranswerid", ModelUtil.getLong(obj, "useranswerid"));
                    tempList.add(contentObj);
                    problemObj.put("diseaselist", tempList);
                    tempProblem.put(id, problemObj);
                } else {
                    Map<String, Object> contentObj = new HashMap<>();
                    contentObj.put("answerid", ModelUtil.getLong(obj, "answerid"));
                    contentObj.put("value", ModelUtil.getStr(obj, "content"));
                    contentObj.put("useranswerid", ModelUtil.getLong(obj, "useranswerid"));
                    tempList.add(contentObj);
                }
            }
        }

        for (Map<String, Object> map : answerList) {
            int contenttype = ModelUtil.getInt(map, "contenttype");
            if (QAContentTypeEnum.DiseaseUser.getCode() == contenttype) {
                map.put("content", tempProblem.get(ModelUtil.getLong(map, "id")));
            }
        }

        return answerList;
    }
}
