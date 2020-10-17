package com.tomqi.aop_mask.utils;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author TOMQI
 * @Title: JoinPointUtils
 * @ProjectName: aop_mask
 * @Description :用于maskAop中传递封装数据
 * @data 2020/10/34:45
 **/
public class MaskContext {

    private MaskContext(){}

    private static final ThreadLocal<ProceedingJoinPoint> pointDeliver = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> argsFlagDeliver = new ThreadLocal<>();

    public static void setPoint (ProceedingJoinPoint point) {
        pointDeliver.set(point);
    }

    public static ProceedingJoinPoint getPoint() {
        return pointDeliver.get();
    }

    public static void setArgsFlagDeliver(Boolean flag) {
        argsFlagDeliver.set(flag);
    }

    public static Boolean getArgsFlagDeliver() {
        return argsFlagDeliver.get();
    }

    public static void remover(){
        pointDeliver.remove();
        argsFlagDeliver.remove();
    }
}
