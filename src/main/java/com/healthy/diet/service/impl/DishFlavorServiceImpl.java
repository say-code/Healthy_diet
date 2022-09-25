package com.healthy.diet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthy.diet.entity.DishFlavor;
import com.healthy.diet.mapper.DishFlavorMapper;
import com.healthy.diet.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
        implements DishFlavorService {
}
