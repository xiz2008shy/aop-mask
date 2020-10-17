package com.tomqi.aop_mask.annotation;

import java.lang.annotation.*;

/**
 * @author TOMQI
 * @Title: MValid
 * @ProjectName: aop_mask
 * @Description : 用于开启mask中的效验
 * @data 2020/10/817:02
 **/

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MValid {
}
