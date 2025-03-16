package com.example.concurrent.concurrency;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class StudentGame3CountDownLatchFunny {
    private static final int TOTAL_STUDENTS = 20;
    private static final int COMPUTERS = 3;
    
    // 定义心情等级
    private static final String[] MOOD_LEVELS = {
        "超级开心 😊 - 'wow！一来就能玩，太棒了！'",
        "很开心 😃 - '运气不错，等的人不多呢～'",
        "还算开心 🙂 - '嗯，等的时间还可以接受~'",
        "一般般 😐 - '还行吧，至少能玩到了'",
        "有点不耐烦 😕 - '怎么要等这么久啊...'",
        "开始着急 😟 - '我的天，什么时候才能轮到我'",
        "很烦躁 😤 - '等的我都快疯了！'",
        "非常生气 😠 - '这也太夸张了吧！'",
        "极度愤怒 😡 - '我等了这么久，简直离谱！'",
        "完全崩溃 🤬 - '垃圾网吧！以后再也不来了！'"
    };

    public static void main(String[] args) throws InterruptedException {
        final Semaphore backendServer = new Semaphore(COMPUTERS);
        CountDownLatch downLatch = new CountDownLatch(TOTAL_STUDENTS);
        AtomicInteger studentCount = new AtomicInteger(TOTAL_STUDENTS);
        AtomicInteger currentOrder = new AtomicInteger(0);

        ExecutorService executorService = Executors.newCachedThreadPool();
        while (true) {
            int i = studentCount.get();
            if (i <= 0) {
                break;
            }
            if (studentCount.compareAndSet(i, i - 1)) {
                executorService.execute(() -> {
                    try {
                        int myOrder = currentOrder.incrementAndGet();
                        backendServer.acquire();
                        
                        // 计算等待百分比并确定心情等级
                        double waitingPercentage = (double) myOrder / TOTAL_STUDENTS;
                        int moodIndex = (int) (waitingPercentage * MOOD_LEVELS.length);
                        moodIndex = Math.min(moodIndex, MOOD_LEVELS.length - 1);
                        
                        // 输出带有序号和心情的日志
                        log.info("我是第{}号玩家，终于轮到我上机了！还有{}个伙伴在等待。{}", 
                            myOrder, 
                            i-1,
                            MOOD_LEVELS[moodIndex]);
                        
                        play(waitingPercentage);
                        backendServer.release();
                        downLatch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        
        // 等待所有玩家完成
        downLatch.await(1, TimeUnit.MINUTES);
        executorService.shutdown();
        log.info("网吧打烊了！今天服务了{}位玩家", TOTAL_STUDENTS);
    }

    @SneakyThrows
    public static void play(double waitingPercentage) {
        Thread.sleep(100);
        // 根据等待时间调整离开时的心情
        if (waitingPercentage <= 0.3) {
            log.warn("玩得很尽兴，下次还来！😊");
        } else if (waitingPercentage <= 0.6) {
            log.warn("还行吧，至少玩到了，回家了～😐");
        } else {
            log.warn("等了这么久才玩到，真是服了！气死我了！🤬");
        }
        Thread.sleep(80);
    }
}
