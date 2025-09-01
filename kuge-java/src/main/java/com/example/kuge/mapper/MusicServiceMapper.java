package com.example.kuge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.kuge.pojo.Favourite;
import com.example.kuge.pojo.Music;
import com.example.kuge.pojo.MusicBase;
import com.example.kuge.pojo.MusicPublicView;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MusicServiceMapper extends BaseMapper<Music> {
    List<MusicPublicView> findmusicpublic(Integer id);

    List<MusicPublicView> findMusicByName(Integer id, String musicName);

    List<MusicBase> findMusicBase();

    List<MusicBase> findMusic(String musicName);

    Music findMusicByMusicName(String musicName, String userName);

    void insertfavourite(Favourite favourite);
    @Select("select * from favourite where user_id=#{userId} and music_id=#{musicId}")
    Favourite selectFavorite(Integer userId,Integer musicId);

    List<MusicBase> getFavourite(Integer id);
    @Delete("delete from favourite where music_id=#{musicId} and user_id=#{id}")
    void deleteFavourite(Integer musicId, Integer id);

    List<MusicBase> selectMusicBase();
    MusicBase selectMusicBaseById(Integer music_id);
}
