package com.syhdoctor.common.answer;

import java.util.List;
import java.util.Map;

public interface IProblem {

    List<Map<String, Object>> getContent(List<Map<String, Object>> answerList,List<Map<String, Object>> objectList);
}
