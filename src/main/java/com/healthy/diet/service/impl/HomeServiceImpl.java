package com.healthy.diet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthy.diet.entity.home;
import com.healthy.diet.mapper.HomeMapper;
import com.healthy.diet.service.HomeService;
import org.springframework.stereotype.Service;

@Service
public class HomeServiceImpl extends ServiceImpl<HomeMapper,home>
        implements HomeService {

}
