package com.tomqi.aop_mask.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
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

    @Autowired
    private ObjectMapper objectMapper;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(1,1,60L, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(200),new LogThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

    public void asyncLog (Logger log,String methodName,long time,Object in,Object out) {
        try {
            String receive = objectMapper.writeValueAsString(in);
            String output = objectMapper.writeValueAsString(out);
            CompletableFuture.runAsync(()-> log.info("[{}]方法耗时:[{}]毫秒,入参=[{}],出参=[{}]",methodName,time,receive,output) ,executor);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
