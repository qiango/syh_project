package com.syhdoctor.webserver.service.category;

import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.category.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public abstract class CategoryBaseService extends BaseService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 查询分类
     *
     * @param name
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> getCategoryList(String name, int pageIndex, int pageSize) {
        return categoryMapper.getCategoryList(name, pageIndex, pageSize);
    }

    /**
     * 查询分类总条数
     *
     * @param name
     * @return
     */
    public long getCategoryListTotal(String name) {
        return categoryMapper.getCategoryListTotal(name);
    }

    /**
     * 查询分类
     *
     * @param id
     * @return
     */
    public Map<String, Object> getCategoryById(int id) {
        return categoryMapper.getCategoryById(id);
    }

    /**
     * 修改分类
     *
     * @param name    分类名称
     * @param sort    排序
     * @param pid     父id
     * @param agentId 登陆人id
     * @param id      分类id
     */
    public void addUpdateCategory(String name, int sort, int pid, int agentId, int id) {
        if (id == 0) {
            categoryMapper.addCategory(name, sort, pid, agentId);
        } else {
            categoryMapper.updateCategory(name, sort, pid, agentId, id);
        }
    }

    public List<?> getCategoryArticle(long articleId) {
        return categoryMapper.getCategoryArticle(articleId);
    }

    /**
     * 删除
     *
     * @param id      分类ID
     * @param agentId 登录人ID
     */
    public void deleteCategory(int id, int agentId) {
        categoryMapper.deleteCategory(id, agentId);
    }

    public List<Map<String, Object>> getCategoryDropList(String name) {
        return categoryMapper.getCategoryDropList(name);
    }
}
