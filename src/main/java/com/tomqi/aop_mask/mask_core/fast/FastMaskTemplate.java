package com.tomqi.aop_mask.mask_core.fast;

import com.tomqi.aop_mask.annotation.MTiming;
import com.tomqi.aop_mask.annotation.TimeNode;
import com.tomqi.aop_mask.mask_core.DataMask;
import com.tomqi.aop_mask.pojo.MaskMessage;


/**
 * @author TOMQI
 * @Title: DataMaskTemplate
 * @ProjectName: aop_mask
 * @Description :适用于更高性能的Mask应用方式
 * @data 2020/10/1822:42
 **/
public class FastMaskTemplate implements DataMask {

    public static final FastMaskTemplate instance = new FastMaskTemplate();

    @MTiming(TimeNode.BEFORE_PRE_HANDLE )
    public void beforePreHandle(MaskMessage message) {
        // 使用时重写
    }

    @MTiming(TimeNode.PRE_HANDLE )
    public void preHandle(MaskMessage message) {
        // 使用时重写
    }

    @MTiming(TimeNode.HANDLE )
    public void handle(MaskMessage message) throws Throwable {
        Object proceed = null;
        proceed = message.proceed();
        message.setResult(proceed);
    }

    @MTiming(TimeNode.POST_HANDLE )
    public void postHandle(MaskMessage message) {
        // 使用时重写
    }

    @MTiming(TimeNode.AFTER_POST_HANDLE )
    public void afterPostHandle(MaskMessage message) {
        // 使用时重写
    }

    @Override
    public Object maskData(MaskMessage message) throws Throwable {
        return null;
    }

}
