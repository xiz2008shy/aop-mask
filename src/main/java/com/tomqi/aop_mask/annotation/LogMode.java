package com.tomqi.aop_mask.annotation;

/**
 * @author TOMQI
 * @Title: LogMode
 * @ProjectName: aop_mask
 * @Description : 描述MLog的输出模式
 * @data 2020/10/2823:43
 **/
public enum LogMode {

    OFF(0,"不开启AUTO日志输出。"),
    ALLIN(1,"输出Masking中所有节点的日志。"),
    GENERIC(2,"只输出整体执行的一条日志");

    int value;
    String desc;

    LogMode(int value,String desc) {
        this.value = value;
        this.desc = desc;
    }
}
