package com.tomqi.aop_mask.container;

import com.tomqi.aop_mask.validation.core.MValidatorHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TOMQI
 * @Title: MValidHandlerContainer
 * @ProjectName: aop_mask
 * @Description :效验器容器
 * @data 2020/10/817:05
 **/
public class MValidatorHandlerContainer {

    private Map<String, MValidatorHandler> map =  new ConcurrentHashMap(16);

    public MValidatorHandler getHandler(String key){
        return map.get(key);
    }

    public void putHandler(String key,MValidatorHandler validatorHandler){
        map.put(key,validatorHandler);
    }
}
