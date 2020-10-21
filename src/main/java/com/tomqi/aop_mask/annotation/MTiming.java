package com.tomqi.aop_mask.annotation;

import java.lang.annotation.*;

/**
 * @author TOMQI
 * @Title: MTiming
 * @ProjectName: aop_mask
 * @Description :该注解作用与@MaskMethod中timing属性含义相同，但该注解拥有更高的优先权，该注解与@MaskMethod同时存在时，采用该注解的属性值。
 * 另外该注解暂不适用于AbstractDefaultDataMask的子类
 * @data 2020/10/1822:51
 **/


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MTiming {

    TimeNode value() default TimeNode.POST_HANDLE;

    /**
     * 用于指定修饰同一方法，同一节点的先后执行顺序，数字小的优先执行，相同大小不能保证执行顺序。
     * @return
     */
    int order () default 0;
}
