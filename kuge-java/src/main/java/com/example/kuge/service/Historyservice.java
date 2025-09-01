package com.example.kuge.service;

import com.example.kuge.pojo.MusicBase;
import com.example.kuge.pojo.PageBean;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface Historyservice {
    void AddHistoryMusic(String token, MusicBase musicBase)  throws JsonProcessingException;

    PageBean<MusicBase> GetHistory(String token, Integer pageNum, Integer pagesize) throws JsonProcessingException;
}
