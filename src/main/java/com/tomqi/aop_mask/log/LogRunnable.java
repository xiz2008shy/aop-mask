package com.tomqi.aop_mask.log;

import org.slf4j.Logger;

/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description
 * @date 2020/10/28 18:55
 */
public class LogRunnable implements Runnable {

    private Logger log;

    private String msg;

    private Object[] params;

    public LogRunnable(Logger log,String msg, Object... params) {
        this.log = log;
        this.msg = msg;
        Object[] arrs = new Object[params.length];
        int index = 0;
        for (Object param : params) {
            arrs[index ++] = param;
        }
    }

    @Override
    public void run() {
        log.info(msg,params);
    }
}
