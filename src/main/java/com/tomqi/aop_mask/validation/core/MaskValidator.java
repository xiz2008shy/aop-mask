package com.tomqi.aop_mask.validation.core;

import com.tomqi.aop_mask.pojo.MValidatorResult;
import com.tomqi.aop_mask.pojo.MethodArgs;

/**
 * @author TOMQI
 * @description 所有mask增强下效验器顶级接口
 * @date 2020/10/3 8:18
 */
public interface MaskValidator {

    /**
     * 具体的效验方法在这里重写，可以直接获取validator注解中所有参数。
     * @param params 标注方法的所有形参
     * @param result 用于效验器的结果传递
     * @return
     */
    MValidatorResult valid(MethodArgs params, MValidatorResult result);
}
