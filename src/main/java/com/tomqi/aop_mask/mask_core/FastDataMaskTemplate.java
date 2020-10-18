package com.tomqi.aop_mask.mask_core;

import com.tomqi.aop_mask.annotation.MTiming;
import com.tomqi.aop_mask.annotation.TimeNode;
import com.tomqi.aop_mask.pojo.MaskMessage;

/**
 * @author TOMQI
 * @Title: DataMaskTemplate
 * @ProjectName: aop_mask
 * @Description :适用于更高性能的Mask应用方式
 * @data 2020/10/1822:42
 **/
public class FastDataMaskTemplate implements DataMask {

    @MTiming(TimeNode.BEFORE_PRE_HANDLE )
    public void beforePreHandle(MaskMessage message) {
    }

    @MTiming(TimeNode.PRE_HANDLE )
    public void preHandle(MaskMessage message) {
    }

    @MTiming(TimeNode.HANDLE )
    public void handle(MaskMessage message) {
    }

    @MTiming(TimeNode.POST_HANDLE )
    public void postHandle(MaskMessage message) {
    }

    @MTiming(TimeNode.AFTER_POST_HANDLE )
    public void afterPostHandle(MaskMessage message) {
    }

    @Override
    public Object maskData(MaskMessage message) {
        return null;
    }
}
