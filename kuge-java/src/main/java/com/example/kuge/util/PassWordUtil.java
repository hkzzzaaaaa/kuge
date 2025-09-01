package com.example.kuge.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PassWordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);//BCrypt加密
    }
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);//验证密码
    }
}
