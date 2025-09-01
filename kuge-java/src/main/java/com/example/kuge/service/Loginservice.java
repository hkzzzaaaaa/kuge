package com.example.kuge.service;


import com.example.kuge.pojo.Result;
import com.example.kuge.pojo.User;

public interface Loginservice {
    public String login(String user_account,String user_password);
    User findByAccount(String userAccount);

    String createNewAccount(User user);

    void Logout(String token);
}
