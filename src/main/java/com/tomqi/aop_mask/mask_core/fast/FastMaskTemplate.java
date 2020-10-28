package com.tomqi.aop_mask.mask_core.fast;

import com.tomqi.aop_mask.annotation.MTiming;
import com.tomqi.aop_mask.annotation.TimeNode;
import com.tomqi.aop_mask.mask_core.DataMask;
import com.tomqi.aop_mask.pojo.MaskMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TOMQI
 * @Title: DataMaskTemplate
 * @ProjectName: aop_mask
 * @Description :适用于更高性能的Mask应用方式
 * @data 2020/10/1822:42
 **/
public class FastMaskTemplate implements DataMask {

    private static final Logger log = LoggerFactory.getLogger(FastMaskTemplate.class);

    @MTiming(TimeNode.BEFORE_PRE_HANDLE )
    public void beforePreHandle(MaskMessage message) {
    }

    @MTiming(TimeNode.PRE_HANDLE )
    public void preHandle(MaskMessage message) {
    }

    @MTiming(TimeNode.HANDLE )
    public void handle(MaskMessage message) throws Throwable {
        Object proceed = null;
        proceed = message.proceed();
        message.setResult(proceed);
    }

    @MTiming(TimeNode.POST_HANDLE )
    public void postHandle(MaskMessage message) {
    }

    @MTiming(TimeNode.AFTER_POST_HANDLE )
    public void afterPostHandle(MaskMessage message) {
    }

    @Override
    public Object maskData(MaskMessage message) throws Throwable {
        return null;
    }

}
