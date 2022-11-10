package com.healthy.diet.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthy.diet.common.Result;
import com.healthy.diet.config.ManageConfig;
import com.healthy.diet.entity.Employee;
import com.healthy.diet.entity.EmployeeByBusiness;
import com.healthy.diet.manage.model.Business;
import com.healthy.diet.manage.service.IBusinessService;
import com.healthy.diet.manage.utils.UUID;
import com.healthy.diet.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author sayCode
 * @date 2022/9/27 10:54
 * project: Heathy_diet
 * Title: ManageController
 * description: 管理员登录
 */

@Slf4j
@RestController
@RequestMapping("/manage")
public class ManageController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private IBusinessService businessService;


    /**
     * 管理员登录 用户有且只有一个
     * @param employee 账号密码信息
     * @return 是否登录成功
     */
    @PostMapping("/login")
    public Result<HashMap<String,String>> login(@RequestBody Employee employee, HttpServletRequest request){

        if (employee == null){
            return Result.error("用户名不存在！");
        }
        if(employee.getUsername().equals(ManageConfig.USERNAME) && employee.getPassword().equals(ManageConfig.PASSWORD)){
            request.getSession().setAttribute("employee", 1433223L);
            Result<HashMap<String,String>> res = new Result<>();
            res.setCode(200);
            res.add("token","root");
            HashMap<String,String>hashMap = new HashMap<>();
            hashMap.put("token","root");
            res.setData(hashMap);
            return res;
        }

        // 将从数据库
        return Result.error("账号或密码输入错误。");
    }

    /**
     * 员工信息注册 注意需要携带公司信息
     * @param employee 员工信息
     * @return 员工是否增添成功
     */
    @PostMapping("/register")
    public  Result<String> register(@RequestBody Employee employee){
        System.out.println(employee);
        log.info("新增员工，员工信息:{}",employee.toString());
        String uuid = UUID.randomUUID().toString();
        // 在新增员工操作中，对员工的密码进行初始化( MD5加密 )
        employee.setPassword(DigestUtils.md5DigestAsHex(employee.getPassword().getBytes()));
        employeeService.save(employee);
        Result result = new Result();
        result.setCode(200);
        result.setMsg("成功新增员工");
        return result;
    }


    @PostMapping("/del")
    public Result del(@RequestBody HashMap<String,String> hashMap){
        Long id = Long.valueOf(hashMap.get("ids"));
        employeeService.removeById(id);
        Result result = new Result();
        result.setCode(200);
        result.setMsg("删除成功");
        return result;
    }

    /**
     * 统计公司总数
     * @return 公司总数
     */
    @GetMapping("businessCount")
    public Result<Integer> businessCount(){
        return Result.success(businessService.businessCount());
    }

    /**
     * 统计菜品总数
     * @return 菜品总数
     */
    @GetMapping("dishCount")
    public Result<Integer>dishCount(){
        return Result.success(businessService.allBusinessDishCount());
    }

    /**
     * 统计订单数量总数
     * @return 菜品总数
     */
    @GetMapping("orderCount")
    public Result<Integer>orderCount(){
        return Result.success(businessService.allOrderCount());
    }

    @PostMapping("/select/employee")
    public Result<HashMap<String, Object>> employeeSelect(@RequestBody HashMap<String, Integer> hashMap){
        Integer currentPage = hashMap.get("page");
        Integer pageSize = hashMap.get("pageSize");
        log.info(String.valueOf(currentPage));
        Integer total = employeeService.count();
        Page<Employee> employeePage = new Page<>(currentPage,pageSize,total);
        employeePage = employeeService.page(employeePage);
        List<Business> businessList= businessService.businessNameSelectAll();
        List<Employee> employeeList = employeePage.getRecords();
        List<EmployeeByBusiness> employeeByBusinessesList = new ArrayList<>();
        for (Employee employee: employeeList
             ) {
            EmployeeByBusiness employeeByBusiness = new EmployeeByBusiness(employee, "");
            if ( !"".equals(employeeByBusiness.getEmployee().getBusinessId()) ){
                System.out.println(employeeByBusiness.getEmployee().getBusinessId());
                employeeByBusiness.setBusinessName(businessList.stream()
                        .filter(businessMessage -> employeeByBusiness.getEmployee().getBusinessId().equals(businessMessage.getBusinessId()))
                        .findAny()
                        .orElse(new Business("无","无")).getBusinessName());
            }
            employeeByBusinessesList.add(employeeByBusiness);

        }
        HashMap<String,Object> resultMap = new HashMap<>();
        HashMap<String, Integer> paperMap = new HashMap<>();
        paperMap.put("page",currentPage);
        paperMap.put("pageSize",currentPage);
        paperMap.put("total",total);
        resultMap.put("list",employeeByBusinessesList);
        resultMap.put("paper",paperMap);
        Result<HashMap<String, Object>> result = new Result<>();
        result.setCode(200);
        result.setData(resultMap);
        result.setMsg("6");
        return result;
    }
}
