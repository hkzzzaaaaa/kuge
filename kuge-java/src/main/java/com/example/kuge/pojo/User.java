package com.example.kuge.pojo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "user")
public class User {
   @TableId(value = "user_id")
   private Integer user_id;
   @Pattern(regexp = "^\\S{1,20}$",groups = registry.class,message = "用户名长度限制1-20位")
   @TableField(value = "user_name")
   private String user_name;
   @TableField(value = "user_account")
   private String user_account;
   @Pattern(regexp = "^\\S{5,20}$",groups = registry.class,message = "密码长度限制5-20位")
   @TableField(value = "user_password")
   @JsonIgnore
   private String user_password;
   @Email(groups = registry.class,message = "请输入正确的邮箱地址")
   @TableField(value = "user_email")
   private String user_email;
   @TableField(value = "user_avatar")
   private String user_avatar;
   @TableField(value = "gender")
   private Integer gender;
   @TableField(value = "birthday")
   private LocalDate birthday;
   @TableField(value = "vip")
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
   private LocalDateTime vip;
   @TableField(value = "province")
   private String province;
   @TableField(value = "city")
   private String city;
   @TableField(value = "signature")
   private String signature;
   @TableField(value = "create_time")
   private LocalDateTime create_time;
   @TableField(value = "update_time")
   private LocalDateTime update_time;

   public interface find{

   }
   public interface registry{

   }
}
