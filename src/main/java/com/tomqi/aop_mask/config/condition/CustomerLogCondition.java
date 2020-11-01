package com.tomqi.aop_mask.config.condition;

import com.tomqi.aop_mask.log.executor.LogExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;


/**
 * @author TOMQI
 * @Title: CustomerCondition
 * @ProjectName: aop-mask
 * @Description : 存在自定义LogExecutor时怎么不再加载默认配置的日志处理器
 * @data 2020/11/114:09
 **/
public class CustomerLogCondition implements Condition {

    private static final Logger log = LoggerFactory.getLogger(CustomerLogCondition.class);

    /**
     * 具体判断是否存在自定义的日志处理器的方式，是看ioc容器中是否存在注册的LogExecutor类型的bean
     * 即自定义日志处理器只需要实现LogExecutor接口并注册到spring中即可
     * @param context
     * @param metadata
     * @return
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        LogExecutor logExecutor = null;
        try {
            logExecutor = beanFactory.getBean(LogExecutor.class);
        } catch (BeansException e) {
            log.info("用户未自定义LogExecutor，将启用MLog的异步/同步日志处理器...");
        }
        if (logExecutor != null ) {
            return false;
        }
        return true;
    }
}
