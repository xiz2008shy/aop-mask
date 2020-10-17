package com.tomqi.aop_mask.Exception;

/**
 * @author TOMQI
 * @Title: NonMaskException
 * @ProjectName: aop_mask
 * @Description :效验异常
 * @data 2020/10/36:09
 **/
public class MValidationException extends RuntimeException {

    public MValidationException(String tips) {
        super(tips);
    }

}
