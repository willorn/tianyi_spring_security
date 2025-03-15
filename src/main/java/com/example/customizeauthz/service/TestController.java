package com.example.customizeauthz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/aa")
public class TestController {

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Test Controller";
    }

    @GetMapping("/json")
    @ResponseBody
    public Member testJson(String phone, String pwd) {
        Member user = new Member();
        user.setPhone(phone);
        user.setPwd(pwd);
        user.setCreateTime(new Date());
        return user;
    }

    @GetMapping("/logger")
    @ResponseBody
    public String testLogger() {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.debug("测试级别日志");
        return "SUCCESS";
    }

}
