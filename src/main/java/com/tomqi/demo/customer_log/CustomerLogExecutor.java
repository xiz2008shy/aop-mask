package com.tomqi.demo.customer_log;

import com.tomqi.aop_mask.log.executor.AbstractLogExecutor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author TOMQI
 * @Title: CustomerLogExecutor
 * @ProjectName: aop-mask
 * @Description : 一个自定义日志处理器的测试用例
 * @data 2020/11/114:00
 **/

//@Component
public class CustomerLogExecutor extends AbstractLogExecutor {

    @Override
    public void executeLog(Logger log, String maskMethodName, long time, Object in, Object out) {
        log.info("我是自定义的日志处理器。[{}]---->[{}]ms--->in[{}]--->out[{}]",maskMethodName,time,in,out);
    }

    @Override
    public void executeLog(Logger log, String originMethodName, String maskMethodName, long time, Object in, Object out) {
        log.info("我是自定义的日志处理器。[{}-{}]---->[{}]ms--->in[{}]--->out[{}]",originMethodName,maskMethodName,time,in,out);
    }
}
