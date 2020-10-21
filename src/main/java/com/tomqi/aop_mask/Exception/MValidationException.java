package com.tomqi.aop_mask.Exception;

/**
 * @author TOMQI
 * @Title: NonMaskException
 * @ProjectName: aop_mask
 * @Description :效验异常
 * @data 2020/10/36:09
 **/
public class MValidationException extends RuntimeException {

    private final String clazzName;

    private final String methodName;

    public MValidationException(String message,String clazzName,String methodName) {
        super(message);
        this.clazzName = clazzName;
        this.methodName = methodName;
    }

    public String getClazzName() {
        return clazzName;
    }

    public String getMethodName() {
        return methodName;
    }

}
