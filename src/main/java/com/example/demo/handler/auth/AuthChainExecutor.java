package com.example.demo.handler.auth;

import com.example.demo.handler.auth.context.BaseContext;
import com.example.demo.lock.DistributedLockTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 责任链通用执行引擎
 */
@Component
public class AuthChainExecutor {

    private final DistributedLockTemplate distributedLockTemplate;

    public AuthChainExecutor(@Autowired(required = false) @Nullable DistributedLockTemplate distributedLockTemplate) {
        this.distributedLockTemplate = distributedLockTemplate;
    }

    /**
     * 普通执行责任链（无锁保护）
     * 适用于不需要并发控制的场景
     */
    public <T extends BaseContext> void execute(T context, List<Consumer<? super T>> handlers) {
        for (Consumer<? super T> handler : handlers) {
            handler.accept(context);
        }
    }

    /**
     * 带分布式锁执行责任链（使用默认锁参数）
     * 默认等待6秒，持有30秒
     *
     * @param context  业务上下文
     * @param lockKey  分布式锁的key
     * @param handlers 责任链处理器列表
     */
    public <T extends BaseContext> void executeWithLock(T context, String lockKey,
                                                        List<Consumer<? super T>> handlers) {
        executeWithLock(context, lockKey, 6, TimeUnit.SECONDS, 30, TimeUnit.SECONDS, handlers);
    }

    public <T extends BaseContext> void executeWithLock(T context, String lockKey,
                                                        long waitTime, TimeUnit waitUnit,
                                                        long leaseTime, TimeUnit leaseUnit,
                                                        List<Consumer<? super T>> handlers) {
        if (distributedLockTemplate == null) {
            throw new IllegalStateException("DistributedLockTemplate not configured");
        }
        distributedLockTemplate.executeWithLock(lockKey, waitTime, waitUnit, leaseTime, leaseUnit,
                () -> execute(context, handlers));
    }
}
