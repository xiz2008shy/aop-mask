package com.tomqi.aop_mask.log;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description MLog异步日志的线程工程
 * @date 2020/10/28 18:17
 */
public class LogThreadFactory implements ThreadFactory {


    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public LogThreadFactory() {
        group = new ThreadGroup("MLog-Group");
        this.namePrefix = "MLog-Thread";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
