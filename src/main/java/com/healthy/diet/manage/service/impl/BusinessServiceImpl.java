package com.healthy.diet.manage.service.impl;

import com.healthy.diet.manage.mapper.BusinessMapper;
import com.healthy.diet.manage.model.Business;
import com.healthy.diet.manage.service.IBusinessService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author sayCode
 * @date 2022/9/25 21:50
 * project: Heathy_diet
 * Title: BusinessServiceImpl
 * description: TODO
 */
public class BusinessServiceImpl implements IBusinessService {

    @Autowired
    BusinessMapper businessMapper;

    @Override
    public int businessInsert(Business business) {
        return businessMapper.businessInsert(business);
    }

    @Override
    public String businessNameSelectByBusinessId(String businessId) {
        return businessMapper.businessNameSelectByBusinessId(businessId);
    }

    @Override
    public int businessDeleteByBusinessId(String businessId) {
        return businessMapper.businessDeleteByBusinessId(businessId);
    }
}
