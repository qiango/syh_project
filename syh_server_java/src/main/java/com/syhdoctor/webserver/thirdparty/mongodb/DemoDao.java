package com.syhdoctor.webserver.thirdparty.mongodb;

import com.syhdoctor.webserver.thirdparty.mongodb.entity.DemoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemoDao {

    void saveDemo(DemoEntity demoEntity);

    void removeDemo(Long id);

    void updateDemo(DemoEntity demoEntity);

    DemoEntity findDemoById(Long id);

    List<DemoEntity> findAll();
}