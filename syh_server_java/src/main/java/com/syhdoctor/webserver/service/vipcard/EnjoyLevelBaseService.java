package com.syhdoctor.webserver.service.vipcard;

import com.syhdoctor.common.utils.EnumUtils.EnjoyTypeEnum;
import com.syhdoctor.common.utils.ModelUtil;
import com.syhdoctor.common.utils.PriceUtil;
import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.vipcard.EnjoyLevelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public abstract class EnjoyLevelBaseService extends BaseService {

    @Autowired
    private EnjoyLevelMapper enjoyLevelMapper;

    /*
等级
 */
    public List<Map<String, Object>> getLevelList(long id, int level, int pageIndex, int pageSize) {
        return enjoyLevelMapper.getLevelList(id, level, pageIndex, pageSize);
    }

    public long getLevelListCount(long id, int level) {
        return enjoyLevelMapper.getLevelListCount(id, level);
    }

    public Map<String, Object> getLevelListId(long id) {
        return enjoyLevelMapper.getLevelListId(id);
    }

    public boolean updateLevel(long id, int level, int currentintegral) {
        return enjoyLevelMapper.updateLevel(id, level, currentintegral);
    }

    public boolean updateAddLevel(long id, int level, int currentintegral) {
        if (id == 0) {
            insertLevel(level, currentintegral);
        } else {
            updateLevel(id, level, currentintegral);
        }
        return true;
    }

    public boolean insertLevel(int level, int currentintegral) {
        return enjoyLevelMapper.insertLevel(level, currentintegral);
    }

    public boolean delLevel(long id) {
        return enjoyLevelMapper.delLevel(id);
    }

    /*
    尊享值
     */

    public List<Map<String, Object>> getEnjoyValueList(long id, int pageIndex, int pageSize) {
        return enjoyLevelMapper.getEnjoyValueList(id, pageIndex, pageSize);
    }

    public long getEnjoyValueListCount(long id) {
        return enjoyLevelMapper.getEnjoyValueListCount(id);
    }

    public Map<String, Object> getEnjoyValueListId(long id) {
        return enjoyLevelMapper.getEnjoyValueListId(id);
    }


    public boolean updateEnjoyValue(long id, int currentintegral, BigDecimal price) {
        return enjoyLevelMapper.updateEnjoyValue(id, currentintegral, price);
    }

    public boolean updateAddEnjoyValue(long id, int currentintegral, BigDecimal price) {
        if (id == 0) {
            insertEnjoyValue(currentintegral, price);
        } else {
            updateEnjoyValue(id, currentintegral, price);
        }
        return true;
    }

    public boolean insertEnjoyValue(int currentintegral, BigDecimal price) {
        return enjoyLevelMapper.insertEnjoyValue(currentintegral, price);
    }

    public boolean delEnjoyValue(long id) {
        return enjoyLevelMapper.delEnjoyValue(id);
    }


    /*
    尊享类别
     */
    public List<Map<String, Object>> getEnjoyTypeList(int type, int pageIndex, int pageSize) {
        return enjoyLevelMapper.getEnjoyTypeList(type, pageIndex, pageSize);
    }

    public long getEnjoyTypeListCount(int type) {
        return enjoyLevelMapper.getEnjoyTypeListCount(type);
    }


    public Map<String, Object> getEnjoyTypeListId(long id) {
        return enjoyLevelMapper.getEnjoyTypeListId(id);
    }


    public boolean updateEnjoyType(long id, String typedescribe) {
        return enjoyLevelMapper.updateEnjoyType(id, typedescribe);
    }

    /*
    修改或添加尊享类别
     */
    public boolean updateAddEnjoyType(long id, int type, String typedescribe) {
        if (id == 0) {
            insertEnjoyType(type, typedescribe);
        } else {
            updateEnjoyType(id, typedescribe);
        }
        return true;
    }

    public boolean insertEnjoyType(int type, String typedescribe) {
        return enjoyLevelMapper.insertEnjoyType(type, typedescribe);
    }

    public boolean delEnjoyType(long id) {
        return enjoyLevelMapper.delEnjoyType(id);
    }


}
