package com.example.kuge.service.impl;

import com.example.kuge.service.Emailservice;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class Emailserviceimpl implements Emailservice {
    @Autowired
    private JavaMailSenderImpl mailSender;
    @Value("${spring.mail.username}")
    private String form;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    public void sandemail(String email,String captcha){
        if(redisTemplate.opsForValue().get(email)!=null){
            throw new RuntimeException("验证码已发送，60秒后重试");
        }
        redisTemplate.opsForValue().set(email,captcha,1, TimeUnit.MINUTES);
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(form);
        message.setTo(email);
        message.setSubject("[你的验证码]");
        message.setText("你的验证码是：" + captcha + "，1分钟内有效，请勿泄露给他人。");
        mailSender.send(message);
    }
    public String getcaptcah(String email){
        String captcha=redisTemplate.opsForValue().get(email);
        redisTemplate.delete(email);
        return captcha;
    }
}
