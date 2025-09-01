package com.example.kuge.controller;

import com.example.kuge.pojo.Result;
import com.example.kuge.pojo.User;
import com.example.kuge.service.Emailservice;
import com.example.kuge.service.Loginservice;
import com.example.kuge.service.impl.Loginserviceimpl;
import com.example.kuge.util.CreateCaptcha;
import com.example.kuge.util.PassWordUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    Loginservice loginservice;
    @Autowired
    Emailservice emailservice;
    @PostMapping("/getlogin")
    public Result<String> Login(@Pattern(regexp = "^\\S{5,20}$") String account,@Pattern(regexp = "^\\S{5,20}$") String password){
         String token=loginservice.login(account,password);
         return Result.success(token);
    }
    @PostMapping("/registry")
    public Result<String> Registry(@Pattern(regexp = "^\\S{1,20}$") String username
    , @Pattern(regexp = "^\\S{5,20}$") String password1, @Email String email,@Pattern(regexp = "^\\S{4,4}$") String captcha){
        if(captcha.equals(emailservice.getcaptcah(email))) {
            User user = new User();
            user.setUser_name(username);
            password1=PassWordUtil.encode(password1);
            user.setUser_password(password1);
            user.setUser_email(email);
            String user_account = loginservice.createNewAccount(user);
            System.out.println("数据插入成功");
            return Result.success(user_account);
        }
        else{
            return Result.error("验证码错误或者失效,注册失败");
        }
    }
    @PostMapping("/getcaptcha")
    public  Result<String> Captcha(@Email String email){
        System.out.println("接收邮箱："+email);
        String captcha= CreateCaptcha.Captcha();
        emailservice.sandemail(email,captcha);
        return Result.success(captcha);
    }
    @GetMapping("/logout")
    public Result Logout(@RequestHeader(value = "Authorization") String token){
        loginservice.Logout(token);
        return Result.success();
    }
}
