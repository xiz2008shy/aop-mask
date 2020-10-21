package com.tomqi.aop_mask.aspect;

import com.tomqi.aop_mask.Exception.MValidationException;
import com.tomqi.aop_mask.annotation.Masking;
import com.tomqi.aop_mask.container.MValidatorHandlerContainer;
import com.tomqi.aop_mask.container.MaskContainer;
import com.tomqi.aop_mask.mask_core.DataMask;
import com.tomqi.aop_mask.pojo.MValidatorResult;
import com.tomqi.aop_mask.pojo.MaskMessage;
import com.tomqi.aop_mask.utils.MaskContext;
import com.tomqi.aop_mask.validation.core.MValidatorHandler;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.tomqi.aop_mask.pojo.MaskMessage.creatMessage;

/**
 * @author TOMQI
 * @description 代理@Masking注解标注的方法
 * @see
 * @date 2020/9/18 11:42
 */
@Aspect
@Component
public class MaskingAop {

    @Autowired
    private MaskContainer maskingStrategies;

    @Autowired
    private MValidatorHandlerContainer validations;

    @Around("@annotation(com.tomqi.aop_mask.annotation.Masking)")
    public Object commonMaskData (ProceedingJoinPoint joinPoint) throws Throwable {
        MaskContext.setPoint(joinPoint);
        try {
            MaskMessage message = creatMessage(joinPoint);
            Masking masking = message.getMasking();
            // validation模块
            if(StringUtils.isNotBlank(masking.id())){
                MValidatorHandler handler = validations.getHandler(message.getMasking().id());
                MValidatorResult validResult = handler.valid(message.getMethodArgs(), null);
                if(!validResult.isPass()){
                    throw new MValidationException(validResult.getMessage(),joinPoint.getSignature().getDeclaringTypeName(),message.getMethodName());
                }
            }

            // mainProcess
            if (masking.onlyValid()) {
                return message.setJoinPoint(joinPoint).proceed();
            }else {
                String simpleClassName = message.getSimpleClassName();
                DataMask dataMask = maskingStrategies.getMask(simpleClassName);
                return dataMask.maskData(message);
            }
        } finally {
            MaskContext.remover();
        }
    }

}
