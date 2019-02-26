package com.syhdoctor.common.answer;

import com.syhdoctor.common.utils.EnumUtils.QAContentTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.UnixUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoServiceImpl implements IProblem {

    private static volatile UserInfoServiceImpl instance = null;

    private UserInfoServiceImpl() {
    }

    public static UserInfoServiceImpl getInstance() {
        synchronized (UserInfoServiceImpl.class) {
            if (instance == null) {
                instance = new UserInfoServiceImpl();
            }
        }
        return instance;
    }

    @Override
    public List<Map<String, Object>> getContent(List<Map<String, Object>> answerList, List<Map<String, Object>> objectList) {
        List<Map<String, Object>> tempList = new ArrayList<>();
        Map<Long, Object> tempProblem = new HashMap<>();
        long tempId = 0;

        for (Map<String, Object> obj : objectList) {
            Long id = ModelUtil.getLong(obj, "id");
            if (id > 0) {
                if (id != tempId) {
                    tempId = id;
                    Map<String, Object> problemObj = new HashMap<>();
                    problemObj.put("id", id);
                    problemObj.put("username", ModelUtil.getStr(obj, "username"));
                    problemObj.put("gendername", ModelUtil.getStr(obj, "gendername"));
                    problemObj.put("disdescribe", ModelUtil.getStr(obj, "disdescribe"));
                    long birthday = ModelUtil.getLong(obj, "birthday");
                    long age = (UnixUtil.getNowTimeStamp() - birthday) / 365 / 24 / 60 / 60 / 1000;
                    problemObj.put("userage", age);

                    tempList = new ArrayList<>();
                    Map<String, Object> contentObj = new HashMap<>();
                    contentObj.put("diseasename", ModelUtil.getStr(obj, "diseasename"));
                    tempList.add(contentObj);
                    problemObj.put("diseaselist", tempList);
                    tempProblem.put(id, problemObj);
                } else {
                    Map<String, Object> contentObj = new HashMap<>();
                    contentObj.put("diseasename", ModelUtil.getStr(obj, "diseasename"));
                    tempList.add(contentObj);
                }
            }
        }

        for (Map<String, Object> map : answerList) {
            int contenttype = ModelUtil.getInt(map, "contenttype");
            if (QAContentTypeEnum.UserInfo.getCode() == contenttype) {
                map.put("content", tempProblem.get(ModelUtil.getLong(map, "id")));
            }
        }

        return answerList;
    }
}
