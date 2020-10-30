package com.tomqi.aop_mask.utils;

import java.util.concurrent.TimeUnit;

/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description
 * @date 2020/10/30 15:56
 */
public class TimeUnitUtils {

    private TimeUnitUtils() {
    }

    public static TimeUnit getUnit(String str) {
        switch(str){
            case "TimeUnit.MINUTES" :
                return TimeUnit.MINUTES;
            case "TimeUnit.MILLISECONDS" :
                return TimeUnit.MILLISECONDS;
            case "TimeUnit.HOURS" :
                return TimeUnit.HOURS;
            case "TimeUnit.SECONDS" :
            default:
                return TimeUnit.SECONDS;
        }
    }
}
