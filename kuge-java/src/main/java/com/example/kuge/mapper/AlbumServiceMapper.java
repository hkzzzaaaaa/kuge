package com.example.kuge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.kuge.pojo.Album;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlbumServiceMapper extends BaseMapper<Album> {
    Album selectAlbum(String album_name, Integer id);
}
