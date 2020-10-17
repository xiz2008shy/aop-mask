package com.tomqi.aop_mask.validation.core;

import com.tomqi.aop_mask.annotation.Validator;


/**
 * @author TOMQI
 * @description 具体的效验器实现从该类继承
 * @date 2020/10/9 0:51
 */
public abstract class AbstractMaskValidator implements MaskValidator {

    private int order = 0;

    private Validator annotation;

    public AbstractMaskValidator(Validator annotation) {
        this.annotation = annotation;
        this.order = annotation.order();
    }

    public Validator getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Validator annotation) {
        this.annotation = annotation;
    }

    public int getOrder(){
        return order;
    }
}
