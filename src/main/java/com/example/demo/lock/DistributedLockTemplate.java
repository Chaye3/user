package com.example.demo.lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁模板接口
 * 支持Lambda风格的锁操作，自动处理锁的获取与释放
 *
 * <p>使用示例：
 * <pre>
 * distributedLockTemplate.executeWithLock("user:register:" + email,
 *     6, TimeUnit.SECONDS,
 *     30, TimeUnit.SECONDS,
 *     () -> {
 *         // 业务逻辑
 *     });
 * </pre>
 */
public interface DistributedLockTemplate {

    /**
     * 在分布式锁保护下执行业务逻辑
     *
     * @param lockKey   锁的key
     * @param waitTime  获取锁的最大等待时间
     * @param waitUnit  等待时间单位
     * @param leaseTime 锁的持有时间（自动释放，防止死锁）
     * @param leaseUnit 持有时间单位
     * @param runnable  需要在锁保护下执行的业务逻辑
     * @throws IllegalStateException 获取锁失败时抛出
     */
    void executeWithLock(String lockKey, long waitTime, TimeUnit waitUnit,
                         long leaseTime, TimeUnit leaseUnit, Runnable runnable);

    /**
     * 在分布式锁保护下执行业务逻辑并返回结果
     *
     * @param lockKey   锁的key
     * @param waitTime  获取锁的最大等待时间
     * @param waitUnit  等待时间单位
     * @param leaseTime 锁的持有时间（自动释放，防止死锁）
     * @param leaseUnit 持有时间单位
     * @param supplier  需要在锁保护下执行的业务逻辑，返回结果
     * @param <T>       返回值类型
     * @return 业务逻辑执行结果
     * @throws IllegalStateException 获取锁失败时抛出
     */
    <T> T executeWithLock(String lockKey, long waitTime, TimeUnit waitUnit,
                          long leaseTime, TimeUnit leaseUnit, java.util.function.Supplier<T> supplier);

    /**
     * 尝试获取锁（不阻塞）
     *
     * @param lockKey   锁的key
     * @param leaseTime 锁的持有时间
     * @param leaseUnit 持有时间单位
     * @param runnable  需要在锁保护下执行的业务逻辑
     * @return true-获取锁成功并执行了业务逻辑；false-获取锁失败
     */
    boolean tryExecuteWithLock(String lockKey, long leaseTime, TimeUnit leaseUnit, Runnable runnable);
}
