// package com.example.customizeauthz.service;
//
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.retry.annotation.Backoff;
// import org.springframework.retry.annotation.Retryable;
// import org.springframework.stereotype.Service;
//
// import javax.annotation.Resource;
// import java.util.concurrent.TimeUnit;
//
// // @Service
// // @Slf4j
// // public class RedisOptimisticLockService {
// //
// //     @Resource
// //     private RedisTemplate<String, String> redisTemplate;
// //
// //     // 调整 Backoff 延迟为 50ms，最大重试 3 次，If positive, then used as a multiplier for generating the next delay for backoff.（如 50ms, 100ms, 200ms
// //     @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 50, multiplier = 2))
// //     public boolean decreaseStock(String key, int amount) {
// //         // 1. WATCH 监控键
// //         redisTemplate.watch(key);
// //
// //         try {
// //             // 2. 获取当前库存（操作窗口开始）
// //             String currentStockStr = redisTemplate.opsForValue().get(key);
// //             if (currentStockStr==null) {
// //                 throw new RuntimeException("库存不存在");
// //             }
// //
// //             int currentStock = Integer.parseInt(currentStockStr);
// //             if (currentStock < amount) {
// //                 throw new RuntimeException("库存不足");
// //             }
// //
// //             // 3. 开始事务
// //             redisTemplate.multi();
// //
// //             // 4. 执行减库存操作
// //             int newStock = currentStock - amount;
// //             redisTemplate.opsForValue().set(key, String.valueOf(newStock));
// //
// //             // 5. 提交事务（操作窗口结束，假设总耗时 2ms）
// //             if (redisTemplate.exec()==null) {
// //                 throw new RuntimeException("并发修改冲突，重试");
// //             }
// //
// //             log.info("库存减少成功，剩余: {}", newStock);
// //             return true;
// //         } catch (Exception e) {
// //             redisTemplate.discard();
// //             throw e;
// //         } finally {
// //             redisTemplate.unwatch(); // 释放监控
// //         }
// //     }
// //
// //     public void initStock(String key, int stock) {
// //         redisTemplate.opsForValue().set(key, String.valueOf(stock), 1, TimeUnit.HOURS);
// //     }
// // }