package com.tomqi.aop_mask.log.executor;

import org.slf4j.Logger;


/**
 * @author TOMQI
 * @Title: LogExecutor
 * @ProjectName: aop-mask
 * @Description : MLog顶级的日志接口
 * @data 2020/11/14:12
 **/
public interface LogExecutor {

    void executeLog (Logger log, String maskMethodName, long time, Object in, Object out) ;

    void executeLog(Logger log, String originMethodName,String maskMethodName, long time, Object in, Object out);

}
