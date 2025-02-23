package com.example.customizeauthz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    // 张三访间 zhangsan:zhangsan => emhhbmdzYw46emhhbmdzYw4=
    // GET http://localhost:8080/hello
    // Authorization: Basic emhhbmdzYW46emhhbmdzYW4=
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}