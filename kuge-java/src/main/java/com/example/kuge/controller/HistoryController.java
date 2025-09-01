package com.example.kuge.controller;

import com.example.kuge.pojo.MusicBase;
import com.example.kuge.pojo.PageBean;
import com.example.kuge.pojo.Result;
import com.example.kuge.service.Historyservice;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/History")
public class HistoryController {
    @Autowired
    Historyservice historyservice;
    @PutMapping("/add")
    public Result AddHistoryMusic(@RequestHeader(value = "Authorization") String token,
                                  @RequestBody MusicBase musicBase
                                  ) throws JsonProcessingException {
        historyservice.AddHistoryMusic(token,musicBase);
        return Result.success();
    }
    @GetMapping("/get")
    public Result<PageBean<MusicBase>> GetHistory(
            @RequestHeader(value = "Authorization") String token,
            Integer pageNum,
            Integer pagesize
    ) throws JsonProcessingException{
        PageBean<MusicBase> pageBean =historyservice.GetHistory(token,pageNum,pagesize);
        return Result.success(pageBean);
    }
}
