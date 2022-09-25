package com.healthy.diet.controller;
import com.healthy.diet.common.Result;
import com.healthy.diet.entity.home;
import com.healthy.diet.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
  *首页
 */
@RestController
@RequestMapping("/Home")
public class HomeController {
    @Autowired
      private HomeService homeService;

    /**
     *  菜品数量
     * @param home
     * @return
     */
    @GetMapping("/numdish")
    public Result<String> numdish(@RequestBody home home){

        return Result.success("yes");
    }

    /**
     * 销售额
     * @param home
     * @return
     */
    @GetMapping("/sales")
    public Result<String> sales(@RequestBody home home){

        return Result.success("yes");
    }

    /**
     * 订单数量
     * @param home
     * @return
     */
    @GetMapping("/numorders")
    public Result<String> numorders(@RequestBody home home){

        return Result.success("yes");
    }
}
