package com.tomqi.aop_mask.mask_core;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author TOMQI
 * @Title: FastMaskFactory
 * @ProjectName: aop_mask
 * @Description :TODO
 * @data 2020/10/2722:29
 **/
public class FastMaskFactory implements FactoryBean {

    private Class<?> accClazz;

    public FastMaskFactory(Class<?> accClazz) {
        this.accClazz = accClazz;
    }

    @Override
    public Object getObject() throws Exception {
        return accClazz.getConstructor().newInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

}
