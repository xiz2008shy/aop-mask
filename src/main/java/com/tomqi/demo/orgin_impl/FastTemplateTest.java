package com.tomqi.demo.orgin_impl;

import com.tomqi.aop_mask.annotation.MaskMethod;
import com.tomqi.aop_mask.annotation.MaskOn;
import com.tomqi.aop_mask.annotation.TimeNode;
import com.tomqi.aop_mask.mask_core.FastDataMaskTemplate;
import com.tomqi.aop_mask.pojo.MaskMessage;


/**
 * @author TOMQI
 * @Title: TemplateTest
 * @ProjectName: aop_mask
 * @Description : 一个测试用例
 * @data 2020/10/190:31
 **/
@MaskOn("DemoController")
public class FastTemplateTest extends FastDataMaskTemplate {

    @MaskMethod("hello")
    @Override
    public void postHandle(MaskMessage message) {
        System.out.println("hello方法的 [postHandle]--->执行!");
    }


    @MaskMethod(methodName = "fast",timing = TimeNode.PRE_HANDLE)
    public void fastPerHandle(MaskMessage message) {
        System.out.println("fast方法的 [fastPerHandle]--->执行!");
    }
}
