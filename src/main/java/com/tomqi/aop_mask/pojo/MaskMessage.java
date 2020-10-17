package com.tomqi.aop_mask.pojo;

import com.tomqi.aop_mask.annotation.Masking;
import com.tomqi.aop_mask.utils.MaskContext;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import java.lang.reflect.Method;
import java.util.Objects;


/**
 * @author TOMQI
 * @description 作为所有被@MaskMethod方法修饰的内置对象，用于形参传递，结果传递等。
 * @date 2020/9/23 10:53
 */
public class MaskMessage {

    /**
     * joinPoint只有当MethodType为HANDLE时可被获取，在AbstractDefaultDataMask中 通常不用关注原方法如何执行。
     */
    private ProceedingJoinPoint joinPoint;

    /**
     * 被Masking注解方法所在类的类名
     */
    private String simpleClassName;

    /**
     * 方法上的@Masking注解对象
     */
    private Masking masking;

    /**
     * 原方法的入参，加了一层封装，原Object数组不再对外暴露
     */
    private MethodArgs methodArgs;

    /**
     * 原执行方法的相关信息
     */
    private MethodSignature signature;

    /**
     * 该字段用于变量传递
     */
    private Object attribute;

    /**
     * 该字段用于结果传递，可被下个执行时点获取。
     */
    private Object result;


    public MaskMessage setJoinPoint(ProceedingJoinPoint joinPoint) {
        this.joinPoint = joinPoint;
        return this;
    }

    public MethodArgs getMethodArgs() {
        return methodArgs;
    }

    /**
     * 该方法可用于获取对应位置的形参入参
     * @param index 形参的索引，从左到右按顺序从0开始
     * @param <T> 返回值无需强转，直接用正确的类型接收即可
     * @return
     */
    public <T>T getMethodArgByIndex(int index) {
        if (Objects.isNull(methodArgs) || methodArgs.size() < index ) {
            return null;
        }
        try {
            return (T)methodArgs.get(index);
        } catch (ClassCastException e){
            return null;
        }
    }

    /**
     * 该方法为类中内部方法，仅在createMessage方法中被调用（初次创建Message对象时）
     * @param methodArgs
     */
    private void setMethodArgs(Object[] methodArgs) {
        this.methodArgs = new MethodArgs(methodArgs);
    }

    /**
     * 对指定索引的形参进行设置，该方法内部将触发argsFlag变为true
     * @param arg 新的值
     * @param index 形参索引
     */
    public void setMethodArgByIndex(Object arg ,int index) {
        if (Objects.nonNull(methodArgs) && methodArgs.size() > index ) {
            methodArgs.set(arg,index);
        }
    }

    public Object getAttribute() {
        return attribute;
    }

    public void setAttribute(Object attribute) {
        this.attribute = attribute;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public Masking getMasking() {
        return masking;
    }

    public void setMasking(Masking masking) {
        this.masking = masking;
    }

    /**
     * 获取result
     * @param <T> 结果无需强转，直接用正确的类型接收即可
     * @return
     */
    public <T>T getResult() {
        try {
            return (T)result;
        } catch (ClassCastException e){
            return null;
        }
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public MethodSignature getSignature() {
        return signature;
    }

    public void setSignature(MethodSignature signature) {
        this.signature = signature;
    }

    public static MaskMessage creatMessage(ProceedingJoinPoint joinPoint){
        MaskMessage message = new MaskMessage();
        message.setMethodArgs(joinPoint.getArgs());
        message.setSignature((MethodSignature)joinPoint.getSignature());
        message.setSimpleClassName(joinPoint.getSignature().getDeclaringType().getSimpleName());
        message.setMasking(message.signature.getMethod().getAnnotation(Masking.class));
        return message;
    }

    /**
     * 获取aop原本执行的方法名
     * @param
     * @return
     */
    public String getMethodName() {
        Masking annotation = this.signature.getMethod().getAnnotation(Masking.class);
        if (StringUtils.isNotBlank(annotation.alias())) {
            return annotation.alias();
        }else {
            return signature.getMethod().getName();
        }
    }

    public static Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature sign = (MethodSignature)joinPoint.getSignature();
        return sign.getMethod();
    }

    /**
     * 操作Masking的原方法执行,屏蔽JoinPoint底层的执行细节，由argsFlag控制流程。
     * @return
     * @throws Throwable
     */
    public Object proceed() throws Throwable {
        Boolean argsFlag = MaskContext.getArgsFlagDeliver();
        if (argsFlag == null || !argsFlag ){
            return joinPoint.proceed();
        }else {
            return joinPoint.proceed(this.methodArgs.constructArgs());
        }
    }
}
