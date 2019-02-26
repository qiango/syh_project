package com.syhdoctor.webserver.service.drugs;

import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.drugs.DrugsMapper;
import com.syhdoctor.webserver.service.code.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public abstract class DrugsBaseService extends BaseService {
    @Autowired
    private DrugsMapper drugsMapper;

    @Autowired
    private CodeService codeService;

    /**
     * 新增修改药品包
     *
     * @param id       药品id
     * @param name     药品名字
     * @param sort     排序
     * @param drugsids 药品字典id
     * @param userId   创建人
     * @return
     */
    @Transactional
    public boolean addUpdateDrugsPackage(long id, String name, String img, int sort, List<?> drugsids, long userId) {
        boolean flag = false;
        if (id == 0) {
            id = drugsMapper.addDrugsPackage(name, img, sort, userId);
        } else {
            drugsMapper.updateDrugsPackage(id, name, img, sort, userId);
            flag = drugsMapper.delMiddleDrugsPackage(id);
        }
        if (drugsids.size() > 0) {
            for (Object value : drugsids) {
                long drugsid = ((Integer) value).longValue();
                if (drugsid > 0) {
                    flag = drugsMapper.addMiddleDrugsPackage(id, drugsid);
                }
            }
        }
        return flag;
    }

    /**
     * 药品包列表
     *
     * @param name      名字
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> getDrugsPackageList(String name, int pageIndex, int pageSize) {
        return drugsMapper.getDrugsPackageList(name, pageIndex, pageSize, (Map<String, Object> map) -> {
            String value = ModelUtil.getStr(map, "value");
            if (value.length() > 10) {
                String substring = value.substring(0, 10);
            }
            return map;
        });
    }

    /**
     * 药品包列表
     *
     * @param pageSize 数量
     * @return
     */
    public List<Map<String, Object>> getDrugsPackageList(int pageSize) {
        return getDrugsPackageList(null, 1, pageSize);
    }

    /**
     * 药品包数量
     *
     * @param name
     * @return
     */
    public long getDrugsPackageCount(String name) {
        return drugsMapper.getDrugsPackageCount(name);
    }

    /**
     * 药品包详情
     *
     * @param id
     * @return
     */
    public Map<String, Object> getDrugsPackage(long id) {
        Map<String, Object> drugsPackage = drugsMapper.getDrugsPackage(id);
        List<?> middleDrugsPackageList = drugsMapper.getMiddleDrugsPackageList(id);
        if (drugsPackage != null) {
            drugsPackage.put("drugsids", middleDrugsPackageList);
        }
        return drugsPackage;
    }

    /**
     * 药品字典列表
     *
     * @return
     */
    public List<Map<String, Object>> getDrugsList(String name, int pageIndex, int pageSize) {
        return codeService.getDrugsList(name, pageIndex, pageSize);
    }

    /**
     * 删除药品包
     *
     * @param id
     * @return
     */
    public boolean delDrugsPackage(long id) {
        drugsMapper.delDrugsPackage(id);
        drugsMapper.delMiddleDrugsPackage(id);
        return true;
    }
}
