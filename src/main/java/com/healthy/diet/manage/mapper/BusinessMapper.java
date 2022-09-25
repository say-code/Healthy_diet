package com.healthy.diet.manage.mapper;

import com.healthy.diet.manage.model.Business;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author sayCode
 * @date 2022/9/25 21:36
 * project: Heathy_diet
 * Title: BusinessMapper
 * description: 公司类Mapper
 */
public interface BusinessMapper {

    /**
     * 插入公司信息
     * @param business 公司信息
     * @return 是否插入成功
     */
    int businessInsert(Business business);

    /**
     * 查找 根据公司 id 返回公司名
     * @param businessId 公司Id
     * @return 公司名
     */
    String businessNameSelectByBusinessId(String businessId);

    /**
     * 删除 根据公司Id删除公司
     * @param businessId 公司Id
     * @return 是否删除成功
     */
    int businessDeleteByBusinessId(String businessId);

}