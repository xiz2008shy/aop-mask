package com.tomqi.aop_mask.annotation;

import com.tomqi.aop_mask.validation.core.AbstractMaskValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author TOMQI
 * @Title: Validator
 * @ProjectName: aop_mask
 * @Description :用于声明效验器的指定
 * @data 2020/10/90:35
 **/

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validator {

    Class<? extends AbstractMaskValidator> validBy();

    int order() default 1;

    String[] keyWord() default {};

    int[] validParamIndex() default {};

    int upperLimit() default 0;

    int lowerLimit() default 0;

}
