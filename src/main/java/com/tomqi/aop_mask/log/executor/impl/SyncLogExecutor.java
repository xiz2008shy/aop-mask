package com.tomqi.aop_mask.log.executor.impl;

import com.tomqi.aop_mask.log.executor.AbstractLogExecutor;
import org.slf4j.Logger;

/**
 * @author TOMQI
 * @Title: SyncLogExecutor
 * @ProjectName: aop-mask
 * @Description : 一个同步的日志输出器
 * @data 2020/11/14:15
 **/
public final class SyncLogExecutor  extends AbstractLogExecutor {
    @Override
    public void executeLog(Logger log, String maskMethodName, long time, Object in, Object out) {
        printLog(log, maskMethodName, time, in, out);
    }

    @Override
    public void executeLog(Logger log, String originMethodName, String maskMethodName, long time, Object in, Object out) {
        StringBuilder sb = new StringBuilder(originMethodName)
                .append("]-[")
                .append(maskMethodName);
        executeLog(log,sb.toString(),time,in,out);
    }
}
