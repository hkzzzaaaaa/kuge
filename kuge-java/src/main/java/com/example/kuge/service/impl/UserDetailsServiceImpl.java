package com.example.kuge.service.impl;
import com.example.kuge.mapper.LoginMapper;
import com.example.kuge.pojo.User;
import com.example.kuge.pojo.Userdetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    LoginMapper loginMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String user_account=username;
        User user=loginMapper.findByAccount(user_account);
        if(user==null){
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        Userdetail userdetail=new Userdetail();
        userdetail.setId(user.getUser_id());
        userdetail.setUser_account(user.getUser_account());
        userdetail.setUser_password(user.getUser_password());
        return userdetail;
    }
}
