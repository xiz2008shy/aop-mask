package com.tomqi.aop_mask.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author TOMQI
 * @description 用於標註需要進行脫敏處理的方法,具体执行详见masking.impl包下对应的执行类
 * @date 2020/9/17 22:25
 */

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Masking {

    /**
     * 当@masking标注的方法是重载方法时，应尽可能为该方法起别名，同时对应的处理类中@MaskMethod的value属性需要与这里起的别名对应。
     * 如果重载方法有超过一个以上被标记时，必须至少对其中一个进行别名指定，否则无法定位到准确的处理方法
     * @see MaskMethod
     * @return
     */
    String alias() default "";

    Validator[] value() default {};

    String id() default "";

    /**
     * 仅进行效验处理，除此之外原方法执行，默认为false，表示默认情况会开启mask策略处理，如果只做效验，请设置true。
     * @return
     */
    boolean onlyValid () default false;

    LogMode logMode () default LogMode.OFF;
}
