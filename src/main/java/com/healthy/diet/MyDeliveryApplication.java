package com.healthy.diet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement  // 开启事务，DishServiceImpl的saveWithFlavor方法
@EnableCaching    // 开启SpringCache注解方式的缓存功能
public class MyDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyDeliveryApplication.class,args);
        log.info("项目启动成功！");
    }
}
