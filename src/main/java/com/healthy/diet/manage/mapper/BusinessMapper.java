package com.healthy.diet.manage.mapper;

import com.healthy.diet.entity.Employee;
import com.healthy.diet.manage.model.Business;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sayCode
 * @date 2022/9/25 21:36
 * project: Heathy_diet
 * Title: BusinessMapper
 * description: 公司类Mapper
 */
@Mapper
public interface BusinessMapper {

    /**
     * 插入公司信息
     * @param business 公司信息
     * @return 是否插入成功
     */
    int businessInsert(Business business);

    /**
     * 查找 公司列表
     * @return 公司名
     */
    ArrayList<Business> businessNameSelectAll();

    /**
     * 根据公司Id查找公司名
     * @param businessId 公司Id
     * @return 公司名
     */
    String businessNameSelectById(String businessId);

    /**
     * 删除 根据公司Id删除公司
     * @param businessId 公司Id
     * @return 是否删除成功
     */
    int businessDeleteByBusinessId(String businessId);

    /**
     * 统计公司数量
     * @return 公司数量
     */
    Integer businessCount();

    /**
     * 总彩屏数量统计
     * @return 总菜品数量
     */
    Integer allBusinessDishCount();

    /**
     * 统计平台总订单量
     * @return 总订单数量
     */
    Integer allOrderCount();

    /**
     * 查找所有用户信息
     * @return 用户信息列表
     */
    List<Employee> selectAllEmployee();

}
