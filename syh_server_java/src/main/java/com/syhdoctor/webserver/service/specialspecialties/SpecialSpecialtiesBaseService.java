package com.syhdoctor.webserver.service.specialspecialties;

import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.specialspecialties.SpecialSpecialtiesMpper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public abstract class SpecialSpecialtiesBaseService extends BaseService {

    @Autowired
    private SpecialSpecialtiesMpper specialSpecialtiesMapper;

    public List<Map<String, Object>> getSpecialSpecialtiesList(long id, int pageIndex, int pageSize) {
        return specialSpecialtiesMapper.getSpecialSpecialtiesList(id, pageIndex, pageSize);
    }

    /**
     * 详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getSpecialSpecialtiesId(long id) {
        Map<String, Object> specialSpecialtiesId = specialSpecialtiesMapper.getSpecialSpecialtiesId(id);
        List<?> SymptomsTypeList = specialSpecialtiesMapper.getSymptomsType(id);
        if (specialSpecialtiesId != null) {
            specialSpecialtiesId.put("symptomtype", SymptomsTypeList);
        }
        return specialSpecialtiesId;
    }

    public Map<String,Object> getShareSpecial(long id){
        return specialSpecialtiesMapper.getShareSpecial(id);
    }

    /**
     * 下拉框
     *
     * @return
     */
    public List<Map<String, Object>> getCommonDiseaseSymptomsType() {
        return specialSpecialtiesMapper.getCommonDiseaseSymptomsType();
    }

    public long getSpecialSpecialtiesListCount(long id) {
        return specialSpecialtiesMapper.getSpecialSpecialtiesListCount(id);
    }

    public boolean delSpecialSpecialties(long id) {
        return specialSpecialtiesMapper.delSpecialSpecialties(id);
    }

    public boolean updateAddSpecialSpecialties(int islogin, long id, String picture, int symptomtype, int sort, String complextext, String color, String buttontext, String headname, String backgroundpicture) {
        boolean bo = false;
        specialSpecialtiesMapper.sortClear(sort);
        if (id == 0) {
            bo = specialSpecialtiesMapper.addSpecialSpecialties(islogin, picture, symptomtype, sort, complextext, color, buttontext, headname, backgroundpicture);
        } else {
            bo = specialSpecialtiesMapper.updateSpecialSpecialties(islogin, id, picture, symptomtype, sort, complextext, color, buttontext, headname, backgroundpicture);
        }
        return bo;
    }


}
