package com.healthy.diet.controller;

import com.healthy.diet.common.Result;
import com.healthy.diet.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sayCode
 * @date 2022/11/7 17:53
 * @project Heathy_diet
 * @Title WxClientController
 * @description TODO
 */

@RestController
@RequestMapping("/wx/api")
public class WxClientController {

    @Autowired
    DishService dishService;

    @GetMapping("/getSessionId")
    public Result<String> getSessionId(HttpServletRequest request){
        return Result.success(request.getSession().getId());
    }
}
