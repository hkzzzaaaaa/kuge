package com.example.kuge.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Userdetail implements UserDetails {
    private Integer id;
    private String user_account;
    private String user_password;
    // 其他字段...

    // 实现UserDetails的抽象方法（权限、账号状态等）
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return user_account; // 对应登录的用户名（user_account）
    }

    @Override
    public String getPassword() {
        return user_password; // 密码
    }

    // 其他方法（账号是否过期、锁定等，根据实际业务实现）
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
