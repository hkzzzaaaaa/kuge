package com.example.kuge.service;

import com.example.kuge.pojo.User;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface InformationService {
    User getUser(String token) throws JsonProcessingException;

    void updateinform(String token, String userName, Integer gender, String province, String city) throws JsonProcessingException;

    void updatesign(String token, String signature) throws JsonProcessingException;

    void updatepassword(String token, String password,String password1) throws JsonProcessingException;

    void updateimage(String token, String imageUrl) throws JsonProcessingException;
}
