package com.syhdoctor.webserver.mapper.wechat;

import com.syhdoctor.webserver.base.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qian.wang
 * @description
 * @date 2018/10/26
 */
@Repository
public class PushMessageMapper extends BaseMapper {


    public void insert(String opendid,String message){
        String sql="insert into push_message (opendid,message,create_time) values (?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(opendid);
        params.add(message);
        insert(sql,params);
    }

}
