package com.example.demo.handler.auth;

import com.example.demo.handler.auth.context.BaseContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 责任链通用执行引擎
 */
@Component
public class AuthChainExecutor {
    /**
     * 顺序执行责任链，并在 finally 中确保释放锁
     */
    public <T extends BaseContext> void execute(T context, List<AuthHandler<? super T>> handlers) {
        try {
            for (AuthHandler<? super T> handler : handlers) {
                handler.handle(context);
            }
        } finally {
            if (context.getLock() != null && context.getLock().isHeldByCurrentThread()) {
                context.getLock().unlock();
            }
        }
    }
}
