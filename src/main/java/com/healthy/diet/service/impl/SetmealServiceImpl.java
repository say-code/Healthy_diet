package com.healthy.diet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthy.diet.common.MyCustomException;
import com.healthy.diet.dto.SetmealDto;
import com.healthy.diet.entity.Setmeal;
import com.healthy.diet.entity.SetmealDish;
import com.healthy.diet.mapper.SetmealMapper;
import com.healthy.diet.service.SetmealDishService;
import com.healthy.diet.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐信息，setmeal 执行insert操作
        this.save(setmealDto); // setmealService.save(setmealDto);

        // 在浏览器的控制台 可以看出，SetmealDish对象没有setmealId,需要通过setmealDto 来获取setmealId
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((mealDishs)->{
            mealDishs.setSetmealId(setmealDto.getId());
            mealDishs.setBusinessId(setmealDto.getBusinessId());
            return mealDishs;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    // 删除套餐，还要 删除 套餐和菜品的 关联数据
    @Transactional
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        // 套餐的 status == 1，表示套餐正在售卖，不能删除，如果非要删除，需要停售套餐
        queryWrapper.eq(Setmeal::getStatus,1);
        queryWrapper.in(Setmeal::getId,ids);

        int count = this.count(queryWrapper);
        if (count > 0){
            throw new MyCustomException("有套餐正在售卖，不能删除！");
        }
        // 如果可以删除套餐，应该先操作setmeal表
        this.removeByIds(ids);

        // 删除套餐和菜品的关联数据
        //  delete from setmeal_dish where setmeal_id in ( id1,id2,id3)
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //  ids 是套餐id, 只有当 setmeal_dish表中 的 setmealId 是ids中的 一个或多个时，才会真正删除 关联数据
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(setmealDishLambdaQueryWrapper);


    }

    @Override
    public void batchDeleteByIds(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Setmeal::getId,ids);

        List<Setmeal> list = this.list(queryWrapper);
        if (list != null){
            for (Setmeal setmeal : list) {
                if (setmeal.getStatus() == 0){
                    this.removeById(setmeal.getId());
                }else {
                    throw new MyCustomException("有套餐正在售卖，不能删除全部套餐！");
                }
            }
        }
    }

    @Override
    public SetmealDto getSetmealData(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null,SetmealDish::getSetmealId,id);


        if (setmeal != null){
            BeanUtils.copyProperties(setmeal,setmealDto);

            List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
            setmealDto.setSetmealDishes(dishes);

            return setmealDto;
        }

        return null;
    }


}
