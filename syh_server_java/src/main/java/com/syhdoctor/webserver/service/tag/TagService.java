package com.syhdoctor.webserver.service.Tag;

import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.tag.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TagService extends BaseService {


    @Autowired
    private TagMapper tagMapper;

    /**
     * 标签列表
     *
     * @param name      标签名称
     * @param pageIndex 分页
     * @param pageSize  分页
     * @return
     */
    public List<Map<String, Object>> getTagList(String name, int pageIndex, int pageSize) {
        return tagMapper.getTagList(name, pageIndex, pageSize);
    }

    /**
     * 总条数
     *
     * @param name
     * @return
     */
    public long getTagTotal(String name) {
        return tagMapper.getTagTotal(name);
    }

    /**
     * 查询单条标签
     *
     * @param tagId
     * @return
     */
    public Map<String, Object> getTabById(int tagId) {
        return tagMapper.getTabById(tagId);
    }

    /**
     * 添加标签
     *
     * @param name    标签名称
     * @param sort    排序
     * @param agentId 登录人ID
     * @return
     */
    public boolean addTag(String name, int sort, int agentId) {
        return tagMapper.addTag(name, sort, agentId);
    }

    /**
     * 修改标签
     *
     * @param name    标签名称
     * @param sort    排序
     * @param agentId 登录人ID
     * @param tagId   标签ID
     * @return
     */
    public boolean updateTag(String name, int sort, int agentId, int tagId) {
        return tagMapper.updateTag(name, sort, agentId, tagId);
    }

    /**
     * 删除标签
     *
     * @param tagId   标签ID
     * @param agentId 登录人ID
     */
    public void deleteTag(int tagId, int agentId) {
        tagMapper.deleteTag(tagId, agentId);
    }
}
