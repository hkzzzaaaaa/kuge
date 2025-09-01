package com.example.kuge.util;

import java.util.Random;

public class CreateCaptcha {
    public static String Captcha(){
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
