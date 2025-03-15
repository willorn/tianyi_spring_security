package com.example.customizeauthz.service;

import lombok.Data;

import java.util.Date;

@Data
public class Member {
    private Integer age;
    private String phone;
    private Date createTime;
    private String pwd;
}