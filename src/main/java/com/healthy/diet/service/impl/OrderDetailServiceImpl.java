package com.healthy.diet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthy.diet.entity.OrderDetail;
import com.healthy.diet.mapper.OrderDetailMapper;
import com.healthy.diet.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
     implements OrderDetailService {
}
