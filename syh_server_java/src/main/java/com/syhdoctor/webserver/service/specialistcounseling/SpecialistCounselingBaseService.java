package com.syhdoctor.webserver.service.specialistcounseling;

import com.syhdoctor.common.utils.EnumUtils.DisplaypositionEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.specialistcounseling.SpecialistCounselingMapper;
import com.syhdoctor.webserver.service.answer.AnswerService;
import com.syhdoctor.webserver.service.focusfigure.FocusfigureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public abstract class SpecialistCounselingBaseService extends BaseService {

    @Autowired
    private SpecialistCounselingMapper specialistCounselingMapper;

    @Autowired
    private FocusfigureService focusfigureService;

    @Autowired
    private AnswerService answerService;

    public List<Map<String, Object>> getSpecialistCounselingList(long id, int pageIndex, int pageSize) {
        return specialistCounselingMapper.getSpecialistCounselingList(id, pageIndex, pageSize);
    }


    /**
     * 详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getSpecialistCounselingId(long id) {
        Map<String, Object> specialSpecialtiesId = specialistCounselingMapper.getSpecialistCounselingId(id);
        List<?> SymptomsTypeList = specialistCounselingMapper.getSymptomsType(id);
        if (specialSpecialtiesId != null) {
            specialSpecialtiesId.put("symptomtype", SymptomsTypeList);
        }
        return specialSpecialtiesId;
    }

    /**
     * 下拉框
     *
     * @return
     */
    public List<Map<String, Object>> getSymptomsType() {
        return specialistCounselingMapper.getSymptomsType();
    }


    public long getSpecialistCounselingListCount(long id) {
        return specialistCounselingMapper.getSpecialistCounselingListCount(id);
    }

    public boolean delSpecialistCounseling(long id) {
        return specialistCounselingMapper.delSpecialistCounseling(id);
    }


    public boolean updateAddSpecialistCounseling(long id, String picture, int sort, String complextext, String color, String buttontext, String headname, String backgroundpicture, int islogin, List<?> symptomtype) {
        boolean ad = false;
        specialistCounselingMapper.sortClear(sort);
        if (id == 0) {
            id = specialistCounselingMapper.addSpecialistCounseling(picture, sort, complextext, color, buttontext, headname, backgroundpicture, islogin);
        } else {
            specialistCounselingMapper.updateSpecialistCounseling(id, picture, sort, complextext, color, buttontext, headname, backgroundpicture, islogin);
            ad = specialistCounselingMapper.delSymptomType(id);
        }
        if (symptomtype.size() > 0) {
            for (Object value : symptomtype) {
                long typeid = ((Integer) value).longValue();
                if (typeid > 0) {
                    ad = specialistCounselingMapper.addSymptomType(id, typeid);
                }
            }
        }

        return ad;
    }


    //app首页专病资讯列表
    public List<Map<String, Object>> getSpecialCounList() {
        return specialistCounselingMapper.getSpecialCounList();
    }

    //app首页专病资讯列表
    public List<Map<String, Object>> getSpecialCounListPage(int pageSize, int pageIndex) {
        return specialistCounselingMapper.getSpecialCounListPage(pageSize, pageIndex);
    }

    //app首页专病资讯详情
    public Map<String, Object> getSpecialCountDetail(long id) {
        return specialistCounselingMapper.getSpecialCountDetail(id);
    }

    //app首页特色专科列表
    public List<Map<String, Object>> getSpecialList() {
        return specialistCounselingMapper.getSpecialList();
    }

    //app首页特色专科列表
    public List<Map<String, Object>> getWebSpecialList() {
        List<Map<String, Object>> webSpecialList = specialistCounselingMapper.getWebSpecialList();
        List<Long> typeids = new ArrayList<>();
        for (Map<String, Object> map : webSpecialList) {
            typeids.add(ModelUtil.getLong(map, "typeid"));
        }

        if (typeids.size() > 0) {
            List<Map<String, Object>> diseaseList = specialistCounselingMapper.getDiseaseList(typeids);
            init(webSpecialList, diseaseList);
        }
        return webSpecialList;
    }

    public void init(List<Map<String, Object>> sList, List<Map<String, Object>> diseaseList) {
        Map<Long, List<Map<String, Object>>> tempMap = new HashMap<>();

        for (Map<String, Object> obj : diseaseList) {
            long typeid = ModelUtil.getLong(obj, "typeid");
            initMap(obj, typeid, tempMap);
        }

        for (Map<String, Object> map : sList) {
            map.put("diseaselist", tempMap.get(ModelUtil.getLong(map, "typeid")));
        }
    }

    public void initMap(Map<String, Object> temp, long id, Map<Long, List<Map<String, Object>>> tempMap) {
        if (tempMap.containsKey(id)) {
            tempMap.get(id).add(temp);
        } else {
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(temp);
            tempMap.put(id, list);
        }
    }

    public Map<String, Object> findfocus() {
        List<Map<String, Object>> list = focusfigureService.bannerList(DisplaypositionEnum.UserOpen.getCode(), 1);
        Map<String, Object> map = new HashMap<>();
        if (list.size() > 0) {
            map = list.get(0);
        }
        return map;
    }

    public List<Map<String, Object>> getSpecialList(int pageSize, int pageIndex) {
        return specialistCounselingMapper.getSpecialList(pageSize, pageIndex);
    }

    public long getSpecialListConut() {
        return specialistCounselingMapper.getSpecialListConut();
    }

    public long getSpecialCountListConut() {
        return specialistCounselingMapper.getSpecialCountListConut();
    }

    //app首页特色专科详情
    public Map<String, Object> getSpecialDetail(long id) {
        return specialistCounselingMapper.getSpecialDetail(id);
    }

    public List<Map<String, Object>> getSymptomsTypeList(long scid) {
        return answerService.getCounselingSymptomsTypeList(scid);
    }
}
