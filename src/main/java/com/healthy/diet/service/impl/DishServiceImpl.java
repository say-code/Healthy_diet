package com.healthy.diet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthy.diet.common.MyCustomException;
import com.healthy.diet.dto.DishDto;
import com.healthy.diet.entity.Dish;
import com.healthy.diet.entity.DishFlavor;
import com.healthy.diet.mapper.DishMapper;
import com.healthy.diet.service.DishFlavorService;
import com.healthy.diet.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Override
    // 由于涉及到对dish、dish_flavor两张表的操作，应该使用 @Transactional 来标注事务
    @Transactional  //  让@Transactional 生效，还需要在启动类添加@EnableTransactionManagement 来开启事务
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        log.info("this = " + this);

        Long dishId = dishDto.getId(); //  获取前端传过来的 dishId

        // 通过Debug的方式，发现前端传过来的 flavors 并不包含 dishId,故dish需要另外赋值
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((flavor)->{  //  flavor 为遍历出来的 每个DishFlavor对象
           flavor.setDishId(dishId);
           return flavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByDishIdWithFlavor(Long dishId) {
        // 只是关联查询两张表，没有涉及事务，不用加 @Transactional
        // 从dish表中查询 菜品的基本信息
        Dish dish = this.getById(dishId);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        //  从dish_flavor表查询 当前菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishId);

        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(list);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表
        this.updateById(dishDto);

        // 删除当前菜品对应的口味数据，dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        // 添加 前端提交过来的口味数据，insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> flavorList = flavors.stream().map((flavor) -> {
            flavor.setDishId(dishDto.getId());
            return flavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavorList);
    }

    @Override
    public void batchDeleteByIds(List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Dish::getId,ids);

        //  mybatisplus提供了 list方法，故 this.list(queryWrapper); -->dishService.list(queryWrapper);
        List<Dish> list = this.list(queryWrapper);

        if (list != null){
            for (Dish dish : list) {
                if (dish.getStatus() == 0){
                    this.removeByIds(ids);
                }else {
                    throw new MyCustomException("有菜品正在售卖，无法全部删除！");
                }
            }
        }


    }
}
