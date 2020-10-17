package com.tomqi.aop_mask.config;

import com.tomqi.aop_mask.container.MValidatorHandlerContainer;
import com.tomqi.aop_mask.container.MaskContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author TOMQI
 * @description 托管MaskingContainer和MValidatorHandlerContainer
 * @date 2020/9/17 22:29
 */

@Configuration
public class MaskBeanConfig {

    @Bean(initMethod = "initContainer")
    public MaskContainer maskingStrategies(){
        return new MaskContainer();
    }

    @Bean
    public MValidatorHandlerContainer validations(){
        return new MValidatorHandlerContainer();
    }
}
