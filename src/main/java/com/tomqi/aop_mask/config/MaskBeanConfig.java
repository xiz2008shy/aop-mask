package com.tomqi.aop_mask.config;

import com.tomqi.aop_mask.config.condition.CustomerLogCondition;
import com.tomqi.aop_mask.container.MValidatorHandlerContainer;
import com.tomqi.aop_mask.container.MaskContainer;
import com.tomqi.aop_mask.log.executor.impl.AsyncLogExecutor;
import com.tomqi.aop_mask.log.executor.LogExecutor;
import com.tomqi.aop_mask.log.LogThreadFactory;
import com.tomqi.aop_mask.log.executor.impl.SyncLogExecutor;
import com.tomqi.aop_mask.utils.TimeUnitUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author TOMQI
 * @description 托管MaskingContainer和MValidatorHandlerContainer
 * @date 2020/9/17 22:29
 */

@Configuration
@EnableAspectJAutoProxy
@PropertySource(value = "classpath:AOP_MaskOption.properties")
public class MaskBeanConfig {

    @Autowired
    private Environment env;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean(initMethod = "initContainer")
    public MaskContainer maskingStrategies() {
        return new MaskContainer();
    }

    @Bean
    public MValidatorHandlerContainer validations() {
        return new MValidatorHandlerContainer();
    }

    @Bean
    @Conditional(CustomerLogCondition.class)
    public LogExecutor logExecutor() {

        boolean isAsync = env.getProperty("LogExecutor.isAsync", boolean.class ,false);
         if (isAsync) {
             int corePoolSize = env.getProperty("LogExecutor.corePoolSize", int.class, 1);

             int maximumPoolSize = env.getProperty("LogExecutor.maximumPoolSize", int.class, 1);

             long keepAliveTime = env.getProperty("LogExecutor.keepAliveTime", long.class, 60L);

             String unitStr = env.getProperty("LogExecutor.unit", String.class, "TimeUnit.MINUTES");
             TimeUnit timeUnit = TimeUnitUtils.getUnit(unitStr);

             int blockQueueLimit = env.getProperty("LogExecutor.blockQueueLimit", int.class, 100);

             return new AsyncLogExecutor(new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit,
                     new ArrayBlockingQueue<>(blockQueueLimit), new LogThreadFactory(),
                     new ThreadPoolExecutor.CallerRunsPolicy()));
         }
         return new SyncLogExecutor();
    }
}
