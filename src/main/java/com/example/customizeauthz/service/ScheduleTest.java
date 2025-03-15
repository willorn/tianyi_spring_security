package com.example.customizeauthz.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ScheduleTest {
    @Scheduled(fixedRate = 1000)
    public void ttt() throws InterruptedException {
        Thread.sleep(3000);
        LocalDateTime now = LocalDateTime.now();
        log.info("开始啦 at {}", now);
    }
}