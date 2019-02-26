package com.syhdoctor.webtask.rechargeable.service;

import com.syhdoctor.webtask.base.service.BaseService;
import com.syhdoctor.webtask.rechargeable.mapper.RechargeableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author qian.wang
 * @description
 * @date 2018/10/23
 */
@Service
public class RechargeableService extends BaseService {

    @Autowired
    private RechargeableMapper rechargeableMapper;

    public void addCard(){
        rechargeableMapper.addRechargeableDetail();
    }


}
