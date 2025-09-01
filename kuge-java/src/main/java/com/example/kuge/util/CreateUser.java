package com.example.kuge.util;

import com.example.kuge.pojo.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreateUser {
    public static User create(User user, String account){
        user.setUser_account(account);
        user.setUser_avatar("C:\\Users\\hkzzz\\Desktop\\MusicSystemcode\\KuGe-project\\src\\assets");
        user.setGender(0);
        user.setBirthday(LocalDate.of(2000,1,1));
        user.setVip(LocalDateTime.of(2000,1,1,1,1));
        user.setProvince("无");
        user.setCity("无");
        user.setSignature("系统原装签名，给每一位小可爱");
        user.setCreate_time(LocalDateTime.now());
        user.setUpdate_time(LocalDateTime.now());
        return user;
    }
}
