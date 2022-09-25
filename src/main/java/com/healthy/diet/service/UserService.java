package com.healthy.diet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthy.diet.entity.User;

public interface UserService extends IService<User> {

    public void sendMsg(String to,String subject,String context);
}
