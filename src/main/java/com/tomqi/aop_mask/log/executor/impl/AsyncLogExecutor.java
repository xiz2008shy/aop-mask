package com.tomqi.aop_mask.log.executor.impl;

import com.tomqi.aop_mask.log.executor.AbstractLogExecutor;
import org.slf4j.Logger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description 一个异步的日志输出器
 * @date 2020/10/28 18:08
 */

public final class AsyncLogExecutor extends AbstractLogExecutor {

    private ThreadPoolExecutor executor;

    public AsyncLogExecutor() {
    }

    public AsyncLogExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void executeLog (Logger log, String maskMethodName, long time, Object in, Object out) {
        CompletableFuture.runAsync(() -> {
            printLog(log, maskMethodName, time, in, out);
        }, executor);
    }



    @Override
    public void executeLog(Logger log, String originMethodName,String maskMethodName, long time, Object in, Object out) {
        StringBuilder sb = new StringBuilder(originMethodName)
                .append("]-[")
                .append(maskMethodName);
        executeLog(log,sb.toString(),time,in,out);
    }

}
