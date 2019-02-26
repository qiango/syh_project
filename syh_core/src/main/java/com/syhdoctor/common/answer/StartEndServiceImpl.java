package com.syhdoctor.common.answer;

import com.syhdoctor.common.utils.EnumUtils.AnswerOrderStateEnum;
import com.syhdoctor.common.utils.EnumUtils.QAContentTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.StrUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StartEndServiceImpl implements IProblem {

    private static volatile StartEndServiceImpl instance;


    private StartEndServiceImpl() {
    }

    public static StartEndServiceImpl getInstance() {
        synchronized (StartEndServiceImpl.class) {
            if (instance == null) {
                instance = new StartEndServiceImpl();
            }
        }
        return instance;
    }

    @Override
    public List<Map<String, Object>> getContent(List<Map<String, Object>> answerList, List<Map<String, Object>> objectList) {
        int status = 0;
        String diagnosis = "";
        int guidance = 0;
        if (objectList != null && objectList.size() > 0) {
            Map<String, Object> map = objectList.get(0);
            status = ModelUtil.getInt(map, "states");
            diagnosis = ModelUtil.getStr(map, "diagnosis");
        }
        if (AnswerOrderStateEnum.OrderSuccess.getCode() == status) {
            if (StrUtil.isEmpty(diagnosis)) {
                guidance = 1;
            } else {
                guidance = 2;
            }
        }
        for (Map<String, Object> map : answerList) {
            int contenttype = ModelUtil.getInt(map, "contenttype");
            if (QAContentTypeEnum.Tips.getCode() == contenttype) {
                Map<String, Object> result = new HashMap<>();
                result.put("status", status);
                result.put("guidance", guidance);
                map.put("contentnew", result);
            }
        }
        return answerList;
    }
}
