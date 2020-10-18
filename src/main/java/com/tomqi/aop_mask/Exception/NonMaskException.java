package com.tomqi.aop_mask.Exception;

/**
 * @author TOMQI
 * @Title: NonMaskException
 * @ProjectName: aop_mask
 * @Description :TODO
 * @data 2020/10/1821:08
 **/
public class NonMaskException extends Exception {

    private static final String NON_MASK = "没有找到对应的mask策略,当前KEY为:";

    public NonMaskException(String message) {
        super(NON_MASK+message);
    }
}
