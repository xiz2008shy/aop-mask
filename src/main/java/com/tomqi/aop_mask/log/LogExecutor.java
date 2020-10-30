package com.tomqi.aop_mask.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description
 * @date 2020/10/28 18:08
 */

public class LogExecutor {

    @Autowired
    private ObjectMapper objectMapper;

    private ThreadPoolExecutor executor;

    public LogExecutor() {
    }

    public LogExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }


    public void asyncLog (Logger log, String methodName, long time, Object in, Object out) {
        try {
            String receive = objectMapper.writeValueAsString(in);
            String output = objectMapper.writeValueAsString(out);
            CompletableFuture.runAsync(()-> log.info("[{}]方法耗时:[{}]毫秒,入参={},出参={}",methodName,time,receive,output) ,executor);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
