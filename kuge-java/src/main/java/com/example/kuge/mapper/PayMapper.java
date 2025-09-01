package com.example.kuge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.kuge.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface PayMapper extends BaseMapper<Orders> {
    @Select("select vip from user where user_id=#{userId}")
    LocalDateTime selectvip(Integer userId);
    @Update("update user set vip=#{time} where user_id=#{userId}")
    void updatevip(LocalDateTime time, Integer userId);
}
