package com.healthy.diet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthy.diet.dto.DishDto;
import com.healthy.diet.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //  新增菜品，同时插入菜品对应的数据，需要操作两张表: dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    // 根据dishId查询 菜品信息和口味信息
    public DishDto getByDishIdWithFlavor(Long dishId);

    // 更新 菜品及对应的口味信息
    void updateWithFlavor(DishDto dishDto);

    void batchDeleteByIds(List<Long> ids);
}
