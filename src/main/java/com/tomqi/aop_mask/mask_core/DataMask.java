package com.tomqi.aop_mask.mask_core;

import com.tomqi.aop_mask.pojo.MaskMessage;


/**
 * @author TOMQI
 * @description 类方法扩展接口，建议通过其实现类AbstractDefaultDataMask进行使用。
 * @date 2020/9/17 22:20
 */

public interface DataMask {

    /**
     * 用于数据处理的核心方法，直接由外部调用，有具体实现类实现
     * @param message
     * @return
     */
    Object maskData(MaskMessage message);
}
