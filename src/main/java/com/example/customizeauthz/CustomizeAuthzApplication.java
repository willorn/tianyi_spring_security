package com.example.customizeauthz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan
public class CustomizeAuthzApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomizeAuthzApplication.class, args);
	}

}