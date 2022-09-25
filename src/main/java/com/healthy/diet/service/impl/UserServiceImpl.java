package com.healthy.diet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthy.diet.entity.User;
import com.healthy.diet.mapper.UserMapper;
import com.healthy.diet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Value("${spring.mail.username}")
    private String from;   // 邮件发送人

    @Autowired
    private JavaMailSender mailSender;


    @Override
    public void sendMsg(String to,String subject,String context){
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(context);

        // 真正的发送邮件操作，从 from到 to
        mailSender.send(mailMessage);

    }

}


