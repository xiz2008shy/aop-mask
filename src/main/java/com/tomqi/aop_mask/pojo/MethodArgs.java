package com.tomqi.aop_mask.pojo;

import com.fasterxml.jackson.annotation.*;
import com.tomqi.aop_mask.utils.MaskContext;

/**
 * @author TOMQI
 * @Title: MethodArgs
 * @ProjectName: aop_mask
 * @Description :用于封装方法形参，让形参的修改处于可被查看的状态，将影响proceed方法的执行方式。
 * @data 2020/10/170:58
 **/
@JsonPropertyOrder({"size","args"})
@JsonIgnoreProperties({"empty","notEmpty"})
@JsonRootName("input")
public class MethodArgs {
    private Object[] args;

    @JsonGetter("size")
    public int size () {
        if (args != null){
            return args.length;
        }
        return 0;
    }

    public Object get (int index) {
        return args[index];
    }

    /**
     * 对于修改入参应使用该方法进行,不建议直接获取object[]操作,该方法中执行时会修改ArgsFlag，用于通知process方法 应该如何执行。
     * @see MaskMessage#proceed()
     * @param arg
     * @param index
     */
    public void set(Object arg ,int index){
        MaskContext.setArgsFlagDeliver(true);
        args[index] = arg;
    }


    public MethodArgs(Object[] args) {
        this.args = args;
    }

    @JsonGetter("args")
    public Object[] constructArgs(){
        return args;
    }

    public boolean isNotEmpty(){
        return args != null && args.length > 0;
    }

    public boolean isEmpty(){
        return args == null || args.length == 0;
    }
}

