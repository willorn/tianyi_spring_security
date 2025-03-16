package com.example.concurrent.concurrency;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class StudentGame1Semaphore {

    public static void main(String[] args) {
        AtomicInteger studentCount = new AtomicInteger(1000); // 这10000个小学生今天一定要玩到这个游戏，玩不到游戏就不回家了
        final Semaphore backendServer = new Semaphore(300); // 黑网吧只能提供300台电脑给小学生玩

        ExecutorService executorService = Executors.newCachedThreadPool();
        while (true) {
            int currentStudent = studentCount.get();
            if (currentStudent <= 0) {
                break;
            }

            if (studentCount.compareAndSet(currentStudent, currentStudent -1)) {
                executorService.execute(()-> {
                    try {
                        backendServer.acquire(); // 改用阻塞方式获取信号量
                        try {
                            log.info("客人刷卡上机 目前还有{}人在排队", currentStudent - 1);
                            play();
                        } finally {
                            backendServer.release();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("任务被中断", e);
                    }
                });
            }
        }
        executorService.shutdown();
        log.info("营业结束");
    }

    @SneakyThrows
    public static void play() {
        Thread.sleep(100);
        log.info("走了走了，今天玩饱了，回家吃饭去喽");
        Thread.sleep(30);
    }

    // /**
    //  * 加完这个synchronized之后，直接变成单线程。屁用没有
    //  * @param studentCount
    //  */
    // @SneakyThrows
    // public synchronized static void play(AtomicInteger studentCount) {
    //     studentCount.decrementAndGet();
    //     Thread.sleep(100);
    //     log.info("走了走了，今天玩饱了，回家吃饭去喽");
    //     Thread.sleep(30);
    // }
}
