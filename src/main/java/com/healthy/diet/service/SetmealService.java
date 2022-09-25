package com.healthy.diet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthy.diet.dto.SetmealDto;
import com.healthy.diet.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    // 新增套餐， 同时需要 保存套餐和菜品的关联 关系
    public void saveWithDish(SetmealDto setmealDto);

    // 删除套餐，还要 删除 套餐和菜品的 关联数据
    public void removeWithDish(List<Long> ids);

    void batchDeleteByIds(List<Long> ids);

    SetmealDto getSetmealData(Long id);
}
