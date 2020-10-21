package com.tomqi.aop_mask.annotation;

/**
 * @author TOMQI
 * @Title: TimeNode
 * @ProjectName: aop_mask
 * @Description :定义方法的执行节点，提供4种节点，顺序是BEFORE_PRE_HANDLE --> preHandle前置处理 --> postHandle后置处理 --> AFTER_POST_HANDLE
 * 如果指定HANDLE 意味着你打算自定义执行操作。他的执行时点在preHandle和postHandle之间,需要注意的是只有这个节点可以拿到joinPoint对象。
 * @data 2020/10/1822:51
 **/


public enum TimeNode {

    //前置处理前
    BEFORE_PRE_HANDLE(0),
    //前置处理
    PRE_HANDLE(1),
    //process处理
    HANDLE(2),
    //后置处理
    POST_HANDLE(3),
    //后置处理后
    AFTER_POST_HANDLE(4);

    private int value;
    TimeNode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
