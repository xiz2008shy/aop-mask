package com.tomqi.aop_mask.annotation;

import java.lang.annotation.*;

/**
 * @author TOMQI
 * @Title: MTiming
 * @ProjectName: aop_mask
 * @Description :该注解作用与@MaskMethod中timing属性含义相同，但该注解仅适用于FastDataMaskTemplate，用于该类的便利性，
 * 日常使用中完全可以使用@MaskMethod进行代替。
 * @data 2020/10/1822:51
 **/


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MTiming {

    TimeNode value() default TimeNode.POST_HANDLE;
}
