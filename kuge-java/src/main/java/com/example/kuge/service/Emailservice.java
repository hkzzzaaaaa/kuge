package com.example.kuge.service;

public interface Emailservice {
    void sandemail(String email,String captcha);
    String getcaptcah(String email);
}
