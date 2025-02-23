package com.example.customizeauthz;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

@SpringBootTest
class CustomizeAuthzApplicationTests {
    @Resource
    PasswordEncoder passwordEncoder;

    @Test
    public void encoder() {
        System.out.println(passwordEncoder.encode("zhangsan"));
        System.out.println(passwordEncoder.encode("lisi"));
        System.out.println(passwordEncoder.encode("wangwu"));
    }

    @Test
    void contextLoads() {
    }

}