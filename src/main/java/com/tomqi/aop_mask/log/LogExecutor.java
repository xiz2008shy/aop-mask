package com.tomqi.aop_mask.log;

import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description
 * @date 2020/10/28 18:08
 */
@Component
public class LogExecutor {

    private ThreadPoolExecutor executor;

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,1,60L, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(200),new LogThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
    }
}
