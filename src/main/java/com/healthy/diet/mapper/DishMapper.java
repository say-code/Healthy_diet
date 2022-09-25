package com.healthy.diet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthy.diet.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
