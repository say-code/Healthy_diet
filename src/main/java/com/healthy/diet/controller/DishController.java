package com.healthy.diet.controller;

// 新增菜品:
// 1、页面发送Ajax请求，请求服务端获取菜品 的分类数据 并展示到下拉框中
// 2、页面发送请求进行图片上传，请求服务端将图片 保存到服务器
// 3、页面发送请求 进行图片下载，将上传的图片进行回显
// 4、点击保存按钮，发送Ajax请求，将菜品相关数据 以JSON形式提交到服务器

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthy.diet.common.Result;
import com.healthy.diet.dto.DishDto;
import com.healthy.diet.entity.Category;
import com.healthy.diet.entity.Dish;
import com.healthy.diet.entity.DishFlavor;
import com.healthy.diet.entity.Employee;
import com.healthy.diet.manage.service.IBusinessService;
import com.healthy.diet.service.CategoryService;
import com.healthy.diet.service.DishFlavorService;
import com.healthy.diet.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IBusinessService businessService;

    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto, HttpServletRequest request){
        log.info(dishDto.toString());
        dishDto.setBusinessId(request.getSession().getAttribute("businessId").toString());
        dishService.saveWithFlavor(dishDto);

        // 清理 后台修改分类 下面的菜品缓存数据
        // String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        // redisTemplate.delete(key);

        return Result.success("新增菜品操作成功！");
    }

    //  分页展示菜品信息
    // dish/page?page=1&pageSize=10&name=122334,name 是搜索框中的输入值
    @GetMapping("/page")
    public Result<Page> pageShow(@RequestParam("page") int page,@RequestParam("pageSize") int pageSize,String name, @RequestParam("type")Integer type,HttpServletRequest request){

        Page<Dish> dishPage = new Page<>(page,pageSize);
        Page<DishDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.eq(name != null,Dish::getName,name);
        queryWrapper.eq(type != null,Dish::getMealType,type);
        Object businessId = request.getSession().getAttribute("businessId");
        if (businessId!=null ){
            if (!"".equals(businessId.toString())){
                queryWrapper.eq(Dish::getBusinessId, businessId.toString());
            }
        }


        //  执行分页查询
        dishService.page(dishPage,queryWrapper);

        //  将 dish 中的属性值复制到 dtoPage，但是忽略 records
        //  records需要另外去设置
        BeanUtils.copyProperties(dishPage,dtoPage,"records");

        List<Dish> records = dishPage.getRecords();
        List<DishDto> dtoList = records.stream().map((dish) -> {  // dish 为每个菜品对象
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(dish,dishDto);

            Long categoryId = dish.getCategoryId();  // 菜品的分类id

            Category category = categoryService.getById(categoryId);
           if (category != null){
               dishDto.setCategoryName(category.getName());
           }

           String business = dishDto.getBusinessId();
           String businessName = businessService.businessNameSelectByBusinessId(business);
           if (businessName!=null){
               dishDto.setBusinessName(businessName);
           }
            return dishDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return Result.success(dtoPage);
    }

    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByDishIdWithFlavor(id);

        return Result.success(dishDto);
    }

    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        // 清理 后台修改分类 下面的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        // redisTemplate.delete(key);

        return Result.success("修改菜品操作成功！");
    }


//    @GetMapping("/list")
//    public Result<List<Dish>> list(Dish dish){
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        Long categoryId = dish.getCategoryId();
//        queryWrapper.eq(categoryId != null,Dish::getCategoryId,categoryId);
//
//        // status 为 1: 还在售卖的菜品
//        queryWrapper.eq(Dish::getStatus,1);
//        // 根据sort 属性升序片排列
//        queryWrapper.orderByDesc(Dish::getSort);
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return Result.success(list);
//    }

    // 根据条件(分类id)查询对应的菜品数据
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish, HttpServletRequest request){

        List<DishDto> dishDtoList = null;
        //  根据菜品的分类(湘菜、川菜) 去缓存菜品数据
        // String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

        // dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dishDtoList != null){
            return Result.success(dishDtoList);
        }

        // dishDtoList == null,即Redis中没有 对应的菜品数据，需要去查询数据库
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        Long categoryId = dish.getCategoryId();
        queryWrapper.eq(categoryId != null,Dish::getCategoryId,categoryId);
        queryWrapper.eq(Dish::getBusinessId,request.getSession().getAttribute("businessId").toString());
        // status 为 1: 还在售卖的菜品
        queryWrapper.eq(Dish::getStatus,1);
        // 根据sort 属性升序片排列
        queryWrapper.orderByDesc(Dish::getSort);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            // 每个item表示 一个菜品 dish，根据菜品的分类id 给菜品设置 菜品的分类名
            Long itemCategoryId = item.getCategoryId();
            Category category = categoryService.getById(itemCategoryId);

            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }

            // 当前菜品的id,根据dishId去查询当前菜品对应的口味
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> flavorQueryWrapper = new LambdaQueryWrapper<>();
            flavorQueryWrapper.eq(DishFlavor::getDishId, dishId);


            List<DishFlavor> dishFlavorList = dishFlavorService.list(flavorQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;

        }).collect(Collectors.toList());

        //  将查询到的菜品数据缓存到Redis,并且设置其 查询到的菜品数据有效时间为1小时，其后会清除菜品该菜品数据
        // redisTemplate.opsForValue().set(key,dishDtoList,60L,TimeUnit.MINUTES);
        // 注意: 如果RedisConfig中配置了value的 序列化方式，则存储key-value时，value应该是String类型，而非List类型

        return Result.success(dishDtoList);
    }

    // 改变菜品的销售状态
    @PostMapping("/status/{status}")
    public Result<String> updateSaleStatus(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        //  菜品具体的售卖状态 由前端修改并返回，该方法传入的status是 修改之后的售卖状态，可以直接根据一个或多个菜品id进行查询并修改售卖即可
        log.info("ids :"+ids);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Dish::getId,ids);


        List<Dish> list = dishService.list(queryWrapper);
        if (list != null){
            for (Dish dish : list) {
                dish.setStatus(status);
                dishService.updateById(dish);
            }
            return Result.success("菜品的售卖状态已更改！");
        }
        return Result.error("售卖状态不可更改,请联系管理员或客服！");

    }

    @DeleteMapping
    public Result<String> batchDelete(@RequestParam("ids") List<Long> ids){
        dishService.batchDeleteByIds(ids);

        return Result.success("成功删除菜品！");
    }

}
