package com.example.customizeauthz.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.customizeauthz.entity.User;
import com.example.customizeauthz.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    @Resource
    UserMapper userMapper;

    /**
     * 【1】授权的时候  重写 load user 方法  是为了去数据库找到对应的用户信息
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getUsername, username));
        if (user==null) {
            log.error("Access denied: Username: " + username + " not found");
            throw new UsernameNotFoundException("Username:" + username + "not found");
        }
        return user;
    }
}
