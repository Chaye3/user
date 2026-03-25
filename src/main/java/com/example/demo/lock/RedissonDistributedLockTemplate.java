package com.example.demo.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redisson的分布式锁模板实现
 *
 * <p>依赖配置（pom.xml）：
 * <pre>
 * &lt;dependency&gt;
 *     &lt;groupId&gt;org.redisson&lt;/groupId&gt;
 *     &lt;artifactId&gt;redisson-spring-boot-starter&lt;/artifactId&gt;
 *     &lt;version&gt;3.25.0&lt;/version&gt;
 * &lt;/dependency&gt;
 * </pre>
 */
@Component
public class RedissonDistributedLockTemplate implements DistributedLockTemplate {

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void executeWithLock(String lockKey, long waitTime, TimeUnit waitUnit,
                                long leaseTime, TimeUnit leaseUnit, Runnable runnable) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // Redisson tryLock 使用统一的 TimeUnit，将 leaseTime 转换为与 waitTime 相同单位
            long leaseTimeInWaitUnit = leaseUnit.convert(leaseTime, waitUnit);
            boolean acquired = lock.tryLock(waitTime, leaseTimeInWaitUnit, waitUnit);
            if (!acquired) {
                throw new IllegalStateException("System busy, please try again later");
            }
            runnable.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Lock acquisition interrupted", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public <T> T executeWithLock(String lockKey, long waitTime, TimeUnit waitUnit,
                                 long leaseTime, TimeUnit leaseUnit, java.util.function.Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // Redisson tryLock 使用统一的 TimeUnit，将 leaseTime 转换为与 waitTime 相同单位
            long leaseTimeInWaitUnit = leaseUnit.convert(leaseTime, waitUnit);
            boolean acquired = lock.tryLock(waitTime, leaseTimeInWaitUnit, waitUnit);
            if (!acquired) {
                throw new IllegalStateException("System busy, please try again later");
            }
            return supplier.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Lock acquisition interrupted", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public boolean tryExecuteWithLock(String lockKey, long leaseTime, TimeUnit leaseUnit, Runnable runnable) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean acquired = lock.tryLock(0, leaseTime, leaseUnit);
            if (!acquired) {
                return false;
            }
            runnable.run();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
