package com.example.kuge.service;
import com.example.kuge.pojo.MusicBase;
import com.example.kuge.pojo.MusicPublicView;
import com.example.kuge.pojo.PageBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface Musicservice {
    void addmusic(String token, String music_name, String album_name, Integer vip, MultipartFile file) throws JsonProcessingException;
    PageBean<MusicPublicView> findmusicpublic(String token, Integer pageNum, Integer pagesize);

    PageBean<MusicPublicView> findMusicByName(String token, String musicName, Integer pageNum, Integer pagesize) throws JsonProcessingException;

    PageBean<MusicBase> findMusicBaseService(Integer pageNum, Integer pagesize) throws JsonProcessingException;

    PageBean<MusicBase> findMusic(Integer pageNum, Integer pagesize,String music_name,String token) throws IOException;

    void putFavourite(String token, Integer music_id) throws JsonProcessingException;

    PageBean<MusicBase> getFavourite(String token, Integer pageNum, Integer pagesize) throws JsonProcessingException;

    void deleteFavourite(String token, String musicName, String userName) throws JsonProcessingException;
}
