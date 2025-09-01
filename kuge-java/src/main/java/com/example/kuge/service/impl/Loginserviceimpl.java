package com.example.kuge.service.impl;

import cn.hutool.log.Log;
import com.example.kuge.mapper.LoginMapper;
import com.example.kuge.pojo.Result;
import com.example.kuge.pojo.User;
import com.example.kuge.service.Loginservice;
import com.example.kuge.util.CreateAccount;
import com.example.kuge.util.CreateUser;
import com.example.kuge.util.JwtUtil;
import com.example.kuge.util.PassWordUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class Loginserviceimpl implements Loginservice {
    @Autowired
    LoginMapper loginMapper;
    @Autowired
    RedisTemplate<String,Integer> redisTemplate;
    @Autowired
    RedisTemplate<String,String> stringRedisTemplate;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    ObjectMapper objectMapper;
    @Override
    public String login(String user_account, String user_password) {
        UsernamePasswordAuthenticationToken authenticationToken =new UsernamePasswordAuthenticationToken(user_account,user_password);
        Authentication authentication=authenticationManager.authenticate(authenticationToken);
        if(Objects.isNull(authentication)){
            throw new RuntimeException("登录失败");
        }
        User user=loginMapper.findByAccount(user_account);
        Map<String,Object> map=new HashMap<>();
        map.put("id",user.getUser_id());
        String token=JwtUtil.getToken(map);
        String user_id="kuge:user:"+user.getUser_id();
        try {
            String json=objectMapper.writeValueAsString(user);
            stringRedisTemplate.opsForValue().set(user_id, json,12, TimeUnit.HOURS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("不存在user用户");
        }
        return token;
    }

    @Override
    public User findByAccount(String userAccount) {
        return loginMapper.findByAccount(userAccount);
    }

    @Override
    public String createNewAccount(User user) {
        Integer maxId=redisTemplate.opsForValue().get("maxid");
        redisTemplate.opsForValue().increment("maxid");
        String user_account = CreateAccount.Create(maxId);
        User user1 = CreateUser.create(user, user_account);
        loginMapper.insertByaccount(user1);
        return user_account;
    }

    @Override
    public void Logout(String token) {
        int id=JwtUtil.getid(token);
        String user_id="kuge:user:"+id;
        redisTemplate.delete(user_id);
    }
}
