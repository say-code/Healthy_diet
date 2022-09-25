package com.healthy.diet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthy.diet.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
