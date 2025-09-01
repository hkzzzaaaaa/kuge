package com.example.kuge.controller;

import com.example.kuge.pojo.*;
import com.example.kuge.service.Musicservice;
import com.example.kuge.util.FileTypesUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/music")
public class MusicController {
    @Autowired
    Musicservice musicservice;
    @Autowired
    FileTypesUtil fileTypesUtil;
    @Value("${upload.allowed-file-types}")
    private String allowedFileTypes;
    @PostMapping("/addmusic")
    public Result addmusic(@RequestHeader(value = "Authorization") String token,
                           @RequestParam("musicname") String music_name,
                           @RequestParam("albumname") String album_name,
                           @RequestParam("vip") Integer vip,
                           @RequestParam("file") MultipartFile file
                           )throws JsonProcessingException {
           fileTypesUtil.inspect(file,allowedFileTypes);
           musicservice.addmusic(token,music_name,album_name,vip,file);
           return Result.success();
    }
    @GetMapping("/findmusicpublic")
    public Result<PageBean<MusicPublicView>> findmusicpublic(
            @RequestHeader(value = "Authorization") String token,
            Integer pageNum,
            Integer pagesize
    ){
            PageBean<MusicPublicView> pageBean =musicservice.findmusicpublic(token,pageNum,pagesize);
            return Result.success(pageBean);
    }
    @GetMapping("/findMusicByName")
    public Result<PageBean<MusicPublicView>> findMusicByName(
            @RequestHeader(value = "Authorization") String token,
            @RequestParam(value = "pageNum") Integer pageNum,
            @RequestParam(value = "pagesize") Integer pagesize,
            @RequestParam(value = "music_name") String music_name
    ) throws JsonProcessingException {
        PageBean<MusicPublicView> pageBean =musicservice.findMusicByName(token,music_name,pageNum,pagesize);
        return Result.success(pageBean);
    }
    @GetMapping("/findMusicBaseService")
    public Result<PageBean<MusicBase>> findMusicBaseService(
            Integer pageNum,
            Integer pagesize
    ) throws JsonProcessingException {
        PageBean<MusicBase> pageBean =musicservice.findMusicBaseService(pageNum,pagesize);
        return Result.success(pageBean);
    }
    @GetMapping("/findMusic")
    public Result<PageBean<MusicBase>> findMusic(
            @RequestHeader(value = "Authorization") String token,
            Integer pageNum,
            Integer pagesize,
            String music_name
    ) throws IOException {
        PageBean<MusicBase> pageBean =musicservice.findMusic(pageNum,pagesize,music_name,token);
        return Result.success(pageBean);
    }
    @PutMapping("putFavourite")
    public  Result putFavourite(
            @RequestHeader(value = "Authorization") String token,
            Integer music_id
    ) throws JsonProcessingException {
        musicservice.putFavourite(token,music_id);
        return Result.success();
    }
    @GetMapping("getFavourite")
    public Result<PageBean<MusicBase>> getFavourite(
            @RequestHeader(value = "Authorization") String token,
            Integer pageNum,
            Integer pagesize
            ) throws JsonProcessingException {
        PageBean<MusicBase> pageBean =musicservice.getFavourite(token,pageNum,pagesize);
        return Result.success(pageBean);
    }
    @PutMapping("deleteFavourite")
    public  Result<PageBean<MusicBase>> deleteFavourite(
            @RequestHeader(value = "Authorization") String token,
            String music_name,
            String user_name
    )throws JsonProcessingException {
        musicservice.deleteFavourite(token,music_name,user_name);
        return Result.success();
    }
}
