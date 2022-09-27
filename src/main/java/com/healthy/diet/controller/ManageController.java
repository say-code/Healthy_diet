package com.healthy.diet.controller;

import com.healthy.diet.common.Result;
import com.healthy.diet.config.ManageConfig;
import com.healthy.diet.entity.Employee;
import com.healthy.diet.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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


    /**
     * 管理员登录 用户有且只有一个
     * @param employee 账号密码信息
     * @return 是否登录成功
     */
    @PostMapping("/login")
    public Result<Employee> login(@RequestBody Employee employee){

        if (employee == null){
            return Result.error("用户名不存在！");
        }

        if(employee.getUsername().equals(ManageConfig.USERNAME) && employee.getUsername().equals(ManageConfig.PASSWORD)){
            return Result.success(employee);
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
        log.info("新增员工，员工信息:{}",employee.toString());
        // 在新增员工操作中，对员工的密码进行初始化( MD5加密 )
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 下面设置 公共属性的值(createTime、updateTime、createUser、updateUser)交给 MyMetaObjectHandler类处理
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        Long empId = (Long) request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return Result.success("成功新增员工");
    }
}
