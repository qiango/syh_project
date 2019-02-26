package com.syhdoctor.webserver.mapper.mongo;

import com.syhdoctor.webserver.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MongoMapper extends BaseMapper {

    public long getId(String tablename) {
        return super.getId(tablename);
    }
}
