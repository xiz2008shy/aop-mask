package com.tomqi.aop_mask.pojo;

import com.tomqi.aop_mask.annotation.TimeNode;
import java.lang.reflect.Method;


/**
 * @author TOMQI
 * @description AbstractDefaultDataMask类初始化时的方法封装对象
 * @date 2020/9/23 11:05
 */
public class MaskMethodInfo {

    private Method processMethod;

    private Method beforePreMethod;

    private Method preMethod;

    private Method postMethod;

    private Method afterPostMethod;

    public Method getProcessMethod() {
        return processMethod;
    }

    public void setProcessMethod(Method processMethod) {
        this.processMethod = processMethod;
    }

    public Method getBeforePreMethod() {
        return beforePreMethod;
    }

    public void setBeforePreMethod(Method beforePreMethod) {
        this.beforePreMethod = beforePreMethod;
    }

    public Method getPreMethod() {
        return preMethod;
    }

    public void setPreMethod(Method preMethod) {
        this.preMethod = preMethod;
    }

    public Method getPostMethod() {
        return postMethod;
    }

    public void setPostMethod(Method postMethod) {
        this.postMethod = postMethod;
    }

    public Method getAfterPostMethod() {
        return afterPostMethod;
    }

    public void setAfterPostMethod(Method afterPostMethod) {
        this.afterPostMethod = afterPostMethod;
    }

    public void setMethod(Method method, TimeNode type) {
        switch (type) {
            case HANDLE:
                this.processMethod = method;
                break;
            case BEFORE_PRE_HANDLE:
                this.beforePreMethod = method;
                break;
            case PRE_HANDLE:
                this.preMethod = method;
                break;
            case POST_HANDLE:
                this.postMethod = method;
                break;
            case AFTER_POST_HANDLE:
                this.afterPostMethod = method;
                break;
            default:
                break;
        }
    }

    public static MaskMethodInfo createMethodInfo(Method method, TimeNode type) {
        MaskMethodInfo info = new MaskMethodInfo();
        info.setMethod(method,type);
        return info;
    }
}
