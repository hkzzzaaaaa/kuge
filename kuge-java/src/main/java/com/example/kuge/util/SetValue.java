package com.example.kuge.util;

import com.example.kuge.pojo.Album;
import com.example.kuge.pojo.Music;

import java.time.LocalDateTime;

public class SetValue{
      public static Music SetMusic(String music_name,Integer album_id,Integer id,String music_url,Integer vip){
          Music music=new Music();
          music.setMusic_name(music_name);
          music.setAlbum_id(album_id);
          music.setUser_id(id);
          music.setMusic_url(music_url);
          music.setIs_vip(vip);
          music.setCreate_time(LocalDateTime.now());
          music.setUpdate_time(LocalDateTime.now());
          return music;
      }
      public static Album SetAlbum(String album_name,Integer id){
          Album album=new Album();
          album.setAlbum_name(album_name);
          album.setUser_id(id);
          album.setCreate_time(LocalDateTime.now());
          album.setUpdate_time(LocalDateTime.now());
          return album;
      }

}
