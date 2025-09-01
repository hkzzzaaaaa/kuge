package com.example.kuge.mapper;

import com.example.kuge.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDateTime;

@Mapper
public interface LoginMapper {
    @Select("select * from user where user_account=#{userAccount}")
    User findByAccount(String userAccount);
    @Select("select max(user_id) from user")
    Integer findMaxId();
    @Insert("INSERT INTO user(user_name, user_account, user_password, user_email, user_avatar, gender, " +
            "birthday, vip, province, city, signature, create_time, update_time) " +
            "VALUES (#{user_name}, #{user_account}, #{user_password}, #{user_email}, " +
            "#{user_avatar}, #{gender}, #{birthday}, #{vip}, " +
            "#{province}, #{city}, #{signature}, #{create_time}, #{update_time})")
    void insertByaccount(User user);
}
