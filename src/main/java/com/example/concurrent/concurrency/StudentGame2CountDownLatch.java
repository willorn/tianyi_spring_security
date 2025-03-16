package com.example.concurrent.concurrency;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class StudentGame2CountDownLatch {

    /**
     * 这个例子是一个更符合实际的例子，我们用 count lunch 来实现
     *
     * @param args
     */
    public static void main(String[] args) {
        final Semaphore backendServer = new Semaphore(300); // 黑网吧只能提供300台电脑给小学生玩
        CountDownLatch downLatch = new CountDownLatch(1000);
        AtomicInteger studentCount = new AtomicInteger(1000);

        ExecutorService executorService = Executors.newCachedThreadPool();
        while (true) {
            int i = studentCount.get();
            //  case 1 don't need to decrease the student
            if (i <= 0) {
                break;
            }
            // case 2 many thread will come here and possess the cas（compare and swap）
            if (studentCount.compareAndSet(i, i - 1)) {
                executorService.execute(() -> {
                    try {
                        backendServer.acquire();
                        log.info("我上机了，看看现在还有{}个伙伴还在外边等", i-1);
                        play();
                        backendServer.release();
                        downLatch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
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
}
