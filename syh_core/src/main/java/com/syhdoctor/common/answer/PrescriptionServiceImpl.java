package com.syhdoctor.common.answer;

import com.syhdoctor.common.utils.EnumUtils.QAContentTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionServiceImpl implements IProblem {

    private static volatile PrescriptionServiceImpl instance;


    private PrescriptionServiceImpl() {
    }

    public static PrescriptionServiceImpl getInstance() {
        synchronized (PrescriptionServiceImpl.class) {
            if (instance == null) {
                instance = new PrescriptionServiceImpl();
            }
        }
        return instance;
    }

    @Override
    public List<Map<String, Object>> getContent(List<Map<String, Object>> answerList, List<Map<String, Object>> objectList) {
        Map<Long, Object> tempProblem = new HashMap<>();
        long tempId = 0;
        for (Map<String, Object> obj : objectList) {
            Long id = ModelUtil.getLong(obj, "id");
            if (id > 0) {
                if (id != tempId) {
                    tempId = id;
                    Map<String, Object> problemObj = new HashMap<>();
                    problemObj.put("id", id);
                    problemObj.put("prescriptionid", ModelUtil.getLong(obj, "prescriptionid"));
                    problemObj.put("presphotourl", String.format("%s?%s", ModelUtil.getStr(obj, "presphotourl"), ModelUtil.getLong(obj, "modifytime")));
                    tempProblem.put(id, problemObj);
                }
            }
        }

        for (Map<String, Object> map : answerList) {
            int contenttype = ModelUtil.getInt(map, "contenttype");
            if (QAContentTypeEnum.Prescription.getCode() == contenttype || QAContentTypeEnum.InExaminePrescription.getCode() == contenttype || QAContentTypeEnum.PrescriptionFail.getCode() == contenttype) {
                map.put("content", tempProblem.get(ModelUtil.getLong(map, "id")));
            }
        }
        return answerList;
    }
}
