package com.syhdoctor.webserver.service.department;

import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.department.DepartmentMapper;
import com.syhdoctor.webserver.service.code.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public abstract class DepartmentBaseService extends BaseService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private CodeService codeService;

    /**
     * 新增修改科室包
     *
     * @param id            科室id
     * @param name          科室名字
     * @param sort          排序
     * @param departmentids 科室字典
     * @param userId        创建人
     * @return
     */
    @Transactional
    public boolean addUpdateDepartmentPackage(long id, String name, int sort, List<?> departmentids, long userId) {
        //清空重复排序
        departmentMapper.updateDepartmentSort(sort);
        boolean flag = false;
        if (id == 0) {
            id = departmentMapper.addDepartmentPackage(name, sort, userId);
        } else {
            departmentMapper.updateDepartmentPackage(id, name, sort, userId);
            flag = departmentMapper.delMiddleDepartmentPackage(id);
        }
        if (departmentids.size() > 0) {
            for (Object value : departmentids) {
                long departmentId = ((Integer) value).longValue();
                if (departmentId > 0) {
                    flag = departmentMapper.addMiddleDepartmentPackage(id, departmentId);
                }
            }
        }
        return flag;
    }

    /**
     * 科室包列表
     *
     * @param name      名字
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> getDepartmentPackageList(String name, int pageIndex, int pageSize) {
        return departmentMapper.getDepartmentPackageList(name, pageIndex, pageSize);
    }


    /**
     * 科室包数量
     *
     * @param name
     * @return
     */
    public long getDepartmentPackageCount(String name) {
        return departmentMapper.getDepartmentPackageCount(name);
    }

    /**
     * 科室包详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getDepartmentPackage(long id) {
        Map<String, Object> departmentPackage = departmentMapper.getDepartmentPackage(id);
        List<?> middleDepartmentPackageList = departmentMapper.getMiddleDepartmentPackageList(id);
        departmentPackage.put("departmentids", middleDepartmentPackageList);
        return departmentPackage;
    }

    /**
     * 科室字典列表（只取了一级）
     *
     * @return
     */
    public List<Map<String, Object>> getDepartmentList(String name, int pageIndex, int pageSize) {
        return codeService.getDepartmentList(name, pageIndex, pageSize);
    }

    /**
     * 删除科室包
     *
     * @param id
     * @return
     */
    public boolean delDepartmentPackage(long id) {
        departmentMapper.delDepartmentPackage(id);
        departmentMapper.delMiddleDepartmentPackage(id);
        return true;
    }
}
