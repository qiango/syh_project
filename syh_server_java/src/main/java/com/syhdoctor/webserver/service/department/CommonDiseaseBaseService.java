package com.syhdoctor.webserver.service.department;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.department.CommonDiseaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author qian.wang
 * @description
 * @date 2018/11/7
 */
@Service
public abstract class CommonDiseaseBaseService extends BaseService {

    @Autowired
    private CommonDiseaseMapper commonDiseaseMapper;

    //列表
    public List<Map<String, Object>> findList(String departName, int pageIndex, int pageSize) {
        return commonDiseaseMapper.findList(departName, pageIndex, pageSize);
    }

    public List<Map<String, Object>> getCommonDiseaseSymptomsType() {
        return commonDiseaseMapper.getCommonDiseaseSymptomsType();
    }

    public boolean insertCommonDiseaseSymptomsType(String name) {
        return commonDiseaseMapper.insertCommonDiseaseSymptomsType(name);
    }

    public boolean updateAddCommonDiseaseSymptomsType(long id, String name) {
        if (id == 0) {
            insertCommonDiseaseSymptomsType(name);
        } else {
            updateCommonDiseaseSymptomsType(id, name);
        }
        return true;
    }

    public boolean updateCommonDiseaseSymptomsType(long id, String name) {
        return commonDiseaseMapper.updateCommonDiseaseSymptomsType(id, name);
    }

    public boolean delCommonDiseaseSymptomsType(long id) {
        return commonDiseaseMapper.delCommonDiseaseSymptomsType(id);
    }

    public boolean delType(long id) {
        delCommonDiseaseSymptomsType(id);
        delCommonDiseaseSymptomsAllTypeid(id);
        return true;
    }

    //根据类型（typeid）删除症状
    public boolean delCommonDiseaseSymptomsAllTypeid(long id) {
        return commonDiseaseMapper.delCommonDiseaseSymptomsAllTypeid(id);
    }

    public List<Map<String, Object>> getCommonDiseaseSymptoms(long typeid) {
        return commonDiseaseMapper.getCommonDiseaseSymptoms(typeid);
    }


    public boolean delCommonDiseaseSymptoms(long id) {
        return commonDiseaseMapper.delCommonDiseaseSymptoms(id);
    }

    public boolean insertCommonDiseaseSymptoms(String name, long typeid) {
        return commonDiseaseMapper.insertCommonDiseaseSymptoms(name, typeid);
    }

    public boolean updateCommonDiseaseSymptoms(long id, String name) {
        return commonDiseaseMapper.updateCommonDiseaseSymptoms(id, name);
    }

    //列表条数
    public long findCount(String departName) {
        return commonDiseaseMapper.getfindListCount(departName);
    }

    //删除之前的科室并新增
    public boolean insertDepart(long departids, List<?> typelist) {
        long departid = departids;
        List<Long> typelists = new ArrayList<>();
        for (Object key : typelist) {
            long typeids = ModelUtil.strToLong(String.valueOf(key), 0);
            typelists.add(typeids);
        }
        if (commonDiseaseMapper.getdepartmentid(departid)) {
            commonDiseaseMapper.deleteDepart(departid);
        }
        return commonDiseaseMapper.insertdepartment(departid, typelists);
    }

    //删除
    public boolean deleteDepart(long id) {
        return commonDiseaseMapper.deleteDepart(id);
    }

    //详情
    public Map<String, Object> findBydetail(long id) {
        return commonDiseaseMapper.findDetail(id);
    }

    public List<Map<String, Object>> findType() {
        return commonDiseaseMapper.findType();
    }

}
