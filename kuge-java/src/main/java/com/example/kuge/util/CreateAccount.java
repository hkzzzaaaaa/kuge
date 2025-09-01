package com.example.kuge.util;

public class CreateAccount {
    private static final Integer account=1000000000;
    public static String Create(Integer id){
        String user_account=Integer.toString(id+account);
        return user_account;
    }
}
