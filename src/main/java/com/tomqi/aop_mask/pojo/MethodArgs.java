package com.tomqi.aop_mask.pojo;

import com.tomqi.aop_mask.utils.MaskContext;

/**
 * @author TOMQI
 * @Title: MethodArgs
 * @ProjectName: aop_mask
 * @Description :用于封装方法形参，让形参的修改处于可被查看的状态，将影响proceed方法的执行方式。
 * @data 2020/10/170:58
 **/
public class MethodArgs {
    private Object[] args;

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
     * 该方法中对ArgsFlag做修改，用于通知process方法 应该如何执行。
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

