package com.example.kuge.filter;

import com.example.kuge.pojo.User;
import com.example.kuge.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;


@Component
public class JwtAuthenticationToken extends OncePerRequestFilter {
    @Autowired
    RedisTemplate<String,String> redisTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //TODO 获取token
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasLength(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        String user_id= "kuge:user:";
        try {
            Map<String, Object> map = JwtUtil.parseToken(token);  //解析token
            Integer id = (Integer) map.get("id");
            user_id = user_id+ id;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
        }
        //从redis中获取用户信息
        String json=redisTemplate.opsForValue().get(user_id);
        if(!StringUtils.hasLength(json)){
            throw new RuntimeException("用户未登录");
        }
        User user=objectMapper.readValue(json, User.class);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                new UsernamePasswordAuthenticationToken(user.getUser_account(),null,null);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request,response);
    }
}
