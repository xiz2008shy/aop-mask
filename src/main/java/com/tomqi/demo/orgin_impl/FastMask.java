package com.tomqi.demo.orgin_impl;

import com.tomqi.aop_mask.annotation.MaskMethod;
import com.tomqi.aop_mask.mask_core.FastDataMaskTemplate;
import com.tomqi.aop_mask.pojo.MaskMessage;

/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description
 * @date 2020/10/20 17:23
 */
public class FastMask extends FastDataMaskTemplate {

    @MaskMethod(methodName = "hello")
    @Override
    public void preHandle(MaskMessage message) {
        String input = message.getMethodArgByIndex(0);
        message.setMethodArgByIndex(input+"\t[beforeHandle]",0);
    }

}
