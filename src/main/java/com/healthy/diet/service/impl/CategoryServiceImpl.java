package com.healthy.diet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthy.diet.common.MyCustomException;
import com.healthy.diet.entity.Category;
import com.healthy.diet.entity.Dish;
import com.healthy.diet.entity.Setmeal;
import com.healthy.diet.mapper.CategoryMapper;
import com.healthy.diet.service.CategoryService;
import com.healthy.diet.service.DishService;
import com.healthy.diet.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService{

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    // 根据id 删除分类，删除之前需要进行 关联判断
    @Override
    public void remove(Long id) {
        // 查询当前分类是否关联了 菜品或套餐，如果已经关联，则抛出业务异常
//        select count(*) from dish where category_id=?
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据分类的id 进行查询
        dishQueryWrapper.eq(Dish::getCategoryId,id);

        //  注意: count统计查询不要忘记 加上过滤条件
        int count1 = dishService.count(dishQueryWrapper);
        if (count1 > 0){
            // 当前分类 关联到其他菜品(Dish),抛出异常，且交给 全局异常处理类GlobalExceptionHander来处理
            throw new MyCustomException("当前分类已经关联到有关菜品，不能删除！");
        }

        LambdaQueryWrapper<Setmeal> mealQueryWrapper = new LambdaQueryWrapper<>();
        //  select count(*) from setmeal where category_id=?
        mealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(mealQueryWrapper);

        if (count2 > 0){
            // 抛出异常
            throw new MyCustomException("当前分类已经关联到有关套餐，不能删除当前分类！");
        }

        //  当前分类 没有关联菜品或套餐，可以正常地删除分类(调用MP 提供的remove方法)
        super.removeById(id);

    }
}