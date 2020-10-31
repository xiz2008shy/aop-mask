package com.tomqi.aop_mask.log.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author TOMQI
 * @Title: AbstractLogExecutor
 * @ProjectName: aop-mask
 * @Description : LogExecutor的中间层，主要提供了一些便利的方法和Jackson序列化对象
 * @data 2020/11/14:21
 **/
public abstract class AbstractLogExecutor implements LogExecutor{

    @Autowired
    private ObjectMapper objectMapper;

    public void printLog(Logger log, String methodName, long time, Object in, Object out) {
        String receive = null;
        String output = null;
        try {
            receive = objectMapper.writeValueAsString(in);
            output = objectMapper.writeValueAsString(out);
        } catch (JsonProcessingException e) {
            log.info("[{}]方法耗时:[{}]ms,入参={},出参={}", methodName, time, in, out);
        }
        log.info("[{}]方法耗时:[{}]ms,入参={},出参={}", methodName, time, receive, output);
    }
}
