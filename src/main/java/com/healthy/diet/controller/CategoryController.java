package com.healthy.diet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthy.diet.common.Result;
import com.healthy.diet.entity.Category;
import com.healthy.diet.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

//菜品分类管理
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // @RequestBody: 将前端回传的JSON数据需要使用@RequestBody 转化为 实体对象
    @PostMapping
    public Result<String> save(@RequestBody Category category, HttpServletRequest request){
        log.info("category:{}",category);
        category.setBusinessId(request.getSession().getAttribute("businessId").toString());
        categoryService.save(category);
        return Result.success("成功新增分类！");

    }

    @GetMapping("/page")
    public Result<Page> showPage(int page, int pageSize, HttpServletRequest request){
        Page<Category> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        //  根据 Category对象的sort字段 来排序展示
        queryWrapper.orderByAsc(Category::getSort);
        queryWrapper.eq(Category::getBusinessId,request.getSession().getAttribute("businessId"));
        categoryService.page(pageInfo,queryWrapper);

        return Result.success(pageInfo);
    }

    //  删除分类:在分类管理列表页面中，
    //  需要注意: 当分类关联了菜品或者套餐时(需要引入Dish、Setmeal对应的实体类、Mapper、Service)，此分类不许删除

    // 根据分类id 来删除分类
    @DeleteMapping
    public Result<String> delete(Long ids){
        log.info("删除分类，分类id为: {}",ids);

//        categoryService.removeById(id);
        categoryService.remove(ids);

        return Result.success("成功删除分类信息！");
    }

    @PutMapping
    public Result<String> update(@RequestBody Category category){
        log.info("修改分类信息:{}",category);

        categoryService.updateById(category);
        return Result.success("分类信息 修改成功！");
    }

    // 根据条件查询分类数据
    @GetMapping("/list")
    public Result<List<Category>> categoryList(Category category, String businessId){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //  只有当 category.getType()不为空，才会比较前端传入的category的type和 实体类中 type属性是否相等
        queryWrapper.eq(category.getType() != null, Category::getType,category.getType());
        queryWrapper.eq(Category::getBusinessId,businessId);
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return Result.success(list);
    }
    // 前端传输到服务端的数据 和实体类中的属性 不是一一对应关系，
    // 需要用到DTO(Data Transfer Object)对象，即数据传输对象，一般用于Controller和Service层之间的数据传输



}
