package com.healthy.diet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthy.diet.entity.ShoppingCart;
import com.healthy.diet.mapper.ShoppingCartMapper;
import com.healthy.diet.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
     implements ShoppingCartService {
}
