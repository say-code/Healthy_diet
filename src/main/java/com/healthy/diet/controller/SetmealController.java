package com.healthy.diet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthy.diet.common.Result;
import com.healthy.diet.dto.SetmealDto;
import com.healthy.diet.entity.Category;
import com.healthy.diet.entity.Setmeal;
import com.healthy.diet.service.CategoryService;
import com.healthy.diet.service.SetmealDishService;
import com.healthy.diet.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
//套餐管理
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    // 当前端传输过来的 JSON数据 与 对应实体类 Setmeal中属性有所不同时，可以使用SetmealDto，
    //   SetmealDto 继承Setmeal，并添加 Setmeal中没有的JSON数据
    @PostMapping
    // @CacheEvict(value = "setmealCache",allEntries = true)
    public Result<String> save(@RequestBody SetmealDto setmealDto, HttpServletRequest request){

        log.info("套餐信息:{}",setmealDto);
        setmealDto.setBusinessId(request.getSession().getAttribute("businessId").toString());
        setmealService.saveWithDish(setmealDto);
        return Result.success("套餐添加 成功！");
    }

    // 套餐Setmeal 分页查询
    @GetMapping("/page")
    public Result<Page> showPage(int page,int pageSize,String name,HttpServletRequest request){

        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>(page,pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 根据name 进行 like模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
        queryWrapper.eq(Setmeal::getBusinessId, request.getSession().getAttribute("businessId").toString());

        setmealService.page(setmealPage,queryWrapper);

        BeanUtils.copyProperties(setmealPage,dtoPage,"records");

        List<Setmeal> records = setmealPage.getRecords();

        List<SetmealDto> dtoList = records.stream().map((record) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(record, setmealDto);

            // 根据分类id查询 分类对象
            Category category = categoryService.getById(record.getCategoryId());
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoList);

        return Result.success(dtoPage);
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)   //  删除套餐，就要删除套餐相关的所有缓存数据
    public Result<String> delete(@RequestParam List<Long> ids){

        log.info("ids = " + ids);

        setmealService.removeWithDish(ids);

        return Result.success("成功删除套餐！");
    }
    // 前端发送的请求：http://localhost:8181/setmeal/list?categoryId=1516353794261180417&status=1
    // 注意: 请求后的参数 是以key-value键值对的方式 传入，而非JSON格式，不需要使用@RequestBody 来标注，
    //   只需要用包含 参数(key)的实体对象接收即可
    @GetMapping("/list")  // 在消费者端 展示套餐信息
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_' +#setmeal.status")
    public Result<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        Long categoryId = setmeal.getCategoryId();
        Integer status = setmeal.getStatus();
        queryWrapper.eq(categoryId != null,Setmeal::getCategoryId,categoryId);
        queryWrapper.eq(status != null,Setmeal::getStatus,status);

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> setmeals = setmealService.list(queryWrapper);

        return Result.success(setmeals);
    }

    // http://localhost:8181/setmeal/status/0?ids=1415580119015145474
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable("status") Integer status,@RequestParam("ids") List<Long> ids){

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Setmeal::getId,ids);

        List<Setmeal> list = setmealService.list(queryWrapper);
        if (list != null){
            for (Setmeal setmeal : list) {
                setmeal.setStatus(status);
                setmealService.updateById(setmeal);
            }
            return Result.success("套餐状态修改成功！");
        }

        return Result.error("套餐状态不能修改,请联系管理或客服！");
    }

    // http://localhost:8181/setmeal/1516369910723248130
    @GetMapping("/{id}")
    public Result<SetmealDto> getSetmel(@PathVariable("id") Long id){
        SetmealDto setmealDto = setmealService.getSetmealData(id);
        return Result.success(setmealDto);
    }

    @PutMapping
    public Result<String> updateMeal(@RequestBody SetmealDto setmealDto){
        setmealService.updateById(setmealDto);
        return Result.success("套餐修改成功！");
    }


    //套餐的批量删除
//    @DeleteMapping
//    public Result<String> batchDelete(@RequestParam List<Long> ids){
//        setmealService.batchDeleteByIds(ids);
//        return Result.success("套餐删除成功!");
//    }



}
