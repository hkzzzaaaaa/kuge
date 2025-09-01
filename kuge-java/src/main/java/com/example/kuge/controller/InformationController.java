package com.example.kuge.controller;

import com.example.kuge.pojo.Result;
import com.example.kuge.pojo.User;
import com.example.kuge.service.InformationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/information")
public class InformationController {
    @Autowired
    InformationService informationService;
    @GetMapping("/getinfo")
    public Result<User> getinformation(@RequestHeader(value = "Authorization") String token) throws JsonProcessingException {
           User user=informationService.getUser(token);
           return Result.success(user);
    }
    @PostMapping("/updateinform")
    public Result updateinform(@RequestHeader(value = "Authorization") String token, @Pattern(regexp = "^\\S{1,20}$",message = "姓名长度限制为1-20之间") String user_name,
                               Integer gender, @Size(max = 5,min = 0) String province,@Size(max = 5,min = 0) String city) throws JsonProcessingException {
        if (province==null){
            province="无";
        }
        if(city==null){
            city="无";
        }
        informationService.updateinform(token,user_name,gender,province,city);
        return Result.success();
    }
    @PostMapping("/updatesign")
    public Result updatesign(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request
    ) throws JsonProcessingException {
        String signature = (String) request.get("signature");
        informationService.updatesign(token,signature);
        return Result.success();
    }
    @PostMapping("/updatepassword")
    public  Result updatepassword(@RequestHeader("Authorization") String token,@Pattern(regexp = "^\\S{5,20}$",message = "密码长度限制为5-20之间") String password
            ,@Pattern(regexp = "^\\S{5,20}$",message = "密码长度限制为5-20之间") String newpassword1
    ) throws JsonProcessingException {
        informationService.updatepassword(token,password,newpassword1);
        return Result.success();
    }
    @PostMapping("/updateimage")
    public  Result updateimage(@RequestHeader("Authorization") String token, @URL String imageUrl) throws JsonProcessingException {
        informationService.updateimage(token,imageUrl);
        return Result.success();
    }
}
