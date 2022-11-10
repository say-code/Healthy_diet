package com.healthy.diet.manage.controller;

import com.healthy.diet.manage.model.Business;
import com.healthy.diet.manage.model.Response;
import com.healthy.diet.manage.service.IBusinessService;
import com.healthy.diet.manage.utils.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author sayCode
 * @date 2022/9/26 19:22
 * project: Heathy_diet
 * Title: BusinessController
 * description: 商家事务控制层
 */
@Slf4j
@RestController
@RequestMapping("business")
public class BusinessController {

    @Autowired
    private IBusinessService iBusinessService;

    /**
     * 插入公司数据
     * @param business 公司数据
     * @return 是否插入成功
     */
    @PostMapping("insert")
    public Response insert(@RequestBody Business business){
        String uuid = UUID.randomUUID().toString();
        business.setBusinessId(uuid);
        if (iBusinessService.businessInsert(business)>0){
            return Response.success();
        }
        log.info("插入错误！");
        return Response.error();
    }

    /**
     * 查找公司 信息
     * @return 公司信息
     */
    @GetMapping("select")
    public Response businessSelectAll(){
        return Response.success(iBusinessService.businessNameSelectAll());
    }
}
