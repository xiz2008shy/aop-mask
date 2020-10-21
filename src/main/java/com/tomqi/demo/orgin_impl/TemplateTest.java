package com.tomqi.demo.orgin_impl;

import com.tomqi.aop_mask.annotation.MaskMethod;
import com.tomqi.aop_mask.annotation.MaskOn;
import com.tomqi.aop_mask.mask_core.FastDataMaskTemplate;
import com.tomqi.aop_mask.pojo.MaskMessage;


/**
 * @author TOMQI
 * @Title: TemplateTest
 * @ProjectName: aop_mask
 * @Description :TODO
 * @data 2020/10/190:31
 **/
@MaskOn("DemoController")
public class TemplateTest extends FastDataMaskTemplate {

    @MaskMethod("hello")
    @Override
    public void postHandle(MaskMessage message) {
        super.postHandle(message);
    }
}