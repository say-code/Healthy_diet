package com.healthy.diet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthy.diet.common.Result;
import com.healthy.diet.entity.Employee;
import com.healthy.diet.entity.EmployeeByBusiness;
import com.healthy.diet.entity.User;
import com.healthy.diet.manage.model.Business;
import com.healthy.diet.service.UserService;
import com.healthy.diet.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


// /user/sendMsg'
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Value("${spring.mail.username}")
    private String from;   // 邮件发送人
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    // 发送邮箱验证码
    @PostMapping("/sendMsg") // sendMsg
    public Result<String> sendMsg(@RequestBody User user, HttpSession session){
        //  获取邮箱账号
        String phone = user.getPhone();

        String subject = "健康饮食登录验证码";
        if (StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            String context = "欢迎使用健康饮食服务平台，登录验证码为: " + code + ",五分钟内有效，请妥善保管!";

             log.info("code={}",code);

             // 真正地发送邮箱验证码
             userService.sendMsg(phone,subject,context);

            //  将随机生成的验证码保存到session中
           session.setAttribute(phone,code);

            // 验证码由保存到session 优化为 缓存到Redis中，并且设置验证码的有效时间为 5分钟
            // redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            String sessionId = session.getId().toString();

            return Result.success(sessionId);
        }

        return Result.error("验证码发送失败，请重新输入!");
    }

    // 消费者端 用户登录
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session){

        log.info("userMap:{}",map.toString());
        // 获取登录表单的 邮箱账号
        String phone = map.get("phone").toString();
        // 获取 验证码
        String code = map.get("code").toString();

        // 从Session中 获取保存的验证码,session 邮箱账号为 key，验证码为value
       Object codeInSession = session.getAttribute(phone);
        log.info("codeInSession:{}",codeInSession.toString());
        // 从Redis中获取缓存验证码
        // Object codeInSession = redisTemplate.opsForValue().get(phone);

        //  页面提交的验证码 和 Session中保存的验证码 进行比对
        if (codeInSession != null && codeInSession.equals(code)){
            //  验证比对无误后，可以成功登录
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if (user == null){  // 数据库中没有当前用户，即当前用户为新用户
                //  新用户 自动注册，其信息保存到数据库中
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);  // 设置用户的状态为1，表示用户可以正常使用，为0，则禁用

                userService.save(user);
            }
            // 用户保存到数据库中后，会自动生成userId,
            session.setAttribute("user",user.getId());

            // 如果用户登录成功，则删除Redis中缓存的验证码
            // redisTemplate.delete(phone);

            //  需要在浏览器端保存用户信息，故返回的数据类型为 Result<User>
            return Result.success(user);

        }
        return Result.error("登录失败，请重新登录!");
    }

    // loginout
    @PostMapping("/loginout")
    public Result<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("userPhone");
        return Result.success("安全退出成功！");
    }

    @PostMapping("/all")
    public Result<HashMap<String, Object>> getAllUerByPage(@RequestBody HashMap<String, Integer> hashMap){
        Integer currentPage = hashMap.get("page");
        Integer pageSize = hashMap.get("pageSize");
        log.info(String.valueOf(currentPage));
        Integer total = userService.count();
        Page<User> userPage = new Page<>(currentPage,pageSize,total);
        userPage = userService.page(userPage);
        List<User> userList = userPage.getRecords();


        return new Result<HashMap<String, Object>>(){{
            setCode(200);
            setData(new HashMap<String,Object>(){{
                put("list",userList);
                put("paper",
                        new HashMap<String, Integer>(2){{
                            put("pageSize",currentPage);
                            put("total",total);
                        }});
            }});
            setMsg("6");
        }};
    }

    /**
     * 修改用户信息
     * @param user user
     * @return result
     */
    @PostMapping("alter")
    public Result<String> alterUser(@RequestBody User user){
        userService.saveOrUpdate(user);
        return new Result<String>(){{
            setCode(200);
            setMsg("更新成功");
        }};
    }



}
