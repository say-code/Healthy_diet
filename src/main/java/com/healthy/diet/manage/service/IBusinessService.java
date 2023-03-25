package com.healthy.diet.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthy.diet.entity.Employee;
import com.healthy.diet.manage.model.Business;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sayCode
 * @date 2022/9/25 21:45
 * project: Heathy_diet
 * Title: IBusinessService
 * description: 公司信息 Service接口层
 */
public interface IBusinessService extends IService<Business> {
    /**
     * 插入公司信息
     * @param business 公司信息
     * @return 是否插入成功
     */
    int businessInsert(Business business);

    /**
     * 查找 根据公司 id 返回公司名
     * @return 公司名
     */
    ArrayList<Business> businessNameSelectAll();

    /**
     * 删除 根据公司Id删除公司
     * @param businessId 公司Id
     * @return 是否删除成功
     */
    int businessDeleteByBusinessId(String businessId);

    /**
     * 根据公司Id查找公司名
     * @param businessId 公司Id
     * @return 公司名
     */
    String businessNameSelectByBusinessId(String businessId);

    /**
     * 统计公司数量
     * @return 公司数量
     */
    Integer businessCount();

    /**
     * 总菜品数量统计
     * @return 菜品数量
     */
    Integer allBusinessDishCount();

    /**
     * 总订单数量统计
     * @return 总订单数量
     */
    Integer allOrderCount();

    List<Employee> selectAllEmployee();
}
