package com.example.demo.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import com.example.demo.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

/**
 * 用于发送邮箱验证码
 * @author Geed
 */
@Slf4j
@RestController
@RequestMapping("/mail")
public class MailController {

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender javaMailSender;

    private void sendSimpleMail(String to, String content){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setCc(from);
        mailMessage.setTo(to);
        mailMessage.setSubject("注册验证码~~~");
        mailMessage.setText(content);
        javaMailSender.send(mailMessage);
        log.info("邮件发送成功");
    }

    @PostMapping("/getCheckCode")
    public Result<?> getCheckCode(@RequestBody String mail){
        log.info("进入方法getCheckCode:"+mail);
        String checkCode = RandomUtil.randomString(6);

        System.out.println(checkCode+" "+URLUtil.decode(mail));
        String message = "欢迎注册Geed‘s Blog账号。\n您的注册邮箱验证码为："+checkCode;
        try {
            sendSimpleMail(mail, message);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("-1", "发送验证码失败");
        }
        return Result.success(checkCode);
    }
}