package com.example.kuge.service.impl;
import com.example.kuge.pojo.MusicBase;
import com.example.kuge.pojo.PageBean;
import com.example.kuge.service.Historyservice;
import com.example.kuge.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class Historyserviceimpl implements Historyservice {
    @Autowired
    private RedisTemplate<String,String> stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    private static final int HISTORY_KEY_MAX=50;
    private static final int HISTORY_TTL_DAYS = 30;
    @Override
    public void AddHistoryMusic(String token, MusicBase musicBase) throws JsonProcessingException {
        int userId = JwtUtil.getid(token);
        String key = "history:user:" + userId;
        List<String> jsonList = stringRedisTemplate.opsForList().range(key, 0, -1);
        List<MusicBase> historyList = new ArrayList<>();
        if (jsonList != null && !jsonList.isEmpty()) {
            for (String json : jsonList) {
                historyList.add(objectMapper.readValue(json, MusicBase.class));
            }
        }
        boolean exists = false;
        int index = 0;
        if (!historyList.isEmpty()) {
            for (MusicBase item : historyList) {
                if (musicBase.getMusic_id()==item.getMusic_id()) {
                    exists = true;
                    break;
                }
                index++;
            }
        }
        if (exists) {
            stringRedisTemplate.opsForList().remove(key, 1, jsonList.get(index));
        }
        String musicJson = objectMapper.writeValueAsString(musicBase);
        stringRedisTemplate.opsForList().leftPush(key, musicJson);
        stringRedisTemplate.expire(key, HISTORY_TTL_DAYS, TimeUnit.DAYS);
        trimHistoryList(key);
    }

    @Override
    public PageBean<MusicBase> GetHistory(String token, Integer pageNum, Integer pagesize) throws JsonProcessingException {
        int userId = JwtUtil.getid(token);
        String key = "history:user:" + userId;
        int start = (pageNum - 1) * pagesize;
        int end = start + pagesize - 1;
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pagesize == null || pagesize < 1) {
            pagesize = 10;
        }
        List<String> jsonList = stringRedisTemplate.opsForList().range(key, start, end);
        List<MusicBase> historyList = new ArrayList<>();
        if (jsonList != null && !jsonList.isEmpty()) {
            for (String json : jsonList) {
                    MusicBase music = objectMapper.readValue(json, MusicBase.class);
                    historyList.add(music);
                }
            }
        Long total = stringRedisTemplate.opsForList().size(key);
        if (total == null) {
            total = 0L;
        }
        PageBean<MusicBase> pageBean = new PageBean<>();
        pageBean.setTotal(total);
        pageBean.setItems(historyList);
        return pageBean;
    }
    private void trimHistoryList(String key) {
        stringRedisTemplate.opsForList().trim(key, 0, HISTORY_KEY_MAX - 1);
    }
}
