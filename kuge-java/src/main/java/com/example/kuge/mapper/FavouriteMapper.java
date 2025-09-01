package com.example.kuge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.kuge.pojo.Favourite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavouriteMapper extends BaseMapper<Favourite> {
}
