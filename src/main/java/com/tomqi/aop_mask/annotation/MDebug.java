package com.tomqi.aop_mask.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author TOMQI
 * @description 该注解用于配合AbstractDefaultDateMask的子类，value指定所修饰的原方法所在的类
 *
 * @date 2020/9/27 10:15
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Component
public @interface MDebug {

    String value();

}
