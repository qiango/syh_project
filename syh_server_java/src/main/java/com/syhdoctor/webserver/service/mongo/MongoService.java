package com.syhdoctor.webserver.service.mongo;

import com.syhdoctor.webserver.base.service.BaseService;
import com.syhdoctor.webserver.mapper.mongo.MongoMapper;
import com.syhdoctor.webserver.thirdparty.mongodb.entity.Share;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MongoService extends BaseService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoMapper mongoMapper;

    public long getId(String tablename) {
        return mongoMapper.getId(tablename);
    }

    public void saveShare(Share share) {
        mongoTemplate.save(share);
    }

    public Share getShareById(long id) {
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, Share.class);
    }

    public Page shareList(int pageIndex, int pageSize) {
        SpringbootPageable pageable = new SpringbootPageable();
        PageModel pm = new PageModel();
        Query query = new Query();
        List<Sort.Order> orders = new ArrayList<Sort.Order>();  //排序
        orders.add(new Sort.Order(Sort.Direction.DESC, "id"));
        Sort sort = new Sort(orders);
        // 开始页
        pm.setPagenumber(pageIndex);
        // 每页条数
        pm.setPagesize(pageSize);
        // 排序
        pm.setSort(sort);
        pageable.setPage(pm);
        // 查询出一共的条数
        Long count = mongoTemplate.count(query, Share.class);
        // 查询
        List<Share> list = mongoTemplate.find(query.with(pageable), Share.class);
        // 将集合与分页结果封装
        return new PageImpl<Share>(list, pageable, count);
    }
}
