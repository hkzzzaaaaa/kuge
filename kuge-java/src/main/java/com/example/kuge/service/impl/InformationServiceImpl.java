package com.example.kuge.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.kuge.mapper.InformationMapper;
import com.example.kuge.pojo.User;
import com.example.kuge.service.InformationService;
import com.example.kuge.util.JwtUtil;
import com.example.kuge.util.PassWordUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class InformationServiceImpl implements InformationService {
    @Autowired
    RedisTemplate<String,String> stringRedisTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    InformationMapper informationMapper;
    @Override
    public User getUser(String token) throws JsonProcessingException {
        String user_id= "kuge:user:";
        Map<String, Object> map = JwtUtil.parseToken(token);
        Integer id = (Integer) map.get("id");
        user_id = user_id+ id;
        String json=stringRedisTemplate.opsForValue().get(user_id);
        User user=objectMapper.readValue(json, User.class);
        stringRedisTemplate.expire(user_id,12, TimeUnit.HOURS);
        return user;
    }

    @Override
    public void updateinform(String token, String userName, Integer gender, String province, String city) throws JsonProcessingException {
        String user_id= "kuge:user:";
        Map<String, Object> map = JwtUtil.parseToken(token);
        Integer id = (Integer) map.get("id");
        user_id = user_id+ id;
        String json=stringRedisTemplate.opsForValue().get(user_id);
        User user=objectMapper.readValue(json, User.class);
        user.setUser_name(userName);
        user.setGender(gender);
        user.setProvince(province);
        user.setCity(city);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", user.getUser_id());
        informationMapper.update(user,updateWrapper);
        String json1=objectMapper.writeValueAsString(user);
        stringRedisTemplate.opsForValue().set(user_id, json1,12, TimeUnit.HOURS);
    }

    @Override
    public void updatesign(String token, String signature) throws JsonProcessingException {
        String user_id= "kuge:user:";
        Map<String, Object> map = JwtUtil.parseToken(token);
        Integer id = (Integer) map.get("id");
        user_id = user_id+ id;
        String json=stringRedisTemplate.opsForValue().get(user_id);
        User user=objectMapper.readValue(json, User.class);
        user.setSignature(signature);
        informationMapper.updateById(user);
        String json1=objectMapper.writeValueAsString(user);
        stringRedisTemplate.opsForValue().set(user_id, json1,12, TimeUnit.HOURS);
    }

    @Override
    public void updatepassword(String token, String password,String password1) throws JsonProcessingException {
        String user_id= "kuge:user:";
        Map<String, Object> map = JwtUtil.parseToken(token);
        Integer id = (Integer) map.get("id");
        user_id = user_id+ id;
        String json=stringRedisTemplate.opsForValue().get(user_id);
        User user=objectMapper.readValue(json, User.class);
        User user2=informationMapper.selectById(user.getUser_id());
        if(PassWordUtil.matches(password,user2.getUser_password())){
            password1=PassWordUtil.encode(password1);
            user2.setUser_password(password1);
            informationMapper.updateById(user2);
        }
        else{
            throw new RuntimeException("原密码错误");
        }
    }

    @Override
    public void updateimage(String token, String imageUrl) throws JsonProcessingException {
        String user_id= "kuge:user:";
        Map<String, Object> map = JwtUtil.parseToken(token);
        Integer id = (Integer) map.get("id");
        user_id = user_id+ id;
        String json=stringRedisTemplate.opsForValue().get(user_id);
        User user=objectMapper.readValue(json, User.class);
        user.setUser_avatar(imageUrl);
        informationMapper.updateById(user);
        String json1=objectMapper.writeValueAsString(user);
        stringRedisTemplate.opsForValue().set(user_id, json1,12, TimeUnit.HOURS);
    }
}
